/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 04/10/2010
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
 * Action que verifica se o usu�rio possui acesso a Conv�nios de Est�gio
 * @author arlindo
 *
 */
public class EntrarConveniosEstagio extends SigaaAbstractAction {
	
	/**
	 * Executa o processamento de verifica��o de permiss�o no portal
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {

		setSubSistemaAtual(req, SigaaSubsistemas.CONVENIOS_ESTAGIO);

		checkRole(new int[] { SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN}, req);

		return mapping.findForward("sucesso");
	}	

}
