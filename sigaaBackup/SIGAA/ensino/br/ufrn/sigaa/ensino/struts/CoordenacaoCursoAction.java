/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ensino.struts;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.arq.struts.ConstantesCadastro;
import br.ufrn.sigaa.arq.struts.SigaaForm;

/**
 * @author Andre M Dantas
 *
 */
public class CoordenacaoCursoAction extends AbstractCrudAction {

	@Override
	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class, req);

		getForm(form).checkRole(req);
		getForm(form).referenceData(req);
		populateRequest(req, getForm(form).getReferenceData());

		Collection lista = null;

		/* Popular formulário de Busca */
		SigaaForm sForm = getForm(form);
		Map<String, String> searchMap = sForm.getSearchMap(req);

		/* Retirar dados da busca de sessão */
		if (getForm(form).isUnregister()) {
			searchMap = null;
			getForm(form).unregisterSearchData(req);
		}

		if (searchMap != null) {
			Set<Entry<String, String>> entrySet = searchMap.entrySet();
			for (Entry<String, String> entry : entrySet)
				BeanUtils.setProperty(sForm, entry.getKey(), entry.getValue());
			req.setAttribute(mapping.getName(), sForm);
		}

		if (getPaging(req) == null) {
			req.setAttribute("pagingInformation", new PagingInformation(0));
		}

		/* Realiza buscas */
		if (getForm(form).isBuscar())
			lista = getForm(form).customSearch(req);
		else
			lista = getCustomList();

		if (lista == null)
			lista = dao.findAll(getUnidadeGestora(req), getNivelEnsino(req), getPaging(req));
		req.setAttribute(ConstantesCadastro.ATRIBUTO_LISTAGEM, lista);

		return mapping.findForward(LISTAR);
	}

	@Override
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		ActionForward ret = super.cancel(mapping, form, req, res);
		if (ArqListaComando.ALTERAR.equals(getUltimoComando(req)) ||
				ArqListaComando.REMOVER.equals(getUltimoComando(req)))
			return list(mapping, form, req, res);
		return ret;
	}

}
