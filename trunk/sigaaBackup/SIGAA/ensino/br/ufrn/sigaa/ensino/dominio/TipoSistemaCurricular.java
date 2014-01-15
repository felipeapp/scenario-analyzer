/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Tipo de sistema curricular (medida de cumprimento de currículo) que um curso ou matriz curricular podem trabalhar
 */
@Entity
@Table(name = "tipo_sistema_curricular", schema = "ensino", uniqueConstraints = {})
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class TipoSistemaCurricular implements PersistDB {

	public static final int HORA_AULA = 2;

	// Fields

	private int id;

	private String descricao;

	// Constructors

	/** default constructor */
	public TipoSistemaCurricular() {
	}

	/** default minimal constructor */
	public TipoSistemaCurricular(int id) {
		this.id = id;
	}

	/** full constructor */
	public TipoSistemaCurricular(int idTipoSistemaCurricular, String descricao) {
		this.id = idTipoSistemaCurricular;
		this.descricao = descricao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_sistema_curricular", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoSistemaCurricular) {
		this.id = idTipoSistemaCurricular;
	}

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 60)
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
