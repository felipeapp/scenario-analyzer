/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '21/09/2006'
 *
 */
package br.ufrn.sigaa.arq.ajax;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Servlet que busca os municípios de um estado por AJAX
 *
 * @author Andre Dantas
 *
 */
public class MunicipioServlet extends SigaaAjaxServlet {

	/**
	 * @see org.ajaxtags.demo.servlet.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception {

		if ("findByNome".equalsIgnoreCase(request.getParameter("buscaTipo")))
			return findByNome(request, response);

		MunicipioDao dao = new MunicipioDao();
		try {
			String ufId = request.getParameter("ufId");
			int id = Integer.parseInt(ufId);
			UnidadeFederativa uf = new UnidadeFederativa(id);
			uf = dao.findByPrimaryKey(uf.getId(), UnidadeFederativa.class);
			ArrayList<Municipio> municipios = new ArrayList<Municipio>();
			// o primeiro município é sempre a capital do estado.
			municipios.add(uf.getCapital());
			municipios.addAll(dao.findByUF(uf.getId()));

			// Create xml schema
			return new AjaxXmlBuilder().addItems(municipios, "nome", "id").toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}
	}

	private String findByNome(HttpServletRequest request, HttpServletResponse response) {
		MunicipioDao dao = new MunicipioDao();
		try {
			String nome = request.getParameter("form:municipioNome");
			return new AjaxXmlBuilder().addItems(dao.findByNome(nome), "nomeUF", "id").toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}
	}
}
