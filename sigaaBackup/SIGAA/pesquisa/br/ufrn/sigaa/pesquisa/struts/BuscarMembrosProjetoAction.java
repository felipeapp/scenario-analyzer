/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/01/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.pesquisa.form.MembroProjetoServidorForm;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/**
 * Action responsável por buscar os membros de projetos de pesquisa
 *
 * @author ricardo
 *
 */
public class BuscarMembrosProjetoAction extends AbstractCrudAction {

	
	/**
	 * Buscar os membros associados aos projetos de acordo com os critérios de busca.
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward listar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(new int [] {SigaaPapeis.GESTOR_PESQUISA, SigaaPapeis.PORTAL_PLANEJAMENTO}, req);
		MembroProjetoDao dao = getDAO(MembroProjetoDao.class, req);
		MembroProjetoServidorForm membroForm = (MembroProjetoServidorForm) form;
		
		if (req.getParameter("popular") != null) {
			membroForm.getObj().getProjeto().setAno(CalendarUtils.getAnoAtual());
			req.setAttribute(mapping.getName(), membroForm);

			return mapping.findForward("listar");
		}
		ListaMensagens erros = new ListaMensagens();
		
		Integer idCoordenador = membroForm.getObj().getServidor().getId();
		ValidatorUtil.validateRequiredId(idCoordenador, "Membro de Projeto", erros);
		
		Collection<MembroProjeto> membros = new ArrayList<MembroProjeto>();
		
		// Verificar se foram detectados erros
		if (erros.isEmpty()) {
			membros = dao.findByServidor(membroForm.getObj().getServidor().getId());
		} else {
			addMensagens(erros.getMensagens(), req);
		}
		if (membros.isEmpty())
			addMensagemErro("Nenhum projeto encontrado para esse docente", req);
		
		req.setAttribute("lista", membros);
		req.setAttribute(mapping.getName(), membroForm);
		return mapping.findForward("listar");
	}
}
