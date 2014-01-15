/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Feb 13, 2008
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.ensino.stricto.dominio.NivelEquipe;

/**
 *
 * @author Victor Hugo
 */
@Component("nivelEquipe") @Scope("request")
public class NivelEquipeMBean extends AbstractControllerCadastro<NivelEquipe> {

	public NivelEquipeMBean() {
		obj = new NivelEquipe();
	}

	 @Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(NivelEquipe.class, "id", "denominacao");
	}

}
