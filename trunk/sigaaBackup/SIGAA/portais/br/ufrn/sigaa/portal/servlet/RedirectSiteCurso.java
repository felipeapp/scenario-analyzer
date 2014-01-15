/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 10/12/2008 
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
import br.ufrn.sigaa.sites.dominio.TipoPortalPublico;

/**
 * Servlet que busca o site do programa correspondente para redirecionar.
 * 
 * @author sist-sigaa-12
 *
 */
@SuppressWarnings("serial")
public class RedirectSiteCurso extends SigaaServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String nomeSite = req.getParameter("sigla");
		
		if ( nomeSite == null || nomeSite.trim().equals("") ) {
			resp.sendRedirect(RepositorioDadosInstitucionais.getLinkSigaa() + "/sigaa/public/curso/lista.jsf");
			return;
		}

		DetalhesSiteDao dao = new DetalhesSiteDao();
		try {
			Integer idCurso = dao.findBySite(nomeSite, TipoPortalPublico.CURSO);
			if ( idCurso == null ) {
				resp.getWriter().println("Site do curso não encontrado.");
			} else {
				resp.sendRedirect("http://" + req.getServerName()
					+ "/sigaa/public/curso/portal.jsf?id=" + idCurso);
			}
		} catch(Exception e ) {
			resp.getWriter().println("Erro ao redirecionar: " + e.getMessage());
		} finally {
			dao.close();
		}

	}

}
