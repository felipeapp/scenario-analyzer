/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
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
 * Entidade responsável pelo gerenciamento do Tipo de Rede Ensino, se é do tipo 
 * Estadual, Privada, Pública, Federal ou Municipal. 
 */
@Entity
@Table(schema="comum", name = "tipo_rede_ensino", uniqueConstraints = {})
public class TipoRedeEnsino implements Validatable {

	// Fields

	private int id;

	private String descricao;

	// Constructors

	/** default constructor */
	public TipoRedeEnsino() {
	}

	/** default minimal constructor */
	public TipoRedeEnsino(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public TipoRedeEnsino(int idTipoRedeEnsino, String descricao) {
		this.id = idTipoRedeEnsino;
		this.descricao = descricao;
	}

	/** full constructor */
	public TipoRedeEnsino(int idTipoRedeEnsino, String descricao,
			Set<Pessoa> pessoas) {
		this.id = idTipoRedeEnsino;
		this.descricao = descricao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_rede_ensino", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoRedeEnsino) {
		this.id = idTipoRedeEnsino;
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

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}

}
