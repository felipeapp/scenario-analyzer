/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '024/12/2006'
 *
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO com informações referentes às formações acadêmicas utilizado para comunicação entre
 * SIGAA e SIGPRH durante o cadastro de formação acadêmicas dos servidores.
 * 
 * @author Alisson Nascimento
 */
public class FormacaoAcademicaDTO implements Serializable {
	
	/**
	 * Descrição da formação acadêmica.
	 */
	private String formacao;

	/**
	 * Grau associado a formação acadêmica.
	 */
	private String grau;

	/**
	 * Título do trabalho que gerou a formação acadêmica.
	 */
	private String titulo;

	/**
	 * Instituição onde se obteve a formação acadêmica.
	 */
	private String instituicao;

	/**
	 * Orientador do trabalho que gerou a formação acadêmica.
	 */
	private String orientador;

	/**
	 * Subárea associada a formação acadêmica.
	 */
	private String subArea;
	
	/**
	 * Início do curso cuja conclusão resultou na nova formação acadêmica.
	 */
	private Date inicio;
	
	/**
	 * Fim do curso cuja conclusão resultou na nova formação acadêmica.
	 */
	private Date fim;

	public String getGrau() {
		return grau;
	}

	public void setGrau(String grau) {
		this.grau = grau;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	public String getOrientador() {
		return orientador;
	}

	public void setOrientador(String orientador) {
		this.orientador = orientador;
	}

	public String getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(String instituicao) {
		this.instituicao = instituicao;
	}

	public String getFormacao() {
		return formacao;
	}

	public void setFormacao(String formacao) {
		this.formacao = formacao;
	}

	public String getSubArea() {
		return subArea;
	}

	public void setSubArea(String subArea) {
		this.subArea = subArea;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

}