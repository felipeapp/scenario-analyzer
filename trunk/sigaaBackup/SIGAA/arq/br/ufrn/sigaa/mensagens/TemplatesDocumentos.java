/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Cria��o: 23/11/2009
 */
package br.ufrn.sigaa.mensagens;

/**
 * Classe que cont�m constantes relativas aos c�digos dos
 * templates de e-mail utilizados pelo sistema.
 * 
 * @author David Pereira
 *
 */
public class TemplatesDocumentos {
	

	/***************************************************************************
	 * Templates do m�dulo EST�GIOS
	 **************************************************************************/	
	/**
	 * C�digo para o template de email para alerta de preenchimento do relat�rio de est�gio
	 */
	public static final String EMAIL_ALERTA_PREENCHIMENTO_RELATORIO_ESTAGIO = "2_23100_1";
	
	/**
	 * C�digo para o template de email para alerta de preenchimento de relat�rios de est�gio que est�o pendentes
	 */
	public static final String EMAIL_ALERTA_PREENCHIMENTO_RELATORIO_ESTAGIO_PENDENTE = "2_23100_2";
	
	/**
	 * C�digo para o template de email informando o cadastro de uma banca de qualifica��o/defesa de p�s gradua��o
	 */
	public static final String EMAIL_AVISO_CADASTRO_BANCA = "2_23100_3";
	
	/** C�digo para o template de email informando o pr�-cadastro do discente foi validado. */
	public static final String EMAIL_CONFIRMACAO_DISCENTE_PRECADASTRADO = "2_23100_4";
	
	/** C�digo para o template de email informando o pr�-cadastro do discente foi exclu�do. */
	public static final String EMAIL_EXCLUSAO_DISCENTE_PRECADASTRADO = "2_23100_5";

}
