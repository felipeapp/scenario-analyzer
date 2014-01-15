/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 04/09/2009
 */
package br.ufrn.comum.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Entidade que armazena as informações dos paises.
 * 
 * @author Jean Guerethes
 * @author Gleydson Lima
 */
@Entity
@Table(name = "pais", schema = "comum")
public class Pais implements PersistDB {

	public static final int BRASIL = 31;
	// Fields

	private int id;

	/** nome do pais */
	private String nome;

	/** Designação da Nacionalidade:
		001          BRASIL		Brasileiro
		002	AFRICA DO SUL	Sul Africano
		003	ANDORRA	???
		004	AFGANISTÃO	Afegão
		005	ARGÉLIA	                Argelino
		006	ALEMANHA            Alemão
		... 
	  */
	private String nacionalidade;
	
	/** código do país no SIAFI*/
	@Column(name = "codigo_siafi")
	private String codigoSiafi;

	// Constructors

	/** default constructor */
	public Pais() {
	}

	/** default minimal constructor */
	public Pais(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public Pais(int idPais, String nome) {
		this.id = idPais;
		this.nome = nome;
	}

	/** full constructor */
	public Pais(int idPais, String nome, String nacionalidade) {
		this.id = idPais;
		this.nome = nome;
		this.nacionalidade = nacionalidade;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	public int getId() {
		return this.id;
	}

	public void setId(int idPais) {
		this.id = idPais;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNacionalidade() {
		return this.nacionalidade;
	}

	public void setNacionalidade(String nacionalidade) {
		this.nacionalidade = nacionalidade;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "nome");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, nome);
	}
	
	@Transient
	public boolean isBrasil() {
		return this.id == BRASIL;
	}

	public String getCodigoSiafi() {
		return codigoSiafi;
	}

	public void setCodigoSiafi(String codigoSiafi) {
		this.codigoSiafi = codigoSiafi;
	}

}