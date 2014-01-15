/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '30/01/2007'
 *
 */
package br.ufrn.sigaa.arq.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.sigaa.arq.dao.CursoDao;

/**
 * Servlet que busca as informações dos cursos por AJAX
 *
 * @author Leonardo
 *
 */
public class CursoServlet extends SigaaAjaxServlet {

	/* (non-Javadoc)
	 * @see org.ajaxtags.servlets.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public String getXmlContent(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		CursoDao dao = null;
		try {

			String nome = findParametroLike("nome", req);

			String nivel = findParametroLike("nivel", req);

			dao = new CursoDao();
			String resultado;

			// Create xml schema
			if( !nivel.equals("S") && !nivel.equals("G")){
				resultado = new AjaxXmlBuilder().addItems(dao.findByNome(nome, 0, null, nivel.charAt(0), null),
						"nome", "id").toString();
			}else if( nivel.equals("G")){
				resultado = new AjaxXmlBuilder().addItems(dao.findByNome(nome, 0, null, nivel.charAt(0), null),
						"descricao", "id").toString();
			}else {
				resultado = new AjaxXmlBuilder().addItems(dao.findByNome(nome, 0, null, nivel.charAt(0), null),
						"nomeCursoStricto", "id").toString();
			}

			return resultado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}
	}

}
