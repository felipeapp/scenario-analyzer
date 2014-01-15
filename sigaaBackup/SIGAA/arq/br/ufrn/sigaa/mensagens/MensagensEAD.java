/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/02/2010
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface que guarda as constantes de mensagens do subsistema de Educação à Distância.
 * 
 * @author Fred_Castro
 */

public interface MensagensEAD {
	/**
	 * Prefixo para estas mensagens. Atualmente 2_11200_
	 */
	public static final String prefixo = Sistema.SIGAA + "_" + SigaaSubsistemas.SEDIS.getId() + "_";
	
	/**
	 * Mensagem exibida quando o usuário tenta selecionar um curso que não existe no polo.<br/><br/>
	 * 
	 * Conteúdo: "O curso selecionado não existe neste pólo."<br/>
	 * Tipo: Erro
	 */
	public static final String CURSO_NAO_EXISTE = prefixo + "1";

	/**
	 * Mensagem exibida quando o tutor selecionado é inválido.<br/><br/>
	 * 
	 * Conteúdo: "O tutor selecionado não é válido. Por favor, reinicie a operação."<br/>
	 * Tipo: Erro
	 */
	public static final String TUTOR_INVALIDO = prefixo + "2";

	/**
	 * Mensagem exibida quando a pessoa selecionada já é um tutor.<br/><br/>
	 * 
	 * Conteúdo: "A pessoa selecionada já é um tutor."<br/>
	 * Tipo: Erro
	 */
	public static final String PESSOA_JA_EH_TUTOR = prefixo + "3";

	/**
	 * Mensagem exibida quando o operador digita uma hora inicial maior que a final.<br/><br/>
	 * 
	 * Conteúdo: "A hora inicial deve ser menor que a hora final."<br/>
	 * Tipo: Erro
	 */
	public static final String HORA_INICIAL_MENOR_QUE_FINAL = prefixo + "4";

	/**
	 * Mensagem exibida quando o operador está tentando cadastrar um horário já cadastrado para o tutor.<br/><br/>
	 * 
	 * Conteúdo: "O horário informado já foi adicionado à lista de horários do tutor."<br/>
	 * Tipo: Erro
	 */
	public static final String HORARIO_JA_INFORMADO = prefixo + "5";

	/**
	 * Mensagem exibida quando o operador está tentando cadastrar um login com espaço para o tutor.<br/><br/>
	 * 
	 * Conteúdo: "O login do usuário não deve possuir espaço."<br/>
	 * Tipo: Erro
	 */
	public static final String LOGIN_COM_ESPACO = prefixo + "6";

}