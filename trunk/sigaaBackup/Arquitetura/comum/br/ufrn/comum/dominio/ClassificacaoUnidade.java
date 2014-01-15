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
 * Entidade para identificacao da classificacao da unidade: acadêmica, administrativa, complexo hospitalar,
 * órgão suplementar.
 * 
 * @author Raphaela Galhardo
 * @author Gleydson Lima
 */
@Entity
@Table(name = "classificacao_unidade", schema="comum")
public class ClassificacaoUnidade  implements PersistDB{

	public static final int ORGAO_SUPLEMENTAR = 1;
	public static final int COMPLEXO_HOSPITALAR = 2;
	public static final int ACADEMICA = 3;
	
	/** Identificador */
	@Id
	@Column(name = "id_classificacao_unidade", nullable = false)
	private int id;

	/** Nome da classificação */
	@Column(name = "denominacao", nullable = false)
	private String denominacao;

	
	/** Construtores */
	public ClassificacaoUnidade() {
	}

	public ClassificacaoUnidade(int id) {
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
