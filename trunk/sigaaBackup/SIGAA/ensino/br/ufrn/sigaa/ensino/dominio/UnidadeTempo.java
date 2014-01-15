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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Unidade de tempo par definição de estruturas curriculares do ensino técnico
 */
@Entity
@Table(name = "unidade_tempo", schema = "ensino", uniqueConstraints = {})
public class UnidadeTempo implements PersistDB {

	// Fields    

	public static final int	MES	= 4;
	public static final int	SEMESTRE	= 5;
	public static final int	ANO	= 6;

	private int id;

	private String descricao;

	private Integer valorEmDias;

	// Constructors

	/** default constructor */
	public UnidadeTempo() {
	}

	/** default minimal constructor */
	public UnidadeTempo(int id) {
		this.id = id;
	}
	
	/** minimal constructor */
	public UnidadeTempo(int idUnidadeTempo, String descricao) {
		this.id = idUnidadeTempo;
		this.descricao = descricao;
	}

	/** full constructor */
	public UnidadeTempo(int idUnidadeTempo, String descricao,
			Integer valoremdias) {
		this.id = idUnidadeTempo;
		this.descricao = descricao;
		this.valorEmDias = valoremdias;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_unidade_tempo", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idUnidadeTempo) {
		this.id = idUnidadeTempo;
	}

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "valorEmDias", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getValorEmDias() {
		return this.valorEmDias;
	}

	public void setValorEmDias(Integer valoremdias) {
		this.valorEmDias = valoremdias;
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
