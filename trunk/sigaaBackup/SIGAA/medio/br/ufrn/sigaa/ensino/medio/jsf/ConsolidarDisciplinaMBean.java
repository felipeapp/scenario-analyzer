/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 27/07/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.dominio.PermissaoAva;
import br.ufrn.sigaa.ava.jsf.ConfiguracoesAvaMBean;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dao.RegraNotaDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.RegraNota;
import br.ufrn.sigaa.ensino.dominio.RetificacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.jsf.ConsolidacaoMBean;
import br.ufrn.sigaa.ensino.medio.dao.CalendarioRegraDao;
import br.ufrn.sigaa.ensino.medio.dao.NotaSerieDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.CalendarioRegra;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.NotaDisciplina;
import br.ufrn.sigaa.ensino.medio.dominio.NotaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;
import br.ufrn.sigaa.ensino.medio.negocio.ConsolidacaoMedioValidator;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.TurmaHelper;
import br.ufrn.sigaa.ensino.negocio.consolidacao.ConsolidacaoMedio;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;
import br.ufrn.sigaa.ensino.stricto.dominio.ConceitoNota;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.Docente;

/**
 * MBean para tratar o caso de uso de consolidar uma disciplina de ensino médio.
 * 
 * @author Rafael Gomes
 *
 */
@Component("consolidarDisciplinaMBean") @Scope("request")
public class ConsolidarDisciplinaMBean extends ConsolidacaoMBean {

	/** Link para a página do lançamento de notas. */
	public static final String JSP_DETALHES_TURMA = "/detalhesTurma.jsp";
	/** Link para a página para a confirmação da consolidação de turma. */
	public static final String JSP_CONFIRMAR = "/confirmar.jsp";
	/** Link para a página para a seleção da turma de disciplina a ser consolidada. */
	public static final String JSP_SELECIONA_DISCIPLINA = "/listaDisciplina.jsp";
	/** Link para a página para a seleção de turma */
	public static final String JSP_SELECIONA_TURMA = "/listaTurma.jsp";
	/** Link para a página para a listagem de turma */
	public static final String JSP_LISTA_TURMA = "/medio/turmaSerie/lista.jsp";
	/** Link para a página para a listagem de turmas do docente após a consolidação */
	public static final String JSP_SELECIONAR_TURMA_POS_CONSOLIDACAO = "/selecionaTurma.jsp";
	
	/** Filtro da busca pelo código */
	private boolean filtroNumero;
	/** Filtro da busca pelo Curso */
	private boolean filtroCurso;
	/** Filtro da busca pelo Ano */
	private boolean filtroAno;
	/** Filtro da busca pela Turma */
	private boolean filtroTurma;
	/** Ano informado na busca de série */
	private Integer ano;
	/** Turma informada na busca de série */
	private String buscaTurma;	
	/** Série informada na busca */
	private Serie serie;
	/** TurmaSerie selecionada */
	private TurmaSerie turmaSerie;
	/** TurmaSerieAno selecionada */
	private TurmaSerieAno turmaSerieAno;
	/** Collection que irá armazenar a listagem das Series. */
	private List<TurmaSerie> listaSeries= new ArrayList<TurmaSerie>(); 	
	/** Todas as matrículas do discente escolhido */
	private Collection<MatriculaComponente> matriculas;
	/** Notas de cada disciplina */
	private List<NotaDisciplina> notasDisciplina;	
	/** Notas de cada disciplina para consolidação parcial.*/
	private List<NotaDisciplina> notasDisciplinaParcial;	
	
	/** Indica se a consolidação é individual. */
	private boolean individual;
	/** Senha usada na autenticação do usuário. */
	private String senha;
	/** Objeto que guarda as informações sobre os parâmetros da unidade gestora da turma. */
	private ParametrosGestoraAcademica param;
	/** Objeto que guarda as informações das configurações da turma selecionada. */
	private ConfiguracoesAva config;
	/** Modelo de Configurações das notas */
	private List<RegraNota> regraNotas;
	/** Indica se a consolidação é parcial */
	private boolean parcial;
	/** Variáveis usadas na reconsolidação de turma. */
	/** Coleção que mantém as notas antigas da turma. */
	private Collection<NotaUnidade> notasIniciaisTurma;
	/** Coleção de retificações das matrículas da turma que está sendo reconsolidada. */
	private Collection<RetificacaoMatricula> retificacoesReconsolidacao;
	/** Lista das regras de nota do curso.*/
	private List<RegraNota> regras;
	/** estratégia de consolidação atual */
	private EstrategiaConsolidacao estrategia;
	/** String utilizada para armazenar a página anterior de acesso a tela de consolidação. */
	private String pageBack;
	
	/** 
	 * Inicializa o bean, método chamado pelo construtor.
	 * Método não invocado por JSPs,
	 */
	public void initObj() {
		turma = new Turma();
		turma.setDisciplina(new ComponenteCurricular());

		serie = new Serie();
		serie.setCursoMedio(new CursoMedio());
		
		conceitos = new ArrayList<ConceitoNota>();
		individual = false;

		numeroUnidades = 0;
		metodoAvaliacao = 0;
		fromPortalTurma = false;
	}
	
	/**
	 * Diretório que se encontra as view's
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não invocado por JSP</li>
	 * </ul>
	 */
	@Override
	public String getDirBase() {
		return "/medio/consolidacao";
	}
	
	/**
	 * Método responsável pela busca de séries de ensino Médio.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/serie/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws ArqException {
		
		if (!filtroAno && !filtroNumero && !filtroTurma && !filtroCurso)
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		
		if (filtroAno && (ano == null || ano < 1900)) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		
		if (filtroNumero && isEmpty(serie.getNumero())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Número de Série");
		
		if (filtroTurma && isEmpty(buscaTurma)) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Turma");
		
		if (filtroCurso && isEmpty(serie.getCursoMedio().getId())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
		
		if (hasErrors())
			return null;
	
		TurmaSerieDao dao = getDAO(TurmaSerieDao.class);
		try {
			listaSeries = dao.findBySerie(
					(filtroCurso ? serie.getCursoMedio() : null),
					(filtroAno ? ano : null), 
					(filtroNumero ? serie.getNumero() : null),
					null, 
					(filtroTurma ? buscaTurma : null));
			if (listaSeries.isEmpty()) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(getDirBase() + JSP_SELECIONA_TURMA);
	}
	
	/**
	 * Direciona o usuário para a tela de seleção de discentes.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/consolidacao/listaTurma.jsp.</li>
	 * </ul>
	 */
	public String telaSelecaoTurma() {
		return forward(getDirBase() + JSP_SELECIONA_TURMA);
	}
	
	/**
	 * Direciona o usuário para a tela de seleção de discentes.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/consolidacao/listaTurma.jsp.</li>
	 * </ul>
	 */
	public String telaSelecaoDisciplina() {
		return forward(getDirBase() + JSP_SELECIONA_DISCIPLINA);
	}
	
	/**
	 * Seleciona a Turma e lista os Discentes para alteração de Situação.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/medio/serie/lista.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarTurma() throws ArqException{
		int idTurma = getParameterInt("id", 0);
		
		if (idTurma <= 0 && isEmpty(turmaSerie)){			
			addMensagemWarning("Turma não selecionada!");
			return null;
		} 
		
		// recupera os parâmetros acadêmicos para saber a média mínima  
		param = getParametrosAcademicos();
		
		TurmaSerieDao dao = getDAO(TurmaSerieDao.class);	
		try {
			if (turmaSerie != null && (turmaSerie.getId() == idTurma) && (idTurma <= 0)){
				idTurma = turmaSerie.getId();
			}	
			turmaSerie = dao.findAllDadosByID(idTurma,false);
			
			if (turmaSerie == null)
				return cancelar();
			
			if (turmaSerie.getDisciplinas() == null || turmaSerie.getDisciplinas().isEmpty()) {
				addMensagemErro("Nenhuma disciplina localizada para a Turma selecionada!");
				return null;
			}
			Collections.sort((List<TurmaSerieAno>) turmaSerie.getDisciplinas());
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_MEDIO,SigaaPapeis.COORDENADOR_MEDIO))
			return telaSelecaoDisciplina();
		
		return forward(getDirBase() + JSP_SELECIONAR_TURMA_POS_CONSOLIDACAO);
	}
	
	/**
	 * Seleciona a Turma e lista os Discentes para alteração de Situação.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/medio/serie/lista.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarDisciplina() throws ArqException{
		int idTurmaSerieAno = 0;
		
		if ( !fromPortalTurma ) {
			idTurmaSerieAno = getParameterInt("id", 0);
			pageBack = getParameter("pageBack");
		}	
	
		TurmaSerieDao dao = getDAO(TurmaSerieDao.class);	
		RegraNotaDao regraNotaDao = getDAO(RegraNotaDao.class); 
		TurmaMedioDao turmaDao = getDAO(TurmaMedioDao.class);
		
		try {
			if (idTurmaSerieAno <= 0 && isEmpty(turmaSerieAno)){			
				addMensagemWarning("Disciplina não selecionada!");
				return null;
			}
			
			if (isEmpty(turmaSerieAno) || (turmaSerieAno.getId() != idTurmaSerieAno) && (idTurmaSerieAno > 0)){
				turmaSerieAno = dao.findByPrimaryKey(idTurmaSerieAno, TurmaSerieAno.class);
			}	
			turma = turmaSerieAno.getTurma();
			
			turmaSerie = turmaSerieAno.getTurmaSerie();
			getCurrentRequest().setAttribute("idTurmaSerie", turmaSerie.getId());
			
			regras = regraNotaDao.findByCurso(turmaSerie.getSerie().getCursoMedio());
			if (ValidatorUtil.isEmpty(regras)){
				addMensagemErro("Não foram definidas as regras de consolidação.");
				return null;
			}	
			setRegraNotas(regras);
			
			matriculas = turmaDao.findMatriculasAConsolidar(turma);
			if ( ValidatorUtil.isEmpty(matriculas) ) {
				addMensagemErro("Nenhum discente com notas a serem consolidadas na disciplina selecionada!");
				return null;
			}
			
			param = getParametrosAcademicos();			
			EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
			estrategia = factory.getEstrategia(turma, param);
			
			if (estrategia == null){
				addMensagemErro("Estratégia de consolidação não definida!");
				return null;
			}
			
			prepararDisciplina(turma);
			turma.setMatriculasDisciplina(matriculas);
			setNotasDisciplina(agrupaNotasDisciplina(matriculas, regras));
			
			calcularNotas();
			calcularFaltas();
			
		} finally {
			if (dao != null) dao.close();
			if (regraNotaDao != null) regraNotaDao.close();
			if (turmaDao != null) turmaDao.close();
		}
		return forward(getDirBase() + JSP_DETALHES_TURMA);
	}
	
	/**
	 * Método responsável por agrupar as notas do discente baseando na regras das notas.
	 * 
	 * @param matriculas
	 * @param regras
	 * @return
	 * @throws DAOException
	 */
	private List<NotaDisciplina> agrupaNotasDisciplina(
			Collection<MatriculaComponente> matriculas,	List<RegraNota> regras) throws DAOException {
		NotaSerieDao notasDao = getDAO(NotaSerieDao.class); 
		FrequenciaAlunoDao fDao = getDAO(FrequenciaAlunoDao.class);
		CalendarioRegraDao calRegraDao = getDAO(CalendarioRegraDao.class);

		List<NotaDisciplina> lista = new ArrayList<NotaDisciplina>();
		Map<Integer, Set<FrequenciaAluno>> frequencias = fDao.findMatriculaFrequenciasByTurma(turma);
		CalendarioAcademico calendarioVigente = CalendarioAcademicoHelper.getCalendario(turma.getDisciplina().getCurso());
		Collection<CalendarioRegra> calRegras = calRegraDao.findByCalendario(calendarioVigente);

		//Agrupa as notas para cada disciplina 
		for (MatriculaComponente mat : matriculas){
					
			List<NotaSerie> notas = notasDao.findNotasByDisciplina(new DiscenteMedio(mat.getDiscente().getId()),mat);
			Set<FrequenciaAluno> freq = frequencias.get(mat.getDiscente().getId());
			
			NotaDisciplina notaDisc = new NotaDisciplina();
			notaDisc.setMatricula(mat);
			notaDisc.setNotas(new ArrayList<NotaSerie>());
			
			Iterator<RegraNota> it = regras.iterator(); 
			boolean insereNotaSerie = true;
			while (it.hasNext()) {
				RegraNota rn = it.next();
				for (NotaSerie ns : notas) {
					if ( (ns.getRegraNota().getId() == rn.getId()) ){
						ns.setRegraNota(rn);
						ns.getNotaUnidade().setFaltasCalc(getFrequenciaBimestre(rn, calRegras, freq));
						insereNotaSerie = false;
					 	break;
					}	
				}
				if (insereNotaSerie) {
					NotaSerie novaNotaSerie = new NotaSerie();
					novaNotaSerie.setRegraNota(rn);
					novaNotaSerie.setNotaUnidade(new NotaUnidade());
					novaNotaSerie.getNotaUnidade().setMatricula(mat);
					novaNotaSerie.getNotaUnidade().setFaltasCalc(getFrequenciaBimestre(rn, calRegras, freq));
					notas.add(novaNotaSerie);
				}
				insereNotaSerie = true;
			}
			
			for (NotaSerie notaSerie : notas){
				if (mat.equals(notaSerie.getNotaUnidade().getMatricula())){
					notaDisc.getNotas().add(notaSerie);
				}
			}
	
			validarNotas(notas);
			mat.setMediaFinal(ConsolidacaoMedio.calculaMediaNotaSerie(notas));
			notaDisc.setMediaParcial(ConsolidacaoMedio.calculaMediaNotaSerie(notas));
			notaDisc.getMatricula().setNotaDisciplina(notaDisc);
			lista.add(notaDisc);
		}
		return lista;
	}
	
	/**
	 * Método responsável por validar o tipo da nota unidade, verificando se a possibilidade de preencher
	 *  o valor de recuperação para o aluno, mediante o valor da nota do mesmo.
	 * 
	 * @param notasSerie
	 */
	private void validarNotas(List<NotaSerie> notasSerie){
		for (NotaSerie notaSerie : notasSerie) {
			if( notaSerie.getRegraNota().isRecuperacao() ){
				notaSerie.setPodeEditar(false);
				String[] refRec = notaSerie.getRegraNota().getRefRec().trim().split(",");
				List<Integer> idsRegraRefRec = new ArrayList<Integer>();
				for (int i = 0; i < refRec.length; i++) {
					idsRegraRefRec.add(Integer.parseInt(refRec[i]));
				}
				
				// O cálculo para saber se o semestre vai necessitar
				// recuperação é ( n1 + n2 ) / 2 < 7
				
				Double media = 0D;
				for (NotaSerie notaRefRec : notasSerie)
					if (idsRegraRefRec.contains(new Integer(notaRefRec.getRegraNota().getId())) && notaRefRec.getNotaUnidade().getNota() != null)
						media += notaRefRec.getNotaUnidade().getNota();
				
				if (media / idsRegraRefRec.size() < param.getMediaMinimaPassarPorMedia())
					notaSerie.setPodeEditar(true);
			}	
		}
	}
	
	
	/**
	 * Vai para a tela de confirmação da consolidação parcial da turma.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * 		<li>sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String confirmarParcial() throws ArqException {
		parcial = true;
		MatriculaComponenteDao dao = null;
		
		try {
			if (!checkOperacaoAtiva(1)) cancelar();
			checkTurmaResponsaveisRole(turma, false);
			salvarNotas();

			ConsolidacaoMedioValidator.validar(dao, getNotasDisciplina(), turma, null, param, metodoAvaliacao, SigaaListaComando.CONSOLIDAR_DISCIPLINA_MEDIO, isUserInRole(SigaaPapeis.GESTOR_MEDIO), parcial, config, getUsuarioLogado());

			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());	
			return null;		
		} finally {
			if (dao != null)
				dao.close();
		}
		return forward(getDirBase() + JSP_CONFIRMAR);
	}
	
	/**
	 * Realiza a consolidação parcial da turma, consolidando as matrículas dos alunos aprovados por média.
	 * e deixando a turma em aberto caso hajam alunos em recuperação. <br/><br/>
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/medio/consolidacao/confirmar.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String parcial() throws ArqException {
		
		if (!UserAutenticacao.autenticaUsuario(getCurrentRequest(), getUsuarioLogado().getLogin(), senha)) {
			addMensagem(MensagensTurmaVirtual.SENHA_INVALIDA_CONSOLIDAR_TURMA);
			return null;
		}
		
		if (!checkOperacaoAtiva(1)) return cancelar();
		
		prepareMovimento(SigaaListaComando.CONSOLIDAR_DISCIPLINA_MEDIO);
		setOperacaoAtiva(SigaaListaComando.CONSOLIDAR_DISCIPLINA_MEDIO.getId());
		
		TurmaMov mov = new TurmaMov();
		mov.setTurma(turma);
		mov.setConsolidacaoIndividual(individual);
		mov.setMetodoAvaliacao(metodoAvaliacao);
		mov.setConfig(config);
		mov.setCodMovimento(SigaaListaComando.CONSOLIDAR_DISCIPLINA_MEDIO);
		mov.setConsolidacaoPartial(parcial);
		mov.setObjAuxiliar(getNotasDisciplina());
		
		try {
			execute(mov, getCurrentRequest());
		
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			removeOperacaoAtiva();
			
			String url = getDirBase() + "/fim.jsf?idTurmaSerie=" + turmaSerie.getId();
			initObj();
			resetBean();
			redirectJSF(url);
			return null;
		
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
	}
	
	/** Método responsável pelo controle do botão voltar da tela de consolidação de disciplina.
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * 		<li>sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @throws ArqException */
	public String voltar() throws ArqException{
		if (pageBack == null)
			return cancelar();
		
		if (pageBack.equals("listarTurma")){
			TurmaSerieMBean tsBean = getMBean("turmaSerie");
			return tsBean.listarTurmasAnual(turmaSerie, JSP_LISTA_TURMA);
		}else if (pageBack.equals("consolidar"))	
			return selecionarTurma();
		else if (pageBack.equals("portalTurma"))	
			return cancelar();
		else 
			return null;
	}
	
	
	/**
	 * Salva as notas cadastradas para a turma sem efetivamente
	 * consolidá-la e as oculta para os alunos.<br/><br/>
	 * 
	 * Método chamado na seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception 
	 */
	public String iniciarConfiguracoesAva() throws Exception {

		ConfiguracoesAvaMBean cBean = getMBean("configuracoesAva");
		return cBean.configurar(turma);
	}
	
	/**
	 * Iniciar fluxo geral para consolidação de disciplinas <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/medio/menus/turma.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String iniciar() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO);
		initObj();
		// recupera os parâmetros acadêmicos para saber a média mínima  
		param = getParametrosAcademicos();
		return forward(getDirBase() + JSP_SELECIONA_TURMA);
	}
	
	
	/**
	 * Carrega ConfiguracoesAva e os parâmetros da gestora acadêmica
	 * 
	 * JSP: Não invocado por jsp
	 * @throws DAOException
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public void prepararDisciplina(Turma turma) throws DAOException, SegurancaException, ArqException {
		setOperacaoAtiva(1, "Nenhuma consolidação ativa. Por favor, reinicie o processo.");
		
		if (turma == null || turma.getId() == 0) {
			addMensagem(MensagensTurmaVirtual.NENHUMA_TURMA_SELECIONADA);
			return;
		}
		
		// busca as informações referente a turma na base de dados
		TurmaMedioDao dao = getDAO(TurmaMedioDao.class);
		MatriculaComponenteDao daoMatricula = getDAO(MatriculaComponenteDao.class);
		turma.getDisciplina().getId();
		if (turma.getDisciplina().getPrograma() != null)
			turma.getDisciplina().getPrograma().getId();
		turma.getDescricaoSemDocente();
		if (turma.getDisciplina().getUnidade().getGestoraAcademica() != null)
			turma.getDisciplina().getUnidade().getGestoraAcademica().getId();

		config = getDAO(TurmaVirtualDao.class).findConfiguracoes(turma);
		
		// recupera os parâmetros acadêmicos para saber a média mínima  
		param = getParametrosAcademicos();
		if (param == null) {
			addMensagem(MensagensTurmaVirtual.PARAMEROS_PARA_NIVEL_NAO_CONFIGURADOS, turma.getDisciplina().getNivelDesc());
			return;
		}

		// Busca alunos matriculados
		Collection<MatriculaComponente> matriculas = dao.findMatriculasAConsolidar(turma);
		if (isEmpty(matriculas)) {
			addMensagemErro("Esta turma não possui matrículas a serem consolidadas.");
			return;
		}
		
		String[] pesosAvaliacoes = param.getArrayPesosAvaliacoes();
		String[] pesoMediaPesoRec = param.getArrayPesosMediaRec();

		if (param.getMetodoAvaliacao() == MetodoAvaliacao.NOTA && (pesosAvaliacoes == null || pesosAvaliacoes.length == 0)) {
			addMensagem(MensagensTurmaVirtual.UNIDADE_SEM_PESOS_AVALIACOES);
			return;
		}
		
		metodoAvaliacao = param.getMetodoAvaliacao();
		for (MatriculaComponente matricula : turma.getMatriculasDisciplina()) {			    
			matricula.setPesoMedia(new Integer(pesoMediaPesoRec[0].trim()));
			matricula.setPesoRecuperacao(new Integer(pesoMediaPesoRec[1].trim()));
			matricula.setEstrategia(estrategia);
		}
		
		retificacoesReconsolidacao = new ArrayList<RetificacaoMatricula>();
		Boolean reconsolidacaoTurma = TurmaHelper.isReconsolidacaoTurma(turma);			
		if(reconsolidacaoTurma && !matriculas.isEmpty()) {				
			turma.setMatriculasDisciplina(matriculas);
			Map<Integer,Integer> mapaMatriculaSituacaoAnteriorMaisRecente = daoMatricula.getMapaSituacaoAnteriorMaisRecenteMacricula(turma.getMatriculasDisciplina());
			for (MatriculaComponente matricula : turma.getMatriculasDisciplina()) {
				if( mapaMatriculaSituacaoAnteriorMaisRecente.get(matricula.getId()) != null ){
					RetificacaoMatricula retificacao = new RetificacaoMatricula();
					retificacao.setMediaFinalAntiga(matricula.getMediaFinal() != null ? matricula.getMediaFinal().doubleValue() : null);
					retificacao.setNumeroFaltasAntigo(matricula.getNumeroFaltas());
					retificacao.setConceitoAntigo(null);					 
					retificacao.setSituacaoAntiga(new SituacaoMatricula (mapaMatriculaSituacaoAnteriorMaisRecente.get(matricula.getId())));
					retificacao.setMatriculaAlterada(matricula);
					retificacoesReconsolidacao.add(retificacao);		
				}	
			}
		}
	}
	
	/**
	 * Vai para a tela de confirmação da consolidação da turma virtual da disciplina.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @return
	 * @throws ArqException 
	 * @throws s 
	 */
	public String confirmar() throws ArqException{
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		parcial = false;
		try {
			if (!checkOperacaoAtiva(1)) cancelar();
			checkTurmaResponsaveisRole(turma, false);
			salvarNotas();
			ConsolidacaoMedioValidator.validar(dao, getNotasDisciplina(), turma, null, param, metodoAvaliacao, SigaaListaComando.CONSOLIDAR_DISCIPLINA_MEDIO, isUserInRole(SigaaPapeis.GESTOR_MEDIO), parcial, config, getUsuarioLogado());
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());	
			return null;		
		} finally {
			if (dao != null)
				dao.close();
		}
		
		
		return forward(getDirBase()+ JSP_CONFIRMAR);
	}
	
	/**
	 * Calcula a nota da disciplina digitada
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void calcularNota(ActionEvent e) throws DAOException {
		
		NotaDisciplina nd = (NotaDisciplina) e.getComponent().getAttributes().get("matricula");
		
		if (nd != null){
			
			boolean ficouEmRecuperacao = false;
			nd.getMatricula().setEstrategia(estrategia);
			
			validarNotas(nd.getNotas());
			
			nd.setMediaParcial(ConsolidacaoMedio.calculaMediaNotaSerie(nd.getNotas()));
			
			nd.setEmRecuperacao(isEmRecuperacao(nd.getMediaParcial()));
			
			ficouEmRecuperacao = isEmRecuperacao(nd.getMediaParcial()) && !nd.getMatricula().isReprovadoFalta(param.getFrequenciaMinima(),param.getMinutosAulaRegular());
			
			nd.getMatricula().setMediaFinal(
				ConsolidacaoMedio.calculaMediaFinalNotaSerie(
					nd.getMatricula(), nd.getMediaParcial(), 
					param.getFrequenciaMinima(), param.getMediaMinimaPassarPorMedia(), ficouEmRecuperacao));	
		}
	}
	
	/**
	 * Calcula as faltas da disciplina digitada
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @param e
	 * @throws ArqException
	 */
	public void calcularFalta(ActionEvent e) throws ArqException {
		
		NotaDisciplina nd = (NotaDisciplina) e.getComponent().getAttributes().get("matricula");
		
		if (nd != null){
			nd.getMatricula().setEstrategia(estrategia);
			nd.getMatricula().setNumeroFaltas(ConsolidacaoMedio.calculaFaltasSerie(nd.getNotas()));
		}
	}	
	
	/**
	 * Método responsável pelo calculo das notas do discente na disciplina.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @return
	 * @throws ArqException 
	 */
	public String calcularNotas() throws ArqException {

		getCurrentRequest().setAttribute("idTurmaSerie", turmaSerie.getId());
		prepararDisciplina(turma);
		boolean ficouEmRecuperacao;
		for (NotaDisciplina nd : getNotasDisciplina()) {
			nd.getMatricula().setEstrategia(estrategia);
			validarNotas(nd.getNotas());
			nd.setMediaParcial(ConsolidacaoMedio.calculaMediaNotaSerie(nd.getNotas()) );
			nd.setEmRecuperacao(isEmRecuperacao(nd.getMediaParcial()));
			ficouEmRecuperacao = isEmRecuperacao(nd.getMediaParcial()) && !nd.getMatricula().isReprovadoFalta(param.getFrequenciaMinima(),param.getMinutosAulaRegular());
			nd.getMatricula().setMediaFinal(
				ConsolidacaoMedio.calculaMediaFinalNotaSerie(
					nd.getMatricula(), nd.getMediaParcial(), param.getFrequenciaMinima(), param.getMediaMinimaPassarPorMedia(), ficouEmRecuperacao));
			for (NotaSerie notaSerie : nd.getNotas()) {
				if (notaSerie.getNotaUnidade().getMatricula().equals(nd.getMatricula()) && !nd.getMatricula().getNotas().contains(notaSerie.getNotaUnidade()) )
					nd.getMatricula().getNotas().add(notaSerie.getNotaUnidade());
			}
			for (MatriculaComponente matricula : turma.getMatriculasDisciplina()) {
				if ( nd.getMatricula().equals(matricula) ){
					matricula.setNotas(nd.getMatricula().getNotas());
					matricula.setRecuperacao(nd.getMatricula().getRecuperacao());
					matricula.setNumeroFaltas(nd.getMatricula().getNumeroFaltas());
					break;
				}
			}
		}
		setNotasDisciplina(getNotasDisciplina());
		return null;
	}
	
	/**
	 * Método responsável pelo calculo de faltas do discente na disciplina.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li> sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @return
	 * @throws ArqException 
	 */
	private String calcularFaltas() throws ArqException {
		getCurrentRequest().setAttribute("idTurmaSerie", turmaSerie.getId());
		prepararDisciplina(turma);
		for (NotaDisciplina nd : getNotasDisciplina()) {
			nd.getMatricula().setEstrategia(estrategia);
			nd.getMatricula().setNumeroFaltas(ConsolidacaoMedio.calculaFaltasSerie(nd.getNotas()));
		}
		setNotasDisciplina(getNotasDisciplina());
		return null;
	}
	
	
	
	/**
	 * Método utilizado para informar se o discente encontra-se em recuperação, mediante a nota parcial. 
	 * 
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * 		<li>sigaa.war/medio/consolidacao/confirmar.jsp</li>
	 * </ul>
	 * 
	 * @param matricula
	 * @return
	 */
	public boolean isEmRecuperacao(Double mediaParcial) {
		return mediaParcial != null 
				&& mediaParcial < param.getMediaMinimaPassarPorMedia();
	}
	
	/**
	 * Consolida a turma.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * 		<li>sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws IOException
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String consolidar() throws ArqException {
		prepareMovimento(SigaaListaComando.CONSOLIDAR_DISCIPLINA_MEDIO);
		setOperacaoAtiva(SigaaListaComando.CONSOLIDAR_DISCIPLINA_MEDIO.getId());
		if (!checkOperacaoAtiva(SigaaListaComando.CONSOLIDAR_DISCIPLINA_MEDIO.getId())) return cancelar();
		
		if (!UserAutenticacao.autenticaUsuario(getCurrentRequest(), getUsuarioLogado().getLogin(), senha)) {
			addMensagemErro("Senha inválida. Digite a senha correta para consolidar a disciplina.");
			return null;
		}

		if(hasOnlyErrors())
			return null;

		try {
			TurmaMov mov = new TurmaMov();
			mov.setTurma(turma);
			mov.setConsolidacaoIndividual(individual);
			mov.setMetodoAvaliacao(metodoAvaliacao);
			mov.setConfig(config);
			mov.setCodMovimento(SigaaListaComando.CONSOLIDAR_DISCIPLINA_MEDIO);
			mov.setRetificacoesReconsolidacao(retificacoesReconsolidacao);
			mov.setObjAuxiliar(getNotasDisciplina());
			executeWithoutClosingSession(mov, getCurrentRequest());
			retificacoesReconsolidacao = new ArrayList<RetificacaoMatricula>();
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			removeOperacaoAtiva();
			
			String url = getDirBase() + "/fim.jsf?idTurmaSerie=" + turmaSerie.getId();
			initObj();
			resetBean();
			redirectJSF(url);
			return null;
		
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
	}
	
	/**
	 * Salva as notas digitadas para a turma.<br/><br/>
	 * 
	 * Não é utilizado por JSPs.
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public void salvarNotas() throws NegocioException, ArqException {
		getCurrentRequest().setAttribute("idTurmaSerie", turmaSerie.getId());
		MatriculaComponenteDao dao = null;
		try {	
			checkTurmaResponsaveisRole(turma, false);
			calcularNotas();

			prepareMovimento(SigaaListaComando.SALVAR_CONSOLIDACAO_DISCIPLINA_MEDIO);
	
			TurmaMov mov = new TurmaMov();
			mov.setTurma(turma);
			mov.setMetodoAvaliacao(metodoAvaliacao);
			mov.setConfig(config);
			mov.setCodMovimento(SigaaListaComando.SALVAR_CONSOLIDACAO_DISCIPLINA_MEDIO);
			mov.setNotasIniciaisTurma(notasIniciaisTurma);
			mov.setObjAuxiliar(getNotasDisciplina());
			
			execute(mov);
	
			addMensagemInformation("Notas Salvas com Sucesso.");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());	
		} finally {
			if (dao != null) dao.close();
		}
	}	
	
	/**
	 * Salva as notas cadastradas para a turma sem efetivamente
	 * consolidá-la e as oculta para os alunos.<br/><br/>
	 * 
	 * Método chamado na seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception 
	 */
	public String salvarOcultar() throws Exception {

		ConfiguracoesAvaMBean cBean = getMBean("configuracoesAva");
		
		if ( config == null ) {
			config = cBean.criar(turma);
			addMensagemWarning("Para poder ocultar as notas, as configurações desta turma virtual foram salvas automaticamente. "+ 
								"Você pode verificar a configuração da turma acessando Turma Virtual->Menu Turma Virtual->Configurações->Configurar Turma.");
		}

		config.setOcultarNotas(true);
		cBean.setConfig(config);
		cBean.salvar();
 		
		return salvar();
	}
	
	/**
	 * Salva as notas cadastradas para a turma sem efetivamente
	 * consolidá-la e as publica para os alunos.<br/><br/>
	 * 
	 * Método chamado na seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception 
	 */
	public String salvarPublicar() throws Exception {

		ConfiguracoesAvaMBean cBean = getMBean("configuracoesAva");
		
		if ( config == null ) {
			config = cBean.criar(turma);
			addMensagemWarning("Para poder  publicar as notas, as configurações desta turma virtual foram salvas automaticamente. "+ 
								"Você pode verificar a configuração da turma acessando Turma Virtual->Menu Turma Virtual->Configurações->Configurar Turma.");
		}

		config.setOcultarNotas(false);
		cBean.setConfig(config);
		cBean.salvar();
 		
		return salvar();
	}	
	
	/**
	 * Salva as notas cadastradas para a turma sem efetivamente
	 * consolidá-la.<br/><br/>
	 * 
	 * Método chamado na seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String salvar() throws ArqException {
		try {
			if (!checkOperacaoAtiva(1)) return cancelar();
			salvarNotas();
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		} catch(NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		return consolidaTurmaPortal();
	}

	/**
	 * Imprime o relatório de notas da turma.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>sigaa.war/medio/consolidacao/confirmar.jsp</li>
	 *		<li>sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String imprimir() throws ArqException {

		try {
			if (!checkOperacaoAtiva(1)) return cancelar();
			salvarNotas();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}

		return forward(getDirBase() + "/relatorio.jsp");
	}

	/**
	 * Volta para a tela de seleção de turma.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li> sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarTurma() {
		return forward(getDirBase() + JSP_SELECIONA_TURMA);
	}

	/**
	 * Volta para a tela de seleção de turma.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/formImportarPlanilha.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarLancarNotas() {
		return redirectJSF(getDirBase() + JSP_DETALHES_TURMA);
	}
	
	/**
	 * Checa se o usuário atual é um dos responsáveis pela turma passada por
	 * parâmetro. 
	 *
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li> Não Invocado por JSP´s </li>
	 * </ul>
	 *
	 * @param turma
	 * @param comPermissao Indica se deve verificar as permissões configuradas por um docente para o usuário logado. 
	 * False indica que mesmo que o usuário tenha recebido permissão de docente, não poderá realizar a operação se não 
	 * tiver um dos outros vínculos.
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public void checkTurmaResponsaveisRole(Turma turma, boolean comPermissao)
			throws SegurancaException, DAOException {
		TurmaVirtualDao dao = getDAO(TurmaVirtualDao.class);
		
		turma = dao.refresh(turma);

		// Professores que ministram as aulas
		if (getServidorUsuario() != null || getUsuarioLogado().getDocenteExterno() != null ) {
			for (Docente professor : turma.getDocentes()) {
				if ( getServidorUsuario() != null && professor != null && professor.getId() == getServidorUsuario().getId()) {
					return;
				}else if ( getServidorUsuario() == null && professor != null && professor.getId() == getUsuarioLogado().getDocenteExterno().getId()) {
					return;
				}
			}
		}
		
		// Usuários que receberam permissão do docente para administrar a turma virtual
		if (comPermissao){
			PermissaoAva permissao = dao.findPermissaoByPessoaTurma(getUsuarioLogado().getPessoa(), turma);
			if (permissao != null) return;
		}

		// Gestores
		if (getUsuarioLogado().isUserInRole( new int[]{SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO }))
			return;

		throw new SegurancaException(
				"Usuário não autorizado a realizar esta operação");
	}

	/**
	 * Retorna a média mínima de aprovação, após todas as notas serem preenchidas, incluindo a recuperação.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public float getMediaMinima(){
		return param.getMediaMinimaAprovacao();
	}
	
	
	/**
	 * Retorna a média mínima para a turma.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public float getMediaMinimaPossibilitaRecuperacao(){
		return param.getMediaMinimaPossibilitaRecuperacao();
	}
	
	/**
	 * Retorna a média mínima para passar por média.
	 * <ul>
	 * 		<li>sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws DAOException
	 */
	public float getMediaMinimaPassarPorMedia() throws DAOException{
		return getParametrosAcademicos().getMediaMinimaPassarPorMedia();
	}
	
	/**
	 * Peso dado a média no calculo da média final, usado quando o discente encontra-se em recuperação
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 */
	public int getPesoMedia() {
		String[] pesoMediaPesoRec = param.getArrayPesosMediaRec();
		return (new Integer(pesoMediaPesoRec[0].trim())); 
	}
	
	/**
	 * Peso dado a Recuperação no calculo da média final
	 * <ul>
	 * 		<li>sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 */
	public int getPesoRecuperacao() {
		String[] pesoMediaPesoRec = param.getArrayPesosMediaRec();
		return (new Integer(pesoMediaPesoRec[1].trim())); 
	}
	
	/**
	 * Retorna a média mínima para ser aprovado
	 * <ul>
	 * 		<li>sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public float getMediaMinimaAprovacao() {
		return param.getMediaMinimaAprovacao();
	}	
	
	/**
	 * Atribui o ano do calendário vigente
	 * @return
	 */
	public float getAnoCalendario(){
		return getCalendarioVigente().getAno();
	}
	
	public ConsolidarDisciplinaMBean() {
	}

	public boolean isIndividual() {
		return individual;
	}
	public void setIndividual(boolean individual) {
		this.individual = individual;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public ParametrosGestoraAcademica getParam() {
		return param;
	}
	public void setParam(ParametrosGestoraAcademica param) {
		this.param = param;
	}
	public ConfiguracoesAva getConfig() {
		return config;
	}
	public void setConfig(ConfiguracoesAva config) {
		this.config = config;
	}
	public List<RegraNota> getRegraNotas() {
		return regraNotas;
	}
	public void setRegraNotas(List<RegraNota> regraNotas) {
		this.regraNotas = regraNotas;
	}
	public boolean isParcial() {
		return parcial;
	}
	public void setParcial(boolean parcial) {
		this.parcial = parcial;
	}
	public Collection<NotaUnidade> getNotasIniciaisTurma() {
		return notasIniciaisTurma;
	}
	public void setNotasIniciaisTurma(Collection<NotaUnidade> notasIniciaisTurma) {
		this.notasIniciaisTurma = notasIniciaisTurma;
	}
	public Collection<RetificacaoMatricula> getRetificacoesReconsolidacao() {
		return retificacoesReconsolidacao;
	}
	public void setRetificacoesReconsolidacao(
			Collection<RetificacaoMatricula> retificacoesReconsolidacao) {
		this.retificacoesReconsolidacao = retificacoesReconsolidacao;
	}
	public boolean isFiltroNumero() {
		return filtroNumero;
	}
	public void setFiltroNumero(boolean filtroNumero) {
		this.filtroNumero = filtroNumero;
	}
	public boolean isFiltroCurso() {
		return filtroCurso;
	}
	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}
	public boolean isFiltroAno() {
		return filtroAno;
	}
	public void setFiltroAno(boolean filtroAno) {
		this.filtroAno = filtroAno;
	}
	public boolean isFiltroTurma() {
		return filtroTurma;
	}
	public void setFiltroTurma(boolean filtroTurma) {
		this.filtroTurma = filtroTurma;
	}
	public Integer getAno() {
		return ano;
	}
	public void setAno(Integer ano) {
		this.ano = ano;
	}
	public String getBuscaTurma() {
		return buscaTurma;
	}
	public void setBuscaTurma(String buscaTurma) {
		this.buscaTurma = buscaTurma;
	}
	public Serie getSerie() {
		return serie;
	}
	public void setSerie(Serie serie) {
		this.serie = serie;
	}
	public TurmaSerie getTurmaSerie() {
		return turmaSerie;
	}
	public void setTurmaSerie(TurmaSerie turmaSerie) {
		this.turmaSerie = turmaSerie;
	}
	public TurmaSerieAno getTurmaSerieAno() {
		return turmaSerieAno;
	}

	public void setTurmaSerieAno(TurmaSerieAno turmaSerieAno) {
		this.turmaSerieAno = turmaSerieAno;
	}

	public List<TurmaSerie> getListaSeries() {
		return listaSeries;
	}
	public void setListaSeries(List<TurmaSerie> listaSeries) {
		this.listaSeries = listaSeries;
	}
	public Collection<MatriculaComponente> getMatriculas() {
		return matriculas;
	}
	public void setMatriculas(Collection<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}
	public List<NotaDisciplina> getNotasDisciplina() {
		return notasDisciplina;
	}
	public void setNotasDisciplina(List<NotaDisciplina> notasDisciplina) {
		this.notasDisciplina = notasDisciplina;
	}

	public List<NotaDisciplina> getNotasDisciplinaParcial() {
		return notasDisciplinaParcial;
	}

	public void setNotasDisciplinaParcial(
			List<NotaDisciplina> notasDisciplinaParcial) {
		this.notasDisciplinaParcial = notasDisciplinaParcial;
	}

	public List<RegraNota> getRegras() {
		return regras;
	}

	public void setRegras(List<RegraNota> regras) {
		this.regras = regras;
	}
	
	public String getPageBack() {
		return pageBack;
	}

	public void setPageBack(String pageBack) {
		this.pageBack = pageBack;
	}

	/**
	 * Retorna uma descrição completa da turma.
	 * <ul>
	 *    <li>sigaa.war/medio/consolidacao/detalhesTurma.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String getDescricaoTurma() {
		String descricao = (turma.getDisciplina() != null ? turma.getDisciplina().getDescricaoResumida() : "")
				+ " - "+turmaSerie.getDescricaoCompleta()  
				+ " ("+ turma.getAnoPeriodo()+ ")";
		return descricao;
	}	
	
	/**
	 * Método chamado a partir do Portal da Turma para lançamento das notas.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/menu.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String consolidaTurmaPortal() throws ArqException {

		TurmaVirtualMBean portalTurma = (TurmaVirtualMBean) getMBean("turmaVirtual");
		GenericDAO dao = getGenericDAO();
		
		try {
			if (isEmpty(turma))
				turma = portalTurma.getTurma();
			
			turma = dao.findAndFetch(turma.getId(), Turma.class, "curso");
			turmaSerieAno = dao.findByExactField(TurmaSerieAno.class, "turma.id", turma.getId(), true);
			fromPortalTurma = true;	
			pageBack = "portalTurma";
			return selecionarDisciplina();
		
		} finally {
			if ( dao != null )
				dao.close();
		}
	}
	
	/**
	 * Seleciona a Turma e lista os Discentes para alteração de Situação.<br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/medio/consolidacao/selecionaTurma.jsp</li>
	 * </ul>	 
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarTurmaDisciplina() throws ArqException{
		int idTurma = getParameterInt("id", 0);
		GenericDAO dao = getGenericDAO();
		
		try {
			if (idTurma <= 0 && isEmpty(idTurma)){			
				addMensagemWarning("Disciplina não selecionada!");
				return null;
			}			
			turma = dao.findAndFetch(idTurma, Turma.class, "curso");
			turmaSerieAno = dao.findByExactField(TurmaSerieAno.class, "turma.id", turma.getId(), true);
			fromPortalTurma = true;	
			pageBack = "portalTurma";
			return selecionarDisciplina();
			
		} finally {
			if ( dao != null )
				dao.close();
		}
	}
	
	/**
	 * Identifica se o processo de consolidação começou a partir do menu gestor de ensino técnico.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>sigaa.war/medio/consolidacao/selecionarTurma.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isFromMenuGestor() {
		return (getParameter("gestor") != null && "true".equals(getParameter("gestor")));
	}

	public List<SelectItem> getTurmasProfessor() {
		return getTurmasSituacaoCombo(SituacaoTurma.ABERTA);
	}
	
	/**
	 * Retorna o número de faltas de um bimestre. As faltas são cadastradas através da frequencia 
	 * da turma virtual.
	 * Método não invocado por JSPs
	 * @param nota
	 * @param calRegras
	 * @param frequencias
	 * @return
	 */
	private short getFrequenciaBimestre( RegraNota nota , Collection<CalendarioRegra> calRegras , Set<FrequenciaAluno> frequencias ){
		
		short faltas = 0;
		
		if ( nota.isNota() ){
			for ( CalendarioRegra cal : calRegras ){
				if (cal.getRegra().getId() == nota.getId() && (cal.getDataInicio() != null && cal.getDataFim() != null)){
					if (frequencias != null)
						for (FrequenciaAluno f : frequencias) 
							if ( f.getData().getTime() >= cal.getDataInicio().getTime() && 
								 f.getData().getTime() <= cal.getDataFim().getTime() )
								faltas += f.getFaltas();
		
				}
			}				
		}			
		
		return faltas;
	}
}