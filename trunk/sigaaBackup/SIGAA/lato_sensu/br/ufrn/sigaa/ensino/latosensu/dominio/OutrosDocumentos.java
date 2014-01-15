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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;


/**
 * Entidade que registra outros documentos dos cursos de pós-graduação lato sensu
 */
@Entity
@Table(name = "outros_documentos", schema = "lato_sensu", uniqueConstraints = {})
public class OutrosDocumentos implements PersistDB {

	// Fields

	private int id;

	private CursoLato curso = new CursoLato();

	private String descricao;

	private Date data;

	private String outrasInformacoes;

	// Constructors

	/** default constructor */
	public OutrosDocumentos() {
	}

	/** minimal constructor */
	public OutrosDocumentos(int idOutrosDocumentos) {
		this.id = idOutrosDocumentos;
	}

	/** full constructor */
	public OutrosDocumentos(int idOutrosDocumentos, CursoLato cursoLato,
			String descricao, Date data, String outrasInformacoes) {
		this.id = idOutrosDocumentos;
		this.curso = cursoLato;
		this.descricao = descricao;
		this.data = data;
		this.outrasInformacoes = outrasInformacoes;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_outros_documentos", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idOutrosDocumentos) {
		this.id = idOutrosDocumentos;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso", unique = false, nullable = true, insertable = true, updatable = true)
	public CursoLato getCurso() {
		return this.curso;
	}

	public void setCurso(CursoLato cursoLato) {
		this.curso = cursoLato;
	}

	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true, length = 150)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@Column(name = "outras_informacoes", unique = false, nullable = true, insertable = true, updatable = true, length = 150)
	public String getOutrasInformacoes() {
		return this.outrasInformacoes;
	}

	public void setOutrasInformacoes(String outrasInformacoes) {
		this.outrasInformacoes = outrasInformacoes;
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
