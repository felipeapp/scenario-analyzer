/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 13/03/2009 
 *
 */
package br.ufrn.sigaa.portal.jsf;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.struts.SigaaServlet;
import br.ufrn.sigaa.parametros.dominio.ParametrosPortalPublico;

/**
 * Servlet Usada para redirecionar para a página do professor a partir do
 * usuário.
 * 
 * @author Gleydson
 * 
 */
@SuppressWarnings("serial")
public class SiteDocenteRedirector extends SigaaServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		List<Integer> situacoes = Arrays.asList( ParametroHelper.getInstance().getParametroIntegerArray( ParametrosPortalPublico.SITUACAO_SERVIDOR_ACESSO_PORTAL_DOCENTE )  ); 
		
		/*String url = req.getRequestURL().toString();
		int ultimaBarra = url.lastIndexOf('/');
		String login = url.substring(ultimaBarra + 1);*/
		
		String login = req.getParameter("login");
		if( ValidatorUtil.isEmpty(login)) {
			res.sendRedirect("/sigaa/public/docente/busca_docentes.jsf?aba=p-academico");
			return;
		}
			

		ServidorDao dao = new ServidorDao();
		try {
			Integer siape = dao.findSiapeByLogin(login, situacoes);
			if (siape != null) {
					res.sendRedirect("/sigaa/public/docente/portal.jsf?siape="
							+ siape);
			} else {
				res.getWriter().println("Site do docente não encontrado");
			}
		} catch (DAOException e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

	}

}
