package br.ufrn.arq.chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Servlet que recupera as mensagens de um determinado Chat
 * 
 * @author Gleydson
 * 
 */
@SuppressWarnings("serial")
public class ReceiveChatServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		int idChat = new Integer(req.getParameter("idchat"));

		resp.setContentType("Content-Type: text/html; charset=utf-8");
		PrintWriter out = resp.getWriter();

		/**
		 * Usuário logado
		 */
		UsuarioGeral user = (UsuarioGeral) req.getSession().getAttribute("usuarioChat");

		if (user == null || !ChatEngine.isUsuarioOnlineChat(user.getId(), idChat)) {
			return;
		} else {

			synchronized (req.getSession()) {
				System.out.println("Entrando na região crítica");
				/**
				 * Última mensagem baixada
				 */
				Long idMsg = (Long) req.getSession().getAttribute(
						"lastMsgId_" + idChat);
				if (idMsg == null)
					idMsg = (long) 1;

				/**
				 * Recupera as mensagens do chat tendo a ultima mensagem como a
				 * base
				 */
				List<ChatMessage> msgs = ChatEngine.recoveryMessages(idChat,
						idMsg);

				long ultimaMsg = 0;
				if (idMsg == 1) {
					System.out.println("Sincronizando ultima mensagem");

					if (msgs.size() > 0) {
						ultimaMsg = msgs.get(msgs.size() - 1).getIdMessage();
					}

					req.getSession(true).setAttribute("lastMsgId_" + idChat,
							ultimaMsg);

				} else {

					for (ChatMessage msg : msgs) {
						if (msg.getIdUsuario() != user.getId()) {
							out.println("<li>");
							out.println("<h3>" + msg.getNomeUsuario()
									+ "<span class='diz'> diz:</span></h3>"
									+ " <p class='texto'>" + StringUtils.converteJavaToHtml(msg.getMensagem())
									+ "</p>");
							out.println("</li>");
							req.getSession(true).setAttribute(
									"lastMsgId_" + idChat, msg.getIdMessage());
						}
					}
				}
			}

			out.close();

			System.out.println("Saindo na região crítica");

		}

	}

}
