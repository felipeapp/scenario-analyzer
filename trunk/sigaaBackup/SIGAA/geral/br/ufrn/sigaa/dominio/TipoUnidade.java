/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '13/09/2006'
 *
 */
package br.ufrn.sigaa.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * TipoUnidade generated by hbm2java
 */
@Entity
@Table(schema="comum", name = "tipo_unidade", uniqueConstraints = {})
public class TipoUnidade implements PersistDB {

	// Fields    

	private int id;

	private String descricao;

	// Constructors

	/** default constructor */
	public TipoUnidade() {
	}

	/** minimal constructor */
	public TipoUnidade(int idTipoUnidade) {
		this.id = idTipoUnidade;
	}

	/** full constructor */
	public TipoUnidade(int idTipoUnidade, String descricao) {
		this.id = idTipoUnidade;
		this.descricao = descricao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_unidade", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoUnidade) {
		this.id = idTipoUnidade;
	}

	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true, length = 30)
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

}