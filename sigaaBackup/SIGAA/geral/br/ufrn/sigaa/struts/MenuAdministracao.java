/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '13/09/2006'
 *
 */
package br.ufrn.sigaa.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;

/**
 * Action de Entrada no Menu de Administra��o
 * Action respons�vel por redirecionar para o Portal do Docente
 *
 * @author Gleydson
 *
 */
public class MenuAdministracao extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		setSubSistemaAtual(request, SigaaSubsistemas.ADMINISTRACAO);
		return mapping.findForward("sucesso");
	}

}
