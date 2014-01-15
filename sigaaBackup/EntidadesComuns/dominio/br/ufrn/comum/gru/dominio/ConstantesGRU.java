/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/05/2011
 *
 */
package br.ufrn.comum.gru.dominio;


/** Constantes utilizados na gera��o da Guia de Recolhimento da Uni�o.<br/>
 * Estas constantes s�o fixas para todas GRU.
 * @author �dipo Elder F. de Melo
 *
 */
public interface ConstantesGRU {
	
	/**
	 * C�digo de correla��o de UG/Gest�o X C�digo GRBB que dever� ser recuperada
	 * do SIAFI. Este valor � imut�vel.
	 */
	public final String UGXGRBB = "00940";
	
	/**
	 * Esta sequ�ncia � resultado do c�digo identifica��o da arrecada��o, 
	 * identifica��o do segmento e identificador do valor da moeda. Estes valores s�o imut�veis.
	 */
	public final String ARRECADACAO_ORGAO_VALOR =  "858";
	
	/** C�digo do Banco do Brasil. Este valor � imut�vel. */
	public final String COD_BANCO_COMPENSACAO = "001";
	
	/** C�digo da moeda Real. Este valor � imut�vel. */
	public final String COD_MOEDA_REAL = "9";
	
	/** C�digo STN junto a FEBRABRAN indicando o uso do CPF no c�digo de barras. Este valor � imut�vel. */
	public final String CODIGO_STN_CPF = "0254";

}
