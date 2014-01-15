/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 20/01/2009 
 */
package br.ufrn.sigaa.avaliacao.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;

/**
 * Action para entrar no portal
 * da avaliação institucional.
 * 
 * @author David Pereira
 *
 */
public class PortalAvaliacaoAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(new int[] { SigaaPapeis.COMISSAO_AVALIACAO, SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL }, req);
		setSubSistemaAtual(req, SigaaSubsistemas.PORTAL_AVALIACAO_INSTITUCIONAL);

		return mapping.findForward("sucesso");
	}
	
}
