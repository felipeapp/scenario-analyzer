/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 03/11/2006
 *
 */
package br.ufrn.sigaa.ensino.latosensu.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.ensino.latosensu.dominio.NoticiaLato;

/**
 * Ações de cadastro e visualização de notícias do lato sensu
 *
 * @author Leonardo
 *
 */
public class NoticiaLatoAction extends AbstractCrudAction {

	public ActionForward verNoticia(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		GenericDAO dao = getGenericDAO(req);
		int id = RequestUtils.getIntParameter(req, "id");

		try {
			NoticiaLato noticia = dao.findByPrimaryKey(id, NoticiaLato.class);
			req.setAttribute("noticia", noticia);
		} finally {
			dao.close();
		}

		return mapping.findForward("noticia");
	}
}
