/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 12/12/2007
 */
package br.ufrn.sigaa.arq.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Classe com métodos utilitários usados no SIGAA
 * 
 * @author David Pereira
 *
 */
public class SigaaUtils {

	/**
	 * Utilizado para remover alguns managed beans de sessão 
	 * quando o usuário clica em um link que não chama o 
	 * método resetBean() desses beans. 
	 */
	public static void clearSessionWEB(HttpServletRequest req) {
		req.getSession().removeAttribute("consolidarTurma");
	}
	
}
