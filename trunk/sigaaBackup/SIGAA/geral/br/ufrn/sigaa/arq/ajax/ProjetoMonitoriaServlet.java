/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on Mar 28, 2007
 *
 */
package br.ufrn.sigaa.arq.ajax;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;

/**
 * Servlet utilizado em campos autocomplete de Projeto de Monitoria
 * 
 * @author Victor Hugo
 *
 */
public class ProjetoMonitoriaServlet extends SigaaAjaxServlet {

	@Override
	public String getXmlContent(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		
		ProjetoMonitoriaDao dao = DAOFactory.getInstance().getDAO( ProjetoMonitoriaDao.class );
		
		try {
			String trecho  = findParametroLike("tituloProjeto", req);
			//String grupo = req.getParameter("grupo");

			/*Integer idGrupo = null;
			try {
				idGrupo = Integer.parseInt( grupo );
			} catch (Exception e) { }
			 */
			
			Collection lista = dao.findByTrecho(trecho);
			return new AjaxXmlBuilder().addItems(lista, "centroAnoTitulo", "id").toString();
		} finally {
			dao.close();
		}
	}

}