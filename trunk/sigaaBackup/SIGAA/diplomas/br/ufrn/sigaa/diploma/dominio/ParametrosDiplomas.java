/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 25/10/2010
 *
 */
package br.ufrn.sigaa.diploma.dominio;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/** Parametros utilizados no m�dulo de Diplomas.
 * @author �dipo Elder F. Melo
 *
 */
public interface ParametrosDiplomas {

	/** Prefixo utilizado na configura��o dos par�metros. Valor atual: "2_22000_" */
	public static final String PREFIX = Sistema.SIGAA + "_" + SigaaSubsistemas.REGISTRO_DIPLOMAS.getId() + "_";
	
	/** ID do tipo de documento Diploma, definido no SIGED. */
	public static final String TIPO_DOCUMENTO_DIPLOMA = PREFIX + "1";

	/** Caminho, no SIGED, onde ser�o armazenados os diplomas digitalizados. */
	public static final String PATH_DIPLOMAS = PREFIX + "2";

	/** Define o ID do descritor de documentos para o nome do aluno. */
	public static final String DESCRITOR_DIPLOMA_NOME_ALUNO = PREFIX + "3";

	/** Define o ID do descritor de documentos para a matr�cula do aluno. */
	public static final String DESCRITOR_DIPLOMA_MATRICULA_ALUNO = PREFIX + "4";
	
	/** Define o ID do descritor de documentos para o n�mero de registro do diploma do aluno. */
	public static final String DESCRITOR_DIPLOMA_NUMERO_REGISTRO = PREFIX + "5";
	
	/** O c�digo de recolhimento utilizado GRU de reavali��o de diplomas */
	public static final String ID_CODIGO_RECOLHIMENTO_GRU_REVALIDACAO_DIPLOMA = PREFIX + "6";    //ex.: 12 =  "28830-6 SERVICOS_ADMINISTRATIVOSS"

	/** Indica se ser� solicitado ao discente FORMANDO que verifique seus dados pessoais, e caso necess�rio, atualize-os.
	 *  Isto � utilizado para a gera��o do diploma do discente.
	 */
	public static final String SOLICITAR_ATUALIZACAO_DADOS_FORMANDO = PREFIX + "7";

	/** Corpo da mensagem a ser enviada ao discente solicitando a verifica��o e atualiza��o de dados pessoais.
	 *  Isto � utilizado para a gera��o do diploma do discente.
	 */
	public static final String EMAIL_SOLICITACAO_ATUALIZACAO_DADOS_PESSOAIS = PREFIX + "8";
}
