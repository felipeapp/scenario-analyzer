/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/10/2010
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface que guarda as constantes de mensagens do subsistema de NEE(Necessidades Educacionais Especiais).
 * 
 * @author Rafael Gomes
 */

public interface MensagensNee {
	/**
	 * Prefixo para estas mensagens. Atualmente 2_24000_
	 */
	public static final String prefixo = Sistema.SIGAA + "_" + SigaaSubsistemas.NEE.getId() + "_";
	
	/**
	 * Mensagem exibida ap�s o cadastro da solicita��o de apoio a NEE, 
	 * informando ao coordenador sobre os contatos da comiss�o de apoio a discentes 
	 * com Necessidades Educacionais Especiais.<br/><br/>
	 * 
	 * Conte�do: "Caro Coordenador,
	 * Favor orientar o estudante a entrar em contato com a CAENE atrav�s do telefone (84) 3342-2501, 
	 * para agendamento do atendimento ou pelo e-mail inclusao@reitoria.ufrn.br."<br/>
	 * Tipo: INFORMATION
	 */
	public static final String INFORMATIVO_APOS_CADASTRO_SOLICITACAO = prefixo + "01";


}
