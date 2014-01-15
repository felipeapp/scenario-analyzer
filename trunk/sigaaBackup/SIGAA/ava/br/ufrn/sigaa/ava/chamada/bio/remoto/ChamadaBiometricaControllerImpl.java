/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 27/08/2009
 *
 */	
package br.ufrn.sigaa.ava.chamada.bio.remoto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.jws.WebService;

import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.integracao.dto.DiscenteDTO;
import br.ufrn.integracao.dto.FrequenciaEletronicaDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.interfaces.ChamadaBiometricaController;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.EstacaoChamadaBiometrica;
import br.ufrn.sigaa.ava.dominio.EstacaoChamadaBiometricaTurma;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;
import br.ufrn.sigaa.ensino.dominio.FrequenciaEletronica;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.TipoCaptcaoFrequencia;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Implementa os métodos da interface utilizada pelo Spring Remote pela comunicação remota 
 * entre o aplicativo desktop de Chamada Biométrica e o SIGAA.   
 * 
 * Esse aplicativo é utilizado nas salas de aula para informatizar a "chamada" feita em papel. 
 * 
 * @author agostinho campos
 * 
 */
@WebService
@Component("chamadaBiometricaController")
public class ChamadaBiometricaControllerImpl extends AbstractController implements ChamadaBiometricaController {

	/**
	 * Valor da falta quando o discente registra presença no 2 horário de aula. 
	 */
	private static final int PRESENCA_APENAS_SEGUNDO_HORARIO = 1;
	
	/**
	 * Lista de feriados da turma. 
	 */
	private List<Date> feriadosTurma;
	
	/**
	 * Método invocado pelo aplicativo desktop para autenticar a senha inserida para determinada turma.
	 * 
	 */
	public FrequenciaEletronicaDTO logarSistemaChamadaBiometria(String codigoTurma, String codigoComponente, String senha, String hostName, String hostAddress, boolean turmaferias) {
		
		
		TurmaVirtualDao turmaVirtualDao = DAOFactory.getInstance().getDAO(TurmaVirtualDao.class);
		TurmaDao turmaDao = DAOFactory.getInstance().getDAO(TurmaDao.class);
		try {
			
			List<Turma> subTurmas = new ArrayList<Turma>();
			Turma turmaLocalizada = new Turma();
		
			// se turma é de férias usar calendário de férias vigente.
			if(turmaferias){
				turmaLocalizada = turmaVirtualDao.autenticaSenhaChamadaBiometrica(codigoTurma.toUpperCase(), codigoComponente.toUpperCase(), 
						CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getAnoFeriasVigente(), CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getPeriodoFeriasVigente(), senha.toLowerCase());
			}else{
				turmaLocalizada = turmaVirtualDao.autenticaSenhaChamadaBiometrica(codigoTurma.toUpperCase(), codigoComponente.toUpperCase(), 
					CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getAno(), CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getPeriodo(), senha.toLowerCase());
			}
			
			FrequenciaEletronicaDTO frequenciaEletronicaDTO = new FrequenciaEletronicaDTO();
				frequenciaEletronicaDTO.setCodigoComponente(turmaLocalizada.getDisciplina().getCodigo());
				frequenciaEletronicaDTO.setCodigoTurma(turmaLocalizada.getCodigo());
				frequenciaEletronicaDTO.setLoginAutorizado(true);
				frequenciaEletronicaDTO.setIdTurma(turmaLocalizada.getId());
				
				// localiza os horários dessa turma
				turmaLocalizada.setHorarios(turmaDao.findHorariosByTurma(turmaLocalizada)); 
				
				// de acordo com os horários, configura as possíveis faltas que o aluno pode ter
				short horariosDia = getPresencasDia(turmaLocalizada, getDiaSemana(new Date()));
				
				frequenciaEletronicaDTO.setHorarios(horariosDia);
				
				// verifica se a turma possui subturmas, caso possua seta a lista de subturmas da Turma no DTO  
				if (turmaLocalizada.getId() != 0)
					subTurmas = turmaDao.findSubturmasByTurmaFetchDocentes(turmaLocalizada);
					
				for (Turma subTurma : subTurmas) {
					
					FrequenciaEletronicaDTO subTurmaDTO = new FrequenciaEletronicaDTO();
					subTurmaDTO.setCodigoComponente(subTurma.getDisciplina().getCodigo());
					subTurmaDTO.setCodigoTurma(subTurma.getCodigo());
					
					// localiza os horários dessa turma
					subTurma.setHorarios(turmaDao.findHorariosByTurma(subTurma)); 
					
					// de acordo com os horários, configura as possíveis faltas que o aluno pode ter
					short horDia = getPresencasDia(subTurma, getDiaSemana(new Date()));
					subTurmaDTO.setHorarios(horDia);
					
					frequenciaEletronicaDTO.getSubTurmas().add(subTurmaDTO);
				}
			
			return frequenciaEletronicaDTO;
			
		} catch (Exception e) {
			notifyError(e);
		} finally {
			turmaVirtualDao.close();
			turmaDao.close();
		}
		return new FrequenciaEletronicaDTO();
	}
	
	/**
	 * Autentica se a senha da estação biométrica é válida, autorizando ou não o usuário do app desktop cadastrar uma nova turma.
	 */
	public boolean verificarSenhaEstacaoBiometrica(String codigoTurma, String codigoComponente, String senha) {
		
		TurmaVirtualDao turmaVirtualDao = DAOFactory.getInstance().getDAO(TurmaVirtualDao.class);
		try {

			Turma turma = turmaVirtualDao.findTurmaEstacaoBiometrica(codigoTurma.toUpperCase(), codigoComponente.toUpperCase(), 
					CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getAno(), CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getPeriodo());
			if ( turmaVirtualDao.verificarSenhaEstacaoBiometrica(turma.getId(), senha.toLowerCase()) )
				return true;
			else
				return false;
			
		} catch (Exception e) {
			notifyError(e);
		} finally {
			turmaVirtualDao.close();
		}
		return false;
	}
	
	/**
	 * Exibe a última chamada realizada para determinada turma exibindo os discentes que faltaram, que
	 * tem presença completa e parcial.
	 */
	public List<DiscenteDTO> ultimasChamadasRealizadas(String codigoTurma, String codigoComponente, boolean temSubturma, String codigoSubturma) {
		
		TurmaVirtualDao dao = new TurmaVirtualDao();
		try {
			
			Turma turma = dao.findTurmaEstacaoBiometrica(codigoTurma.toUpperCase(), codigoComponente.toUpperCase(), 
					CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getAno(), CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getPeriodo());
			
			int idSubTurma = 0;
			if (temSubturma) {
				for (Turma subTurma : turma.getSubturmas()) {
					if (subTurma.getCodigo().equals(codigoSubturma.toUpperCase()))
						idSubTurma = subTurma.getId();
				}
				
				return dao.findUltimasChamadas(idSubTurma);
			}
			
			return dao.findUltimasChamadas(turma.getId());
			
		} catch (Exception e) {
			notifyError(e);
		} finally {
			dao.close();
		}
		return new ArrayList<DiscenteDTO>();
	}
	
	/**
	 * Verifica se o início da aula informado pelo docente ao iniciar o 
	 * sistema é válido de acordo com o horário do servidor.
	 *
	 * Para iniciar uma chamada o horário informado deve ser igual ou superior
	 * ao horário atual do servidor.  
	 */
	public boolean verificarValidadeInicioAula(Date horarioInicioAula) {
		
		// extrai a hora-minuto informada pelo desktop, pois a data (dia, mês, ano) do desktop pode estar errada
		Calendar horaMinutoDesktop = Calendar.getInstance();
		horaMinutoDesktop.setTime(horarioInicioAula);
		
		int hora = horaMinutoDesktop.get(Calendar.HOUR_OF_DAY);
		int minuto = horaMinutoDesktop.get(Calendar.MINUTE);
		
		// usa a hora informada pelo desktop, porém compara-se com o dia atual do servidor
		Calendar horaInicioAulaDesktop = Calendar.getInstance();
		horaInicioAulaDesktop.set(Calendar.HOUR_OF_DAY, hora);
		horaInicioAulaDesktop.set(Calendar.MINUTE, minuto);
		horaInicioAulaDesktop.set(Calendar.SECOND, 0);
		horaInicioAulaDesktop.set(Calendar.MILLISECOND, 0);
		
		Calendar horarioAtualServidor = Calendar.getInstance();
		horarioAtualServidor.set(Calendar.SECOND, 0);
		horarioAtualServidor.set(Calendar.MILLISECOND, 0);
		
		if ( horaInicioAulaDesktop.getTime().getTime() >= horarioAtualServidor.getTime().getTime() )
			return true;
		else
			return false;
	}
	
	/**
	 * Inicia uma nova chamada remota. É responsável por abrir a turma 
	 * e registrar no sistema que o usuário iniciou uma nova chamada.
	 * @throws NegocioRemotoException 
	 * @throws Exception 
	 */
	public FrequenciaEletronicaDTO iniciarNovaChamada(String codigo, String idTurma) throws NegocioRemotoException {

		// localiza/abre a turma para começar a receber presenças dos alunos e retorna um DTO para enviar o desktop
		FrequenciaEletronicaDTO frequenciaDTO = gerarFrequenciaEletronicaDTO(codigo, idTurma);
		
		// registra no sistema que o usuário iniciou a abertura de uma nova chamada para uma turma
		FrequenciaEletronica freque = registrarAberturaChamada(frequenciaDTO.getIdTurma());
		frequenciaDTO.setIdFrequenciaEletronica(freque.getId());
		
		return frequenciaDTO; 
	}

	/**
	 * Cria processador e registra no banco que usuário iniciou uma nova chamada. 
	 *  
	 * @param idUsuarioLogado
	 * @param frequenciaDTO
	 * @return 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	private FrequenciaEletronica registrarAberturaChamada(int idTurma) {
		
		FrequenciaEletronica frequenciaEletronica = new FrequenciaEletronica();
		GenericDAO dao = DAOFactory.getGeneric(Sistema.SIGAA);
		try {
			
			frequenciaEletronica.setDataHoraAbertura(new Date());
			frequenciaEletronica.setTurma(new Turma(idTurma));
			
			dao.create(frequenciaEletronica);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return frequenciaEletronica;
	}

	/**
	 * Retorna o número total de horários de um determinado dia de aula de uma turma.
	 * @param turma
	 * @param dia
	 * @return
	 */
	private short getPresencasDia(Turma turma, int dia) {
		short presencas = 0;
		for( HorarioTurma ht: turma.getHorarios()){
			if(dia == Character.getNumericValue(ht.getDia()) && CalendarUtils.isDentroPeriodo(ht.getDataInicio(), ht.getDataFim(), new Date()) ){
				presencas++;
			}
		}
		return presencas;
	}
	
	/**
	 * Retorna o dia da semana de acordo com uma data.
	 * @param data
	 * @return
	 */
	private int getDiaSemana(Date data){
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	
	/**
	 * Gera um DTO de acordo com a turma localizada. Esse DTO inclui a turma e os discentes matriculados na mesma.
	 * @param codigo
	 * @param idTurma
	 * @return
	 * @throws NegocioRemotoException 
	 * @throws Exception 
	 */
	private FrequenciaEletronicaDTO gerarFrequenciaEletronicaDTO(String codigo, String codigoTurma) throws NegocioRemotoException {
		
		feriadosTurma = null;
		TurmaDao tDao = new TurmaDao();
		DiscenteDao discenteDao = new DiscenteDao();
		FrequenciaEletronicaDTO frequenciaEletronicaDTO = new FrequenciaEletronicaDTO();
		PessoaDao pessoaDao = new PessoaDao();
		TurmaVirtualDao turmaVirtualDao = new TurmaVirtualDao();
		try {
			
			Turma turmaAbertaParaChamada = abrirTurma(codigo.toUpperCase(), codigoTurma.toUpperCase(), tDao);
			
			if (turmaAbertaParaChamada.getId() == 0) 
				throw new NegocioRemotoException("Não foi localizada nenhuma aula dessa turma para hoje.");
			
			frequenciaEletronicaDTO.setIdTurma(turmaAbertaParaChamada.getId());
			frequenciaEletronicaDTO.setNomeTurma(turmaAbertaParaChamada.getDescricaoDisciplina());
			frequenciaEletronicaDTO.setCodigoComponente(turmaAbertaParaChamada.getDisciplina().getCodigo());
			frequenciaEletronicaDTO.setCodigoTurma(codigoTurma);
			frequenciaEletronicaDTO.setHoraServidor(new Date());
			
			// localiza os horários dessa turma
			turmaAbertaParaChamada.setHorarios(tDao.findHorariosByTurma(turmaAbertaParaChamada)); 
			
			// de acordo com os horários, configura as possíveis faltas que o aluno pode ter
			short horariosDia = getPresencasDia(turmaAbertaParaChamada, getDiaSemana(new Date()));
			
			if ( verificarAulaValida(horariosDia, new Date(), turmaAbertaParaChamada) ) {
				
				frequenciaEletronicaDTO.setHorarios(horariosDia);
			
					Collection<Discente> listaDiscentesMatriculados = discenteDao.findByTurma(turmaAbertaParaChamada.getId(), true);
					
					for (Discente discente : listaDiscentesMatriculados) {
						
						DiscenteDTO discenteDTO = new DiscenteDTO();
						discenteDTO.setCpf_cnpj( discente.getPessoa().getCpf_cnpj() );
						discenteDTO.setMatricula( discente.getMatricula() );
						discenteDTO.setNome( discente.getNome() );
						discenteDTO.setIdDiscente( discente.getId() );
						
						discenteDTO.setDigitais( pessoaDao.findDigitalDiscenteByCPF( discente.getPessoa().getCpf_cnpj() ) );
						
						frequenciaEletronicaDTO.getDiscentesTurma().add(discenteDTO);
					}
			}
			else {
				throw new NegocioRemotoException("Não foi localizada nenhuma aula dessa turma para hoje.");
			}			
		} catch (DAOException e) {
			throw new RemoteAccessException(e.getMessage());
		}finally {
			tDao.close();
			discenteDao.close();
			pessoaDao.close();
			turmaVirtualDao.close();
		}
		return frequenciaEletronicaDTO;
	}

	/**
	 * Verifica se foi encontrado horário/aula para abrir a tela de chamada.
	 * Verifica se existe aula para o dia atual e se o dia não é um feriado.
	 * 
	 * @param horariosDia
	 * @param hoje
	 * @return
	 * @throws DAOException 
	 */
	private boolean verificarAulaValida(short horariosDia, Date hoje, Turma turma) throws DAOException {
		
		if( feriadosTurma == null )
			feriadosTurma = TurmaUtil.getFeriados(turma);
		
		boolean diaValido = true;
		for (Date f : feriadosTurma) {
			int valor = CalendarUtils.compareTo(f, hoje);
				if ( valor == 0) // hoje é feriado
					diaValido = false;
		}
		
		if (horariosDia == 0) // verifica se existe aula para hoje
			diaValido = false;
		
		return diaValido;
	}

	/**
	 * Localiza a turma de acordo com os dados informados pelo usuário.
	 * @param codigo
	 * @param idTurma
	 * @param tDao
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	private Turma abrirTurma(String codigo, String codigoTurma, TurmaDao tDao) throws DAOException, LimiteResultadosException {
		List<Turma> turmaAbertaParaChamada = (List<Turma>) tDao.findGeral(null, null, codigo, codigoTurma, null, null, null, 
				CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getAno(), CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getPeriodo(), null, null,  new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL), null, null, null, null, null,null,null);
		if (!turmaAbertaParaChamada.isEmpty())
			return turmaAbertaParaChamada.get(0);
		else
			return new Turma();
	}

	 /**
	 * Antes de fechar o software, registra falta para todos os alunos que não registram sua presença no sistema. 
	 */
	public void  registrarFaltaParaAlunos(int idTurmaOuSubturma, int horarios, String codigoTurma, String codigoComponente) {
		
		TurmaDao turmaDao = DAOFactory.getInstance().getDAO(TurmaDao.class);
		FrequenciaAlunoDao dao = DAOFactory.getInstance().getDAO(FrequenciaAlunoDao.class);
		
		try {
			EstacaoChamadaBiometrica estacao = dao.findEstacaoBiometricaByTurma(codigoTurma, codigoComponente, CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getAno(), CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getPeriodo());
			
			Collection<MatriculaComponente> discentesTurma = turmaDao.findParticipantesTurma(idTurmaOuSubturma);
			List<Discente> list = dao.findDiscentesComFrequenciaByTurmaDia(idTurmaOuSubturma, new Date());
			
				for (MatriculaComponente matriculaComponente : discentesTurma) {	
					
					boolean discenteSemPresenca = true;
					
					for (Discente discente : list) { // verifica se o aluno já teve presença registrada no dia, pra evitar duplicação
						if (  matriculaComponente.getDiscente().getId() == discente.getId() ) {
							discenteSemPresenca = false;
						}
					}
					
					if (discenteSemPresenca) {
						FrequenciaAluno frequenciaAluno = new FrequenciaAluno();
						frequenciaAluno.setData(new Date());
							
						Discente d = new Discente(matriculaComponente.getDiscente().getId());
						frequenciaAluno.setDiscente(d);
						frequenciaAluno.setEstacaoChamadaBiometricaTurma(estacao);
						
						Turma turma = new Turma();
						turma.setId(idTurmaOuSubturma);
						frequenciaAluno.setTurma(turma);
						
						// TODO: problema relacionado a entidade FrequenciaAluno, vai ter que se modificada, pois
						// TODO: atualmente não contempla registrar aula pela manhã e pela tarde no mesmo dia para uma Turma.
//						if (horarios == 4) {
//							frequenciaAluno.setFaltas((short)(horarios-2));
//							frequenciaAluno.setHorarios((short)horarios);
//						}
//						else {
							frequenciaAluno.setFaltas((short)(horarios));
							frequenciaAluno.setHorarios((short)horarios);
//						}
//						
						frequenciaAluno.setHoraPresencaDigital(new
								Date());
						
						frequenciaAluno.setTipoCaptcaoFrequencia(TipoCaptcaoFrequencia.DIGITAL);
						
						ArrayList<FrequenciaAluno> listaFreq = new ArrayList<FrequenciaAluno>();
						listaFreq.add(frequenciaAluno);
						
						dao.create(frequenciaAluno);
					}
			}
		} catch(DAOException e) {
			throw new RemoteAccessException(e.getMessage());
		} finally {
			dao.close();
			turmaDao.close();
		}
	}
	
	/**
	 * Registra no SIGAA as presenças dos alunos de acordo com a Turma que está aberta e para o aluno que enviou sua digital.
	 * @throws DAOException 
	 * @throws FrequenciaDuplicadaDiaException 
	 */
	public boolean registrarFrequenciaDiscente(int idDiscente, int codigoTurmaOuSubturma, String codigoTurma, String codigoComponente, int horarios, Date horaInicialAula, 
			int duracaoAula, int minTolerAula, boolean iniciouSegundaAula) {
		
		UsuarioGeral usuarioLogadoDesktop = new UsuarioGeral();
		FrequenciaAlunoDao dao = DAOFactory.getInstance().getDAO(FrequenciaAlunoDao.class, usuarioLogadoDesktop);
		
		try {
			boolean frequenciaExsitente = dao.findFrequenciasByDiscenteDataTurma(new Discente(idDiscente), new Turma(codigoTurmaOuSubturma), new Date());
		
			if (!frequenciaExsitente) {
		
					EstacaoChamadaBiometrica estacao = dao.findEstacaoBiometricaByTurma(codigoTurma, codigoComponente, CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getAno(), CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getPeriodo());
					
					FrequenciaAluno frequenciaAluno = new FrequenciaAluno();
					frequenciaAluno.setData(new Date());
		
					Discente d = new Discente(idDiscente);
					frequenciaAluno.setDiscente(d);
					
					frequenciaAluno.setEstacaoChamadaBiometricaTurma(estacao);
					
					Turma turma = new Turma();
					turma.setId(codigoTurmaOuSubturma);
					frequenciaAluno.setTurma(turma);
					
					int faltas = verificarQuantidadeFaltas(horaInicialAula, duracaoAula, minTolerAula);
					
					// TODO: problema relacionado como foi modelada a entidade FrequenciaAluno atualmente ela
					// TODO: não contempla registrar aula pela manhã e pela tarde no mesmo dia para uma Turma.
					if (iniciouSegundaAula)
						frequenciaAluno.setFaltas((short)PRESENCA_APENAS_SEGUNDO_HORARIO);
					else
						frequenciaAluno.setFaltas((short)faltas);
						
					frequenciaAluno.setHorarios((short)horarios);
					frequenciaAluno.setHoraPresencaDigital(new Date());
					frequenciaAluno.setTipoCaptcaoFrequencia(TipoCaptcaoFrequencia.DIGITAL);
					
					ArrayList<FrequenciaAluno> listaFreq = new ArrayList<FrequenciaAluno>();
					listaFreq.add(frequenciaAluno);
					
					dao.create(frequenciaAluno);
					
				
				return true;
			}
			
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;			
		} finally {
			dao.close();
		}
	}

    /**
     * Calcula a quantidade de faltas que o aluno poderá ter ou não.
     * O cálculo também leva em consideração o tempo de tolerância que o professor dá
     * ao iniciar a aula.
     *
     * Por exemplo: uma aula de 50 min, com tempo de tolerância de 20 min
     * e a aula começando as 11:00 horas da manhã.
     *
     * As presenças registradas até as 11:20 minutos serão consideradas sem
     * nenhuma penalidade para o discente, ou seja, nenhuma falta será registrada
     * para o mesmo.
     *
     * A partir das 11:21 minutos o discente terá registrada apenas uma presença
     * no sistema.
     * 
     * @return
     */
    private int verificarQuantidadeFaltas(Date horaInicialAula, int duracaoAula, int minTolerAula) {
    	Calendar calendarDesktop = Calendar.getInstance();
    	// seta data/hora que veio do desktop, porém só importa o horário
    	calendarDesktop.setTime(horaInicialAula);
    	
    	// extrai a hora-minuto informada pelo desktop, pois a data (dia, mês, ano) do desktop pode estar errada
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(new Date());
    	calendar.set(Calendar.HOUR_OF_DAY, calendarDesktop.get(Calendar.HOUR_OF_DAY));
    	calendar.set(Calendar.MINUTE, calendarDesktop.get(Calendar.MINUTE));
    	calendar.set(Calendar.MILLISECOND, 0);
    	
        Date diferenca = subtrairHora(new Date(), calendar.getTime());
        int totalFaltas = ( (duracaoAula-minTolerAula) + converterToMinutos(diferenca) )/duracaoAula;
        return totalFaltas;
    }
    
	/**
	 * Subtrai os minutos iniciais da aula pelos minutos atuais do sistema.
	 * @return
	 */
    private static Date subtrairHora(Date horaAtualSistema, Date horaInicialAula) {
    	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        long minutoHoraAtualSistema = getMinutos(horaAtualSistema);
        long minutoHoraInicialAula = getMinutos(horaInicialAula);

        long result = 0;
        if (minutoHoraAtualSistema < minutoHoraInicialAula)
           result = (minutoHoraInicialAula - minutoHoraAtualSistema) * 60 * 1000;
        else
            result = (minutoHoraAtualSistema - minutoHoraInicialAula) * 60 * 1000;

        Date data = new Date(result);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal.getTime();
   }

	/**
	 * Retorna os minutos
	 * @return
	 */
    private static long getMinutos(Date data) {
        long minutos = data.getTime() / 1000 / 60;
        return minutos;
   }
    
	/**
	 * Converte a hora para minutos
	 * @return
	 */
    private static int converterToMinutos(Date hora) {

        long minutosMili = hora.getTime() / 1000 / 60;
        long minutos = (minutosMili * 60 * 1000);
        Date date = new Date(minutos);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal.get(Calendar.MINUTE);
    }
    
	/**
	 * Cadastra uma nova turma no sistema através do app desktop
	 * @throws NegocioRemotoException 
	 * 
	 * @throws Exception 
	 */
	public boolean cadastrarTurma(String codigoTurma, String codigoComponente, String hostMac, String hostAddress) throws NegocioRemotoException {
		
		TurmaVirtualDao dao = new TurmaVirtualDao();
		
		try {

			int ano = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getAno();
			int periodo = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getPeriodo();
			
			// a turma que está sendo cadastrada ainda não existe
			Turma turmaExistente = dao.findTurmaEstacaoBiometrica(codigoTurma.toUpperCase(), codigoComponente.toUpperCase(), ano, periodo);
			
			if ( turmaExistente.getId() == 0) {
				Turma turmaLocalizada = dao.findTurmaAnoPeriodoAtual(codigoTurma.toUpperCase(), codigoComponente.toUpperCase(), ano, periodo);
					if (turmaLocalizada.getId() != 0) {
						
						EstacaoChamadaBiometrica estacao = new EstacaoChamadaBiometrica();
						
						EstacaoChamadaBiometricaTurma estacaoTurma = new EstacaoChamadaBiometricaTurma();
						estacaoTurma.setTurma(turmaLocalizada);
						estacaoTurma.setEstacaoChamadaBiometrica(estacao);
						
						estacao.setIp(hostAddress);
						estacao.setMac(hostMac);
						estacao.getEstacaoChamadaBiometricaTurma().add(estacaoTurma);
						
						dao.create(estacao);
						return true;
					}
					else
						throw new NegocioRemotoException("Turma não localizada para dos dados informados.");
			}
			else
				return false;
				
		} catch (DAOException e) {
			notifyError( e );
		} finally {
 			dao.close();
		}
		
		return false;
	}
	
	/**
	 * Registra que a chamada foi finalizada pelo usuário no app Desktop.
	 */
	public boolean fecharChamada(int idFrequencia, String codigoTurma, String codigoComponente, String senha, boolean turmaFerias) {
		
		UsuarioDao dao = DAOFactory.getInstance().getDAO(UsuarioDao.class);
		try {	
				
				// 	verifica se usuário informou a senha correta para a turma que está sendo fechada
				if ( logarSistemaChamadaBiometria(codigoTurma.toUpperCase(), codigoComponente.toUpperCase(), senha, null, null, turmaFerias ).getIdTurma() != 0 ) { 
					FrequenciaEletronica frequenciaFechamento = dao.findByPrimaryKey(idFrequencia, FrequenciaEletronica.class);
					
					frequenciaFechamento.setDataHoraFechamento(new Date()); // define data-hora de fechamento da chamada dessa Turma 
					dao.create(frequenciaFechamento);
					return true;
				}
				else 
					return false;				
				
		} catch (DAOException e) {
			e.printStackTrace();
			return false;
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Envia para o desktop a hora atual do servidor para que o dekstop possa usá-la como ponto de partida para seu próprio incremento de tempo local.
	 */
	@Override
	public Date sincronizarDataHoraServidor() {
		return new Date();
	}
}