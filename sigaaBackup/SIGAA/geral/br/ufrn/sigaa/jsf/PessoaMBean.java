/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

public class PessoaMBean extends
		SigaaAbstractController<br.ufrn.sigaa.pessoa.dominio.Pessoa> {
	public PessoaMBean() {
		obj = new Pessoa();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(Pessoa.class, "Id", "Denominacao");
	}
}
