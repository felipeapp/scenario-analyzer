/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/09/2006'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import java.util.Set;

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
 * Classe que registra os tipos de periodicidade de aulas utilizadas nos cursos lato sensu
 * (diário, semanal, etc.)
 */
@Entity
@Table(name = "tipo_periodicidade_aula", schema = "lato_sensu", uniqueConstraints = {})
public class TipoPeriodicidadeAula implements PersistDB {

	// Fields

	private int id;

	private String descricao;

	// Constructors

	/** default constructor */
	public TipoPeriodicidadeAula() {
	}

	/** default minimal constructor */
	public TipoPeriodicidadeAula(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public TipoPeriodicidadeAula(int idTipoPeriodicidadeAula, String descricao) {
		this.id = idTipoPeriodicidadeAula;
		this.descricao = descricao;
	}

	/** full constructor */
	public TipoPeriodicidadeAula(int idTipoPeriodicidadeAula, String descricao,
			Set<TurmaEntradaLato> turmaEntradas) {
		this.id = idTipoPeriodicidadeAula;
		this.descricao = descricao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_periodicidade_aula", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoPeriodicidadeAula) {
		this.id = idTipoPeriodicidadeAula;
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

}
