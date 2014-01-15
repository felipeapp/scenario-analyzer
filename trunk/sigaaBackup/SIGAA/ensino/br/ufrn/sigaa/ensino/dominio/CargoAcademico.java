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
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;


/**
 * Entidade que armazena os cargos acadêmicos disponíveis para definição dos cursos
 */
@Entity
@Table(name = "cargo_academico", schema = "ensino", uniqueConstraints = {})
public class CargoAcademico implements Validatable {

	// Constantes usadas pra facilitar as buscas
	public static final int COORDENACAO = 1;
	public static final int VICE_COORDENACAO = 2;
	public static final int SECRETARIA = 3;

	private int id;

	private String descricao;

	/** default constructor */
	public CargoAcademico() {
	}

	/** minimal constructor */
	public CargoAcademico(int idCargoAcademico) {
		this.id = idCargoAcademico;
	}

	/** full constructor */
	public CargoAcademico(int idCargoAcademico, String descricao) {
		this.id = idCargoAcademico;
		this.descricao = descricao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_cargo_academico", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idCargoAcademico) {
		this.id = idCargoAcademico;
	}

	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true, length = 150)
	public String getDescricao() {
		return this.descricao;
	}

	@Transient
	public String getDescricaoResumido() {
		switch (id) {
		case COORDENACAO:
			return "Coord";
		case VICE_COORDENACAO:
			return "Vice";
		default:
			break;
		}
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

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDescricao(), "Descrição", lista);
		return lista;
	}

}
