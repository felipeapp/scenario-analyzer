/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
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
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade responsável pelo gerenciamento dos graus de formação, podendo ser dentre outros
 * Ensino Médio, Graduação, Mestrado, Analfabeto. 
 */
@Entity
@Table(schema = "comum", name = "grau_formacao", uniqueConstraints = {})
public class GrauFormacao implements PersistDB, Validatable {

	// Fields    

	private int id;

	/** descrição do grau de formação */
	private String descricao;

	/** ordem da titulação, utilizado para ordenar as listas pra apresentação na sequência correta */
	private Integer ordemtitulacao;

	// Constructors

	/** default constructor */
	public GrauFormacao() {
	}

	/** default minimal constructor */
	public GrauFormacao(int id) {
		this.id = id;
	}
	
	/** minimal constructor */
	public GrauFormacao(int idGrauFormacao, String descricao) {
		this.id = idGrauFormacao;
		this.descricao = descricao;
	}

	/** full constructor */
	public GrauFormacao(int idGrauFormacao, String descricao,
			Integer ordemtitulacao) {
		this.id = idGrauFormacao;
		this.descricao = descricao;
		this.ordemtitulacao = ordemtitulacao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_grau_formacao", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idGrauFormacao) {
		this.id = idGrauFormacao;
	}

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "ordemtitulacao", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getOrdemtitulacao() {
		return this.ordemtitulacao;
	}

	public void setOrdemtitulacao(Integer ordemtitulacao) {
		this.ordemtitulacao = ordemtitulacao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "descricao");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, descricao);
	}

	/** Utilizado para verifica se os campos obrigatório foram informados.  */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		ValidatorUtil.validateRequired(ordemtitulacao, "Ordem da Titulação", lista);
		return lista;
	}

}