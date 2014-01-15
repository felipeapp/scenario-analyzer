/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Criado em: 2007/10/29
 */
package br.ufrn.comum.negocio;

import br.ufrn.arq.dominio.Comando;

/**
 * Enumerando os comandos do sistema comum.
 * 
 * @author David Pereira
 *
 */
public class ComumListaComando {

	public static final String PREFIX = "br.ufrn.shared.";
	
	/** Movimento de Liberação de um comando */
	public static final Comando PREPARE_MOVIMENTO = new Comando(50, null);

	/**
	 * AUTENTICACAO/AUTORIZACAO WIRELESS
	 */
	public static final Comando CADASTRAR_AUTENTICACAO_WIRELESS = new Comando(150, PREFIX + "wireless.negocio.ProcessadorAutenticacaoWireless");
	public static final Comando CADASTRAR_VISITANTE_EXT_WIRELESS = new Comando(151, PREFIX + "wireless.negocio.ProcessadorVisitanteExtWireless");
	
}
