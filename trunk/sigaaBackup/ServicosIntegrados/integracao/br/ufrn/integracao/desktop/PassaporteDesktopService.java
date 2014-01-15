/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 10/06/2009
 */
package br.ufrn.integracao.desktop;

import javax.jws.WebService;



/**
 * Servi�o para cria��o de passaporte para sistemas Desktop.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@WebService
public interface PassaporteDesktopService {

	/**
	 * <p>Cria um passaporte para uma aplica��o desktop.</p>
	 * 
	 * <p>IMPORTANTE: Mant�m a compatibilidade com a maneira antiga de ser criar o passaporte.</p>
	 * 
	 * @param idUsuario
	 * @param login
	 * @param sistemaOrigem
	 * @param sistemaDestino
	 */
	public void criarPassaporte(int idUsuario, String login, int sistemaOrigem, int sistemaDestino);
	
	
	/**
	 * <p>Cria um passaporte para uma aplica��o desktop.</p>
	 * 
	 * <p>IMPORTANTE: Nova maneira de se criar o passaporte, onde o redirecionamento s� ocorre depois que os v�nculos do usu�rio s�o tratados. <br/>
	 * � como se est� utilizando no SIGAA no momento. 21/09/2011 <br/> 
	 * </p>
	 * 
	 * @param idUsuario
	 * @param login
	 * @param sistemaOrigem
	 * @param sistemaDestino
	 * @param url
	 */
	public void criarPassaporteSigaa(int idUsuario, String login, int sistemaOrigem, int sistemaDestino, String url);
	
}
