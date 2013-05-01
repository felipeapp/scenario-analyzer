package br.com.ecommerce.arq.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Representa os par�metros que ser�o usados para informar como o sistema deve
 * se comportar.
 * @author Rodrigo Dutra de Oliveira
 *
 */
@Entity
@Table
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class Parametro extends SimpleDB{

	/**
	 * C�digo unico que representa cada par�metro.
	 */
	@Column(unique=true)
	private String codigo;
	
	/**
	 * Nome do par�metro
	 */
	private String nome;
	
	/**
	 * Descri��o informando onde ele � usado.
	 */
	private String descricao;
	
	/**
	 * Valor propriamente dito do par�metro.
	 */
	private String valor;
	
	public Parametro(){
		
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
}
