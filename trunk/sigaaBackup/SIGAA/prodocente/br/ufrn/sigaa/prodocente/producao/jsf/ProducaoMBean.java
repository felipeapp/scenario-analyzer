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

import br.ufrn.sigaa.prodocente.producao.dominio.Producao;

/**
 * Gerado pelo CrudBuilder
 */
public class ProducaoMBean
		extends
		AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.Producao> {
	public ProducaoMBean() {
		obj = new Producao();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(Producao.class, "id", "descricao");
	}
	
}
