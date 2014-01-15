/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '15/03/2007'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.sigaa.prodocente.atividades.dominio.TipoRelatorio;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

public class TipoRelatorioMBean extends
AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.TipoRelatorio> {
	public TipoRelatorioMBean(){
		obj = new TipoRelatorio();
		setBuscaServidor(true);
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoRelatorio.class, "id", "descricao");
	}
}
