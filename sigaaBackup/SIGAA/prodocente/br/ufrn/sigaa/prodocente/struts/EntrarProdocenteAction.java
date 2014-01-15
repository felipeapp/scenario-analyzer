/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '15/01/2007'
 *
 */
package br.ufrn.sigaa.prodocente.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;

/**
 * Action usada para verificar se o usuário tem acesso ao prodocente
 *
 * @author Gleydson
 *
 */
public class EntrarProdocenteAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		checkRole(SigaaPapeis.GRUPO_PRODOCENTE, request);
		setSubSistemaAtual(request, SigaaSubsistemas.PROD_INTELECTUAL);
		return mapping.findForward("sucesso");

	}

}
