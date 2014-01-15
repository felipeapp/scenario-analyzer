/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 24/042/2009 
 *
 */
package br.ufrn.sigaa.portal.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.sigaa.arq.dao.site.DetalhesSiteDao;
import br.ufrn.sigaa.arq.struts.SigaaServlet;

/**
 * Servlet que busca o site do departamento correspondente para redirecionar.
 * 
 * @author Mário Rizzi Rocha
 * 
 */
@SuppressWarnings("serial")
public class RedirectSiteDepartamento extends SigaaServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String nomeSite = req.getParameter("sigla");
		
		if ( nomeSite == null || nomeSite.trim().equals("") ) {
			resp.sendRedirect(RepositorioDadosInstitucionais.getLinkSigaa() + "/sigaa/public/departamento/lista.jsf");
			return;
		}

		DetalhesSiteDao dao = new DetalhesSiteDao();
		try {
			Integer idDepto = dao.findBySite(nomeSite);
			if ( idDepto == null ) {
				resp.getWriter().println("Site do departamento não publicado ou não encontrado");
			} else {
				resp.sendRedirect("http://" + req.getServerName()
					+ "/sigaa/public/departamento/portal.jsf?id=" + idDepto);
			}
		} catch(Exception e ) {
			resp.getWriter().println("Erro ao redirecionar: " + e.getMessage());
		} finally {
			dao.close();
		}

	}

}
