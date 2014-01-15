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

import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.Colegiado;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoComissaoColegiado;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoMembroColegiado;

/**
 * Gerado pelo CrudBuilder
 */
public class ColegiadoMBean
		extends
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.Colegiado> {
	public ColegiadoMBean() {
		obj = new Colegiado();
		obj.setDepartamento(new Unidade());
		obj.setServidor(new Servidor());
		obj.setTipoComissaoColegiado(new TipoComissaoColegiado());
		obj.setTipoMembroColegiado(new TipoMembroColegiado());
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(Colegiado.class, "id", "descricao");
	}
	@Override
	protected void afterCadastrar() {
		obj = new Colegiado();
	}
}
