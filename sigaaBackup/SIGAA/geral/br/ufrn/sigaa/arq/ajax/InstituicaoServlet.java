/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '04/04/2007'
 *
 */
package br.ufrn.sigaa.arq.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.sigaa.arq.dao.InstituicoesEnsinoDao;

/**
 * Servlet que retorna ao ajaxtags o resultado de busca de instituições de ensino
 * @author Andre M Dantas
 *
 */
public class InstituicaoServlet extends SigaaAjaxServlet {

	public String getXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		InstituicoesEnsinoDao dao = new InstituicoesEnsinoDao();
		try {
			String n = request.getParameter("form:instituicaoNome");
			// Create xml schema
			return new AjaxXmlBuilder().addItems(dao.findByNome(n),
					"descricao", "id").toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}
	}

}

