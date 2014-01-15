/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '11/01/2011'
 *
 */

package br.ufrn.sigaa.assistencia.jsf;

import br.ufrn.arq.erros.ArqException;

/**
 * Inteface para marcar os mbeans que precisam criar um fluxo alternativo para o
 * Caso de Uso de Aadesao ao Cadastro �nico
 * 
 * @author Henrique Andr�
 * 
 */
public interface AdesaoCadastroUnicoBolsaFluxoNavegacao {
	public String getUrlDestino() throws ArqException;
}
