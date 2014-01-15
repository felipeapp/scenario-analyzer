/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '13/12/2006'
 *
 */
package br.ufrn.sigaa.arq.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.TurmaEntradaLatoDao;

/**
 * Servlet que busca disciplinas ou turmas de entrada pelo curso de lato sensu.
 *
 * @author Leonardo
 *
 */
public class CursoLatoAjax extends SigaaAjaxServlet {

	public String getXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if (request.getParameter("tipo").equals("disciplina")) {
			return getDisciplinasXmlContent(request, response);
		} else {
			return getTurmasEntradaXmlContent(request, response);
		}

	}

	public String getDisciplinasXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ComponenteCurricularDao dao = new ComponenteCurricularDao();
		try {
			String idString = findParametroLike("id", request);
			if(idString == null || "".equals(idString.trim()))
				return null;
			
			int idCurso = new Integer(idString);

			// Create xml schema
			return new AjaxXmlBuilder().addItems( dao.findByCursoLato(idCurso), "codigoNome", "id").toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}
	}

	public String getTurmasEntradaXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		TurmaEntradaLatoDao dao = new TurmaEntradaLatoDao();

		try {
			int idCurso = new Integer(findParametroLike("id", request));

			// Create xml schema
			return new AjaxXmlBuilder().addItems(dao.findByCursoLato(idCurso, true), "descricao", "id")
					.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}
	}

}
