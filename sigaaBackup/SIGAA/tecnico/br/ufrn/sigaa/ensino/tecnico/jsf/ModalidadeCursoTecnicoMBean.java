package br.ufrn.sigaa.ensino.tecnico.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.tecnico.dominio.ModalidadeCursoTecnico;

@Component @Scope("request")
public class ModalidadeCursoTecnicoMBean extends SigaaAbstractController<ModalidadeCursoTecnico> {

	public ModalidadeCursoTecnicoMBean() {
		obj = new ModalidadeCursoTecnico();
	}
	
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return toSelectItems(getAll(), "id", "descricao");
	}
	
}
