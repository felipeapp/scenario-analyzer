/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/01/2007'
 *
 */
package br.ufrn.sigaa.caixa_postal.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.sigaa.caixa_postal.dominio.MensagensHelper;

/**
 * 
 * Classe utilizada para ler mensagens
 * 
 * @author Gleydson Lima
 * 
 */
public class CaixaPostalAction extends AbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		MensagensHelper.preencheCaixaEntrada(req, getUsuarioLogado(req));
		return mapping.findForward("caixa_postal");

	}

}
