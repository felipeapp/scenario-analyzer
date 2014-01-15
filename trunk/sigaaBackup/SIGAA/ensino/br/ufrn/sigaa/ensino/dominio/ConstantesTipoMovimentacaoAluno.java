/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 26/07/2012
 *
 */
package br.ufrn.sigaa.ensino.dominio;

/** Constantes que indicam o {@link TipoMovimentacaoAluno Tipo de Movimenta��o do Aluno}
 * @author �dipo Elder F. de Melo
 *
 */
public interface ConstantesTipoMovimentacaoAluno {

	/** Tipo que indica conclus�o */
	public static final int CONCLUSAO = 1;
	/** Tipo que indica trancamento */
	public static final int TRANCAMENTO = 101;
	/** Tipo que indica excedido numero de reprova��es */
	public static final int EXCEDIDO_NUMERO_REPROVACOES = 102;
	/** Tipo que indica prorroga��o administrativa */
	public static final int PRORROGACAO_ADMINISTRATIVA = 201;
	/** Tipo que indica prorroga��o judicial */
	public static final int PRORROGACAO_JUDICIAL = 202;
	/** Tipo que indica prorroga��o trancamento programa */
	public static final int PRORROGACAO_TRANCAMENTO_PROGRAMA = 203;
	/** Tipo que indica prorroga��o estorno */
	public static final int PRORROGACAO_ESTORNO = 204;
	/** Tipo que indica antecipa��o administrativa */
	public static final int ANTECIPACAO_ADMINISTRATIVA = 205;
	/** Tipo que indica antecipa��o judicial */
	public static final int ANTECIPACAO_JUDICIAL = 206;

	// usado no cancelamento autom�tico do aluno que n�o se matriculou
	/** Tipo que indica abandono */
	@Deprecated
	public static final int ABANDONO = 16;
	/** Tipo que indica cancelamento por n�o ter matr�cula no ano-per�odo. */
	public static final int ABANDONO_NENHUMA_MATRICULA = 19;
	/** Tipo que indica cancelamento por n�o ter integralizado pelo menos um componente curricular. */
	public static final int ABANDONO_NENHUMA_INTEGRALIZACAO = 20;
	/** Tipo que indica prazo m�ximo */
	public static final int PRAZO_MAXIMO = 17;
	/** Tipo que indica cancelamento por n�o confirma��o de v�nculo */
	public static final int NAO_CONFIRMACAO_VINCULO = 18;
	/** Tipo que indica exclu�do */
	public static final int EXCLUIDO = 8;
	/** Tipo que indica cancelamento por upgrade de n�vel */
	public static final int CANCELAMENTO_POR_UPGRADE_NIVEL = 304;
	/** Tipo usado quando o discente integraliza toda sua grade curricular */
	public static final int INTEGRALIZACAO_DE_DISCENTE = 315;
	/** Tipo usado quando o discente cancela o v�nculo anterior*/
	public static final int CANCELAMENTO_POR_EFETIVACAO_NOVO_CADASTRO = 10;
}
