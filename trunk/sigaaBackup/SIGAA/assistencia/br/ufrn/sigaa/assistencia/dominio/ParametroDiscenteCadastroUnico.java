package br.ufrn.sigaa.assistencia.dominio;

import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Encapsular os parâmetros vindos do formulário
 * 
 * @author Rayron Victor
 */
public class ParametroDiscenteCadastroUnico {
	
	/** Centro que o discente pertence */
	private Unidade centro = new Unidade();
	/** Curso do discente */
	private Curso curso = new Curso();
	/** Área do discente */
	private String area;
	
	public Unidade getCentro() {
		return centro;
	}
	public void setCentro(Unidade centro) {
		this.centro = centro;
	}
	public Curso getCurso() {
		return curso;
	}
	public void setCurso(Curso curso) {
		this.curso = curso;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
}
