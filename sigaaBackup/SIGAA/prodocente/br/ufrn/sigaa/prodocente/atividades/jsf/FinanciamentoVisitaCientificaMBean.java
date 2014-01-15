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

import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.FinanciamentoVisitaCientifica;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;
import br.ufrn.sigaa.prodocente.producao.dominio.VisitaCientifica;

/**
 * Gerado pelo CrudBuilder
 */
public class FinanciamentoVisitaCientificaMBean	extends AbstractControllerAtividades<FinanciamentoVisitaCientifica> {
	public FinanciamentoVisitaCientificaMBean() {
		obj = new FinanciamentoVisitaCientifica();
		obj.setVisitaCientifica(new VisitaCientifica());
		obj.setServidor(new Servidor());
	}

	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(FinanciamentoVisitaCientifica.class, "id", "descricao");
	}
	
	@Override
	public String getUrlRedirecRemover()
	{
		return "/sigaa/prodocente/atividades/FinanciamentoVisitaCientifica/lista.jsf";
	}

}
