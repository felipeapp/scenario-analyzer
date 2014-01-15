/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Data de Criação: 19/08/2008 
 */
package br.ufrn.arq.chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.util.UFRNUtils;

/**
 * Servlet para listar os usuários de um chat.
 * 
 * @author David Pereira
 *
 */
public class ListMembersServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		int idChat = Integer.parseInt(req.getParameter("idchat"));
		List<UsuarioChat> lista = ChatEngine.listMembers(idChat);
		for (Iterator<UsuarioChat> it = lista.iterator(); it.hasNext(); ) {
			UsuarioChat usr = it.next();
			
			boolean videoChat = Boolean.valueOf(req.getParameter("videoChat"));
			
			out.println((videoChat && usr.isMinistrante() ? "[m]"  : "")+ "<li class=\"membro\">");
			if (usr.getIdFoto() == null)
				out.println("<img src=\"/shared/chat/images/foto-default.jpg\" width=\"40\"/>");
			else
				out.println("<img src=\"/sigaa/verFoto?idFoto=" + usr.getIdFoto() + "&key="+UFRNUtils.generateArquivoKey(usr.getIdFoto())+"\" width=\"40\"/>");
			
			StringBuffer nome = new StringBuffer();
		    for (int i = 0; i < usr.getNome().length(); i++){
		        char c = usr.getNome().charAt(i);
		        if (c > 127 || c=='"' || c=='<' || c=='>')
		        	nome.append("&#" + (int) c + ";");
		        else
		        	nome.append(c);
		    }
			
			out.println("<h4>" + nome.toString() + "</h4>");
			out.println("<p class=\"login\">" + usr.getLogin() + "</p>");
			out.println("</li>");
			out.flush();
		}
		
		out.close();
	}
	
}
