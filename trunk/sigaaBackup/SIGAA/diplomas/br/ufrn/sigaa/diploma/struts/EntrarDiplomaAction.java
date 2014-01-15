/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 23/08/2010
 *
 */
package br.ufrn.sigaa.diploma.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;

public class EntrarDiplomaAction  extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {

		setSubSistemaAtual(req, SigaaSubsistemas.REGISTRO_DIPLOMAS);

		checkRole(new int[] { SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO,
				SigaaPapeis.GESTOR_DIPLOMAS_LATO,
				SigaaPapeis.GESTOR_DIPLOMAS_STRICTO}, req);

		return mapping.findForward("sucesso");
	}
}
