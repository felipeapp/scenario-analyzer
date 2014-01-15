/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 03/12/2007
 */
package br.ufrn.arq.migracao;

/**
 * Usado para substituição de valores na migração, por exemplo.
 * Onde tem S, botar true.
 *
 * @author Gleydson Lima
 *
 */
public class ColumnReplace {

	private String nomeColuna;

	private Object origemValue;

	private Object destValue;

	public ColumnReplace(String coluna, Object valueOrigem, Object valueDestino) {
		this.nomeColuna = coluna;
		this.origemValue = valueOrigem;
		this.destValue = valueDestino;
	}

	public Object getDestValue() {
		return destValue;
	}

	public void setDestValue(Object destValue) {
		this.destValue = destValue;
	}

	public String getNomeColuna() {
		return nomeColuna;
	}

	public void setNomeColuna(String nomeColuna) {
		this.nomeColuna = nomeColuna;
	}

	public Object getOrigemValue() {
		return origemValue;
	}

	public void setOrigemValue(Object origemValue) {
		this.origemValue = origemValue;
	}

}
