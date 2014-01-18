/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 06/08/2013
 * 
 */package br.ufrn.sigaa.ensino_rede.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Define a oferta de vagas nos cursos em rede.
 * 
 * @author Henrique André
 * 
 */
@Entity
@Table(schema="ensino_rede", name = "oferta_curso_associado")
public class OfertaCursoAssociado implements PersistDB  {

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_oferta_curso_associado", nullable = false)
	private int id;

	/**
	 * Ano da oferta
	 */
	@Column(nullable = false)
	private int ano;

	/**
	 * periodo da oferta
	 */
	@Column(nullable = false)
	private int periodo;

	/**
	 * Curso ofertado
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_dados_curso_rede", nullable = false)
	private DadosCursoRede dadosCurso;

	/**
	 * Total de vagas
	 */
	@Column(name = "total_vagas")
	private Integer totalVagas;

	@Transient
	private int totalAtivos;
	
	/**
	 * Construtor padronizado. 
	 */
	public OfertaCursoAssociado() {
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public DadosCursoRede getDadosCurso() {
		return dadosCurso;
	}

	public void setDadosCurso(DadosCursoRede dadosCurso) {
		this.dadosCurso = dadosCurso;
	}

	public Integer getTotalVagas() {
		return totalVagas;
	}

	public void setTotalVagas(Integer totalVagas) {
		this.totalVagas = totalVagas;
	}

	public int getTotalAtivos() {
		return totalAtivos;
	}

	public void setTotalAtivos(int totalAtivos) {
		this.totalAtivos = totalAtivos;
	}
	
}
