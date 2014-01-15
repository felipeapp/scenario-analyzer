/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/05/2011
 *
 */
package br.ufrn.comum.gru.dominio;

import br.ufrn.comum.dominio.Sistema;

/** Parâmetros utilizados na geração de Guia de Recolhimento da União.
 * 
 * @author Édipo Elder F. de Melo
 *
 */
public interface ParametrosGRU {

	/** Prefixo utilizado na configuração dos parâmetros. Valor atual: "3_0_" */
	public static final String PREFIX = Sistema.COMUM + "_0_";

	/** ID da unidade favorecida padrão para recebimento dos pagamentos da GRU. */
	public String ID_UNIDADE_FAVORECIDA_PADRAO = PREFIX + "01";
	
	/** Agência padrão para recebimento dos pagamentos da GRU. */
	public String AGENCIA_PADRAO = PREFIX + "02";
	
	/** Número do convênio padrão para recebimento dos pagamentos da GRU. */
	public String CONVENIO_PADRAO = PREFIX + "03";
	
	/** Número da gestão padrão para recebimento dos pagamentos da GRU. */
	public static final String GESTAO_PADRAO = PREFIX + "04";

	/** Valor do campo Unidade Gestora da GRU*/
	public static final String CODIGO_UNIDADE_GESTORA_GRU = "2_10100_21";
	
	/** Valor do campo Codigo da Gestão da GRU*/
	public static final String CODIGO_GESTAO_GRU = "2_10100_22";
	
	/** Instrução padrão a ser emitido na GRU*/
	public static final String INSTRUCAO_PADRAO_GRU = "2_10100_23";

}
