/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 08/12/2006
 *
 */
package br.ufrn.sigaa.arq.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.sigaa.arq.dao.UnidadeDao;

/**
 * Servlet que busca as informações das unidades por AJAX
 *
 * @author Leonardo
 *
 */
public class UnidadeServlet extends SigaaAjaxServlet {

	/* (non-Javadoc)
	 * @see org.ajaxtags.servlets.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public String getXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		UnidadeDao dao = null;
		try {

			String nome = findParametroLike("nome", request);

			String nivel = findParametroLike("nivel", request);

			dao = new UnidadeDao();

			// Create xml schema
			if(nivel!=null && nivel.equals("C")){ //unidades acadêmicas
   			    return new AjaxXmlBuilder().addItems(dao.findUnidadeAcademicaByNome(nome, null),
						"siglaNome", "id").toString();
			} else {
				return new AjaxXmlBuilder().addItems(dao.findByNome(nome),
					"siglaNome", "id").toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}
	}

}
