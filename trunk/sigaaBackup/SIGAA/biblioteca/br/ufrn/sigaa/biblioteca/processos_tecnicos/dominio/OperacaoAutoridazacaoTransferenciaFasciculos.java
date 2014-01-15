/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/05/2012
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

/**
 * 
 * <p> As opera��es que o usu�rio pode fazer na autoriza��o da tansfer�ncia dos fasc�culos. </p>
 *
 * @author jadson
 *
 */
public enum OperacaoAutoridazacaoTransferenciaFasciculos {
	
	/** Constante que indica que o usu�rio selecionou a op��o de autorizar a transfer�ncia */
	SIM, 
	/** Constante que indica que o usu�rio selecionou a op��o de negar a transfer�ncia */
	NAO, 
	/** Constante que indica que o usu�rio quer que o fasc�culo continue pendente aguardando na listagem de pendentes */
	AGUARDAR;
}
