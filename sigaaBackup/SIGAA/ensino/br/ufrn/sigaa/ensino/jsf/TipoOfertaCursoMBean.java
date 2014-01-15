/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on May 24, 2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.TipoOfertaCurso;

/**
 * MBean usado para a gera��o de Comboboxes de objetos da classe TipoOfertaCurso
 * 
 * @author Victor Hugo
 *
 */
@Component("tipoOfertaCurso")
@Scope("request")
public class TipoOfertaCursoMBean extends SigaaAbstractController<TipoOfertaCurso> {
	
	public TipoOfertaCursoMBean() {
		obj = new TipoOfertaCurso();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoOfertaCurso.class, "id", "descricao");
	}
	
	
}
