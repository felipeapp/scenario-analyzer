/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 11/04/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.parametros.dominio;

import java.util.Arrays;
import java.util.List;

import br.ufrn.sigaa.ensino.dominio.FormaIngresso;

/**
 * Interface contendo todos os parâmetros da forma de ingresso do discente.
 * 
 * @author Rafael Gomes
 *
 */
public interface ParametrosFormaIngresso {
	
	/** Identificador da forma de ingresso judicial */
	public static final String FORMA_INGRESSO_JUDICIAL = "2_10100_38";
	
	/** Constante que define a forma de ingresso REINGRESSO. */
	public static final List<Integer> REINGRESSO = Arrays.asList(34131, 34132);

	/** Constante que define a forma de ingresso ALUNO_ESPECIAL. */
	public static final FormaIngresso ALUNO_ESPECIAL = new FormaIngresso(34116);

	/** Constante que define a forma de ingresso VESTIBULAR. */
	public static final FormaIngresso VESTIBULAR = new FormaIngresso(34110);

	/** Constante que define a forma de ingresso SELECAO_POS_GRADUACAO. */
	public static final FormaIngresso SELECAO_POS_GRADUACAO = new FormaIngresso(994409);

	/** Constante que define a forma de ingresso MOBILIDADE_ESTUDANTIL. */
	public static final FormaIngresso MOBILIDADE_ESTUDANTIL = new FormaIngresso(34130);
	
	/** Constante que define a forma de ingresso REINGRESSO AUTOMÁTICO */
	public static final FormaIngresso REINGRESSO_AUTOMATICO = new FormaIngresso(34131);
	
	/** Constante que define a forma de ingresso Transferência Voluntária. */
	public static final FormaIngresso TRANSFERENCIA_VOLUNTARIA = new FormaIngresso(34109);
	
	/** Constante que define a forma de ingresso Processo Seletivo Técnico, Lato-Sensu. */
	public static final FormaIngresso PROCESSO_SELETIVO = new FormaIngresso(37350);
	
	/** Constante que define a forma de ingresso ESTUDOS COMPLEMENTARES. */
	public static final FormaIngresso ESTUDOS_COMPLEMENTARES = new FormaIngresso(1697747);
	
	/** Constante que define a  forma de ingresso PORTADOR DE DIPLOMA. */
	public static final FormaIngresso PORTADOR_DIPLOMA  = new FormaIngresso(1697747);
	
	/** Constante que define a forma de ingresso ALUNO_ESPECIAL DE PÓS GRADUAÇÃO. */
	public static final FormaIngresso ALUNO_ESPECIAL_POS = new FormaIngresso(994410);

	/** Constante que define a forma de ingresso "CONVENIO PEC-G". */
	public static final FormaIngresso ALUNO_PEC_G = new FormaIngresso(34117);
	
		/** ID da forma de ingresso "SISU". Valor inicial: 51252808 */
	public static final String ID_FORMA_INGRESSO_SISU = "2_10100_55";
}
