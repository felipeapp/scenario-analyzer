/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '23/03/2007'
 *
 */
package br.ufrn.sigaa.arq.ajax;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.sigaa.arq.dao.pesquisa.LinhaPesquisaDao;

/**
 * Servlet utilizado em campos autocomplete de Linhas de Pesquisa
 *
 * @author ricardo
 *
 */
public class LinhaPesquisaServlet extends SigaaAjaxServlet {

	@Override
	public String getXmlContent(HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		LinhaPesquisaDao linhaPesquisaDao = new LinhaPesquisaDao();

		try {
			String trecho  = findParametroLike("linha", req);
			String grupo = req.getParameter("grupo");

			Integer idGrupo = null;
			try {
				idGrupo = Integer.parseInt( grupo );
			} catch (Exception e) { }

			Collection lista = linhaPesquisaDao.findByTrecho(trecho, idGrupo);
			return new AjaxXmlBuilder().addItems(lista, "nome", "id").toString();
		} finally {
			linhaPesquisaDao.close();
		}
	}

}
