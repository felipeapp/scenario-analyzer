/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 17/09/2012 
 */
package br.ufrn.sigaa.ava.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.web.struts.AbstractAction;

/**
 * Action para associar um vídeo do porta arquivos a uma turma. Se uma
 * turma virtual tiver sido selecionada, pega a turma selecionada. Caso contrário, 
 * vai para página de escolha da turma.
 * 
 * @author David Pereira
 *
 */
public class AssociarVideoTurmaAction extends AbstractAction {

	/**
	 * Associa o arquivo à turma selecionada.
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward associar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		req.getSession().setAttribute("idArquivo", req.getParameter("id"));
		Boolean paComTurma = new Boolean((String) req.getSession().getAttribute("paComTurma"));
		Boolean cadastrarVideoTurma = (Boolean) req.getSession().getAttribute("cadastrarVideoTurma");
		
		if (!paComTurma) {
			req.getSession().setAttribute("idArquivo", req.getParameter("id"));
			return mapping.findForward("selecionaVideo");
		}
		
		if (cadastrarVideoTurma == null || cadastrarVideoTurma)
			res.sendRedirect("/sigaa/ava/VideoTurma/novo.jsf");
		else
			res.sendRedirect("/sigaa/ava/VideoTurma/editar.jsf");


		return null;
	}
	
}
