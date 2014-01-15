/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '21/05/2007'
 *
 */
package br.ufrn.sigaa.arq.ajax;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.sigaa.arq.dao.pesquisa.ConsultorDao;

/**
 * Servlet que busca as informações dos consultores por AJAX
 *
 * @author Ricardo Wendell
 *
 */
public class ConsultorServlet extends SigaaAjaxServlet {

	/**
	 * @see org.ajaxtags.demo.servlet.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public String getXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ConsultorDao dao = null;
		try {

			/**
			 * Tipos: todos, internos, externos
			 */
			String tipo = findParametroLike("tipo", request);
			String nome = findParametroLike("nome", request);

			dao = new ConsultorDao();
			Collection lista = null;

			// Verificar a filtragem por tipo de consultor
			Boolean internos = null;
			if ( tipo != null && !"".equals(tipo) && !"todos".equals(tipo) ) {
				if (tipo.equals( "internos" )) {
					internos = true;
				} else {
					internos = false;
				}
			}

			// Buscar consultores
			lista = dao.findByNomeAndTipo(nome, internos);

			// Create xml schema
			return new AjaxXmlBuilder().addItems(lista, "nome", "id").toString();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}
	}

}
