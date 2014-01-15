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
 * Forma de avaliação dos alunos num curso lato sensu
 *
 * @author Leonardo
 *
 */
@Entity
@Table(name = "forma_avaliacao", schema = "lato_sensu", uniqueConstraints = {})
public class FormaAvaliacao {

	// Fields
	
	private int id;
	
	private String descricao;
	
	private Set<FormaAvaliacaoProposta> formasAvaliacaoProposta = new HashSet<FormaAvaliacaoProposta>(0);

	// Constructors

	/** default constructor */
	public FormaAvaliacao() {
	}

	/** default minimal constructor */
	public FormaAvaliacao(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public FormaAvaliacao(int idFormaAvaliacao, String descricao) {
		this.id = idFormaAvaliacao;
		this.descricao = descricao;
	}

	/** full constructor */
	public FormaAvaliacao(int idFormaAvaliacao, String descricao,
			Set<FormaAvaliacaoProposta> formasAvaliacaoProposta) {
		this.id = idFormaAvaliacao;
		this.descricao = descricao;
		this.formasAvaliacaoProposta = formasAvaliacaoProposta;
	}

	// Property accessors
	/**
	 * @return the id
	 */
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_forma_avaliacao", unique = true, nullable = false, insertable = true, updatable = true)
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
	@OneToMany(cascade = { }, fetch = FetchType.LAZY, mappedBy = "formaAvaliacao")
	public Set<FormaAvaliacaoProposta> getFormasAvaliacaoProposta() {
		return formasAvaliacaoProposta;
	}

	/**
	 * @param formasSelecaoProposta the formasSelecaoProposta to set
	 */
	public void setFormasAvaliacaoProposta(
			Set<FormaAvaliacaoProposta> formasAvaliacaoProposta) {
		this.formasAvaliacaoProposta = formasAvaliacaoProposta;
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
