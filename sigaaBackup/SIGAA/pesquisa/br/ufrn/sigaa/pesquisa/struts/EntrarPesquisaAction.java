/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.sigaa.projetos.dominio.Edital;

/**
 * Efetua o acesso ao módulo de pesquisa.
 * 
 * @author Ricardo Wendell
 *
 */
public class EntrarPesquisaAction extends AbstractAction {

	/**
	 * Responsável pela execução da entrada em Pesquisa  
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(isUserInRole(request, SigaaPapeis.GESTOR_PESQUISA, SigaaPapeis.MEMBRO_COMITE_PESQUISA, SigaaPapeis.NIT)){
			
			request.setAttribute("hideSubsistema", Boolean.TRUE);
			setSubSistemaAtual(request, SigaaSubsistemas.PESQUISA);
			request.getSession().setAttribute("tipoEdital", Edital.PESQUISA);
			request.getSession().setAttribute("nivel", NivelEnsino.GRADUACAO);
			
			return mapping.findForward("sucesso");
			
		} else if (isUserInRole(request, SigaaPapeis.PESQUISA_TECNICO_ADMINISTRATIVO)){
			
			setSubSistemaAtual(request, SigaaSubsistemas.PESQUISA);
			request.getSession().setAttribute("tipoEdital", Edital.PESQUISA);

			request.setAttribute("hideSubsistema", Boolean.TRUE);

			return mapping.findForward("menu_ta");
			
		} else {
			throw new SegurancaException();
		}


	}

}
