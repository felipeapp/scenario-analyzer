/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 29/10/2009
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens do subsistema do Portal do Coordenador de Stricto.
 * 
 * @author Jean Guerethes
 */
public interface MensagensPortalCoordenadorStricto {

	/**
	 * Prefixo para estas mensagens.
	 */
	static final String prefix = Sistema.SIGAA + "_" + SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.getId() + "_";

	/**
	 * Essa mensagem ser� exibida quando o docente atingir o seu limite de bolsa, para um determinado
	 * tipo de orienta��o. � poss�vel passar um atributo. 
	 *  
	 */
	public static final String LIMITE_BOLSISTA = prefix + "01";

	/**
	 * Quando o docente n�o tem cotas para cadastrar um docente com sendo seu orientador. 
	 * Para como par�metro o tipo de orienta��o que o mesmo n�o pode cadastrar.
	 * 
	 */
	public static final String SEM_COTAS_PARA_BOLSISTA = prefix + "02";
	
	/**
	 * Caso o usu�rio tente mudar o tipo do discente e exibido a mensagem abaixo:
	 *  
	 * Mensagem: N�o � poss�vel alterar o tipo do discente. 
	 * Caso ela tenha ingress�o como regular � necess�rio cadastr�-lo com uma nova matr�cula. 
	 * Caso tenha sido cadastrado errado entre em contato com a Pr� Reitoria de P�s-Gradua��o. 
	 */
	public static final String NAO_E_POSSIVEL_ALTERAR_TIPO_DO_DISCENTE = prefix + "10";
	
	/**
	 * Exibe uma mensagem informando que s� � permitido realizar altera��o dos dados do discente do 
	 * programa no qual o usu�rio tamb�m for cadastrado.  
	 * 
	 * Mensagem: S� � permitido alterar dados dos discente do seu programa.
	 */
	public static final String ALTERACAO_APENAS_DO_MESMO_PROGRAMA = prefix + "11";	
	
	/**
	 * Mensagem exibida para informar ao usu�rio que o per�odo de an�lise de solicita��es de matr�cula � pertinente ao registrado no calend�rio 
	 * acad�mico do programa, que pertence o discente.
	 * Conte�do: O programa de p�s-gradua��o n�o possui curso ativo.
	 * Tipo: ERROR.
	 */
	public static final String PROGRAMA_NAO_POSSUI_CURSO = prefix + "12";
	
}