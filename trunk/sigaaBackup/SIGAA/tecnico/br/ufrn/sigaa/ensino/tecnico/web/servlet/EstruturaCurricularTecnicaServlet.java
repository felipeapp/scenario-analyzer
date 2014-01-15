/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 10/12/2009
 *
 */
package br.ufrn.sigaa.ensino.tecnico.web.servlet;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.ajax.SigaaAjaxServlet;
import br.ufrn.sigaa.ensino.tecnico.dao.EstruturaCurricularTecDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.EstruturaCurricularTecnica;

/**
 * Servlet para buscar as estruturas curriculares de um determinado curso técnico por AJAX.
 * 
 * @author Leonardo Campos
 *
 */
public class EstruturaCurricularTecnicaServlet extends SigaaAjaxServlet {

	/**
	 * @see org.ajaxtags.demo.servlet.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public String getXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if ( getSubSistemaCorrente(request).equals(SigaaSubsistemas.TECNICO) )
			return buscarEstruturasCurricularesTecnicas(request, response);
		else
			return null;

	}

	/**
	 * Busca as estruturas curriculares a partir das informações passadas no request.
	 * @param request
	 * @param response
	 * @return
	 */
	private String buscarEstruturasCurricularesTecnicas(HttpServletRequest request, HttpServletResponse response) {

		EstruturaCurricularTecDao dao = new EstruturaCurricularTecDao();
		try {
			String param = request.getParameter("cursoId");
			AjaxXmlBuilder ajaxXmlBuilder = new AjaxXmlBuilder();
            if (param == null || param.trim().equals("")) {
					return ajaxXmlBuilder.addItems(new ArrayList<EstruturaCurricularTecnica>(), "descricao", "id").toString();
			}
			int id = Integer.parseInt(param);
			Collection<EstruturaCurricularTecnica> curriculos = dao.findByCursoTecnico(id);
			ajaxXmlBuilder.addItem("-- SELECIONE --", "0");
			return ajaxXmlBuilder.addItems(curriculos, "descricao", "id").toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}

	}

}
