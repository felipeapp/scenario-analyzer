package br.ufrn.arq.chat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.dao.UsuarioDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Servlet usada para registrar a entrada em um Chat.
 * 
 * 
 * @author Gleydson
 *
 */
public class EntrarChatServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	
		int idChat = new Integer(req.getParameter("idchat"));
		int idUsuario = new Integer(req.getParameter("idusuario"));
		String origem = req.getParameter("origem"); // de onde o usuário originou a abertura da sala de chat (portal discente ou turma vitual, por exemplo)
		boolean ministrante = Boolean.valueOf(req.getParameter("ministrante"));
		
		String passKey = req.getParameter("passkey");
		
		// recuperar usuário do banco
		UsuarioDAOImpl dao = new UsuarioDAOImpl(); 
		try {
			UsuarioGeral usuario = dao.findByPrimaryKey(idUsuario);
			
			// aplicar o passkey e comparar com o informado
			String generatedPassKey = ChatEngine.generatePassKey(ChatEngine.usuarioGeralParaChat(usuario), idChat);
			if (generatedPassKey.equals(passKey)) {
				// setar em sessão o atribudo userChat
				req.getSession().setAttribute("usuarioChat", usuario);
				// deixando a ultima mensagem vazia
				req.getSession().removeAttribute("lastMsgId_" + idChat);
				
				req.getSession().setAttribute("origem", origem); // usado em /chat/index.jsp para recarregar ou não o portal discente
				
				if (ministrante)
					ChatEngine.registrarMinistrante(usuario, idChat);
				
				// redicionar para o chat
				String auxV = req.getParameter("video");
				String redirect = "/chat/index.jsp";
				if (!StringUtils.isEmpty(auxV) && auxV.equals("true"))
					redirect = "/chat/indexVideo.jsp";
					
				req.getRequestDispatcher(redirect).forward(req, resp);
			} else
				System.out.println(generatedPassKey + " - " + passKey);
		} catch (DAOException e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		
	}	
	
}
