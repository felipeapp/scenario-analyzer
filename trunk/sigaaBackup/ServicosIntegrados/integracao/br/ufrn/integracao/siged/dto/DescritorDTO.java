/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 16/12/2009
*
*/

package br.ufrn.integracao.siged.dto;

import java.io.Serializable;

/**
 * Data Transfer Object que armazena informações
 * sobre os descritores e os seus valores para um determinado
 * documento do SIGED.
 * 
 * @author David Pereira
 *
 */
public class DescritorDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3806115900983307765L;

	/** Identificador do Descritor no SIGED */
	private int id;
	
	/** Nome do descritor */
	private String descricao;
	
	/** Tipo de Documento do qual o descritor faz parte */
	private int tipoDocumento;
	
	/** Documento associado ao descritor, para quando esta classe 
	 * for usada como equivalente da classe ValorDescritor do SIGED.
	 * Pode ser nulo. */
	private Integer documento;
	
	/** Valor do descritor quando associado a um documento. Pode ser nulo.  */
	private String valor;
	
	/** Complemento do valor do descritor quando associado a um documento. Pode ser nulo.  */
	private String complemento;
	
	/** Alias utilizado como referência para atribuição do valor dos descritores no cadastro dos documentos.  */
	private String alias;

	public DescritorDTO() {
		System.out.println("Classloader de DescritorDTO: " + getClass().getClassLoader());
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(int tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getComplemento() {
		return complemento;
	}

	public Integer getDocumento() {
		return documento;
	}

	public void setDocumento(Integer documento) {
		this.documento = documento;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
}
