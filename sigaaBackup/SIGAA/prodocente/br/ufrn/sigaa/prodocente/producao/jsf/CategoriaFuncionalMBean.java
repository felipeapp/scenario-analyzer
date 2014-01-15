/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '12/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.prodocente.producao.dominio.CategoriaFuncional;

/**
 * MBean de categoria funcional
 * @author Andre M Dantas
 *
 */
public class CategoriaFuncionalMBean 	extends
		AbstractControllerCadastro<br.ufrn.sigaa.prodocente.producao.dominio.CategoriaFuncional> {
	public CategoriaFuncionalMBean() {
		obj = new CategoriaFuncional();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(CategoriaFuncional.class, "id", "descricao");
	}

	@Override
	protected void afterCadastrar() {
		obj = new CategoriaFuncional();
	}

}
