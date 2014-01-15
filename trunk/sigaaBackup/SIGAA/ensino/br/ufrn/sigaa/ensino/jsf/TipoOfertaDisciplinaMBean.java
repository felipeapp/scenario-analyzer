/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jun 25, 2008
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.TipoOfertaDisciplina;

/**
 * MBean usado para a geração de Comboboxes de objetos da classe TipoOfertaDisciplina
 * 
 * @author Victor Hugo
 */
@Component("tipoOfertaDisciplina") @Scope("request")
public class TipoOfertaDisciplinaMBean extends SigaaAbstractController<TipoOfertaDisciplina> {

	public TipoOfertaDisciplinaMBean() {
		obj = new TipoOfertaDisciplina();
	}

	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoOfertaDisciplina.class, "id", "denominacao");
	}

}
