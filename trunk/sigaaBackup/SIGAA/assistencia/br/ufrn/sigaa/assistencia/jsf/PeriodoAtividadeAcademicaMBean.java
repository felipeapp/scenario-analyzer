package br.ufrn.sigaa.assistencia.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.dominio.PeriodoAtividadeAcademica;

/**
 * MBean respons�vel pelo CRUD do Per�odo de Atividades Acad�micas.
 * @author Victor
 *
 */
@Component @Scope("request")
public class PeriodoAtividadeAcademicaMBean extends SigaaAbstractController<PeriodoAtividadeAcademica> {

	public PeriodoAtividadeAcademicaMBean() {
		clear();
	}

	/** "Limpa" os atributos do objeto. */
	private void clear() {
		obj = new PeriodoAtividadeAcademica();
	}
	
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return getAll(PeriodoAtividadeAcademica.class, "id", "descricao");
	}
	
}