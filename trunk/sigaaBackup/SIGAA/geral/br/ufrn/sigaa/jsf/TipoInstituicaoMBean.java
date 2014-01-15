/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '25/04/2007'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.dominio.TipoInstituicao;

public class TipoInstituicaoMBean extends AbstractControllerCadastro<TipoInstituicao> {

	public TipoInstituicaoMBean() {
		obj = new TipoInstituicao();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoInstituicao.class, "id", "denominacao");
	}
}
