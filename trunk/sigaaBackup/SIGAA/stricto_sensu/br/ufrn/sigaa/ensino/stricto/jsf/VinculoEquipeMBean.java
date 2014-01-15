/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.ensino.stricto.dominio.VinculoEquipe;

@Component("vinculoEquipe") @Scope("request")
public class VinculoEquipeMBean extends AbstractControllerCadastro<VinculoEquipe> {

	public VinculoEquipeMBean(){
		obj = new VinculoEquipe();
	}

	@Override
	public Collection<SelectItem> getAllCombo(){
		return getAll(VinculoEquipe.class, "id", "denominacao");
	}
}
