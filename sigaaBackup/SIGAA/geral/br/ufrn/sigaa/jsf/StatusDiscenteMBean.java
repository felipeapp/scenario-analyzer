/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jan 30, 2008
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/** Controller responsável por operações sobre StatusDiscente.
 *
 * @author Victor Hugo
 *
 */
public class StatusDiscenteMBean extends SigaaAbstractController<StatusDiscente> {

	/** Construtor padrão. */
	public StatusDiscenteMBean() {
		obj = new StatusDiscente();
	}

	/** Retorna uma coleção com todos possíveis status.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	public Collection<SelectItem> getAllCombo() {
		return toSelectItems( StatusDiscente.getStatusTodos() , "id", "descricao");
	}
	
	/** Retorna uma coleção com todos possíveis status para discentes de graduação.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	public Collection<SelectItem> getAllTecnicoCombo() {
		return toSelectItems( StatusDiscente.getStatusTecnico() , "id", "descricao");
	}
	
	/** Retorna uma coleção com todos possíveis status para discentes de graduação.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	public Collection<SelectItem> getAllGraduacaoCombo() {
		return toSelectItems( StatusDiscente.getStatusGraduacao() , "id", "descricao");
	}
	
	/** Retorna uma coleção com todos possíveis status para discentes de pós graduação.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	public Collection<SelectItem> getAllStrictoCombo() {
		return toSelectItems( StatusDiscente.getStatusStricto() , "id", "descricao");
	}
	
	/**
	 * Retorna uma coleção com todos possíveis status ativos para discentes de lato sensu.
	 * @return
	 */
	public Collection<SelectItem> getAllLatoCombo() {
		return toSelectItems( StatusDiscente.getStatusStricto() , "id", "descricao");
	}

	/**
	 * Retorna uma coleção com todos possíveis status ativos para discentes de ensino à distância.
	 * @return
	 */
	public Collection<SelectItem> getAllAtivosEad() {
		Collection<Integer> ativos = StatusDiscente.getAtivos();
		ativos.add( StatusDiscente.CADASTRADO );
		Collection<StatusDiscente> status = new ArrayList<StatusDiscente>();
		for( int i : ativos ){
			status.add( new StatusDiscente(i,StatusDiscente.getDescricao(i) ) );
		}
		return toSelectItems( status, "id", "descricao");
	}
	
	/** 
	 * Retorna uma coleção com todos possíveis status para discentes de ensino médio.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	public Collection<SelectItem> getAllMedioCombo() {
		return toSelectItems( StatusDiscente.getStatusMedio() , "id", "descricao");
	}
}
