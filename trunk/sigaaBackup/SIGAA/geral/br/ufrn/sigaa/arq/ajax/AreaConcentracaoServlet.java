/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '05/03/2007'
 *
 */
package br.ufrn.sigaa.arq.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;

/**
 * Servlet que busca informações de Áreas de Concentração por AJAX
 * 
 * @author Leonardo
 *
 */
public class AreaConcentracaoServlet extends SigaaAjaxServlet {

	@Override
	public String getXmlContent(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		GenericDAO dao = new GenericDAOImpl(Sistema.SIGAA);
		try {

			String nome = findParametroLike("nome", req);
			
			// Create xml schema
			return new AjaxXmlBuilder().addItems(dao.findByLikeField(AreaConcentracao.class, "denominacao", nome),
					"denominacao", "id").toString();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}
	}

	
	
}
