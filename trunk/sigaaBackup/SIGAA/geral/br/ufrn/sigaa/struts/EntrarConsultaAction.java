/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '21/05/2007'
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
 * Action usada para verificar se o usuário tem acesso ao sistema de consulta de alunos
 * e servidores.
 * 
 * @author leonardo
 *
 */
public class EntrarConsultaAction extends
		SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		checkRole(SigaaPapeis.CONSULTADOR_ACADEMICO, request);
		setSubSistemaAtual(request, SigaaSubsistemas.CONSULTA);
		return mapping.findForward("sucesso");

	}
}
