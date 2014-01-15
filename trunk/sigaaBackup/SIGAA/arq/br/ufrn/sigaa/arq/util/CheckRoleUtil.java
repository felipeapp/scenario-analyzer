/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/20 - 14:51:28
 */
package br.ufrn.sigaa.arq.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.SkipPageException;

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Classe utilitária para verificação de permissão em uma JSP
 * 
 * @author David Pereira
 *
 */
public class CheckRoleUtil {

	public static void checkRole(HttpServletRequest req, HttpServletResponse res, int papel) throws ServletException, IOException, SkipPageException {
		checkRole(req, res, new int[] { papel });
	}
	
	public static void checkRole(HttpServletRequest req, HttpServletResponse res, int... papeis) throws ServletException, IOException, SkipPageException {
		Usuario usuario = (Usuario) req.getSession().getAttribute("usuario");
		
		try {
			UFRNUtils.checkRole(usuario, papeis);
		} catch (SegurancaException e) {
			req.getRequestDispatcher("/WEB-INF/jsp/include/erros/autorizacao.jsp").forward(req, res);
			throw new SkipPageException();
		}
		
	
	}
	
}
