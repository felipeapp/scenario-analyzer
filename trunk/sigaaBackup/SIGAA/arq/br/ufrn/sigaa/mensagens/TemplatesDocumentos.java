/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 23/11/2009
 */
package br.ufrn.sigaa.mensagens;

/**
 * Classe que contém constantes relativas aos códigos dos
 * templates de e-mail utilizados pelo sistema.
 * 
 * @author David Pereira
 *
 */
public class TemplatesDocumentos {
	

	/***************************************************************************
	 * Templates do módulo ESTÁGIOS
	 **************************************************************************/	
	/**
	 * Código para o template de email para alerta de preenchimento do relatório de estágio
	 */
	public static final String EMAIL_ALERTA_PREENCHIMENTO_RELATORIO_ESTAGIO = "2_23100_1";
	
	/**
	 * Código para o template de email para alerta de preenchimento de relatórios de estágio que estão pendentes
	 */
	public static final String EMAIL_ALERTA_PREENCHIMENTO_RELATORIO_ESTAGIO_PENDENTE = "2_23100_2";
	
	/**
	 * Código para o template de email informando o cadastro de uma banca de qualificação/defesa de pós graduação
	 */
	public static final String EMAIL_AVISO_CADASTRO_BANCA = "2_23100_3";
	
	/** Código para o template de email informando o pré-cadastro do discente foi validado. */
	public static final String EMAIL_CONFIRMACAO_DISCENTE_PRECADASTRADO = "2_23100_4";
	
	/** Código para o template de email informando o pré-cadastro do discente foi excluído. */
	public static final String EMAIL_EXCLUSAO_DISCENTE_PRECADASTRADO = "2_23100_5";

}
