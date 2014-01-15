/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 19/05/2010
 */
package br.ufrn.sigaa.assistencia.remoto;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.hibernate.HibernateException;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.DiasAlimentacaoDTO;
import br.ufrn.integracao.dto.ParametroRetornoLogarAcessoRU;
import br.ufrn.integracao.dto.PessoaDto;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.exceptions.PessoaDigitalExistenteSistemaExcpetion;
import br.ufrn.integracao.interfaces.AcessoRURemoteService;
import br.ufrn.integracao.interfaces.IdentificacaoBiometricaRemoteService;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.sae.BolsaAuxilioDao;
import br.ufrn.sigaa.arq.dao.sae.ConfiguracoesPeriodoResultadoDAO;
import br.ufrn.sigaa.arq.dao.sae.DiasAlimentacaoDao;
import br.ufrn.sigaa.arq.dao.sae.RegistroAcessoRUDao;
import br.ufrn.sigaa.arq.dominio.UsuarioMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoRefeicaoRU;
import br.ufrn.sigaa.assistencia.restaurante.dominio.DiasAlimentacao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Implementa os métodos da interface utilizada pelo Spring Remote para comunicação
 * remota com o cliente desktop de Controle de Acesso ao RU (aplicativo que gerencia 
 * o acesso de pessoas ao restaurante universitário). 
 * 
 * @author agostinho campos
 * 
 */
@WebService
@Component("acessoRURemoteService")
public class AcessoRURemoteServiceImpl extends AbstractController implements AcessoRURemoteService {

	@Resource(name = "identificacaoBiometricaInvoker")
	private IdentificacaoBiometricaRemoteService identificador;
	
	
	/**
	 * Recebe um fluxo de bytes que representa a digital de uma pessoa. Nesse caso, são apenas discentes
	 * que enviam suas digitais, assim o sistema verifica se eles tem ou não direito a refeição.
	 * @throws NegocioRemotoException 
	 *  
	 */
	public DiasAlimentacaoDTO autorizarRefeicao(byte[] digital) throws NegocioRemotoException {

		List<BolsaAuxilio> bolsaDiasAlimentacao = null;
		try {

			// Identifica pessoa
			long cpf = identificador.identificar(digital);
			
			if (cpf == 0) {
				throw new NegocioRemotoException("PESSOA NÃO IDENTIFICADA OU DIGITAL NÃO CADASTRADA!");
			} else {

                ConfiguracoesPeriodoResultadoDAO confPerioResultDAO = new ConfiguracoesPeriodoResultadoDAO();
                AnoPeriodoReferenciaSAE anoPeriodoReferenciaSAE = confPerioResultDAO.findAnoPeriodoResultadoSAE();
                confPerioResultDAO.close();
                
                long tipoBolsa = tipoBolsaDiscente(cpf);
                
                DiasAlimentacaoDao dao = new DiasAlimentacaoDao();
                // traz a bolsa com seus respectivos dias de alimentação
                bolsaDiasAlimentacao = dao.findDiscentesDiasAlimentacaoMapaRU(cpf, anoPeriodoReferenciaSAE, tipoBolsa);
                dao.close();
                
                if (bolsaDiasAlimentacao != null) {
	                // só existe uma bolsa por cpf para cada Ano/Período
                	// popula o DTO de acordo com a bolsa passada
					DiasAlimentacaoDTO diasAlimentacaoDTO = popularDTO(bolsaDiasAlimentacao.get(0)); 
					
					// verifica se o discente já teve acesso a refeição daquele horário/dia
					if ( verificarDuplicacaoAcessoByMatricula(bolsaDiasAlimentacao.get(0).getDiscente().getMatricula()) ) {
						diasAlimentacaoDTO.setAcessouRURefeicaoAtual(true); //seta que o acesso é inválido para aquela refeição
						return diasAlimentacaoDTO;
					}
					else
						return diasAlimentacaoDTO;
                }
                else
                	return new DiasAlimentacaoDTO();
			}

		} catch (HibernateException e) {
			e.printStackTrace();
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Verifica a última solicitação de bolsa do discente.
	 */
	private long tipoBolsaDiscente(long cpf) throws DAOException {
		long tipoBolsa = 0;
		BolsaAuxilioDao bolsaAuxilioDao = new BolsaAuxilioDao();
		try {
			tipoBolsa = bolsaAuxilioDao.findTipoBolsaDiscente(cpf);
		} finally {
			bolsaAuxilioDao.close();
		}
		return tipoBolsa;
	}

	/**
	 * Verifica se o discente já passou pela catraca para a mesma refeição no mesmo dia.
	 * Caso o discente já tenha acessado, irá exibir mensagem na aplicação desktop.
	 * 
	 * OBS: o discente com bolsa só pode acessar apenas uma vez o restaurante para cada refeição. 
	 * 
	 * @throws Exception
	 */
	private boolean verificarDuplicacaoAcessoByMatricula(Long mat) {
		
		RegistroAcessoRUDao registroAcessoRUDao = new RegistroAcessoRUDao();
		List<Object> acessoLocalizado = registroAcessoRUDao.verificarPermissaoAcessoDiarioRU(mat, verificarTurnoAtual());
		registroAcessoRUDao.close();
		
		if (acessoLocalizado != null && acessoLocalizado.size() > 0)
			return true;
		else
			return false;
	}

	/**
	 * Criar e popula objeto DTO com os dias que o Discente pode se alimentar, matricula, o tipo da refeição, 
	 * o vínculo, qual tipo de refeição vai estar liberada, etc.
	 * 
	 * @param bolsaDiasAlimentacao
	 * @return
	 * @throws DAOException 
	 */
	private DiasAlimentacaoDTO popularDTO(BolsaAuxilio bolsaDiasAlimentacao) throws DAOException {

		if (bolsaDiasAlimentacao != null) {

			TipoRefeicaoRU tipoRefeicaoRUAutorizada = verificarPermissaoAlimentacao(bolsaDiasAlimentacao);

			DiasAlimentacaoDTO diasAlimentacaoDTO = new DiasAlimentacaoDTO();

			if (bolsaDiasAlimentacao.getDiscente().getIdFoto() != null)
				diasAlimentacaoDTO.setIdFoto(bolsaDiasAlimentacao.getDiscente().getIdFoto());
			else
				diasAlimentacaoDTO.setIdFoto(0);

			diasAlimentacaoDTO.setNome(bolsaDiasAlimentacao.getDiscente().getPessoa().getNome());
			diasAlimentacaoDTO.setMatricula(bolsaDiasAlimentacao.getDiscente().getMatricula());
			
			diasAlimentacaoDTO.setCpf(bolsaDiasAlimentacao.getDiscente().getPessoa().getCpf_cnpj());
			
			diasAlimentacaoDTO.setCurso(bolsaDiasAlimentacao.getDiscente().getCurso().getDescricao());
			
			diasAlimentacaoDTO.setTipoBolsa(bolsaDiasAlimentacao.getTipoBolsaAuxilio().getId());
			
			diasAlimentacaoDTO.setCafeAutorizado( tipoRefeicaoRUAutorizada.isCafe() );
			diasAlimentacaoDTO.setAlmocoAutorizado( tipoRefeicaoRUAutorizada.isAlmoco() );
			diasAlimentacaoDTO.setJantaAutorizado( tipoRefeicaoRUAutorizada.isJanta() );

			diasAlimentacaoDTO.setHorarioValidoCafe( tipoRefeicaoRUAutorizada.isHorarioCafeValido() );
			diasAlimentacaoDTO.setHorarioValidoAlmoco( tipoRefeicaoRUAutorizada.isHorarioAlmocoValido() );
			diasAlimentacaoDTO.setHorarioValidoJanta( tipoRefeicaoRUAutorizada.isHorarioJantaValido() );
			
			diasAlimentacaoDTO.setSituacaoBolsa( bolsaDiasAlimentacao.getSituacaoBolsa().getId() );
			
			for (DiasAlimentacao dia : bolsaDiasAlimentacao.getDiasAlimentacao()) {
					if ( dia.getTipoRefeicao().isCafe() ) {
						diasAlimentacaoDTO.setSegundaC( dia.getSegunda());
						diasAlimentacaoDTO.setTercaC(dia.getTerca());
						diasAlimentacaoDTO.setQuartaC(dia.getQuarta());
						diasAlimentacaoDTO.setQuintaC(dia.getQuinta());
						diasAlimentacaoDTO.setSextaC(dia.getSexta());
						diasAlimentacaoDTO.setSabadoC(dia.getSabado());
						diasAlimentacaoDTO.setDomingoC(dia.getDomingo());
					}
					if ( dia.getTipoRefeicao().isAlmoco() ) {
						diasAlimentacaoDTO.setSegundaA( dia.getSegunda());
						diasAlimentacaoDTO.setTercaA(dia.getTerca());
						diasAlimentacaoDTO.setQuartaA(dia.getQuarta());
						diasAlimentacaoDTO.setQuintaA(dia.getQuinta());
						diasAlimentacaoDTO.setSextaA(dia.getSexta());
						diasAlimentacaoDTO.setSabadoA(dia.getSabado());
						diasAlimentacaoDTO.setDomingoA(dia.getDomingo());
					}
					if ( dia.getTipoRefeicao().isJanta() ) {
						diasAlimentacaoDTO.setSegundaJ( dia.getSegunda());
						diasAlimentacaoDTO.setTercaJ(dia.getTerca());
						diasAlimentacaoDTO.setQuartaJ(dia.getQuarta());
						diasAlimentacaoDTO.setQuintaJ(dia.getQuinta());
						diasAlimentacaoDTO.setSextaJ(dia.getSexta());
						diasAlimentacaoDTO.setSabadoJ(dia.getSabado());
						diasAlimentacaoDTO.setDomingoJ(dia.getDomingo());
					}
				}
				return diasAlimentacaoDTO;
			}
			else
				return new DiasAlimentacaoDTO();
	}

	/**
	 * Verifica se o aluno tem direito a refeição, de acordo com o dia da semana e o tipo da refeição.
	 * 
	 * @param diasAlimentacao
	 * @return
	 * @throws DAOException 
	 */
	private TipoRefeicaoRU verificarPermissaoAlimentacao(BolsaAuxilio diasAlimentacao) throws DAOException {

		boolean horarioValidoCafe = false;
		boolean horarioValidoAlmoco = false;
		boolean horarioValidoJanta = false;
		
		boolean diaSemanaValidoCafe = false;
		boolean diaSemanaValidoAlmoco = false;
		boolean diaSemanaValidoJanta = false;
		
		HashMap<Integer, TipoRefeicaoRU> refeicoesFimSemana = getRefeicoesFimSemana();
		
		for (DiasAlimentacao bax : diasAlimentacao.getDiasAlimentacao()) {
			
			if (bax.getTipoRefeicao().isCafe()) {
				if (!CalendarUtils.isWeekend( Calendar.getInstance()) ) {
					horarioValidoCafe = validarHorario( bax.getTipoRefeicao().getHoraInicio(), bax.getTipoRefeicao().getMinutoInicio(), bax.getTipoRefeicao().getHoraFim(), bax.getTipoRefeicao().getMinutoFim() );
					diaSemanaValidoCafe = verificarBolsaPossuiDiaSemanaAtual(bax);
				}
				if (CalendarUtils.isWeekend( Calendar.getInstance()) ) {
					horarioValidoCafe = validarHorario( refeicoesFimSemana.get(TipoRefeicaoRU.CAFE_FIM_SEMANA).getHoraInicio(), refeicoesFimSemana.get(TipoRefeicaoRU.CAFE_FIM_SEMANA).getMinutoInicio(), refeicoesFimSemana.get(TipoRefeicaoRU.CAFE_FIM_SEMANA).getHoraFim(), refeicoesFimSemana.get(TipoRefeicaoRU.CAFE_FIM_SEMANA).getMinutoFim() );
					diaSemanaValidoCafe = verificarBolsaPossuiDiaSemanaAtual(bax);	
				}
			}
			
			if (bax.getTipoRefeicao().isAlmoco()) {
				
				if (!CalendarUtils.isWeekend( Calendar.getInstance()) ) {
					horarioValidoAlmoco = validarHorario( bax.getTipoRefeicao().getHoraInicio(), bax.getTipoRefeicao().getMinutoInicio(), bax.getTipoRefeicao().getHoraFim(), bax.getTipoRefeicao().getMinutoFim() );
					diaSemanaValidoAlmoco = verificarBolsaPossuiDiaSemanaAtual(bax);
				}
				if (CalendarUtils.isWeekend( Calendar.getInstance()) ) {
					horarioValidoAlmoco = validarHorario( refeicoesFimSemana.get(TipoRefeicaoRU.ALMOCO_FIM_SEMANA).getHoraInicio(), refeicoesFimSemana.get(TipoRefeicaoRU.ALMOCO_FIM_SEMANA).getMinutoInicio(), refeicoesFimSemana.get(TipoRefeicaoRU.ALMOCO_FIM_SEMANA).getHoraFim(), refeicoesFimSemana.get(TipoRefeicaoRU.ALMOCO_FIM_SEMANA).getMinutoFim() );
					diaSemanaValidoAlmoco = verificarBolsaPossuiDiaSemanaAtual(bax);	
				}
			}
			
			if (bax.getTipoRefeicao().isJanta()) {
				if (!CalendarUtils.isWeekend( Calendar.getInstance()) ) {
					horarioValidoJanta = validarHorario( bax.getTipoRefeicao().getHoraInicio(), bax.getTipoRefeicao().getMinutoInicio(), bax.getTipoRefeicao().getHoraFim(), bax.getTipoRefeicao().getMinutoFim() );
					diaSemanaValidoJanta = verificarBolsaPossuiDiaSemanaAtual(bax);
				}
				if (CalendarUtils.isWeekend( Calendar.getInstance()) ) {
					horarioValidoJanta = validarHorario( refeicoesFimSemana.get(TipoRefeicaoRU.JANTA_FIM_SEMANA).getHoraInicio(), refeicoesFimSemana.get(TipoRefeicaoRU.JANTA_FIM_SEMANA).getMinutoInicio(), refeicoesFimSemana.get(TipoRefeicaoRU.JANTA_FIM_SEMANA).getHoraFim(), refeicoesFimSemana.get(TipoRefeicaoRU.JANTA_FIM_SEMANA).getMinutoFim() );
					diaSemanaValidoJanta = verificarBolsaPossuiDiaSemanaAtual(bax);	
				}
			}
		}		
		
		TipoRefeicaoRU tipoRefeicaoRULiberada = new TipoRefeicaoRU();
		if (horarioValidoCafe && diaSemanaValidoCafe)
			tipoRefeicaoRULiberada.setTipoRefeicao(TipoRefeicaoRU.CAFE);
		
		if (horarioValidoAlmoco && diaSemanaValidoAlmoco)
			tipoRefeicaoRULiberada.setTipoRefeicao(TipoRefeicaoRU.ALMOCO);
		
		if (horarioValidoJanta && diaSemanaValidoJanta)
			tipoRefeicaoRULiberada.setTipoRefeicao(TipoRefeicaoRU.JANTA);

		tipoRefeicaoRULiberada.setHorarioCafeValido( horarioValidoCafe );
		tipoRefeicaoRULiberada.setHorarioAlmocoValido( horarioValidoAlmoco );
		tipoRefeicaoRULiberada.setHorarioJantaValido( horarioValidoJanta );
		
		return tipoRefeicaoRULiberada;
	}

	/**
	 * Retorna uma Hash com as refeições do Fim de Semana. Isso é necessário pois
	 * durante o fim de semana o RU tem horário diferenciados da semana.
	 * 
	 * Ordenando da seguinte forma: Café, Almoço, Janta
	 * @return
	 */
	private HashMap<Integer, TipoRefeicaoRU> getRefeicoesFimSemana() {
		
		BolsaAuxilioDao dao = new BolsaAuxilioDao();
		try {
		
			List<TipoRefeicaoRU> lista = (List<TipoRefeicaoRU>) dao.findByExactField(TipoRefeicaoRU.class, "fimSemana", CalendarUtils.isWeekend(Calendar.getInstance()), "asc", "id");			
			HashMap<Integer, TipoRefeicaoRU> hashMap = new HashMap<Integer, TipoRefeicaoRU>();
			
			for (TipoRefeicaoRU tipoRefeicaoRU : lista) {
				if (tipoRefeicaoRU.isCafe())
					hashMap.put(TipoRefeicaoRU.CAFE_FIM_SEMANA, tipoRefeicaoRU);
				if (tipoRefeicaoRU.isAlmoco())
					hashMap.put(TipoRefeicaoRU.ALMOCO_FIM_SEMANA, tipoRefeicaoRU);
				if (tipoRefeicaoRU.isJanta())
					hashMap.put(TipoRefeicaoRU.JANTA_FIM_SEMANA, tipoRefeicaoRU);
			}
			return hashMap;
			
		} catch (DAOException e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return new HashMap<Integer, TipoRefeicaoRU>();		
	}

	/**
	 * Verifica se a bolsa do discente possui um dia da semana que seja igual ao dia da semana atual  
	 * @param bax
	 * @return
	 */
	private boolean verificarBolsaPossuiDiaSemanaAtual(DiasAlimentacao dia) {
		Calendar hoje = Calendar.getInstance();

		if (Calendar.MONDAY == hoje.get(Calendar.DAY_OF_WEEK))
			if (dia.getSegunda())
				return true;
	
		if (Calendar.TUESDAY == hoje.get(Calendar.DAY_OF_WEEK))
			if (dia.getTerca())
				return true;
		
		if (Calendar.WEDNESDAY == hoje.get(Calendar.DAY_OF_WEEK))
			if (dia.getQuarta())
				return true;
		
		if (Calendar.THURSDAY == hoje.get(Calendar.DAY_OF_WEEK))
			if (dia.getQuinta())
				return true;
	
		if (Calendar.FRIDAY == hoje.get(Calendar.DAY_OF_WEEK))
			if (dia.getSexta())
				return true;
		
		if (Calendar.SATURDAY == hoje.get(Calendar.DAY_OF_WEEK))
			if (dia.getSabado())
				return true;
		
		if (Calendar.SUNDAY == hoje.get(Calendar.DAY_OF_WEEK))
			if (dia.getDomingo())
				return true;
		
		return false;
	}

	/**
	 * Verifica o turno atual de acordo com os horários estabelecidos para cada refeição no banco de dados.
	 * Horários da semana são diferentes dos horários de fim de semana.
	 *  
	 * @return
	 * @throws DAOException 
	 * @throws DAOException 
	 */

	private char verificarTurnoAtual() {
		
		BolsaAuxilioDao bolsaDao = new BolsaAuxilioDao();
		
		try {
			java.util.Collection<TipoRefeicaoRU> lista = bolsaDao.findAll(TipoRefeicaoRU.class);
			HashMap<Integer, TipoRefeicaoRU> hashMap = getRefeicoesFimSemana();
			
			if (!CalendarUtils.isWeekend(Calendar.getInstance())) {
				for (TipoRefeicaoRU tipoRU : lista) {
					if ( tipoRU.isCafe() )
						if ( validarHorario( tipoRU.getHoraInicio(), tipoRU.getMinutoInicio(), tipoRU.getHoraFim(), tipoRU.getMinutoFim() ))
							return 'C';
					if ( tipoRU.isAlmoco() )
						if ( validarHorario( tipoRU.getHoraInicio(), tipoRU.getMinutoInicio(), tipoRU.getHoraFim(), tipoRU.getMinutoFim() ))
							return 'A';
					if ( tipoRU.isJanta() )
						if ( validarHorario( tipoRU.getHoraInicio(), tipoRU.getMinutoInicio(), tipoRU.getHoraFim(), tipoRU.getMinutoFim() ))
							return 'J';
				}
			}
			
			
			if (CalendarUtils.isWeekend(Calendar.getInstance())) {
				for (TipoRefeicaoRU tipoRU : lista) {
					if ( tipoRU.isCafe() )
						if ( validarHorario( hashMap.get(TipoRefeicaoRU.CAFE_FIM_SEMANA).getHoraInicio(), hashMap.get(TipoRefeicaoRU.CAFE_FIM_SEMANA).getMinutoInicio(), hashMap.get(TipoRefeicaoRU.CAFE_FIM_SEMANA).getHoraFim(), hashMap.get(TipoRefeicaoRU.CAFE_FIM_SEMANA).getMinutoFim() ))
							return 'C';
					if ( tipoRU.isAlmoco() )
						if ( validarHorario( hashMap.get(TipoRefeicaoRU.ALMOCO_FIM_SEMANA).getHoraInicio(), hashMap.get(TipoRefeicaoRU.ALMOCO_FIM_SEMANA).getMinutoInicio(), hashMap.get(TipoRefeicaoRU.ALMOCO_FIM_SEMANA).getHoraFim(), hashMap.get(TipoRefeicaoRU.ALMOCO_FIM_SEMANA).getMinutoFim() ))
							return 'A';
					if ( tipoRU.isJanta() )
						if ( validarHorario( hashMap.get(TipoRefeicaoRU.JANTA_FIM_SEMANA).getHoraInicio(), hashMap.get(TipoRefeicaoRU.JANTA_FIM_SEMANA).getMinutoInicio(), hashMap.get(TipoRefeicaoRU.JANTA_FIM_SEMANA).getHoraFim(), hashMap.get(TipoRefeicaoRU.JANTA_FIM_SEMANA).getMinutoFim() ))
							return 'J';
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bolsaDao.close();
		}
		
		return '0';
	}

	/**
	 * Cria dois objetos Dates com horários estipulados para verificar em qual intervalo os mesmos estão.  
	 * 
	 * @param horaInicio
	 * @param minutoInicio
	 * @param horaFim
	 * @param minutoFim
	 * @return
	 */
	private boolean validarHorario(int horaInicio, int minutoInicio, int horaFim, int minutoFim) {
		Date dInicio = CalendarUtils.configuraTempoDaData(new Date(), horaInicio, minutoInicio, 0, 0);
		Date dFim = CalendarUtils.configuraTempoDaData(new Date(), horaFim, minutoFim, 0, 0);
		return verificarIntervaloDatas(dInicio, dFim);
	}

	/**
	 * Verifica se os horários informados estão no intervalo estabelecido.
	 * 
	 * Ex: se o momento atual está no horário do café da manhã ou se está entre
	 * o horário do almoço, etc.
	 */
	private boolean verificarIntervaloDatas(Date inicio, Date fim) {
		Date horaAtual = new Date(System.currentTimeMillis());
		if ( horaAtual.after(inicio) && horaAtual.before(fim) )
			return true;
		else
			return false;
	}
	
	/**
	 * Método invocado pelo aplicativo desktop para autenticar o usuário, caso o mesmo possua o papel
	 * correspondente  
	 */
	public ParametroRetornoLogarAcessoRU logarSistemaRU(String login, String senha, String hostName, String hostAddress) {

		Usuario user = logar(login, senha, hostName, hostAddress);

		if (user != null && user.isAutorizado()) {
			if (user.isUserInRole(SigaaPapeis.CONTROLADOR_ACESSO_RESTAURANTE)) {
				ParametroRetornoLogarAcessoRU retorno = new ParametroRetornoLogarAcessoRU();
				retorno.idUsuario = user.getId();
				return retorno;
			}
		}

		return null;
	}

	/**
	 * Registra as informações passadas pelo client desktop, para se ter
	 * controle de quem passa pela catraca.
	 */
	public boolean registrarAcessoRU(int tipoLiberacao, Date dataHora, int idUsuario, Long matricula, int tipoBolsa, String outraJustificativa) {
		
		RegistroAcessoRUDao registroAcessoRUDao = new RegistroAcessoRUDao();
		try {
			// registra acesso ao RU
				registroAcessoRUDao.registrarAcessoRU(tipoLiberacao, dataHora, idUsuario, matricula, tipoBolsa, outraJustificativa, verificarTurnoAtual());
		} finally {
			registroAcessoRUDao.close();
		}
		return true;
	}
	
	/**
	 * Recebe um fluxo de bytes que representa a digital de uma pessoa e verifica e retorna um DTO caso
	 * a identificação seja localizada. 
	 * @throws PessoaDigitalExistenteSistemaExcpetion 
	 * @throws NegocioRemotoException 
	 */
	public PessoaDto identificarPessoa(byte[] digital, String tipoDedo, Long cpf) throws PessoaDigitalExistenteSistemaExcpetion, NegocioRemotoException {

		long cpfLocalizado = identificador.identificar(digital);
		
		if (cpfLocalizado == 0) {
			throw new NegocioRemotoException("Pessoa não identificada ou digital não cadastrada!");
		} else if (cpfLocalizado != cpf) {
		    throw new PessoaDigitalExistenteSistemaExcpetion("Digital já cadastrada no sistema. O CPF atual não é da pessoa que possui essa digital.");
		} else {
			
			PessoaDao dao = null;
			
			try {
				
				dao = DAOFactory.getInstance().getDAO(PessoaDao.class);
				Pessoa p = dao.findByCpf(cpfLocalizado, true);
			
				PessoaDto dto = new PessoaDto();
					dto.setCpf(cpfLocalizado);
					dto.setNome(p.getNome());
					dto.setTipoDedo( dao.findTipoDedoCadastradoByCPF(cpfLocalizado, tipoDedo) );
				
				return dto;
				
			} catch(DAOException e) {
				notifyError(e);
			} catch (SQLException e) {
				notifyError(e);
			} finally {
				if (dao!=null)
					dao.close();
			}
		}
		return null;
	}
	
	/**
	 * Método que invoca o EJB da arquitetura para fazer o logon e retorna o usuário.
	 * 
	 * A senha recebida do aplicativo desktop é um hash MD5
	 * 
	 * @param login
	 * @param senha
	 * @param inetAdd
	 * @return
	 */
	private Usuario logar(String login, String senha, String hostName, String hostAddress) {
		UsuarioMov userMov = new UsuarioMov();
		userMov.setCodMovimento(SigaaListaComando.LOGON);

		Usuario usuario = new Usuario();
		usuario.setLogin(login);
		usuario.setSenha(senha);
		userMov.setUsuario(usuario);
		userMov.setAutenticarComHash(false);

		Usuario user = null;
		try {
			userMov.setIP(hostAddress);
			userMov.setHost(hostName);
			userMov.setUserAgent("Sistema Restaurante Universitario");
			user = (Usuario) executeWithoutClosingSession(userMov, new FacadeDelegate("ejb/SigaaFacade"), usuario, Sistema.SIGAA);			
		} catch (NegocioException e) {
			notifyError(e);
		} catch (ArqException e) {
			notifyError(e);
		}
		return user;
	}

	/** 
	 * Identifica pessoa pelo cartão de acesso ao ru.
	 * @throws NegocioRemotoException 
	 */
	public PessoaDto identificarPessoaRU(String cartaoAcesso, Long matricula) throws NegocioRemotoException{
		
		IdentificacaoCartaoRu identificadorCartao = new IdentificacaoCartaoRu();
		long cpfLocalizado = identificadorCartao.identificarCartaoRu(cartaoAcesso);

		if (cpfLocalizado == 0) {
			throw new NegocioRemotoException("Cartão não identificado!");
		} 
		else {

			PessoaDao dao = null;

			try {

				dao = DAOFactory.getInstance().getDAO(PessoaDao.class);
				Pessoa p = dao.findByCpf(cpfLocalizado, true);

				PessoaDto dto = new PessoaDto();
				dto.setCpf(cpfLocalizado);
				dto.setNome(p.getNome());

				return dto;

			} catch(DAOException e) {
				notifyError(e);
			} finally {
				if (dao!=null)
					dao.close();
			}
		}
		return null;
	}

	
	public DiasAlimentacaoDTO autorizarRefeicaoCartao(String cartaoAcesso) throws NegocioRemotoException {
		List<BolsaAuxilio> bolsaDiasAlimentacao = null;
		try {

			// Identifica pessoa
			IdentificacaoCartaoRu identificadorCartao = new IdentificacaoCartaoRu();
			long cpf = identificadorCartao.identificarCartaoRu(cartaoAcesso);
			
			if (cpf == 0) {
				throw new NegocioRemotoException("PESSOA NÃO IDENTIFICADA OU CARTÃO NÃO CADASTRADO!");
			} else {

                ConfiguracoesPeriodoResultadoDAO confPerioResultDAO = new ConfiguracoesPeriodoResultadoDAO();
                
                long tipoBolsa = tipoBolsaDiscente(cpf);
				
                AnoPeriodoReferenciaSAE anoPeriodoReferenciaSAE = confPerioResultDAO.findAnoPeriodoResultadoSAE();
                confPerioResultDAO.close();
	                
                DiasAlimentacaoDao dao = new DiasAlimentacaoDao();
                // traz a bolsa com seus respectivos dias de alimentação
                bolsaDiasAlimentacao = dao.findDiscentesDiasAlimentacaoMapaRU(cpf, anoPeriodoReferenciaSAE, tipoBolsa);
                dao.close();
                
                if (bolsaDiasAlimentacao != null) {
	                // só existe uma bolsa por cpf para cada Ano/Período
                	// popula o DTO de acordo com a bolsa passada
					DiasAlimentacaoDTO diasAlimentacaoDTO = popularDTO(bolsaDiasAlimentacao.get(0)); 
					
					// verifica se o discente já teve acesso a refeição daquele horário/dia
					if ( verificarDuplicacaoAcessoByMatricula(bolsaDiasAlimentacao.get(0).getDiscente().getMatricula()) ) {
						diasAlimentacaoDTO.setAcessouRURefeicaoAtual(true); //seta que o acesso é inválido para aquela refeição
						return diasAlimentacaoDTO;
					}
					else
						return diasAlimentacaoDTO;
                }
                else{
                	bolsaDiasAlimentacao = dao.findDiscentesDiasAlimentacaoMapa(cpf);
                	  if (bolsaDiasAlimentacao != null) {
      	                // só existe uma bolsa por cpf para cada Ano/Período
                      	// popula o DTO de acordo com a bolsa passada
      					DiasAlimentacaoDTO diasAlimentacaoDTO = popularDTO(bolsaDiasAlimentacao.get(0)); 
      					
      					// verifica se o discente já teve acesso a refeição daquele horário/dia
      					if ( verificarDuplicacaoAcessoByMatricula(bolsaDiasAlimentacao.get(0).getDiscente().getMatricula()) ) {
      						diasAlimentacaoDTO.setAcessouRURefeicaoAtual(true); //seta que o acesso é inválido para aquela refeição
      						return diasAlimentacaoDTO;
      					}
      					else
      						return diasAlimentacaoDTO;
                      }
                }
                	return new DiasAlimentacaoDTO();
			}

		} catch (HibernateException e) {
			e.printStackTrace();
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
}