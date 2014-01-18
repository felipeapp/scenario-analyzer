/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '13/09/2006'
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * TipoLogradouro generated by hbm2java
 */
@Entity
@Table(schema="comum", name = "tipo_logradouro", uniqueConstraints = {})
public class TipoLogradouro implements Validatable {

	public static final int RUA = 11;

	// Fields

	private int id;

	private String descricao;

	// Constructors

	@Override
	public String toString() {
		return descricao != null ? descricao.toUpperCase() : "";
	}

	/** default constructor */
	public TipoLogradouro() {
	}

	/** default minimal constructor */
	public TipoLogradouro(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public TipoLogradouro(int idTipoLogradouro, String descricao) {
		this.id = idTipoLogradouro;
		this.descricao = descricao;
	}

	/** full constructor */
	public TipoLogradouro(int idTipoLogradouro, String descricao,
			Set<Cep> ceps, Set<Endereco> enderecos) {
		this.id = idTipoLogradouro;
		this.descricao = descricao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_logradouro", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoLogradouro) {
		this.id = idTipoLogradouro;
	}

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 20)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descri��o", lista);
		return lista;
	}

}