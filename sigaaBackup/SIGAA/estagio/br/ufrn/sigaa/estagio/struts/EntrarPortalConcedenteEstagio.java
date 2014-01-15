/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 25/10/2010
 *
 */
package br.ufrn.sigaa.estagio.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;

/**
 * Action que verifica se o usuário possui acesso ao Portal do Concedente de Estágio
 *  
 * @author arlindo
 *
 */
public class EntrarPortalConcedenteEstagio extends SigaaAbstractAction  {

	/**
	 * Executa o processamento de verificação de permissão no portal
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {

		setSubSistemaAtual(req, SigaaSubsistemas.PORTAL_CONCEDENTE_ESTAGIO);

		checkRole(new int[] { SigaaPapeis.PORTAL_CONCEDENTE_ESTAGIO}, req);

		return mapping.findForward("sucesso");
	}		
	
}
