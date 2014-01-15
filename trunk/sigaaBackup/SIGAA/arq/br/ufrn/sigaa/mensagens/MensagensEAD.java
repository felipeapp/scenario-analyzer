/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/02/2010
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface que guarda as constantes de mensagens do subsistema de Educa��o � Dist�ncia.
 * 
 * @author Fred_Castro
 */

public interface MensagensEAD {
	/**
	 * Prefixo para estas mensagens. Atualmente 2_11200_
	 */
	public static final String prefixo = Sistema.SIGAA + "_" + SigaaSubsistemas.SEDIS.getId() + "_";
	
	/**
	 * Mensagem exibida quando o usu�rio tenta selecionar um curso que n�o existe no polo.<br/><br/>
	 * 
	 * Conte�do: "O curso selecionado n�o existe neste p�lo."<br/>
	 * Tipo: Erro
	 */
	public static final String CURSO_NAO_EXISTE = prefixo + "1";

	/**
	 * Mensagem exibida quando o tutor selecionado � inv�lido.<br/><br/>
	 * 
	 * Conte�do: "O tutor selecionado n�o � v�lido. Por favor, reinicie a opera��o."<br/>
	 * Tipo: Erro
	 */
	public static final String TUTOR_INVALIDO = prefixo + "2";

	/**
	 * Mensagem exibida quando a pessoa selecionada j� � um tutor.<br/><br/>
	 * 
	 * Conte�do: "A pessoa selecionada j� � um tutor."<br/>
	 * Tipo: Erro
	 */
	public static final String PESSOA_JA_EH_TUTOR = prefixo + "3";

	/**
	 * Mensagem exibida quando o operador digita uma hora inicial maior que a final.<br/><br/>
	 * 
	 * Conte�do: "A hora inicial deve ser menor que a hora final."<br/>
	 * Tipo: Erro
	 */
	public static final String HORA_INICIAL_MENOR_QUE_FINAL = prefixo + "4";

	/**
	 * Mensagem exibida quando o operador est� tentando cadastrar um hor�rio j� cadastrado para o tutor.<br/><br/>
	 * 
	 * Conte�do: "O hor�rio informado j� foi adicionado � lista de hor�rios do tutor."<br/>
	 * Tipo: Erro
	 */
	public static final String HORARIO_JA_INFORMADO = prefixo + "5";

	/**
	 * Mensagem exibida quando o operador est� tentando cadastrar um login com espa�o para o tutor.<br/><br/>
	 * 
	 * Conte�do: "O login do usu�rio n�o deve possuir espa�o."<br/>
	 * Tipo: Erro
	 */
	public static final String LOGIN_COM_ESPACO = prefixo + "6";

}