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
import br.ufrn.sigaa.prodocente.producao.dominio.TipoComissaoColegiado;

/**
 * Gerado pelo CrudBuilder
 */
public class TipoComissaoColegiadoMBean
		extends
		AbstractControllerCadastro<br.ufrn.sigaa.prodocente.producao.dominio.TipoComissaoColegiado> {
	public TipoComissaoColegiadoMBean() {
		obj = new TipoComissaoColegiado();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoComissaoColegiado.class, "id", "descricao");
	}

	@Override
	protected void afterCadastrar() {
		obj = new TipoComissaoColegiado();
	}
}
