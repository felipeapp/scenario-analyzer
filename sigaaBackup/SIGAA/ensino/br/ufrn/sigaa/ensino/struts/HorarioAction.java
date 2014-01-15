/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ensino.struts;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.arq.struts.ConstantesCadastro;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.form.HorarioForm;
import br.ufrn.sigaa.ensino.negocio.HorarioValidator;

/**
 * @author eric
 *
 * Classe deprecated deve ser usado a classe HorarioMBean.
 */
@Deprecated
public class HorarioAction extends AbstractCrudAction {

	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		SigaaForm cForm = (SigaaForm) form;

		getForm(form).checkRole(req);

		/* Popular Form */
		getForm(form).referenceData(req);
		populateRequest(req, getForm(form).getReferenceData());

		if (!flushOnlyErros(req)
				&& RequestUtils.getIntParameter(req, "id") == 0) {
			cForm.setDefaultProps();
		}
		/* Popular objeto em caso de atualização */
		if (!getForm(form).hasError(req) && RequestUtils.getIntParameter(req, "id") != 0)
			cForm.setObj(getForm(form).formBackingObject(req));
		req.setAttribute(mapping.getName(), cForm);

		if (req.getAttribute("remove") == null) {
			if (cForm.getObjAsPersistDB().getId() == 0) {
				prepareMovimento(SigaaListaComando.CADASTRAR_HORARIO, req);
			} else {
				prepareMovimento(SigaaListaComando.ALTERAR_HORARIO, req);
			}
		}
		return mapping.findForward(FORM);
	}

	@Override
	public ActionForward persist(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		getForm(form).checkRole(req);

		getForm(form).validate(req);

		HorarioForm horarioForm = (HorarioForm) form;

		Horario obj = (Horario) getForm(
				form).getObjAsPersistDB();

		HorarioValidator.validarHorario(obj, horarioForm, newListaMensagens(req));


		if (!flushErros(req)) {


			if (obj.getId() == 0) {
				obj.setCodMovimento(SigaaListaComando.CADASTRAR_HORARIO);
				if( getNivelEnsino(req) != NivelEnsino.LATO )
					obj.setUnidade(new Unidade(getUnidadeGestora(req)));
				else {
					obj.setUnidade(null);
				}
				obj.setNivel(getNivelEnsino(req));

				try {
					execute(obj, req);
				} catch (NegocioException e) {
					addMensagens(e.getListaMensagens().getMensagens(), req);
					return mapping.findForward("form");
				}
				removeSession(mapping.getName(), req);
				addMessage(req, "Operação realizada com sucesso!",
						TipoMensagemUFRN.INFORMATION);
				return edit(mapping, form, req, res);
			} else {
				obj.setCodMovimento(SigaaListaComando.ALTERAR_HORARIO);
				try {
					execute(obj, req);
				} catch (NegocioException e) {
					addMensagens(e.getListaMensagens().getMensagens(), req);
					return mapping.findForward("form");
				}
				addMessage(req, "Operação realizada com sucesso!",
						TipoMensagemUFRN.INFORMATION);
				req.setAttribute("voltando", true);
				removeSession(mapping.getName(), req);
				return list(mapping, form, req, res);
			}

		} else {
			req.setAttribute(mapping.getName(), form);
			return edit(mapping, form, req, res);
		}
	}


	@Override
	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		getForm(form).checkRole(req);

		Collection lista = getForm(form).customSearch(req);

		req.setAttribute(ConstantesCadastro.ATRIBUTO_LISTAGEM, lista);

		return mapping.findForward(LISTAR);
	}


	@Override
	public ActionForward cancelar(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionForward padrao = super.cancelar(mapping, form, request, response);
		if (getUltimoComando(request).equals(SigaaListaComando.ALTERAR_HORARIO) ||
				getUltimoComando(request).equals(ArqListaComando.REMOVER))
			return list(mapping, form, request, response);
		return padrao;
	}

}
