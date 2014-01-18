/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/01/2008
 *
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.negocio.SigaaHelper;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.ForumDao;
import br.ufrn.sigaa.ava.dominio.Forum;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroAva;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;
import br.ufrn.sigaa.ensino_rede.portal.jsf.PortalCoordenadorRedeMBean;

/**
 * Managed-Bean para gerenciamento de Fóruns de Turma Virtual(AVA) e também Fóruns de Curso.
 * 
 */
@Component("forum")
@Scope("session")
public class ForumMBean extends CadastroTurmaVirtual<Forum> {

	//private boolean notificar
	/** Verifica se o fórum é de um curso. */
	private boolean eUmForumDeCurso;
	/** Lista dos fóruns de um curso. */
	private List<Forum> listaForunsPorCurso = new ArrayList<Forum>();
	
	/** Verifica se o fórum é de um curso. */
	private boolean eUmForumDePrograma;
	/** Lista dos fóruns de um curso. */
	private List<Forum> listaForunsPorPrograma = new ArrayList<Forum>();
	
	/**
	* Redireciona o usuário para a página de acordo com seu papel.
	* <br />
	* Método chamado pela(s) seguinte(s) JSP(s):
	* <ul>
	* <li>sigaa.war\graduacao\ForumMensagem\listar.jsp</li>
	* </ul>
	*
	* @throws SegurancaException
	* @throws DAOException
	*/	
	public String cancelarForumCursos() {
		
		String url = null;
		
		getPaginacao().setPaginaAtual(0);
		
		// Se não for nenhum tipo de coordenador ou secretário, então é discente.
		if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_GRADUACAO, SigaaPapeis.SECRETARIA_COORDENACAO) && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR)) 
			url = "/graduacao/coordenador.jsf";
		 else if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS) && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO)) 
			url = "/stricto/coordenacao.jsf";
		 else if (isUserInRole(SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO) && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO)) 
			 url = "/lato/coordenador.jsf";
		 else if ((getServidorUsuario() != null && getServidorUsuario().isDocente()) || getUsuarioLogado().getDocenteExternoAtivo() != null)
			 url = "/portais/docente/cursos_forum.jsf";
		 else 
			return redirect("/sigaa/verPortalDiscente.do");
		
		return forward(url);
		
	}
	
	/**
	 * Se não é um fórum de curso, lista os fóruns pela turma.<br/><br/>
	 * 
	 * Método não invocado por JSPs. Este é public por causa da arquitetura.
	 */
	@Override
	public List<Forum> lista() {
		if (!eUmForumDeCurso)
			return getDAO(ForumDao.class).findByTurma(turma());
		
		return null;
	}

	/**
	 * Caso o fórum não exista, ele é criado na primeira vez que algum usuário do curso
	 * tente postar alguma mensagem (seja discente ou o coordenador do curso).
	 * 
	 * @param idCurso
	 * @throws DAOException
	 */
	private void criarForumCurso(int idCurso) throws DAOException {
		
 		object = new Forum();
		object.setCurso(true);
		
		List<Forum> nomeForumCurso = getDAO(ForumDao.class).findNomeCursoByID( idCurso );
		
		Usuario u = (Usuario) getCurrentSession().getAttribute("usuario");
		object.setUsuario(u);
		object.setIdCursoCoordenador(idCurso);
		object.setDescricao("FÓRUM DO CURSO DE " + nomeForumCurso.get(0).getDescricao());
		object.setTitulo("FÓRUM DO CURSO DE " + nomeForumCurso.get(0).getTitulo());
		object.setAtivo(true);
		object.setTipo(Short.parseShort("1"));
		object.setTopicos(false);
		
		
		if (getUsuarioLogado().getDiscente() != null) {
			object.setNivel(getUsuarioLogado().getDiscente().getNivel());
		}
		
		List<Curso> cursosSelecionados = new ArrayList<Curso>();
		
		if (isPortalCoordenadorLato()) { // verificar se o usuário é coordenador de Lato
			Curso c = (Curso) getCurrentSession().getAttribute("cursoAtual");
			cursosSelecionados.add(c);
		} 
		else if (getUsuarioLogado().getDiscenteAtivo() != null) { // verificar se é Discente
			Curso c = getUsuarioLogado().getDiscenteAtivo().getCurso();
			cursosSelecionados.add(c);
		}

		if (isPortalCoordenadorStricto()) { // verificar se o usuário é coordenador de Stricto
			Unidade programa = (Unidade) getCurrentSession().getAttribute("programaAtual");
			cursosSelecionados = CollectionUtils.toList( getDAO(CursoDao.class).findByPrograma(programa.getId()) );
		}
		
		if (isPortalCoordenadorGraduacao()) { // verificar se o usuário é coordenador de Graduação
			Curso c = (Curso) getCurrentSession().getAttribute("cursoAtual");
			cursosSelecionados.add(c);
		}
		
		if (getCurrentSession().getAttribute("cursoAtual") != null && !cursosSelecionados.contains(getCurrentSession().getAttribute("cursoAtual"))) {
			cursosSelecionados.add((Curso) getCurrentSession().getAttribute("cursoAtual"));
		}
		
		for (Curso curso : cursosSelecionados) {
			prepare(SigaaListaComando.CADASTRAR_AVA);
			
			object.setNivel(curso.getNivel());
			object.setIdCursoCoordenador(curso.getId());
			cadastrarForum(SigaaListaComando.CADASTRAR_AVA, object, getEspecificacaoCadastro());
		}
	}

	/**
	 * Método executado antes de persistir o objeto Fórum quando o fórum é de Turma (usado em Turma Virtual).<br/><br/>
	 * 
	 * Método não invocado por JSPs. Public por causa da arquitetura.
	 */
	@Override
	public void antesPersistir() {
		object.setTipo(Forum.TURMA);
		object.setTopicos(false);
	}
	
	/**
	 * Responsável por criar o movimento e cadastrar o objeto Fórum.
	 * Método não invocado por JSPs.
	 * @param comando
	 * @param object
	 * @param specification
	 * @return
	 */
	private Notification cadastrarForum(Comando comando, PersistDB object, Specification specification) {
		try {

			MovimentoCadastroAva mov = new MovimentoCadastroAva();
			mov.setCodMovimento(comando);
			mov.setObjMovimentado(object);
			mov.setSpecification(specification);

			return (Notification) executeWithoutClosingSession(mov, getCurrentRequest());
						
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		}
	}
	
	/**
	 * Lista os fóruns de curso de acordo com o usuário (discente , coordenador ou secretário).<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>sigaa.war/graduacao/coordenacor.jsp</li>
	 * 		<li>sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * 		<li>sigaa.war/lato/coordenador.jsp</li>
	 * 		<li>sigaa.war/portais/discente/discente.jsp</li>
	 * 		<li>sigaa.war/portais/discente/menu_discente.jsp</li>
	 * 		<li>sigaa.war/stricto/coordenacao</li>
	 * 		<li>sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String listarForunsCurso() throws DAOException {

		eUmForumDeCurso = true;

		Curso curso = null;
		
		Integer idCurso = getParameterInt("id_curso");
		
		if (idCurso == null) {
		
			if (isPortalCoordenadorLato()) { // verificar se o usuário é coordenador de Lato
				curso = (Curso) getCurrentSession().getAttribute("cursoAtual");
			} 
			if (isPortalCoordenadorStricto()) { // verificar se o usuário é coordenador de Stricto
				Unidade programa = (Unidade) getCurrentSession().getAttribute("programaAtual");
				Collection<Curso> cursos = getDAO(CursoDao.class).findByPrograma(programa.getId());
				curso = cursos.iterator().next();
			}
			if (isPortalCoordenadorGraduacao()) { // verificar se o usuário é coordenador de Graduação
				curso = SigaaHelper.getCursoAtualCoordenacao();
			}
			
		} else {
			curso = getGenericDAO().findByPrimaryKey(idCurso.intValue(), Curso.class);
			getCurrentSession().setAttribute("cursoAtual", curso);
		}
		
		if (curso == null) { // caso não seja coordenador, verificar o curso do discente logado
			
			boolean alunoRegular = getUsuarioLogado().getDiscenteAtivo().isRegular();
			if (!alunoRegular) { 
				addMensagemWarning("Desculpe. Mas alunos especiais não podem acessar o Fórum de cursos.");
				return null;
			}
			
			curso = getUsuarioLogado().getDiscenteAtivo().getCurso();
			
			if ( curso == null ) {
				addMensagemWarning("Desculpe. Você não possui um curso associado para acessar o Fórum de cursos.");
				return null;
			} else {
				listaForunsPorCurso = getDAO(ForumDao.class).findForunsDeCursoByIDCurso( curso.getId() );	
			}
		}

		listaForunsPorCurso = getDAO(ForumDao.class).findForunsDeCursoByIDCurso( curso.getId() );
		
		if (listaForunsPorCurso.size() == 0) {
			
			// caso não exista nenhum curso existente para o curso, cria pela primeira vez
			criarForumCurso( curso.getId() );
			
			ForumMensagemMBean fm = (ForumMensagemMBean)getMBean("forumMensagem");
			return fm.listarForunsPorCurso(); // redireciona para a página de tópicos do curso em questão. página em branco pois o fórum é criado vazio.
			
		} else {
			ForumMensagemMBean fm = (ForumMensagemMBean)getMBean("forumMensagem");
			return fm.listarForunsPorCurso(); // redireciona para a página de tópicos do curso em questão exibindo os tópicos cadastrados 
		}
	}

	/**
	 * Padrão Specification. Serve para validar o estado de um objeto. Nesse caso valida o Fórum que está sendo criado. 
	 * Método não invocado por JSPs.
	 */
	@Override
	public Specification getEspecificacaoCadastro() {
		return new Specification() {
			Notification notification = new Notification();
			
			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				Forum ref = (Forum) objeto;
				if (isEmpty(ref.getTitulo()))
					notification.addError("É obrigatório informar um título!");
				
				if ( isEmpty(ref.getDescricao()) )
					notification.addError("É obrigatório informar descrição!");
								
				return !notification.hasMessages();
			}
		};
	}
	
	/**
	 * Cadastra uma mensagem no fórum.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/ava/Forum/novo.jsp</li>
	 * 		<li>sigaa.war/cv/ForumComunidade/novo.jsp</li>
	 * 		<li>sigaa.war/graduacao/Forum/index.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws ArqException {
		
		prepareMovimento(SigaaListaComando.CADASTRAR_AVA);
		
		object.setTurma(turma());
		antesPersistir();
		Notification notification = execute(SigaaListaComando.CADASTRAR_AVA, object, getEspecificacaoCadastro());
		
		if (notification.hasMessages())
			return notifyView(notification);
		
		listagem = null;
		flash("Cadastrado com sucesso.");
		
		notificarTurma("Novo Fórum na Turma", "<p>O fórum " + object.getTitulo() + 
				" foi cadastrado na turma pelo usuário " + getUsuarioLogado().getPessoa().getNome() 
				+ "</p><p>Para postar mensagens entre na turma virtual do SIGAA</p>", ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE);
		
		aposPersistir();
		return redirect("/ava/" + getClasse().getSimpleName() + "/listar.jsf");
		
	}

	/**
	 * Método que instância o objeto Fórum do MBean.<br/><br/>
	 * 
	 * Método chamado no método "novo()" no bean CadastroTurmaVirtual.<br/>
	 * Não invocado por JSPs.
	 */
	@Override
	public void instanciar() {
		object = new Forum();
	}
	
	/**
	 * Método invocado antes da remoção do objeto Fórum.
	 * 
	 * Não invocado por JSPs
	 * 
	 */
	@Override
	protected void antesRemocao() throws DAOException {
		getGenericDAO().detach(object);
	}
	
	// MÉTODOS PARA O FÓRUM DO PROGRAMA
	
	/**
	 * Lista os fóruns de um programa.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>sigaa.war/ensino_rede/portal/portal.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String listarForunsPrograma() throws DAOException {
		seteUmForumDePrograma(true);

		ProgramaRede programa = null;
		
		Integer idPrograma = getParameterInt("id_programa");
		
		if (idPrograma == null) {
		
			PortalCoordenadorRedeMBean mBean = getMBean("portalCoordenadorRedeBean");
			programa = mBean.getProgramaRede();
			
		} 
		
		if (programa == null) { 
			addMensagemWarning("Desculpe. Mas o senhor(a) não pode acessar o Fórum do Programa.");
			return null;
		}

		listaForunsPorPrograma = getDAO(ForumDao.class).findForunsDeProgramaByIDPrograma( programa.getId() );
		ForumMensagemMBean fm = (ForumMensagemMBean)getMBean("forumMensagem");
		fm.setFiltro(null);
		if (listaForunsPorPrograma.size() == 0) {
			criarForumPrograma(programa);
		}
		return fm.listarForunsPorPrograma(); 
	}
	
	/**
	 * Caso o fórum não exista, ele é criado na primeira vez que algum usuário do programa
	 * tente postar alguma mensagem.
	 * 
	 * @param idCurso
	 * @throws DAOException
	 */
	private void criarForumPrograma( ProgramaRede programa) throws DAOException {
		
 		object = new Forum();
		object.setPrograma(true);
		
		Usuario u = (Usuario) getCurrentSession().getAttribute("usuario");
		object.setUsuario(u);
		object.setProgramaRede(programa);
		object.setDescricao("FÓRUM DO PROGRAMA " + programa.getDescricao());
		object.setTitulo("FÓRUM DO PROGRAMA " + programa.getDescricao());
		object.setAtivo(true);
		object.setTipo(Short.parseShort("1"));
		object.setTopicos(false);
		object.setNivel(NivelEnsino.STRICTO);
		
		prepare(SigaaListaComando.CADASTRAR_AVA);
		cadastrarForum(SigaaListaComando.CADASTRAR_AVA, object, getEspecificacaoCadastro());

	}
	
	/**
	* Redireciona o usuário para a página de acordo com seu papel.
	* <br />
	* Método chamado pela(s) seguinte(s) JSP(s):
	* <ul>
	 * 		<li>/sigaa.war/ensino_rede/forum_mensagem_rede/listar.jsp</li>
	* </ul>
	*
	* @throws SegurancaException
	* @throws DAOException
	*/	
	public String cancelarForumPrograma() {
		
		String url = null;
		
		getPaginacao().setPaginaAtual(0);
		
		// Se não for nenhum tipo de coordenador ou secretário, então é discente.
		if (isUserInRole(SigaaPapeis.COORDENADOR_GERAL_REDE)) 
			url = "/ensino_rede/modulo/menu.jsp";
		else 
			url = "/ensino_rede/portal/portal.jsp";
		
		return forward(url);
		
	}
	
	/** Indica que se o usuário é um discente ativo 
	 * @return
	 */
	public boolean isUsuarioAtivo(){
	if(getUsuarioLogado().getDiscenteAtivo() != null)
		return getUsuarioLogado().getDiscenteAtivo().isAtivo();
	else
		return true;
	}
	
	public List<Forum> getListaForunsPorCurso() {
		return listaForunsPorCurso;
	}

	public void setListaForunsPorCurso(List<Forum> listaForunsPorCurso) {
		this.listaForunsPorCurso = listaForunsPorCurso;
	}

	public void setListaForunsPorPrograma(List<Forum> listaForunsPorPrograma) {
		this.listaForunsPorPrograma = listaForunsPorPrograma;
	}

	public List<Forum> getListaForunsPorPrograma() {
		return listaForunsPorPrograma;
	}

	/** Seta se o fórum é de um curso. 
	 * @param eUmForumDePrograma
	 */
	public void seteUmForumDePrograma(boolean eUmForumDePrograma) {
		this.eUmForumDePrograma = eUmForumDePrograma;
	}

	/** Indica se o fórum é de um curso.
	 * @return
	 */
	public boolean iseUmForumDePrograma() {
		return eUmForumDePrograma;
	}
}
