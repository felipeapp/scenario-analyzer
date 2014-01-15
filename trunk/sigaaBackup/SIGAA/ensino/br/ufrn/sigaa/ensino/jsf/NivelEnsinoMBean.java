/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 10/10/2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.web.jsf.AbstractController;

/**
 * MBean auxiliar para combos de n�veis de ensino
 *
 * @author Andre Dantas
 *
 */
@Component("nivelEnsino")
@Scope("request")
public class NivelEnsinoMBean extends AbstractController {

	/**
	 * Retorna uma lista de n�veis Stricto Sensu no formato SelectItem
	 * <br/><br/>
	 * M�todo chamado por diversas JSPs.
	 * @return
	 */
	public Collection<SelectItem> getStrictoCombo() {
		Collection<SelectItem> combo = new ArrayList<SelectItem>(0);
		for (Character n : NivelEnsino.getNiveisStricto()) {
			if (n != NivelEnsino.STRICTO)
			combo.add(new SelectItem(n+"", NivelEnsino.getDescricao(n)));
		}
		return combo;
	}
	
	/**
	 * Retorna uma lista com todos os n�veis no formato SelectItem
	 * <br/><br/>
	 * M�todo chamado por diversas JSPs.
	 * @return
	 */
	public Collection<SelectItem> getAllCombo() {
		return Arrays.asList(NivelEnsino.getNiveisCombo()) ;
	}	
	
	/**
	 * Retorna uma lista com todos os n�veis Superiores (Stricto, Doutorado, Mestrado, Lato), no formato SelectItem
	 * <br/><br/>
	 * M�todo chamado por diversas JSPs.
	 * @return
	 */
	public Collection<SelectItem> getNiveisSuperiorCombo(){
		Collection<SelectItem> combo = new ArrayList<SelectItem>();
		combo.addAll(getStrictoCombo());
		combo.add(new SelectItem(NivelEnsino.LATO, NivelEnsino.getDescricao(NivelEnsino.LATO)));
		return combo;
	}

}
