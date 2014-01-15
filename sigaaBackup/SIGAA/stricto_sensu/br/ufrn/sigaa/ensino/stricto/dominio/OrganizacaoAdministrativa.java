/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
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
 * Esta entidade define o tipo da organização administrativa de um curso STRICTO
 * EX: Centro, Inter-Unidades , Inter-institucional (integrado entre instituições)
 * @author Victor Hugo
 */
@Entity
@Table(name = "organizacao_administrativa", schema = "stricto_sensu", uniqueConstraints = {})
public class OrganizacaoAdministrativa implements PersistDB {

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_organizacao_administrativa")
	private int id;

	/** a denominação da organização */
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
