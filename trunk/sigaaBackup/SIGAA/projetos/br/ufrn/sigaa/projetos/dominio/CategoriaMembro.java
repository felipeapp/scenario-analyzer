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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Categoria de membros de projetos de pesquisa ou grupos de pesquisa
 * 
 * @author Ricardo Wendell
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "categoria_membro", schema = "projetos", uniqueConstraints = {})
public class CategoriaMembro implements PersistDB {

	/*
	 * Valores das constantes EM RH.CATEGORIA
	 * 
	 * 1;"Docente                       "
	 * 2;"Tecnico Administrativo        "
	 * 3;"Consignatarias                "
	 * 4;"Terceirizados                 "
	 * 5;"Bolsistas                     "
	 * 6;"Medico Residente              "
	 *
	 */
	
	public static final int DOCENTE = 1;
	public static final int DISCENTE = 2;
	public static final int SERVIDOR = 3;
	public static final int EXTERNO = 4;
	
	private int id;
	private String descricao;

	// Constructors

	/** default constructor */
	public CategoriaMembro() {
	}

	/** default minimal constructor */
	public CategoriaMembro(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public CategoriaMembro(int idCategoriaEquipe, String descricao) {
		id = idCategoriaEquipe;
		this.descricao = descricao;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_categoria_membro")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	           
	public int getId() {
		return id;
	}

	public void setId(int idCategoriaEquipe) {
		id = idCategoriaEquipe;
	}

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getDescricao() {
		return descricao;
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

	@Transient
	public boolean isServidor() {
		return DOCENTE == id || SERVIDOR == id;
	}

	@Transient
	public boolean isDocente() {
		return DOCENTE == id;
	}
	
	@Transient
	public boolean isApenasServidor() {
		return SERVIDOR == id;
	}
	
	@Transient
	public boolean isDiscente() {
		return DISCENTE == id;
	}
	
	@Transient
	public boolean isExterno() {
		return EXTERNO == id;
	}



}
