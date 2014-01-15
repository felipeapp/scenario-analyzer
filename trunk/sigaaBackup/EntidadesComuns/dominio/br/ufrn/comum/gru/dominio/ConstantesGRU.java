/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/05/2011
 *
 */
package br.ufrn.comum.gru.dominio;


/** Constantes utilizados na geração da Guia de Recolhimento da União.<br/>
 * Estas constantes são fixas para todas GRU.
 * @author Édipo Elder F. de Melo
 *
 */
public interface ConstantesGRU {
	
	/**
	 * Código de correlação de UG/Gestão X Código GRBB que deverá ser recuperada
	 * do SIAFI. Este valor é imutável.
	 */
	public final String UGXGRBB = "00940";
	
	/**
	 * Esta sequência é resultado do código identificação da arrecadação, 
	 * identificação do segmento e identificador do valor da moeda. Estes valores são imutáveis.
	 */
	public final String ARRECADACAO_ORGAO_VALOR =  "858";
	
	/** Código do Banco do Brasil. Este valor é imutável. */
	public final String COD_BANCO_COMPENSACAO = "001";
	
	/** Código da moeda Real. Este valor é imutável. */
	public final String COD_MOEDA_REAL = "9";
	
	/** Código STN junto a FEBRABRAN indicando o uso do CPF no código de barras. Este valor é imutável. */
	public final String CODIGO_STN_CPF = "0254";

}
