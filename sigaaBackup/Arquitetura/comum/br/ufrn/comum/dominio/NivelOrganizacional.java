/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 19/05/2008
 */
package br.ufrn.comum.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Permite a identificacao do nível organizacional da unidade:
 * tático, operacional, estratégico, gerencial.
 * 
 * @author Raphaela Galhardo
 * @author Gleydson Lima
 */
@Entity
@Table(name = "nivel_organizacional", schema = "comum")
public class NivelOrganizacional  implements PersistDB{

	public static final int ESTRATEGICO = 4;
	
	@Id
	@Column(name = "id_nivel_organizacional", nullable = false)
	/** Identificador da tabela */
	private int id;

	/** Nome do nível organizacional */
	@Column(name = "denominacao", nullable = false)
	private String denominacao;

	/** Construtores */
	public NivelOrganizacional() {
	}

	public NivelOrganizacional(int id) {
		this.id = id;
	}
	
	
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
