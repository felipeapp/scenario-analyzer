/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/12/2006
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.form.MembroProjetoDiscenteForm;

/**
 * Action respons�vel pela emiss�o de declara��es para discentes associados
 * a planos de trabalho de pesquisa
 * 
 * @author ricardo
 *
 */
public class EmitirDeclaracaoDiscenteAction extends AbstractCrudAction {

	/**
	 * Solicita pelo discente a emiss�o de uma declara��o
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward solicitar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		return null;
	}
	
	/**
	 * Busca o discente associado a um plano de trabalho e emite a declara��o
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward emitir(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		MembroProjetoDiscenteForm membroForm = (MembroProjetoDiscenteForm) form;
		
		MembroProjetoDiscente membro = getGenericDAO(req).findByPrimaryKey(membroForm.getObj().getId(), MembroProjetoDiscente.class);
		
		if (membro != null) {
			req.setAttribute("membro", membro);
//			membro.getPlanoTrabalho().getTipoBolsaString()
			
			
			return mapping.findForward("declaracao");
		} else {
			addMensagemErro("� necess�rio informar um discente para e emiss�o da declara��o", req);
			return mapping.findForward("listar");
		}
		
	}	
	
}
