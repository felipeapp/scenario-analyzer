/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 19/10/2006
 *
 */
package br.ufrn.sigaa.ensino.latosensu.struts;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.arq.struts.ConstantesCadastro;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.ParceriaLato;

/**
 * Ações do CRUD de ParceriaLato
 *
 * @author Leonardo
 *
 */
public class ParceriaLatoAction extends AbstractCrudAction {


	@Override
	public ActionForward persist(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		getForm(form).checkRole(req);

		getForm(form).validate(req);

		if (!flushErros(req)) {

			PersistDB obj = getForm(form).getObjAsPersistDB();
			MovimentoCadastro mov = new MovimentoCadastro();
			getForm(form).beforePersist(req);
			mov.setObjMovimentado(obj);

			getForm(form).clear();
			removeSession(mapping.getName(), req);

			if (obj.getId() == 0) {
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_PARCERIA_LATO);
				execute(mov, req);
				addMessage(req, "Operação realizada com sucesso!",
						TipoMensagemUFRN.INFORMATION);
				return edit(mapping, form, req, res);
			} else {
				mov.setCodMovimento(SigaaListaComando.ALTERAR_PARCERIA_LATO);
				execute(mov, req);
				req.setAttribute("voltando", true);
				addMessage(req, "Operação realizada com sucesso!",
						TipoMensagemUFRN.INFORMATION);
				return list(mapping, form, req, res);
			}

		} else {
			req.setAttribute(mapping.getName(), form);
			return edit(mapping, form, req, res);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		SigaaForm cForm = (SigaaForm) form;

		getForm(form).checkRole(req);

		/* Popular Form */
		getForm(form).referenceData(req);
		populateRequest(req, getForm(form).getReferenceData());

		if (!flushOnlyErros(req)
				&& RequestUtils.getIntParameter(req, "id") == 0) {
			cForm.clear();
			cForm.setDefaultProps();
		}
		/* Popular objeto em caso de atualização */
		if (!getForm(form).hasError(req)
				&& RequestUtils.getIntParameter(req, "id") != 0)
			cForm.setObj(getForm(form).formBackingObject(req));
		req.setAttribute(mapping.getName(), cForm);

		if (req.getAttribute("remove") == null) {
			if (cForm.getObjAsPersistDB().getId() == 0) {
				prepareMovimento(SigaaListaComando.CADASTRAR_PARCERIA_LATO, req);
			} else {
				prepareMovimento(SigaaListaComando.ALTERAR_PARCERIA_LATO, req);
			}
		}
		return mapping.findForward(FORM);
	}

	/* (non-Javadoc)
	 * @see br.ufrn.sigaa.arq.struts.AbstractCrudAction#list(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		GenericDAO dao = getGenericDAO(req);

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

		Usuario user = (Usuario) getUsuarioLogado(req);
		/* Realiza buscas */
		if (getForm(form).isBuscar())
			lista = getForm(form).customSearch(req);
		else
			if( user.isCoordenadorLato() ){
				lista = new HashSet<ParceriaLato>();
				for( CursoLato c: user.getCursosLato() ){
					lista.addAll( dao.findByExactField(ParceriaLato.class, "cursoLato.id", c.getId()));
				}
			}

		if (lista == null)
			lista = dao
					.findAll(getForm(form).getCommandClass(), getPaging(req));
		req.setAttribute(ConstantesCadastro.ATRIBUTO_LISTAGEM, lista);

		return mapping.findForward(LISTAR);
	}

}
