/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * created on 31/01/2008
 * 
 */
package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.negocio.SigaaHelper;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.ForumDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.ForumMensagem;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.forum.dao.GestorForumCursoDao;
import br.ufrn.sigaa.ava.jsf.CadastroTurmaVirtual;
import br.ufrn.sigaa.ava.jsf.ControllerTurmaVirtual;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroAva;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.jsf.HistoricoMBean;
import br.ufrn.sigaa.ensino.medio.dao.ForumMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.ForumMensagemMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.ForumMedio;
import br.ufrn.sigaa.ensino.medio.dominio.ForumMensagemMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.mensagens.MensagensPortalCoordenadorStricto;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.portal.jsf.PortalCoordenacaoStrictoMBean;

/**
 * Controlador respons�vel por postar mensagens no f�rum de turma das s�ries de ensino m�dio.
 * 
 * @author Rafael Gomes
 */
@Component("forumMensagemMedio")
@Scope("request")
public class ForumMensagemMedioMBean extends CadastroTurmaVirtual<ForumMensagemMedio> {

	/** Arquivo a ser anexado ao t�pico do forum e enviado por email.*/
	private UploadedFile arquivo;
	/** Identificador do registro do Forum selecionado. */
	private int idForumSelecionado;
	
	/** Indica o t�pico de aula selecionado.  */
	private int idTopicoAula;
	
	/** Indica se os alunos receber�o e-mails de notifica��o da cria��o de forum e t�picos.*/
	private boolean notificar;
	/** Identificador do registro do Forum selecionado. */
	private int forumSelecionado;
	/** Identificador do registro da Mensagem selecionada no Forum. */
	private int idForumMensagemSelecionado;
	/** Identificador do discente logado.*/
	private int discenteLogado;

	/** Listagem de f�runs da turmaSerie.*/
	private List<ForumMensagemMedio> listaForunsPorTurmaSerie;
	/** Indica se o forum selecionado pertence a uma TurmaSerie.*/
	private boolean eUmForumDeTurmaSerie;
	/** vari�vel utilizada para manter o t�tulo do forum da TurmaSerie.*/
	private String tituloTurmaSerieDiscutidaAtualmente;
	
	/** F�rum ao qual a mensagem pertence. */
	private ForumMedio forum;
	/** vari�vel utilizada para manter o conte�do do forum do curso.*/
	private String conteudo;
	
	/**Constante para controle da fun��o validacaoTextArea.*/
	private static final int CONTROLE_VALIDACAO = 87;

	/** Constante de controle da pagina��o de mensagens. */
	private static final int CONTROLE_PAGINACAO = 1;
	
	/*
	 * IN�CIO PAGINA��O
	 */
	/** Lista de mensagens do f�rum de uma p�gina */
	private List<ForumMensagemMedio> mensagensPaginadas;
	/** Total de mensagens da p�gina */
	private Integer totalRegistros;
	/** N�mero da primeira mensagem de uma p�gina. */
	private int primeiroRegistro;
	/** Quantidade de mensagens de cada p�gina. */
	private int quantRegistrosPagina = 20;
	/** Array que guarda as p�ginas. */
	private Integer[] pages = new Integer[0];
	/** P�gina Atual. */
	private int paginaAtual;
	
	/** Se est� criando um novo t�pico ou respondendo um t�pico antigo. */
	private boolean cadastroTopico;
	/** Cole��o de turmaSerie que um coordenador de curso pode cadastrar um f�rum.*/
	private Collection<TurmaSerie> turmasSerie;
	
	/** Se o su�rio est� ativo */
	private boolean usuarioAtivo = true;
	/** Lista de mensagens de Portal do discente */
	private List<ForumMensagemMedio> forumMensagemPortalDiscente;
	/** objeto respons�vel por pela turmaS�rie regular do discente*/
	private TurmaSerie turmaSerie = new TurmaSerie();
	
	/**
	 * Carrega os dados por demanda de acordo com o ID informado. 
	 * 
	 * @throws DAOException
	 */
	private void loadMensagensPaginadasForumCursos(int idMensagem)
			throws DAOException {

		ForumMedioDao dao = getDAO(ForumMedioDao.class);
		
		mensagensPaginadas = dao.findMensagensByTopico(idMensagem, primeiroRegistro, quantRegistrosPagina);
		setarMatriculasDiscentesPostaramMensagens();
		
		totalRegistros = dao.countMensagensForumCursos(idMensagem);

		listaForunsPorTurmaSerie = mensagensPaginadas;
		listagem = mensagensPaginadas;

		paginaAtual = (totalRegistros / quantRegistrosPagina)
				- ((totalRegistros - primeiroRegistro) / quantRegistrosPagina)
				+ CONTROLE_PAGINACAO;
		int totalPages = (totalRegistros / quantRegistrosPagina)
				+ ((totalRegistros % quantRegistrosPagina != 0) ? CONTROLE_PAGINACAO : 0);
		pages = new Integer[totalPages];
		for (int i = 0; i < totalPages; i++) {
			pages[i] = i + CONTROLE_PAGINACAO;
		}
	}

	/**
	 * 	Seta as  matr�culas dos v�nculos ativos do discente que postou a mensagem no 
	 *  f�rum (caso o usu�rio seja um discente).
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private List<ForumMensagemMedio> setarMatriculasDiscentesPostaramMensagens() throws HibernateException, DAOException {
		ForumDao dao = getDAO(ForumDao.class);
		
		Curso curso = SigaaHelper.getCursoAtualCoordenacao();
		
		for (ForumMensagemMedio forumMensagemMedio : mensagensPaginadas) {
			List<Long> matriculas = dao.findMatriculasDiscentePostaramMensagens(forumMensagemMedio.getUsuario().getId(), curso != null ? curso.getId() : null );
			forumMensagemMedio.setMatriculas(matriculas);
		}
		
		return mensagensPaginadas;
	}

	/**
	 * Cria novo f�rum caso o(s) docente(s) permita(m).<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ava/ForumMensagem/listar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/listar.jsp</li>
	 * </ul>
	 */
	@Override
	public String novo() {
		TurmaVirtualMBean turmaBean = getMBean("turmaVirtual");
		ConfiguracoesAva configuracoes = turmaBean.getConfig();
		if (configuracoes != null && !configuracoes.isPermiteAlunoCriarForum()
				&& !turmaBean.isPermissaoDocente()) {
			addMensagem(MensagensTurmaVirtual.PERMISSAO_NEGACA_DISCENTE_CRIAR_FORUM);
			return null;
		} else {
			return super.novo();
		}
	}

	/**
	 * Exibe as Mensagens da p�ginas  
	 * 
	 * @param idMensagem
	 * @return
	 * @throws DAOException
	 */
	private List<ForumMensagemMedio> getMensagensPaginadas(int idMensagem) throws DAOException {
		loadMensagensPaginadasForumCursos(idMensagem);
		return mensagensPaginadas;
	}

	/**
	 * Abre a mensagem selecionada.
	 * 
	 * @param firstRow
	 * @throws DAOException
	 */
	private void page(int firstRow) throws DAOException {
		cadastroTopico = false;
		this.primeiroRegistro = firstRow;
		loadMensagensPaginadasForumCursos(getIdForumMensagemSelecionado());
	}

	/**
	 * Vai para a primeira p�gina.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
     * <ul>
     * 		<li>/sigaa.war/ava/ForumMensagem/mostrar.jsp</li>
	 * 		<li>/sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/mostrar.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public void pageFirst() throws DAOException {
		page(0);
	}

	/**
	 * Serve para ir a pr�xima p�gina.<br/><br/>
	 *  
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ava/ForumMensagem/mostrar.jsp</li>
	 * 		<li>/sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/mostrar.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public void pageNext() throws DAOException {
		page(primeiroRegistro + quantRegistrosPagina);
	}

	/**
	 * Vai para a p�gina anterior.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ava/ForumMensagem/mostrar.jsp</li>
	 * 		<li>/sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/mostrar.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public void pagePrevious() throws DAOException {
		page(primeiroRegistro - quantRegistrosPagina);
	}

	/**
	 * Vai direto para a �ltima p�gina.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ava/ForumMensagem/mostrar.jsp</li>
	 * 		<li>/sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/mostrar.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public void pageLast() throws DAOException {
		page(totalRegistros
				- ((totalRegistros % quantRegistrosPagina != 0) ? totalRegistros
						% quantRegistrosPagina
						: quantRegistrosPagina));
	}

	public void setDataList(List<ForumMensagemMedio> mensagensPaginadasForumCursos) {
		this.mensagensPaginadas = mensagensPaginadasForumCursos;
	}

	public int getTotalRows() {
		return totalRegistros;
	}

	public int getTotalPages() {
		return pages.length;
	}

	public void setTotalRows(int totalRows) {
		this.totalRegistros = totalRows;
	}

	public int getFirstRow() {
		return primeiroRegistro;
	}

	public void setFirstRow(int firstRow) {
		this.primeiroRegistro = firstRow;
	}

	/**
	 * Quantidade de registros por p�gina.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ava/ForumMensagem/mostrar.jsp</li>
	 * 		<li>/sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/mostrar.jsp</li>
	 * </ul>
	 * @return
	 */
	public int getRowsPerPage() {
		return quantRegistrosPagina;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.quantRegistrosPagina = rowsPerPage;
	}

	public Integer[] getPages() {
		return pages;
	}

	public void setPages(Integer[] pages) {
		this.pages = pages;
	}

	/**
	 * P�gina atual.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ava/ForumMensagem/mostrar.jsp</li>
	 * 		<li>/sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/mostrar.jsp</li>
	 * </ul>
	 * @return
	 */
	public int getCurrentPage() {
		return paginaAtual;
	}

	public void setCurrentPage(int currentPage) {
		this.paginaAtual = currentPage;
	}

	/**
	 * Zera a pagina��o, como se n�o houvesse mensagens.
	 */
	private void clearPaginacao() {
		paginaAtual = 0;
		primeiroRegistro = 0;
		totalRegistros = 0;
	}

	/**
	 * FIM PAGINA��O
	 */

	/**
	 * Construtor padr�o
	 */
	public ForumMensagemMedioMBean() {
		
	}

	public ForumMedio getForum() {
		return forum;
	}

	public void setForum(ForumMedio forum) {
		this.forum = forum;
	}

	public List<ForumMensagemMedio> getListaForumMensagens(){
		return listaForunsPorTurmaSerie;
	}
	
	/**
	 * Retorna a lista de f�runs por curso
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/ForumMensagem/listar.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<ForumMensagemMedio> getListaForunsPorTurmaSerie() throws DAOException {
		if (getPaginacao().getPaginaAtual() != paginaAtual) {
			getMensagens();
			paginaAtual = getPaginacao().getPaginaAtual();
		}
		
		return listaForunsPorTurmaSerie;
	}

	public void setListaForunsPorCurso(List<ForumMensagemMedio> listaForunsPorCurso) {
		this.listaForunsPorTurmaSerie = listaForunsPorCurso;
	}

	public int getForumSelecionado() {
		return forumSelecionado;
	}

	public void setForumSelecionado(int forumSelecionado) {
		this.forumSelecionado = forumSelecionado;
	}

	public boolean isNotificar() {
		return notificar;
	}

	public void setNotificar(boolean notificar) {
		this.notificar = notificar;
	}

	/**
	 * Vai para a listagem de mensagens de um t�pico.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ava/ForumMensagem/listar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/listar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/coordenador.jsp</li>
	 * 		<li>/sigaa.war/lato/coordenador.jsp</li>
	 * 		<li>/sigaa.war/portais/discente/discente.jsp</li>
	 * 		<li>/sigaa.war/stricto/coordenacao.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String mostrar() throws DAOException {

		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO();
		
			object = new ForumMensagemMedio();
	
			Integer idForumMensagem = getParameterInt("idForumMensagem");
			registrarAcao(dao.findByPrimaryKey(idForumMensagem, ForumMensagem.class).getTitulo(), EntidadeRegistroAva.FORUM_MENSAGEM, AcaoAva.ACESSAR);
			
			if (idForumMensagem != null)
				setIdForumMensagemSelecionado(idForumMensagem);
	
			clearPaginacao();
			
			listagem = getMensagensPaginadas(getIdForumMensagemSelecionado());
			return forward("/medio/" + getClasse().getSimpleName() + "/mostrar.jsp");
		} finally {
			if (dao != null)
				dao.close();
		}
		
	}
	
	/**
	 * Lista de t�picos de um F�rum que n�o seja um f�rum de turmaSerie. <br/><br/>
	 * 
	 * M�todo n�o invocado por JSPs. � public porque � chamado pela classe MenuTurmaMBean.
	 */
	public String listar(int idMural) {
		idForumSelecionado = idMural;
		return listar ();
	}

	/**
	 * Lista de t�picos de um F�rum que n�o seja um f�rum de Curso.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ava/Forum/listar.jsp</li>
	 * 		<li>/sigaa.war/ava/ForumMensagem/novo.jsp</li>
	 * 		<li>/sigaa.war/ava/aulas.jsp</li>
	 * 		<li>/sigaa.war/ava/menu.jsp</li>
	 * 		<li>/sigaa.war/cv/ForumComunidade/listar.jsp</li>
	 * </ul>
	 */
	@Override
	public String listar() {

		if (!eUmForumDeTurmaSerie) {
			listagem = null;
			return forward("/medio/" + getClasse().getSimpleName() + "/listar.jsp");
		}

		return null;
	}

	/**
	 * Lista as mensagens do f�rum de um determinado curso.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ava/Forum/mensagens.jsp</li>
	 * 		<li>/sigaa.war/cv/ForumComunidade/mensagens.jsp</li>
	 * </ul>
	 */
	@Override
	public List<ForumMensagemMedio> lista() {
		Integer id = getParameterInt("id",0);
		
		if (id == 0)
		{
			if (idForumSelecionado != 0)
				id = idForumSelecionado;
			else 
				id = getForum().getId();
			
		}
		setForumSelecionado(id);

		try {
			
			forum = getGenericDAO().findByPrimaryKey(id, ForumMedio.class);
			return (List<ForumMensagemMedio>) getDAO(ForumMedioDao.class).findListaMensagensForumByIDForum(getForumSelecionado());
			
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return new ArrayList<ForumMensagemMedio>();
	}

	/**
	 * Ao cancelar a inser��o de uma mensagem no f�rum, o usu�rio ser� redirecionado
	 * para a p�gina de listar.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ava/Forum/mensagens.jsp</li>
	 * 		<li>/sigaa.war/ava/ForumMensagem/mostrar.jsp</li>
	 * 		<li>sigaa.war/cv/ForumComunidade/mensagens.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		listagem = null;
		return forward("/medio/" + getClasse().getSimpleName() + "/listar.jsf");
	}

	/**
	 * Lista todos os f�runs por curso. <br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/mostrar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/novo.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String voltarParaForumMensagensTurmas() throws DAOException {
		return listarForunsPorTurmaSerie();
	}

	/**
	 * M�todo respons�vel pela remo��o de um f�rum.
	 * 
	 * M�todo n�o chamado por jsp.
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String removerDaTurma () throws ArqException{
		remover();
		
		TurmaVirtualMBean turmaBean = getMBean ("turmaVirtual");
		return turmaBean.entrar();
	}
	
	/**
	 * Remove uma mensagem ou t�pico de um f�rum.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:/sigaa.war/ava/Forum/mensagens.jsp
	 * <ul>
	 * 		<li>/sigaa.war/ava/ForumMensagem/listar.jsp</li>
	 * 		<li>/sigaa.war/ava/ForumMensagem/mostrar.jsp</li>
	 * 		<li>/sigaa.war/cv/ForumComunidade/mensagens.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() {
		try {
	
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), getClasse());
	
			// Registra a tentativa de remo��o da mensagem do f�rum.
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.FORUM_MENSAGEM, AcaoAva.INICIAR_REMOCAO, object.getId());
			
			antesRemocao();
			prepare(ArqListaComando.DESATIVAR);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(object);
			mov.setCodMovimento(ArqListaComando.DESATIVAR);

			execute(mov);
			
			// Registra a remo��o da mensagem do f�rum.
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.FORUM_MENSAGEM, AcaoAva.REMOVER, object.getId());
			
			object = new ForumMensagemMedio();
			flash(" Mensagem do F�rum removido com sucesso.");

			if (getParameterBoolean("topico")) { // se for t�pico redireciona
				// pra a lista de t�picos
				listagem = (List<ForumMensagemMedio>) getDAO(ForumMedioDao.class).findListaMensagensForumByIDForum(getForumSelecionado());
				return forward("/ava/" + getClasse().getSimpleName()	+ "/listar.jsf");
			} else { // se for mensagem redireciona para a lista de mensagens de
				// um t�pico
				listagem = getMensagensPaginadas(getIdForumMensagemSelecionado());
				listagem.iterator().next();
				pageLast();
				return forward("/ava/" + getClasse().getSimpleName() + "/mostrar.jsf");
			}

		} catch (Exception e) {
			return tratamentoErroPadrao(e, "Existem registros associados ao que voc� est� tentando remover que inviabilizam sua remo��o");
		}

	}

	/**
	 * Verifica se o campo conte�do foi informado. Tornado-o campo obrigat�rio.<br/><br/>
	 * 
	 * N�o invocado por jsp
	 * 
	 */
	@Override
	public Specification getEspecificacaoCadastro() {
		return new Specification() {
			Notification notification = new Notification();

			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				ForumMensagem ref = (ForumMensagem) objeto;
				if (isEmpty(ref.getConteudo()))
						notification
							.addError("� obrigat�rio informar o conte�do da mensagem!");

				return !notification.hasMessages();
			}
		};
	}

	/**
	 * M�todo respons�vel por emitir o hist�rico do aluno.
	 * 
	 * M�todo chamado pela seguinte JSPs: /sigaa.war/graduacao/ForumMensagem/mostrar.jsp
	 * 
	 * @throws ArqException
	 * */
	public String emitirHistoricoAluno() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_MEDIO, SigaaPapeis.GESTOR_MEDIO);
			
		HistoricoMBean historicoMBean = getMBean("historico");
		Integer idDiscentePost = getParameterInt("idDiscentePost");
		
		Discente discentePost = getGenericDAO().findByPrimaryKey(idDiscentePost.intValue(), Discente.class);
		
		historicoMBean.setDiscente(discentePost);
		return historicoMBean.selecionaDiscente();
	}
	
	/**
	 * Cadastra uma mensagem para um F�rum de Turma Virtual e envia e-mail aos participantes.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:/sigaa.war/ava/ForumMensagem/novo.jsp
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String cadastrarTopico() throws ArqException, NegocioException {
		
		// Registra a tentativa de cadastrar um f�rum.
		registrarAcao(object.getTitulo(), EntidadeRegistroAva.FORUM_MENSAGEM, AcaoAva.INICIAR_INSERCAO);
		
		validacaoTextArea();
		
		cadastroTopico = true;
		ForumMensagemMedio cadObject = object;
		
		if (idTopicoAula == 0)
			object.setTopicoAula(null);
		else
			object.setTopicoAula(new TopicoAula(idTopicoAula));

		boolean ok = true;
		
		if (object.getTitulo().equals("")){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "T�tulo");
			ok = false;
		}
		
		if (object.getConteudo().equals("")){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Conte�do");
			ok = false;
		}
		
		if (!ok)
			return null;
		
		cadastrarMensagem();			
		flash("Cadastrado com sucesso.");
		
		notificarTurma("Novo T�pico de F�rum",
				"Um novo t�pico de discuss�o foi criado no f�rum da turma: "
						+ "<br><br>T�tulo: " + cadObject.getTitulo()
						+ "<br><br>" + cadObject.getConteudo().replaceAll("\\n", "<br>") + "<BR><BR>"
						+ "Postado por: " + getUsuarioLogado().getNome(), ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE, ControllerTurmaVirtual.DOCENCIA_ASSISTIDA );
		
		forum = getGenericDAO().findByPrimaryKey(getForumSelecionado(), ForumMedio.class);
		listagem = (List<ForumMensagemMedio>) getDAO(ForumMedioDao.class).findListaMensagensForumByIDForum(getForumSelecionado());
		
		if (paginaOrigem != null && paginaOrigem.equals("portalPrincipal")){
			TurmaVirtualMBean turmaBean = getMBean ("turmaVirtual");
			return turmaBean.retornarParaTurma();
		}
		
		return forward("/medio/" + getClasse().getSimpleName() + "/listar.jsf");
	}

	/**
	 *  Cadastro de mensagem de f�rum mais gen�rico.
	 * 
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	private void cadastrarMensagem() throws ArqException, NegocioException {

		validacaoTextArea();
		prepareMovimento(SigaaListaComando.CADASTRAR_TOPICO_FORUM_MEDIO);

		ForumMedio f = new ForumMedio();
		f.setId(getForumSelecionado());
		
		// Se est� cadastrando atrav�s da turma virtual, n�o selecionou um f�rum
		if (f.getId() == 0){
			TurmaVirtualMBean t = getMBean ("turmaVirtual");
			if (t.getMural() != null)
				f.setId(t.getMural().getId());
		}

		object.setForum(f);
		ForumMensagemMedio topico = new ForumMensagemMedio();

		if (getParameterInt("idForumMensagem") != null || cadastroTopico == false) {

			if (getParameterInt("idForumMensagem") != null)
				idForumMensagemSelecionado = getParameterInt("idForumMensagem");

			topico.setId(idForumMensagemSelecionado);
			object.setTopico(topico);
		} else if (cadastroTopico == true) {
			object.setTopico(null);
		}
		
		MovimentoCadastro movCad = new MovimentoCadastro();
		movCad.setObjMovimentado(object);
		movCad.setCodMovimento(SigaaListaComando.CADASTRAR_TOPICO_FORUM_MEDIO);
		execute(movCad);

		// Registra inser��o ou resposta de um t�pico
		if ( cadastroTopico )
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.FORUM_MENSAGEM, AcaoAva.INSERIR, object.getId());
		else
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.FORUM_MENSAGEM, AcaoAva.RESPONDER, object.getId());
			
		if (getParameterInt("idForumMensagem") != null || idForumMensagemSelecionado != 0) {
			// caso seja null � por que � um t�pico ao inv�s de mensagens

			if (getParameterInt("idForumMensagem") != null)
				idForumMensagemSelecionado = getParameterInt("idForumMensagem");

			listagem = getMensagensPaginadas(idForumMensagemSelecionado);
		}
	}

	/**
	 * Cadastrar mensagem em f�rum da Turma Virtual. � a resposta a algum t�pico.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ava/Forum/mensagens.jsp</li>
	 * 		<li>/sigaa.war/ava/ForumMensagem/mostrar.jsp</li>
	 * 		<li>/sigaa.war/ava/ForumMensagem/novo.jsp</li>
	 * 		<li>/sigaa.war/cv/ForumComunidade/mensagens.jsp</li>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/mostrar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/novo.jsp</li>
	 * </ul>
	 */
	public String responderTopicoForum() throws ArqException {
		
		
		if (!object.getTitulo().equals("") && !object.getConteudo().equals("")) {

			ForumMedioDao dao = null;
			
			try {
				dao = getDAO(ForumMedioDao.class);
				ForumMensagemMedio objOrig = object;
				ForumMensagemMedio topico = dao.findByPrimaryKey(idForumMensagemSelecionado, ForumMensagemMedio.class);
	
				if (getParameterInt("idForumMensagem") != null || idForumMensagemSelecionado != 0) {
	
					if (getParameterInt("idForumMensagem") != null)
						idForumMensagemSelecionado = getParameterInt("idForumMensagem");
	
	
					// Registra a tentativa de responder um t�pico.
					registrarAcao(topico.getTitulo(), EntidadeRegistroAva.FORUM_MENSAGEM, AcaoAva.INICIAR_RESPOSTA, forum.getId());
					
					// adiciona o t�pico 
					try {
						cadastrarMensagem();
					} catch (NegocioException e1) {
						e1.printStackTrace();
					}
	
					// envia e-mail para os participantes do t�pico
					List<String> emails = null;
					try {
						emails = dao.findEmailsByTopico(topico);
					} catch (DAOException e) {
						emails = new ArrayList<String>();
					}
					
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
					String data = sdf.format(object.getData());
					
					for (String email : emails) {
						MailBody mail = new MailBody();
						mail.setContentType(MailBody.TEXT_PLAN);
						mail.setAssunto("Resposta ao T�pico: " + topico.getTitulo() + " - " +turma().getDescricaoSemDocente() + " - " + data );
						mail.setFromName("F�rum SIGAA: " + getUsuarioLogado().getNome());
						mail.setEmail(email);
						
					    // Removendo coment�rios inseridos quando se copia e cola conte�dos de arquivos do Word.
						objOrig.setConteudo(StringUtils.removerComentarios(objOrig.getConteudo()));
						
						mail.setMensagem(objOrig.getConteudo());
						Mail.send(mail);
					}
					object = new ForumMensagemMedio();
					flash("Resposta cadastrada com sucesso. Participantes deste t�pico foram notificados via e-mail");
					listagem = getMensagensPaginadas(getIdForumMensagemSelecionado());
					pageLast();
					return forward("/ava/" + getClasse().getSimpleName()
							+ "/mostrar.jsp");
				}
				return forward("/ava/" + getClasse().getSimpleName() + "/mostrar.jsp");
			} finally {
				if (dao != null)
					dao.close();
			}
		} else {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "T�tulo e Conte�do");
			return forward("/ava/" + getClasse().getSimpleName() + "/mostrar.jsp");
		}
	}

	/**
	 * Apenas inst�ncia o ForumMensagem.<br/><br/>
	 * 
	 * N�o invocado por JSP.
	 * 
	 */
	@Override
	public void instanciar() {

		object = new ForumMensagemMedio();

		int idForumSelecionado = getForumSelecionado();

		try {

			ForumMedio f = getDAO(ForumMedioDao.class).findByPrimaryKey(
					idForumSelecionado, ForumMedio.class);

			object.setForum(f);
			object.setTopico(object);

		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
	}

	/**
	 * ************************
	 *	FORUM DE CURSOS 
	 * ************************
	 */
	
	/**
	 * Verifica se conte�do foi preenchido, caso n�o tenho sido. Retorna mensagem.<br/><br/>
	 * 
	 * N�o invocado por JSP.
	 * 
	 */
	public Specification getEspecificacaoCadastroRespostas() {
		return new Specification() {
			Notification notification = new Notification();

			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				ForumMensagem ref = (ForumMensagem) objeto;
				if (isEmpty(ref.getConteudo()) || ref.getConteudo().equals("")
						|| ref.getConteudo().equals(" ")) {
					notification
							.addError("� obrigat�rio informar o conte�do da mensagem!");
				}

				return !notification.hasMessages();
			}
		};
	}

	/**
	 * Ao cadastrar uma mensagem no f�rum h� a verifica��o se o t�tulo e conte�do foram informados.<br/><br/>
	 * 
	 * N�o invocado por JSPs.
	 * 
	 * @return
	 */
	public Specification getEspecificacaoCadastroTopicos() {
		return new Specification() {
			Notification notification = new Notification();

			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				ForumMensagemMedio ref = (ForumMensagemMedio) objeto;
				if (isEmpty(ref.getConteudo()))
					notification
							.addError("� obrigat�rio informar o conte�do da mensagem!");

				if (isEmpty(ref.getTitulo()))
					notification
							.addError("� obrigat�rio informar o t�tulo da mensagem!");

				return !notification.hasMessages();
			}
		};
	}

	/**
	 * Remove um t�pico com todas as suas mensagens.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/listar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/coordenador.jsp</li>
	 * 		<li>/sigaa.war/lato/coordenador.jsp</li>
	 * 		<li>/sigaa.war/stricto/coordenacao.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public String removerMensagensTurmaSerie() throws DAOException {
		try {
			getDAO(ForumMedioDao.class).removerTopicosComFilhos(
					getParameterInt("id"));
			flash("O T�pico e todas as mensagens do mesmo foram removidas com sucesso.");
		} catch (Exception e) {
			addMensagem(MensagensArquitetura.REMOCAO_OBJETO_ASSOCIADO);
			throw new DAOException(
					"Existem registros associados ao que voc� est� tentando remover");
		}

		return listarForunsPorTurmaSerie();
	}

	/**
	 * Remove uma mensagem de um t�pico.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/mostrar.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public String removerMensagensTopicos() throws DAOException {

		try {
			getDAO(ForumMedioDao.class).removerTopicosIndividuais(
					getParameterInt("id"));
			flash("Mensagem removida com sucesso.");

			clearPaginacao();

			listaForunsPorTurmaSerie = getMensagensPaginadas(getIdForumMensagemSelecionado());
			if (listaForunsPorTurmaSerie.size() >= 1) {
				tituloTurmaSerieDiscutidaAtualmente = listaForunsPorTurmaSerie.get(0).getTitulo();
				return irParaForum();
			} else {
				return listarForunsPorTurmaSerie();
			}

		} catch (Exception e) {
			addMensagem(MensagensTurmaVirtual.REMOVER_PRIMEIRA_MENSAGEM);
			return  null;
		}
	}

	/**
	 * Cadastra novo f�rum de curso.<br/><br/>
	 * M�todo chamado pela seguinte JSP: /sigaa.war/graduacao/ForumMensagem/listar.jsp
	 * @return
	 */
	public String novoForumTurmaSerie() {
		try {

			object = new ForumMensagemMedio();
			object.setTurmaSerie(true);
			
			int idForumSelecionado = getForumSelecionado();

			try {

				ForumMedio f = getDAO(ForumMedioDao.class).findByPrimaryKey(
						idForumSelecionado, ForumMedio.class);

				object.setForum(f);
				object.setTopico(object);

			} catch (DAOException e) {
				tratamentoErroPadrao(e);
			}

			prepareMovimento(SigaaListaComando.CADASTRAR_AVA);
			setOperacaoAtiva(SigaaListaComando.CADASTRAR_AVA.getId());
		
			return forward("/medio/" + getClasse().getSimpleName() + "/novo.jsp");
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		}
	}

	/**
	 * Verifica se o usu�rio logado � gestor do curso atual.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean isGestorForumCurso() throws DAOException{
		
		boolean isGestorForumCurso = false;
		
		if( !isEmpty(SigaaHelper.getCursoAtualCoordenacao()) ){
			isGestorForumCurso = getDAO(GestorForumCursoDao.class).isGestorForumCurso( 
					getUsuarioLogado().getVinculoAtivo().getServidor(),
					getUsuarioLogado().getVinculoAtivo().getDocenteExterno(), SigaaHelper.getCursoAtualCoordenacao());
		}
		
		return isGestorForumCurso;
	
	}
	
	/**
	 * Lista todos os f�runs por curso.<br/><br/>
	 * N�o invocado por JSP
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarForunsPorCurso() throws DAOException {
		getMensagens();
		return forward("/medio/" + getClasse().getSimpleName() + "/listar.jsp");
	}
	
	/**
	 * Lista todos os f�runs por curso.<br/><br/>
	 * N�o invocado por JSP
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarForunsPorTurmaSerie() throws DAOException {
		getMensagens();
		return forward("/medio/" + getClasse().getSimpleName() + "/listar.jsp");
	}
	
	/**
	 * Lista todas mensagens do F�rum.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	 <li>/sigaa.war/portais/docente/cursos_forum.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String listarMensagemPorForum() throws DAOException {

		int id = getParameterInt("id",0);
		if (id == 0) {
			addMensagemErro("F�rum n�o informado!");
			return null;
		}
		
		eUmForumDeTurmaSerie = true;
		if(getUsuarioLogado().getDiscenteAtivo() != null)
			usuarioAtivo = getUsuarioLogado().getDiscenteAtivo().isAtivo();
		
		setForumSelecionado(id);
		
		forum = getGenericDAO().findByPrimaryKey(id, ForumMedio.class);
		
		Integer idCurso = getParameterInt("idCurso",0);
		if (idCurso > 0){
			Curso curso = getGenericDAO().findByPrimaryKey(idCurso.intValue(), Curso.class);
			getCurrentSession().setAttribute("cursoAtual", curso);		
		}

		setTamanhoPagina(10);
		listaForunsPorTurmaSerie = (ArrayList<ForumMensagemMedio>) getDAO(ForumMedioDao.class).
			findListaMensagensForumByIDForum(forum.getId(), getPaginacao());
		
		return forward("/graduacao/" + getClasse().getSimpleName() + "/listar.jsp");
	}	

	/**
	 * Carrega as mensagens do f�rum
	 * @throws DAOException
	 */
	private void getMensagens() throws DAOException {
		eUmForumDeTurmaSerie = true;
		
		if ( getUsuarioLogado().getServidorAtivo() != null || getUsuarioLogado().getDocenteExternoAtivo() != null ){
			buscarMensagensPorDocente();
			return;
		}

		if(getUsuarioLogado().getDiscenteAtivo() != null)
			usuarioAtivo = getUsuarioLogado().getDiscenteAtivo().isAtivo();

		if (getParameterInt("id") != null) {
			int id = getParameterInt("id");
			setForumSelecionado(id);
		}

		if (getUsuarioLogado().getDiscenteAtivo() != null) {
			MatriculaDiscenteSerieDao mdsDao = getDAO(MatriculaDiscenteSerieDao.class);
			MatriculaDiscenteSerie matriculaSerie = new MatriculaDiscenteSerie();
			
			if (getUsuarioLogado().getDiscenteAtivo() != null) {
				matriculaSerie = mdsDao.findSerieAtualDiscente((DiscenteMedio) getUsuarioLogado().getDiscenteAtivo(), getCalendarioVigente().getAno());
				if( isNotEmpty(matriculaSerie) ) {
					turmaSerie = matriculaSerie.getTurmaSerie();
					buscarMensagensPorTurmaSerie(turmaSerie);
				}
			} 	
		}
	}

	/**
	 * Busca todas as mensagens de um determinado curso.<br/><br/>
	 * N�o invocado por JSP
	 * 
	 * @param curso
	 * @throws DAOException
	 */
	public void buscarMensagensPorTurmaSerie(TurmaSerie turmaSerie) throws DAOException {
		if (turmaSerie != null) {
			ForumMedio f = getDAO(ForumMedioDao.class).findForumMensagensByTurmaSerie(turmaSerie.getId());
			forum = f;
			setTamanhoPagina(10);
			listaForunsPorTurmaSerie = (ArrayList<ForumMensagemMedio>) getDAO(ForumMedioDao.class).findListaMensagensForumByIDForum(f.getId(), getPaginacao());
		}	
	}

	/**
	 * Busca todas as mensagens de um determinado curso.<br/><br/>
	 * N�o invocado por JSP
	 * 
	 * @param curso
	 * @throws DAOException
	 */
	public void buscarMensagensPorDocente() throws DAOException {
		ForumMensagemMedioDao fmDao = getDAO(ForumMensagemMedioDao.class);
		if( getUsuarioLogado().getServidorAtivo() != null ){
			listaForunsPorTurmaSerie = fmDao.findForumMensagemByDocenteAno(getUsuarioLogado().getServidorAtivo().getId() , null, CalendarUtils.getAnoAtual());
		} else if ( getUsuarioLogado().getDocenteExternoAtivo() != null ){
			listaForunsPorTurmaSerie = fmDao.findForumMensagemByDocenteAno(null , getUsuarioLogado().getDocenteExternoAtivo().getId(), CalendarUtils.getAnoAtual());
		} 
		setTamanhoPagina(10);
		
	}
	
	/**
	 * Busca todas as mensagens por programa.
	 * 
	 * @param programa
	 * @throws DAOException
	 */
	private void buscarMensagensPorPrograma(Unidade programa) throws DAOException {
		forum = null;
		listaForunsPorTurmaSerie = null;
		listagem = null;
		ForumMedioDao fDao = null;
		CursoDao cDao = null;
		
		try {
			
			fDao = getDAO(ForumMedioDao.class);
			cDao = getDAO(CursoDao.class);
			
			if (programa != null) {
				
				listaForunsPorTurmaSerie = new ArrayList<ForumMensagemMedio>();
				
				Collection<Curso> cursos = cDao.findByPrograma(programa.getId());
				if( isEmpty(cursos) ){
					addMensagem( MensagensPortalCoordenadorStricto.PROGRAMA_NAO_POSSUI_CURSO );
				}else{
					listaForunsPorTurmaSerie = (List<ForumMensagemMedio>) getDAO(ForumMedioDao.class).
						findListaMensagensForumByCursos(cursos);
				}
				
			}
			
			Collections.sort(listaForunsPorTurmaSerie, new Comparator<ForumMensagemMedio>(){
				public int compare(ForumMensagemMedio o1, ForumMensagemMedio o2) {
					return o2.getUltimaPostagem().compareTo(o1.getUltimaPostagem());
				}
			});
			
		}finally{
			if (fDao != null)
				fDao.close();
			if (cDao != null)
				cDao.close();
		}	
		
	}

	/**
	 * Mostra os f�runs e as mensagem que o curso disp�e.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/listar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/coordenador.jsp</li>
	 * 		<li>/sigaa.war/lato/coordenador.jsp</li>
	 * 		<li>/sigaa.war/portais/discente/discente.jsp</li>
	 * 		<li>/sigaa.war/stricto/coordenacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String mostrarForumMensagemTurmaSerie() throws ArqException {
		
		if(getUsuarioLogado().getDiscenteAtivo() != null)
			usuarioAtivo = getUsuarioLogado().getDiscenteAtivo().isAtivo();
		
		object = new ForumMensagemMedio();
		int idForumMensagem = 0;
		if (getParameterInt("idForumMensagem") != null) {
			idForumMensagem = getParameterInt("idForumMensagem");
			setIdForumMensagemSelecionado(idForumMensagem);
		}
		
		clearPaginacao();
		listaForunsPorTurmaSerie = getMensagensPaginadas(getIdForumMensagemSelecionado());

		if (listaForunsPorTurmaSerie.size() >= 1)
			tituloTurmaSerieDiscutidaAtualmente = listaForunsPorTurmaSerie.get(0).getTitulo();

		return forward("/medio/" + getClasse().getSimpleName() + "/mostrar.jsp");
	}
	
	/**
	 * Cadastra uma resposta de um F�rum de Curso.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/graduacao/ForumMensagem/mostrar.jsp
	 * @return
	 * @throws DAOException 
	 * @throws ArqException
	 */
	public String responderTopicoForumTurmaSerie() throws DAOException {

		try {
			
			ForumMedioDao fDao = getDAO(ForumMedioDao.class);
			ForumMedio f = new ForumMedio();
			ForumMensagemMedio fm = new ForumMensagemMedio();
			
			if (getParameterInt("idForumMensagem") == null) {
				List<TurmaSerie> turmasSerie = new ArrayList<TurmaSerie>();
				
				if (getUsuarioLogado().getDiscenteAtivo() != null) {
					turmasSerie = getDAO(TurmaSerieDao.class).findByDiscente( getUsuarioLogado().getDiscenteAtivo(), false, SituacaoMatriculaSerie.MATRICULADO.getId());
				}
				
				if (turmasSerie.isEmpty()) {
					addMensagemErro("N�o foi poss�vel identificar a Turma.");
					return null;
				}
				
				f = fDao.findForumMensagensByTurmaSerie( turmasSerie.get(0).getId() );
				setForumSelecionado(f.getId());
			
				object.setForum(f);
			} else {
				
				fm.setId(getParameterInt("idForumMensagem"));
				f = fDao.findForumMensagensByID(fm.getId()).getForum();
				object.setForum(f);
				object.setTopico(fm);
			}
	
			prepareMovimento(SigaaListaComando.CADASTRAR_TOPICO_FORUM_MEDIO);
			object.setTurmaSerie(true);
			MovimentoCadastro movCad = new MovimentoCadastro();
			movCad.setObjMovimentado(object);
			movCad.setCodMovimento(SigaaListaComando.CADASTRAR_TOPICO_FORUM_MEDIO);

			if (isEmpty(object.getConteudo())) {
				addMensagem(MensagensArquitetura.CAMPO_SEM_ABREVIACOES, "Conte�do");
				return irParaForum();
			}
			
			if ( fDao.existMensagensDuplicadaByForumUsuario(object.getTopico().getId(), getUsuarioLogado().getId(), object.getConteudo()) ) {
				addMensagemErro("N�o � poss�vel cadastrar respostas duplicadas.");
				return irParaForum();
			}
			execute(movCad);
			
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e);
		} catch (ArqException e) {
			tratamentoErroPadrao(e, "N�o foi poss�vel cadastrar sua resposta para esse F�rum.");
			return forward("/medio/" + getClasse().getSimpleName() + "/listar.jsp");
		}

		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

		listaForunsPorTurmaSerie = getMensagensPaginadas(getIdForumMensagemSelecionado());
		if (listaForunsPorTurmaSerie.size() >= 1)
			tituloTurmaSerieDiscutidaAtualmente = listaForunsPorTurmaSerie.get(0).getTitulo();

		object = new ForumMensagemMedio();
		pageLast();
		return forward("/medio/" + getClasse().getSimpleName() + "/mostrar.jsp");
	}

	/**
	 * Direciona para a lista de mensagens de um t�pico.
	 * 
	 * @throws ArqException
	 */
	private String irParaForum() throws ArqException {
		listaForunsPorTurmaSerie = getMensagensPaginadas(getIdForumMensagemSelecionado());
		if (listaForunsPorTurmaSerie.size() >= 1)
			tituloTurmaSerieDiscutidaAtualmente = listaForunsPorTurmaSerie.get(0).getTitulo();

		object = new ForumMensagemMedio();
		pageLast();
		return forward("/medio/" + getClasse().getSimpleName() + "/mostrar.jsp");
	}

	/**
	 * Quando criada nova mensagem envia para o participantes do f�rum em quest�o. 
	 * 
	 * @param object
	 */
	private void enviarEmailNotificacaoTodosAlunosForum(ForumMensagemMedio object, UploadedFile anexo) {

		ForumMedioDao dao = getDAO(ForumMedioDao.class);

		List<String> emails = null;
		try {
			emails = dao.findEmailsTodosParticipantesForum(object);
		} catch (DAOException e) {
			emails = new ArrayList<String>();
		}

		for (String email : emails) {
			MailBody mail = new MailBody();
			mail.setAssunto("Nova Mensagem criada em: " + object.getForum().getDescricao());
			mail.setFromName(getUsuarioLogado().getNome());
			mail.setEmail(email);
			mail.setMensagem(object.getConteudo());
			if(anexo != null) {
				try {
					mail.addAttachment(new Object[] { arquivo.getName(), arquivo.getBytes() });
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Mail.send(mail);
		}
	}

	/**
	 * Cadastra uma mensagem para o f�rum que estiver aberto no momento .<br/><br/>
	 *
	 * M�todo chamado pela seguinte JSP: /sigaa.war/graduacao/ForumMensagem/novo.jsp
	 * @return
	 * @throws DAOException
	 */
	public String cadastrarMensagemForumTurmaSerie() throws DAOException {
		
		if( !isOperacaoAtiva(SigaaListaComando.CADASTRAR_AVA.getId())  ){
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return super.cancelar();
		}
		
		validacaoTextArea();
		eUmForumDeTurmaSerie = true;
		ForumMedioDao fDao = null;
		
		try {
			
			
			if (object.getTitulo().equals("")){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "T�tulo");
			}
			
			if (object.getConteudo().equals("")){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Conte�do");
			}
			
			if (hasErrors()) return null;
			
			fDao = getDAO(ForumMedioDao.class);
			
			List<TurmaSerie> turmasSerie = new ArrayList<TurmaSerie>();
			List<TurmaSerie> turmaSerieSelecionadas = new ArrayList<TurmaSerie>();
			 
			
			if (isPortalDiscente() && getUsuarioLogado().getDiscenteAtivo() != null ) {
				turmasSerie = getDAO(TurmaSerieDao.class).findByDiscente( getUsuarioLogado().getDiscenteAtivo(), false, SituacaoMatriculaSerie.MATRICULADO.getId());
				turmaSerieSelecionadas.addAll(turmasSerie);
			}
			
			if (isDocente() && ( getCurrentSession().getAttribute("turmaSerieAtual") != null && !turmaSerieSelecionadas.contains(getCurrentSession().getAttribute("turmaSerieAtual"))) ) {
				turmaSerieSelecionadas.add((TurmaSerie) getCurrentSession().getAttribute("turmaSerieAtual"));
			}
			
			if ( turmaSerieSelecionadas.isEmpty() ) {
				addMensagemErro("N�o foi poss�vel identificar a turma de Ensino M�dio.");
				return null;
			}			
				
			boolean sucesso = true;
			ForumMensagemMedio forumAtual = UFRNUtils.deepCopy(object);
			for (TurmaSerie turmaSerie : turmaSerieSelecionadas) {
				
				ForumMedio f = fDao.findForumMensagensByTurmaSerie(turmaSerie.getId());
				if ( isEmpty(f) ){
					ForumMedioMBean fm = (ForumMedioMBean)getMBean("forumMedio");
					// caso n�o exista nenhum f�rum para a turmaSerie, o f�rum ser� criado.
					f = (ForumMedio) fm.criarForumTurmaSerie( turmaSerie );
				}	
					
				setForumSelecionado(f.getId());
	
				try {
					prepareMovimento(SigaaListaComando.CADASTRAR_AVA);
				} catch (ArqException e1) {
					e1.printStackTrace();
				}
	
				if (getParameterInt("idForumMensagem") == null) {
					object.setForum(f);
					object.setTopico(null);
				} else {
					ForumMensagemMedio fm = fDao.findForumMensagensByID(getParameterInt("idX"));
	
					if (fm == null) {
						object.setForum(f);
						object.setTopico(object.getTopico());
					} else {
						object.setForum(f);
						object.setTopico(fm.getTopico());
	
					}
				}
	
				if (eUmForumDeTurmaSerie)
					object.setTurmaSerie(true);
			
				if (arquivo != null)
					anexarArquivoAoForum();
	
				object.setUltimaPostagem(new Date());
				
				Notification notification = new Notification();
				if ( getUltimoComando().equals(SigaaListaComando.CADASTRAR_AVA) )
					notification = executeForumCursos(SigaaListaComando.CADASTRAR_AVA, object, getEspecificacaoCadastroTopicos());
				
				if (notification.hasMessages()) {
					sucesso = false;
					notifyView(notification);
					break;
				} else {
					if (notificar) {
						enviarEmailNotificacaoTodosAlunosForum(object, arquivo);
					}
					object = new ForumMensagemMedio();
					object = UFRNUtils.deepCopy(forumAtual);
				}
			}
			
			if (sucesso)
				flash("Cadastrado com sucesso.");
			
			return listarForunsPorCurso();
		}finally {
			if (fDao != null)
				fDao.close();
		}	
	}

	/**
	 * Serve para baixar o arquivo postado no f�rum.<br/><br/>
	 * N�o invocado por jsp
	 * @return
	 */
	public String baixarArquivoAnexadoForum() {
		return redirectJSF(getContextPath() + "/verProducao?idProducao="
				+ listaForunsPorTurmaSerie.get(0).getIdArquivo());
	}

	/**
	 * Serve para anexar um arquivo ao f�rum em quest�o.<br/><br/>
	 * N�o invocado por jsp
	 */
	private void anexarArquivoAoForum() {
		try {

			object.setIdArquivo(EnvioArquivoHelper.getNextIdArquivo());
			EnvioArquivoHelper.inserirArquivo(object.getIdArquivo(), arquivo
					.getBytes(), arquivo.getContentType(), arquivo.getName());

		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	/**
	 * Notifica quando um f�rum em curso for aberto. 
	 * 
	 * @param comando
	 * @param object
	 * @param specification
	 * @return
	 */
	protected Notification executeForumCursos(Comando comando,
			PersistDB object, Specification specification) {
		try {

			MovimentoCadastroAva mov = new MovimentoCadastroAva();
			mov.setCodMovimento(comando);
			mov.setObjMovimentado(object);
			mov.setSpecification(specification);

			return (Notification) execute(mov, getCurrentRequest());

		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		}
	}

	/**
	 * Serve para evitar que quando o usu�rio segure um tecla sem que haja quebra de linha, 
	 * para evitar que se tenha uma barra de rolagem na horizontal.<br/><br/>
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/novo.jsp</li>
	 * 		<li>/sigaa.war/ava/ForumMensagem/novo.jsp</li>
	 * </ul>
	 * 
	 */
	private void validacaoTextArea() {
		int tam = object.getConteudo().length(),fim = CONTROLE_VALIDACAO,inicio=0,i;
		  if (tam > CONTROLE_VALIDACAO) {
			  String[] palavra = new String[tam];
			  boolean teste = true;
			  if (object.getConteudo().contains(" ") == false){
				for (i = 0; teste == true; i++, fim=fim+CONTROLE_VALIDACAO) {
					if (fim >= tam) {
						palavra[i] = object.getConteudo().substring(inicio, tam);
						inicio = fim;
						System.out.print(palavra[i]+ " ");
						teste= false;
					}else{
						palavra[i] = object.getConteudo().substring(inicio, fim);
						inicio = fim;
						System.out.print(palavra[i]+ " ");
					}
				}
						String p = " ";  
						for (int j = 0; j != i; j++) {
							p += palavra[j] + " ";
							object.setConteudo(p);
					}
			}
		}
	}
	

	/**
	 * Retorna as mensagens do F�rum pra mostrar no portal do discente.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/portais/discente/discente.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<ForumMensagemMedio> getForumMensagemPortalDiscente() throws DAOException {
		MatriculaDiscenteSerieDao mdsDao = getDAO(MatriculaDiscenteSerieDao.class);
		MatriculaDiscenteSerie matriculaSerie = new MatriculaDiscenteSerie();
		
		if (getUsuarioLogado().getDiscenteAtivo() != null) {
			matriculaSerie = mdsDao.findSerieAtualDiscente((DiscenteMedio) getUsuarioLogado().getDiscenteAtivo(), getCalendarioVigente().getAno());
			if( isNotEmpty(matriculaSerie) ) turmaSerie = matriculaSerie.getTurmaSerie();
		} 
		
		getPaginacao().setPaginaAtual(0);
		if ( forumMensagemPortalDiscente == null && getDiscenteUsuario()!= null ) {
			if (getDiscenteUsuario().getCurso() != null)  {
				if (getListaForunsPorTurmaSerie() != null) {
					if ( getListaForunsPorTurmaSerie() == null )
						forumMensagemPortalDiscente = new ArrayList<ForumMensagemMedio>();
					else
						forumMensagemPortalDiscente = getListaForunsPorTurmaSerie();
				} else {
					ForumMedio forum = getDAO(ForumMedioDao.class).findForumMensagensByTurmaSerie(turmaSerie.getId() );
					if (forum != null) {
						setForum(forum);
		
						List<ForumMensagemMedio> lista = (List<ForumMensagemMedio>) getDAO(ForumMedioDao.class).findListaMensagensForumByIDForum(forum.getId());
						if (lista != null)
							setListaForunsPorCurso(lista);
					}
					usuarioAtivo = getDiscenteUsuario().isAtivo();
				}
			}
			
			if ( getListaForunsPorTurmaSerie() == null )
				forumMensagemPortalDiscente = new ArrayList<ForumMensagemMedio>();
			else
				forumMensagemPortalDiscente = getListaForunsPorTurmaSerie();
		}
		
		return forumMensagemPortalDiscente;
	}	
	
	/**
	 * Retorna o calend�rio vigente para as opera��es
	 *
	 * @return
	 * @throws DAOException 
	 */
	public CalendarioAcademico getCalendarioVigente() {
		CalendarioAcademico cal = (CalendarioAcademico) getCurrentSession().getAttribute("calendarioAcademico");
		if (cal == null)
			try {
				cal = CalendarioAcademicoHelper.getCalendario(getUsuarioLogado());
			} catch (Exception e) {
				e.printStackTrace();
			}

		if (cal == null) {
			throw new RuntimeNegocioException("� necess�rio que o calend�rio acad�mico esteja definido para realizar esta opera��o.");
		}
		return cal;
	}
	
	/**
	 * Limpa os f�runs do portal discente ap�s a renderiza��o do mesmo.
	 * Utilizado para evitar m�ltiplas chamadas ao m�todo getForumMensagemPortalDiscente(), o que
	 * causava a realiza��o de diversas consultas desnecess�rias.
	 * M�todo chamado pela seguinte JSP: /sigaa.war/portais/discente/discente.jsp
	 * @return
	 */
	public String getLimparForumMensagemPortalDiscente() {
		forumMensagemPortalDiscente = null;
		return null;
	}
	
	/**
	 * Retorna as mensagens do F�rum pra mostrar no portal do docente.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/portais/discente/medio/forum_turma_docente.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<ForumMensagemMedio> getForumMensagemDocente() throws DAOException {
		
		if (getListaForunsPorTurmaSerie() != null)
			return getListaForunsPorTurmaSerie();
		
		buscarMensagensPorDocente();
		
		return getListaForunsPorTurmaSerie();
	}
	
	/**
	 * Retorna as mensagens do F�rum pra mostrar no portal coordenador de Gradua��o.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/graduacao/coordenador.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<ForumMensagemMedio> getForumMensagemCoordenacaoGraducao() throws DAOException {
		
		if (getListaForunsPorTurmaSerie() != null)
			return getListaForunsPorTurmaSerie();
		
		buscarMensagensPorTurmaSerie(turmaSerie);
		
		return getListaForunsPorTurmaSerie();
	}
	
	/**
	 * Retorna as mensagens do F�rum pra mostrar no portal coordenador de Stricto .<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/stricto/coordenacao.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<ForumMensagemMedio> getForumMensagemCoordenacaoStricto() throws DAOException {

		if (getListaForunsPorTurmaSerie() != null)
			return getListaForunsPorTurmaSerie();
		
		PortalCoordenacaoStrictoMBean bean = getMBean("portalCoordenacaoStrictoBean");
		Unidade programa = bean.getProgramaStricto();
		buscarMensagensPorPrograma(programa);
		return getListaForunsPorTurmaSerie();
	}	
	
	/**
	 * Retorna as mensagens do Forum pra mostrar no portal coordenador de Lato.<br/><br/>
	 * M�todo chamado pela seguinte JSP: /sigaa.war/lato/coordenador.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<ForumMensagemMedio> getForumMensagemCoordenacaoLato() throws DAOException {
		
		if (getListaForunsPorTurmaSerie() != null)
			return getListaForunsPorTurmaSerie();
		
	    buscarMensagensPorTurmaSerie(turmaSerie);
        
        return getListaForunsPorTurmaSerie();
	}
	
	/**
	 * <p>
	 * Verifica se o coordenador logado � o coordenador do curso escolhido na lista de f�runs.
	 * (Tamb�m usado quando o usu�rio entra pelo portal da coordena��o)
	 * </p>
	 * <p>
	 * � usado para habilitar/desabilitar a remo��o de t�picos
	 * </p>
	 * JSP: /sigaa.war/graduacao/ForumMensagem/listar.jsp
	 * @return
	 */
	public boolean isCoordenadorDoCursoEscolhido() {
		
		boolean coordenadorDoCurso = false;
		Curso curso = (Curso) getCurrentSession().getAttribute("cursoAtual");
		
		if (getAcessoMenu().isCoordenadorCursoGrad() && !ValidatorUtil.isEmpty(curso)) {
		
			Collection<Curso> cursosDoCoordenador = getAcessoMenu().getCursos();
			
			coordenadorDoCurso = cursosDoCoordenador == null ? false : cursosDoCoordenador.contains(curso);
		}
		
		return coordenadorDoCurso;
	}
	
	public int getIdForumMensagemSelecionado() {
		return idForumMensagemSelecionado;
	}

	public void setIdForumMensagemSelecionado(int idForumMensagemSelecionado) {
		this.idForumMensagemSelecionado = idForumMensagemSelecionado;
	}

	public String getTituloTurmaSerieDiscutidaAtualmente() {
		return tituloTurmaSerieDiscutidaAtualmente;
	}

	public void setTituloTurmaSerieDiscutidaAtualmente(
			String tituloTurmaSerieDiscutidaAtualmente) {
		this.tituloTurmaSerieDiscutidaAtualmente = tituloTurmaSerieDiscutidaAtualmente;
	}
	
	/**
	 * Inicia o caso de uso de cadastrar um t�pico de f�rum e volta para a turma virtual.<br/><br/>
	 * 
	 * Chamado pela seguinte JSP: /sigaa.war/ava/aulas.jsp
	 * 
	 * 
	 * @param idTopicoSelecionado
	 * @return
	 */
	public String novoParaTurma(Integer idTopicoSelecionado) {
		paginaOrigem = getParameter("paginaOrigem");
		
		this.idTopicoAula = idTopicoSelecionado;
		return novo();
	}

	/**
	 * Retorna o discente logado.<br/><br/>
	 * N�o invocado por JSP
	 * @return
	 */
	public int getDiscenteLogado() {
		Usuario u = (Usuario) getCurrentSession().getAttribute("usuario");
		discenteLogado = u.getId();

		return discenteLogado;
	}

	public void setDiscenteLogado(int discenteLogado) {
		this.discenteLogado = discenteLogado;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public Collection<TurmaSerie> getTurmasSerie() {
		return turmasSerie;
	}

	public void setTurmasSerie(Collection<TurmaSerie> turmasSerie) {
		this.turmasSerie = turmasSerie;
	}

	/**
	 * Retorna o usu�rio ativo
	 * @return the alunoAtivo
	 */
	public boolean isUsuarioAtivo() {
		return usuarioAtivo;
	}

	/**
	 * Verifica se o usu�rio � docente.
	 * @return the alunoAtivo
	 */
	public boolean isDocente(){
		return getUsuarioLogado().getServidorAtivo() != null || getUsuarioLogado().getDocenteExternoAtivo() != null; 
	}
	
	/**
	 * Seta o usu�rio ativo
	 * @param alunoAtivo the alunoAtivo to set
	 */
	public void setUsuarioAtivo(boolean usuarioAtivo) {
		this.usuarioAtivo = usuarioAtivo;
	}

	public int getIdTopicoAula() {
		return idTopicoAula;
	}

	public void setIdTopicoAula(int idTopicoAula) {
		this.idTopicoAula = idTopicoAula;
	}

}