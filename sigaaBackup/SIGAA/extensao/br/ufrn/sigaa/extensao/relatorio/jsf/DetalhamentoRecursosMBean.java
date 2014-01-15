/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/12/2006
 *
 */
package br.ufrn.sigaa.extensao.relatorio.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.extensao.relatorio.dominio.DetalhamentoRecursos;

/*******************************************************************************
 * Gerado pelo CrudBuilder
 ******************************************************************************/
@Component("detalhamentoRecursos")
@Scope("request")
public class DetalhamentoRecursosMBean extends
AbstractControllerCadastro<br.ufrn.sigaa.extensao.relatorio.dominio.DetalhamentoRecursos> {
	public DetalhamentoRecursosMBean() {
		obj = new DetalhamentoRecursos();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(DetalhamentoRecursos.class, "id", "descricao");
	}
}
