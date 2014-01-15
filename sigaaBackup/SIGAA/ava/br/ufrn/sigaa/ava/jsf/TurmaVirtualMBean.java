/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/09/2009
 *
 */

package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.hibernate.HibernateException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrm.sigaa.nee.dao.NeeDao;
import br.ufrn.arq.chat.ChatEngine;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.AmbienteUtils;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteTurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.infantil.TurmaInfantilDao;
import br.ufrn.sigaa.ava.dao.ForumDao;
import br.ufrn.sigaa.ava.dao.PerfilUsuarioAvaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.Forum;
import br.ufrn.sigaa.ava.dominio.GrupoDiscentes;
import br.ufrn.sigaa.ava.dominio.NoticiaTurma;
import br.ufrn.sigaa.ava.dominio.PerfilUsuarioAva;
import br.ufrn.sigaa.ava.dominio.PermissaoAva;
import br.ufrn.sigaa.ava.dominio.RegistroAtividadeTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeralMensagem;
import br.ufrn.sigaa.ava.negocio.ChatTurmaCleanerThread;
import br.ufrn.sigaa.ava.negocio.GrupoDiscentesHelper;
import br.ufrn.sigaa.ava.questionarios.jsf.QuestionarioTurmaMBean;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.PlanoCurso;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.jsf.PlanoCursoMBean;
import br.ufrn.sigaa.ensino.jsf.ConsolidarTurmaMBean;
import br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dao.PlanoDocenciaAssistidaDao;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.PlanoDocenciaAssistida;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;
import br.ufrn.sigaa.mobile.touch.jsf.LoginMobileTouchMBean;
import br.ufrn.sigaa.nee.dominio.SolicitacaoApoioNee;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosTurmaVirtual;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Docente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.portal.jsf.PortalDocenteMBean;
import br.ufrn.sigaa.twitter.jsf.TwitterMBean;

/**
 * Managed-Bean responsável por efetuar operações no portal das turmas.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 * 
 */
@Component("turmaVirtual") @Scope("session")
public class TurmaVirtualMBean extends ControllerTurmaVirtual {
	
	/** Link da página de impressão dos participantes da turma. */
	public static final String PAGINA_IMPRIMIR_PARTICIPANTES = "/ava/participantes_impressao.jsf";
	
	/** Link da página de impressão dos tópicos de aula da turma. */
	public static final String PAGINA_IMPRIMIR_AULAS = "/ava/aulas_impressao.jsf";
	
	/** Link da listagem de turmas abertas. */
	public static final String LISTA_TURMAS_ABERTAS = "/ava/turmasAbertas.jsf";
	
	/** Link da listagem de turmas anteriores. */
	public static final String LISTA_TURMAS_ANTERIORES = "/portais/docente/turmas.jsf";
	
	/** Modo clássico de exibição da turma virtual. */
	public static final int MODO_CLASSICO = 1;

	/** Modo mobile de exibição da turma virtual. */
	public static final int MODO_MOBILE = 2;



	/** Turma sendo acessada atualmente na turma virtual */
	private Turma turma;

	/** Lista das avaliações da turma */
	List<DataAvaliacao> datasAvaliacoes;
	
	/** Se o usuário logado é docente */
	private boolean docente;

	/** Se o usuário logado é discente */
	private boolean discente;
	
	/** Indica se o usuário logado tem alguma PermissaoAva associada a ele. */
	private boolean permissao;

	/** Se o usuário logado não é docente, mas tem permissão de docente */
	private boolean acessoDocente;
	
	/** Indica se o usuário está na tela de Publicação da Turma Virtual */
	private boolean acessouPublicacao = false;

	/** Indica o tipo do relatório. */
	private int tipoRelatorio;

	/** Conjunto de turmas do mesmo componente curricular que a turma atual */ 
	private List<Turma> turmasSemelhantes;

	/** {@link PermissaoAvaMBean} existente. */
	@Autowired
	private PermissaoAvaMBean permissaoBean;

	/** Lista de docentes da turma. */
	private List<DocenteTurma> docentesTurma;
	
	/** Coleção de participantes da turma. */
	private Collection<MatriculaComponente> discentesTurma;
	
	/** Coleção de discentes que trancaram a matrícula na turma. */
	private Collection<MatriculaComponente> discentesTrancadosTurma;
	
	// Campos utilizados na busca das turmas virtuais
	/** Id do departamento no qual deseja-se buscar as turmas. */
	private Integer idDepto;

	/** Id do centro no qual deseja-se buscar as turmas. */
	private Integer idCentro;
	
	/** Palavra chave. */
	private String palavraChave;
	
	/** Lista de turmas. */
	private List<Turma> turmas;
	
	/** Lista dos planos de docencia assistida da turma virtual. */
	private List<PlanoDocenciaAssistida> docenciaAssistida;
	
	/** Lista contendo as últimas 5 notícias da turma virtual. */
	private List <NoticiaTurma> noticias;
	
	/** Indica se a página está sendo exibida no formato de impressão. */
	private boolean imprimir;
	
	/** Tópico de aula que foi selecionado para exibição. */
	private TopicoAula topicoSelecionado = new TopicoAula();
	
	/** Armazena o id do próximo tópico da turma virtual. */
	private int idProximoTopico;
	
	/** Armazena o id do tópico anterior da turma virtual. */
	private int idTopicoAnterior;

	/** Matrícula selecionada pra se exibir o perfil do discente. */
	private MatriculaComponente matriculaSelecionada;
	
	/** Parecer acadêmico a respeito da necessidade educacional especial do aluno. */
	private SolicitacaoApoioNee parecerNeeDiscenteSelecionado;
	
	/** Armazena o estilo de visualização dos tópicos utilizado. */
	private int estiloVisualizacaoTopicos = ConfiguracoesAva.ESTILO_TOPICOS_LISTA;
	
	/** Configurações da turma virtual. */
	private ConfiguracoesAva configuracoes;
	
	/** Edição de tópicos da turma virtual. */
	private boolean edicaoAtiva = false;
	
	/** Conjunto de grupos da turma. */
	private List<GrupoDiscentes> grupos;

	/** Lista contendo as últimas atividades da turma virtual. */
	private List<RegistroAtividadeTurma> ultimasAtividades;
	
	/** Lista contendo as últimas mensagens dos fóruns da turma virtual. */
	private List<ForumGeralMensagem> ultimasMensagens;
	
	/** Permite que o usuário altere o modo de visualização da turma virtual entre Mobile e Clássico. */
	private int modoExibicao = MODO_CLASSICO;
	
	/**
	 * Permite a um professor visualizar uma turma virtual como
	 * se fosse um discente.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war/ava/menu.jsp</li>
	 *   </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String visualizarComoDiscente() throws DAOException {

		if (acessoDocente == true && docente == false && discente == true) {
			docente = true;
			discente = false;
			acessoDocente = true;
		} else {
			docente = false;
			discente = true;
			acessoDocente = true;
		}
		
		TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
		return tBean.retornarParaTurma();
	}

	/**
	 * Método utilizado para acessar uma turma virtual.
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * 		<li>sigaa.war/lato/coordenador.jsp</li>
	 * 		<li>sigaa.war/portais/discente/discente.jsp</li>
	 * 		<li>sigaa.war/portais/discente/turmas.jsp</li>
	 * 		<li>sigaa.war/portais/docente/docente.jsp</li>
	 * 		<li>sigaa.war/portais/docente/turmas.jsp</li>
	 *   </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String entrar() throws ArqException {
		
		if (isModoOtimizado()) {
			addMensagemWarning("O acesso ao Ambiente Virtual de Aprendizado (Turma Virtual) está temporariamente desabilitado.");
			return null;
		}

		// Verifica se o usuário possui permissão para entrar na turma.
		
		// Guarda o sistema atual.
		SubSistema subSistema = getSubSistema();
		// Guarda a turma anterior.
		Turma turmaAnterior = turma;
		// Instancia a lista de docência assistida 
		docenciaAssistida = new ArrayList<PlanoDocenciaAssistida>();
		
		configuracoes = null;
		
		preparaEntrada();
		
		// Reinicia o bean que gerencia a conta do twitter da turma.
		TwitterMBean twitterBean = getMBean("twitterMBean");
		twitterBean.resetBean();
		
		GenericDAO dao = null;
		    
	    // Se for obrigatório, verifica se há um plano de curso cadastrado para esta turma.
		if (turma.isAberta() && ParametroHelper.getInstance().getParametroBoolean(ParametrosTurmaVirtual.PLANO_CURSO_OBRIGATORIO)){
		    try {
		    	dao = getGenericDAO();
		    	Integer idTurma = getParameterInt("idTurma");
		    	if (idTurma == null && turma != null && turma.getId() > 0)
		    		idTurma = turma.getId();
		    	
		    	// Se for um docente da turma acessando, verifica o plano de curso.
		    	if (idTurma != null && isPermissaoDocente()){
		    		// Procura o plano de curso desta turma.
			    	PlanoCurso planoCurso = dao.findByExactField(PlanoCurso.class, "turma.id", idTurma, true);
			    	
			    	// Se não tiver o plano, obriga o cadastro.
			    	if (planoCurso == null || !planoCurso.isFinalizado()){
			    		PlanoCursoMBean planoMBean = getMBean("planoCurso");
			    		planoMBean.setTurma(turma());
			    		planoMBean.setAcessoTurmaVirtual(true);
	
			    		// Como não foi possível acessar a turma virtual e este bean está em session, volta os valores para os anteriores.
			    		setSubSistemaAtual(subSistema);
			    		if (turmaAnterior != null)
			    			turma = turmaAnterior;
			    		
			    		return planoMBean.gerenciarPlanoCurso();
			    	}
		    	}
		    } finally {
		    	if (dao != null)
		    		dao.close();
		    }
		}
	    
	    if (!hasOnlyErrors()){
	    	
	    	// Reinicia os tópicos de aula
	    	clearCacheTopicosAula();
			
	    	// configura a listagem de tópicos de aula
			configurarTopicos(true);
					
	    	// Garante que o bean de consolidação vai utilizar a nova turma na próxima vez que o professor tentar lançar as notas.
	    	ConsolidarTurmaMBean cBean = getMBean("consolidarTurma");
	    	cBean.setTurma(turma);
	    	
	    	carregarNoticias();
	    	
	    	// Registra a entrada do usuário.
	    	registrarAcao(null, EntidadeRegistroAva.TURMA, AcaoAva.ACESSAR, turma.getId());
	    	
		    // Se está cadastrado, permite entrar na turma.
	    	getCurrentSession().setAttribute("paComTurma", "true");
	    	
	    	return retornarParaTurma();
	    }
	    
	    return null;
	}

	private boolean isModoOtimizado() {
		return getUsuarioLogado().getVinculoAtivo().isVinculoDiscente() && ParametroHelper.getInstance().getParametroBoolean(ParametrosGerais.PORTAL_DISCENTE_MODO_REDUZIDO);
	}
	
	/**
	 * Prepara os tópicos de aula para serem exibidos ao usuário.
	 * 
	 * @throws DAOException
	 */
	private void configurarTopicos (boolean inicializando) throws DAOException {
		
		TopicoAulaMBean tABean = getMBean("topicoAula");
		
		
		// Se está entrando na turma, exibir as aulas de acordo com a configuração da turma.
		if (inicializando){
			if (getConfig() != null)
				estiloVisualizacaoTopicos = configuracoes.getEstiloVisualizacaoTopicos();
		}
		
		if (estiloVisualizacaoTopicos == ConfiguracoesAva.ESTILO_TOPICOS_PAGINADOS){
			if (tABean.getAulas() != null && tABean.getAulas().size() > 0) {
				
				TopicoAula topico = null;
				
				// Se é para exibir os tópicos paginados, exibe o referente a última aula já apresentada.
				for (TopicoAula t : tABean.getAulas())
					if (t.getData().before(new Date()))
						topico = t;
					else {
						if (topico == null)
							topico = t;
						break;
					}
				
				if (topico != null)
					topicoSelecionado.setId(topico.getId());
				
				exibirTopico(null);
			}
		} else {
			topicoSelecionado = new TopicoAula();
			idProximoTopico = 0;
			idTopicoAnterior = 0;
		}
	}

	/**
	 * Apaga todos os dados atuais da turma virtual.
	 */
	private void resetData() {
		TopicoAulaMBean taBean = getMBean("topicoAula");
		taBean.setListagem(null);
		taBean.setTopicosAulas(null);
		taBean.setItens(null);
		taBean.setDatasAulas(null);
		EnqueteMBean eBean = getMBean("enquete");
		eBean.setListagem(null);
		TarefaTurmaMBean ttBean = getMBean("tarefaTurma");
		ttBean.setListagem(null);
		ImportacaoDadosTurmasAnterioresMBean idBean = getMBean("importacaoDadosTurma");
		idBean.setTurmas(null);
		docentesTurma = null;
		discentesTurma = null;
		discentesTrancadosTurma = null;
		ultimasAtividades = null;
		ultimasMensagens = null;
		grupos = null;
	}
	
	/**
	 * Carrega as notícias cadastradas na turma virtual.
	 * Método não invocado por JSP´s
	 * @throws DAOException 
	 */
	public void carregarNoticias() throws DAOException {
		TurmaVirtualDao turmaDao = getDAO(TurmaVirtualDao.class);
		
		try {
			noticias = turmaDao.findUltimasNoticiasByTurma(turma());
		} finally {
			turmaDao.close();
		}
	}

	/**
	 * Busca o fórum associado à turma.<br /><br />
	 * Método não invocado por JSP´s
	 * 
	 * @return
	 */
	public Forum getMural() {
		return getDAO(ForumDao.class).findMuralByTurma(turma);
	}

	/**
	 * <p>Retorna os docentes da turma.</p>
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/participantes.jsp</li>
	 *   </ul>
	 * @return
	 * @throws DAOException 
	 */
	public List<DocenteTurma> getDocentesTurma() throws DAOException {
		if (docentesTurma == null) {
			docentesTurma = getDocentesTurma(turma(), getDAO(UsuarioDao.class), getDAO(DocenteTurmaDao.class));
		}
		return docentesTurma;
	}
	
	/**
	 * <p>Retorna os discentes que possuem plano de docência assistida da turma.</p>
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/participantes.jsp</li>
	 *   </ul>
	 * @return
	 * @throws DAOException 
	 */
	public List<PlanoDocenciaAssistida> getDocenciaAssistida() throws DAOException{
		if (isEmpty(docenciaAssistida)){
			PlanoDocenciaAssistidaDao dao = getDAO(PlanoDocenciaAssistidaDao.class);
			UsuarioDao usuarioDao = getDAO(UsuarioDao.class);
			try{
				docenciaAssistida = new ArrayList<PlanoDocenciaAssistida>();
				docenciaAssistida = dao.findAllByTurma(turma());
								
				for (PlanoDocenciaAssistida plano : docenciaAssistida){
					plano.getDiscente().setUsuario(usuarioDao.findPrimeiroUsuarioByPessoa(plano.getDiscente().getPessoa().getId()));
				}
			} finally {
				if (dao != null)
					dao.close();
				
				if (usuarioDao != null)
					usuarioDao.close();				
			}
		}
		return docenciaAssistida;
	}
	
	/**
	 * Retorna os docentes de uma turma. 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/lista_participantes.jsp</li>
	 *   </ul>
	 * 
	 * @param turma
	 * @param usuarioDao
	 * @param turmaDao
	 * @return
	 * @throws DAOException
	 */
	public List<DocenteTurma> getDocentesTurma(Turma turma, UsuarioDao usuarioDao, DocenteTurmaDao docenteTurmaDao) throws DAOException {
		
		List<DocenteTurma> docentesTurma = new ArrayList<DocenteTurma>();
		if (!isEmpty(turma.getSubturmas())) { 
			// Em caso de turma agrupadora, deve-se buscar os docentes das sub-turmas.
			List<Servidor> docentes = new ArrayList<Servidor>();
			List<DocenteExterno> docentesExternos = new ArrayList<DocenteExterno>();
			Map<Integer, String> subTurmasDocente = new HashMap<Integer, String>();
			Map<Integer, String> subTurmasDocenteExterno = new HashMap<Integer, String>();
				
			List<Turma> subTurmas = turma.getSubturmas();
			for (Turma st : subTurmas) {
				for (DocenteTurma dt : st.getDocentesTurmas()) {
					if (dt.getDocente() != null) {
						// Servidor
						if (!docentes.contains(dt.getDocente())) {
							docentes.add(dt.getDocente());
						}
						if (subTurmasDocente.get(dt.getDocente().getId()) == null)
							subTurmasDocente.put(dt.getDocente().getId(), st.getCodigo() + " ");
						else
							subTurmasDocente.put(dt.getDocente().getId(), subTurmasDocente.get(dt.getDocente().getId()) + st.getCodigo() + " ");
						
					} else {
						// Docente externo
						if (!docentesExternos.contains(dt.getDocenteExterno())) {
							docentesExternos.add(dt.getDocenteExterno());
						}
						if (subTurmasDocenteExterno.get(dt.getDocenteExterno().getId()) == null)
							subTurmasDocenteExterno.put(dt.getDocenteExterno().getId(), st.getCodigo() + " ");
						else
							subTurmasDocenteExterno.put(dt.getDocenteExterno().getId(), subTurmasDocenteExterno.get(dt.getDocenteExterno().getId()) + st.getCodigo() + " ");
					}
				}
			}
			for (Servidor doc : docentes) {
				doc.setPrimeiroUsuario(usuarioDao.findPrimeiroUsuarioByPessoa(doc.getPessoa().getId()));
				DocenteTurma dt = new DocenteTurma();
				try {
					dt.setTurma((Turma) UFRNUtils.deepCopy(turma));
				} catch (Exception e) {
					e.printStackTrace();
				}
				dt.setDocente(doc);
				dt.getTurma().setCodigo(subTurmasDocente.get(doc.getId()));
				docentesTurma.add(dt);
			}
			for (DocenteExterno doc : docentesExternos) {
				Servidor s = new Servidor();
				s.setPessoa(doc.getPessoa());
				s.setFormacao(doc.getFormacao());
				s.setPrimeiroUsuario(usuarioDao.findPrimeiroUsuarioByPessoa(doc.getPessoa().getId()));
				
				DocenteExterno de = new DocenteExterno();
				de.setPessoa(doc.getPessoa());
				
				DocenteTurma dt = new DocenteTurma();
				try {
					dt.setTurma((Turma) UFRNUtils.deepCopy(turma));
				} catch (Exception e) {
					e.printStackTrace();
				}
				dt.setDocente(s);
				dt.setDocenteExterno(de);				
				dt.getTurma().setCodigo(subTurmasDocenteExterno.get(doc.getId()));
				
				docentesTurma.add(dt);
			}
		} else {
			docentesTurma = docenteTurmaDao.findDocentesByTurma(turma);
		}
		return docentesTurma;
	}

	/**
	 * Retorna os discentes matriculados na turma.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/lista_participantes.jsp</li>
	 *   </ul>
	 * @return
	 * @throws DAOException 
	 * @throws NegocioException
	 */
	public Collection<MatriculaComponente> getDiscentesTurma() throws DAOException{
		
		TurmaDao dao = null;
		TurmaInfantilDao tDao = null;
		
		if (discentesTurma == null)
			try {
				dao = getDAO(TurmaDao.class);
				
				if (turma != null) {
					if (!isEmpty(turma.getSubturmas())) {
						List<MatriculaComponente> discentes = new ArrayList<MatriculaComponente>();
						for (Turma st : turma.getSubturmas()) {
							Collection<MatriculaComponente> list = dao.findParticipantesTurma(st.getId());
							if (list != null) discentes.addAll(list);
						}
						Collections.sort(discentes, new Comparator<MatriculaComponente>() {
							public int compare(MatriculaComponente o1, MatriculaComponente o2) {
								return o1.getDiscente().getPessoa().getNomeAscii().compareToIgnoreCase(o2.getDiscente().getPessoa().getNomeAscii());
							}
						});
						
						discentesTurma = discentes;
					} else {
						
						// Caso a turma seja infantil
						if (turma.isInfantil()){
							tDao = getDAO(TurmaInfantilDao.class);
							discentesTurma = tDao.findMatriculasAConsolidar(turma);
						}else							
							discentesTurma = dao.findParticipantesTurma(getTurma().getId());
					}	
				} else
					discentesTurma = new ArrayList<MatriculaComponente>();
			} finally {
				if (dao != null)
					dao.close();
				if (tDao != null)
					tDao.close();
			}
		
		return discentesTurma;
	}
	
	/**
	 * Retorna os discentes trancados matriculados na turma.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/lista_participantes.jsp</li>
	 *   </ul>
	 * @return
	 * @throws NegocioException
	 */
	public Collection<MatriculaComponente> getDiscentesTrancados(){
		
		TurmaDao dao = null;
		
		if (discentesTrancadosTurma == null)
			try {
				dao = getDAO(TurmaDao.class);
				
				if (turma != null) {
					if (!isEmpty(turma.getSubturmas())) {
						List<MatriculaComponente> discentes = new ArrayList<MatriculaComponente>();
						for (Turma st : turma.getSubturmas()) {
							Collection<MatriculaComponente> list = dao.findParticipantesTurma(st.getId(), null, true, false, SituacaoMatricula.TRANCADO);
							if (list != null) discentes.addAll(list);
						}
						Collections.sort(discentes, new Comparator<MatriculaComponente>() {
							public int compare(MatriculaComponente o1, MatriculaComponente o2) {
								return o1.getDiscente().getPessoa().getNomeAscii().compareToIgnoreCase(o2.getDiscente().getPessoa().getNomeAscii());
							}
						});
						
						discentesTrancadosTurma = discentes;
					} else
						discentesTrancadosTurma = dao.findParticipantesTurma(turma.getId(), null, true, false, SituacaoMatricula.TRANCADO);
				} else
					discentesTrancadosTurma = new ArrayList<MatriculaComponente>();
			} finally {
				if (dao != null)
					dao.close();
			}
			
		return discentesTrancadosTurma;
	}
	
	/**
	 * Exibe a listagem com os discentes trancados.<br/>
	 * Método não invocado por JSPs. É public porque é chamado no método acessarAlunosTrancados da classe MenuTurmaMBean.
	 * 
	 * @return
	 */
	public String visualizarDiscentesTrancados () {
		discentesTrancadosTurma = null;
		return forward ("/ava/alunos_trancados.jsp");
	}
	
	/**
	 * Retorna os monitores da turma.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List <Discente> getMonitores () throws DAOException {
		
		TurmaVirtualDao dao = null;
		try {
			dao = getDAO(TurmaVirtualDao.class);	
			List <Discente> monitores = dao.findMonitores (turma().getDisciplina().getId());	
			return monitores;
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	/**
	 * Retorna as configurações da turma virtual atual.
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ensino/consolidacao/relatorio.jsp</li>
	 *   </ul>
	 * @return
	 */
	public ConfiguracoesAva getConfig() {
		return getConfig(getTurma());
	}
	
	/**
	 * Força o novo carregamento das configurações da turma virtual.
	 * <br />
	 * Método não invocado por JSP(s)
	 */
	public void reiniciarConfiguracoes () {
		configuracoes = null;
	}

	/**
	 * Retorna as configurações de uma turma passada como parâmetro
	 * @param turma
	 * @return
	 */
	private ConfiguracoesAva getConfig(Turma turma) {
		TurmaVirtualDao dao = null;
		
		if (configuracoes == null && turma != null){
			try {
				dao = getDAO(TurmaVirtualDao.class);
				configuracoes = dao.findConfiguracoes(turma);
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		return configuracoes;
	}

	/**
	 * Verifica se as turmas podem ser visualizadas na parte pública do SIGAA.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/public/docente/turma.jsp</li>
	 *   </ul>
	 * @return
	 * @throws NegocioException
	 */
	public String getVerificaAcessoExterno() throws NegocioException {
		
		int idTurma = getParameterInt("tid",0);
		
		ConfiguracoesAva config = getDAO(TurmaVirtualDao.class).
			findConfiguracoes(new Turma(idTurma));

		if(turma != null && turma.getId() != idTurma){
			TopicoAulaMBean topicoMBean = getMBean("topicoAula");
			topicoMBean.setTopicosAulas(null);
		}
		if(config != null)
			turma = config.getTurma();
		
		if (config == null || !config.isPermiteVisualizacaoExterna()){ 
			MensagemAviso msg = UFRNUtils.getMensagem(MensagensTurmaVirtual.PERMISSAO_VISUALIZACAO_EXTERNA);
			return msg.getMensagem();
		}
		return null;
	}

	/**
	 * Visualiza diário de classe da turma em questão de forma assíncrona.
	 * <br />
	 * Método não invocado por JSP´s.
	 * 
	 * @param evento
	 * @throws JRException
	 * @throws DAOException
	 * @throws IOException
	 * @throws SegurancaException
	 */
	public void visualizaListaPresenca(ActionEvent evento) throws Exception {

		if (getParameterInt("idTurma") != null) {
			Turma turma = getDAO(TurmaVirtualDao.class).findByPrimaryKey(
					getParameterInt("idTurma"), Turma.class);
			criarListaPresenca(turma);
		} else {
			criarListaPresenca(turma());
		}
	}
	
	/**
	 * Atualiza o discente selecionado para que o usuário da turma virtual visualize seu perfil.
	 * <br />
	 * Chamada pela JSP:
	 * <ul>
	 * <li>/sigaa.war/ava/PerfilUsuarioAva/link_perfil.jsp</li>
	 * </ul>
	 * 
	 * @param evento
	 * @throws DAOException 
	 */
	public void visualizarPerfil (ActionEvent e) throws DAOException {

		int idPessoa = getParameterInt("idPessoa", 0);
		
		// TODO Atualmente só é permitido aos discentes cadastrar perfil.
		
		PerfilUsuarioAvaDao dao = null;
		
		if (idPessoa > 0) {
			
			dao = getDAO(PerfilUsuarioAvaDao.class);
			
			for (MatriculaComponente m : getDiscentesTurma()){
				if (m.getDiscente().getPessoa().getId() == idPessoa){
					registrarAcao(m.getDiscente().getPessoa().getNome(), EntidadeRegistroAva.PERFIL, AcaoAva.ACESSAR, idPessoa);
					
					matriculaSelecionada = m;
					
					try {
						PerfilUsuarioAva p = dao.findPerfilByPessoaTurma(m.getDiscente().getPessoa(), turma);
						
						if (p != null)
							m.setPerfilDiscente(p);
					} finally {
						if (dao != null)
							dao.close();
					}
					
					return;
				}
			}
		}
		
		// Se chegou aqui, é porque o usuário selecionado não tem um discente associado.
		matriculaSelecionada = null;
	}

	/**
	 * Visualiza diário de classe da turma em questão
	 *  <br />
	 * Método chamado pelas seguintes JSPs:
	 *  <ul>
	 *    <li>sigaa.war/ava/menu.jsp</li>
	 *    <li>sigaa.war/ensino/turma/busca_turma.jsp</li>
	 *    <li>sigaa.war/portais/turma/menu_turma.jsp</li>
	 * 	  <li>sigaa.war/WEB-INF/jsp/pesquisa/menu/iniciacao.jsp</li>
	 *  </ul>
	 * @param evento
	 * @throws JRException
	 * @throws DAOException
	 * @throws IOException
	 * @throws SegurancaException
	 */
	public String visualizaListaPresenca() throws Exception {

		if (getParameterInt("idTurma") != null) {
			Turma turma = getDAO(TurmaVirtualDao.class).findByPrimaryKey(getParameterInt("idTurma"), Turma.class);
			criarListaPresenca(turma);
		} else {
			criarListaPresenca(turma());
		}
		return null;
	}
	
	/**
	 * Lista as sub-turmas de uma turma agrupadora para visualização da
	 * lista de presença.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/menu.jsp</li>
	 *  </ul>
	 * @return
	 */
	public String listaSubTurmasListaPresenca() {
		return forward("/ava/subturmas.jsf");
	}
	
	/**
	 * Seleciona a sub-turma caso a turma da turma virtual seja agrupadora.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/subturmas.jsp</li>
	 *  </ul>
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 */
	public String escolherSubTurmaListaPresenca() throws Exception {
		Turma turma = getDAO(GenericSigaaDAO.class).findByPrimaryKey(getParameterInt("id"), Turma.class);
		criarListaPresenca(turma);
		return null;
	}

	/**
	 * Cria a lista de presença para a turma passada como parâmetro.
	 * 
	 * @param turma
	 * @throws Exception
	 */
	private void criarListaPresenca(Turma turma) throws Exception {

		String path = "/br/ufrn/sigaa/relatorios/fontes/DiarioClasse.jasper";

		// Abrir arquivo com o template
		InputStream template = getClass().getResourceAsStream(path);

		// Geração do relatório para o formato JasperPrint. Pode ser exportado
		// para outros formatos
		TurmaDao dao = getDAO(TurmaDao.class);
		Collection<MatriculaComponente> matriculas = null;
		try {
			turma = dao.refresh(turma);
			matriculas = dao.findParticipantesTurma(turma.getId(), null, true, false, SituacaoMatricula.MATRICULADO, SituacaoMatricula.APROVADO,
					SituacaoMatricula.REPROVADO, SituacaoMatricula.REPROVADO_FALTA, SituacaoMatricula.REPROVADO_MEDIA_FALTA);
		} catch (TurmaVirtualException e) {
			addMensagemErro(e.getMessage());
			return;
		} catch (Exception e) {
			tratamentoErroPadrao(e);
			return;
		}
		if (isEmpty(matriculas)) {
			addMensagemErro("Não há alunos matriculados nessa turma.");
			return;
		}

		JRBeanCollectionDataSource rds = new JRBeanCollectionDataSource(
				matriculas);

		HashMap<String, String> parametros = new HashMap<String, String>();
		String turmaDesc = turma.getCodigo();
		if (turma.getPolo() != null) {
			Polo polo = dao.findByPrimaryKey(turma.getPolo().getId(), Polo.class);
			turmaDesc += " - " + polo.getDescricao();
		}
		turmaDesc += " (" + matriculas.size() + " alunos)";
		parametros.put("turma", turmaDesc);
		parametros.put("disciplina", turma.getDisciplina().getCodigoNome());
		parametros.put("horario", turma.getDescricaoHorario());
		parametros.put("anoSemestre", turma.getAno() + "." + turma.getPeriodo());

		StringBuilder sb = new StringBuilder();
		sb = new StringBuilder();
		for (Iterator<Docente> it = turma.getDocentes().iterator(); it.hasNext();) {
			Docente docente = it.next();
			sb.append(docente.getNome());
			if (it.hasNext())
				sb.append(", ");
		}
		parametros.put("docente", sb.toString());

		JasperPrint prt = JasperFillManager.fillReport(template, parametros, rds);
		String nomeArquivo = "listapresenca_" + turma.getDisciplina().getCodigo() + "_"
		+ turma.getAnoPeriodo() + "_" + turma.getCodigo() + ".pdf";
		JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), "pdf");

		FacesContext.getCurrentInstance().responseComplete();
	}


	/**
	 * Indica se o usuário pode acessar a turma como docente 
	 * (sendo docente ou tendo permissão para isso).
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/menu.jsp</li>
	 *  </ul>
	 * @return
	 */
	public boolean isPermissaoDocente() {
		return docente || ( permissaoBean != null &&
				permissaoBean.getPermissaoUsuario() != null && 
				permissaoBean.getPermissaoUsuario().isDocente()
			);
	}
	
	/**
	 * Retorna as avaliações da turma atual.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/rodape.jsp</li>
	 *  </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<DataAvaliacao> getAvaliacoes() throws DAOException {
		
		// Verifica se esta lista é da turma atual
		if ( !isEmpty(datasAvaliacoes) )
		{
			if ( datasAvaliacoes.get(0).getTurma().getId() != turma.getId() )
				datasAvaliacoes = null;
		}
		else
			datasAvaliacoes = null;
		
		// Se a lista for vazia busca no banco
		if ( datasAvaliacoes == null)
		{
			TurmaVirtualDao dao = null;
			
			try
			{
				dao = getDAO(TurmaVirtualDao.class);
				datasAvaliacoes = dao.findAvaliacaoDataByTurma(turma().getId());
			}
			finally{
				if ( dao != null )
					dao.close();
			}
		}
		return datasAvaliacoes;
	}

	/**
	 * Retorna as notícias da turma atual.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/cabecalho.jsp</li>
	 *  </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<NoticiaTurma> getNoticias (){
		return noticias;
	}
	
	public void setNoticias (List <NoticiaTurma> noticias){
		this.noticias = noticias;
	}

	/**
	 * Retorna as última notícia cadastrada ou atualziada na turma atual.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/barra_direita.jsp</li>
	 *  </ul>
	 * @return
	 * @throws DAOException
	 */
	public NoticiaTurma getUltimaNoticia() throws DAOException {
		
		NoticiaTurma ultima = null;
		
		for ( NoticiaTurma n : noticias ){
			Date nData = n.getDataAtualizacao() != null ? n.getDataAtualizacao() : n.getData();
			Date uData = null;

			if ( ultima != null )
				uData = ultima.getDataAtualizacao() != null ? ultima.getDataAtualizacao() : ultima.getData();

			if ( ultima == null || uData == null || nData.getTime() > uData.getTime() )
				ultima = n;
		}
		
		return ultima;
	}
	
	/**
	 * Retorna as últimas atividades da turma atual.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/barra_direita.jsp</li>
	 *  </ul>
	 * @return
	 * @throws DAOException
	 */
	public List <RegistroAtividadeTurma> getUltimasAtividades() throws DAOException {
		
		TurmaVirtualDao dao = null;
		
		if (ultimasAtividades == null)
			try {
				dao = getDAO(TurmaVirtualDao.class);
				ultimasAtividades = dao.findUltimasAtividades(turma());
			} finally {
				if (dao != null)
					dao.close();
			}
		
		return ultimasAtividades;
	}
	
	/**
	 * Retorna as últimas mensagens dos fóruns da turma atual.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/barra_direita.jsp</li>
	 *  </ul>
	 * @return
	 * @throws DAOException
	 */
	public List <ForumGeralMensagem> getUltimasMensagens() throws DAOException {
		
		TurmaVirtualDao dao = null;
		
		if (ultimasMensagens == null)
			try {
				dao = getDAO(TurmaVirtualDao.class);
				ultimasMensagens = dao.findUltimasMensagens(turma());
				
				for ( ForumGeralMensagem fm : ultimasMensagens ){
					String texto =  fm.getConteudo();
					texto = StringUtils.stripHtmlTags(texto);
					texto = StringUtils.limitTxt(texto, 100);
					fm.setConteudo(texto);
				}
				
			} finally {
				if (dao != null)
					dao.close();
			}
		
		return ultimasMensagens;
	}
	
	public void setUltimasAtividades (List <RegistroAtividadeTurma> ultimasAtividades){
		this.ultimasAtividades = ultimasAtividades;
	}
	
	/**
	 * Cria um chat para uma turma virtual
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/menu.jsp</li>
	 * 		<li>sigaa.war/cv/include/_menu_comunidade.jsp</li>
	 * 		<li>sigaa.war/portais/discente/discente.jsp</li>
	 *  </ul>
	 * @return
	 * @throws ArqException
	 */
	public String createChat() throws ArqException {
		registrarAcao(null, EntidadeRegistroAva.CHAT, AcaoAva.ACESSAR, turma().getId() );
		ChatEngine.createChat(getTurma().getId());
		try {
			ChatEngine.registerUser(getUsuarioGeralLogado(), getTurma().getId());
		} catch (IOException e) {
			throw new ArqException(e);
		}
		return null;//forward("/ava/Chat/mensagens.jsp");
	}
	
	/**
	 * Cria um chat para uma turma virtual passando o id da turma como parâmetro de request
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/portais/discente/discente.jsp</li>
	 *  </ul>
	 * @return
	 * @throws ArqException
	 */
	public String createChatParam() throws ArqException {
		ChatEngine.createChat(getParameterInt("id"));
		ChatTurmaCleanerThread.clean();
		try {
			ChatEngine.registerUser(getUsuarioGeralLogado(), getParameterInt("id"));
		} catch (IOException e) {
			throw new ArqException(e);
		}
		return null;//forward("/ava/Chat/mensagens.jsp");
	}
	
	/**
	 * Retorna a senha para acesso ao chat da turma atual
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/menu.jsp</li>
	 *  </ul>
	 * @return
	 */
	public String getChatPassKey() {
		return ChatEngine.generatePassKey(getUsuarioGeralLogado(), getTurma().getId());
	}
	
	/**
	 * Retorna a senha para acesso ao chat de uma turma passada como parâmetro.
	 * Método não invocado por JSP´s
	 * @return
	 */
	public String getChatPassKeyParam() {
		return ChatEngine.generatePassKey(getUsuarioGeralLogado(), getParameterInt("id"));
	}

	/**
	 * Retorna uma instância de UsuarioGeral com os dados do usuário logado.
	 * @return
	 */
	private UsuarioGeral getUsuarioGeralLogado() {
		UsuarioGeral usr = new UsuarioGeral(getUsuarioLogado().getId(), getUsuarioLogado().getNome(), 0, getUsuarioLogado().getLogin(), null);
		usr.setIdFoto(getUsuarioLogado().getIdFoto());
		return usr;
	}

	/**
	 * Retorna a lista de turmas semelhantes (do mesmo componente curricular
	 * e do mesmo ano-período) à turma atual.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/ConteudoTurma/_form.jsp</li>
	 *      <li>sigaa.war/ava/IndicacaoReferencia/_form.jsp</li>
	 *      <li>sigaa.war/ava/PortaArquivos/associar.jsp</li>
	 *      <li>sigaa.war/ava/TarefaTurma/_form.jsp</li>
	 *      <li>sigaa.war/ava/TopicoAula/_form.jsp</li>
	 *  </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public List<Turma> getTurmasSemelhantes() throws ArqException {
		
		if (turmasSemelhantes == null) {
			turmasSemelhantes = new ArrayList<Turma>();
			
			if (getUsuarioLogado().getVinculoAtivo().isVinculoServidor() || getUsuarioLogado().getVinculoAtivo().isVinculoDocenteExterno()) {
				PortalDocenteMBean portal = getMBean("portalDocente");
				Collection<Turma> turmasAbertas = portal.getTurmasAbertas();
				TurmaDao dao = getDAO(TurmaDao.class);
				for (Turma turma : turmasAbertas) {
					Turma t = dao.findByPrimaryKeyOtimizado(turma.getId());
					if (t.getDisciplina().getId() == getTurma().getDisciplina().getId()) {
						turmasSemelhantes.add(turma);
					}
				}
				boolean existe = false;
				for (Turma ts : turmasSemelhantes) {
					if (ts.getId() == turma.getId()) {
						existe = true;
						break;
					}
				}
				if (!existe)
					turmasSemelhantes.add(turma);
				
			} else {
				turmasSemelhantes.add(turma);
			}
		}
		return turmasSemelhantes;
	}

	/**
	 * Retorna todas as turmas que que o docente leciona (incluindo subturmas).
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> getTurmasESubturmasSemestre() throws DAOException {
		List<Turma> turmas = new ArrayList<Turma>();
		if ( getUsuarioLogado().getServidor() != null )
			turmas = (List<Turma>) getDAO(TurmaDao.class).findByDocente(getUsuarioLogado().getServidor(), null, turma().getAno(), turma().getPeriodo(), null, SituacaoTurma.ABERTA);
		else if ( getUsuarioLogado().getDocenteExterno() != null )
			turmas = (List<Turma>) getDAO(TurmaDao.class).findByDocenteExterno(getUsuarioLogado().getDocenteExterno(), null, turma().getAno(), turma().getPeriodo(), null, false, true, SituacaoTurma.ABERTA);
		return turmas;
	}
	
	/**
	 * Retorna somente as turmas ou turmas agrupadoras.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> getTurmasSemestre () throws DAOException {
		List <Turma> turmas = getTurmasESubturmasSemestre();
		List <Turma> rs = new ArrayList <Turma> ();
		
		for (Turma t : turmas){
			if (t.getTurmaAgrupadora() != null){
				t.setId(t.getTurmaAgrupadora().getId());
				t.setCodigo(t.getTurmaAgrupadora().getCodigo());
			}
			
			if (!rs.contains(t))
				rs.add(t);
		}
		
		return rs;
	}
	
	/**
	 * Retorna uma lista com as turmas que o usuário pode acessar.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/NoticiaTurma/_form.jsp</li>	
	 * 	 </ul>
	 * @see {@link #getTurmasSemelhantes()}
	 * @see {@link #getTurmasSemestre()}
	 * 
	 * @return
	 * @throws ArqException
	 */
	public List<Turma> getTurmasPermissaoNoticia () throws ArqException {
		
		TurmaVirtualDao dao = null;
		
		try {
			dao = getDAO(TurmaVirtualDao.class);
			List<Turma> turmas = new ArrayList<Turma>();
			// Usuario pode ter permissão sendo um discente
			if ( getUsuarioLogado().getServidor() != null )
				 turmas = getTurmasSemestre();
			
			// Turmas que o usuário possue permissão
			List<Turma> turmasSemelhantes = dao.findTurmasPermitidasByPessoa (getUsuarioLogado().getPessoa(), PermissaoAva.DOCENTE);
			
			if ( turmasSemelhantes != null )
				for ( Turma t : turmasSemelhantes )
					if ( !turmas.contains(t) )
						turmas.add(t);
			
			// Caso a turma não esteja aberta
			if ( !turmas.contains(turma) )
				turmas.add(turma);
			
			Collections.sort(turmas, new Comparator<Turma>(){
				public int compare(Turma t1, Turma t2) {
					int retorno = 0;
					retorno = t2.getAno() - t1.getAno();
					if( retorno == 0 ) {
						retorno = t2.getPeriodo() - t1.getPeriodo();
						if ( retorno == 0 ){
							String nome = StringUtils.toAscii(t1.getDescricaoSemDocente());
							return nome.compareToIgnoreCase(StringUtils.toAscii( t2.getDescricaoSemDocente() ));
						}
					}	
					return retorno;
				}
			});
			
			return turmas;
		}finally {
			if ( dao != null )
				dao.close();
		}
	}
	
	/**
	 * Retorna a lista para visualização em combobox das sub-turmas referentes a turma atual 
	 * selecionada, que o docente que está logado dá aula ou possui permissão de docente.
	 * <br />
	 * Método chamado pela seguinte JSP:
	 *   <ul>
	 *      <li>sigaa.war/ava/TopicoAula/_form.jsp</li>
	 *      <li>sigaa.war/ava/indicacaoReferencia/_form.jsp</li>
	 *  </ul>
	 *  
	 * @return
	 * @throws DAOException 
	 * @throws DAOException
	 */
	public List <Turma> getTurmasSemestrePermissaoDocente () throws DAOException{
		List <Turma> rs = new ArrayList<Turma>();
		
		TurmaVirtualDao dao = null;
		TurmaDao tDao = null;
		try {
			dao = getDAO(TurmaVirtualDao.class);
			tDao = getDAO(TurmaDao.class);
		
			if ( getUsuarioLogado().getServidor() != null )
				rs.addAll(getTurmasSemestre());
			if(getUsuarioLogado().getDocenteExterno() != null)
			    	rs.addAll(tDao.findByDocenteExterno(getUsuarioLogado().getDocenteExterno(), null, turma.getAno(), turma.getPeriodo(), null, false, true, SituacaoTurma.ABERTA));
			if(getUsuarioLogado().getDiscente() != null)
			    	rs.addAll(dao.findTurmasHabilitadasByPessoa(getUsuarioLogado().getPessoa()));
			
			List <Turma> turmasPermitidas = dao.findTurmasPermitidasByPessoa (getUsuarioLogado().getPessoa(), PermissaoAva.DOCENTE);
			
			for (Turma t : turmasPermitidas)
				if (!rs.contains(t))
					rs.add(t);
		} finally {
		    if (dao != null)
			dao.close();
		    if (tDao != null)
			tDao.close();
		}
		
		return rs;
	}

	/**
	 * Retorna a lista para visualização em combobox das sub-turmas referentes a turma atual 
	 * selecionada, que o docente que está logado dá aula.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/AulaExtra/_form.jsp</li>
	 *      <li>sigaa.war/ava/TarefaTurma/_form.jsp</li>
	 *      <li>sigaa.war/ava/TarefaTurma/_formEditar.jsp</li>
	 *  </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
   public List<SelectItem> getSubTurmasCombo() throws DAOException {
		return toSelectItems(getSubTurmasDocente(), "id", "descricaoSemDocente");
	}
	
	/**
	 * Retorna a lista de sub-turmas da turma atual para
	 * as quais o docente que está logado dá aula.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/FrequenciaAluno/subturmas.jsp</li>
	 *      <li>sigaa.war/ava/subturmas.jsp</li>
	 *      <li>sigaa.war/ava/consolidacao/subturmas.jsp</li>
	 *  </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> getSubTurmasDocente() throws DAOException {
		
		if (permissaoBean != null && permissaoBean.getPermissaoUsuario() != null && permissaoBean.getPermissaoUsuario().isDocente()){
			return turma.getSubturmas();
		} else if (docente) {
			List<Turma> turmas = new ArrayList<Turma>();
			List<Turma> subturmas = turma.getSubturmas();
			DocenteTurmaDao dao = getDAO(DocenteTurmaDao.class);
			
			Integer idServidor = getServidorUsuario() != null ? getServidorUsuario().getId() : null;
			Integer idDocenteExterno = getUsuarioLogado().getDocenteExterno() != null ? getUsuarioLogado().getDocenteExterno().getId() : null;
			
			for (Turma turma : subturmas) {
				List<DocenteTurma> dt = dao.findAllByDocenteTurma(idServidor, idDocenteExterno, turma.getId());
				if (isNotEmpty(dt)) turmas.add(turma);
			}
			return turmas;
		}
		
		return null;
	}
	
	/**
	 * Retorna a lista de sub-turmas da turma atual. Utilizado para o usuário visualizar as estatísticas da turma.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/subturmas_estatisticas.jsp</li>
	 *  </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> getSubturmas() throws DAOException {
		
		if (docente) {
			List<Turma> turmas = new ArrayList<Turma>();
			List<Turma> subturmas = turma.getSubturmas();
			DocenteTurmaDao dao = getDAO(DocenteTurmaDao.class);
			
			Integer idServidor = getServidorUsuario() != null ? getServidorUsuario().getId() : null;
			Integer idDocenteExterno = getUsuarioLogado().getDocenteExterno() != null ? getUsuarioLogado().getDocenteExterno().getId() : null;
			
			for (Turma turma : subturmas) {
				List<DocenteTurma> dt = dao.findAllByDocenteTurma(idServidor, idDocenteExterno, turma.getId());
				if (isNotEmpty(dt)) turmas.add(turma);
			}
			return turmas;
		} else
			return turma.getSubturmas();
	}
	
	/**
	 * Inicializa a turma selecionada, criando um mural para ela, caso não exista.
	 * @throws DAOException
	 */
	private void preparaEntrada() throws DAOException{
		
		TurmaDao dao = getDAO(TurmaDao.class);
		TurmaVirtualDao tvDao = getDAO(TurmaVirtualDao.class);

		resetData();

		Integer idTurma = getParameterInt("idTurma");
		if (idTurma == null)
			idTurma = turma.getId();
		
		turma = dao.findByPrimaryKey(idTurma, Turma.class);
		turma.setHorarios(dao.findHorariosByTurma(turma));
		turma.setDocentesTurmas(CollectionUtils.toHashSet(getDocentesTurma()));
		
		
		List <Turma> subTurmas = turma.getSubturmas();
		
		if (subTurmas != null) {
			subTurmas = dao.findSubturmasByTurmaFetchDocentes(turma);
			for (Turma st:subTurmas) {
				dao.refresh(st.getSituacaoTurma());
			}
			turma.setSubturmas(subTurmas);
		}
		if (turma.getCurso() != null)
			dao.refresh(turma.getCurso());

		if (turma.getPolo() != null) {
			dao.refresh(turma.getPolo());
			turma.getPolo().getCidade().getNomeUF();
		}
		Unidade unidade = dao.findByPrimaryKey(turma.getDisciplina().getUnidade().getId(), Unidade.class);
		turma.getDisciplina().setUnidade(unidade);
		
		// Inicializa os dados necessários para exibir a descrição da turma.
		turma.getDescricao();
		
		// Inicializa os dados do programa da disciplina da turma.
		turma.getDisciplina().getPrograma();
		
		turmasSemelhantes = null;
		
		Forum mural = getMural();
		if (mural == null) {
			mural = new Forum();
			mural.setData(new Date());
			mural.setTitulo(turma.getDisciplina().getCodigoNome());
			mural.setDescricao(turma.getDescricao());
			mural.setTipo(Forum.MURAL);
			mural.setUsuario(getUsuarioLogado());
			mural.setTurma(turma);
			mural.setTopicos(false);

			try {
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(mural);
				mov.setCodMovimento(ArqListaComando.CADASTRAR);
				prepareMovimento(ArqListaComando.CADASTRAR);
				executeWithoutClosingSession(mov);
			} catch (Exception e) {
				addMensagemErro(e.getMessage());
			}
		}
		
		if (!hasErrors()){
			
			discente = false;
			docente = false;
			permissao = false;
			
			// Verifica se o usuário é docente desta turma
			if (getUsuarioLogado().getVinculoAtivo().isVinculoServidor()) {
				if (!isEmpty(subTurmas)) {
					docente = tvDao.isDocenteSubTurma(getUsuarioLogado().getServidorAtivo(), turma());
				} else {
					docente = tvDao.isDocenteTurma(getUsuarioLogado().getServidorAtivo(), turma());
				}
			}
			
			// Verifica se o usuário é docente externo desta turma.
			if (getUsuarioLogado().getVinculoAtivo().isVinculoDocenteExterno()) {
				if (!isEmpty(subTurmas)) {
					docente = tvDao.isDocenteExternoSubTurma(getUsuarioLogado().getDocenteExterno(), turma());
				} else {
					docente = tvDao.isDocenteExternoTurma(getUsuarioLogado().getDocenteExterno(), turma());
				}
			}
			
			// Verifica se o usuário é chefe do departamento
			if( !docente && getAcessoMenu() != null && getAcessoMenu().isChefeDepartamento() 
					&& turma.getDisciplina().getUnidade().getId() == getUsuarioLogado().getServidorAtivo().getUnidade().getId() ){
				discente = true;
			}
			
			//Verifica se o usuário é coordenador e pode ter acesso à turma virtual
			if( !docente && OperacaoTurmaValidator.verificarCoordenadores(turma) ) {
				discente = true;
			}

			// Verifica se o usuário é Gestor Técnico e a turma é a mesma da unidade do usuário.
			if( !docente && getAcessoMenu() != null && (getAcessoMenu().isTecnico() && turma.getDisciplina()
					.getUnidade().getId() == getUsuarioLogado().getVinculoAtivo().getUnidade().getId()) ){
				discente = true;
			}
						
			// Verifica se o usuário é aluno desta turma.
			if (getUsuarioLogado().getVinculoAtivo().isVinculoDiscente()) {
				if (!isEmpty(subTurmas)) {
					discente = tvDao.isDiscenteSubTurma(getUsuarioLogado().getDiscenteAtivo().getDiscente(), turma());
				} else {
					discente = tvDao.isDiscenteTurma(getUsuarioLogado().getDiscenteAtivo().getDiscente(), turma());
				}
			}
			
			// Verifica se o usuário é do DAE ou PPG.
			if( !docente && getAcessoMenu() != null && (getAcessoMenu().isDae() || getAcessoMenu().isPpg() || getAcessoMenu().isMedio()) ){
				discente = true;
			}
			
			if ( permissaoBean == null )
				permissaoBean = getMBean ("permissaoAva");
			
			// Busca novamente a permissão na entrada da turma, para verificar se foi modificada a permissão durante a sessão do usuário.
			permissaoBean.setJaBuscado(false);
			
			// Verifica se o usuário tem PermissaoAVA
			if (permissaoBean.getPermissaoUsuario() != null)
				permissao = true;

			// Verifica se o docente publicou a turma para acesso público.
			ConfiguracoesAva conf = tvDao.findConfiguracoes(turma);
			boolean permiteAcessoPublico = conf == null ? false : conf.isPermiteVisualizacaoExterna();
			
			// Checa se o usuário tem permissão para acessar a turma.
			if (!discente && !docente && !permissao && !permiteAcessoPublico)
				addMensagem(MensagensTurmaVirtual.TURMA_RESTRITA_A_PARTICIPANTES);
			
			if (!hasErrors()){
				// Se conseguiu entrar, registra o acesso se for discente.
				registrarLogAcessoDiscenteTurmaVirtual(Turma.class.getName(), 0, getParameterInt("idTurma", 0));
				setSubSistemaAtual(SigaaSubsistemas.PORTAL_TURMA);
			}
		}
	}
	
	/**
	 * Exibe a listagem com as Turmas Virtuais Abertas.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *  </ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciarTurmasAbertas() {
		setAcessouPublicacao(false);
		PlanoCursoMBean bean = getMBean("planoCurso");
		bean.setAcessoTurmasAbertas(true);
		return forward(LISTA_TURMAS_ABERTAS);
	}	
	
	/**
	 * Exibe a listagem com as Turmas Virtuais Abertas para o acesso a publicação da turma virtual. 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *  </ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciarPublicacaoTurma() {
		setAcessouPublicacao(true);
		return forward(LISTA_TURMAS_ABERTAS);
	}
	
	/**
	 * Exibe a listagem de turmas virtuais do docente logado 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *  </ul>
	 * @return
	 */
	public String listarTurmasVirtuais(){
		return forward(LISTA_TURMAS_ANTERIORES);	
	}
	
	/**
	 * Inicializa a turma e exibe o formulário para que o aluno responda à tarefa selecionada.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/portais/discente/discente.jsp</li>
	 *  </ul>
	 * @return
	 * @throws ArqException
	 */
	public String visualizarTarefa () throws ArqException{

		preparaEntrada();
		
		if (!hasErrors()){
			RespostaTarefaTurmaMBean respostaBean = getMBean("respostaTarefaTurma");
			return respostaBean.enviarTarefa();
		}
		return null;
	}
	
	/**
	 * Inicializa a turma e exibe o formulário para que o aluno responda ao questionario selecionado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/portais/discente/discente.jsp</li>
	 *  </ul>
	 * @return
	 * @throws ArqException
	 */
	public String visualizarQuestionario () throws ArqException{

		preparaEntrada();
		
		if (!hasErrors()){
			QuestionarioTurmaMBean qBean = getMBean("questionarioTurma");
			return qBean.iniciarResponderQuestionario();
		}
		return null;
	}
	
	/**
	 * Exibe a listagem dos participantes da turma em modo de impressão.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/participantes.jsp</li>
	 *  </ul>
	 * @return
	 */
	public String imprimirParticipantes (){
		imprimir = true;
		return forward(PAGINA_IMPRIMIR_PARTICIPANTES);
	}
	
	/**
	 * Abre a janela para impressão da listagem de aulas da turma.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/index.jsp</li>
	 *  </ul>
	 * @return
	 */
	public String imprimirAulas() {
		imprimir = true;
		return forward(PAGINA_IMPRIMIR_AULAS);
	}
	
	/**
	 * Direciona o usuário para a página inicial da turma virtual.
	 * <br />
	 * Método não invocado por JSPs.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String retornarParaTurma () throws DAOException {
		
		clearCacheTopicosAula();
		
		exibirTopico (null);
		return forward("/ava/index.jsf");
	}
	
	/**
	 * Exibe somente o tópico selecionado pelo usuário ou todos os tópicos, caso não seja passado um id.
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/aulas.jsp</li>
	 *  </ul>
	 * @param e
	 * @throws DAOException 
	 */
	public String exibirTopico () throws DAOException {
		estiloVisualizacaoTopicos = getParameterBoolean("paginados") ? ConfiguracoesAva.ESTILO_TOPICOS_PAGINADOS : ConfiguracoesAva.ESTILO_TOPICOS_LISTA;
		
		clearCacheTopicosAula();
		
		configurarTopicos(false);
		exibirTopico (null);
		return forward("/ava/index.jsf");
	}
	
	/**
	 * Exibe somente o tópico selecionado pelo usuário
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/aulas.jsp</li>
	 *  </ul>
	 * @param e
	 * @throws DAOException 
	 */
	public void exibirTopico (ActionEvent e) throws DAOException {
		
		int id = topicoSelecionado.getId();
		
		if (id > 0){
			TopicoAulaMBean tABean = getMBean ("topicoAula");
			topicoSelecionado.setId(id);
			
			int indice = tABean.getAulas().indexOf(topicoSelecionado);
			
			if (indice >= 0){
				topicoSelecionado = new TopicoAula();
				BeanUtils.copyProperties(tABean.getAulas().get(indice), topicoSelecionado);
				
				if (indice < tABean.getAulas().size() -1)
					idProximoTopico = tABean.getAulas().get(indice + 1).getId();
				else
					idProximoTopico = 0;
				
				if (indice > 0)
					idTopicoAnterior = tABean.getAulas().get(indice - 1).getId();
				else
					idTopicoAnterior = 0;
				
				return;
			}
		}
		
		// Se chegou aqui, não exibe um tópico específico.
		topicoSelecionado = new TopicoAula();
		idProximoTopico = 0;
		idTopicoAnterior = 0;
	}
	
	/**
	 * Visualizar o parecer acadêmico a respeito da necessidade educacional especial do aluno.
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/ava/nee/link_parecer_nee.jsp</li>
	 * <li>/sigaa.war/ava/nee/panel_parecer_nee.jsp</li>
	 * </ul>
	 * 
	 * @param evento
	 * @throws DAOException 
	 */
	public void visualizarParecerNee(ActionEvent e) throws DAOException {

		int idDiscenteNee = getParameterInt("idDiscente_nee",0);
		
		NeeDao dao = getDAO(NeeDao.class);
		
		for (SolicitacaoApoioNee s : dao.findSolicitacaoApoioNeeByDiscente(idDiscenteNee)) {
			parecerNeeDiscenteSelecionado = s;
		}
		parecerNeeDiscenteSelecionado.setDiscente(getDAO(DiscenteDao.class).findByPK(idDiscenteNee));
		parecerNeeDiscenteSelecionado.getTiposNecessidadeSolicitacaoNee().addAll(
				dao.findNecessidadesEspeciaisBySolicitacaoNee(parecerNeeDiscenteSelecionado.getId()));
	
	}
	
	/**
	 * Retorna a String com a hora atual do sistema.
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @param evento
	 * @throws DAOException 
	 */
	public String getHora () {
		SimpleDateFormat sdf =  new SimpleDateFormat("H:mm:ss");
		String res = sdf.format(new Date());
		return res;
	}
	
	/**
	 * Indica ao mbean que as páginas não estão mais em modo de impressão.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>sigaa.war/ava/participantes_impressao.jsp</li>
	 *  </ul>
	 * @return
	 */
	public String getConcluirImpressao (){
		imprimir = false;
		return null;
	}
	
	
	/**
	 * Permite edição dos tópicos de aula da turma.
	 * 
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/ava/topico_aula.jsp</li>
	 * </ul>
	 * 
	 * @param evento
	 * @throws DAOException 
	 */
	public void ativarEdicao() {
		edicaoAtiva = !edicaoAtiva;
	}


	public boolean isAcessouPublicacao() {
		return acessouPublicacao;
	}

	public void setAcessouPublicacao(boolean acessouPublicacao) {
		this.acessouPublicacao = acessouPublicacao;
	}

	public Integer getIdDepto() {
		return idDepto;
	}

	public void setIdDepto(Integer idDepto) {
		this.idDepto = idDepto;
	}

	public Integer getIdCentro() {
		return idCentro;
	}

	public void setIdCentro(Integer idCentro) {
		this.idCentro = idCentro;
	}

	public String getPalavraChave() {
		return palavraChave;
	}

	public void setPalavraChave(String palavraChave) {
		this.palavraChave = palavraChave;
	}

	public List<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(List<Turma> turmas) {
		this.turmas = turmas;
	}	
	
	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}
	
	public boolean isAcessoDocente() {
		return acessoDocente;
	}

	public void setAcessoDocente(boolean acessoDocente) {
		this.acessoDocente = acessoDocente;
	}
	
	public boolean isDocente() {
		return docente;
	}
	
	public void setDocente(boolean docente) {
		this.docente = docente;
	}

	public boolean isDiscente() {
		return discente;
	}

	public void setDiscente(boolean discente) {
		this.discente = discente;
	}

	public boolean isImprimir() {
		return imprimir;
	}

	public void setImprimir(boolean imprimir) {
		this.imprimir = imprimir;
	}

	public int getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(int tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public void setDocenciaAssistida(List<PlanoDocenciaAssistida> docenciaAssistida) {
		this.docenciaAssistida = docenciaAssistida;
	}

	public TopicoAula getTopicoSelecionado() {
		return topicoSelecionado;
	}

	public void setTopicoSelecionado(TopicoAula topicoSelecionado) {
		this.topicoSelecionado = topicoSelecionado;
	}

	public int getIdProximoTopico() {
		return idProximoTopico;
	}

	public void setIdProximoTopico(int idProximoTopico) {
		this.idProximoTopico = idProximoTopico;
	}

	public int getIdTopicoAnterior() {
		return idTopicoAnterior;
	}

	public void setIdTopicoAnterior(int idTopicoAnterior) {
		this.idTopicoAnterior = idTopicoAnterior;
	}

	public boolean isPermissao() {
		return permissao;
	}

	public void setPermissao(boolean permissao) {
		this.permissao = permissao;
	}

	public MatriculaComponente getMatriculaSelecionada() {
		return matriculaSelecionada;
	}

	public void setMatriculaSelecionada(MatriculaComponente matriculaSelecionada) {
		this.matriculaSelecionada = matriculaSelecionada;
	}

	public SolicitacaoApoioNee getParecerNeeDiscenteSelecionado() {
		return parecerNeeDiscenteSelecionado;
	}

	public void setParecerNeeDiscenteSelecionado(SolicitacaoApoioNee parecerNeeDiscenteSelecionado) {
		this.parecerNeeDiscenteSelecionado = parecerNeeDiscenteSelecionado;
	}

	public int getEstiloVisualizacaoTopicos() {
		return estiloVisualizacaoTopicos;
	}

	public void setEstiloVisualizacaoTopicos(int estiloVisualizacaoTopicos) {
		this.estiloVisualizacaoTopicos = estiloVisualizacaoTopicos;
	}
	
	public boolean isExibirTopicosPaginados () {
		return estiloVisualizacaoTopicos == ConfiguracoesAva.ESTILO_TOPICOS_PAGINADOS;
	}
	
	public List<DataAvaliacao> getDatasAvaliacoes() {
		return datasAvaliacoes;
	}

	public void setDatasAvaliacoes(List<DataAvaliacao> datasAvaliacoes) {
		this.datasAvaliacoes = datasAvaliacoes;
	}

	public boolean isEdicaoAtiva() {
		return edicaoAtiva;
	}

	public void setEdicaoAtiva(boolean edicaoAtiva) {
		this.edicaoAtiva = edicaoAtiva;
	}

	public void setGrupos(List<GrupoDiscentes> grupos) {
		this.grupos = grupos;
	}

	/** Retorna grupos de discentes da turma virtual. */
	public List<GrupoDiscentes> getGrupos() throws DAOException {
		if ( grupos == null )
			grupos = GrupoDiscentesHelper.carregarGrupos(turma);
		return grupos;
	}

	public int getModoExibicao() {
		return modoExibicao;
	}

	public void setModoExibicao(int modoExibicao) {
		this.modoExibicao = modoExibicao;
	}

	/** Informa se o usuário está visualizando a turma em modo clássico. */
	public boolean getModoClassico(){
		return modoExibicao == MODO_CLASSICO;
	}

	/** Informa se o usuário está visualizando a turma em modo mobile. */
	public boolean getModoMobile(){
		return modoExibicao == MODO_MOBILE;
	}
	
	public String getEnderecoServidorVideo () {
		return ParametroHelper.getInstance().getParametro(ParametrosTurmaVirtual.ENDERECO_SERVIDOR_VIDEO);
	}

	/** Define o modo de exibição para mobile.
	 * 
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/ava/index.jsp</li>
	 * </ul>
	 * @throws NegocioException 
	 */
	public String alterarParaModoMobile() throws DAOException {
		try {
			setModoExibicao(MODO_MOBILE);
			LoginMobileTouchMBean lmBean = (LoginMobileTouchMBean) getMBean("loginMobileTouch");

			//Caso o usuário atual tenha apenas um vínculo, segue direto para o portal.
			if (ValidatorUtil.isNotEmpty(getUsuarioLogado().getVinculos()) && getUsuarioLogado().getVinculos().size() == 1) {
				return lmBean.acessarPortalMobile(getUsuarioLogado(), getCurrentRequest());
				
			} else {
				return forward ("/mobile/touch/vinculos.jsf");
				
			}
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		return null;
	}
	
	/** Verifica se a turma virtual está sendo acessada por um dispositivo móvel
	 * 
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/ava/topico_aula.jsp</li>
	 * </ul>
	 * @throws NegocioException 
	 */
	public boolean isAcessoMobile() {
		String uagent = getCurrentRequest().getHeader("User-Agent").toLowerCase();
		boolean mobile = AmbienteUtils.isMobileUserAgent(uagent);
		return mobile;
	}
	
	public void setUltimasMensagens(List<ForumGeralMensagem> ultimasMensagens) {
		this.ultimasMensagens = ultimasMensagens;
	}
	
	
}
