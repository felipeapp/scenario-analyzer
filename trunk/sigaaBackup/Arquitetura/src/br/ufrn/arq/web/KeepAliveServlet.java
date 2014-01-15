/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/04/2010
 */
package br.ufrn.arq.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet para manter a sessão ativa. Utilizado
 * pela tag <ufrn:keepAlive/>.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class KeepAliveServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		StringBuilder texto = new StringBuilder("<small><em>Tempo de Sess&atilde;o:</em> <span id=\"spanRelogio\" title=\"Tempo restante para a expira&ccedil;&atilde;o da sess&atilde;o.\">");
		
		int min = req.getSession().getMaxInactiveInterval();
		int hora = min / 60;
		min = min % 60;

		String horaStr = String.valueOf(hora);
		String minStr = String.valueOf(min);
		
		if (horaStr.length() == 1) horaStr = "0" + hora;
		if (minStr.length() == 1) minStr = "0" + min;
		texto.append(horaStr + ":" + minStr);
		texto.append("</span></small>");
		
		res.getWriter().println(texto);
	}
	
}
