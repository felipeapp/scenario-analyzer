/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '19/10/2006'
 *
 */
package br.ufrn.sigaa.ensino.tecnico.web.servlet;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.sigaa.arq.ajax.SigaaAjaxServlet;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.tecnico.dao.DiscenteTecnicoDao;

/**
 * Servlet que busca as informações de discentes por ajax
 * @author Rafael
 *
 */
public class DiscenteTecnicoServlet extends SigaaAjaxServlet {

	/**
	 * @see org.ajaxtags.demo.servlet.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public String getXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		DiscenteTecnicoDao dao = new DiscenteTecnicoDao();
		try {
			String nome  = null;
			Enumeration parametros = request.getParameterNames();
			while ( parametros.hasMoreElements() ) {
				String parameter = (String) parametros.nextElement();
				if ( parameter.indexOf("pessoa.nome") != -1) {
					nome = request.getParameter(parameter);
					break;
				}
			}

			Unidade unid = ((Usuario)request.getSession().getAttribute("usuario")).getUnidade();

			// Create xml schema
			return new AjaxXmlBuilder().addItems(dao.findByNome(nome, unid.getId(), getNivelEnsino(request), null),
					"matriculaNome", "id").toString();
		} catch (Exception e) {
			e.printStackTrace();

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Erro ao buscar discente. Contacte o administrador do sistema.");
			response.flushBuffer();
			return null;
		} finally {
			if (dao != null)
				dao.close();
		}
	}

}
