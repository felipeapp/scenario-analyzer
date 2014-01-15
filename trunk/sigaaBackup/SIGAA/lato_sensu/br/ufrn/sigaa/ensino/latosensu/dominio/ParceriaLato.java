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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.PessoaJuridica;

/**
 * Associa um curso lato sensu a uma pessoa jurídica
 */
@Entity
@Table(name = "parceria_lato", schema = "lato_sensu", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"id_curso", "id_pessoa_juridica" }) })
public class ParceriaLato implements PersistDB {

	// Fields

	private int id;

	private PessoaJuridica pessoaJuridica = new PessoaJuridica();

	private CursoLato cursoLato = new CursoLato();

	private String descricao;

	// Constructors

	/** default constructor */
	public ParceriaLato() {
	}

	/** minimal constructor */
	public ParceriaLato(int idParceria) {
		this.id = idParceria;
	}

	/** full constructor */
	public ParceriaLato(int idParceria, PessoaJuridica pessoaJuridica,
			CursoLato cursoLato) {
		this.id = idParceria;
		this.pessoaJuridica = pessoaJuridica;
		this.cursoLato = cursoLato;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_parceria", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idParceria) {
		this.id = idParceria;
	}

	@ManyToOne(cascade = { }, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pessoa_juridica", unique = false, nullable = true, insertable = true, updatable = true)
	public PessoaJuridica getPessoaJuridica() {
		return this.pessoaJuridica;
	}

	public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
		this.pessoaJuridica = pessoaJuridica;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_curso", unique = false, nullable = true, insertable = true, updatable = true)
	public CursoLato getCursoLato() {
		return cursoLato;
	}

	public void setCursoLato(CursoLato cursoLato) {
		this.cursoLato = cursoLato;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ListaMensagens validate() {

		ListaMensagens erros = new ListaMensagens();

		ValidatorUtil.validateRequired(descricao, "Descrição", erros
				.getMensagens());
		ValidatorUtil
				.validateRequired(cursoLato, "Curso", erros.getMensagens());
		ValidatorUtil.validaInt(pessoaJuridica.getNumDeclaracaoEstadual(),
				"Número Declaração Estadual", erros.getMensagens());

		return erros;
	}

}
