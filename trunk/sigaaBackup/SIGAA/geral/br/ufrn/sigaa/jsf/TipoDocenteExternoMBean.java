/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
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

	/** Construtor padr�o.
	 * 
	 */
	public TipoDocenteExternoMBean() {
		obj = new TipoDocenteExterno();
	}

	/** Cria uma cole��o de SelectItem de TipoDocenteExterno v�lidos (exclui o tipo -1, N�O INFORMADO).
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
