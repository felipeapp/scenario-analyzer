/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '08/03/2007'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;
import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.sigaa.prodocente.atividades.dominio.TipoOrientacaoDocente;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Gerado pelo CrudBuilder
 */
public class TipoOrientacaoDocenteMBean
		extends
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.TipoOrientacaoDocente> {
	public TipoOrientacaoDocenteMBean() {
		obj = new TipoOrientacaoDocente();
		setBuscaServidor(true);
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoOrientacaoDocente.class, "id", "descricao");
	}
	@Override
	protected void afterCadastrar() {
		obj = new TipoOrientacaoDocente();
	}


}