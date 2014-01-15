/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 06/10/2006
 *
 */
package br.ufrn.sigaa.arq.struts;

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

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.RequestUtils;

/**
 * Action comum para todas as operações de cadastro do Sigaa
 *
 * @author David Pereira
 *
 */
public class AbstractCrudAction extends SigaaAbstractAction {

	/** Forwards Default */
	protected static final String FORM = "form";

	protected static final String VIEW = "view";

	protected static final String LISTAR = "listar";

	/**
	 * Action para persistir os dados vindos de um formulário no banco de dados.
	 * Se o id for 0, a ação é de cadastrar, se for diferente de 0 a ação é
	 * atualizar. Em caso de criação, o usuário é redirecionado para o form, em
	 * caso de atualização o usuário é redirecionado para a listagem.
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
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
				mov.setCodMovimento(ArqListaComando.CADASTRAR);
				try {
					execute(mov, req);
				} catch (NegocioException e) {
					req.setAttribute(mapping.getName(), form);
					return edit(mapping, form, req, res);
				}
				addMessage(req, "Operação realizada com sucesso!",
						TipoMensagemUFRN.INFORMATION);
				return edit(mapping, form, req, res);
			} else {
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				try {
					execute(mov, req);
				} catch (NegocioException e) {
					req.setAttribute(mapping.getName(), form);
					return edit(mapping, form, req, res);
				}
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

	/**
	 * Action para exibir o formulário em caso de cadastro ou atualização.
	 * Atualização ocorre quando for passado por request um parâmetro id != 0.
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
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
				prepareMovimento(ArqListaComando.CADASTRAR, req);
				cForm.setConfirmButton("Cadastrar");
			} else {
				prepareMovimento(ArqListaComando.ALTERAR, req);
				cForm.setConfirmButton("Alterar");
			}
		}
		 else
			cForm.setConfirmButton("Remover");
		return mapping.findForward(FORM);
	}

	@SuppressWarnings("unchecked")
	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		GenericDAO dao = getGenericDAO(req);

		getForm(form).checkRole(req);

		int id = RequestUtils.getIntParameter(req, "id");

		Object obj = dao.findByPrimaryKey(id, getForm(form).getCommandClass());
		req.setAttribute(ConstantesCadastro.ATRIBUTO_VISUALIZACAO, obj);

		return mapping.findForward(VIEW);
	}

	/**
	 * Action para remoção. Se não existir um parâmetro "confirm" em request,
	 * vai para edit com os campos desabilitados (para visualização). Se houver,
	 * apaga o objeto.
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward remove(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		SigaaForm sigaaForm = (SigaaForm) form;

		getForm(form).checkRole(req);

		if (getForm(form).isConfirm()) {
			GenericDAO dao = getGenericDAO(req);
			PersistDB obj = null;
			try {
				obj = dao.findByPrimaryKey(sigaaForm
						.getObjAsPersistDB().getId(), sigaaForm
						.getCommandClass());
			} finally {
				dao.close();
			}

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			sigaaForm.setConfirmButton("Remover");
			if(req.getParameter("desativar") == null && req.getAttribute("desativar") == null)
				mov.setCodMovimento(ArqListaComando.REMOVER);
			else
				mov.setCodMovimento(ArqListaComando.DESATIVAR);

			try {
				execute(mov, req);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens().getMensagens(), req);
				req.setAttribute(mapping.getName(), form);
				return edit(mapping, form, req, res);
			}

			addMessage(req, "Remoção realizada com sucesso!",
					TipoMensagemUFRN.INFORMATION);

			// Limpar formulário, caso ele esteja em sessão
			req.getSession(false).removeAttribute(mapping.getName());

			return list(mapping, form, req, res);
		} else {
			req.setAttribute("remove", true);
			if(req.getParameter("desativar") == null)
				prepareMovimento(ArqListaComando.REMOVER, req);
			else
				prepareMovimento(ArqListaComando.DESATIVAR, req);
			return edit(mapping, form, req, res);
		}

	}

	/**
	 * Action para listar os objetos
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward list(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

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
			try {
				for (Entry<String, String> entry : entrySet)
					BeanUtils.setProperty(sForm, entry.getKey(), entry.getValue());
			} catch (Exception e) {
				System.out.println(e);
			}

			if (searchMap.get("buscar") != null) {
				getForm(form).setBuscar( Boolean.valueOf(searchMap.get("buscar") ));
			}

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


		if (lista == null && req.getAttribute("custom") == null)
			lista = dao
					.findAll(getForm(form).getCommandClass(), getPaging(req));

		req.setAttribute(ConstantesCadastro.ATRIBUTO_LISTAGEM, lista);

		return mapping.findForward(LISTAR);
	}

	/**
	 * Método utilizado para realizar um list customizado.
	 *
	 * @return
	 */
	public Collection getCustomList() {
		return null;
	}

	/**
	 * Action para cancelar um caso de uso de cadastro
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		clearSession(req);
		removeSession(mapping.getName(), req);
		String dispatch = req.getParameter("currentDispatch");

		if (("edit".equals(dispatch) && RequestUtils.getIntParameter(req, "id") != 0)
				|| "remove".equals(dispatch)) {
			return list(mapping, form, req, res);
		} else {
			return getMappingSubSistema(req, mapping);
		}

	}

	/**
	 * Adiciona os atributos de um mapa em request
	 *
	 * @param req
	 * @param mapa
	 */
	protected void populateRequest(HttpServletRequest req,
			Map<String, Object> mapa) {
		if (mapa != null && !mapa.isEmpty()) {
			for (String attr : mapa.keySet()) {
				req.setAttribute(attr, mapa.get(attr));
			}
		}
	}

	/**
	 * Retorna um CrudForm
	 *
	 * @param form
	 * @return
	 */
	public SigaaForm getForm(ActionForm form) {
		return (SigaaForm) form;
	}

	public void saveMessages(HttpServletRequest req, ListaMensagens msgs) {
		req.setAttribute("mensagensAviso", msgs);
	}

	public void addMessage(HttpServletRequest req, String message,
			TipoMensagemUFRN tipo) {
		ListaMensagens lista = (ListaMensagens) req
				.getAttribute("mensagensAviso");
		if (lista == null)
			lista = new ListaMensagens();
		lista.getMensagens().add(MensagemAviso.valueOf(message, tipo));
		req.setAttribute("mensagensAviso", lista);
	}

	public Integer getParameterInt(HttpServletRequest req, String param,
			int defaultValue) {
		String p = req.getParameter(param);

		try {
			return Integer.valueOf(p);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public Integer getParameterInt(HttpServletRequest req, String param) {
		return getParameterInt(req, param, 0);
	}

}