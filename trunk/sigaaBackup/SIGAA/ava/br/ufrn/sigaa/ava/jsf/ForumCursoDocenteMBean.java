/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/01/2011
 *
 */
package br.ufrn.sigaa.ava.jsf;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.dao.ForumCursoDocenteDao;
import br.ufrn.sigaa.ava.dao.ForumDao;
import br.ufrn.sigaa.ava.dominio.Forum;
import br.ufrn.sigaa.ava.dominio.ForumCursoDocente;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Managed-Bean respons�vel por associar docentes a f�runs de cursos.
 * 
 * @author arlindo
 * 
 */
@Component("forumCursoDocente") @Scope("request")
public class ForumCursoDocenteMBean extends SigaaAbstractController<ForumCursoDocente> {
	
	/** Servidor selecionado */
	private Servidor servidor;
	
	/** Curso selecionado */
	private Curso curso;
	
	/** N�vel Informado */
	private Character nivel;
	
	/** Lista de F�runs por Curso */
	private List<Forum> listaForunsPorCurso = new ArrayList<Forum>();
	
	/** Lista de F�runs que o docente possui permiss�o */
	private List<ForumCursoDocente> forunsComPermissao = new ArrayList<ForumCursoDocente>();
	
	/** Construtor padr�o */
	public ForumCursoDocenteMBean() {
		init();
		
		curso = new Curso();
		servidor = new Servidor();		
	}
	
	/** Inicializa os objetos */
	private void init(){
		obj = new ForumCursoDocente();
		obj.setServidor(new Servidor());
		obj.setForum(new Forum());
		obj.getServidor().setPessoa(new Pessoa());		
	}
	
	/**
	 * Inicia o formul�rio para cadastro 
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/administracao/menus/cadastro.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar(){
		return forward("/portais/docente/forum_curso_docente.jsf");
	}
	
	/**
	 * Redireciona para a tela de adicionar forum
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/docente/forum_curso_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciarSelecaoForum() throws HibernateException, DAOException{
		
		carregaForunsDocente();
		
		return forward(getFormPage()); 
	}
	
	/**
	 * Busca os F�runs do Curso Informado
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/docente/selecao_forum.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String buscar() throws HibernateException, DAOException{
		
		if (ValidatorUtil.isEmpty(curso))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
		
		if (nivel == null || nivel == '0')
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "N�vel");
		
		if (hasErrors())
			return null;
		
		carregarForunsCurso();
		
		if (ValidatorUtil.isEmpty(listaForunsPorCurso))
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);			

		return forward(getFormPage()); 
	}

	/**
	 * Carrega os F�runs do Curso Selecionado
	 */
	private void carregarForunsCurso() {
		if (!ValidatorUtil.isEmpty(servidor) && !ValidatorUtil.isEmpty(curso))
			listaForunsPorCurso = getDAO(ForumDao.class).findForunsSemPermissaoByDocenteCurso(servidor.getId(), curso.getId() );
	}
	
	/**
	 * Busca por f�runs que o docente possui acesso
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/docente/forum_curso_docente.jsp</li>
	 * </ul>
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String buscaForunsDocente() throws HibernateException, DAOException{
		
		if (ValidatorUtil.isEmpty(servidor)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Docente");
			return null;			
		}		
		
		carregaForunsDocente();
				
		return forward(getFormPage());
	}
	
	/**
	 * Carrega os f�runs que o docente possui permiss�o
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void carregaForunsDocente() throws HibernateException, DAOException {
		if (!ValidatorUtil.isEmpty(servidor))
			forunsComPermissao = getDAO(ForumCursoDocenteDao.class).findByDocente( servidor.getId() );	
	}
	
	/**
	 * Adiciona o acesso ao f�rum
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/docente/forum_curso_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String adicionarForum() throws ArqException, NegocioException{
		if (ValidatorUtil.isEmpty(servidor)){
			addMensagemErro("Informe o Docente!");
			return null;
		}			
		
		int id = getParameterInt("id", 0);
		if (id == 0){
			addMensagemErro("F�rum n�o selecionado!");
			return null;
		}
		
		if (!ValidatorUtil.isEmpty( getGenericDAO().findByExactField(ForumCursoDocente.class, 
				new String[]{"forum.id", "servidor.id"}, 
				new String[]{String.valueOf(id), String.valueOf(servidor.getId())})) ) {
			addMensagemErro("O Docente j� possui acesso ao f�rum selecionado!");
			return null;			
		}
		
		Forum f = new Forum();
		f.setId(id);
		
		init();
		
		obj.setForum(f);
		obj.setServidor(servidor);
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		return cadastrar();
	}
	
	/**
	 * Remove o acesso ao f�rum
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/docente/forum_curso_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String removerForum() throws ArqException, NegocioException{
		populateObj(true);
		
		if (ValidatorUtil.isEmpty(obj)){
			addMensagemErro("O f�rum selecionado j� foi removido anteriormente!");
			obj = new ForumCursoDocente();
			return forward(getFormPage());			
		}
		
		prepareMovimento(ArqListaComando.REMOVER);		
		
		return cadastrar();
		
	}
	
	/**
	 * Salva o acesso ao f�rum
	 * <br/><br/>
	 * M�todo n�o chamado por JSP.
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		try {
			// Prepara o movimento, setando o objeto
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(getUltimoComando());
			execute(mov);
			
			carregarForunsCurso();				
			carregaForunsDocente();
			
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, TipoMensagemUFRN.INFORMATION);	

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		return forward(getFormPage());
	}	
	
	/**
	 * Retorna os curso em formato de combo
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/docente/forum_curso_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getCursoNivel() throws DAOException{
		CursoDao dao = getDAO(CursoDao.class);
		try {
			if ( nivel != null && nivel != ' ' )
				return toSelectItems(dao.findByNivel(nivel), "id", "descricao");
			else 
				return new ArrayList<SelectItem>();
		} finally {
			dao.close();
		}		
	}
	
	@Override
	public String getFormPage() {
		return "/portais/docente/selecao_forum.jsf";
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Character getNivel() {
		return nivel;
	}

	public void setNivel(Character nivel) {
		this.nivel = nivel;
	}

	public List<Forum> getListaForunsPorCurso() {
		return listaForunsPorCurso;
	}

	public void setListaForunsPorCurso(List<Forum> listaForunsPorCurso) {
		this.listaForunsPorCurso = listaForunsPorCurso;
	}

	public List<ForumCursoDocente> getForunsComPermissao() {
		return forunsComPermissao;
	}

	public void setForunsComPermissao(List<ForumCursoDocente> forunsComPermissao) {
		this.forunsComPermissao = forunsComPermissao;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}
}
