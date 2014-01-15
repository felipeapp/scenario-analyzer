/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 18/09/2006
 *
 */
package br.ufrn.sigaa.arq.struts;

import static br.ufrn.sigaa.arq.struts.ConstantesCadastro.SESSION_MAP_NAME;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 *
 * @author David Ricardo
 *
 */
public abstract class AbstractWizardAction extends AbstractCrudAction {

	@SuppressWarnings("unchecked")
	public void toSession(HttpServletRequest req, String name, Object value) {
		Map sessionMap = (Map) req.getSession().getAttribute(SESSION_MAP_NAME);
		sessionMap.put(name, value);
	}

	public Object fromSession(HttpServletRequest req, String name) {
		Map<?, ?> sessionMap = (Map<?, ?>) req.getSession().getAttribute(SESSION_MAP_NAME);
		return sessionMap.get(name);
	}

	/**
	 * Usado para navegar entre forward do mesmo caso de uso
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward navegar(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String view = request.getParameter("view");
		setStep(request, view);
		return mapping.findForward(view);
	}


}
