/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '22/09/2006'
 *
 */
package br.ufrn.sigaa.arq.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.sigaa.arq.dao.PessoaDao;

/**
 * Servlet que busca as informações dos docentes por AJAX
 *
 * @author Gleydson
 *
 */
public class PessoaServlet extends SigaaAjaxServlet {

	/**
	 * @see org.ajaxtags.demo.servlet.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public String getXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		PessoaDao dao = null;
		try {

			String model  = findParametroLike("nome", request);
			String tipo = request.getParameter("tipo");

			dao = new PessoaDao();

			// Create xml schema
			return new AjaxXmlBuilder().addItems(dao.findByNomeTipo(model, tipo.charAt(0), null),
					"nome", "id").toString();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}
	}

}
