/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.projetos.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Entidades financiadoras podem ser agrupadas por estes grupos ex: CAPES< Estaduais, Sesu, etc
 */
@Entity
@Table(name = "grupo_entidade_financiadora", schema = "projetos")
public class GrupoEntidadeFinanciadora implements PersistDB {

	// Fields    

	private int id;

	/** descrição do grupo */
	private String nome;
	

	// Constructors

	/** default constructor */
	public GrupoEntidadeFinanciadora() {
	}
	
	/** default minimal constructor */
	public GrupoEntidadeFinanciadora(int id) {
		this.id = id;
	}
	

	/** full constructor */
	public GrupoEntidadeFinanciadora(int idGrupoEntFinanc, String nome) {
		this.id = idGrupoEntFinanc;
		this.nome = nome;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_grupo_ent_financ")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })	
	public int getId() {
		return this.id;
	}

	public void setId(int idGrupoEntFinanc) {
		this.id = idGrupoEntFinanc;
	}

	@Column(name = "nome", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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
