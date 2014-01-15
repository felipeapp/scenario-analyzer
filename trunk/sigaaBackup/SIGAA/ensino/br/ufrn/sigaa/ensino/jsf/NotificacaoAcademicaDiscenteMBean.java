package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dao.NotificacaoAcademicaDao;
import br.ufrn.sigaa.ensino.dominio.NotificacaoAcademica;
import br.ufrn.sigaa.ensino.dominio.NotificacaoAcademicaDiscente;
import br.ufrn.sigaa.ensino.dominio.RegistroNotificacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean responsável por verificar se um discente recebeu uma notificação acadêmica.
 * @author Diego Jácome
 */
@Component("notificacaoAcademicaDiscente")
@Scope("request")
public class NotificacaoAcademicaDiscenteMBean extends SigaaAbstractController<NotificacaoAcademicaDiscente> {
	
	/** Link para a página da listagem de discentes */
	private static final String LISTAR_DISCENTES_NOTIFICADOS = "/administracao/discentes/NotificacaoAcademica/listaDiscentes.jsp";
	
	/** Link para a página de informações do discente */
	private static final String INFO_DISCENTE_NOTIFICADO = "/administracao/discentes/NotificacaoAcademica/infoDiscente.jsp";
	
	/** Lista de notificações acadêmicas enviadas para os discentes */
	private List<NotificacaoAcademicaDiscente> notificacoesDiscentes;
	
	/** Lista de registros de notificações acadêmicas visualizadas pelos discentes */
	private List<RegistroNotificacaoAcademica> registroNotificacoes;
	
	/** Senha usada na autenticação do usuário. */
	private String senha;
	
	/** Notificação no qual os discentes estão sendo listados */
	private NotificacaoAcademica notificacao;
	
	/** Discente que será buscado */
	private Discente discente;
	
	/** Indica se os discentes estão sendo filtrados */
	private Boolean filtrarDiscentes = false;
	
	/** Indica o total de notificações confirmadas */
	private Integer totalConfirmadas;

	/** Indica o total de notificações visualizadas */
	private Integer totalVisualizadas;
	
	/** Número de notificações de discentes enviadas */
	public Integer numNotificacoesDiscentes;
	
	/** Construtor Padrão */
	public NotificacaoAcademicaDiscenteMBean() {}
	
	/**
	 * Entra na tela de aviso de notificações
	 * 
	 * JSP: Não invocado por JSP
	 * @return
	 * @throws DAOException 
	 * @throws ArqException
	 */
	public String listarNotificacoesPendentes() throws DAOException {
		setSubSistemaAtual(SigaaSubsistemas.PORTAL_DISCENTE);		
		return redirectJSF("/ensino/notificacoes_academicas/notificacoes_pendentes.jsp");
	}
	
	/**
	 * Carrega os dados das notificações pendentes
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ensino.notificacoes_academicas/notificacoes_pendentes.jsp</li>
	 * </ul>
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws ArqException
	 */
	public NotificacaoAcademicaDiscente getUltimaNotificacao () throws ArqException, NegocioException {	
		if ( obj == null ) {
			carregarObjeto();
			registrarVisualizacao();
		}	
		return obj;

	}
	
	/**
	 * Carrega o objeto
	 * JSP: Não invocado por JSP
	 * @return
	 * @throws DAOException 
	 * @throws ArqException
	 */
	private void carregarObjeto () throws HibernateException, DAOException {
		NotificacaoAcademicaDao dao = null;
		try {
				dao = getDAO(NotificacaoAcademicaDao.class);
				
				 NotificacaoAcademicaDiscente notificacoesUrgentes = dao.findNotificacaoDiscentePendenteByDiscente(getUsuarioLogado().getDiscenteAtivo().getDiscente(), true);		
				 NotificacaoAcademicaDiscente notificacoes = dao.findNotificacaoDiscentePendenteByDiscente(getUsuarioLogado().getDiscenteAtivo().getDiscente(), false);		
				 
				 if ( notificacoesUrgentes != null )
					 obj = notificacoesUrgentes;
				 else 
					 obj = notificacoes;
				 
				 if (obj != null)
					 registroNotificacoes = (List<RegistroNotificacaoAcademica>) dao.findByExactField(RegistroNotificacaoAcademica.class, new String[] { "notificacaoDiscente.id", "discente.id"}, new Object[] { obj.getId() , obj.getDiscente().getId() });

				 
		} finally {
			if ( dao != null )
				dao.close();
		}
	}
	
	/**
	 * Verifica se o discente possui notificações pendentes
	 * JSP: Não invocado por JSP
	 * @return
	 * @throws ArqException 
	 * @throws ArqException
	 */
	public String getVerificaNotificacoes () throws HibernateException, ArqException {
		NotificacaoAcademicaDao dao = null;
		try {
				dao = getDAO(NotificacaoAcademicaDao.class);
				
				Usuario administrador = (Usuario) getCurrentSession().getAttribute("usuarioAnterior");
				
				if (getUsuarioLogado().getDiscenteAtivo() != null && administrador == null){
					 boolean pendente = dao.isPendenteNotificacao(getUsuarioLogado().getDiscenteAtivo().getDiscente().getId(),true);
					 if ( pendente )
							return redirect("/formularioNotificacaoAcademica.jsf");
				}
				return null;
				 
		} finally {
			if ( dao != null )
				dao.close();
		}
	}
	
	private void registrarVisualizacao() throws ArqException, NegocioException {
		
		Discente d = getGenericDAO().refresh(obj.getDiscente());
		if (getUsuarioLogado().getDiscenteAtivo().getDiscente().getId() == d.getId()){
			prepareMovimento(ArqListaComando.CADASTRAR);
			RegistroNotificacaoAcademica rNot = new RegistroNotificacaoAcademica();
			rNot.setNotificacaoDiscente(obj);
			rNot.setDiscente(getUsuarioLogado().getDiscenteAtivo().getDiscente());
			rNot.setRegistroEntrada(getRegistroEntrada());
			rNot.setDataVisualizacao(new Date());
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(rNot);
			mov.setCodMovimento(ArqListaComando.CADASTRAR);
			execute(mov);
		}	
	}

	/**
	 * Confirma a leitura da confirmação
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ensino.notificacoes_academicas/notificacoes_pendentes.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws ArqException
	 */
	public String confirmar () throws ArqException, NegocioException {
	
		NotificacaoAcademicaDao dao = null;
		try {
			
			dao = getDAO(NotificacaoAcademicaDao.class);

			if (obj.getNotificacaoAcademica() == null)
				obj = dao.refresh(obj);
			
			if (obj.getNotificacaoAcademica().isExigeConfirmacao() && !confirmaSenha())
				return null;
			
			obj.setDataConfirmacao(new Date());
			obj.setPendente(false);
			obj.setRegistroConfirmacao(getUsuarioLogado().getRegistroEntrada());
			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			prepareMovimento(ArqListaComando.ALTERAR);
			mov.setCodMovimento(ArqListaComando.ALTERAR);
			execute(mov);
			
			carregarObjeto();
			
			if ( isEmpty(obj) )			
				return redirect("/sigaa/verPortalDiscente.do");
			else {
				addMensagemWarning("Existe outra notificação pendente");
				return forward("/ensino/notificacoes_academicas/notificacoes_pendentes.jsp");
			}
				
		
		} finally {
			if ( dao != null )
				dao.close();
		}
	}
	
	/**
	 * Acessa o sigaa sem ler a notificação.
	 * Método utilizado nas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ensino.notificacoes_academicas/notificacoes_pendentes.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws ArqException
	 */
	public String entrarSemLer () {
		return redirect("/sigaa/verPortalDiscente.do");
	}
	
	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getSenha() {
		return senha;
	}
	
	/**
	 * Visualiza os discentes de uma notificação.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/acompanharNotificacoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String visualizarDiscentes() throws ArqException {
		Integer id = getParameterInt("id");
		Integer idRegistroNotificacao = getParameterInt("idRegistro");
		numNotificacoesDiscentes = getParameterInt("qtdEnviadas");
		GenericDAO dao = null;
		NotificacaoAcademicaDao nDao = null;
		
		try {
			if(isEmpty(id)) {
				addMensagem(MensagensArquitetura.OBJETO_NAO_FOI_SELECIONADO);
				return null;
			}

			discente = new Discente();
			discente.setCurso(new Curso());
			
			dao = getGenericDAO();
			nDao = getDAO(NotificacaoAcademicaDao.class);
			notificacao = dao.findByPrimaryKey(id,NotificacaoAcademica.class);
			notificacao.setIdRegistroNotificacao(idRegistroNotificacao);
			totalConfirmadas = nDao.countTotalConfirmadas(id);
			totalVisualizadas = nDao.countTotalVisualizadas(id);
			return forward(LISTAR_DISCENTES_NOTIFICADOS);
		
		} finally {
			if ( dao != null )
				dao.close();
			if (nDao != null)
				nDao.close();
		}
	}

	/**
	 * Filtra os discentes de uma notificação.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/listarDiscentes.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public void filtrarDiscentes() throws DAOException {

		NotificacaoAcademicaDao dao = null;
		
		try {
			
			if (discente.getMatricula() == null 
				&& (discente.getNome() == null || discente.getNome().isEmpty())
				&& (discente.getCurso().getNome() == null || discente.getCurso().getNome().isEmpty())) {
				addMensagemErro("Selecione pelo menos um critério de busca.");
				return;
			}
			
			dao = getDAO(NotificacaoAcademicaDao.class);
			notificacoesDiscentes = dao.findNotificacoesByDiscente(notificacao.getId(),notificacao.getIdRegistroNotificacao(), 
					discente.getMatricula(), discente.getNome(), discente.getCurso().getNome());
			filtrarDiscentes = true;
			configurarVisualizacaoCursos();

		} finally {
			if ( dao != null )
				dao.close();
		}
	}
	
	/**
	 * Retorna as notificações dos discentes paginados
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/listaDiscentes.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	@Override
	public Collection<NotificacaoAcademicaDiscente> getAllPaginado() throws ArqException {
		NotificacaoAcademicaDao dao = null;
		try {
			if (!filtrarDiscentes) {
				PagingInformation paging = getMBean("paginacao");	
				paging.setTamanhoPagina(500);
				dao = getDAO(NotificacaoAcademicaDao.class);
				notificacoesDiscentes = dao.findNotificacoesDiscentes(notificacao.getId(),notificacao.getIdRegistroNotificacao(),getPaginacao());
				configurarVisualizacaoCursos();
			}
		} catch (Exception e) {
			notifyError(e);
		}
		return notificacoesDiscentes;
	}

	/**
	 * Configura as informações sobre os cursos para a página de listagem de notificação 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/listaDiscentes.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	private void configurarVisualizacaoCursos() {
		
		Integer idCurso = 0;
		Integer idMunicipio = 0;
		NotificacaoAcademicaDiscente nCursoExibido = null; 
		
		for (NotificacaoAcademicaDiscente n : notificacoesDiscentes){
			if (n.getDiscente().getCurso().getId() != idCurso || n.getDiscente().getCurso().getMunicipio().getId() != idMunicipio){
				n.setMostrarCurso(true);
				nCursoExibido = n;
				nCursoExibido.setQtdAlunos(1);
			}	
			else{
				n.setMostrarCurso(false);
				if (nCursoExibido!=null)
					nCursoExibido.setQtdAlunos(nCursoExibido.getQtdAlunos()+1);
			}	
			idCurso = n.getDiscente().getCurso().getId();
			idMunicipio = n.getDiscente().getCurso().getMunicipio().getId();
		}
		
	}

	/**
	 * Carrega os dados dos discentes selecionado:
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/listaDiscentes.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String infoDiscente () throws DAOException{
		
		Integer idNotificacao = getParameterInt("idNotificacao",0);
		
		if (idNotificacao == 0){
			addMensagemErro("Nenhum discente selecionado.");
			return null;
		}
			
		for (NotificacaoAcademicaDiscente n : notificacoesDiscentes)
			if ( n.getId() == idNotificacao )
				obj = n;
		
		 if (obj != null)
			 registroNotificacoes = (List<RegistroNotificacaoAcademica>) getGenericDAO().findByExactField(RegistroNotificacaoAcademica.class, new String[] { "notificacaoDiscente.id", "discente.id"}, new Object[] { obj.getId() , obj.getDiscente().getId() });
		
		return forward(INFO_DISCENTE_NOTIFICADO);
	}
			
	/**
	 * Volta a exibir a paginação após o usuário utilizar o filtro
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/listaDiscentes.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public void exibeTodos() {
		PagingInformation paging = getMBean("paginacao");
		filtrarDiscentes = false;	
		paging.setPaginaAtual(0);
	}
	
    /**
	 * Limpa a filtragem de discentes e chama o método de paginação para avançar a página.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/listaDiscente.jsp</li>
     * </ul>
	 * 
	 * @param e
	 */
	public void nextPage(ActionEvent e) {
		PagingInformation paging = getMBean("paginacao");
		filtrarDiscentes = false;		
		paging.nextPage(e);
	}

	/**
	 * Limpa a filtragem de discentes e chama o método de paginação para voltar a página.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/listaDiscente.jsp</li>
     * </ul>
	 * 
	 * @param e
	 */
	public void previousPage(ActionEvent e) {
		PagingInformation paging = getMBean("paginacao");
		filtrarDiscentes = false;				
		paging.previousPage(e);
	}
	
	/**
	 * Limpa a filtragem de discentes e chama o método de paginação para trocar a página.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/listaDiscente.jsp</li>
     * </ul>
	 * 
	 * @param e
	 */
	public void changePage(ValueChangeEvent e) {
		PagingInformation paging = getMBean("paginacao");
		filtrarDiscentes = false;		
		paging.changePage(e);
	}
	
	/**
	 * Seta o discente em HistoricoMBean.
	 * <br /><br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/infoDiscente.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String historico () throws ArqException{
		Integer id = getParameterInt("idDiscente");
		DiscenteGraduacao discente = getGenericDAO().findByPrimaryKey(id, DiscenteGraduacao.class);	
		HistoricoMBean historico = (HistoricoMBean) getMBean("historico");
		historico.setDiscente(discente);
		return historico.selecionaDiscente();
	}
	
	/**
	 * Volta para a lista de discente 
	 * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/infoDiscente.jsp</li>
     * </ul>
	 */
	public String voltarListaDiscentes () throws DAOException{
		return forward(LISTAR_DISCENTES_NOTIFICADOS);
	}
	
	public void setNotificacao(NotificacaoAcademica notificacao) {
		this.notificacao = notificacao;
	}

	public NotificacaoAcademica getNotificacao() {
		return notificacao;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Discente getDiscente() {
		return discente;
	}

	public boolean isFiltrarDiscentes() {
		return filtrarDiscentes;
	}

	public void setRegistroNotificacoes(List<RegistroNotificacaoAcademica> registroNotificacoes) {
		this.registroNotificacoes = registroNotificacoes;
	}

	public List<RegistroNotificacaoAcademica> getRegistroNotificacoes() {
		return registroNotificacoes;
	}

	public void setTotalConfirmadas(Integer totalConfirmadas) {
		this.totalConfirmadas = totalConfirmadas;
	}

	public Integer getTotalConfirmadas() {
		return totalConfirmadas;
	}

	public void setTotalVisualizadas(Integer totalVisualizadas) {
		this.totalVisualizadas = totalVisualizadas;
	}

	public Integer getTotalVisualizadas() {
		return totalVisualizadas;
	}
	
	
	public Integer getNumNotificacoesDiscentes() {
		return numNotificacoesDiscentes;
	}

	public void setNumNotificacoesDiscentes(Integer numNotificacoesDiscentes) {
		this.numNotificacoesDiscentes = numNotificacoesDiscentes;
	}

}
