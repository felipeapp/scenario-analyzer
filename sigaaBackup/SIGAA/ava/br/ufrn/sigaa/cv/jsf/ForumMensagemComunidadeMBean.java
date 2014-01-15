/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.cv.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UICommand;
import javax.faces.event.ActionEvent;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.cv.dao.ForumComunidadeDao;
import br.ufrn.sigaa.ava.dao.ForumDao;
import br.ufrn.sigaa.ava.dominio.ForumMensagem;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.cv.dominio.ForumComunidade;
import br.ufrn.sigaa.cv.dominio.ForumMensagemComunidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;

/**
 * Managed-Bean responsável por postar mensagens no fórum da Comunidade Virtual
 * 
 */
@Component @Scope("session")
public class ForumMensagemComunidadeMBean extends PaginacaoComunidadeController <ForumMensagemComunidade> {

	private UploadedFile arquivo;	
	
	private boolean notificar;
	private int forumSelecionado;
	private int idForumMensagemSelecionado;

	private int discenteLogado;

	private boolean eUmForumDeCurso;
	private String tituloCursoDiscutidoAtualmente;
	private ForumComunidade forum;

	/** 
	 * INICIO PAGINAÇÃO
	 */
	
    private List<ForumMensagemComunidade> mensagensPaginadas;
    private Integer totalRegistros;
    private int primeiroRegistro;
    private int quantRegistrosPagina = 20;
    private Integer[] pages = new Integer[0];
    private int paginaAtual;
	
    /**
     * Carrega os dados por demanda de acordo com o ID
     * informado.
     * 
     * @throws DAOException
     */
	private void loadMensagensPaginadasForum (int idMensagem) throws DAOException {

		ForumComunidadeDao dao = getDAO(ForumComunidadeDao.class);
		
		mensagensPaginadas = dao.findMensagensByTopico(idMensagem, primeiroRegistro, quantRegistrosPagina);
		totalRegistros = dao.countMensagensForumCursos(idMensagem);
		
		listagem = mensagensPaginadas;
		
        paginaAtual = (totalRegistros / quantRegistrosPagina) - ((totalRegistros - primeiroRegistro) / quantRegistrosPagina) + 1;
        int totalPages = (totalRegistros / quantRegistrosPagina) + ((totalRegistros % quantRegistrosPagina != 0) ? 1 : 0);
        pages = new Integer[totalPages];
        for (int i = 0; i < totalPages; i++) {
            pages[i] = i + 1;
        }
	}
	
	/**
	 * Retorna as mensagens paginadas.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp
	 * 
	 * @param idMensagem
	 * @return
	 * @throws DAOException
	 */
	public List<ForumMensagemComunidade> getMensagensPaginadas(int idMensagem) throws DAOException {
		loadMensagensPaginadasForum(idMensagem);
		return mensagensPaginadas;
	}
	
	/**
	 * Define o primeiro registro
	 * 
	 * @param firstRow
	 * @throws DAOException
	 */
    private void page(int firstRow) throws DAOException {
        this.primeiroRegistro = firstRow;
        loadMensagensPaginadasForum(getIdForumMensagemSelecionado());
    }
    
    /**
     * Vai para a primeira página.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp
     * 
     * @throws DAOException
     */
    public void pageFirst() throws DAOException {
        page(0);
    }

    /**
     * Avança uma página.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp
     * 
     * @throws DAOException
     */
    public void pageNext() throws DAOException {
        page(primeiroRegistro + quantRegistrosPagina);
    }

    /**
     * Volta uma página.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp
     * 
     * @throws DAOException
     */
    public void pagePrevious() throws DAOException {
        page(primeiroRegistro - quantRegistrosPagina);
    }

    /**
     * Vai para a última página.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp
     * 
     * @throws DAOException
     */
    public void pageLast() throws DAOException {
        page(totalRegistros - ((totalRegistros % quantRegistrosPagina != 0) ? totalRegistros % quantRegistrosPagina : quantRegistrosPagina));
    }

    public void page(ActionEvent event) throws DAOException {
        page(((Integer) ((UICommand) event.getComponent()).getValue() - 1) * quantRegistrosPagina);
    }

	public void setDataList(List<ForumMensagemComunidade> mensagensPaginadas) {
		this.mensagensPaginadas = mensagensPaginadas;
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

	public int getCurrentPage() {
		return paginaAtual;
	}

	public void setCurrentPage(int currentPage) {
		this.paginaAtual = currentPage;
	}

	/**
	 * Reseta a paginação
	 */
	public void clearPaginacao() {
		paginaAtual = 0;
		primeiroRegistro = 0;
		totalRegistros = 0;
	}

	/** 
	 * FIM PAGINAÇÃO
	 */
    
	/**
	 * Construtor
	 */
	public ForumMensagemComunidadeMBean() {
		classe = ForumMensagemComunidade.class;
		setTamanhoPagina(10);
	}

	/**
	 * Vai para a listagem de mensagens de um tópico.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ForumMensagemComunidade/listar.jsp
	 */
	@Override
	public String mostrar() throws DAOException {
		
		object = new ForumMensagemComunidade();
		
		Integer idForumMensagem = getParameterInt("idForumMensagem");
		if (idForumMensagem != null)
			setIdForumMensagemSelecionado(idForumMensagem);

		clearPaginacao();
		listagem = getMensagensPaginadas(getIdForumMensagemSelecionado());
		return forward("/cv/" + classe.getSimpleName() + "/mostrar.jsp");
	}


	/**
	 * Lista os tópicos de acordo com o fórum.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ForumMensagemComunidade/listar.jsp
	 */
	@Override
	public List<ForumMensagemComunidade> lista() {
		Integer id = getParameterInt("id"); 
		try {
			if (id != null) {
				setForumSelecionado(id);
				forum = getGenericDAO().findByPrimaryKey(id, ForumComunidade.class);
			}
	
			if (!eUmForumDeCurso)
					return getDAO(ForumComunidadeDao.class).findListaMensagensForumByIDForum(getForumSelecionado(), getPaginacao(), crescente);
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}

		return new ArrayList<ForumMensagemComunidade>();
	}

	/**
	 * Redireciona usuário para página de listagem de tópicos.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp</li>
	 * 	<li>/sigaa.war/cv/ForumMensagemComunidade/novo.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		try {
			listagem = getMensagensPaginadas(getIdForumMensagemSelecionado());
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return redirect("/cv/" + classe.getSimpleName() + "/listar.jsf");
	}

	/**
	 * Remove uma mensagem ou tópico de um fórum.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/cv/ForumMensagemComunidade/listar.jsp</li>
	 * 	<li>/sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() {
		Integer topico = null;
		try {
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), classe);
			topico = (object.getTopico() == null ? null : object.getTopico().getId()); 
			antesRemocao();
			prepare(ArqListaComando.DESATIVAR);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(object);
			mov.setCodMovimento(ArqListaComando.DESATIVAR);

			execute(mov);
			
			object = new ForumMensagemComunidade();
			flash(" Mensagem do Fórum removido com sucesso.");

			if (getParameterBoolean("topico")) { // se for tópico redireciona pra a lista de tópicos
				listagem = null;
				return redirect("/cv/" + classe.getSimpleName() + "/listar.jsf");
			} else { // se for mensagem redireciona para a lista de mensagens de um tópico
				listagem = getDAO(ForumComunidadeDao.class).findMensagensByTopico(topico, 0, 0);
				return redirect("/cv/" + classe.getSimpleName() + "/mostrar.jsf");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			addMensagemWarning("Existem registros associados ao que você está tentando remover que inviabilizam sua remoção");
			return null;
		}

	}

	/**
	 * Padrão Specification. Verifica os preenchimento dos campos antes do cadastro.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp</li>
	 * 	<li>/sigaa.war/cv/ForumMensagemComunidade/novo.jsp</li>
	 * </ul>
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
					notification.addError("É obrigatório informar o conteúdo da mensagem!");

				return !notification.hasMessages();
			}
		};
	}

	/**
	 * Cadastra um tópico em um fórum e envia e-mail aos participantes.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ForumMensagemComunidade/novo.jsp
	 * @return
	 * @throws ArqException
	 */
	public String cadastrarTopico() throws ArqException {

		if ( object.getTitulo().equals("") || object.getConteudo().equals("") ) {
			addMensagemErro("Campos Título e Conteúdo devem ser preenchidos!");
			return null;
		}
		else {
			ForumMensagemComunidade cadObject = object;
			cadastrarTopicoMensagem(); // cadastra o tópico
			
			flash("Cadastrado com sucesso.");
	
			if (notificar)
				notificarComunidade("Novo Tópico de Fórum",
						"Um novo tópico de discussão foi criado no fórum da turma: "
								+ "<br><br>Título: " + cadObject.getTitulo()
								+ "<br><br>" + cadObject.getConteudo() + "<BR>"
								+ "Postado por: " + getUsuarioLogado().getNome());
	
			listagem = getDAO(ForumComunidadeDao.class)
					.findListaForumMensagensByIDForum(getForumSelecionado(),
							getIdForumMensagemSelecionado());
			listagem = null;
			return redirect("/cv/" + classe.getSimpleName() + "/listar.jsf");
		}
	}

	/**
	 * Método que prepara o Movimento para o processador e serve para cadastrar 
	 * tanto um tópico de fórum como uma resposta de um tópico existente.  
	 * 
	 * @throws ArqException
	 */
	private void cadastrarTopicoMensagem() throws ArqException {

		prepareMovimento(SigaaListaComando.CADASTRAR_TOPICO_FORUM_COMUNIDADE);

		ForumComunidade f = new ForumComunidade();
		f.setId(getForumSelecionado());

		object.setForum(f);
		ForumMensagemComunidade topico = new ForumMensagemComunidade();
		if (getParameterInt("idForumMensagem") != null) {
			topico.setId(getParameterInt("idForumMensagem"));
			object.setTopico(topico);
		} else if (topico == null){
			object.setTopico(null);
		}

		object.setComunidade(comunidade());

		MovimentoCadastro movCad = new MovimentoCadastro();
		movCad.setObjMovimentado(object);
		movCad.setCodMovimento(SigaaListaComando.CADASTRAR_TOPICO_FORUM_COMUNIDADE);

		try {
			execute(movCad);
		} catch (NegocioException e) {
			e.printStackTrace();
		}
		
		if ( getParameterInt("idForumMensagem") != null || idForumMensagemSelecionado != 0) {
			// caso seja null é por que é um tópico ao invés de mensagens
			
			if (getParameterInt("idForumMensagem") != null)
				idForumMensagemSelecionado = getParameterInt("idForumMensagem");
			
			listagem = getMensagensPaginadas(idForumMensagemSelecionado);
		}
		
		object = new ForumMensagemComunidade();
	}

	/**
	 * Cadastrar a resposta a uma mensagem.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ForumMensagemComunidade/mostrar.jsp
	 */
	public String cadastrarResposta() throws ArqException {

			ForumMensagemComunidade objOrig = object;
			ForumMensagemComunidade topico = null;
			
			ForumDao dao = getDAO(ForumDao.class);
			if (getParameterInt("idForumMensagem") != null) {
				topico = dao.findByPrimaryKey(getParameterInt("idForumMensagem"), ForumMensagemComunidade.class);
			}
			else {
				topico = dao.findByPrimaryKey(idForumMensagemSelecionado, ForumMensagemComunidade.class);
				topico.setId(idForumMensagemSelecionado);
				object.setTopico(topico);
			}
			
				// cadastra a resposta
				cadastrarTopicoMensagem();
	
				// envia e-mail para os participantes do tópico
				if (notificar) {
					notificarComunidade("Resposta ao Tópico " + topico.getTitulo(),objOrig.getConteudo());
				}	
				flash("Resposta cadastrada com sucesso.");
		listagem = getMensagensPaginadas(getIdForumMensagemSelecionado());
		pageLast();
		
		return forward("/cv/" + classe.getSimpleName() + "/mostrar.jsp");
	}
	
	/**
	 * Remove uma mensagem de um tópico
	 */
	public String removerMensagensTopicos() {

		try {
			
			object = getDAO(ForumComunidadeDao.class).findForumMensagemComunidade(getParameterInt("id"));
			
			prepareMovimento(SigaaListaComando.REMOVER_TOPICO_FORUM_COMUNIDADE);

			MovimentoCadastro movCad = new MovimentoCadastro();
			movCad.setObjMovimentado(object);
			movCad.setCodMovimento(SigaaListaComando.REMOVER_TOPICO_FORUM_COMUNIDADE);
	
			execute(movCad);
			
			flash("Mensagem removida com sucesso.");

			listagem = getDAO(ForumComunidadeDao.class).findListaMensagensForumByIDForum(getForumSelecionado(), getPaginacao(), crescente);
			
			if (listagem.size() >= 1) {
				return irParaForum();
			} else {
				listagem = getDAO(ForumComunidadeDao.class).findListaMensagensForumByIDForum(getForumSelecionado(), getPaginacao(), crescente);
				return forward("/cv/" + classe.getSimpleName() + "/listar.jsp");
			}
				
		} catch (Exception e) {
			addMensagem(MensagensTurmaVirtual.REMOVER_PRIMEIRA_MENSAGEM);
			return  null;
		}
	}
	
	public List <ForumMensagemComunidade> getListagemOrdenada() throws DAOException {
		
		if (listagem == null){
			
			ForumComunidadeDao dao = null;
			try {
				dao = getDAO(ForumComunidadeDao.class);
				listagem = dao.findListaMensagensForumByIDForum(getForumSelecionado(), getPaginacao(), crescente);
				
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		return listagem;
	}

	/**
	 * Reseta a listagem de tópicos e redireciona para página de listagem.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ForumMensagemComunidade/listar.jsp
	 */
	@Override
	public String listar() {
		iniciarListagem();
		
		// Quando o usuário entra na listagem pela primeira vez, pega a ordem da configuração da comunidade.
		ComunidadeVirtualMBean cvBean = getMBean("comunidadeVirtualMBean");
		if (cvBean.getComunidade() != null)
			crescente = cvBean.getComunidade().isOrdemCrescente();
		
		return forward("/cv/ForumMensagemComunidade/listar.jsp");
	}
	
	/**
	 * Direciona para a lista de mensagens de um tópico
	 */
	private String irParaForum() throws DAOException {

		listagem = getMensagensPaginadas(getIdForumMensagemSelecionado());
		if (listagem.size() >= 1)
			tituloCursoDiscutidoAtualmente = listagem.get(0).getTitulo();

		object = new ForumMensagemComunidade();
		pageLast();
		return forward("/cv/" + classe.getSimpleName() + "/mostrar.jsp");
	}

	/**
	 * Instância ForumMensagemComunidade e seta em object o ForumComunidade que tenha sido selecionado.
	 */
	@Override
	public void instanciar() {

		object = new ForumMensagemComunidade();
		int idForumSelecionado = getForumSelecionado();

		try {
			ForumComunidade f = getGenericDAO().findByPrimaryKey(idForumSelecionado, ForumComunidade.class);
			object.setForum(f);
			
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

	public ForumComunidade getForum() {
		return forum;
	}

	public void setForum(ForumComunidade forum) {
		this.forum = forum;
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

	public int getIdForumMensagemSelecionado() {
		return idForumMensagemSelecionado;
	}

	public void setIdForumMensagemSelecionado(int idForumMensagemSelecionado) {
		this.idForumMensagemSelecionado = idForumMensagemSelecionado;
	}

	public String getTituloCursoDiscutidoAtualmente() {
		return tituloCursoDiscutidoAtualmente;
	}

	public void setTituloCursoDiscutidoAtualmente(String tituloCursoDiscutidoAtualmente) {
		this.tituloCursoDiscutidoAtualmente = tituloCursoDiscutidoAtualmente;
	}

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
}