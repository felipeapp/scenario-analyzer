/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '02/06/2008'
 *
 */
package br.ufrn.sigaa.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;

/**
 *
 * @author amdantas
 *
 */
public class MenuSaeAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (isUserInRole(request ,SigaaPapeis.SAE, SigaaPapeis.SAE_COORDENADOR, SigaaPapeis.SAE_DEFINIR_DIAS_ALIMENTACAO, 
				SigaaPapeis.SAE_GESTOR_CARTAO_ALIMENTACAO, SigaaPapeis.SAE_VISUALIZA_ACESSO_RU, 
				SigaaPapeis.SAE_VISUALIZAR_CADASTRO_UNICO, SigaaPapeis.SAE_VISUALIZAR_RANKING )){
			setSubSistemaAtual(request, SigaaSubsistemas.SAE);
			request.getSession().removeAttribute("nivel");
			return mapping.findForward("sucesso");
		}
		else{
			throw new SegurancaException();
		}
	}
	
}