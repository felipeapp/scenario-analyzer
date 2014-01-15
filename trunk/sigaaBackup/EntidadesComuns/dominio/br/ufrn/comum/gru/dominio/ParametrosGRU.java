/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/05/2011
 *
 */
package br.ufrn.comum.gru.dominio;

import br.ufrn.comum.dominio.Sistema;

/** Par�metros utilizados na gera��o de Guia de Recolhimento da Uni�o.
 * 
 * @author �dipo Elder F. de Melo
 *
 */
public interface ParametrosGRU {

	/** Prefixo utilizado na configura��o dos par�metros. Valor atual: "3_0_" */
	public static final String PREFIX = Sistema.COMUM + "_0_";

	/** ID da unidade favorecida padr�o para recebimento dos pagamentos da GRU. */
	public String ID_UNIDADE_FAVORECIDA_PADRAO = PREFIX + "01";
	
	/** Ag�ncia padr�o para recebimento dos pagamentos da GRU. */
	public String AGENCIA_PADRAO = PREFIX + "02";
	
	/** N�mero do conv�nio padr�o para recebimento dos pagamentos da GRU. */
	public String CONVENIO_PADRAO = PREFIX + "03";
	
	/** N�mero da gest�o padr�o para recebimento dos pagamentos da GRU. */
	public static final String GESTAO_PADRAO = PREFIX + "04";

	/** Valor do campo Unidade Gestora da GRU*/
	public static final String CODIGO_UNIDADE_GESTORA_GRU = "2_10100_21";
	
	/** Valor do campo Codigo da Gest�o da GRU*/
	public static final String CODIGO_GESTAO_GRU = "2_10100_22";
	
	/** Instru��o padr�o a ser emitido na GRU*/
	public static final String INSTRUCAO_PADRAO_GRU = "2_10100_23";

}
