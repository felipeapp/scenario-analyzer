/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Jun 25, 2008
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Esta entidade define o tipo da organiza��o administrativa de um curso STRICTO
 * EX: Centro, Inter-Unidades , Inter-institucional (integrado entre institui��es)
 * @author Victor Hugo
 */
@Entity
@Table(name = "organizacao_administrativa", schema = "stricto_sensu", uniqueConstraints = {})
public class OrganizacaoAdministrativa implements PersistDB {

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_organizacao_administrativa")
	private int id;

	/** a denomina��o da organiza��o */
	private String denominacao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}



}
