/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Dec 4, 2007
 *
 */
package br.ufrn.sigaa.questionario.jsf;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.questionario.dominio.TipoQuestionario;

/**
 *
 * @author Victor Hugo
 *
 */
public class TipoQuestionarioMBean extends SigaaAbstractController<TipoQuestionario> {

	public TipoQuestionarioMBean() {
		obj = new TipoQuestionario();
	}

}
