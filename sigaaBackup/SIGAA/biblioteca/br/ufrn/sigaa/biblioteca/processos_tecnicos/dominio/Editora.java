/*
 * Editora.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

/**
 *
 * Classe que guarda temporariamente as editoras que vem do SIPAC.
 *
 * @author jadson
 * @since 24/08/2009
 * @version 1.0 criacao da classe
 *
 */
public class Editora{
	
	private int id;
	private String denominacao;
	
	public Editora(int id, String denominacao) {
		super();
		this.id = id;
		this.denominacao = denominacao;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getDenominacao() {
		return denominacao;
	}
	
	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}	
}