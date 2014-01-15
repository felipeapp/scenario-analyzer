/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 26/07/2012
 *
 */
package br.ufrn.sigaa.ensino.dominio;

/** Constantes que indicam o {@link TipoMovimentacaoAluno Tipo de Movimentação do Aluno}
 * @author Édipo Elder F. de Melo
 *
 */
public interface ConstantesTipoMovimentacaoAluno {

	/** Tipo que indica conclusão */
	public static final int CONCLUSAO = 1;
	/** Tipo que indica trancamento */
	public static final int TRANCAMENTO = 101;
	/** Tipo que indica excedido numero de reprovações */
	public static final int EXCEDIDO_NUMERO_REPROVACOES = 102;
	/** Tipo que indica prorrogação administrativa */
	public static final int PRORROGACAO_ADMINISTRATIVA = 201;
	/** Tipo que indica prorrogação judicial */
	public static final int PRORROGACAO_JUDICIAL = 202;
	/** Tipo que indica prorrogação trancamento programa */
	public static final int PRORROGACAO_TRANCAMENTO_PROGRAMA = 203;
	/** Tipo que indica prorrogação estorno */
	public static final int PRORROGACAO_ESTORNO = 204;
	/** Tipo que indica antecipação administrativa */
	public static final int ANTECIPACAO_ADMINISTRATIVA = 205;
	/** Tipo que indica antecipação judicial */
	public static final int ANTECIPACAO_JUDICIAL = 206;

	// usado no cancelamento automático do aluno que não se matriculou
	/** Tipo que indica abandono */
	@Deprecated
	public static final int ABANDONO = 16;
	/** Tipo que indica cancelamento por não ter matrícula no ano-período. */
	public static final int ABANDONO_NENHUMA_MATRICULA = 19;
	/** Tipo que indica cancelamento por não ter integralizado pelo menos um componente curricular. */
	public static final int ABANDONO_NENHUMA_INTEGRALIZACAO = 20;
	/** Tipo que indica prazo máximo */
	public static final int PRAZO_MAXIMO = 17;
	/** Tipo que indica cancelamento por não confirmação de vínculo */
	public static final int NAO_CONFIRMACAO_VINCULO = 18;
	/** Tipo que indica excluído */
	public static final int EXCLUIDO = 8;
	/** Tipo que indica cancelamento por upgrade de nível */
	public static final int CANCELAMENTO_POR_UPGRADE_NIVEL = 304;
	/** Tipo usado quando o discente integraliza toda sua grade curricular */
	public static final int INTEGRALIZACAO_DE_DISCENTE = 315;
	/** Tipo usado quando o discente cancela o vínculo anterior*/
	public static final int CANCELAMENTO_POR_EFETIVACAO_NOVO_CADASTRO = 10;
}
