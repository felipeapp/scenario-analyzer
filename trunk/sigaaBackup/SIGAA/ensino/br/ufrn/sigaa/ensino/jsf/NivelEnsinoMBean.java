/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
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
 * MBean auxiliar para combos de níveis de ensino
 *
 * @author Andre Dantas
 *
 */
@Component("nivelEnsino")
@Scope("request")
public class NivelEnsinoMBean extends AbstractController {

	/**
	 * Retorna uma lista de níveis Stricto Sensu no formato SelectItem
	 * <br/><br/>
	 * Método chamado por diversas JSPs.
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
	 * Retorna uma lista com todos os níveis no formato SelectItem
	 * <br/><br/>
	 * Método chamado por diversas JSPs.
	 * @return
	 */
	public Collection<SelectItem> getAllCombo() {
		return Arrays.asList(NivelEnsino.getNiveisCombo()) ;
	}	
	
	/**
	 * Retorna uma lista com todos os níveis Superiores (Stricto, Doutorado, Mestrado, Lato), no formato SelectItem
	 * <br/><br/>
	 * Método chamado por diversas JSPs.
	 * @return
	 */
	public Collection<SelectItem> getNiveisSuperiorCombo(){
		Collection<SelectItem> combo = new ArrayList<SelectItem>();
		combo.addAll(getStrictoCombo());
		combo.add(new SelectItem(NivelEnsino.LATO, NivelEnsino.getDescricao(NivelEnsino.LATO)));
		return combo;
	}

}
