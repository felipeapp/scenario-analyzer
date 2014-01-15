/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '024/12/2006'
 *
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO com informa��es referentes �s forma��es acad�micas utilizado para comunica��o entre
 * SIGAA e SIGPRH durante o cadastro de forma��o acad�micas dos servidores.
 * 
 * @author Alisson Nascimento
 */
public class FormacaoAcademicaDTO implements Serializable {
	
	/**
	 * Descri��o da forma��o acad�mica.
	 */
	private String formacao;

	/**
	 * Grau associado a forma��o acad�mica.
	 */
	private String grau;

	/**
	 * T�tulo do trabalho que gerou a forma��o acad�mica.
	 */
	private String titulo;

	/**
	 * Institui��o onde se obteve a forma��o acad�mica.
	 */
	private String instituicao;

	/**
	 * Orientador do trabalho que gerou a forma��o acad�mica.
	 */
	private String orientador;

	/**
	 * Sub�rea associada a forma��o acad�mica.
	 */
	private String subArea;
	
	/**
	 * In�cio do curso cuja conclus�o resultou na nova forma��o acad�mica.
	 */
	private Date inicio;
	
	/**
	 * Fim do curso cuja conclus�o resultou na nova forma��o acad�mica.
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