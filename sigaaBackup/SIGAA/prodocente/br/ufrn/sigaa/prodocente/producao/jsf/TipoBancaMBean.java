/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoBanca;

/**
 * Gerado pelo CrudBuilder
 */
public class TipoBancaMBean
		extends
		AbstractControllerCadastro<br.ufrn.sigaa.prodocente.producao.dominio.TipoBanca> {
	public TipoBancaMBean() {
		obj = new TipoBanca();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoBanca.class, "id", "descricao");
	}


	@Override
	protected void afterCadastrar() {
		obj = new TipoBanca();
	}
}
