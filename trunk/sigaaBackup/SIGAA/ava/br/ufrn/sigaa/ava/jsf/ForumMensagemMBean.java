/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * created on 31/01/2008
 * 
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO;
import static br.ufrn.arq.util.ValidatorUtil.isAllEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

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
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.negocio.SigaaHelper;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenacaoGeralRede;
import br.ufrn.sigaa.ava.dao.ForumDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.dominio.DenunciaMensagem;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.Forum;
import br.ufrn.sigaa.ava.dominio.ForumMensagem;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.forum.dao.GestorForumCursoDao;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroAva;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.jsf.HistoricoMBean;
import br.ufrn.sigaa.ensino.latosensu.jsf.PortalCoordenadorLatoMBean;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;
import br.ufrn.sigaa.ensino_rede.portal.jsf.PortalCoordenadorRedeMBean;
import br.ufrn.sigaa.mensagens.MensagensPortalCoordenadorStricto;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;
import br.ufrn.sigaa.parametros.dominio.ParametrosPortalDocente;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.portal.jsf.PortalCoordenacaoStrictoMBean;
import br.ufrn.sigaa.portal.jsf.PortalCoordenadorGraduacaoMBean;

/**
 * Controlador responsável por postar mensagens no fórum da turma virtual e no fórum de cursos
 * 
 * @author David
 */
@Component("forumMensagem")
@Scope("request")
public class ForumMensagemMBean extends CadastroTurmaVirtual<ForumMensagem> {

	/** Arquivo a ser anexado ao tópico do forum e enviado por email.*/
	private UploadedFile arquivo;
	/** Identificador do registro do Forum selecionado. */
	private int idForumSelecionado;
	
	/** Indica o tópico de aula selecionado.  */
	private int idTopicoAula;
	
	/** Indica se os alunos receberão e-mails de notificação da criação de forum e tópicos.*/
	private boolean notificar;
	/** Identificador do registro do Forum selecionado. */
	private int forumSelecionado;
	/** Identificador do registro da Mensagem selecionada no Forum. */
	private int idForumMensagemSelecionado;
	/** Identificador do discente logado.*/
	private int discenteLogado;

	/** Listagem de fóruns do Curso.*/
	private List<ForumMensagem> listaForunsPorCurso;
	/** Indica se o forum selecionado pertence a um curso.*/
	private boolean eUmForumDeCurso;
	/** variável utilizada para manter o título do forum do curso.*/
	private String tituloCursoDiscutidoAtualmente;
	
	/** Listagem de fóruns do Programa.*/
	private List<ForumMensagem> listaForunsPorPrograma;
	/** Indica se o forum selecionado pertence a um Programa.*/
	private boolean eUmForumDePrograma;
	/** variável utilizada para manter o título do forum do programa.*/
	private String tituloProgramaDiscutidoAtualmente;
	
	/** Fórum ao qual a mensagem pertence. */
	private Forum forum;
	/** variável utilizada para manter o conteúdo do forum do curso.*/
	private String conteudo;
	/** variável utilizada para filtrar os fóruns por nome.*/
	private String filtro;
	
	/**Constante para controle da função validacaoTextArea.*/
	private static final int CONTROLE_VALIDACAO = 87;

	/** Constante de controle da paginação de mensagens. */
	private static final int CONTROLE_PAGINACAO = 1;
	
	/*
	 * INÍCIO PAGINAÇÃO
	 */
	/** Lista de mensagens do fórum de uma página */
	private List<ForumMensagem> mensagensPaginadas;
	/** Total de mensagens da página */
	private Integer totalRegistros;
	/** Número da primeira mensagem de uma página. */
	private int primeiroRegistro;
	/** Quantidade de mensagens de cada página. */
	private int quantRegistrosPagina = 20;
	/** Array que guarda as páginas. */
	private Integer[] pages = new Integer[0];
	/** Página Atual. */
	private int paginaAtual;
	
	/** Se está criando um novo tópico ou respondendo um tópico antigo. */
	private boolean cadastroTopico;
	/** Coleção de cursos de pós-graduação que um coordenador de curso pode cadastrar um fórum.*/
	private Collection<Curso> cursos;
	
	/** Se o suário está ativo */
	private boolean usuarioAtivo = true;
	/** Lista de mensagens de Portal do discente */
	private List<ForumMensagem> forumMensagemPortalDiscente;
	
	private DenunciaMensagem denuncia = new DenunciaMensagem();
	
	
	/**
	 * Carrega os dados por demanda de acordo com o ID informado. Funciona tanto
	 * para o Fórum de Cursos como para Fórum graduação.
	 * 
	 * @throws DAOException
	 */
	private void loadMensagensPaginadasForum(int idMensagem)
			throws DAOException {

		ForumDao dao = getDAO(ForumDao.class);
		
		mensagensPaginadas = dao.findMensagensByTopico(idMensagem, primeiroRegistro, quantRegistrosPagina);
		setarMatriculasDiscentesPostaramMensagens();
		
		totalRegistros = dao.countMensagensForum(idMensagem);

		listaForunsPorCurso = mensagensPaginadas;
		listaForunsPorPrograma = mensagensPaginadas;
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
	 * 	Seta as  matrículas dos vínculos ativos do discente que postou a mensagem no 
	 *  fórum (caso o usuário seja um discente).
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private List<ForumMensagem> setarMatriculasDiscentesPostaramMensagens() throws HibernateException, DAOException {
		ForumDao dao = getDAO(ForumDao.class);
		
		Curso curso = SigaaHelper.getCursoAtualCoordenacao();
		
		for (ForumMensagem forumMensagem : mensagensPaginadas) {
			List<Long> matriculas = dao.findMatriculasDiscentePostaramMensagens(forumMensagem.getUsuario().getId(), curso != null ? curso.getId() : null );
			forumMensagem.setMatriculas(matriculas);
		}
		
		return mensagensPaginadas;
	}

	/**
	 * Cria novo fórum caso o(s) docente(s) permita(m).<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
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
	 * Exibe as Mensagens da páginas  
	 * 
	 * @param idMensagem
	 * @return
	 * @throws DAOException
	 */
	private List<ForumMensagem> getMensagensPaginadas(int idMensagem) throws DAOException {
		loadMensagensPaginadasForum(idMensagem);
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
		loadMensagensPaginadasForum(getIdForumMensagemSelecionado());
	}

	/**
	 * Vai para a primeira página.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
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
	 * Serve para ir a próxima página.<br/><br/>
	 *  
	 * Método chamado pelas seguintes JSPs:
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
	 * Vai para a página anterior.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
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
	 * Vai direto para a última página.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
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

	public void setDataList(List<ForumMensagem> mensagensPaginadasForumCursos) {
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
	 * Quantidade de registros por página.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
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
	 * Página atual.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
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
	 * Zera a paginação, como se não houvesse mensagens.
	 */
	private void clearPaginacao() {
		paginaAtual = 0;
		primeiroRegistro = 0;
		totalRegistros = 0;
	}

	/**
	 * FIM PAGINAÇÃO
	 */

	/**
	 * Construtor padrão
	 */
	public ForumMensagemMBean() {
		
	}

	public Forum getForum() {
		return forum;
	}

	public void setForum(Forum forum) {
		this.forum = forum;
	}

	public List<ForumMensagem> getListaForumMensagens()
	{
		if (eUmForumDeCurso)
			return listaForunsPorCurso;
		else if (eUmForumDePrograma)
			return listaForunsPorPrograma;
		else
			return listaForunsPorCurso;
	}
	/**
	 * Retorna a lista de fóruns por curso
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/ForumMensagem/listar.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<ForumMensagem> getListaForunsPorCurso() throws DAOException {
		if (getPaginacao().getPaginaAtual() != paginaAtual) {
			getMensagens();
			paginaAtual = getPaginacao().getPaginaAtual();
		}
		
		return listaForunsPorCurso;
	}

	public void setListaForunsPorCurso(List<ForumMensagem> listaForunsPorCurso) {
		this.listaForunsPorCurso = listaForunsPorCurso;
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
	 * Vai para a listagem de mensagens de um tópico.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
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
		
			object = new ForumMensagem();
	
			Integer idForumMensagem = getParameterInt("idForumMensagem");
			registrarAcao(dao.findByPrimaryKey(idForumMensagem, ForumMensagem.class).getTitulo(), EntidadeRegistroAva.FORUM_MENSAGEM, AcaoAva.ACESSAR);
			
			if (idForumMensagem != null)
				setIdForumMensagemSelecionado(idForumMensagem);
	
			clearPaginacao();
			
			listagem = getMensagensPaginadas(getIdForumMensagemSelecionado());
			return forward("/ava/" + getClasse().getSimpleName() + "/mostrar.jsp");
		} finally {
			if (dao != null)
				dao.close();
		}
		
	}
	
	/**
	 * Lista de tópicos de um Fórum que não seja um fórum de curso. <br/><br/>
	 * 
	 * Método não invocado por JSPs. É public porque é chamado pela classe MenuTurmaMBean.
	 */
	public String listar(int idMural) {
		idForumSelecionado = idMural;
		return listar ();
	}

	/**
	 * Lista de tópicos de um Fórum que não seja um fórum de Curso.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
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

		if (!eUmForumDeCurso) {
			listagem = null;
			return forward("/ava/" + getClasse().getSimpleName() + "/listar.jsp");
		}

		return null;
	}

	/**
	 * Lista as mensagens do fórum de um determinado curso.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ava/Forum/mensagens.jsp</li>
	 * 		<li>/sigaa.war/cv/ForumComunidade/mensagens.jsp</li>
	 * </ul>
	 */
	@Override
	public List<ForumMensagem> lista() {
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
			
			forum = getGenericDAO().findByPrimaryKey(id, Forum.class);
			return (List<ForumMensagem>) getDAO(ForumDao.class).findListaMensagensForumByIDForum(getForumSelecionado());
			
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return new ArrayList<ForumMensagem>();
	}

	/**
	 * Ao cancelar a inserção de uma mensagem no fórum, o usuário será redirecionado
	 * para a página de listar.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ava/Forum/mensagens.jsp</li>
	 * 		<li>/sigaa.war/ava/ForumMensagem/mostrar.jsp</li>
	 * 		<li>sigaa.war/cv/ForumComunidade/mensagens.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	@Override
	public String cancelar(){
		listagem = null;
		return forward("/ava/" + getClasse().getSimpleName() + "/listar.jsf");
	}
	
	
	/**
	 * Cancela o Cadastro do Formulário de Evolução
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/form_cadastro_evolucao.jsp</li>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/view.jsp</li>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/remover.jsp</li>
	 *		<li>sigaa.war/infantil/CadastroFormularioEvolucao/alterar.jsp</li>
	 *	</ul>
	 * @throws ArqException 
	 */
	public String cancelarDenuncia() throws ArqException {
		 return mostrarForumMensagemCurso();
	}

	/**
	 * Lista todos os fóruns por curso. <br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/mostrar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/novo.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String voltarParaForumMensagensCursos() throws DAOException {
		return listarForunsPorCurso();
	}

	/**
	 * Método responsável pela remoção de um fórum.
	 * 
	 * Método não chamado por jsp.
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
	 * Remove uma mensagem ou tópico de um fórum.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:/sigaa.war/ava/Forum/mensagens.jsp
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
	
			// Registra a tentativa de remoção da mensagem do fórum.
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.FORUM_MENSAGEM, AcaoAva.INICIAR_REMOCAO, object.getId());
			
			antesRemocao();
			prepare(ArqListaComando.DESATIVAR);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(object);
			mov.setCodMovimento(ArqListaComando.DESATIVAR);

			execute(mov);
			
			// Registra a remoção da mensagem do fórum.
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.FORUM_MENSAGEM, AcaoAva.REMOVER, object.getId());
			
			object = new ForumMensagem();
			flash(" Mensagem do Fórum removido com sucesso.");

			if (getParameterBoolean("topico")) { // se for tópico redireciona
				// pra a lista de tópicos
				listagem = (List<ForumMensagem>) getDAO(ForumDao.class).findListaMensagensForumByIDForum(getForumSelecionado());
				return forward("/ava/" + getClasse().getSimpleName()	+ "/listar.jsf");
			} else { // se for mensagem redireciona para a lista de mensagens de
				// um tópico
				listagem = getMensagensPaginadas(getIdForumMensagemSelecionado());
				listagem.iterator().next();
				pageLast();
				return forward("/ava/" + getClasse().getSimpleName() + "/mostrar.jsf");
			}

		} catch (Exception e) {
			return tratamentoErroPadrao(e, "Existem registros associados ao que você está tentando remover que inviabilizam sua remoção");
		}

	}

	/**
	 * Verifica se o campo conteúdo foi informado. Tornado-o campo obrigatório.<br/><br/>
	 * 
	 * Não invocado por jsp
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
							.addError("É obrigatório informar o conteúdo da mensagem!");

				return !notification.hasMessages();
			}
		};
	}

	/**
	 * Método responsável por emitir o histórico do aluno.
	 * 
	 * Método chamado pela seguinte JSPs: /sigaa.war/graduacao/ForumMensagem/mostrar.jsp
	 * 
	 * @throws ArqException
	 * */
	public String emitirHistoricoAluno() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS);
			
		HistoricoMBean historicoMBean = getMBean("historico");
		Long matricula = getParameterLong("matricula");
		
		if (isEmpty(matricula)) {
			addMensagemErro("Nenhum discente selecionado.");
			return null;
		}
		
		Collection<Discente> discentes = getGenericDAO().findByExactField(Discente.class, "matricula", matricula);

		if (isEmpty(discentes)) {
			addMensagemErro("Nenhum discente localizado para matrícula informada:" + matricula);
			return null;
		}
		
		
		historicoMBean.setDiscente(discentes.iterator().next());
		return historicoMBean.selecionaDiscente();
	}
	
	/**
	 * Cadastra uma mensagem para um Fórum de Turma Virtual e envia e-mail aos participantes.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:/sigaa.war/ava/ForumMensagem/novo.jsp
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String cadastrarTopico() throws ArqException, NegocioException {
		
		// Registra a tentativa de cadastrar um fórum.
		registrarAcao(object.getTitulo(), EntidadeRegistroAva.FORUM_MENSAGEM, AcaoAva.INICIAR_INSERCAO);
		
		validacaoTextArea();
		
		cadastroTopico = true;
		ForumMensagem cadObject = object;
		
		if (idTopicoAula == 0)
			object.setTopicoAula(null);
		else
			object.setTopicoAula(new TopicoAula(idTopicoAula));

		boolean ok = true;
		
		if (object.getTitulo().equals("")){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Título");
			ok = false;
		}
		
		if (object.getConteudo().equals("")){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Conteúdo");
			ok = false;
		}
		
		if (!ok)
			return null;
		
		cadastrarMensagem();			
		flash("Cadastrado com sucesso.");
		
		notificarTurma("Novo Tópico de Fórum",
				"Um novo tópico de discussão foi criado no fórum da turma: "
						+ "<br><br>Título: " + cadObject.getTitulo()
						+ "<br><br>" + cadObject.getConteudo().replaceAll("\\n", "<br>") + "<BR><BR>"
						+ "Postado por: " + getUsuarioLogado().getNome(), ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE, ControllerTurmaVirtual.DOCENCIA_ASSISTIDA );
		
		forum = getGenericDAO().findByPrimaryKey(getForumSelecionado(), Forum.class);
		listagem = (List<ForumMensagem>) getDAO(ForumDao.class).findListaMensagensForumByIDForum(getForumSelecionado());
		
		if (paginaOrigem != null && paginaOrigem.equals("portalPrincipal")){
			TurmaVirtualMBean turmaBean = getMBean ("turmaVirtual");
			return turmaBean.retornarParaTurma();
		}
		
		return forward("/ava/" + getClasse().getSimpleName() + "/listar.jsf");
	}

	/**
	 *  Cadastro de mensagem de fórum mais genérico.
	 * 
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	private void cadastrarMensagem() throws ArqException, NegocioException {

		validacaoTextArea();
		prepareMovimento(SigaaListaComando.CADASTRAR_TOPICO_FORUM);

		Forum f = new Forum();
		f.setId(getForumSelecionado());
		
		// Se está cadastrando através da turma virtual, não selecionou um fórum
		if (f.getId() == 0){
			TurmaVirtualMBean t = getMBean ("turmaVirtual");
			if (t.getMural() != null)
				f.setId(t.getMural().getId());
		}

		object.setForum(f);
		ForumMensagem topico = new ForumMensagem();

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
		movCad.setCodMovimento(SigaaListaComando.CADASTRAR_TOPICO_FORUM);
		execute(movCad);

		// Registra inserção ou resposta de um tópico
		if ( cadastroTopico )
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.FORUM_MENSAGEM, AcaoAva.INSERIR, object.getId());
		else
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.FORUM_MENSAGEM, AcaoAva.RESPONDER, object.getId());
			
		if (getParameterInt("idForumMensagem") != null || idForumMensagemSelecionado != 0) {
			// caso seja null é por que é um tópico ao invés de mensagens

			if (getParameterInt("idForumMensagem") != null)
				idForumMensagemSelecionado = getParameterInt("idForumMensagem");

			listagem = getMensagensPaginadas(idForumMensagemSelecionado);
		}
	}

	/**
	 * Cadastrar mensagem em fórum da Turma Virtual. É a resposta a algum tópico.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
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

			ForumDao dao = null;
			
			try {
				dao = getDAO(ForumDao.class);
				ForumMensagem objOrig = object;
				ForumMensagem topico = dao.findByPrimaryKey(idForumMensagemSelecionado, ForumMensagem.class);
	
				if (getParameterInt("idForumMensagem") != null || idForumMensagemSelecionado != 0) {
	
					if (getParameterInt("idForumMensagem") != null)
						idForumMensagemSelecionado = getParameterInt("idForumMensagem");
	
	
					// Registra a tentativa de responder um tópico.
					registrarAcao(topico.getTitulo(), EntidadeRegistroAva.FORUM_MENSAGEM, AcaoAva.INICIAR_RESPOSTA, forum.getId());
					
					// adiciona o tópico 
					try {
						cadastrarMensagem();
					} catch (NegocioException e1) {
						e1.printStackTrace();
					}
	
					// envia e-mail para os participantes do tópico
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
						mail.setAssunto("Resposta ao Tópico: " + topico.getTitulo() + " - " +turma().getDescricaoSemDocente() + " - " + data );
						mail.setFromName("Fórum SIGAA: " + getUsuarioLogado().getNome());
						mail.setEmail(email);
						
					    // Removendo comentários inseridos quando se copia e cola conteúdos de arquivos do Word.
						objOrig.setConteudo(StringUtils.removerComentarios(objOrig.getConteudo()));
						
						mail.setMensagem(objOrig.getConteudo());
						Mail.send(mail);
					}
					object = new ForumMensagem();
					flash("Resposta cadastrada com sucesso. Participantes deste tópico foram notificados via e-mail");
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
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Título e Conteúdo");
			return forward("/ava/" + getClasse().getSimpleName() + "/mostrar.jsp");
		}
	}

	/**
	 * Apenas instância o ForumMensagem.<br/><br/>
	 * 
	 * Não invocado por JSP.
	 * 
	 */
	@Override
	public void instanciar() {

		object = new ForumMensagem();

		int idForumSelecionado = getForumSelecionado();

		try {

			Forum f = getDAO(ForumDao.class).findByPrimaryKey(
					idForumSelecionado, Forum.class);

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
	 * Verifica se conteúdo foi preenchido, caso não tenho sido. Retorna mensagem.<br/><br/>
	 * 
	 * Não invocado por JSP.
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
							.addError("É obrigatório informar o conteúdo da mensagem!");
				}

				return !notification.hasMessages();
			}
		};
	}

	/**
	 * Ao cadastrar uma mensagem no fórum há a verificação se o título e conteúdo foram informados.<br/><br/>
	 * 
	 * Não invocado por JSPs.
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
				ForumMensagem ref = (ForumMensagem) objeto;
				if (isEmpty(ref.getConteudo()))
					notification.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Conteúdo");

				if (isEmpty(ref.getTitulo()))
					notification.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Título");

				return !notification.hasMessages();
			}
		};
	}

	/**
	 * Remove um tópico com todas as suas mensagens.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/listar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/coordenador.jsp</li>
	 * 		<li>/sigaa.war/lato/coordenador.jsp</li>
	 * 		<li>/sigaa.war/stricto/coordenacao.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public String removerMensagensCurso() throws DAOException {
		try {
			getDAO(ForumDao.class).removerTopicosComFilhos(
					getParameterInt("id"));
			flash("O Tópico e todas as mensagens do mesmo foram removidas com sucesso.");
		} catch (Exception e) {
			addMensagem(MensagensArquitetura.REMOCAO_OBJETO_ASSOCIADO);
			throw new DAOException(
					"Existem registros associados ao que você está tentando remover");
		}

		return listarForunsPorCurso();
	}

	/**
	 * Remove uma mensagem de um tópico.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/mostrar.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String removerMensagensTopicos() throws ArqException, NegocioException {
		
		object = getGenericDAO().findByPrimaryKey( getParameterInt("id"), ForumMensagem.class );
		if( isEmpty(object) ){
			addMensagem( MensagensArquitetura.ACAO_JA_EXECUTADA );
			return null;
		}	
		String descricaoTipo = object.getDescricaoTipo();
		
		prepareMovimento(  SigaaListaComando.REMOVER_MENSAGEM_TOPICO_FORUM );
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado( object );
		mov.setCodMovimento( SigaaListaComando.REMOVER_MENSAGEM_TOPICO_FORUM );
		execute(mov);
		
		if( hasErrors() )
			return null;
		addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, descricaoTipo );
		
		clearPaginacao();
		
		if (iseUmForumDePrograma()){
			listaForunsPorPrograma = getMensagensPaginadas( getIdForumMensagemSelecionado() );
			return mostrarForumMensagemPrograma();
		}
		listaForunsPorCurso = getMensagensPaginadas( getIdForumMensagemSelecionado() );
		if ( !isAllEmpty( listaForunsPorCurso ) ) {
			tituloCursoDiscutidoAtualmente = listaForunsPorCurso.get(0).getTitulo();
			return irParaForum();
		} 
		
		return listarForunsPorCurso();
		
	}
	
	/**
	 * Denunciar uma mensagem de um tópico.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp</li>
	 * 		<li>/sigaa.war/graduacao/ForumMensagem/mostrar.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String denunciarMensagensTopicos() throws ArqException, NegocioException {
		setOperacaoAtiva(SigaaListaComando.DENUNCIAR_MENSAGEM_TOPICO_FORUM.getId());
		this.denuncia = new DenunciaMensagem();
		this.object = getGenericDAO().findByPrimaryKey( getParameterInt("idForumMensagem"), ForumMensagem.class );
		this.denuncia.setForumMensagem(object);
		if( isEmpty(object) ){
			addMensagem( MensagensArquitetura.ACAO_JA_EXECUTADA );
			return null;
		}	
		
 		return forward("/graduacao/"+getClasse().getSimpleName()+ "/denuncia.jsp");
	}
	
	/**
	 * Cadastra nova deununcia.<br/><br/>
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/ForumMensagem/denuncia.jsp
	 * @return
	 */
	public String cadastrarDenuncia() throws ArqException, NegocioException{
		checkOperacaoAtiva(SigaaListaComando.DENUNCIAR_MENSAGEM_TOPICO_FORUM.getId() );
		object = getGenericDAO().findByPrimaryKey( getParameterInt("idForumMensagem"), ForumMensagem.class );
		denuncia.setForumMensagem(object);
		prepareMovimento(  SigaaListaComando.DENUNCIAR_MENSAGEM_TOPICO_FORUM );
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado( denuncia );
		mov.setCodMovimento( SigaaListaComando.DENUNCIAR_MENSAGEM_TOPICO_FORUM );
			
		erros = denuncia.validate();
		
		if( hasErrors() )
			return null; 
		
		try {
			execute(mov);
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Denúncia" );
			removeOperacaoAtiva();
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
		}
	
		return mostrarForumMensagemCurso();
	}
	
	/**
	 * Cadastra novo fórum de curso.<br/><br/>
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/ForumMensagem/listar.jsp
	 * @return
	 */
	public String novoForumCurso() {
		try {

			if (isPortalCoordenadorStricto()) {
				Unidade programa = (Unidade) getCurrentSession().getAttribute("programaAtual");
				cursos = getDAO(CursoDao.class).findByPrograma(programa.getId());
			}
			novoForum();		
			object.setCurso(true);
			return forward("/graduacao/" + getClasse().getSimpleName() + "/novo.jsp");
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		}
	}

	/** Cadastra novo fórum.
	 * @throws ArqException
	 */
	private void novoForum() throws ArqException {
		object = new ForumMensagem();		
		
		int idForumSelecionado = getForumSelecionado();

		try {

			Forum f = getDAO(ForumDao.class).findByPrimaryKey(
					idForumSelecionado, Forum.class);

			object.setForum(f);
			object.setTopico(object);

		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}

		prepareMovimento(SigaaListaComando.CADASTRAR_AVA);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_AVA.getId());
	}

	/**
	 * Verifica se o usuário logado é gestor do curso atual.
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
	 * Lista todos os fóruns por curso.<br/><br/>
	 * Não invocado por JSP
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarForunsPorCurso() throws DAOException {
		getMensagens();
		return forward("/graduacao/" + getClasse().getSimpleName() + "/listar.jsp");
	}
	
	/**
	 * Lista todas mensagens do Fórum.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	 <li>/sigaa.war/portais/docente/cursos_forum.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String listarMensagemPorForum() throws DAOException {

		int id = getParameterInt("id",0);
		if (id == 0) {
			addMensagemErro("Fórum não informado!");
			return null;
		}
		
		eUmForumDeCurso = true;
		if(getUsuarioLogado().getDiscenteAtivo() != null)
			usuarioAtivo = getUsuarioLogado().getDiscenteAtivo().isAtivo();
		
		setForumSelecionado(id);
		
		forum = getGenericDAO().findByPrimaryKey(id, Forum.class);
		
		Integer idCurso = getParameterInt("idCurso",0);
		if (idCurso > 0){
			Curso curso = getGenericDAO().findByPrimaryKey(idCurso.intValue(), Curso.class);
			getCurrentSession().setAttribute("cursoAtual", curso);		
		}

		setTamanhoPagina(10);
		listaForunsPorCurso = (ArrayList<ForumMensagem>) getDAO(ForumDao.class).
			findListaMensagensForumByIDForum(forum.getId(), getPaginacao());
		
		return forward("/graduacao/" + getClasse().getSimpleName() + "/listar.jsp");
	}	

	/**
	 * Carrega as mensagens do fórum
	 * @throws DAOException
	 */
	private void getMensagens() throws DAOException {
		eUmForumDeCurso = true;
		if(getUsuarioLogado().getDiscenteAtivo() != null)
			usuarioAtivo = getUsuarioLogado().getDiscenteAtivo().isAtivo();

		if (getParameterInt("id") != null) {
			int id = getParameterInt("id");
			setForumSelecionado(id);
		}

		Curso curso = SigaaHelper.getCursoAtualCoordenacao();
		
		if (isPortalCoordenadorLato()) { // verificar se o usuário é coordenador de Lato
			curso = (Curso) getCurrentSession().getAttribute("cursoAtual");
		}
		else if (getUsuarioLogado().getDiscenteAtivo() != null && getUsuarioLogado().getDiscenteAtivo().getCurso() != null) {
			curso = getUsuarioLogado().getDiscenteAtivo().getCurso();
			
		} if (isPortalCoordenadorStricto()) { // verificar se o usuário é coordenador de Stricto
			Unidade programa = (Unidade) getCurrentSession().getAttribute("programaAtual");
			buscarMensagensPorPrograma(programa);
		}
		if (isPortalCoordenadorGraduacao()) { // verificar se o usuário é coordenador de Graduação
			curso = SigaaHelper.getCursoAtualCoordenacao();
		}

		if (curso != null && !isPortalCoordenadorStricto()) {
			buscarMensagensPorCurso(curso);
		}
	}

	/**
	 * Busca todas as mensagens de um determinado curso.<br/><br/>
	 * Não invocado por JSP
	 * 
	 * @param curso
	 * @throws DAOException
	 */
	public void buscarMensagensPorCurso(Curso curso) throws DAOException {
		if (curso != null) {
			Forum f = getDAO(ForumDao.class).findForumMensagensByIDCurso(curso.getId());
			forum = f;
			int numForunsPaginados = ParametroHelper.getInstance().getParametroInt(ParametrosPortalDocente.NUMERO_FORUNS_PAGINACAO_FORUNS_CURSO);
			setTamanhoPagina(numForunsPaginados);
			if (filtro == null || filtro.isEmpty())
				listaForunsPorCurso = (ArrayList<ForumMensagem>) getDAO(ForumDao.class).findListaMensagensForumByIDForum(f.getId(), getPaginacao());
			else
				listaForunsPorCurso = getDAO(ForumDao.class).findMensagensByIdForumFiltro(f.getId(),filtro,getPaginacao());
		}	
	}
	
	/**
	 * Busca todas as mensagens por programa.
	 * 
	 * @param programa
	 * @throws DAOException
	 */
	private void buscarMensagensPorPrograma(Unidade programa) throws DAOException {
		forum = null;
		listaForunsPorCurso = null;
		listagem = null;
		ForumDao fDao = null;
		CursoDao cDao = null;
		
		try {
			
			fDao = getDAO(ForumDao.class);
			cDao = getDAO(CursoDao.class);
			
			if (programa != null) {
				
				listaForunsPorCurso = new ArrayList<ForumMensagem>();
				
				Collection<Curso> cursos = cDao.findByPrograma(programa.getId());
				if( isEmpty(cursos) ){
					addMensagem( MensagensPortalCoordenadorStricto.PROGRAMA_NAO_POSSUI_CURSO );
				}else{
					listaForunsPorCurso = (List<ForumMensagem>) getDAO(ForumDao.class).
						findListaMensagensForumByCursos(cursos);
				}
				
			}
			
			Collections.sort(listaForunsPorCurso, new Comparator<ForumMensagem>(){
				public int compare(ForumMensagem o1, ForumMensagem o2) {
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
	 * Mostra os fóruns e as mensagem que o curso dispõe.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
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
	public String mostrarForumMensagemCurso() throws ArqException {
		
		if(getUsuarioLogado().getDiscenteAtivo() != null)
			usuarioAtivo = getUsuarioLogado().getDiscenteAtivo().isAtivo();
		
		object = new ForumMensagem();
		int idForumMensagem = 0;
		if (getParameterInt("idForumMensagem") != null) {
			idForumMensagem = getParameterInt("idForumMensagem");
			setIdForumMensagemSelecionado(idForumMensagem);
		}
		
		clearPaginacao();
		listaForunsPorCurso = getMensagensPaginadas(getIdForumMensagemSelecionado());

		if (listaForunsPorCurso.size() >= 1)
			tituloCursoDiscutidoAtualmente = listaForunsPorCurso.get(0).getTitulo();

		return forward("/graduacao/" + getClasse().getSimpleName() + "/mostrar.jsp");
	}
	
	/**
	 * Cadastra uma resposta de um Fórum de Curso.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/ForumMensagem/mostrar.jsp
	 * @return
	 * @throws DAOException 
	 * @throws ArqException
	 */
	public String responderTopicoForumCursos() throws DAOException {

		try {
			
			ForumDao fDao = getDAO(ForumDao.class);
			
			Curso curso = SigaaHelper.getCursoAtualCoordenacao();
			
			if (getUsuarioLogado().getDiscenteAtivo() != null
					&& getUsuarioLogado().getDiscenteAtivo().getCurso() != null) {
				curso = getUsuarioLogado().getDiscenteAtivo().getCurso();
			}	
			if (isPortalCoordenadorLato()) {
				curso = (Curso) getCurrentSession().getAttribute("cursoAtual");
			} 
			if (isPortalCoordenadorStricto()) {
				Unidade programa = (Unidade) getCurrentSession().getAttribute("programaAtual");
				Collection<Curso> cursos = getDAO(CursoDao.class).findByPrograma(programa.getId());
				curso = cursos.iterator().next();
			}
			if (isPortalCoordenadorGraduacao()) {
				curso = SigaaHelper.getCursoAtualCoordenacao();
			}
			
			if (curso == null) {
				addMensagem(MensagensTurmaVirtual.CURSO_NAO_IDENTIFICADO);
				return null;
			}
			
			Forum f = fDao.findForumMensagensByIDCurso( curso.getId() );
			setForumSelecionado(curso.getId());
			ForumMensagem fm = new ForumMensagem();
			
			if (getParameterInt("idForumMensagem") == null) {
				object.setForum(f);
			} else {
				
				fm.setId(getParameterInt("idForumMensagem"));
	
				object.setForum(f);
				object.setTopico(fm);
			}
	
			prepareMovimento(SigaaListaComando.CADASTRAR_TOPICO_FORUM);
			object.setCurso(true);
			MovimentoCadastro movCad = new MovimentoCadastro();
			movCad.setObjMovimentado(object);
			movCad.setCodMovimento(SigaaListaComando.CADASTRAR_TOPICO_FORUM);

			if (isEmpty(object.getConteudo())) {
				addMensagemErro("Não é possível cadastrar mensagem que não possua conteúdo.");
				return irParaForum();
			}
			
			if ( fDao.existMensagensDuplicadaByForumUsuario(object.getTopico().getId(), getUsuarioLogado().getId(), object.getConteudo()) ) {
				addMensagemErro("Não é possível cadastrar respostas duplicadas.");
				return irParaForum();
			}
			execute(movCad);
			
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e);
		} catch (ArqException e) {
			tratamentoErroPadrao(e, "Não foi possível cadastrar sua resposta para esse Fórum.");
			return forward("/graduacao/" + getClasse().getSimpleName() + "/listar.jsp");
		}

		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

		listaForunsPorCurso = getMensagensPaginadas(getIdForumMensagemSelecionado());
		if (listaForunsPorCurso.size() >= 1)
			tituloCursoDiscutidoAtualmente = listaForunsPorCurso.get(0).getTitulo();

		object = new ForumMensagem();
		pageLast();
		return forward("/graduacao/" + getClasse().getSimpleName() + "/mostrar.jsp");
	}

	/**
	 * Direciona para a lista de mensagens de um tópico.
	 * 
	 * @throws ArqException
	 */
	private String irParaForum() throws ArqException {
		listaForunsPorCurso = getMensagensPaginadas(getIdForumMensagemSelecionado());
		if (listaForunsPorCurso.size() >= 1)
			tituloCursoDiscutidoAtualmente = listaForunsPorCurso.get(0).getTitulo();

		object = new ForumMensagem();
		pageLast();
		return forward("/graduacao/" + getClasse().getSimpleName() + "/mostrar.jsp");
	}

	/**
	 * Quando criada nova mensagem envia para o participantes do fórum em questão. 
	 * 
	 * @param object
	 */
	private void enviarEmailNotificacaoTodosAlunosForum(ForumMensagem object, UploadedFile anexo) {

		ForumDao dao = getDAO(ForumDao.class);

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
	 * Cadastra uma mensagem para o fórum que estiver aberto no momento .<br/><br/>
	 *
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/ForumMensagem/novo.jsp
	 * @return
	 * @throws DAOException
	 */
	public String cadastrarMensagemForumCursos() throws DAOException {
		
		if( !isOperacaoAtiva(SigaaListaComando.CADASTRAR_AVA.getId())  ){
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return super.cancelar();
		}
		
		validacaoTextArea();
		eUmForumDeCurso = true;
		ForumDao fDao = null;
		
		try {
			fDao = getDAO(ForumDao.class);
			
			Integer[] values = getParameterIntValues("cursoStricto");
				
			List<Curso> cursos = new ArrayList<Curso>();
			List<Curso> cursosSelecionados = new ArrayList<Curso>();
	
			if (isPortalCoordenadorLato())  { // verificar se o usuário é coordenador de Lato
				Curso c = (Curso) getCurrentSession().getAttribute("cursoAtual");
				cursosSelecionados.add(c);
			} 
			
			if (isPortalDiscente() && getUsuarioLogado().getDiscenteAtivo() != null && getUsuarioLogado().getDiscenteAtivo().getCurso() != null) {
				Curso c = getUsuarioLogado().getDiscenteAtivo().getCurso();
				cursosSelecionados.add(c);
			}
			
			if (isPortalCoordenadorStricto()) { // verificar se o usuário é coordenador de Stricto
				Unidade programa = (Unidade) getCurrentSession().getAttribute("programaAtual");
				cursos = CollectionUtils.toList( getDAO(CursoDao.class).findByPrograma(programa.getId()) );
			}
	
			if (isPortalCoordenadorGraduacao()) { // verificar se o usuário é coordenador de Graduação
				Curso curso = SigaaHelper.getCursoAtualCoordenacao();
				cursosSelecionados.add(curso);
			}
			
			if (getCurrentSession().getAttribute("cursoAtual") != null && !cursosSelecionados.contains(getCurrentSession().getAttribute("cursoAtual"))) {
				cursosSelecionados.add((Curso) getCurrentSession().getAttribute("cursoAtual"));
			}
	
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					for (int j = 0; j < cursos.size(); j++) {
						if ( values[i].equals(cursos.get(j).getId()) ) {
							cursosSelecionados.add(cursos.get(j));
						}
					}
				}
			}
			
			if ( isEmpty(cursosSelecionados) ) {
				addMensagem(MensagensTurmaVirtual.CURSO_NAO_IDENTIFICADO );
				return null;
			}			
				
			boolean sucesso = true;
			ForumMensagem forumAtual = UFRNUtils.deepCopy(object);
			for (Curso curso : cursosSelecionados) {
				
				Forum f = fDao.findForumMensagensByIDCurso(curso.getId() );
				sucesso = cadastrarMensagens(fDao, sucesso, forumAtual, f);
			}
			
			if (sucesso)
				flash("Cadastrado com sucesso.");
			
			return listarForunsPorCurso();
		}finally {
			if (fDao != null)
				fDao.close();
		}	
	}

	/** Cadastra a mensagem no fórum.
	 * @param fDao
	 * @param sucesso
	 * @param forumAtual
	 * @param f
	 * @return
	 * @throws DAOException
	 */
	private boolean cadastrarMensagens(ForumDao fDao, boolean sucesso,
			ForumMensagem forumAtual, Forum f) throws DAOException {
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
			ForumMensagem fm = fDao.findForumMensagensByID(getParameterInt("idX"));

			if (fm == null) {
				object.setForum(f);
				object.setTopico(object.getTopico());
			} else {
				object.setForum(f);
				object.setTopico(fm.getTopico());

			}
		}

		if (eUmForumDeCurso)
			object.setCurso(true);
		
		

		if (arquivo != null)
			anexarArquivoAoForum();

		object.setUltimaPostagem(new Date());
		
		Notification notification = new Notification();
		if ( getUltimoComando().equals(SigaaListaComando.CADASTRAR_AVA) )
			notification = executeForumCursos(SigaaListaComando.CADASTRAR_AVA, object, getEspecificacaoCadastroTopicos());
		
		if (notification.hasMessages()) {
			sucesso = false;
			notifyView(notification);
			return sucesso;
		} else {
			if (notificar) {
				enviarEmailNotificacaoTodosAlunosForum(object, arquivo);
			}
			object = new ForumMensagem();
			object = UFRNUtils.deepCopy(forumAtual);
		}
		return sucesso;
	}

	/**
	 * Serve para baixar o arquivo postado no fórum.<br/><br/>
	 * Não invocado por jsp
	 * @return
	 */
	public String baixarArquivoAnexadoForum() {
		return redirectJSF(getContextPath() + "/verProducao?idProducao="
				+ listaForunsPorCurso.get(0).getIdArquivo());
	}

	/**
	 * Serve para anexar um arquivo ao fórum em questão.<br/><br/>
	 * Não invocado por jsp
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
	 * Notifica quando um fórum em curso for aberto. 
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
	 * Serve para evitar que quando o usuário segure um tecla sem que haja quebra de linha, 
	 * para evitar que se tenha uma barra de rolagem na horizontal.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
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
	 * Retorna as mensagens do Fórum pra mostrar no portal do discente.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/portais/discente/discente.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<ForumMensagem> getForumMensagemPortalDiscente() throws DAOException {
		getPaginacao().setPaginaAtual(0);
		if (forumMensagemPortalDiscente == null) {
			if( getDiscenteUsuario()!=null)
				if (getDiscenteUsuario().getCurso() != null)  {
					if (getListaForunsPorCurso() != null) {
						if ( getListaForunsPorCurso() == null )
							forumMensagemPortalDiscente = new ArrayList<ForumMensagem>();
						else
							forumMensagemPortalDiscente = getListaForunsPorCurso();
					} else {
						Forum forum = getDAO(ForumDao.class).findForumMensagensByIDCurso(getDiscenteUsuario().getCurso().getId() );
						if (forum != null) {
							setForum(forum);
			
							List<ForumMensagem> lista = (List<ForumMensagem>) getDAO(ForumDao.class).findListaMensagensForumByIDForum(forum.getId());
							if (lista != null)
								setListaForunsPorCurso(lista);
						}
						usuarioAtivo = getDiscenteUsuario().isAtivo();
					}
			}
			
			if ( getListaForunsPorCurso() == null )
				forumMensagemPortalDiscente = new ArrayList<ForumMensagem>();
			else
				forumMensagemPortalDiscente = getListaForunsPorCurso();
		}
		
		return forumMensagemPortalDiscente;
	}	
	
	/**
	 * Limpa os fóruns do portal discente após a renderização do mesmo.
	 * Utilizado para evitar múltiplas chamadas ao método getForumMensagemPortalDiscente(), o que
	 * causava a realização de diversas consultas desnecessárias.
	 * Método chamado pela seguinte JSP: /sigaa.war/portais/discente/discente.jsp
	 * @return
	 */
	public String getLimparForumMensagemPortalDiscente() {
		forumMensagemPortalDiscente = null;
		return null;
	}
	
	/**
	 * Retorna as mensagens do Fórum pra mostrar no portal coordenador de Graduação.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/coordenador.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<ForumMensagem> getForumMensagemCoordenacaoGraducao() throws DAOException {
		
		if (getListaForunsPorCurso() != null)
			return getListaForunsPorCurso();
		
		PortalCoordenadorGraduacaoMBean mBean = getMBean("portalCoordenadorGrad");
		buscarMensagensPorCurso(mBean.getCursoAtualCoordenacao());
		
		return getListaForunsPorCurso();
	}
	
	/**
	 * Retorna as mensagens do Fórum pra mostrar no portal coordenador de Stricto .<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/stricto/coordenacao.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<ForumMensagem> getForumMensagemCoordenacaoStricto() throws DAOException {

		if (getListaForunsPorCurso() != null)
			return getListaForunsPorCurso();
		
		PortalCoordenacaoStrictoMBean bean = getMBean("portalCoordenacaoStrictoBean");
		Unidade programa = bean.getProgramaStricto();
		buscarMensagensPorPrograma(programa);
		return getListaForunsPorCurso();
	}	
	
	/**
	 * Retorna as mensagens do Forum pra mostrar no portal coordenador de Lato.<br/><br/>
	 * Método chamado pela seguinte JSP: /sigaa.war/lato/coordenador.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<ForumMensagem> getForumMensagemCoordenacaoLato() throws DAOException {
		
		if (getListaForunsPorCurso() != null)
			return getListaForunsPorCurso();
		
		PortalCoordenadorLatoMBean mBean = getMBean("portalCoordenadorLato");
        buscarMensagensPorCurso(mBean.getCursoAtualCoordenacao());
        
        return getListaForunsPorCurso();
	}
	
	/**
	 * <p>
	 * Verifica se o coordenador logado é o coordenador do curso escolhido na lista de fóruns.
	 * (Também usado quando o usuário entra pelo portal da coordenação)
	 * </p>
	 * <p>
	 * É usado para habilitar/desabilitar a remoção de tópicos
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
	
	// MÉTODOS PARA ENSINO EM REDE
	
	/**
	 * Retorna as mensagens do Fórum pra mostrar no portal coordenador de ensino em rede.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>sigaa.war/ensino_rede/portal/portal.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<ForumMensagem> getForumMensagemCoordenacaoEnsinoRede() throws DAOException {
		
		if (getListaForunsPorPrograma() != null)
			return getListaForunsPorPrograma();
		
		ProgramaRede programa = getProgramaRede();
		buscarMensagensPorProgramaRede(programa);
		
		return getListaForunsPorPrograma();
	}
	
	/**
	 * Busca todas as mensagens de um determinado programa.<br/><br/>
	 * Não invocado por JSP
	 * 
	 * @param programa
	 * @throws DAOException
	 */
	public void buscarMensagensPorProgramaRede(ProgramaRede programa) throws DAOException {
		if (programa != null) {
			Forum f = getDAO(ForumDao.class).findForumMensagensByIDPrograma(programa.getId());
			forum = f;
			int numForunsPaginados = ParametroHelper.getInstance().getParametroInt(ParametrosPortalDocente.NUMERO_FORUNS_PAGINACAO_FORUNS_CURSO);
			setTamanhoPagina(numForunsPaginados);
			if (filtro == null || filtro.isEmpty())
				listaForunsPorPrograma = (ArrayList<ForumMensagem>) getDAO(ForumDao.class).findListaMensagensForumByIDForum(f.getId(), getPaginacao());
			else
				listaForunsPorPrograma = getDAO(ForumDao.class).findMensagensByIdForumFiltro(f.getId(),filtro,getPaginacao());
		}	
	}
	
	/**
	 * Mostra os fóruns e as mensagem que o programa dispõe.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>sigaa.war/ensino_rede/portal/portal.jsp</li>
	 * 		<li>/sigaa.war/ensino_rede/forum_mensagem_rede/listar.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String mostrarForumMensagemPrograma() throws ArqException {
		
		if(getUsuarioLogado().getDiscenteAtivo() != null)
			usuarioAtivo = getUsuarioLogado().getDiscenteAtivo().isAtivo();
		
		object = new ForumMensagem();
		int idForumMensagem = 0;
		if (getParameterInt("idForumMensagem") != null) {
			idForumMensagem = getParameterInt("idForumMensagem");
			setIdForumMensagemSelecionado(idForumMensagem);
		}
		
		clearPaginacao();
		listaForunsPorPrograma = getMensagensPaginadas(getIdForumMensagemSelecionado());
		eUmForumDePrograma = true;
		
		if (listaForunsPorPrograma.size() >= 1)
			tituloProgramaDiscutidoAtualmente = listaForunsPorPrograma.get(0).getTitulo();

		return forward("/ensino_rede/forum_mensagem_rede/mostrar.jsp");
	}
	
	/**
	 * Carrega as mensagens do fórum
	 * @throws DAOException
	 */
	private void getMensagensPrograma() throws DAOException {
		eUmForumDePrograma = true;

		if (getParameterInt("id") != null) {
			int id = getParameterInt("id");
			setForumSelecionado(id);
		}

		ProgramaRede programa = getProgramaRede();
		buscarMensagensPorProgramaRede(programa);
	}
	
	/**
	 * Remove um tópico com todas as suas mensagens.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>sigaa.war/ensino_rede/portal/portal.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public String removerMensagensPrograma() throws DAOException {
		try {
			getDAO(ForumDao.class).removerTopicosComFilhos(
					getParameterInt("id"));
			flash("O Tópico e todas as mensagens do mesmo foram removidas com sucesso.");
		} catch (Exception e) {
			addMensagem(MensagensArquitetura.REMOCAO_OBJETO_ASSOCIADO);
			throw new DAOException(
					"Existem registros associados ao que você está tentando remover");
		}

		return listarForunsPorPrograma();
	}
	
	/**
	 * Cadastra uma mensagem para o fórum que estiver aberto no momento .<br/><br/>
	 *
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * 		<li>sigaa.war/ensino_rede/forum_mensagem_rede/novo.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String cadastrarMensagemForumProgramas() throws DAOException {
		
		if( !isOperacaoAtiva(SigaaListaComando.CADASTRAR_AVA.getId())  ){
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		
		validacaoTextArea();
		eUmForumDeCurso = true;
		ForumDao fDao = null;
		
		try {
			fDao = getDAO(ForumDao.class);
					
			ProgramaRede programa = getProgramaRede();
			
			boolean sucesso = true;
			ForumMensagem forumAtual = UFRNUtils.deepCopy(object);
				
			Forum f = fDao.findForumMensagensByIDPrograma(programa.getId() );
			sucesso = cadastrarMensagens(fDao, sucesso, forumAtual, f);

			if (sucesso) {
				flash("Cadastrado com sucesso.");
				removeOperacaoAtiva();
				return listarForunsPorPrograma();
			} else 
				return null;
		}finally {
			if (fDao != null)
				fDao.close();
		}	
	}
	
	/**
	 * Cadastra novo fórum de programa.<br/><br/>
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * 		<li>sigaa.war/ensino_rede/forum_mensagem_rede/listar.jsp</li>
	 * </ul>
	 * @return
	 */
	public String novoForumPrograma() {
		try {	
			novoForum();		
			return forward("/ensino_rede/forum_mensagem_rede/novo.jsp");
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		}
	}
	
	/**
	 * Cadastra uma resposta de um Fórum de Programa.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * 		<li>sigaa.war/ensino_rede/forum_mensagem_rede/mostrar.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws ArqException
	 */
	public String responderTopicoForumProgramas() throws DAOException {

		try {
			
			ForumDao fDao = getDAO(ForumDao.class);
			
			ProgramaRede programa = getProgramaRede();
						
			if (programa == null) {
				addMensagemErro("Programa não identificado");
				return null;
			}
			
			Forum f = fDao.findForumMensagensByIDPrograma(programa.getId());
			setForumSelecionado(programa.getId());
			ForumMensagem fm = new ForumMensagem();
			
			if (getParameterInt("idForumMensagem") == null) {
				object.setForum(f);
			} else {
				
				fm.setId(getParameterInt("idForumMensagem"));
	
				object.setForum(f);
				object.setTopico(fm);
			}
	
			prepareMovimento(SigaaListaComando.CADASTRAR_TOPICO_FORUM);
			object.setCurso(false);
			MovimentoCadastro movCad = new MovimentoCadastro();
			movCad.setObjMovimentado(object);
			movCad.setCodMovimento(SigaaListaComando.CADASTRAR_TOPICO_FORUM);

			if (isEmpty(object.getConteudo())) {
				addMensagemErro("Não é possível cadastrar mensagem que não possua conteúdo.");
				return irParaForumPrograma();
			}
			
			if ( fDao.existMensagensDuplicadaByForumUsuario(object.getTopico().getId(), getUsuarioLogado().getId(), object.getConteudo()) ) {
				addMensagemErro("Não é possível cadastrar respostas duplicadas.");
				return irParaForumPrograma();
			}
			execute(movCad);
			
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e);
		} catch (ArqException e) {
			tratamentoErroPadrao(e, "Não foi possível cadastrar sua resposta para esse Fórum.");
			return forward("/ensino_rede/forum_mensagem_rede/listar.jsp");
		}

		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

		listaForunsPorPrograma = getMensagensPaginadas(getIdForumMensagemSelecionado());
		if (listaForunsPorPrograma.size() >= 1)
			tituloProgramaDiscutidoAtualmente = listaForunsPorPrograma.get(0).getTitulo();

		object = new ForumMensagem();
		pageLast();
		return forward("/ensino_rede/forum_mensagem_rede/mostrar.jsp");
	}
	
	/**
	 * Lista todos os fóruns por programa.<br/><br/>
	 * Não invocado por JSP
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarForunsPorPrograma() throws DAOException {
		getMensagensPrograma();
		return forward("/ensino_rede/forum_mensagem_rede/listar.jsp");
	}
	
	/** 
	 * Retorna o programa de ensino em rede do usuário
	 */
	private ProgramaRede getProgramaRede() {
		ProgramaRede programa = null;
		if ( getUsuarioLogado().getVinculoAtivo().getTipoVinculo() instanceof TipoVinculoCoordenacaoGeralRede ){
			TipoVinculoCoordenacaoGeralRede vinculo = (TipoVinculoCoordenacaoGeralRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
			programa = vinculo.getCoordenacao().getProgramaRede();
		} else {
			PortalCoordenadorRedeMBean mBean = getMBean("portalCoordenadorRedeBean");
			programa = mBean.getProgramaRede();
		}
		return programa;
	}
	
	/**
	 * Direciona para a lista de mensagens de um tópico.
	 * 
	 * @throws ArqException
	 */
	private String irParaForumPrograma() throws ArqException {
		listaForunsPorPrograma = getMensagensPaginadas(getIdForumMensagemSelecionado());
		if (listaForunsPorCurso.size() >= 1)
			tituloProgramaDiscutidoAtualmente = listaForunsPorPrograma.get(0).getTitulo();

		object = new ForumMensagem();
		pageLast();
		return forward("/ensino_rede/forum_mensagem_rede/mostrar.jsp");
	}
	
	/**
	 * <p>
	 * Verifica se o usuário logado está com o vínculo de gestor do programa escolhido na lista de fóruns.
	 * </p>
	 * <p>
	 * É usado para habilitar/desabilitar a remoção de tópicos
	 * </p>
	 * JSP: /sigaa.war/graduacao/ForumMensagem/listar.jsp
	 * @return
	 */
	public boolean isGestorDoProgramaEscolhido() {
		
		boolean coordenadorDoPrograma = false;

		if ( getUsuarioLogado().getVinculoAtivo().getTipoVinculo() instanceof TipoVinculoCoordenacaoGeralRede )
			coordenadorDoPrograma = true;
		
		return coordenadorDoPrograma;
	}
	
	/**
	 * Retorna o usuário logado.<br/><br/>
	 * Não invocado por JSP
	 * @return
	 */
	public int getIdUsuarioLogado() {
		Usuario u = (Usuario) getCurrentSession().getAttribute("usuario");
		return u.getId();
	}
	
	public int getIdForumMensagemSelecionado() {
		return idForumMensagemSelecionado;
	}

	public void setIdForumMensagemSelecionado(int idForumMensagemSelecionado) {
		this.idForumMensagemSelecionado = idForumMensagemSelecionado;
	}

	public String getTituloCursoDiscutidoAtualmente() {
		return tituloCursoDiscutidoAtualmente;
	}

	public void setTituloCursoDiscutidoAtualmente(
			String tituloCursoDiscutidoAtualmente) {
		this.tituloCursoDiscutidoAtualmente = tituloCursoDiscutidoAtualmente;
	}
	
	/**
	 * Inicia o caso de uso de cadastrar um tópico de fórum e volta para a turma virtual.<br/><br/>
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
	 * Não invocado por JSP
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

	public Collection<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(Collection<Curso> cursos) {
		this.cursos = cursos;
	}

	/**
	 * Retorna o usuário ativo
	 * @return the alunoAtivo
	 */
	public boolean isUsuarioAtivo() {
		return usuarioAtivo;
	}

	/**
	 * Seta o usuário ativo
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

	public void setFiltro(String filtro) {
		this.filtro = filtro;
	}

	public String getFiltro() {
		return filtro;
	}

	public DenunciaMensagem getDenuncia() {
		return denuncia;
	}

	public void setDenuncia(DenunciaMensagem denuncia) {
		this.denuncia = denuncia;
	}
	
	public void setListaForunsPorPrograma(List<ForumMensagem> listaForunsPorPrograma) {
		this.listaForunsPorPrograma = listaForunsPorPrograma;
	}

	/** Retorna uma coleção de mensagens de um fórum por programa.
	 * @return
	 * @throws DAOException
	 */
	public List<ForumMensagem> getListaForunsPorPrograma() throws DAOException {
		if (getPaginacao().getPaginaAtual() != paginaAtual) {
			getMensagensPrograma();
			paginaAtual = getPaginacao().getPaginaAtual();
		}
		
		return listaForunsPorPrograma;
	}

	/** Seta que o fórum é de um programa.
	 * @param eUmForumDePrograma
	 */
	public void seteUmForumDePrograma(boolean eUmForumDePrograma) {
		this.eUmForumDePrograma = eUmForumDePrograma;
	}

	/** Indica que o fórum é de um programa.
	 * @return
	 */
	public boolean iseUmForumDePrograma() {
		return eUmForumDePrograma;
	}

	public void setTituloProgramaDiscutidoAtualmente(
			String tituloProgramaDiscutidoAtualmente) {
		this.tituloProgramaDiscutidoAtualmente = tituloProgramaDiscutidoAtualmente;
	}

	public String getTituloProgramaDiscutidoAtualmente() {
		return tituloProgramaDiscutidoAtualmente;
	}
}