/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '01/12/2006'
 *
 */
package br.ufrn.sigaa.dominio;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Guarda as modalidades de educação da UFRN (presencial e a distância)
 */
@Entity
@Table(schema = "comum", name = "modalidade_educacao", uniqueConstraints = {})
@Cache ( usage = CacheConcurrencyStrategy.READ_ONLY )
public class ModalidadeEducacao implements Validatable {

	public static final int A_DISTANCIA = 2;

	public static final int PRESENCIAL = 1;
	
	public static final int SEMI_PRESENCIAL = 3;

	// Fields

	private int id;

	/** descrição da modalidade */
	private String descricao;

	// Constructors

	/** default constructor */
	public ModalidadeEducacao() {
	}

	/** default minimal constructor */
	public ModalidadeEducacao(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public ModalidadeEducacao(int idModalidadeEducacao, String descricao) {
		this.id = idModalidadeEducacao;
		this.descricao = descricao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_modalidade_educacao", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idModalidadeEducacao) {
		this.id = idModalidadeEducacao;
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
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", erros);
		return erros;
	}

	@Transient
	public boolean isADistancia() {
		return id == A_DISTANCIA;
	}
	
	@Transient
	public boolean isPresencial() {
		return id == PRESENCIAL;
	}	
	
	@Transient
	public boolean isSemiPresencial() {
		return id == SEMI_PRESENCIAL;
	}
}
