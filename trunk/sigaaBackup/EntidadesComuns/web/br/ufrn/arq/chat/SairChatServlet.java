/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Data de Criação: 19/08/2008 
 */
package br.ufrn.arq.chat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Servlet para retirar um usuário de um chat
 * 
 * @author David Pereira
 *
 */
public class SairChatServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UsuarioGeral user = (UsuarioGeral) req.getSession().getAttribute("usuarioChat");
		if ( user == null ) {
			return;
		}
		
		int idChat = new Integer(req.getParameter("idchat"));
		ChatEngine.unregisterUser(ChatEngine.usuarioGeralParaChat(user), idChat);
		
		// Fechar a janela popup
		resp.setContentType("text/html");
		resp.getWriter().println("<script type='text/javascript'>");
		resp.getWriter().println("self.close();");
		resp.getWriter().println("</script>");
		resp.getWriter().flush();
		resp.getWriter().close();
	}
	
}
