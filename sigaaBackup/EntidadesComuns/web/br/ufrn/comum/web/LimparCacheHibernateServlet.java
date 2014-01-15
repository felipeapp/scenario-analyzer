/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/06/2010
 */
package br.ufrn.comum.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.util.HibernateUtils;

/**
 * Servlet para limpar o cache do Hibernate nos projetos. Chamado
 * por uma operação do SIGAdmin.
 * 
 * @author David Pereira
 *
 */
public class LimparCacheHibernateServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String login = req.getParameter("login");
		String senha = req.getParameter("senha");
		
		try {
			if (UserAutenticacao.autenticaUsuario(req, login, senha)) {
				Integer sistema = Integer.valueOf(req.getParameter("sistema"));
				String entidade = req.getParameter("entidade");
				
				try {
					Class.forName(entidade);
				} catch (ClassNotFoundException e) {
					res.setStatus(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
				
				HibernateUtils.clearHibernateCache(DAOFactory.getInstance().getSessionFactory(sistema), entidade);
				
			} else {
				res.setStatus(HttpServletResponse.SC_FORBIDDEN);	
			}
		} catch (ArqException e) {
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
	}
	
}
