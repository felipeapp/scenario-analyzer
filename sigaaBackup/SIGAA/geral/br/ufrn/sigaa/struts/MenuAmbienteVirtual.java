/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '30/09/2009'
 *
 */
package br.ufrn.sigaa.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;

/**
 * Action struts que redireciona para o menu principal do
 * módulo Ambientes Virtuais
 * 
 * @author agostinho campos
 *
 */
public class MenuAmbienteVirtual extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		setSubSistemaAtual(request, SigaaSubsistemas.AMBIENTES_VIRTUAIS);
		request.getSession().removeAttribute("nivel");
		if (isUserInRole(request, SigaaPapeis.GESTOR_AMBIENTES_VIRTUAIS)) {
			return mapping.findForward("sucessoGestor");
		}
		return mapping.findForward("sucesso");
	}
	
}