package br.ufrn.comum.wireless;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.log.Logger;
import br.ufrn.arq.usuarios.UserAutenticacao;

/**
 * Servlet para autenticação dos usuários da redes wireless
 *
 * @author Gleydson
 *
 */
public class AutenticaServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String login = req.getParameter("login");
		String senha = req.getParameter("senha");

		// autorizado
		boolean autorizado = false;
		// autenticado
		boolean autenticado = false;

		Integer idUsuario = null;
		
		try {
			autenticado = UserAutenticacao.autenticaUsuario(req, login, senha);
			idUsuario = UserAutenticacao.getIdUsuario(req, login);
		} catch(ArqException e) {
			e.printStackTrace();
		}

		// usuário e senha correto
		if (autenticado) {

			// verifica se pode autorizar
			try {
				autorizado = ConexaoWAS.autenticar(req.getRemoteAddr(), idUsuario);
			} catch (SQLException e) {
				autorizado = false;
				Logger.error("Falha na autorização Wireless (IP: " + req.getRemoteAddr() + "):" + e.getMessage());
				e.printStackTrace();
			}

			if (autorizado) {
				res.sendRedirect("/admin/wireless/autenticado.jsf");
				return;
			} else {
				Logger.error("Falha na autorização Wireless (IP: " + req.getRemoteAddr() + ")");
			}
		}

		if (!autorizado || !autenticado) {
			res.sendRedirect("/admin/wireless/index.jsf?erro=true");
			return;
		}

	}

}
