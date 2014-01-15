/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 06/11/2007
 */
package br.ufrn.arq.web.struts;

/**
 * Form para trabalhar com unidades.
 * 
 * @author Gleydson Lima
 *
 */
public class UnidadeForm extends AbstractForm {

	private int id;

	private Integer codigo;

	private String nome;

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
