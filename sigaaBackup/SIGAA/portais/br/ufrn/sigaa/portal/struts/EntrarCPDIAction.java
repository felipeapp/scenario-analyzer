/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 22/11/2007 
 *
 */
package br.ufrn.sigaa.portal.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.web.struts.AbstractAction;

/**
 * Classe utilizada para redirect para entrar no portal da CPDI (Comissão Permanente de Desenvolvimento Institucional)
 *
 * @author Gleydson
 *
 */
public class EntrarCPDIAction extends AbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		checkRole(SigaaPapeis.MEMBRO_CPDI, request);

		request.setAttribute("hideSubsistema", Boolean.TRUE);
		setSubSistemaAtual(request, SigaaSubsistemas.PORTAL_CPDI);
		return mapping.findForward("sucesso");

	}
}