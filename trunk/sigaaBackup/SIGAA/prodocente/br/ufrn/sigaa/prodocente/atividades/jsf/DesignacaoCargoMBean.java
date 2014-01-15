/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.sigaa.prodocente.atividades.dominio.DesignacaoCargo;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Gerado pelo CrudBuilder
 */
public class DesignacaoCargoMBean
		extends
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.DesignacaoCargo> {
	public DesignacaoCargoMBean() {
		obj = new DesignacaoCargo();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(DesignacaoCargo.class, "id", "descricao");
	}
	@Override
	protected void afterCadastrar() {
		obj = new DesignacaoCargo();
	}
}
