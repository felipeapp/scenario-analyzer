/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '19/11/2008'
 *
 */

package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.CalendarUtils.createDate;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dao.FeriadoDao;
import br.ufrn.comum.dominio.Feriado;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.AulaFrequencia;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.negocio.FrequenciaHelper;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.jsf.GerarDiarioClasse;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.dominio.FrequenciaMov;
import br.ufrn.sigaa.ensino.negocio.dominio.MovimentoFrequenciaPlanilha;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;
import br.ufrn.sigaa.parametros.dominio.ParametrosTurmaVirtual;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Managed-Bean para aluno
 */
@Component("frequenciaAluno") @Scope("request")
public class FrequenciaAlunoMBean extends CadastroTurmaVirtual<FrequenciaAluno> {

	/** Link para a página da planilha de frequência. */
	public static final String PAGINA_FORM_PLANILHA = "/ava/FrequenciaAluno/formPlanilha.jsp";
	
	/** Dados das frequências dos alunos que serão usados na planilha de frequência. */
	private String dadosFrequenciaPlanilha;
	/** Dados das datas das aula da turma que serão usados na planilha de frequência. */
	private String dadosDatasAulasPlanilha;
	
	/** Caso o acesso seja feito pelo docente: Lista de todas as frequências da turma numa data */
	/** Caso o acesso seja feito pelo discente: Lista de todas as frequências de um aluno numa data. */
	private List<FrequenciaAluno> frequencias;

	/** Lista de todas as faltas da primeira unidade.*/ 
	private List<FrequenciaAluno> listaFaltasPorUnidade1 = new ArrayList<FrequenciaAluno>();
	/** Lista de todas as faltas da segunda unidade.*/ 
	private List<FrequenciaAluno> listaFaltasPorUnidade2 = new ArrayList<FrequenciaAluno>();
	/** Lista de todas as faltas da terceira unidade.*/ 
	private List<FrequenciaAluno> listaFaltasPorUnidade3 = new ArrayList<FrequenciaAluno>();
	
	/** Mapeia os discentes com suas faltas por unidade. */
	private Map<String, List<Short>> mapa = new TreeMap<String, List<Short>>();
	
	/** Indica se as frequências devem ser lançadas em planilha. */
	private boolean lancarEmPlanilha;
	
	/** Número máximo de presenças para um determinado dia. */
	private short maxFaltas;

	/** Data onde sera lançada a frequência. */
	private Date dataSelecionada;

	/** Dia em que sera lançada a frequência. */
	private Integer diaNovo;

	/** Ultimo dia em que a frequência foi lancada. */
	private Integer diaAntigo;
	
	/** Turma onde as frequências estão sendo lançadas. */
	private Turma turma;

	/** Indica se a turma atual pode ter frequências registradas com impressão digital. */
	private boolean turmaChamadaBiometrica;

	/** Lista contendo todas as presenças dos alunos da turma. */
	private List <Object []> listagemPlanilha;
	
	/** Lista contendo todos os dias de aula para esta turma. */
	private List <AulaFrequencia> listagemAulas;
	
	/** Indica se houve sucesso no cadastro. */
	private boolean cadastrado = false;
	
	/** Informações sobre a turma. */
	private ParametrosGestoraAcademica parametrosGestora;
	
	/** Map contendo os dias de aula da turma */
	private List <Map <String, Object>> calendarios;
	
	/** Indica se deve permanecer listando a data selecionada. */
	private boolean permanecerDataSelecionada = false;
	
	/**Tempo de salvamento automático da planilha de frequência*/
	private int tempoSalvamentoPlanilha = 0;
	
	public FrequenciaAlunoMBean() {
		init();
		tempoSalvamentoPlanilha = ParametroHelper.getInstance().getParametroInt(ParametrosTurmaVirtual.TEMPO_SALVAMENTO_AUTOMATICO_PLANILHA_FREQUENCIA);
	}

	/** 
	 * Inicializa o bean.
	 */
	private void init() {
		object = new FrequenciaAluno();
		object.setData( new Date() );
		object.setDiscente( new Discente() );
		object.setTurma( new Turma() );
		setFeriadosTurma(null);
		frequencias = null;
	}

	/**
	 * Retorna uma lista com as quantidades de faltas dos alunos
	 * divididas por unidade.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/menu.jsp
	 * @return
	 * @throws DAOException
	 */
	public String totalDeFaltasPorUnidade() throws DAOException {

		mapa.clear();
		listaFaltasPorUnidade1.clear();
		listaFaltasPorUnidade2.clear();
		listaFaltasPorUnidade3.clear();
		
		ConfiguracoesAva config = getDAO(TurmaVirtualDao.class).findConfiguracoes(turma());
		if (config == null)
			config = new ConfiguracoesAva();

		if ( config.getDataFimPrimeiraUnidade() == null || config.getDataFimSegundaUnidade() == null ||
					config.getDataFimTerceiraUnidade() == null ) {
			addMensagem(MensagensTurmaVirtual.DATAS_UNIDADES_NAO_CONFIGURADAS);
		}
		else {
			FrequenciaAlunoDao freqDao = getDAO(FrequenciaAlunoDao.class);

			int unid1 = 1;
			int unid2 = 2;
			int unid3 = 3;
			
			if (isEmpty(turma().getSubturmas())) {
				listaFaltasPorUnidade1 = freqDao.findTotalDeFaltasPorUnidade(getCalendarioVigente(turma()).getInicioPeriodoLetivo(), config.getDataFimPrimeiraUnidade(), config.getTurma().getId(), unid1);
				listaFaltasPorUnidade2 = freqDao.findTotalDeFaltasPorUnidade(config.getDataFimPrimeiraUnidade(), config.getDataFimSegundaUnidade(), config.getTurma().getId(), unid2);
				listaFaltasPorUnidade3 = freqDao.findTotalDeFaltasPorUnidade(config.getDataFimSegundaUnidade(), config.getDataFimTerceiraUnidade(), config.getTurma().getId(), unid3);
			} else {
				listaFaltasPorUnidade1 = freqDao.findTotalDeFaltasPorUnidadeSubTurmas(getCalendarioVigente(turma()).getInicioPeriodoLetivo(), config.getDataFimPrimeiraUnidade(), config.getTurma().getId(), unid1);
				listaFaltasPorUnidade2 = freqDao.findTotalDeFaltasPorUnidadeSubTurmas(config.getDataFimPrimeiraUnidade(), config.getDataFimSegundaUnidade(), config.getTurma().getId(), unid2);
				listaFaltasPorUnidade3 = freqDao.findTotalDeFaltasPorUnidadeSubTurmas(config.getDataFimSegundaUnidade(), config.getDataFimTerceiraUnidade(), config.getTurma().getId(), unid3);
			}

				for (FrequenciaAluno itLista1 : listaFaltasPorUnidade1) {
					
					if (itLista1.getDiscente() != null) {
						mapa.put(itLista1.getDiscente().getNome(), new ArrayList<Short>());
						mapa.get(itLista1.getDiscente().getNome()).add(itLista1.getFaltas());						
					}
					
				}
			
					//exibe ZEROS, caso não exista faltas lançadas.
					if ( listaFaltasPorUnidade2.size() == 0 && listaFaltasPorUnidade1.size() > 0 ) {				
						for (FrequenciaAluno itLista1 : listaFaltasPorUnidade1) {			
							if (itLista1.getDiscente() != null) {
								mapa.get(itLista1.getDiscente().getNome()).add((short)0);
							}
						}				
					}
			
			for (FrequenciaAluno itLista2 : listaFaltasPorUnidade2) {
				if (itLista2.getDiscente() != null && itLista2.getFaltas() != 0) {
					if (mapa.get(itLista2.getDiscente().getNome()) == null) mapa.put(itLista2.getDiscente().getNome(), new ArrayList<Short>());
					mapa.get(itLista2.getDiscente().getNome()).add(itLista2.getFaltas());
				}
			}
			
				//exibe ZEROS, caso não exista faltas lançadas.
				if ( listaFaltasPorUnidade3.size() == 0 && listaFaltasPorUnidade1.size() > 0 ) {				
					for (FrequenciaAluno itLista1 : listaFaltasPorUnidade1) {
						if (itLista1.getDiscente() != null) {
							mapa.get(itLista1.getDiscente().getNome()).add((short)0);
						}
					}				
				}
			
			for (FrequenciaAluno itLista3 : listaFaltasPorUnidade3) {
				if (itLista3.getDiscente() != null && itLista3.getFaltas() != (short)0) {					
					mapa.get(itLista3.getDiscente().getNome()).add(itLista3.getFaltas());
				}
			}
			
			return forward("/ava/FrequenciaAluno/listar.jsp");
		}
		return "";		
	}

	/**
	 * Cria um documento PDF com a sessão "mapa de frequência"
	 * do diário de turma.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/menu.jsp
	 * 
	 * @return
	 * @throws Exception
	 */
	public String mapaFrequencia() throws Exception {
		
		TurmaDao dao = null;
		FrequenciaAlunoDao freqDao = null;
		AvaliacaoDao avDao = null;
		MatriculaComponenteDao mDao = null;
		
		try {
			
			dao = getDAO(TurmaDao.class);
			freqDao = getDAO(FrequenciaAlunoDao.class);
			avDao = getDAO(AvaliacaoDao.class);
			mDao = getDAO(MatriculaComponenteDao.class);
			
			Turma turma = dao.findByPrimaryKey(turma().getId(), Turma.class);
			turma.setMatriculasDisciplina(mDao.findAtivasByTurma(turma));
			
			boolean ok = false;
			if (turma.getSubturmas() != null)
				for (Turma t : turma.getSubturmas())
					if (!t.getMatriculasDisciplina().isEmpty()){
						ok = true;
						break;
					}
			
			if (!turma.getMatriculasDisciplina().isEmpty())
				ok = true;
			
			if (ok){
				ParametrosGestoraAcademica p = ParametrosGestoraAcademicaHelper.getParametros(turma);
				CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(turma);
		
				HttpServletResponse res = getCurrentResponse();
				
				new GerarDiarioClasse(turma, calendario, p, dao, freqDao, avDao, getDAO(UsuarioDao.class), true).gerarMapa(res.getOutputStream());
				res.setContentType("application/pdf");
				res.addHeader("Content-Disposition", "attachment; filename=mapa_"+turma.getDisciplina().getCodigo()+"_"+turma.getAnoPeriodo()+"_"+turma.getCodigo()+"_.pdf");
				FacesContext.getCurrentInstance().responseComplete();
			} else
				addMensagem(MensagensTurmaVirtual.NAO_HA_ALUNOS_MATRICULADOS);
			return null;
		} finally {
			if (dao != null)
				dao.close();
			if (freqDao != null)
				freqDao.close();
			if (avDao != null)
				avDao.close();
			if (mDao != null)
				mDao.close();
		}
	}

	/**
	 * Direciona um aluno para uma página contendo as suas presenças
	 * e faltas em todos os dias cujas frequências foram lançadas pelo professor.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/menu.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String frequenciaAluno() throws DAOException {
		popularFrequenciaAluno();
		
		if (turma == null)
			turma = turma();
		
		return forward("/ava/FrequenciaAluno/mapa.jsp");
	}
	
	/**
	 * Popula a lista de frequencia dos alunos.
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void popularFrequenciaAluno() throws HibernateException, DAOException {
		TurmaDao tDao = null;
		FrequenciaAlunoDao fDao = null;
		
		try {
			Turma turma = this.turma;
			if (turma == null) turma = turma();
			
			if (getDiscenteUsuario() == null)
				throw new RuntimeNegocioException("Você não é discente desta turma.");
			
			// Se a turma que o aluno está for agrupadora, deve buscar a frequência dele na subturma em que ele está matriculado.
			if (turma.isAgrupadora()){
				tDao = getDAO(TurmaDao.class);
				turma = tDao.findSubturmaByTurmaDiscente(turma, getDiscenteUsuario().getDiscente());
			}
				
			fDao = getDAO(FrequenciaAlunoDao.class);
			frequencias = fDao.findFrequenciasByDiscente(getDiscenteUsuario().getDiscente(), turma);
							
			//buscar os dias que são feriados
			if( isEmpty(getFeriadosTurma()) )
				setFeriadosTurma( TurmaUtil.getFeriados(turma) );
					
			Iterator<FrequenciaAluno> itrFrequencias = frequencias.iterator();
			Iterator<Date> itrFeriados = getFeriadosTurma().iterator();
			
						
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.YEAR, -1);
			
			//remove os feriados de anos inferiores ao ano passado.
			while (itrFeriados.hasNext()) {
				Date d = itrFeriados.next();
				if (CalendarUtils.getAno(d) < c.get(Calendar.YEAR)) {
					itrFeriados.remove();
				}
				
			}
						
			//para cada possivel dia de aula
			while (itrFrequencias.hasNext()) {
				FrequenciaAluno aux = itrFrequencias.next();
				Date d = aux.getData();
				
				itrFeriados = getFeriadosTurma().iterator();
				//caso o possivel dia de aula seja um feriado remova-o da lista.
				while (itrFeriados.hasNext() ){
					if (CalendarUtils.isSameDay(d,itrFeriados.next())) {
						itrFrequencias.remove();
						break;
					}
					
				}
						
			}
		} finally {
			if (tDao != null)
				tDao.close();
			if (fDao != null)
				fDao.close();
		}
	}

	/**
	 * Cadastra frequência para todos os alunos em um dia selecionado.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/FrequenciaAluno/form.jsp
	 */
	@Override
	public String cadastrar() {

		try {

			if(dataSelecionada == null) {
				addMensagemErro("Selecione uma data para lançar a frequência.");
				registrarAcao(null, EntidadeRegistroAva.FREQUENCIA, AcaoAva.INICIAR_INSERCAO, turma().getId() );
				return null;
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String data = sdf.format(dataSelecionada);
			registrarAcao(data, EntidadeRegistroAva.FREQUENCIA, AcaoAva.INICIAR_INSERCAO, turma().getId() );
			
			if(dataSelecionada.after(new Date())) {
				addMensagemErro("Não é possível cadastrar frequências para datas posteriores à atual.");
				return null;
			}
			if (turma.isAberta()){
				prepareMovimento(SigaaListaComando.LANCAR_FREQUENCIA);
				execute(new FrequenciaMov(frequencias), getCurrentRequest());
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				calendarios = null;
				registrarAcao(data, EntidadeRegistroAva.FREQUENCIA, AcaoAva.INSERIR, turma().getId() );

			} else
				addMensagemErro("Esta turma já está consolidada e não pode ser alterada.");
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * Cadastra frequência para todos os alunos em todos os dias.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/FrequenciaAluno/formPlanilha.jsp
	 * @throws ArqException 
	 */
	public String cadastrarPlanilha() throws ArqException {
		try {
			
		if (isOperacaoAtiva(SigaaListaComando.LANCAR_FREQUENCIA_PLANILHA.getId())) {
			
			if (turma.isAberta()){
			
				// Corrigindo problema com CharSet do javascript.
				String [][] valores = converteEmVetor(dadosFrequenciaPlanilha);
				preparaDadosPlanilha();
				String [][] original = converteEmVetor(dadosFrequenciaPlanilha);
				
				for (int i = 0; i < valores.length; i++)
					if (!valores[i][5].equals("T"))
						if (!original[i][5].equals(valores[i][5])){
							original[i][5] = valores[i][5];
							// Atualiza os valores e indica que foram modificados.
							listagemPlanilha.get(i)[5] = original[i][5];
							listagemPlanilha.get(i)[7] = "true";
						}
				
				dadosFrequenciaPlanilha = converteEmString(original);
				
				
				try {
					execute(new MovimentoFrequenciaPlanilha(listagemPlanilha, turma), getCurrentRequest());
					setOperacaoAtiva(null);
					addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
					calendarios = null;
					cadastrado = true;
				
				} catch (NegocioException e) {
					addMensagens(e.getListaMensagens());
				}
			
			}  else
				addMensagemErro("Esta turma já está consolidada e não pode ser alterada.");
		}
		
		
		// Tratamento de erro que ocorre quando é feita duas requisições ao mesmo tempo.
		} catch (ArrayIndexOutOfBoundsException e) {
			addMensagemWarning("A planilha não pode ser salva, por favor tente novamente.");
		}
				
		return null;
	}
	
	/**
	 * Cadastra frequência para todos os alunos em todos os dias de forma automatica.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/FrequenciaAluno/formPlanilha.jsp
	 * @throws ArqException 
	 */
	public void cadastrarPlanilhaAutomatico() throws ArqException {
		
		try {
		
		if (turma.isAberta()){
			// Corrigindo problema com CharSet do javascript.
			String [][] valores = converteEmVetor(dadosFrequenciaPlanilha);
			preparaDadosPlanilha();
			String [][] original = converteEmVetor(dadosFrequenciaPlanilha);
			
			for (int i = 0; i < valores.length; i++)
				if (!valores[i][5].equals("T"))
					if (!original[i][5].equals(valores[i][4])){
						original[i][5] = valores[i][5];
						// Atualiza os valores e indica que foram modificados.
						listagemPlanilha.get(i)[5] = original[i][5];
						listagemPlanilha.get(i)[7] = "true";
					}
			
			dadosFrequenciaPlanilha = converteEmString(original);
			
			
			try {
				execute(new MovimentoFrequenciaPlanilha(listagemPlanilha, turma), getCurrentRequest());
				prepareMovimento(SigaaListaComando.LANCAR_FREQUENCIA_PLANILHA);
				setOperacaoAtiva(SigaaListaComando.LANCAR_FREQUENCIA_PLANILHA.getId());
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				calendarios = getCalendarios();
			
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
			}
		
		}  
		
		} catch (ArrayIndexOutOfBoundsException e) {
			addMensagemWarning("A planilha não foi salva automaticamente.");
		}
		
	}

	
	/**
	 * Converte a string passada para um vetor.
	 * A string passada deve ser no formato "1,2,3;4,5,6;7,8,9"
	 */
	private String [][] converteEmVetor (String frequencias){
		String [] fs = frequencias.split(";");
		String [][] vetor = new String[fs.length][];
		for (int i = 0; i < fs.length; i++)
			vetor[i] = fs[i].split(",");
		
		return vetor;
	}
	
	/**
	 * Converte o vetor passado na string que o representa.
	 */
	private String converteEmString (String [][] vetor){
		String string = "";
		
		for (int i = 0; i < vetor.length; i++){
			String f = "";
			for (int j = 0; j < vetor[i].length; j++)
				f += (f.equals("") ? "" : ",") + vetor[i][j];
			string += (string.equals("") ? "" : ";") + f;
		}
		
		return string;
	}

	/**
	 * Retorna a lista com os nomes dos meses para serem usados
	 * nos calendários para lançamento de frequência.
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public Map<Integer, String> getMesesString() throws SegurancaException, DAOException {
		Set<Date> datas = TurmaUtil.getDatasAulasTruncate(turma, getCalendarioVigente(turma));
		Map<Integer, String> mapaMeses = new LinkedHashMap<Integer, String>();
		
		for (Date data : datas) {
			Calendar c = Calendar.getInstance();
			c.setTime(data);

			int mes = c.get(Calendar.MONTH);
			if (!mapaMeses.containsKey(mes))
				mapaMeses.put(mes, getMes(mes));
		}
		return mapaMeses;
	}

	/**
	 * Retorna o ano de um determinado mês das aulas da turma.
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public Integer getAnoAula(Integer mes) throws SegurancaException, DAOException {
		
		if (mes != null){
			Set<Date> datas = TurmaUtil.getDatasAulasTruncate(turma, getCalendarioVigente(turma));
	
			for (Date data : datas) {
				Calendar c = Calendar.getInstance();
				c.setTime(data);
				int mesAux = c.get(Calendar.MONTH);
				int ano = c.get(Calendar.YEAR);
				if (mesAux == mes)
					return ano;
			}
		}	
		return null;
	}
	
	/**
	 * Cancela a operação.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/FrequenciaAluno/form.jsp
	 * 
	 */
	@Override
	public String cancelar() {
		init();
		resetBean();
		
		try {
			TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
			return tBean.retornarParaTurma();
		} catch (DAOException e){
			tratamentoErroPadrao(e);
			return null;
		}
	}
	/**
	 * Retorna uma lista de possíveis dados para o lançamento
	 * de frequência: presença ou um número de faltas.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getPresencasCombo() throws DAOException {
		ArrayList<SelectItem> itens = new ArrayList<SelectItem>();
		for(Short i = 0; i <= maxFaltas; i++){
			if (i == 0)
				itens.add( new SelectItem(i, "Presente") );
			else
				itens.add( new SelectItem(i, i + " Falta" + (i != 1 ? "s" :"")) );
		}
		return itens;
	}

	/**
	 * Retorna o número total de horários de um determinado dia de aula de uma turma.
	 * 
	 * @param turma
	 * @param diaSemana
	 * @return
	 */
	private short getPresencasDia(Turma turma, Date data) {
		
		int diaSemana = CalendarUtils.getDiaSemanaByData(data);
		
		short presencas = 0;
		for( HorarioTurma ht: turma.getHorarios()){
			if(ht.getDataInicio().getTime() <= data.getTime() && ht.getDataFim().getTime() >= data.getTime() && diaSemana == Character.getNumericValue(ht.getDia()) ){
				presencas++;
			}
		}

		return presencas;
	}

	public short getMaxFaltas() {
		return maxFaltas;
	}

	public void setMaxFaltas(short maxFaltas) {
		this.maxFaltas = maxFaltas;
	}

	/**
	 * Retorna uma matriz de inteiros com o calendário para o mês
	 * e ano passados como parâmetro.
	 * 
	 * @param mes
	 * @param ano
	 * @return
	 */
	public int[][] getCalendario(int mes, int ano) {
		int[][] calendario = new int[6][7];
		Calendar c = Calendar.getInstance();
		c.set(ano, mes, 1);

		int inicio = c.get(Calendar.DAY_OF_WEEK) - 1;
		int total = c.getActualMaximum(Calendar.DAY_OF_MONTH);

		int j = 0; int dia = 0;
		for (int i = inicio; i < total + inicio; i++) {
			if (i % 7 == 0) j++;
			calendario[j][i % 7] = ++dia;
		}
		return calendario;
	}

	/**
	 * Direciona o usuário para a página de lançamento de frequência de uma
	 * turma.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/menu.jsp
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String lancar() throws DAOException {
		
		if (turma == null)
			turma = turma();
		
		this.frequencias = null;
		
		turmaChamadaBiometrica = getDAO(TurmaVirtualDao.class).findEstacaoChamadaBiometricaByTurma(turma());
		diaNovo = getParameterInt("dia");
		

		CalendarioAcademico calendarioAcademico = getCalendarioVigente(turma);
		Set<Date> datasAulas = TurmaUtil.getDatasAulasTruncate(turma, calendarioAcademico);

		if (isEmpty(datasAulas) ) {
			if (!turma.isTurmaEnsinoIndividual()){
				addMensagemErro("Esta turma não está com os horários definidos.");
				return null;
			} else {
				addMensagemErro("Ainda não foi cadastrados horários para essa turma. Para cadastrá-los acesse: Menu Turma Virtual -> Turma -> Registrar Aula de Ensino Individual.");
				return null;
			}
		} 
		
		
		return forward("/ava/FrequenciaAluno/form.jsp");
	}
	
	/**
	 * Direciona o usuário para a página de lançamento de frequência de uma
	 * turma.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/menu.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String lancarPlanilha() throws ArqException {
		listagemPlanilha = null;
		listagemAulas = null;
		cadastrado = false;
		
		if (!lancarEmPlanilha)
			turma = turma();
		
		if (turma.getDisciplina().getUnidade() != null)
			turma.getDisciplina().setUnidade(getGenericDAO().refresh(turma.getDisciplina().getUnidade()));
		
		// Verifica se a turma registra frequência.
		getCalendarioVigente(turma);
		
		// Se não há matrículas ativas, este caso de uso não deve ser utilizado.
		long matriculas = getDAO(MatriculaComponenteDao.class).findTotalMatriculasByTurma(turma, true);
		if (matriculas == 0){
			addMensagem(MensagensTurmaVirtual.NAO_HA_ALUNOS_MATRICULADOS);
			return null;
		}
		
		turmaChamadaBiometrica = getDAO(TurmaVirtualDao.class).findEstacaoChamadaBiometricaByTurma(turma);
		parametrosGestora = params();
		
		prepareMovimento(SigaaListaComando.LANCAR_FREQUENCIA_PLANILHA);
		setOperacaoAtiva(SigaaListaComando.LANCAR_FREQUENCIA_PLANILHA.getId());
		preparaDadosPlanilha();
		
		if (isEmpty(listagemPlanilha)){
			addMensagemErro("Esta turma não está com os horários definidos.");
			return null;
		}
		
		return forward(PAGINA_FORM_PLANILHA);
	}
	
	/**
	 * Caso a frequência seja iniciada em uma turma agrupadora, 
	 * ela deverá acontecer sub-turma por sub-turma. Este método é chamado para 
	 * listar as sub-turmas da turma agrupadora.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/menu.jsp
	 * @return
	 */
	public String listaSubTurmas() {
		Boolean planilha = getParameterBoolean("planilha");
		lancarEmPlanilha = (planilha == true) ? true : false;
		
		return forward("/ava/FrequenciaAluno/subturmas.jsp");
	}
	
	/**
	 * Caso a frequência seja iniciada em uma turma agrupadora, 
	 * ela deverá acontecer sub-turma por sub-turma. Este método é chamado para 
	 * listar as sub-turmas da turma agrupadora para então exibir a planilha.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/menu.jsp
	 * @return
	 */
	public String listaSubTurmasPlanilha() {
		lancarEmPlanilha = true;
		return forward("/ava/FrequenciaAluno/subturmas.jsp");
	}
	
	/**
	 * Seleciona a sub-turma caso a turma da turma virtual seja agrupadora.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ensino/consolidacao/subturmas.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String escolherSubTurma() throws ArqException {
		turma = getDAO(TurmaDao.class).findAndFetch(getParameterInt("id"), Turma.class, "turmaAgrupadora");
		turma.getDisciplina().setUnidade(getDAO(TurmaDao.class).refresh(turma.getDisciplina().getUnidade()));
		turma.setHorarios(getDAO(TurmaDao.class).findHorariosByTurma(turma));
		
		this.frequencias = null;
		
		if (lancarEmPlanilha){
			return lancarPlanilha();
		} else {
			calendarios = null;
			return forward("/ava/FrequenciaAluno/form.jsp");
		}
	}
	
	@Override
	public List<FrequenciaAluno> getListagem() {
		TurmaVirtualMBean turmaVirtual = getMBean("turmaVirtual");
		if(turmaVirtual.isDiscente() && !turmaVirtual.isPermissaoDocente() && listagem == null) {
			try {
				popularFrequenciaAluno();
			} catch (DAOException e) {
				tratamentoErroPadrao(e);
			}
		}
		return lista();
	}
	
	/**
	 * Prepara os dados contendo as datas das aulas e as frequências dos alunos para serem utilizados na planilha.
	 * 
	 * @return
	 * @throws ArqException 
	 */
	private void preparaDadosPlanilha () throws ArqException{
		
		TurmaDao dao = null;
		FrequenciaAlunoDao fDao = null;
		
		try {
			
			// Índices referentes aos campos do array de listagemPlanilha
			final int ID_MAT = 0;
			final int MAT = 1;
			final int NOME = 2;
			final int DIA = 3;
			final int MES = 4;
			final int NUM_FALTAS = 5;
			final int ID_FREQ = 6;
			final int ALTERADA = 7;
			final int NUM_AULAS = 8;
			final int ID_DISCENTE = 9;
			final int DATA = 10;
			final int TRANCADO = 11;
			
			// Índices referentes ao Object[] retornado na busca de matrículas 
			final int M_ID_MAT = 0;
			final int M_MAT = 1;
			final int M_NOME = 2;
			final int M_ID_DISCENTE = 3;
			final int M_TRANCADO = 4;
			
			dao = getDAO(TurmaDao.class);
			fDao = getDAO(FrequenciaAlunoDao.class);
			listagemPlanilha = dao.findPresencaPlanilhaByTurma(turma.getId());
			
			List <Object []> aux = new ArrayList <Object []> ();
			
			getListagemAulas();
			
			// Se não tem frequências registradas, cria os objetos a serem preenchidos
			if ( listagemPlanilha.isEmpty()){
				// Busca as matrículas
				List <Object []> mats = fDao.findMatriculasPlanilhaByTurma(turma.getId());
				
				for ( Object [] m : mats  ) 
					for (AulaFrequencia a : listagemAulas){
						Object [] o = { m[M_ID_MAT], m[M_MAT] , m[M_NOME] , a.getDia() , a.getMes(), null, null, false, getNumeroAulas(a), m[M_ID_DISCENTE], a.getData(), m[M_TRANCADO]};
						listagemPlanilha.add(o);
					}
				
			// Se já tem frequências registradas, completa as lacunas
			} else {
			
				int i = 0;
				int idMatricula = -1;
				List <Object []> mats = fDao.findMatriculasPlanilhaByTurma(turma.getId());
			
				// Caso o professor preencha as frequencias pela planilha, e na turma possuir alunos que fizeram rematricula e não tiveram suas frequencias cadastradas no caso acima.
				for ( Object [] m : mats  ) 
					for (AulaFrequencia a : listagemAulas){
						Object [] o = { m[M_ID_MAT], m[M_MAT] , m[M_NOME] , a.getDia() , a.getMes(), null, null, false, getNumeroAulas(a), m[M_ID_DISCENTE], a.getData(), m[M_TRANCADO]};
						boolean rematricula = true;
						Iterator<Object[]> it = listagemPlanilha.iterator();  
						while (it.hasNext()) {
							Object [] p = it.next(); 
							if ( o[MAT].toString().compareToIgnoreCase(p[MAT].toString()) == 0 )	
								rematricula = false;
						}
					if ( rematricula )
						listagemPlanilha.add(o);
				}		
				
				Collections.sort(listagemPlanilha, new Comparator<Object []>(){
					public int compare(Object [] o1, Object [] o2) {
						String nome1 = StringUtils.toAscii( (String) o1[NOME]);
						String nome2 = StringUtils.toAscii( (String) o2[NOME]);
						int retorno = nome1.compareToIgnoreCase(nome2);
						if ( retorno == 0 ) {
							Long matricula1 = o1[MAT] != null ? ((Number)o1[MAT]).longValue() : 0;
							Long matricula2 = o2[MAT] != null ? ((Number)o2[MAT]).longValue() : 0;
							retorno = matricula1.compareTo(matricula2);
							if ( retorno == 0 ){
								Integer idMatricula1 = o1[ID_MAT] != null ? ((Number)o1[ID_MAT]).intValue() : 0;
								Integer idMatricula2 = o2[ID_MAT] != null ? ((Number)o2[ID_MAT]).intValue() : 0;
								retorno = idMatricula1.compareTo(idMatricula2);
								if ( retorno == 0 ){
									Date d1 = (Date) o1[DATA];
									Date d2 = (Date) o2[DATA];
									retorno = d1.compareTo(d2);
								}
							}
						}
						return retorno;
					}
				});
				
				boolean falhou = false;
				for (Object [] p : listagemPlanilha){
					
					int contagemAulasInvalidas = 0;
					
					// Se mudou o aluno,
					if (idMatricula != ((Number) p[ID_MAT]).intValue()){
						idMatricula = ((Number) p[ID_MAT]).intValue();
						
						// Completa as presenças do aluno anterior, caso precise
						if (!aux.isEmpty()){
							// Pega o usuário anterior.
							Object [] f = aux.get(aux.size() - 1);
							while (i < listagemAulas.size()){
								AulaFrequencia aula = listagemAulas.get(i);
								Object [] o = {f[ID_MAT], f[MAT], f[NOME], aula.getDia(), aula.getMes(), null, 0, false, getNumeroAulas(aula), f[ID_DISCENTE], aula.getData(), f[TRANCADO]};
								aux.add(o);
								i++;
							}
						}
						
						// Reinicia a contagem de aulas para o novo aluno.
						i = 0;
					}
					
					// Se a presença ainda não foi cadastrada para esse dia,
					if (p[2] == null){
						for (AulaFrequencia aula : listagemAulas){
							Object [] o = {p[ID_MAT], p[MAT], p[NOME], aula.getDia(), aula.getMes(), null, 0, false, getNumeroAulas(aula), p[ID_DISCENTE], aula.getData(), p[TRANCADO]};
							aux.add(o);
							i++;
						}
					// Se a presença para este aluno foi cadastrada neste dia.
					} else {
						boolean ok = false;
						AulaFrequencia aula = null;
			
						// Aqui, retira as frequencias que foram cadastradas no passo abaixo caso tenha ocorrido algum erro.
						// Ex: Tem aula dia 8, 10 e 12. O sistema estava esperando a frequência do dia 8 e recebeu a do dia 12.
						//     Então adiciona as frequências do dia 8 e 10. 
						// 	   Mas a aula do dia 12 veio incorreta, e agora a nova frequencia é do dia 8
						//	   Deve-se retirar as antigas frequecias dos dias 8 e 10.
						if ( falhou ){
							int j = 0;
							boolean remover = false;
							for ( Object[] o : aux ){
								if ( p[ID_MAT].equals(o[ID_MAT]) && o[DATA].equals(p[DATA]) ){
									remover = true;
									break;
								}	
								j++;
							}
							if ( remover ) {
								while ( j < aux.size() ){
									aux.remove(j);
								}	
							}
							falhou = false;
						}
						// Aqui, adiciona as datas frequências que não foram cadastradas.
						// Ex: Tem aula dia 8, 10 e 12. O sistema estava esperando a frequência do dia 8 e recebeu a do dia 12.
						//     Então adiciona as frequências do dia 8 e 10. 
						while (!ok && i < listagemAulas.size()){
							aula = listagemAulas.get(i);
							Calendar cal = Calendar.getInstance();
							cal.setTime((Date) p[DATA]);
							if (cal.getTime().getTime() > aula.getData().getTime()){
								Object [] o = {p[ID_MAT], p[MAT], p[NOME], aula.getDia(), aula.getMes(), null, 0, false, getNumeroAulas(aula), p[ID_DISCENTE], aula.getData(), p[TRANCADO]};
								aux.add(o);
							} else {
								p[NUM_AULAS] = getNumeroAulas(aula);
								aula.setLancada(true);
								ok = true;
							}
						
							contagemAulasInvalidas++;
							i++;
						}
					}
					
					// Por fim, adiciona a frequência do discente para o dia se for de uma data correta.
					AulaFrequencia aula = listagemAulas.get(i-1 >= listagemAulas.size() ? listagemAulas.size() - 1 : i-1);
					boolean ok = false;
					if (Float.parseFloat(""+p[MES]) == aula.getMes() && Float.parseFloat(""+p[DIA]) == aula.getDia())
						ok = true;
					else {
						i-=contagemAulasInvalidas;
						contagemAulasInvalidas = 0;
					}	

					
					if (p[DIA] != null && ok)
						aux.add(p);
					else {
						aula.setLancada(false);
						falhou = true;
					}	
				}
				
				// Completa as presenças do último aluno.
				if (!aux.isEmpty()){
					// Pega o usuário anterior.
					Object [] f = aux.get(aux.size() - 1);
					while (i < listagemAulas.size()){
						AulaFrequencia aula = listagemAulas.get(i);
						Object [] o = {f[ID_MAT], f[MAT], f[NOME], aula.getDia(), aula.getMes(), null, 0, false, getNumeroAulas(aula), f[ID_DISCENTE], aula.getData(), f[TRANCADO]};
						aux.add(o);
						i++;
					}
				}
				
				listagemPlanilha = aux;
			}
			
		} finally {
			if (dao != null)
				dao.close();
			if (fDao != null)
				fDao.close();
		}
		
		dadosFrequenciaPlanilha = "";
		
		for (Object [] l : listagemPlanilha){
			String aluno = "";
			for (Object o : l)
				aluno += aluno.equals("") ? o : "," + o;
				
			dadosFrequenciaPlanilha += (dadosFrequenciaPlanilha.equals("") ? "" : ";") + aluno;
		}
		
		dadosDatasAulasPlanilha = "";
		
		for (AulaFrequencia a : listagemAulas){
			String data = a.getDia() + "," + a.getMes() + "," + getNumeroAulas(a) + "," + a.getData() + "," + a.isLancada() + "," + a.isFeriado() + "," + a.isAulaCancelada() ;
			dadosDatasAulasPlanilha += (dadosDatasAulasPlanilha.equals("") ? "" : ";") + data;
		}
	
	}
	
	/** 
	 * Método responsável por realizar a contabilização do número de aulas hora por aula.
	 * */
	private Object getNumeroAulas(AulaFrequencia aula) {
		if (aula.getAulasExtra() > 0)
			return aula.getAulas() + aula.getAulasExtra();
		return aula.getAulas();
	}

	/**
	 * Retorna um array de inteiro contendo o dia, mês e quantidade de horários para todas as aulas desta turma.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List <AulaFrequencia> getListagemAulas () throws DAOException{
		if (listagemAulas == null)
			listagemAulas = TurmaUtil.getListagemAulas(turma);
		
		return listagemAulas;
	}

	/**
	 * Retorna uma String com o código HTML necessário
	 * para a exibição dos calendários para lançamento de frequências.
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public List <Map <String, Object>> getCalendarios() throws SegurancaException, DAOException, NegocioException {
		
		if (turma == null) {
			turma = turma();
			if(turma == null){
				throw new NegocioException ("Por favor, acesse esta página através dos links disponíveis na turma virtual.");
			}
		}
		
		if (calendarios == null){
			FrequenciaAlunoDao freqDao = getDAO(FrequenciaAlunoDao.class);
			
			setFeriadosTurma( TurmaUtil.getFeriados(turma) );
			List <Date> diasSemAulas = null;
			
			if ( turma.getTurmaAgrupadora() == null )
				diasSemAulas = TurmaUtil.getDatasCanceladas(turma.getId());
			else 
				diasSemAulas = TurmaUtil.getDatasCanceladas(turma.getTurmaAgrupadora().getId());
			
			turma.setDisciplina(freqDao.refresh(turma.getDisciplina()));
			turma.getDisciplina().setUnidade(freqDao.refresh(turma.getDisciplina().getUnidade()));
			
			CalendarioAcademico calendarioAcademico = getCalendarioVigente(turma);
			
			Set<Date> datasAulas = TurmaUtil.getDatasAulasTruncate(turma, calendarioAcademico);
				
			calendarios = new ArrayList<Map<String,Object>>();
			
			if ( (turma.getHorarios() == null || turma.getHorarios().isEmpty() || turma.getDataInicio() == null) && !turma.isTurmaEnsinoIndividual() ){
				addMensagemErro("Esta Turma não está com horários definidos.");
				redirectJSF(getSubSistema().getLink());
				return calendarios;
			}
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(turma.getDataInicio());
			
			// Para cada Mês, adicionam-se os dias.
			for (Entry <Integer, String> mes : getMesesString().entrySet()) {
				
				Integer anoAula = getAnoAula(mes.getKey());
				
				if (anoAula != null) {
					int [][] calendario = getCalendario(mes.getKey(), anoAula);
					for (int i = 0; i < calendario.length; i++) {
						for (int j = 0; j < calendario[i].length; j++) {
							Map <String, Object> c = new HashMap <String, Object> ();
							
							c.put("nomeMes", mes.getValue());
							c.put("diaDaSemana", j);
							c.put("dia", calendario[i][j]);
							c.put("mes", mes.getKey());
							c.put("ano", anoAula);
							
							Date dataCal = createDate(calendario[i][j], mes.getKey(), anoAula);
							
							// Se é um dia de aula
							if (datasAulas.contains(dataCal) && calendario[i][j] > 0 && !getFeriadosTurma().contains(dataCal) || turma.getHorarios() == null || turma.getHorarios().isEmpty()) {
								// Se o docente já lançou as frequencias para o dia
								if (freqDao.diaTemFrequencia(dataCal, turma))
									c.put("frequenciaLancada", true);
								
								// Se o docente marcou o dia para não ter aula
								if (diasSemAulas != null && !diasSemAulas.isEmpty())
									for ( Date d : diasSemAulas ) 
										if ( dataCal.equals(d) ){
											c.put("semAula", true);
											break;
										}
								
								c.put("exibirLink", true);
								
							// Se é feriado
							} else if (datasAulas.contains(dataCal) && calendario[i][j] > 0 && getFeriadosTurma().contains(dataCal))
								c.put("feriado", true);
							
							calendarios.add(c);
						}
					}
				}
			}
		}
		
		return calendarios;
	}
	
	public void setCalendarios (List <Map <String, Object>> calendarios) {
		this.calendarios = calendarios;
	}

	/** 
	 * Popula os feriados para o ano do calendário vigente,
	 *  município e unidade de federação da unidade associada a turma. 
	 */
	private List<Date> getFeriados() throws DAOException {
		
		Turma turmaMunicipioUF = null;
		turmaMunicipioUF = getGenericDAO().findByPrimaryKey( turma.getId(),
					Turma.class, "id", "disciplina.id", "disciplina.unidade.id",
					"campus.endereco.municipio.id", "campus.endereco.municipio.nome",
					"campus.endereco.municipio.unidadeFederativa.sigla","turmaAgrupadora.id" );
		
		/** Seta o municipio da turma em relação a existência ou não de campus */
		Municipio municipioTurma = turmaMunicipioUF.getCampus() != null ?
				turmaMunicipioUF.getCampus().getEndereco().getMunicipio()
			: turmaMunicipioUF.getDisciplina().getUnidade().getMunicipio();
		
		List <Feriado> feriados = getDAO( FeriadoDao.class, Sistema.COMUM ).
			findFeriadosPorAnoMunicipioEstado( getCalendarioVigente(turma).getAno(), 
					municipioTurma.getNome(), 
					municipioTurma.getUnidadeFederativa().getSigla() );

		/** Popula a lista de datas de feriado */
		List<Date> listaDataFeriado = new ArrayList<Date>();
		for ( Feriado feriado : feriados ) {
			listaDataFeriado.add( feriado.getDataFeriado() );
		}
		return listaDataFeriado;
	}

	/**
	 * Redireciona para a página de cadastro de tópico de aula, afim de cadastrar uma aula cancelada.
	 * Método chamado pela seguinte JSP: /ava/FrequenciaAluno/form.jsp
	 * @return
	 */
	public String cancelarAula () {

			addMensagemWarning("Para cancelar a aula marque o campo \"Cancelar Aula\" e cadastre o tópico de aula. Após a aula ser cancelada "+
					"não será nescessário o lançamento da frequência e os discentes não poderão notificar falta.");
		
			TopicoAulaMBean topBean = getMBean("topicoAula");	
			String res = topBean.novo();
			topBean.getObject().setAulaCancelada(true);
			topBean.getObject().setDescricao("Não Haverá Aula");
			topBean.getObject().setData(dataSelecionada);
			topBean.getObject().setFim(dataSelecionada);
			
			calendarios = null;
			return res;
	}

	/**
	 * Verifica se a aula do dia selecionado foi cancelada.
	 * Método chamado pela seguinte JSP: /ava/FrequenciaAluno/form.jsp
	 * @throws DAOException
	 * @return
	 */
	public boolean isSemAula () throws DAOException{

		List<Date> diasSemAulas = null;
		
		if ( turma.getTurmaAgrupadora() == null )
			diasSemAulas = TurmaUtil.getDatasCanceladas(turma.getId());
		else
			diasSemAulas = TurmaUtil.getDatasCanceladas(turma.getTurmaAgrupadora().getId());
		
		if ( dataSelecionada != null && diasSemAulas != null && !diasSemAulas.isEmpty() )
			for ( Date d : diasSemAulas ) {
				if ( dataSelecionada.compareTo(d) == 0 )
					return true;
			}
		return false;

	}
	/**
	 * Retorna a data selecionada.
	 * @return the dataSelecionada
	 */
	public Date getDataSelecionada() {
		return dataSelecionada;
	}

	/**
	 * Seta a data selecionada.
	 * @param dataSelecionada the dataSelecionada to set
	 */
	public void setDataSelecionada(Date dataSelecionada) {
		this.dataSelecionada = dataSelecionada;
	}

	/**
	 * Remove todas as frequências lançadas em um determinado dia.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/FrequenciaAluno/form.jsp
	 */
	@Override
	public String remover() {
		
		if (turma.isAberta()){
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				String data = sdf.format(dataSelecionada);
				registrarAcao(data, EntidadeRegistroAva.FREQUENCIA, AcaoAva.INICIAR_REMOCAO, turma().getId() );
				prepareMovimento(SigaaListaComando.REMOVER_FREQUENCIA);
				FrequenciaMov mov = new FrequenciaMov(frequencias);
				mov.setCodMovimento(SigaaListaComando.REMOVER_FREQUENCIA);
				execute(mov, getCurrentRequest());
				registrarAcao(data, EntidadeRegistroAva.FREQUENCIA, AcaoAva.REMOVER, turma().getId() );
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				frequencias = null;
				calendarios = null;
				permanecerDataSelecionada = true;
			} catch (Exception e) {
				notifyError(e);
				e.printStackTrace();
			}
		} else
			addMensagemErro("Esta turma já está consolidada e não pode ser alterada.");

		return null;
	}

	/**
	 * Retorna o número máximo de faltas permitidos na disciplina antes do discente reprovar por faltas.
	 * Método chamado pela seguinte JSP: /ava/FrequenciaAluno/form.jsp
	 * @throws DAOException
	 * @throws NegocioException
	 * @return
	 */
	public int getMaximoFaltas() throws DAOException, NegocioException {
		Turma turma = this.turma;
		if (turma == null) turma = turma();
		ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(turma);
		if (param == null) throw new NegocioException("Os Parâmetros Acadêmicos da sua unidade não foram definidos. Por favor, contate o suporte do sistema.");
		return turma.getDisciplina().getMaximoFaltasPermitido(param.getFrequenciaMinima(), param.getMinutosAulaRegular());
	}

	/**
	 * Retorna o calendário acadêmico vigente para
	 * a turma passada como parâmetro.
	 * 
	 * @param turma
	 * @return
	 */
	public CalendarioAcademico getCalendarioVigente(Turma turma) {
		try {
			CalendarioAcademico calendarioVigente = CalendarioAcademicoHelper.getCalendario(turma);
			if (turma.isLato())
				calendarioVigente = CalendarioAcademicoHelper.getCalendario(ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalLato());
			return calendarioVigente;
		} catch (DAOException e) {
			return null;
		}
	}

	/**
	 * Lista as frequências cadastradas para a data selecionada
	 * em um calendário.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/menu.jsp
	 */
	@Override
	public List<FrequenciaAluno> lista() {
		if ( permanecerDataSelecionada || (getParameterInt("dia") != null && (frequencias == null ||  diaNovo != diaAntigo))) {

			TurmaDao turmaDao = null;
			TopicoAulaDao topDao= null;
			
			if (!permanecerDataSelecionada)
				dataSelecionada = createDate(getParameterInt("dia"), getParameterInt("mes"), getParameterInt("ano"));
			
			try {
				
				maxFaltas = FrequenciaHelper.getMaxFaltasData(turma, dataSelecionada);
			
				
				if (maxFaltas == 0)
					maxFaltas = 10;
			

				turmaDao = getDAO(TurmaDao.class);
				frequencias = turmaDao.findFrequenciasByTurma(turma, dataSelecionada);
				Collection<MatriculaComponente> matriculas = turmaDao.findMatriculasAConsolidar(turma);
				
				if ( frequencias != null ) {
					if ( matriculas != null ) {
						for ( MatriculaComponente m : matriculas ) {
							if (!frequencias.contains(new FrequenciaAluno(m.getDiscente().getDiscente(), dataSelecionada, turma))) {
								FrequenciaAluno freq = new FrequenciaAluno();
								freq.setData(dataSelecionada);
								Pessoa p = m.getDiscente().getPessoa();
								freq.setDiscente(m.getDiscente().getDiscente());
								freq.getDiscente().setPessoa(p);
								freq.setTurma(turma);
								freq.setFaltas((short) 0);
								freq.setHorarios(maxFaltas);
								frequencias.add(freq);
							}
						}
					}
		
					Collections.sort(frequencias, new Comparator<FrequenciaAluno>() {
						public int compare(FrequenciaAluno o1, FrequenciaAluno o2) {
							return o1.getDiscente().getPessoa().getNomeAscii().compareToIgnoreCase(o2.getDiscente().getPessoa().getNomeAscii());
						}
					});
				}
					
				permanecerDataSelecionada = false;
				diaAntigo = diaNovo;
				
				try {
					prepareMovimento(SigaaListaComando.LANCAR_FREQUENCIA);
					prepareMovimento(SigaaListaComando.REMOVER_FREQUENCIA);
				} catch (ArqException e) {
					throw new TurmaVirtualException(e);
				}
			} catch (DAOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if ( topDao != null )
					topDao.close();
				if (turmaDao != null)
					turmaDao.close();
			}
		} /*else
			maxFaltas = 10;*/

		return frequencias;
	}
	
	/**
	 * Verifica se já foi lançada a frequência da data selecionada.
	 * 
	 * Método chamado pela seguinte JSP: /ava/FrequenciaAluno/form.jsp
	 * @throws DAOException 
	 */
	public boolean isDiaTemFrequencia () throws DAOException {
		FrequenciaAlunoDao fDao = null;
		try{
			fDao = getDAO( FrequenciaAlunoDao.class );
			return fDao.diaTemFrequencia(dataSelecionada, turma);
		}finally{
			if (fDao != null)
				fDao.close();
		}
	}
	
	public boolean isTurmaChamadaBiometrica() {
		return turmaChamadaBiometrica;
	}

	public void setTurmaChamadaBiometrica(boolean turmaChamadaBiometrica) {
		this.turmaChamadaBiometrica = turmaChamadaBiometrica;
	}

	public Map<String, List<Short>> getMapa() {
		return mapa;
	}

	public void setMapa(Map<String, List<Short>> mapa) {
		this.mapa = mapa;
	}

	public Turma getTurma() {
		return turma;
	}

	public boolean isCadastrado() {
		return cadastrado;
	}

	public void setCadastrado(boolean cadastrado) {
		this.cadastrado = cadastrado;
	}

	public ParametrosGestoraAcademica getParametrosGestora() {
		return parametrosGestora;
	}

	public void setParametrosGestora(ParametrosGestoraAcademica parametrosGestora) {
		this.parametrosGestora = parametrosGestora;
	}

	public String getDadosFrequenciaPlanilha() {
		return dadosFrequenciaPlanilha;
	}

	public void setDadosFrequenciaPlanilha(String dadosFrequenciaPlanilha) {
		this.dadosFrequenciaPlanilha = dadosFrequenciaPlanilha;
	}

	public String getDadosDatasAulasPlanilha() {
		return dadosDatasAulasPlanilha;
	}

	public void setDadosDatasAulasPlanilha(String dadosDatasAulasPlanilha) {
		this.dadosDatasAulasPlanilha = dadosDatasAulasPlanilha;
	}

	public boolean isLancarEmPlanilha() {
	    return lancarEmPlanilha;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}
	
	public int getTempoSalvamentoPlanilha() {
		return tempoSalvamentoPlanilha;
	}

	public void setTempoSalvamentoPlanilha(int tempoSalvamentoPlanilha) {
		this.tempoSalvamentoPlanilha = tempoSalvamentoPlanilha;
	}

}