/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '26/01/2007'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.ensino.dominio.TipoSistemaCurricular;

public class TipoSistemaCurricularMBean extends AbstractControllerCadastro<TipoSistemaCurricular> {
	public TipoSistemaCurricularMBean() {
		obj = new TipoSistemaCurricular();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoSistemaCurricular.class, "id", "descricao");
	}
}
