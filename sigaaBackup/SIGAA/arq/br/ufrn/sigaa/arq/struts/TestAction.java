/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 06/12/2006
 *
 */
package br.ufrn.sigaa.arq.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.sigaa.ensino.timer.CalculosDiscenteGraduacaoTimer;

/**
 * Action usada para testar algoritmos dentro do ambiente de execução do
 * servidor
 * 
 * @author Gleydson
 * 
 */
public class TestAction extends AbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		CalculosDiscenteGraduacaoTimer t = new CalculosDiscenteGraduacaoTimer();
		t.run();
		
		
		
		return null;
	}

}