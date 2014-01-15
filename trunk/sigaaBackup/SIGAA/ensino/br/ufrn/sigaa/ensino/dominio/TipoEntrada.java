/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.ensino.dominio;

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
 * Entidade responsável pelo gerenciamento dos tipos de entrada.
 */
@Entity
@Deprecated
@Table(name = "tipo_entrada", schema = "ensino", uniqueConstraints = {})
public class TipoEntrada implements Validatable {

	// Fields

	private int id;

	private String descricao;

	// Constructors

	/** default constructor */
	public TipoEntrada() {
	}

	/** minimal constructor */
	public TipoEntrada(int idTipoEntrada) {
		this.id = idTipoEntrada;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_entrada", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoEntrada) {
		this.id = idTipoEntrada;
	}

	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
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

	/** Utilizado para verifica se os campos obrigatório foram informados.  */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDescricao(), "Descrição", lista);
		return lista;
	}

}
