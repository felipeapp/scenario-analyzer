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

import br.ufrn.arq.util.StringUtils;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;

/**
 * Servlet que busca as informações dos servidores por AJAX
 *
 * @author Gleydson
 *
 */
public class ServidorServlet extends SigaaAjaxServlet {

	/**
	 * @see org.ajaxtags.demo.servlet.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public String getXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ServidorDao dao = null;
		try {

			String nome = findParametroLike("nome", request);
			int unidade = 0;
			int categoria = "tecnico".equals(request.getParameter("categoria")) ? Categoria.TECNICO_ADMINISTRATIVO : 0;
			String tipo = request.getParameter("tipo");
			String inativos = request.getParameter("inativos");
			boolean areaPublica = Boolean.valueOf(request.getParameter("areaPublica"));
			boolean unidadeGestora = false;

			// se a busca for por unidade, pega a unidade do usuário logado.
			if (!"todos".equalsIgnoreCase(tipo)) {
				if ("centro".equals(tipo)) {
					unidade = getUnidadeUsuario(request).getGestora().getId();
					unidadeGestora = true;
				} else {
					unidade = getUnidadeUsuario(request).getId();
				}
			}
			dao = new ServidorDao();			

			// Create xml schema
			return new AjaxXmlBuilder().addItems(
					dao.findByNome(StringUtils.toAscii(nome), unidade, inativos == null, unidadeGestora, categoria, areaPublica), 
						areaPublica ? "descricaoResumida" : "siapeNomeFormatado", "id").toString();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(dao!=null)
				dao.close();
		}
	}

}
