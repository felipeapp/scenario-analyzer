package br.ufrn.sigaa.assistencia.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.dominio.ModalidadeEsportiva;

/**
 * MBean responsável pelo CRUD das Modalidade Esportivas.
 *
 */
@Component @Scope("request")
public class ModalidadeEsportivaMBean extends SigaaAbstractController<ModalidadeEsportiva>{

	public ModalidadeEsportivaMBean() {
		obj = new ModalidadeEsportiva();
	}
	
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return getAll(ModalidadeEsportiva.class, "id", "descricao");
	}
	
}