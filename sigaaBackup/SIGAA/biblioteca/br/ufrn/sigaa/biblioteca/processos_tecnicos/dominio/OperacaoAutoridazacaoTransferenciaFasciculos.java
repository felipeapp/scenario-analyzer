/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/05/2012
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

/**
 * 
 * <p> As operações que o usuário pode fazer na autorização da tansferência dos fascículos. </p>
 *
 * @author jadson
 *
 */
public enum OperacaoAutoridazacaoTransferenciaFasciculos {
	
	/** Constante que indica que o usuário selecionou a opção de autorizar a transferência */
	SIM, 
	/** Constante que indica que o usuário selecionou a opção de negar a transferência */
	NAO, 
	/** Constante que indica que o usuário quer que o fascículo continue pendente aguardando na listagem de pendentes */
	AGUARDAR;
}
