/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 21/06/2010
 *
 */
package br.ufrn.sigaa.parametros.dominio;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/** Interface que define par�metros utilizados no m�dulo Vestibular. 
 * @author �dipo Elder F. Melo
 *
 */
public interface ParametrosVestibular {
	
	/** Prefixo utilizado na configura��o dos par�metros. Valor atual: "2_13000_" */
	public static final String PREFIX = Sistema.SIGAA + "_" + SigaaSubsistemas.VESTIBULAR.getId() + "_";

	/** Resolu��o m�nima, em KPixels, da foto 3x4 a ser enviada pelo candidato.*/
	public static final String RESOLUCAO_MINIMA_FOTO_3X4= PREFIX + "2";

	/** Quantidade m�xima de fotos a serem exportadas do SIGAA para arquivo. */
	public static final String QUANTIDADE_MAXIMA_FOTOS_EXPORTAR = PREFIX + "3";

	/** ID do �ndice acad�mico que � utilizado na sele��o de fiscais do vestibular, dentre os alunos de gradua��o. */
	public static final String INDICE_ACADEMICO_SELECAO_FISCAL_GRADUACAO = PREFIX + "4";

	/** Comiss�o Respons�vel pelo Vestibular */
	public static final String SIGLA_COMISSAO_RESPONSAVEL_PELO_VESTIBULAR = PREFIX + "5";

	/** Indica se a convoca��o de candidatos aprovados no vestibular pode ser por turno distinto de funcionamento de curso. */
	public static final String CONVOCAR_APROVADOS_TURNO_DISTINTO = PREFIX + "6";
	
	/** Unidade que receber� o cr�dito vindo dos pagamentos da GRU. */
	public static final String UNIDADE_RECEBIMENTO_GRU = PREFIX + "7";

}