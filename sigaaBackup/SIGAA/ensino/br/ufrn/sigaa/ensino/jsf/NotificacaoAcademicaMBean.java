package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
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
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dao.NotificacaoAcademicaDao;
import br.ufrn.sigaa.ensino.dominio.NotificacaoAcademica;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * MBean responsável de lançar a notificação de desligamento do curso para os discentes.
 * @author Diego Jácome
 */
@Component("notificacaoAcademica")
@Scope("request")
public class NotificacaoAcademicaMBean extends SigaaAbstractController<NotificacaoAcademica> {

	/** Link para a página do formulário */
	private static final String VIEW_FORM = "/administracao/discentes/NotificacaoAcademica/formNotificacaoAcademica.jsp";
	/** Link para a página de listagem */
	private static final String LIST_PAGE = "/administracao/discentes/NotificacaoAcademica/listar.jsp";
	/** Link para a página de exibição */
	private static final String VIEW = "/administracao/discentes/NotificacaoAcademica/mostrar.jsp";
	/** Link para a página de acompanhamentos das notificações */
	private static final String ACOMPANHAR_NOTIFICACOES = "/administracao/discentes/NotificacaoAcademica/acompanharNotificacoes.jsp";
	/** Link para a página confirmação das notificações */
	private static final String ENVIAR_NOTIFICACOES = "/administracao/discentes/NotificacaoAcademica/enviarNotificacoes.jsp";
	/** Link para a página dos destinatários da notificação */
	private static final String LISTAR_DESTINATARIOS = "/administracao/discentes/NotificacaoAcademica/listaDestinatarios.jsp";
	/** Link para a página de busca de discentes */
	private static final String BUSCAR_DISCENTE = "/administracao/discentes/NotificacaoAcademica/buscarDiscente.jsp";
	/** Link para a página de notificação individual */
	private static final String NOTIFICAR_INDIVIDUAL = "/administracao/discentes/NotificacaoAcademica/notificarIndividual.jsp";
	
	/** Notificações que estão selecionadas para serem enviadas. */
	private List<NotificacaoAcademica> notificacoesSelecionadas;
	
	/** Notificações que já foram enviadas. */
	private List<NotificacaoAcademica> notificacoesEnviadas;
	
	/** Se deve a tela de destinatários deve voltar para a listagem. */
	private boolean inicio = true;
	
	/** Discente que será utilizado na notificação individual. */
	private Discente discente;
	
	/** Indica se o usuário está tentando fazer uma notificação individual. */
	private Boolean individual;
	
	/** Indica se existe alguma notificação que possui ano período referência. */
	private Boolean possuiAnoPeriodo;
	
	/** Indica se existe alguma notificação selecionada que possui ano período referência. */
	private Boolean possuiAnoPeriodoSelecionada;
	
	/** Filtro das notificações acadêmicas enviadas */
	private Integer ano;
	
	/** Filtro das notificações acadêmicas enviadas */
	private Integer periodo;
	
	/** Construtor Padrão */
	public NotificacaoAcademicaMBean() {
		obj = new NotificacaoAcademica();
	}
	
	/**
	 * Inicia o caso de uso da notificação.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException{
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA,SigaaPapeis.ADMINISTRADOR_DAE);
		individual = false;
		return forward(getListPage());
	}
	
	/**
	 * Cadastra ou altera uma nova notificação.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/formNotificacaoAcademica.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	@Override
	public String cadastrar() throws ArqException, NegocioException{
		beforeCadastrarAndValidate();
		doValidate();
			
		if (!hasErrors()){
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			prepareMovimento(SigaaListaComando.CADASTRAR_NOTIFICACAO_ACADEMICA);
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_NOTIFICACAO_ACADEMICA);
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
			all = null;
			notificacoesSelecionadas = null;
			return forward(getListPage());
		} 

		return null;
	}
	
	
	/**
	 * Verifica o papel do usuário.
	 * Método não invocado por JSP(s) 
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA,SigaaPapeis.ADMINISTRADOR_DAE);
	}
	
	/**
	 * Validação do objeto. <br/>
	 * Método não invocado por JSP´s.
	 * 
	 * @return
	 */
	@Override
	protected void doValidate() throws ArqException {
		
		if (!StringUtils.notEmpty(obj.getDescricao()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descrição");
		if (!StringUtils.notEmpty(obj.getMensagemEmail()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Mensagem de E-Mail");
		if (!StringUtils.notEmpty(obj.getMensagemNotificacao()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Mensagem de Notificação");
		if (!StringUtils.notEmpty(obj.getSqlFiltrosDiscentes()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Filtro de Discentes");
		
		NotificacaoAcademicaDao dao = null; 
		try{	
			dao = getDAO(NotificacaoAcademicaDao.class);
			String erro = dao.isConsultaSQLValido(obj.getSqlFiltrosDiscentes(),obj.isAnoPeriodoReferencia(),null,null);
			if ( erro != null) {
				addMensagem(erro, "Filtro de Discentes");
				addMensagemWarning("Para o SQL digitado seja validado é necessário que a busca seja " +
						" feita apenas pelo id do discente.<br/>"+
						"Ex: SELECT d.id_discente FROM discente d ...");
			}	
		}finally {
			if ( dao != null )
				dao.close();
		}
		super.doValidate();
	}
		
	/**
	 * Método que retorna todos as notificações.<br/><br/>
	 * Método não invocado por JSP´s.
	 *
	 */
	@Override
	public Collection <NotificacaoAcademica> getAll() throws DAOException{
		
		if (all == null)
			all = getGenericDAO().findAllAtivos(NotificacaoAcademica.class,"descricao");
		
		return all;
	}
	
	/**
	 * Método que atualiza o objeto.<br/><br/>
	 * Método não invocado por JSP´s.
	 *
	 */
	@Override
	public String atualizar() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, NotificacaoAcademica.class);
		return super.atualizar();
	}
	
	/**
	 * Método que remove o objeto, verificando se o mesmo existe.<br/><br/>
	 * Método não invocado por JSP´s.
	 * @throws NegocioException 
	 *
	 */
	public String desativar() throws ArqException, NegocioException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, NotificacaoAcademica.class);
		if (obj == null || !obj.isAtivo()) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		obj.setAtivo(false);
		
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		prepareMovimento(ArqListaComando.ALTERAR);
		return super.cadastrar();
	}
	
	/**
	 * Ação realizada antes de entrar no cadastrar
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		prepareMovimento(SigaaListaComando.CADASTRAR_NOTIFICACAO_ACADEMICA);
		obj = new NotificacaoAcademica();
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}
		
	/**
	 * Exibe os discente de uma notificacao.<br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/listar.jsp</li>
	 * </ul>
	 */
	public void  marcarNotificacao (ValueChangeEvent e) throws DAOException {
		Integer idNotificacao = (Integer) e.getComponent().getAttributes().get("idNotificacao");

		if (notificacoesSelecionadas == null)
			notificacoesSelecionadas = new ArrayList<NotificacaoAcademica>();
		
		Boolean marcando = (Boolean) e.getNewValue();
		
		NotificacaoAcademica notificacao = null;
		List <NotificacaoAcademica> ns = (List<NotificacaoAcademica>) getAll();			
		for (NotificacaoAcademica n : ns)
			if (n.getId() == idNotificacao){
				notificacao = n;
				break;
			}
		
		if (notificacao != null){
			if ( marcando && !notificacoesSelecionadas.contains(notificacao)){
				notificacoesSelecionadas.add(notificacao);
			} else {
				notificacao.setExibirDiscentes(false);
				notificacoesSelecionadas.remove(notificacao);
			}
		}
	}
		
	/**
	 * Notifica os discentes.<br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/enviarNotificacoes.jsp</li>
	 * </ul>
	 */
	public String notificar () throws ArqException, NegocioException{
		checkChangeRole();
		
		if ( isOperacaoAtiva(SigaaListaComando.ENVIAR_NOTIFICACAO_ACADEMICA.getId()) ){
			if ( isEmpty(notificacoesSelecionadas) ){
				addMensagemErro("Nenhuma notificação selecionada.");
				return iniciar();
			}
			
			for ( NotificacaoAcademica n : notificacoesSelecionadas ){
				if ( n.getDiscentes() == null || n.getDiscentes().isEmpty() )
					addMensagemErro("A notificação " + obj.getDescricao() + " não possui nenhum discente. Escolha um ano ou período de referência diferente.");		
			}
			
			if (hasErrors())
				return null;
			
			if (!hasErrors()){
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setColObjMovimentado(notificacoesSelecionadas);
				prepareMovimento(SigaaListaComando.ENVIAR_NOTIFICACAO_ACADEMICA);
				mov.setCodMovimento(SigaaListaComando.ENVIAR_NOTIFICACAO_ACADEMICA);
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
				
			}
			
			setOperacaoAtiva(null);
			return iniciar();
			
		} else {
			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
			" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			redirectJSF(getSubSistema().getLink());
		}
		
		return null;
	}
	
	/**
	 * Mostra informações sobre a notificação acadêmica.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/listar.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String mostrar() throws ArqException {
		
		Integer id = getParameterInt("id");
		
		if(isEmpty(id)) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return cancelar();
		}
		
		obj = getGenericDAO().findByPrimaryKey(id, NotificacaoAcademica.class);
		
        return forward(VIEW);

    }
	/**
	 * método chamado antes do cadastrar, reimplementado pelo filho se ele
	 * desejar fazer algo antes de cadatrar
	 */
	@Override
	public void beforeCadastrarAndValidate(){
		String sql = obj.getSqlFiltrosDiscentes();
		obj.setSqlFiltrosDiscentes(sql.replaceAll(";",""));
	}
	
	/**
	 * Listas as notificações selecionadas carregando os discentes.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/listar.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String confirmar() throws DAOException {		

		NotificacaoAcademicaDao dao = null;
		
		try {
			if ( notificacoesSelecionadas == null || notificacoesSelecionadas.isEmpty() ){
				addMensagemErro("Nenhuma notificação selecionada.");
				return null;	
			}
			
			dao = getDAO(NotificacaoAcademicaDao.class);
			HashMap<Integer,List<Discente>> todosDiscentes = dao.findDiscentesByNotificacoes(notificacoesSelecionadas);


			for ( NotificacaoAcademica n : notificacoesSelecionadas ){
				List<Discente> discentes = todosDiscentes.get(n.getId());
				
				if ( discentes == null || discentes.isEmpty() ){
					addMensagemErro("A notificação " + n.getDescricao() + " não possui nenhum discente. Escolha um ano ou período de referência diferente.");
				} else {				
					n.setNumeroDiscentes(discentes.size());
					n.setDiscentes(discentes);
				}
			}
			
			if (hasErrors())
				return null;
			
			setOperacaoAtiva(SigaaListaComando.ENVIAR_NOTIFICACAO_ACADEMICA.getId());
			return forward(ENVIAR_NOTIFICACOES);
			
		} finally {
			if ( dao != null )
				dao.close();
		}
		
	}
	
	/**
	 * Redireciona o usuário para a página de listagem reiniciando o objeto.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/formNotificacaoAcademica.jsp</li>
	 * </ul>
	 * @throws SegurancaException 
	 */
	public String voltar() throws SegurancaException {
		obj = new NotificacaoAcademica();
		return iniciar();
	}
	
	/**
	 * Redireciona o usuário para a página de listagem.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/listaDestinatarios.jsp</li>
	 * </ul>
	 * @throws SegurancaException 
	 */
	public String voltarDestinatarios() throws SegurancaException {
			
		if ( !inicio )
			return forward(ENVIAR_NOTIFICACOES);
		else 
			return iniciar();
			
	}
	
	/**
	 * Redireciona o usuário para a página de listagem resetando o bean o objeto.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/formNotificacaoAcademica.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		resetBean();
		try {
			return iniciar();
		} catch (SegurancaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Listagem da notificação
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/formNotificacaoAcademica.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String redirectBuscarDiscente() {		
		return forward(BUSCAR_DISCENTE);
	}
	
	/**
	 * Listagem da notificação
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String acompanhar() throws HibernateException, DAOException {		
		
		NotificacaoAcademicaDao dao = null;
		
		try {
			
			int mes = br.ufrn.arq.util.CalendarUtils.getMesAtual();
			ano = br.ufrn.arq.util.CalendarUtils.getAnoAtual();
			periodo = (( mes < 6 ) ? 1 :  2);
			
			if (notificacoesEnviadas == null){
				dao = getDAO(NotificacaoAcademicaDao.class);
				notificacoesEnviadas = dao.findNotificacaoEnviadas(ano,periodo);
			}
			return forward(ACOMPANHAR_NOTIFICACOES);
		} finally {
			if ( dao !=  null )
				dao.close();
		}		
	}

	/**
	 * Filtra as notificações enviadas dependendo do ano e do período
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/acompanharNotificacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String filtrarEnviadas () throws HibernateException, DAOException {
		
		NotificacaoAcademicaDao dao = null;
		
		try {
			
			if (isEmpty(ano) || isEmpty(periodo)){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano-Período");
				return null;
			}

			dao = getDAO(NotificacaoAcademicaDao.class);
			notificacoesEnviadas = dao.findNotificacaoEnviadas(ano,periodo);

			return forward(ACOMPANHAR_NOTIFICACOES);
		} finally {
			if ( dao !=  null )
				dao.close();
		}		
	}
	
	/**
	 * Retorna a tela de acompanhamento das notificações.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/listaDiscentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarAcompanhar () throws HibernateException, DAOException {
			return forward(ACOMPANHAR_NOTIFICACOES);
	}
	
	/**
	 * Listagem de notificação enviadas
	 * Método não invocado por JSP´s.
	 * 
	 * @return
	 */
	public String getListPage() {		
		return LIST_PAGE;
	}
	
	@Override
	public String getFormPage() {
		return VIEW_FORM;
	}
	
	/**
	 * Redefinição da página após o remover
	 * 
	 * @return
	 */
	protected String forwardRemover() {
		return getListPage();
	}

	/**
	 * Redefinição da página após o cadastrar
	 * 
	 * @return
	 */
	public String forwardCadastrar() {
		return getListPage();
	}
	
	public void setNotificacoesSelecionadas(List<NotificacaoAcademica> notificacoesSelecionadas) {
		this.notificacoesSelecionadas = notificacoesSelecionadas;
	}

	public List<NotificacaoAcademica> getNotificacoesSelecionadas() {
		return notificacoesSelecionadas;
	}

	
	/**
	 * Carrega os destinatários da notificação.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/listar.jsp</li>
	 * </ul>
	 * @return
	 */
	public String carregarDestinatarios() throws HibernateException, DAOException {
		
		int idNotificacao = getParameterInt("id", 0);
		NotificacaoAcademicaDao dao = null;
		
		try {
		
			// Percorre a lista de notificações pois o ano e o periodo de referência são transientes
			for (NotificacaoAcademica n : all)
				if (n.getId() == idNotificacao){
					obj = n;
					break;
				}
			
			dao = getDAO(NotificacaoAcademicaDao.class);
			obj.setDiscentes(dao.findDiscentesByNotificacao(obj));
			inicio = true;
			
		} finally {
			if ( dao != null )
				dao.close();
		}
		
		return forward(LISTAR_DESTINATARIOS);
	}
	
	/**
	 * Retorna os destinatários da notificação.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/enviarNotificacoes.jsp</li>
	 * </ul>
	 * @return
	 */
	public String listarDestiantarios() throws HibernateException, DAOException {
		
		int idNotificacao = getParameterInt("id", 0);
				
		if (notificacoesSelecionadas == null)
			notificacoesSelecionadas = new ArrayList<NotificacaoAcademica>();
		
		for (NotificacaoAcademica n : notificacoesSelecionadas)
			if (n.getId() == idNotificacao){
				obj = n;
				break;
			}
		
		inicio = false;		
		return forward(LISTAR_DESTINATARIOS);
	}
	
	/**
	 * Carrega os destinatários de uma notificação após alterar seu ano e período de referência.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/enviarNotificacoes.jsp</li>
	 * </ul>
	 * @return
	 */
	public void carregarDiscentesAno( ValueChangeEvent evt ) throws DAOException{
		
		int idNotificacao = getParameterInt("id", 0);
		
		NotificacaoAcademicaDao dao = null;
				
		try {
		
			// Percorre a lista de notificações pois o ano e o periodo de referência são transientes
			for (NotificacaoAcademica n : notificacoesSelecionadas)
				if (n.getId() == idNotificacao){
					obj = n;
					break;
				}
			
			obj.setAnoReferencia((Integer) evt.getNewValue());
			dao = getDAO(NotificacaoAcademicaDao.class);
			obj.setDiscentes(dao.findDiscentesByNotificacao(obj));
			
			if (obj.getDiscentes() == null || obj.getDiscentes().isEmpty())
				addMensagemWarningAjax("A notificação " + obj.getDescricao() + " não possui nenhum discente. Escolha um ano ou período de referência diferente.");
			
			if ( obj.getDiscentes() != null )
				obj.setNumeroDiscentes(obj.getDiscentes().size());
			
			int index = notificacoesSelecionadas.indexOf(obj);
			if ( index >= 0  ){
				notificacoesSelecionadas.get(index).setDiscentes(obj.getDiscentes());
				notificacoesSelecionadas.get(index).setAnoReferencia(obj.getAnoReferencia());
				notificacoesSelecionadas.get(index).setNumeroDiscentes(obj.getNumeroDiscentes());
			}
			
			inicio = true;
			
		} finally {
			if ( dao != null )
				dao.close();
		}
	}
	
	/**
	 * Carrega os destinatários de uma notificação após alterar seu ano e período de referência.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/enviarNotificacoes.jsp</li>
	 * </ul>
	 * @return
	 */
	public void carregarDiscentesPeriodo( ValueChangeEvent evt ) throws DAOException{
		
		int idNotificacao = getParameterInt("id", 0);
		
		NotificacaoAcademicaDao dao = null;
				
		try {
		
			// Percorre a lista de notificações pois o ano e o periodo de referência são transientes
			for (NotificacaoAcademica n : notificacoesSelecionadas)
				if (n.getId() == idNotificacao){
					obj = n;
					break;
				}
			
			obj.setPeriodoReferencia((Integer) evt.getNewValue());
			dao = getDAO(NotificacaoAcademicaDao.class);
			obj.setDiscentes(dao.findDiscentesByNotificacao(obj));
			
			if (obj.getDiscentes() == null || obj.getDiscentes().isEmpty())
				addMensagemWarningAjax("A notificação " + obj.getDescricao() + " não possui nenhum discente. Escolha um ano ou período de referência diferente.");

			
			if ( obj.getDiscentes() != null )
				obj.setNumeroDiscentes(obj.getDiscentes().size());
			
			int index = notificacoesSelecionadas.indexOf(obj);
			if ( index >= 0  ){
				notificacoesSelecionadas.get(index).setDiscentes(obj.getDiscentes());
				notificacoesSelecionadas.get(index).setPeriodoReferencia(obj.getPeriodoReferencia());
				notificacoesSelecionadas.get(index).setNumeroDiscentes(obj.getNumeroDiscentes());
			}
			
			inicio = true;
			
		} finally {
			if ( dao != null )
				dao.close();
		}
	}
	
	/**
	 * Retorna todas as notificações que foram enviadas para discentes.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/acompanharNotificacoes.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<NotificacaoAcademica> getNotificacoesEnviadas() throws HibernateException, DAOException {
		return notificacoesEnviadas;
	}
	
	/**
	 * Retorna se alguma notificação possui ano e período de referência.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public boolean isPossuiAnoPeriodoReferencia () {
		
		if ( possuiAnoPeriodo == null ){
			possuiAnoPeriodo = false;
			
			if ( all != null )
				for ( NotificacaoAcademica n : all )
					if (n.isAnoPeriodoReferencia()){
						possuiAnoPeriodo = true;
						break;
					}
		}
		return possuiAnoPeriodo;
	}
	
	/**
	 * Retorna se alguma notificação selecionada possui ano e período de referência.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public boolean isPossuiAnoPeriodoReferenciaSelecionadas () {
		
		if ( possuiAnoPeriodoSelecionada == null ){
			possuiAnoPeriodoSelecionada = false;
			
			if ( notificacoesSelecionadas != null )
				for ( NotificacaoAcademica n : notificacoesSelecionadas )
					if (n.isAnoPeriodoReferencia()){
						possuiAnoPeriodoSelecionada = true;
						break;
					}
		}
		return possuiAnoPeriodoSelecionada;
	}
	
	/**
	 * Retorna uma lista de selectItems com os possíveis anos de uma notificação.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public List<SelectItem> getAnosCombo() throws NegocioException, DAOException {

		List<SelectItem> anosCombo = new ArrayList<SelectItem>();
		int anoAtual = br.ufrn.arq.util.CalendarUtils.getAnoAtual();
		
		for ( int ano = anoAtual ; ano >= 1900 ; ano-- )
			anosCombo.add(new SelectItem(ano));
	
		return anosCombo;
	}
	
	/**
	 * Retorna uma lista de selectItems com os possíveis períodos de uma notificação.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public List<SelectItem> getPeriodosCombo() throws NegocioException, DAOException {
		
		List<SelectItem> periodosCombo = new ArrayList<SelectItem>();
		int mes = br.ufrn.arq.util.CalendarUtils.getMesAtual();
		int periodoAtual = (( mes < 6 ) ? 1 :  2);
		
		periodosCombo.add(new SelectItem(periodoAtual));
		if (periodoAtual==1)
			periodosCombo.add(new SelectItem(2));
		else
			periodosCombo.add(new SelectItem(1));

		return periodosCombo;
	}
		
	/************************************************************************************************************
	//
	// NOTIFICAÇÃO INDIVIDUAL
	//
	************************************************************************************************************/
	
	/**
	 * Inicia o envio de notificação para um único discente
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciarIndividual() throws HibernateException, DAOException, SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA,SigaaPapeis.ADMINISTRADOR_DAE);
		discente = new Discente();
		Pessoa p = new Pessoa();
		discente.setPessoa(p);
		individual = true;
		return forward(BUSCAR_DISCENTE);
	}

	/**
	 * Cancela o caso de uso da notificação individual.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/buscarDiscente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String cancelarIndividual() {
		resetBean();
		redirectJSF(getSubSistema().getLink());
		return null;
	}
	
	/**
	 * Volta para o formulário na notificação individual.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/buscarDiscente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarForm()  {
		if ( isOperacaoAtiva(SigaaListaComando.ENVIAR_NOTIFICACAO_ACADEMICA.getId()) ){
			setOperacaoAtiva(SigaaListaComando.CADASTRAR_NOTIFICACAO_ACADEMICA.getId());
			return forward(getFormPage());
		} else {
			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
			" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			redirectJSF(getSubSistema().getLink());
		}
		return null;
	}
	
	/**
	 * Valida o discente e avança para o cadastro da notificação acadêmica na notificação individual
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/buscarDiscente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public String preCadastrarIndividual() throws ArqException, NegocioException {
		
		if (isEmpty(discente))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Discente");
		
		if (hasErrors())
			return null;
		
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_NOTIFICACAO_ACADEMICA.getId());
		return preCadastrar();
	}
	
	/**
	 * Direciona para tela de confirmação de notificação individual.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/formNotificacaoAcademica.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String confirmarIndividual() throws HibernateException, ArqException {	
	
		if ( isOperacaoAtiva(SigaaListaComando.CADASTRAR_NOTIFICACAO_ACADEMICA.getId()) ){
			// Recarrega o discente
			discente = getGenericDAO().findByPrimaryKey(discente.getId(), Discente.class);
			
			// Cria o sql
			String sql = "select id_discente from discente where id_discente = " + discente.getId();
			obj.setSqlFiltrosDiscentes(sql);
			// Carrega os discentes
			ArrayList<Discente> discentes = new ArrayList<Discente>();
			discentes.add(discente);
			obj.setDiscentes(discentes);
			// Valida a notificação
			doValidate();
			
			if (hasErrors())
				return null;
			
			setOperacaoAtiva(SigaaListaComando.ENVIAR_NOTIFICACAO_ACADEMICA.getId());
			return forward(NOTIFICAR_INDIVIDUAL);
		} else {
			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
			" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			redirectJSF(getSubSistema().getLink());
		}	
		return null;
	}
	
	/**
	 * Direciona para tela de confirmação de notificação individual.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/discentes/NotificacaoAcademica/notificarIndividual.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String notificarIndividual() throws HibernateException, ArqException, NegocioException {	
		
		if ( isOperacaoAtiva(SigaaListaComando.ENVIAR_NOTIFICACAO_ACADEMICA.getId()) ){
			beforeCadastrarAndValidate();
			doValidate();
			
			try {
				if (!hasErrors()){
					MovimentoCadastro mov = new MovimentoCadastro();
					mov.setObjMovimentado(obj);
					prepareMovimento(SigaaListaComando.CADASTRAR_NOTIFICACAO_ACADEMICA);
					mov.setCodMovimento(SigaaListaComando.CADASTRAR_NOTIFICACAO_ACADEMICA);
					execute(mov);
				} 
				
				if (!hasErrors()){
					notificacoesSelecionadas = new ArrayList<NotificacaoAcademica>();
					notificacoesSelecionadas.add(obj);
					MovimentoCadastro mov = new MovimentoCadastro();
					mov.setColObjMovimentado(notificacoesSelecionadas);
					prepareMovimento(SigaaListaComando.ENVIAR_NOTIFICACAO_ACADEMICA);
					mov.setCodMovimento(SigaaListaComando.ENVIAR_NOTIFICACAO_ACADEMICA);
					execute(mov);
					addMensagem(OPERACAO_SUCESSO);
				}
				
				setOperacaoAtiva(null);
			} finally {
				
			}
			return iniciarIndividual();
		} else {
			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
			" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			redirectJSF(getSubSistema().getLink());
		}
		
		return null;
	}
		
	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setIndividual(Boolean individual) {
		this.individual = individual;
	}

	public Boolean getIndividual() {
		return individual;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getAno() {
		return ano;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Integer getPeriodo() {
		return periodo;
	}

}
