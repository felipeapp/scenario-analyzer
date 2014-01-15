/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '27/04/2007'
 *
 */
package br.ufrn.sigaa.arq.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.arq.util.StringUtils;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dao.UsuarioDao;

/**
 * Servlet utilizada no autocomplete dos formulários no sistema.
 * Caso algum parâmetro seja alterado na servlet, realizar a mesma alteração nas JSP's.
 * @author leonardo
 * @author mario
 *
 */
public class UsuarioServlet extends SigaaAjaxServlet {

	/* (non-Javadoc)
	 * @see org.ajaxtags.servlets.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public String getXmlContent(HttpServletRequest req,	HttpServletResponse res) throws Exception {

		UsuarioDao dao = null;
		boolean somenteServidores = false;
		Integer categoria = null;
		
		try {
			
			dao = new UsuarioDao();

			String nome = findParametroLike("nome", req);
			String docente = req.getParameter("docente");
			String servidor = req.getParameter("servidor");
			
			//Somente servidores docente
			if (docente != null && Boolean.parseBoolean(docente)){
				somenteServidores = true;
				categoria = Categoria.DOCENTE;
			}else if (servidor != null && Boolean.parseBoolean(servidor)){
				somenteServidores = true;
			}
			
			return new AjaxXmlBuilder().addItems( 
					dao.findByNome(StringUtils.toAscii(nome), somenteServidores, categoria),
					"loginNome", "id").toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			dao.close();
		}
	}

}
