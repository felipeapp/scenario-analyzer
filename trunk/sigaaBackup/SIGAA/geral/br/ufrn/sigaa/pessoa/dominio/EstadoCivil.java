/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '13/09/2006'
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

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
 * Armazena os tipos de estado civil
 */
@Entity
@Table(schema= "comum", name = "estado_civil", uniqueConstraints = {})
public class EstadoCivil implements Validatable {

	// Fields
	private int id;

	/** descrição do estado civil */
	private String descricao;

	// Constructors

	/** default constructor */
	public EstadoCivil() {
	}

	/** default minimal constructor */
	public EstadoCivil(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public EstadoCivil(int idEstadoCivil, String descricao) {
		this.id = idEstadoCivil;
		this.descricao = descricao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_estado_civil", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idEstadoCivil) {
		this.id = idEstadoCivil;
	}

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
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

	@Override
	public String toString() {
		return descricao;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}

}