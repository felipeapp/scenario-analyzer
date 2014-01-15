/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 17/11/2006
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Forma adotada para efetuar a seleção dos alunos
 * para ingressarem num curso de lato sensu
 *
 * @author Leonardo
 *
 */
@Entity
@Table(name = "forma_selecao", schema = "lato_sensu", uniqueConstraints = {})
public class FormaSelecao {

	// Fields
	
	private int id;
	
	private String descricao;
	
	private Set<FormaSelecaoProposta> formasSelecaoProposta = new HashSet<FormaSelecaoProposta>(0);

	// Constructors

	/** default constructor */
	public FormaSelecao() {
	}

	/** default minimal constructor */
	public FormaSelecao(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public FormaSelecao(int idFormaSelecao, String descricao) {
		this.id = idFormaSelecao;
		this.descricao = descricao;
	}

	/** full constructor */
	public FormaSelecao(int idFormaSelecao, String descricao,
			Set<FormaSelecaoProposta> formasSelecaoProposta) {
		this.id = idFormaSelecao;
		this.descricao = descricao;
		this.formasSelecaoProposta = formasSelecaoProposta;
	}

	// Property accessors
	/**
	 * @return the id
	 */
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_forma_selecao", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return the descricao
	 */
	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 50)
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param descricao the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return the formasSelecaoProposta
	 */
	@OneToMany(cascade = { }, fetch = FetchType.LAZY, mappedBy = "formaSelecao")
	public Set<FormaSelecaoProposta> getFormasSelecaoProposta() {
		return formasSelecaoProposta;
	}

	/**
	 * @param formasSelecaoProposta the formasSelecaoProposta to set
	 */
	public void setFormasSelecaoProposta(
			Set<FormaSelecaoProposta> formasSelecaoProposta) {
		this.formasSelecaoProposta = formasSelecaoProposta;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "descricao");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, descricao);
	}
	
}
