/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 21/06/2010
 *
 */
package br.ufrn.sigaa.parametros.dominio;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/** Interface que define parâmetros utilizados no módulo Vestibular. 
 * @author Édipo Elder F. Melo
 *
 */
public interface ParametrosVestibular {
	
	/** Prefixo utilizado na configuração dos parâmetros. Valor atual: "2_13000_" */
	public static final String PREFIX = Sistema.SIGAA + "_" + SigaaSubsistemas.VESTIBULAR.getId() + "_";

	/** Resolução mínima, em KPixels, da foto 3x4 a ser enviada pelo candidato.*/
	public static final String RESOLUCAO_MINIMA_FOTO_3X4= PREFIX + "2";

	/** Quantidade máxima de fotos a serem exportadas do SIGAA para arquivo. */
	public static final String QUANTIDADE_MAXIMA_FOTOS_EXPORTAR = PREFIX + "3";

	/** ID do índice acadêmico que é utilizado na seleção de fiscais do vestibular, dentre os alunos de graduação. */
	public static final String INDICE_ACADEMICO_SELECAO_FISCAL_GRADUACAO = PREFIX + "4";

	/** Comissão Responsável pelo Vestibular */
	public static final String SIGLA_COMISSAO_RESPONSAVEL_PELO_VESTIBULAR = PREFIX + "5";

	/** Indica se a convocação de candidatos aprovados no vestibular pode ser por turno distinto de funcionamento de curso. */
	public static final String CONVOCAR_APROVADOS_TURNO_DISTINTO = PREFIX + "6";
	
	/** Unidade que receberá o crédito vindo dos pagamentos da GRU. */
	public static final String UNIDADE_RECEBIMENTO_GRU = PREFIX + "7";

}