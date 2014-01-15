/*
 * BuscaEtiquetaServlet.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.servlets;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.sigaa.arq.ajax.SigaaAjaxServlet;
import br.ufrn.sigaa.arq.dao.biblioteca.EtiquetaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;

/**
 *
 * Servlet que busca por Etiquetas dados um tipo de etiqueta, tipo de campo e uma substring da tag
 *
 * @author Fred
 * @since 26/09/2008
 * @version 1.0 Criação da classe
 *
 */
public class BuscaEtiquetaServlet extends SigaaAjaxServlet{

	@Override
	public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception {

		EtiquetaDao dao = DAOFactory.getInstance().getDAO(EtiquetaDao.class, null, null);
		AjaxXmlBuilder builder = new AjaxXmlBuilder();

		try {
			
			String tag = findParametroLike("tagEtiquetaBuscada", request).toUpperCase();
			short tipoEtiqueta = Short.parseShort(findParametroLike("tipoEtiqueta", request));
			char tipoCampo = findParametroLike("tipoCampo", request).toCharArray()[0];
			
			System.out.println(tipoCampo + " -> " + tag + " -> " + tipoEtiqueta);
			
			Collection <Etiqueta> dados = dao.findEtiquetasByTipoCampoAndTipoAndTagLikeAtivas(tipoCampo, tag, tipoEtiqueta);

			builder.addItems(dados, "tag", "id");
			return builder.toString();

		} finally {
			if (dao != null)
				dao.close();
		}
	}
}
