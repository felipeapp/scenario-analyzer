/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 17/09/2008 
 */
package br.ufrn.sigaa.ava.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.sigaa.ava.jsf.TopicoAulaMBean;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Action para associar um arquivo do porta arquivos a uma turma. Se uma
 * turma virtual tiver sido selecionada, pega a turma selecionada. Caso contrário, 
 * vai para página de escolha da turma.
 * 
 * @author David Pereira
 *
 */
public class AssociarArquivoTurmaAction extends AbstractAction {

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
		
		Boolean paComTurma = new Boolean((String) req.getSession().getAttribute("paComTurma"));
				
		if (!paComTurma) {
			req.getSession().setAttribute("idArquivo", req.getParameter("id"));
			return mapping.findForward("selecionaTurma");
		} else {
			res.sendRedirect("/sigaa/ava/PortaArquivos/associar.jsf?id=" + req.getParameter("id"));
		}
		
		return null;
	}

	/**
	 * Caso a turma ainda não tenha sido escolhida, exibe a página para se selecionar uma turma virtual.
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward escolher(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		TurmaVirtualMBean tv = new TurmaVirtualMBean();
		Turma t = getGenericDAO(req).findByPrimaryKey(new Integer(req.getParameter("id")), Turma.class);
		tv.setTurma(t);
		req.getSession().setAttribute("turmaVirtual", tv);
		
		TopicoAulaMBean ta = new TopicoAulaMBean();
		req.getSession().setAttribute("topicoAula", ta);
		
		res.sendRedirect("/sigaa/ava/PortaArquivos/associar.jsf?id=" + req.getParameter("idArquivo"));
		return null;
	}
	
}
