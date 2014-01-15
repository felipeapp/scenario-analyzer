package br.ufrn.sigaa.extensao.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.ProgramaEstrategicoExtensao;

@Component @Scope("request") 
public class ProgramaEstrategicoMBean extends SigaaAbstractController<ProgramaEstrategicoExtensao> {

	public ProgramaEstrategicoMBean() {
		obj = new ProgramaEstrategicoExtensao();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(ProgramaEstrategicoExtensao.class, "id", "descricao");
	}

}
