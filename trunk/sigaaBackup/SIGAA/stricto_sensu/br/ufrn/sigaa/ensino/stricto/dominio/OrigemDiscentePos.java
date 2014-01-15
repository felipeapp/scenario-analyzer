/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
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
 * A origem do discente de pós
 * 1 - Própria IFES
 * 2 - Instituição Privada de Ensino
 * 3 - IFES no Estado
 * 4 - IFES no Exterior
 * 5 - Universidade Estadual
 * 6 - IFES fora do Estado
 * @author Gleydson
 */
@Entity
@Table(name = "origem_discente_pos", schema = "stricto_sensu")
public class OrigemDiscentePos implements PersistDB{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_origem_discente", nullable = false)
	private int id;

	/** a denominação da origem */
	private String denominacao;

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
