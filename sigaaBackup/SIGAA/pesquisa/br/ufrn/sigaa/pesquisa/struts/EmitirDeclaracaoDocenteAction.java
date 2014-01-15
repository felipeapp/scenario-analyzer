/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/07/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroProjetoDiscenteDao;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.form.MembroProjetoServidorForm;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/**
 * Action respons�vel pela emiss�o de declara��es para docentes associados
 * a planos de trabalho de pesquisa
 * @author leonardo
 *
 */
public class EmitirDeclaracaoDocenteAction extends AbstractCrudAction {

	/**
	 * Busca um docente coordenador do projeto de pesquisa e emite a declara��o
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward emitirDeclaracaoCoordenacao(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		MembroProjetoServidorForm membroForm = (MembroProjetoServidorForm) form;
		
		MembroProjeto membro = getGenericDAO(req).findByPrimaryKey(membroForm.getObj().getId(), MembroProjeto.class);
		
		if (membro != null) {
			req.setAttribute("membro", membro);
			req.setAttribute("dataAtual", new Date());
			
			return mapping.findForward("declaracaoCoordenacao");
		} else {
			addMensagemErro("� necess�rio informar um docente para e emiss�o da declara��o", req);
			return mapping.findForward("listar");
		}
		
	}
	
	/**
	 * Emiss�o da declara��o de orienta��es.
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward emitirDeclaracaoOrientacoes(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		MembroProjetoServidorForm membroForm = (MembroProjetoServidorForm) form;
		MembroProjetoDiscenteDao dao = getDAO(MembroProjetoDiscenteDao.class, req);
		
		MembroProjeto membro = dao.findByPrimaryKey(membroForm.getObj().getId(), MembroProjeto.class);
		
		if (membro != null) {
			req.setAttribute("membro", membro);
			
			Collection<MembroProjetoDiscente> orientandos = dao.findByOrientador(membro.getServidor().getId(), false);
			
			if(orientandos != null)
				req.setAttribute("orientandos", orientandos);
			
			return mapping.findForward("declaracaoOrientacoes");
		} else {
			addMensagemErro("� necess�rio informar um docente para e emiss�o da declara��o", req);
			return mapping.findForward("listar");
		}
		
	}
	
}
