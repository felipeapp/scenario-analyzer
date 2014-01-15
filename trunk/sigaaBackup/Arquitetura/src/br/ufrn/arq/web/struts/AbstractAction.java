/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 08/09/2004
 */
package br.ufrn.arq.web.struts;
 
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.UserTransaction;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;
import org.hibernate.Session;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.dao.PaginableDAO;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.FacesContextUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.Step;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe da arquitetura em que todas as Actions do Sistema devem herdar.
 *
 * @author Gleydson Lima
 *
 */
public abstract class AbstractAction extends DispatchAction {

	private static final String CONTROLED_SESSION = "listaAtributos";

	private static final String LAST_COMMAND = "lastCommand";

	/** Formatador Padrão de Datas */
	private SimpleDateFormat df;

	private DecimalFormat decF;

	public AbstractAction() {
		decF = new DecimalFormat("#,##0.00");
		df = new SimpleDateFormat("dd/MM/yyyy");
		df.setLenient(false);
	}

	/**
	 * Retorna a data contida na String. Caso a data seja invï¿½lida, ï¿½
	 * retornado null
	 *
	 * @param data
	 * @return
	 */
	public Date parseDate(String data) {
		return Formatador.getInstance().parseDate(data);
	}

	/**
	 * Retorna o valor contido na String. Caso o valor seja invï¿½lido, retorna
	 * 0.
	 *
	 * @param valor
	 * @return
	 */
	public Double parseValor(String valor) {
		try {
			return new Double(decF.parse(valor).doubleValue());
		} catch (Exception e) {
			return new Double(0);
		}
	}

	/**
	 * Este mï¿½todo ï¿½ a implementaï¿½ï¿½o principal da Action e ï¿½ marcado
	 * como abstrato para obrigar a implementaï¿½ï¿½o no filho.
	 *
	 *
	 * public abstract ActionForward execute(ActionMapping mapping, ActionForm
	 * form, HttpServletRequest req, HttpServletResponse res) throws Exception;
	 */

	/**
	 * Retorna o Delegate do Usuï¿½rio e cria caso ainda nï¿½o possua
	 *
	 * @param req
	 * @return
	 */
	protected FacadeDelegate getUserDelegate(HttpServletRequest req)
			throws ArqException {

		HttpSession session = req.getSession(false);
		if (session == null) {
			throw new ArqException("Sessão não ativada");
		} else {

			FacadeDelegate facade = (FacadeDelegate) session
					.getAttribute("userFacade");
			if (facade == null) {
				String jndiName = (String) getServlet().getServletContext()
						.getAttribute("jndiName");
				facade = new FacadeDelegate(jndiName);
				session.setAttribute("userFacade", facade);
			}
			return facade;
		}
	}

	/**
	 * Chama o processador baseado no movimento passado
	 *
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public Object execute(Movimento mov, HttpServletRequest req)
			throws NegocioException, ArqException, RemoteException {
		forceCloseSession(req);

		mov.setApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext()));
		return getUserDelegate(req).execute(mov, req);

	}

	/**
	 * Chama o processador baseado no movimento passado
	 *
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public Object execute(Movimento mov, HttpServletRequest req,
			ActionMapping mapping) throws NegocioException, ArqException,
			RemoteException {

		forceCloseSession(req);
		mov.setApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext()));
		return getUserDelegate(req).execute(mov, req);

	}

	/**
	 * Solicita desbloqueio do movimento para o usuï¿½rio
	 *
	 * @param codMovimento
	 *            Cï¿½digo do movimento a ser desbloqueado
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public void prepareMovimento(int codMovimento, HttpServletRequest req)
			throws ArqException, RemoteException, NegocioException {

		getUserDelegate(req).prepare(codMovimento, req);

	}

	public void prepareMovimento(Comando comando, HttpServletRequest req)
			throws ArqException, RemoteException, NegocioException {
		req.getSession(false).setAttribute(LAST_COMMAND, comando);
		if ( getUserDelegate(req) == null ) {
			throw new ArqException("Usuário não logado");
		}
		getUserDelegate(req).prepare(comando.getId(), req);

	}

	public Integer getSistema(HttpServletRequest req) {
		return (Integer) req.getAttribute("sistema");
	}

	/**
	 * Retorna um DAO do tipo Genï¿½rico
	 *
	 * @return
	 * @throws DAOException
	 */
	public GenericDAO getGenericDAO(HttpServletRequest req) throws ArqException {

		GenericDAOImpl dao = new GenericDAOImpl(getSistema(req));
		dao.setUsuario(getUsuarioLogado(req));
		dao.setSession(getCurrentSession(req));

		return dao;

	}

	/**
	 * Retorna o usuï¿½rio logado
	 *
	 * @param req
	 * @return Usuario
	 */
	public UsuarioGeral getUsuarioLogado(HttpServletRequest req) throws ArqException {

		UsuarioGeral user = (UsuarioGeral) req.getSession(false).getAttribute("usuario");
		
		if (user == null) {
			throw new ArqException("Usuário não logado");
		}
		
		return user;

	}

	/**
	 * Verifica se o usuï¿½rio logado tem o papel informado
	 *
	 * @param papel
	 * @param req
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public void checkRole(int papel, HttpServletRequest req) throws SegurancaException, ArqException {
		UsuarioGeral user = getUsuarioLogado(req);
		UFRNUtils.checkRole(user, papel);
	}

	/**
	 * Verifica se o usuï¿½rio logado possui um dos papï¿½is informados no array
	 *
	 * @param papeis
	 * @param req
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public void checkRole(int[] papeis, HttpServletRequest req) throws SegurancaException, ArqException {
		UsuarioGeral user = getUsuarioLogado(req);
		UFRNUtils.checkRole(user, papeis);
	}
	
	/**
	 * Verifica se o usuário logado tem os papéis passados como parâmetro
	 * e se esses papéis são válidos para a unidade do parâmetro.
	 * @throws ArqException 
	 */
	public void checkRole(HttpServletRequest req, UnidadeGeral unidade, int... papeis) throws ArqException {
		UFRNUtils.checkRole(getUsuarioLogado(req), unidade, papeis);
	}

	/**
	 * Verifica se o usuï¿½rio estï¿½ logado e possui permissï¿½es acesso ao
	 * SIPAC
	 *
	 * @param req
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public void checkRoleSipac(HttpServletRequest req)
			throws SegurancaException, ArqException {
		UsuarioGeral user = getUsuarioLogado(req);

		if (user == null || !user.isUserInSIPAC()) {
			throw new SegurancaException(
					"Usuï¿½rio nï¿½o autorizado a realizar esta operaï¿½ï¿½o");
		}
	}

	/**
	 * Retorna true se usuï¿½rio possuir alguns dos papeis passados como
	 * parï¿½metro
	 *
	 * @param req
	 * @param papeis
	 * @return true se usuï¿½rio possuir papeis
	 * @throws SegurancaException
	 *             se nï¿½o houver usuï¿½rio logado
	 */
	public boolean isUserInRole(HttpServletRequest req, int... papeis)
			throws SegurancaException, ArqException {

		UsuarioGeral user = getUsuarioLogado(req);
		for (int papel : papeis) {
			if (user.isUserInRole(papel))
				return true;
		}

		return false;
	}

	public <T extends GenericDAO> T getDAO(Class<T> daoClass, HttpServletRequest req) throws ArqException {
		return DAOFactory.getInstance().getDAO(daoClass, (req == null ? null : getUsuarioLogado(req)), getCurrentSession(req));
	}

	/**
	 * Mï¿½todo que seta os atributos para serem usados na paginaï¿½ï¿½o de
	 * listas
	 *
	 * @param req
	 * @param pagDAO
	 */
	public void setAttributePaginacao(HttpServletRequest req,
			PaginableDAO pagDAO) {

		int pageNum = pagDAO.getPageNum();
		int totalPages = pagDAO.getTotalPaginas();

		req.setAttribute("inicio", pagDAO.getPageNum());
		int total = (int) Math.ceil((double) pagDAO.getCount()
				/ pagDAO.getPageSize());
		req.setAttribute("fim", total);
		req.setAttribute("total", pagDAO.getCount());

		// Flag para a tag de paginaï¿½ï¿½o
		if (total != 0)
			req.setAttribute("paginable", "true");

		// TODO Rafael - Deprecated?
		// Lista de pï¿½ginas anteriores e prï¿½ximas
		LinkedList<Integer> pagAnteriores = new LinkedList<Integer>();
		LinkedList<Integer> pagPosteriores = new LinkedList<Integer>();

		for (int i = pageNum - 5; (i < pageNum); i++)
			if (i > 0)
				pagAnteriores.add(i);

		for (int i = pageNum + 1; (i <= totalPages) && (i <= pageNum + 5); i++)
			pagPosteriores.add(i);

		req.setAttribute("pagAnteriores", pagAnteriores);
		req.setAttribute("pagPosteriores", pagPosteriores);

	}

	/**
	 * Mï¿½todo que auxilia na troca de pï¿½ginas nos DAOs paginï¿½veis
	 *
	 * @param pagDAO
	 * @param form
	 */
	public void navegaPagina(PaginableDAO pagDAO, AbstractForm form) {
		if (form.getAcao() == ConstantesActionGeral.PROX_PAGINA) {
			form.setPageNum(form.getPageNum() + 1);
		}
		if (form.getAcao() == ConstantesActionGeral.RET_PAGINA) {
			if (form.getPageNum() > 1) {
				form.setPageNum(form.getPageNum() - 1);
			}
		} else
			pagDAO.setPageNum(form.getPageNum());

		if (form.getPageSize() > 0)
			pagDAO.setPageSize(form.getPageSize());

		pagDAO.setPaginable(true);

		if (form.getPageNum() <= 0) {
			form.setPageNum(1);
			pagDAO.setPageNum(1);
		}

	}

	/**
	 * Transforma uma coleï¿½ï¿½o qualquer em ArrayList
	 *
	 * @param col
	 * @return
	 */
	public <T> ArrayList<T> toArrayList(Collection<T> col) {
		ArrayList<T> list = new ArrayList<T>();
		if (col != null) {
			list.addAll(col);
		}
		return list;
	}

	public <T> TreeSet<T> toTreeSet(Collection<T> col) {
		TreeSet<T> treeSet = new TreeSet<T>();

		for (Iterator<T> it = col.iterator(); it.hasNext(); ) {
			treeSet.add(it.next());
		}

		return treeSet;
	}

	public <T> HashSet<T> toHashSet(Collection<T> col) {
		HashSet<T> hashSet = new HashSet<T>();

		for (Iterator<T> it = col.iterator(); it.hasNext(); ) {
			hashSet.add(it.next());
		}

		return hashSet;
	}

	/**
	 * Retorna o ano orï¿½amentï¿½rio escolhido pelo usuï¿½rio
	 *
	 * @param req
	 * @return
	 * @throws ArqException
	 */
	public int getAnoOrcamentario(HttpServletRequest req) throws ArqException {
		return getUsuarioLogado(req).getAnoOrcamentario();
	}

	/**
	 * Retorna uma transaï¿½ï¿½o do tipo Bean Managed para ser usado na Action
	 *
	 * @return
	 * @throws ArqException
	 */
	public UserTransaction getTransactionBMT() throws ArqException {

		try {
			InitialContext ic = new InitialContext();
			return (UserTransaction) ic.lookup("UserTransaction");
		} catch (Exception e) {
			throw new ArqException("Nï¿½o foi possï¿½vel criar transaï¿½ï¿½o: "
					+ e.getMessage(), e);
		}
	}

	/**
	 * Adiciona uma mensagem que serï¿½ mostrada pelo arquivo
	 * include/mensagens.jsp
	 *
	 * @param mensagem
	 * @param req
	 */
	@SuppressWarnings("unchecked")
	public void addMessage(String mensagem, HttpServletRequest req) {

		ArrayList<String> mensagens = null;
		if (req.getSession(false).getAttribute("mensagens") == null) {
			mensagens = new ArrayList<String>();
		} else {
			mensagens = (ArrayList<String>) req.getSession(false).getAttribute("mensagens");
		}
		mensagens.add(mensagem);
		req.getSession(false).setAttribute("mensagens", mensagens);
	}

	/**
	 * Retorna erros. Sobrescrita provisï¿½ria do mï¿½todo getErros.
	 *
	 * @see Action#getErrors(javax.servlet.http.HttpServletRequest)
	 */
	public ActionErrors getErrors(HttpServletRequest req) {
		ActionErrors errors = (ActionErrors) req
				.getAttribute(Globals.ERROR_KEY);
		if (errors == null) {
			errors = new ActionErrors();
		}
		return errors;

	}

	public boolean hasErros(HttpServletRequest req){
		ActionErrors errors = (ActionErrors) req.getAttribute(Globals.ERROR_KEY);
		if (errors != null && !errors.isEmpty()) {
			return true;
		}
		return false;
	}

	public void addErro(String mensagem, HttpServletRequest req) {
		ActionMessages erros = getErrors(req);

		erros.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(mensagem));

		saveErrors(req, erros);
	}

	public void addErro(String mensagem, HttpServletRequest req,
			String argumento) {
		addErro(mensagem, req, new String[] { argumento });
	}

	@SuppressWarnings("deprecation")
	public void addErro(String mensagem, HttpServletRequest req,
			Object[] argumentos) {
		ActionErrors erros = getErrors(req);
		if (argumentos == null)
			erros.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage(mensagem));
		else
			erros.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					mensagem, argumentos));
		saveErrors(req, erros);
	}

	/**
	 * <p>
	 * Save the specified error messages keys into the appropriate request
	 * attribute for use by the &lt;html:errors&gt; tag, if any messages are
	 * required. Otherwise, ensure that the request attribute is not created.
	 * </p>
	 *
	 * @param request
	 *            The servlet request we are processing
	 * @param errors
	 *            Error messages object
	 * @since Struts 1.2
	 */
	protected void saveErrors(HttpServletRequest request, ActionMessages errors) {

		// Remove any error messages attribute if none are required
		if ((errors == null) || errors.isEmpty()) {
			request.removeAttribute(Globals.ERROR_KEY);
			return;
		}

		// Save the error messages we need
		request.setAttribute(Globals.ERROR_KEY, errors);

	}

	/**
	 * Retira o formulï¿½rio de request caso as mensagens serï¿½o mostrados.
	 * Esse mï¿½todo serve para limpar um formulï¿½rio para poder enviar para
	 * pï¿½gina de inclusï¿½o sem os dados anteriores.
	 *
	 * @param req
	 * @param formName
	 */
	public void clearForm(HttpServletRequest req, String formName) {

		if (req.getAttribute("mensagens") != null) {
			req.removeAttribute(formName);
			req.getSession().removeAttribute(formName);
		}
	}

	/**
	 * Adiciona uma linha no cabeï¿½alho do relatï¿½rio
	 *
	 * @param mensagem
	 * @param req
	 */
	@SuppressWarnings("unchecked")
	public void addComplementoPrint(String mensagem, HttpServletRequest req) {

		ArrayList<String> mensagens = null;
		if (req.getAttribute("complemento") == null) {
			mensagens = new ArrayList<String>();
			req.setAttribute("complemento", mensagens);
		} else {
			mensagens = (ArrayList<String>) req.getAttribute("complemento");
		}
		mensagens.add(mensagem);
	}

	/**
	 * Informa o subsistema atual para o uso do linkSubSistema
	 *
	 * @param req
	 * @param subsistema
	 */
	public void setSubSistemaAtual(HttpServletRequest req, int subsistema) {

		req.getSession(false).removeAttribute("subsistema");
		req.getSession(false).setAttribute("subsistema",
				String.valueOf(subsistema));
	}

	public void setSubSistemaAtual(HttpServletRequest req, SubSistema subsistema) {

		req.getSession(false).removeAttribute("subsistema");
		req.getSession(false).setAttribute("subsistema", subsistema);
	}

	/**
	 * Retorna o subsistema que estï¿½ sendo usado atualmente
	 *
	 * @param req
	 * @return subsistema
	 */
	public static int getSubSistemaAtual(HttpServletRequest req) {
		Object subsistema = null;
		HttpSession session = req.getSession(false);

		if (session != null)
			subsistema = session.getAttribute("subsistema");

		if (subsistema instanceof SubSistema) {
			return ((SubSistema) subsistema).getId();
		} else {
			return (subsistema == null) ? 0 : Integer.parseInt(subsistema
					.toString());
		}
	}

	/**
	 * Mï¿½todo usado para obter a aï¿½ï¿½o quando nï¿½o hï¿½ Form.
	 *
	 * @param req
	 * @return
	 */
	public int getAcao(HttpServletRequest req) throws ServletException {
		if (req.getParameter("acao") == null) {
			return 0;
		} else {
			return Integer.parseInt(req.getParameter("acao"));
		}

	}

	public boolean isLocal(HttpServletRequest req) throws ServletException {

		if (req.getSession(true).getAttribute("local") != null) {
			return true;
		} else {
			return false;
		}
	}

	/** Retorna a operaï¿½ï¿½o ativa */
	public String getOperacaoAtiva(HttpServletRequest req) {
		return (String) req.getSession().getAttribute("operacaoAtiva");
	}

	/** Seta a operaï¿½ï¿½o ativa */
	public void setOperacaoAtiva(HttpServletRequest req, String operacao,
			String msg) {
		req.getSession().setAttribute("operacaoAtiva", operacao);
		req.getSession().setAttribute("mensagemOperacaoAtiva", msg);
	}

	public void setOperacaoAtiva(HttpServletRequest req, String operacao) {
		setOperacaoAtiva(req, operacao, "A operação já havia sido concluída. Por favor, reinicie os procedimentos.");
	}
	
	/** Remove a operaï¿½ï¿½o ativa de sessï¿½o */
	public void removeOperacaoAtiva(HttpServletRequest req) {
		req.getSession().removeAttribute("operacaoAtiva");
	}

	public int getUnidadeGestora(HttpServletRequest req) throws ArqException {

		UnidadeGeral unidade = getUsuarioLogado(req).getUnidade();

		int idUnidadeGestora = unidade.getId();
		if (unidade.getTipo() == UnidadeGeral.UNIDADE_FATO) {
			idUnidadeGestora = unidade.getUnidadeGestora().getId();
		}
		return idUnidadeGestora;
	}

	/**
	 * Retorna o ActionForward e seta atributos em sessï¿½o para redicionar
	 * usuï¿½rio externo para tela de logon.
	 *
	 * @param req
	 * @param mapping
	 * @return
	 */
	public ActionForward forwardUsuarioExternoNaoLogago(HttpServletRequest req,
			ActionMapping mapping) {
		req.setAttribute("requestLogin", Boolean.TRUE);
		req.setAttribute("url", req.getRequestURL() + "?"
				+ req.getQueryString());
		return mapping.findForward("login");
	}

	/**
	 * Verifica se o usuï¿½rio tem um determinado papel
	 *
	 * @param papel
	 * @param req
	 * @return
	 */
	public boolean isUserInRole(int papel, HttpServletRequest req) {
		try {
			return getUsuarioLogado(req).isUserInRole(papel);
		} catch (ArqException e) {
			return false;
		}
	}

	/**
	 * Retorna o ano atual
	 *
	 * @return
	 */
	public int getAnoAtual() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.YEAR);
	}
	
	/**
	 * Retorna o mes atual
	 *
	 * @return
	 */
	public String getMesAtual() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		String mes = "";
		
		if((c.get(Calendar.MONTH)+1) < 10)
			mes = "0";
		mes += c.get(Calendar.MONTH)+1;
		
		return mes;
	}
	
	/**
	 * Retorna um objeto a partir do ID passado por parametro na request
	 *
	 * @param <T>
	 * @param classe
	 * @param param
	 * @param req
	 * @return
	 * @throws ArqException
	 */
	public <T extends PersistDB> T getObjById(Class<T> classe, String param, HttpServletRequest req) throws ArqException {
		GenericDAO dao = getGenericDAO(req);
		try {
			return dao.findByPrimaryKey(new Integer(req.getParameter(param)),
					classe);
		} finally {
			dao.close();
		}
	}

	/**
	 * Retoran um atributo em sessao
	 *
	 * @param att
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object getFromSession(String att, HttpServletRequest req) {
		ArrayList<String> lista = (ArrayList<String>) req.getSession().getAttribute(CONTROLED_SESSION);
		if (lista == null || !lista.contains(att))
			return null;
		return req.getSession(false).getAttribute(att);
	}

	/**
	 * Adiciona o atributo em sessão
	 *
	 * @param attribute
	 * @param objeto
	 * @param req
	 */
	@SuppressWarnings("unchecked")
	public void addSession(String attribute, Object objeto, HttpServletRequest req) {

		ArrayList<String> lista = (ArrayList<String>) req.getSession().getAttribute(CONTROLED_SESSION);
		if (lista == null) {
			lista = new ArrayList<String>();
			req.getSession().setAttribute(CONTROLED_SESSION, lista);
		}
		req.getSession().setAttribute(attribute, objeto);
		lista.add(attribute);
	}

	/**
	 * Remove o atributo de sessão
	 *
	 * @param attribute
	 * @param objeto
	 * @param req
	 */
	public void removeSession(String attribute, HttpServletRequest req) {
		req.getSession().removeAttribute(attribute);
	}

	@SuppressWarnings("unchecked")
	public void clearSession(HttpServletRequest req) {

		ArrayList<String> lista = (ArrayList<String>) req.getSession().getAttribute(CONTROLED_SESSION);
		if (lista != null) {
			for (String atributo : lista) {
				req.getSession().removeAttribute(atributo);
			}
		}
	}

	/**
	 * Seta o passo atual (controla até onde o ufrn:step deve exibir os passos
	 *
	 * @param req
	 * @param step
	 */
	public void setStep(HttpServletRequest req, int step) {
		req.getSession().setAttribute("step", step);
	}

	/**
	 * Retorna o passo atual
	 *
	 * @param req
	 * @return
	 */
	public int getCurrentStep(HttpServletRequest req) {
		Integer step = (Integer) req.getSession().getAttribute("step");
		if (step != null)
			return step.intValue();
		return -1;
	}

	/**
	 * Seta o passo atual de acordo com a view
	 *
	 * @param req
	 * @param step
	 */
	@SuppressWarnings("unchecked")
	public void setStep(HttpServletRequest req, String step) {

		ArrayList<Step> steps = (ArrayList<Step>) req.getSession().getAttribute("steps");
		if (steps != null) {
			int stepOrd = 1;
			for (Step s : steps) {
				if (s.getView().endsWith(step))
					break;
				else
					stepOrd++;
			}
			req.getSession().setAttribute("step", stepOrd);
		}
	}

	/**
	 * Retorna um passo
	 *
	 * @param req
	 * @param step
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Step getStep(HttpServletRequest req, int step) {
		ArrayList<Step> steps = (ArrayList<Step>) req.getSession().getAttribute("steps");
		if (steps == null)
			return null;
		return steps.get(step);
	}

	/**
	 * Retorna o último passo
	 *
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Step getLastStep(HttpServletRequest req) {
		ArrayList<Step> steps = (ArrayList<Step>) req.getSession().getAttribute("steps");
		if (steps == null)
			return null;
		return steps.get(steps.size() - 1);
	}

	/**
	 * Retorna o primeiro passo
	 *
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Step getFirstStep(HttpServletRequest req) {
		ArrayList<Step> steps = (ArrayList<Step>) req.getSession().getAttribute("steps");
		if (steps == null)
			return null;
		return steps.get(0);
	}

	@SuppressWarnings("unchecked")
	public void addStep(HttpServletRequest req, String descricao,
			String action, String view) {

		// Lista de passos do caso de uso
		ArrayList<Step> steps = (ArrayList<Step>) req.getSession().getAttribute("steps");
		if (steps == null) {
			steps = new ArrayList<Step>();
			req.getSession().setAttribute("steps", steps);
		}

		// Adiciona um passo e coloca o link para a ação de navegação
		steps.add(new Step(descricao, action + ".do?dispatch=navegar&view=" + view));

	}

	@SuppressWarnings("unchecked")
	public void addStep(Step step, HttpServletRequest req) {

		// Lista de passos do caso de uso
		ArrayList<Step> steps = (ArrayList<Step>) req.getSession().getAttribute("steps");
		if (steps == null) {
			steps = new ArrayList<Step>();
			req.getSession().setAttribute("steps", steps);
		}
		// Adiciona um passo e coloca o link para a ação de navegação
		steps.add(step);

	}

	/**
	 * Limpa todos os passos armazenados
	 *
	 * @param req
	 */
	public void clearSteps(HttpServletRequest req) {
		req.getSession().removeAttribute("steps");
	}

	public ActionForward getMappingSubSistema(HttpServletRequest req, ActionMapping mapping) throws ArqException, IOException {
		return getMappingSubSistema( req,null, mapping);
	}
	
	/**
	 * Retorna o mapping do subsistema atual
	 *
	 * @param req
	 * @param response 
	 * @param mapping
	 * @return
	 * @throws ArqException 
	 * @throws IOException 
	 */
	public ActionForward getMappingSubSistema(HttpServletRequest req,
			HttpServletResponse response, ActionMapping mapping) throws ArqException, IOException {
		SubSistema sub = null;
		Object o = req.getSession().getAttribute("subsistema");
		if (o instanceof SubSistema) {
			sub = (SubSistema) o; 
		} else if (o instanceof String){
			GenericSharedDBDao genericDAO = new GenericSharedDBDao();
			
			try {
				sub = genericDAO.findByPrimaryKey(Integer.parseInt((String) o), SubSistema.class);
			} finally {
				genericDAO.close();
			}
		}
		
		if (sub != null) {
			if (response != null) {
				response.sendRedirect(sub.getLink());
			} else {
				return mapping.findForward(sub.getForward());
			}
			return null;
		} else {
			return mapping.findForward("menuPrincipal");
		}

	}

	/**
	 * Adiciona uma mensagem na lista
	 *
	 * @param msgConteudo
	 * @param req
	 */
	public static void addMensagemErro(String msgConteudo, HttpServletRequest req) {
		MensagemAviso msg = new MensagemAviso(msgConteudo,
				TipoMensagemUFRN.ERROR);
		addMensagem(msg, req);
	}

	public void addMensagensErro(Collection<String> mensagens,
			HttpServletRequest req) {
		for (String msg : mensagens) {
			addMensagemErro(msg, req);
		}
	}

	/**
	 * Adiciona uma mensagem na lista de mensagens da solicitação
	 *
	 * @param mensagem
	 * @param req
	 */
	public static void addMensagem(MensagemAviso mensagem, HttpServletRequest req) {

		ListaMensagens lista = (ListaMensagens) req.getSession()
				.getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION);
		if (lista == null) {
			lista = new ListaMensagens();
			req.getSession().setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, lista);
		}
		lista.addMensagem(mensagem);

	}

	/**
	 * Adiciona uma mensagem de informação na solicitação
	 *
	 * @param msgConteudo
	 * @param req
	 */
	public void addInformation(String msgConteudo, HttpServletRequest req) {
		MensagemAviso msg = new MensagemAviso(msgConteudo,
				TipoMensagemUFRN.INFORMATION);
		addMensagem(msg, req);
	}

	/**
	 * Adiciona uma mensagem de warning na solicitação
	 *
	 * @param msgConteudo
	 * @param req
	 */
	public void addWarning(String msgConteudo, HttpServletRequest req) {
		MensagemAviso msg = new MensagemAviso(msgConteudo,
				TipoMensagemUFRN.WARNING);
		addMensagem(msg, req);
	}
	
	/**
	 * Adiciona uma mensagem ao sistema buscando-a no banco de dados,
	 * de acordo com o código passado como parâmetro.
	 * @param codigo
	 * @throws ArqException 
	 */
	public void addMensagem(HttpServletRequest req, String codigo, Object... params) throws ArqException {
		MensagemAviso msg = UFRNUtils.getMensagem(codigo, params);
		addMensagem(msg, req);
	}

	/**
	 * Cria uma lista temporária par armazenas os erros informados ao validador
	 * @deprecated Usar <code>ListaMensagens newListaMensagens(HttpServletRequest req)</code>
	 * @param req
	 * @return
	 */
	@Deprecated
	public ArrayList<MensagemAviso> newListMensagens(HttpServletRequest req) {
		ArrayList<MensagemAviso> lista = new ArrayList<MensagemAviso>();
		req.getSession().setAttribute(ListaMensagens.LISTA_TEMPORARIA_SESSION, lista);
		return lista;
	}
	
	/**
	 * Cria uma lista temporária par armazenas os erros informados ao validador
	 *
	 * @param req
	 * @return
	 */
	public ListaMensagens newListaMensagens(HttpServletRequest req) {
		ListaMensagens lista = new ListaMensagens();
		req.getSession().setAttribute(ListaMensagens.LISTA_TEMPORARIA_SESSION, lista);
		return lista;
	}

	/**
	 * Adiciona a coleção de mensagens informadas
	 *
	 * @param mensagens
	 * @param req
	 */
	public static void addMensagens(Collection<MensagemAviso> mensagens,
			HttpServletRequest req) {

		ListaMensagens msgs = (ListaMensagens) req
				.getSession().getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION);
		if (msgs == null) {
			msgs = new ListaMensagens();
			req.getSession().setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, msgs);
		}
		msgs.addAll(mensagens);
	}

	/**
	 * Verifica se existem erros. Retira da lista temporária e coloca na
	 * persistente
	 *
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean flushErros(HttpServletRequest req) {
		
		if (req.getSession().getAttribute(ListaMensagens.LISTA_TEMPORARIA_SESSION) != null) {
			Object lista = req.getSession().getAttribute(ListaMensagens.LISTA_TEMPORARIA_SESSION);
			
			if (lista instanceof ListaMensagens) {
				ListaMensagens lm = (ListaMensagens) lista;
				req.getSession().removeAttribute(ListaMensagens.LISTA_TEMPORARIA_SESSION);
				if (!lm.isEmpty()) {
					addMensagens(lm.getMensagens(), req);
					return true;
				}
			} else {
				ArrayList<MensagemAviso> errosAtuais = (ArrayList<MensagemAviso>) lista;
				req.getSession().removeAttribute(ListaMensagens.LISTA_TEMPORARIA_SESSION);
				if (!errosAtuais.isEmpty()) {
					addMensagens(errosAtuais, req);
					return true;
				}				
			}
		}
		
		return false;
	}

	/**
	 * Usado para remover elementos de um Set, uma vez que eles não tem
	 * vinculação com posição
	 *
	 * @param col
	 * @param posicao
	 */
	public <T> void removePorPosicao(Collection<T> col, int posicao) {
		List<T> lista;
		if (col != null) {

			if (posicao < 0 || posicao >= col.size()) {
				return;
			}

			if (col instanceof List<?>) {
				lista = (List<T>) col;
				lista.remove(posicao);
			}
			else {
				lista = toArrayList(col);
				lista.remove(posicao);
				col.clear();
				col.addAll(lista);
			}
		}
	}

	/**
	 * Açao padrão de cancelar
	 *
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward cancelar(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		clearSession(request);
		removeSession(mapping.getName(), request);
		clearSteps(request);

		String defaultCancel = (String) request.getSession().getAttribute(
				"defaultCancel");
		if (defaultCancel != null) {
			response.sendRedirect(defaultCancel);
			return null;
		} else {
			return getMappingSubSistema(request,response, mapping);
		}

	}

	public PagingInformation getPaging(HttpServletRequest req) {
		if (req.getParameter("page") != null
				&& !"".equals(req.getParameter("page"))) {
			int page = 1;
			try {
				page = Integer.parseInt(req.getParameter("page"));
			} catch (Exception e) {
			}

			PagingInformation information = new PagingInformation(page);
			req.setAttribute("pagingInformation", information);
			return information;
		} else {
			PagingInformation pi = (PagingInformation) req.getAttribute("pagingInformation");
			if (pi != null)
				return pi;
			return null;
		}
	}

	public Comando getUltimoComando(HttpServletRequest req) {
		return (Comando) req.getSession(false).getAttribute(LAST_COMMAND);
	}

	/**
	 * Retorna a sessão corrente do Hibernate que está em request
	 *
	 * @param req
	 * @return
	 */
	public Session getCurrentSession(HttpServletRequest req) {
		return DAOFactory.getInstance().getSessionRequest(req);
	}

	public void forceCloseSession(HttpServletRequest req) {

		Session session = getCurrentSession(req);
		if (session != null) {
			session.close();
		}
	}

	/**
	 * Retorna um managed-bean existente no container do JavaServer Faces.
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static <T> T getMBean(String mbeanName, HttpServletRequest req, HttpServletResponse res) {
		FacesContext fc = FacesContextUtils.getFacesContext(req, res);
		return (T) fc.getApplication().getVariableResolver().resolveVariable(fc, mbeanName);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getBean(HttpServletRequest req, String beanName) {
		WebApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(req.getSession().getServletContext());
		return (T) ac.getBean(beanName);
	}

}