/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '27/09/2006'
 *
 */
package br.ufrn.sigaa.arq.ajax;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pesquisa.dominio.ProgramaEntidadeFinanciadora;

/**
 * Servlet que busca os programas por entidade financiadora
 *
 * @author Gleydson
 *
 */
public class ProjetoPesquisaAjax extends SigaaAjaxServlet {

	public String getXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if (request.getParameter("tipo").equals("programa")) {
			return getProgramasXmlContent(request, response);
		} else {
			return getAreasXmlContent(request, response);
		}

	}

	public String getProgramasXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		GenericDAO dao = new GenericDAOImpl(Sistema.SIGAA);
		try {
			int idEntidade = new Integer(findParametroLike("id", request));

			// Create xml schema
			return new AjaxXmlBuilder().addItems(
					dao.findByExactField(ProgramaEntidadeFinanciadora.class,
							"entidadeFinanciadora", idEntidade), "nome", "id")
					.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}
	}

	public String getAreasXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		AreaConhecimentoCnpqDao dao = new AreaConhecimentoCnpqDao();
		Collection retorno = null;
		try {
			String idString = findParametroLike("id", request);
			if (idString == null || "".equals(idString.trim()) ) {
				return "";
			}
			int idEntidade = new Integer(idString);

			String tipoArea = request.getParameter("tipoArea");

			if (tipoArea.equals("area")) {
				retorno = dao.findAreas(new AreaConhecimentoCnpq(idEntidade));
				return new AjaxXmlBuilder().addItems(retorno, "nome", "codigo")
				.toString();
			} else if (tipoArea.equalsIgnoreCase("subArea")) {
				retorno = dao.findSubAreas(idEntidade);
			} else if (tipoArea.equals("especialidade")) {
				retorno = dao.findEspecialidade(new AreaConhecimentoCnpq(
						idEntidade));
			}

			// Create xml schema
			return new AjaxXmlBuilder().addItems(retorno, "nome", "id")
					.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}
	}

}
