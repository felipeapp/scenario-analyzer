/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/01/2008
 *
 */
package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.Forum;
import br.ufrn.sigaa.ava.jsf.CadastroTurmaVirtual;
import br.ufrn.sigaa.ava.jsf.ControllerTurmaVirtual;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroAva;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.medio.dao.ForumMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.ForumMedio;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;

/**
 * Managed-Bean para gerenciamento de Fóruns de Turma/Serie.
 * 
 */
@Component("forumMedio")
@Scope("session")
public class ForumMedioMBean extends CadastroTurmaVirtual<ForumMedio> {

	/** Lista dos fóruns de um curso. */
	private List<Forum> listaForunsPorCurso = new ArrayList<Forum>();
	/** Lista dos fóruns de uma turmaSerie. */
	private List<ForumMedio> listaForunsPorTurma = new ArrayList<ForumMedio>();
	/** Lista de turmaSerie. */
	private List<TurmaSerie> listaTurmaSerie = new ArrayList<TurmaSerie>();
	
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
			 return redirect("/sigaa/verPortalDocente.do");
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
	public List<ForumMedio> lista() {
		return null;
	}

	/**
	 * Caso o fórum não exista, ele é criado na primeira vez que algum usuário do curso
	 * tente postar alguma mensagem (seja discente ou o coordenador do curso).
	 * 
	 * @param idCurso
	 * @throws DAOException
	 */
	public Object criarForumTurmaSerie(TurmaSerie turmaSerie) throws DAOException {
		
		object = new ForumMedio();	
		
		Usuario u = (Usuario) getCurrentSession().getAttribute("usuario");
		object.setUsuario(u);
		object.setTurmaSerie(turmaSerie);
		object.setDescricao("FÓRUM DE TURMA DA " + turmaSerie.getDescricaoSerieTurma());
		object.setTitulo("FÓRUM DE TURMA DA " + turmaSerie.getDescricaoSerieTurma());
		object.setAtivo(true);
		object.setTipo(Short.parseShort("1"));
		object.setTopicos(false);
		
		prepare(SigaaListaComando.CADASTRAR_AVA);
			
		object.setNivel(turmaSerie.getSerie().getCursoMedio().getNivel());
		cadastrarForumTurmas(SigaaListaComando.CADASTRAR_AVA, object, getEspecificacaoCadastro());
		
		return object;
		
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
	private Notification cadastrarForumTurmas(Comando comando, PersistDB object, Specification specification) {
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
	 * 		<li></li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String listarForunsTurmaSerie() throws DAOException {

		Collection<TurmaSerie> turmasSerie = new ArrayList<TurmaSerie>();
		
		if (getUsuarioLogado().getDiscenteAtivo() != null) {
			boolean alunoRegular = getUsuarioLogado().getDiscenteAtivo().isRegular();
			if (!alunoRegular) { 
				addMensagemWarning("Desculpe. Mas alunos especiais não podem acessar o Fórum de cursos.");
				return null;
			}
			turmasSerie = getDAO(TurmaSerieDao.class).findByDiscenteSerieAno( (DiscenteMedio) getUsuarioLogado().getDiscenteAtivo(), null, getCalendarioVigente().getAno());
			listaForunsPorTurma = getDAO(ForumMedioDao.class).findForumTurmaSerieByDiscente(getUsuarioLogado().getDiscenteAtivo(), getCalendarioVigente().getAno());
		}
		
		if ( turmasSerie.isEmpty() && listaForunsPorTurma.isEmpty() ){
			addMensagemWarning("Desculpe. Apenas alunos matrículados em turmas de nível médio podem cadastrar tópicos em fórum de turma.");
			return null;
		}
		
		if (listaForunsPorTurma.size() == 0) {
			
			// caso não exista nenhum fórum para a turmaSerie, o fórum será criado.
			for (TurmaSerie ts : turmasSerie) {
				criarForumTurmaSerie( ts );
			}
			
			ForumMensagemMedioMBean fm = (ForumMensagemMedioMBean)getMBean("forumMensagemMedio");
			return fm.listarForunsPorTurmaSerie(); // redireciona para a página de tópicos do curso em questão. página em branco pois o fórum é criado vazio.
			
		} else {
			ForumMensagemMedioMBean fm = (ForumMensagemMedioMBean)getMBean("forumMensagemMedio");
			return fm.listarForunsPorTurmaSerie(); // redireciona para a página de tópicos do curso em questão exibindo os tópicos cadastrados 
		}
	}

	/**
	 * Lista todas as TurmaSeries que o docente seja vinculado, para cadastrar um novo tópico. 
	 * <br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/portais/docente/docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String listarTurmaSeriesDocente() throws DAOException {
		getForunsTurmaSerie();
		return forward("/medio/" + getClasse().getSimpleName() + "/listar_forum_turma.jsp");
	}
	
	/**
	 * Carrega os fóruns das turmas do docente.
	 * @throws DAOException
	 */
	private void getForunsTurmaSerie() throws DAOException {
		if ( getUsuarioLogado().getServidorAtivo() != null || getUsuarioLogado().getDocenteExternoAtivo() != null ){
			buscarForumTurmaPorDocente();
			return;
		}
	}
	
	/**
	 * Busca as turmas que um docente seja vinvulado.<br/><br/>
	 * Não invocado por JSP
	 * 
	 * @param curso
	 * @throws DAOException
	 */
	private void buscarForumTurmaPorDocente() throws DAOException {
		TurmaSerieDao tsDao = getDAO(TurmaSerieDao.class);
		if( getUsuarioLogado().getServidorAtivo() != null ){
			listaTurmaSerie = tsDao.findByDocenteAno(getUsuarioLogado().getServidorAtivo().getId() , null, CalendarUtils.getAnoAtual());
		} else if ( getUsuarioLogado().getDocenteExternoAtivo() != null ){
			listaTurmaSerie = tsDao.findByDocenteAno(null , getUsuarioLogado().getDocenteExternoAtivo().getId(), CalendarUtils.getAnoAtual());
		} 
	}	
	
	/**
	 * Método responsável por setar o fórum da turma selecionado pelo docente, 
	 * quando não existir o fórum cadastrado, o mesmo realizará este procedimento.
	 * @return
	 * @throws DAOException 
	 */
	public String selecionaForumTurmaMedio() throws DAOException{
		ForumMedioDao fmDao = getDAO(ForumMedioDao.class);
		int id = getParameterInt("id_turma_serie", 0);
		ForumMedio forumTurma = fmDao.findForumTurmaSerieByID(id);
		getCurrentSession().setAttribute("turmaSerieAtual", new TurmaSerie(id));
		
		if (isEmpty(forumTurma)) {
			// caso não exista nenhum fórum para a turmaSerie, o fórum será criado.
			criarForumTurmaSerie( fmDao.findByPrimaryKey(id, TurmaSerie.class) );
					
			ForumMensagemMedioMBean fm = (ForumMensagemMedioMBean)getMBean("forumMensagemMedio");
			fm.setForumSelecionado(object.getId());
			return fm.listarForunsPorTurmaSerie(); // redireciona para a página de tópicos do curso em questão. página em branco pois o fórum é criado vazio.
			
		} else {
			ForumMensagemMedioMBean fm = (ForumMensagemMedioMBean)getMBean("forumMensagemMedio");
			fm.setForumSelecionado(forumTurma.getId());
			return fm.listarForunsPorTurmaSerie(); // redireciona para a página de tópicos do curso em questão exibindo os tópicos cadastrados 
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
				ForumMedio ref = (ForumMedio) objeto;
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
		object = new ForumMedio();
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
	
	/**
	 * Método responsável por retornar a situação do discente conforme a sua ativação.
	 * @return
	 */
	public boolean isUsuarioAtivo(){
	if(getUsuarioLogado().getDiscenteAtivo() != null)
		return getUsuarioLogado().getDiscenteAtivo().isAtivo();
	else
		return true;
	}
	
	/**
	 * Método responsável por verificar se o docente possui vinculo com algum fórum ativo do nível médio.
	 * @return
	 * @throws DAOException 
	 */
	public boolean isExibeForumMedioDocente() throws DAOException{
		ForumMedioDao fmDao = getDAO(ForumMedioDao.class);
		
		if( getUsuarioLogado().getServidorAtivo() != null ){
			return fmDao.existeTurmaMedioByDocenteAno(getUsuarioLogado().getServidorAtivo().getId() , null, getCalendarioVigente().getAno());
		} else if ( getUsuarioLogado().getDocenteExternoAtivo() != null ){
			return fmDao.existeTurmaMedioByDocenteAno(null , getUsuarioLogado().getDocenteExternoAtivo().getId(), getCalendarioVigente().getAno());
		} else {
			return false;
		}
	}
	
	
	public List<Forum> getListaForunsPorCurso() {
		return listaForunsPorCurso;
	}

	public void setListaForunsPorCurso(List<Forum> listaForunsPorCurso) {
		this.listaForunsPorCurso = listaForunsPorCurso;
	}
	
	public List<TurmaSerie> getListaTurmaSerie() {
		return listaTurmaSerie;
	}

	public void setListaTurmaSerie(List<TurmaSerie> listaTurmaSerie) {
		this.listaTurmaSerie = listaTurmaSerie;
	}

	/**
	 * Retorna o calendário vigente para as operações
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
			throw new RuntimeNegocioException("É necessário que o calendário acadêmico esteja definido para realizar esta operação.");
		}
		return cal;
	}
}

