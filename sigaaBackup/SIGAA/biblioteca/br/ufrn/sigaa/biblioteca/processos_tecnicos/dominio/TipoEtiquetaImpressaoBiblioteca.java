/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 06/05/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

/**
 *
 * <p> Os tipos de etiqueta que s�o impressos pelas bibliotecas </p>
 * 
 * @author jadson
 *
 */
public enum TipoEtiquetaImpressaoBiblioteca {
	
	CODIGO_BARRAS(0, "C�digo de Barras"), LOMBADA(1, "Lombada"), ENDERECO(2, "Endere�o");
	
	int valor;
	
	String descricao;
	
	private TipoEtiquetaImpressaoBiblioteca(int valor, String descricao){
		this.valor = valor;
		this.descricao = descricao;
	}

	public int getValor() {
		return valor;
	}

	public String getDescricao() {
		return descricao;
	}
}
