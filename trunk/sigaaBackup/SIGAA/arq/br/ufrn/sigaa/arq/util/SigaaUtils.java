/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 12/12/2007
 */
package br.ufrn.sigaa.arq.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Classe com m�todos utilit�rios usados no SIGAA
 * 
 * @author David Pereira
 *
 */
public class SigaaUtils {

	/**
	 * Utilizado para remover alguns managed beans de sess�o 
	 * quando o usu�rio clica em um link que n�o chama o 
	 * m�todo resetBean() desses beans. 
	 */
	public static void clearSessionWEB(HttpServletRequest req) {
		req.getSession().removeAttribute("consolidarTurma");
	}
	
}
