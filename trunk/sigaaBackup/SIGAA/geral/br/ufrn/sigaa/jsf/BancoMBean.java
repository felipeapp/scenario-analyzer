/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/09/2007'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pessoa.dominio.Banco;

/**
 * @author Ricardo Wendell
 *
 */
public class BancoMBean extends SigaaAbstractController<Banco> {

    public BancoMBean() {
	obj = new Banco();
    }

    public Collection<SelectItem> getAllCombo() {
    	return getAll(Banco.class, "id", "denominacao");
    }

}
