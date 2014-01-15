/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '04/05/2007'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pessoa.dominio.TipoDocenteExterno;

/** Controller do TipoDocenteExterno
 * 
 * @author Victor Hugo
 *
 */
public class TipoDocenteExternoMBean extends SigaaAbstractController<TipoDocenteExterno> {

	/** Construtor padrão.
	 * 
	 */
	public TipoDocenteExternoMBean() {
		obj = new TipoDocenteExterno();
	}

	/** Cria uma coleção de SelectItem de TipoDocenteExterno válidos (exclui o tipo -1, NÃO INFORMADO).
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	public Collection<SelectItem> getAllCombo() {
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		for (SelectItem item : getAll(TipoDocenteExterno.class, "id", "denominacao")) {
			if (!item.getValue().equals("-1"))
				lista.add(item);
		}
		return lista;
	}
	
}
