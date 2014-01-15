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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Classificação das entidades financiadoras dos projetos
 */
@Entity
@Table(name = "classificacao_financiadora", schema = "projetos", uniqueConstraints = {})
public class ClassificacaoFinanciadora implements Validatable {

	// Fields

	private int id;

	/** descrição da classificação das entidades financiadoras */
	private String descricao;


	// Constructors

	/** default constructor */
	public ClassificacaoFinanciadora() {
	}

	/** default minimal constructor */
	public ClassificacaoFinanciadora(int id) {
		this.id = id;
	}

	/** full constructor */
	public ClassificacaoFinanciadora(int idClassificacaoFinanciadora,
			String descricao) {
		this.id = idClassificacaoFinanciadora;
		this.descricao = descricao;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_classificacao_financiadora")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })	
	public int getId() {
		return this.id;
	}

	public void setId(int idClassificacaoFinanciadora) {
		this.id = idClassificacaoFinanciadora;
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
		return null;
	}

}
