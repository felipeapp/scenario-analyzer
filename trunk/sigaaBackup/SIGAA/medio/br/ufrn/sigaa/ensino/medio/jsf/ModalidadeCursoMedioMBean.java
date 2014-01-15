/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/05/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.medio.dominio.ModalidadeCursoMedio;

/**
 * Cadastro de modalidade de curso do ensino técnico.
 *
 * @author Rafael Gomes
 */
@Component("modalidadeCursoMedio") @Scope("request")
public class ModalidadeCursoMedioMBean extends SigaaAbstractController<ModalidadeCursoMedio> {

	/**
	 * Construtor padrão
	 */
	public ModalidadeCursoMedioMBean() {
		obj = new ModalidadeCursoMedio();
	}
	
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return toSelectItems(getAll(), "id", "descricao");
	}
	
	@Override
	public String getDirBase() {
		return "/medio/modalidadeCursoMedio";
	}
	
}
