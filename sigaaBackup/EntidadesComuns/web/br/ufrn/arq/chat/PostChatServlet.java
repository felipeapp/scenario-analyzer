package br.ufrn.arq.chat;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Servlet que possibilita o usuário enviar mensagens ao Chat.
 * 
 * @author Gleydson
 *
 */
public class PostChatServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	
		// recupera o chat que o usuário quer postar
		int idChat = new Integer(req.getParameter("idchat"));
		// recupera a mensagem
		String message = req.getParameter("msg");
		if (!isEmpty(message)) {
			// verifica se o usuário está logado e online no chat
			UsuarioGeral user = (UsuarioGeral) req.getSession().getAttribute("usuarioChat");
			if ( user == null || !ChatEngine.isUsuarioOnlineChat(user.getId(), idChat))
				return;
			
			ChatEngine.postMessage(idChat, ChatEngine.usuarioGeralParaChat(user), message, user.getIdFoto());
		}
	}
}
