package br.ufrn.sigaa.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;

public class PortalRelatoriosAction extends SigaaAbstractAction {
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		checkRole(new int[] { SigaaPapeis.PORTAL_RELATORIOS }, request);
		setSubSistemaAtual(request, SigaaSubsistemas.PORTAL_RELATORIOS);
		
		clearSessionWeb(request);
		
		return mapping.findForward("sucesso");
	}
}
