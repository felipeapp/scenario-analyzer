/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 26/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;

/**
 * Controller responsável pelo cadastro/alteração de um Programa de Rede de
 * Ensino com cursos ofertados em outras universidades.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Component @Scope("request")
public class ProgramaRedeMBean extends EnsinoRedeAbstractController<ProgramaRede> {

	private Collection<SelectItem> allCombo;

	public ProgramaRedeMBean() {
		obj = new ProgramaRede();
	}
	
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		if (allCombo == null) {
			allCombo = toSelectItems(getAll(), "id", "descricao");
		}
		return allCombo;
	}
}
