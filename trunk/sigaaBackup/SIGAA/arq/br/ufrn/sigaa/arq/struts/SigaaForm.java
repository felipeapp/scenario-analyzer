/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 29/09/2006
 *
 */
package br.ufrn.sigaa.arq.struts;

import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.PARAMETROS_SESSAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.arq.web.struts.AbstractForm;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * Form genérico do Sigaa
 *
 * @author David Pereira
 *
 */
public class SigaaForm<T> extends AbstractForm {

	protected T obj;

	protected boolean confirm; // Confirmação em caso de remoção

	protected Map<String, Object> mapa = new HashMap<String, Object>();

	private Collection<String> searchData = new ArrayList<String>();

	private Map<String, String> searchMap = new HashMap<String, String>();

	private boolean unregister = false;

	private boolean buscar = false;

	private String confirmButton = "Cadastrar";
	
	public void clearReferenceData() {
		mapa = new HashMap<String, Object>();
	}

	/**
	 * @return the obj
	 */
	public T getObj() {
		return obj;
	}

	public PersistDB getObjAsPersistDB() {
		return (PersistDB) obj;
	}

	/**
	 * @param obj
	 *            the obj to set
	 */
	public void setObj(T obj) {
		this.obj = obj;
	}

	/**
	 * @return the confirm
	 */
	public boolean isConfirm() {
		return confirm;
	}

	/**
	 * @param confirm
	 *            the confirm to set
	 */
	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}

	/**
	 * Cria o Backing Object, usado em atualizações.
	 *
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Object formBackingObject(HttpServletRequest req) throws Exception {

		GenericDAO dao = getGenericDAO(req);

		Object obj = getCommandClass().newInstance();
		int id = RequestUtils.getIntParameter(req, "id");
		try {
			if (id != 0)
				obj = dao.findByPrimaryKey(id, getCommandClass());
		} finally {
			dao.close();
		}

		return obj;

	}

	/**
	 * Classe do objeto que está sendo cadastrado
	 *
	 * @return
	 */
	public Class getCommandClass() {
		return obj.getClass();
	}

	/**
	 * Valida os dados submetidos no formulário. Por padrão, é chamado o método
	 * validate() da classe de domínio.
	 */
	public void validate(HttpServletRequest req) throws DAOException {
		if (obj instanceof Validatable) {
			ListaMensagens erros = ((Validatable) obj).validate();
			if (erros != null) {
				for (MensagemAviso erro : erros.getMensagens()) {
					addMensagem(erro, req);
				}
			}
		}
	}

	/**
	 * Dados de referência para popular o form (ex. popular selects)
	 *
	 * @return Mapa de atributos que devem estar presentes no formulário
	 */
	public void referenceData(HttpServletRequest req) throws ArqException {

	}

	/**
	 * Cria uma busca definida pelo usuário. Se retornar null o padrão é
	 * utilizar findAll().
	 *
	 * @return
	 */
	public Collection customSearch(HttpServletRequest req) throws ArqException {
		return null;
	}

	/**
	 * Informa ao form quais são os atributos em request que são considerados
	 * parâmetros de busca
	 *
	 * @param fields
	 */
	protected void registerSearchData(String... fields) {
		for (String field : fields)
			searchData.add(field);
		searchData.add("buscar");
	}

	/**
	 * Retira os atributos considerados parâmetros de busca de sessão
	 *
	 * @param req
	 */
	public void unregisterSearchData(HttpServletRequest req) {
		req.getSession().removeAttribute("searchMap");
	}

	/**
	 * Retorna os dados da busca
	 *
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getSearchMap(HttpServletRequest req) {
		if (buscar) {
			searchMap = new HashMap<String, String>();
			for (String field : searchData)
				searchMap.put(field, req.getParameter(field));
			req.getSession().setAttribute("searchMap", searchMap);
		} else {
			searchMap = (Map<String, String>) req.getSession().getAttribute(
					"searchMap");
			if (req.getAttribute("voltando") == null
					&& req.getParameter("voltando") == null)
				unregister = true;
		}

		return searchMap;
	}

	/**
	 * Retorna parâmetros de busca para a listagem saber para onde voltar
	 *
	 * @param req
	 * @return
	 */
	
	public String getSearchItem(HttpServletRequest req, String param) {
		return searchMap.get(param);
	}

	@SuppressWarnings("unchecked")
	protected void addAll(String attr, Class classe) throws DAOException {
		GenericDAO dao = new GenericDAOImpl(Sistema.SIGAA);
		try {
			mapa.put(attr, dao.findAll(classe));
		} finally {
			dao.close();
		}

	}

	@SuppressWarnings("unchecked")
	protected void addAll(String attr, Class classe, String orderBy,
			String ascDesc) throws DAOException {
		GenericDAO dao = new GenericDAOImpl(Sistema.SIGAA);

		try {
			mapa.put(attr, dao.findAll(classe, orderBy, ascDesc));
		} finally {
			dao.close();
		}

	}

	@SuppressWarnings("unchecked")
	protected void addAllProjection(String attr, Class classe, String... fields)
			throws DAOException {
		GenericDAO dao = new GenericDAOImpl(Sistema.SIGAA);

		try {
			mapa.put(attr, dao.findAllProjection(classe, fields));
		} finally {
			dao.close();
		}
	}

	@SuppressWarnings("unchecked")
	protected void addAllProjection(String attr, String orderBy,
			String ascDesc, Class classe, String... fields) throws DAOException {

		GenericDAO dao = new GenericDAOImpl(Sistema.SIGAA);

		try {
			mapa.put(attr, dao.findAllProjection(classe, orderBy, ascDesc,
					fields));
		} finally {
			dao.close();
		}
	}

	public Map<String, Object> getReferenceData() {
		return this.mapa;
	}

	/**
	 * @return the unregister
	 */
	public boolean isUnregister() {
		return unregister;
	}

	/**
	 * @return the buscar
	 */
	public boolean isBuscar() {
		return buscar;
	}

	/**
	 * @param buscar
	 *            the buscar to set
	 */
	public void setBuscar(boolean buscar) {
		this.buscar = buscar;
	}

	/**
	 * Retorna as informações de paginação. Usado pela Tag UFRN:table e os DAOs
	 *
	 * @param req
	 * @return
	 */
	public PagingInformation getPaging(HttpServletRequest req) {
		if (req.getParameter("page") != null
				&& !"".equals(req.getParameter("page"))) {
			PagingInformation information = new PagingInformation(RequestUtils
					.getIntParameter(req, "page"));
			req.setAttribute("pagingInformation", information);
			return information;
		} else {
			PagingInformation pi = (PagingInformation) req.getAttribute("pagingInformation");
			if (pi != null)
				return pi;
			return null;
		}
	}

	/**
	 * Retorna um DAO tipado
	 *
	 * @param <U>
	 * @param classe
	 * @param req
	 * @return
	 * @throws ArqException
	 */
	public <U extends GenericDAO> U getDAO(Class<U> classe, HttpServletRequest req)
			throws DAOException {
		try {
			return DAOFactory.getInstance().getDAO(classe,
					getUsuarioLogado(req), getCurrentSession(req));
		} catch (ArqException e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna a sessão corrente em request
	 *
	 * @param req
	 * @return
	 */
	public Session getCurrentSession(HttpServletRequest req) {
		return DAOFactory.getInstance().getSessionRequest(req);
	}

	/**
	 * Método usado para fazer alterações personalizadas no objeto antes do
	 * persistência do AbstractCrudAction
	 *
	 * @param o
	 */
	public void beforePersist(HttpServletRequest req) throws DAOException {

	}

	/**
	 * Método usado para verificar permissões no AbstractCrudAction
	 *
	 * @param req
	 * @throws ArqException
	 */
	public void checkRole(HttpServletRequest req) throws ArqException {

	}

	public void persist(PersistDB obj2, HttpServletRequest req, SigaaForm form) {

	}

	/**
	 * Obtém um DAO com a sessão vinculada a view
	 *
	 * @param req
	 * @return
	 * @throws ArqException
	 */
	public GenericDAO getGenericDAO(HttpServletRequest req) throws DAOException {
		return getDAO(GenericSigaaDAO.class, req);
	}

	@SuppressWarnings("unchecked")
	public void clear() throws Exception {
		this.obj = (T) getCommandClass().newInstance();
	}

	public SubSistema getSubSistemaCorrente(HttpServletRequest req) {
		return (SubSistema) req.getSession().getAttribute("subsistema");
	}

	public int getUnidadeGestora(HttpServletRequest req) throws ArqException {

		try {
			return getParametrosAcademicos(req).getUnidade().getId();
		} catch (Exception e) {
			throw new ArqException("É necessário unidade acadêmica definida para acessar essa operação");
		}
		/*Unidade unidade = (Unidade) getUsuarioLogado(req).getUnidade().getGestoraAcademica();

		int unidadeGestoraId = unidade.getId();
		if (unidade.getTipo() == UnidadeGeral.UNIDADE_FATO) {
			// unidadeGestoraId = unidade.getUnidadeGestora().getId();
		}
		return unidadeGestoraId;*/
	}

	public ParametrosGestoraAcademica getParametrosAcademicos(HttpServletRequest req) throws ArqException {
		if ( req.getSession().getAttribute(PARAMETROS_SESSAO) == null ) {
			return ParametrosGestoraAcademicaHelper.getParametros((Usuario)getUsuarioLogado(req));
		} else {
			return (ParametrosGestoraAcademica) req.getSession().getAttribute(PARAMETROS_SESSAO);
		}
	}
	
	public char getNivelEnsino(HttpServletRequest req) throws ArqException  {
		char nivel = SigaaSubsistemas.getNivelEnsino(getSubSistemaCorrente(req));
		if ( nivel == ' ') {
			Character nivelSession = (Character) req.getSession().getAttribute("nivel");
			if ( nivelSession == null ) {
				throw new ArqException("Nivel Necessita de definição para esta operação");
			}
			return nivelSession;
		}
		return nivel;
	}

	public void setDefaultProps() {

	}

	public String getConfirmButton() {
		return confirmButton;
	}

	public void setConfirmButton(String confirmButton) {
		this.confirmButton = confirmButton;
	}

}