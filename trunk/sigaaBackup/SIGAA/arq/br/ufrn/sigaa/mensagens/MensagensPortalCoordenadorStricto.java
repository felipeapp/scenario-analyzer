/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
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
	 * Essa mensagem será exibida quando o docente atingir o seu limite de bolsa, para um determinado
	 * tipo de orientação. É possível passar um atributo. 
	 *  
	 */
	public static final String LIMITE_BOLSISTA = prefix + "01";

	/**
	 * Quando o docente não tem cotas para cadastrar um docente com sendo seu orientador. 
	 * Para como parâmetro o tipo de orientação que o mesmo não pode cadastrar.
	 * 
	 */
	public static final String SEM_COTAS_PARA_BOLSISTA = prefix + "02";
	
	/**
	 * Caso o usuário tente mudar o tipo do discente e exibido a mensagem abaixo:
	 *  
	 * Mensagem: Não é possível alterar o tipo do discente. 
	 * Caso ela tenha ingressão como regular é necessário cadastrá-lo com uma nova matrícula. 
	 * Caso tenha sido cadastrado errado entre em contato com a Pró Reitoria de Pós-Graduação. 
	 */
	public static final String NAO_E_POSSIVEL_ALTERAR_TIPO_DO_DISCENTE = prefix + "10";
	
	/**
	 * Exibe uma mensagem informando que só é permitido realizar alteração dos dados do discente do 
	 * programa no qual o usuário também for cadastrado.  
	 * 
	 * Mensagem: Só é permitido alterar dados dos discente do seu programa.
	 */
	public static final String ALTERACAO_APENAS_DO_MESMO_PROGRAMA = prefix + "11";	
	
	/**
	 * Mensagem exibida para informar ao usuário que o período de análise de solicitações de matrícula é pertinente ao registrado no calendário 
	 * acadêmico do programa, que pertence o discente.
	 * Conteúdo: O programa de pós-graduação não possui curso ativo.
	 * Tipo: ERROR.
	 */
	public static final String PROGRAMA_NAO_POSSUI_CURSO = prefix + "12";
	
}