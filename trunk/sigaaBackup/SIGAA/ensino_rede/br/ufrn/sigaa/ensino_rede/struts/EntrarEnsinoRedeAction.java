package br.ufrn.sigaa.ensino_rede.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino_rede.portal.jsf.PortalCoordenadorRedeMBean;

/**
 * 
 * Action que controla a entrada no menu do ensino em rede.
 * 
 * @author Leonardo Campos
 *
 */
public class EntrarEnsinoRedeAction extends SigaaAbstractAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		checkRole(new int[] { SigaaPapeis.COORDENADOR_GERAL_REDE, SigaaPapeis.COORDENADOR_UNIDADE_REDE}, request);
		
		Usuario u = (Usuario) getUsuarioLogado(request);
		
		if (u.getVinculoAtivo().getTipoVinculo().isCoordenadorGeralRede()) {
			setSubSistemaAtual(request, SigaaSubsistemas.ENSINO_REDE);
			return mapping.findForward("modulo");
		} else if (u.getVinculoAtivo().getTipoVinculo().isCoordenadorUnidadeRede()){
			setSubSistemaAtual(request, SigaaSubsistemas.PORTAL_ENSINO_REDE);
			PortalCoordenadorRedeMBean portal = getMBean("portalCoordenadorRedeBean", request, response);
			if (portal != null)
				portal.setTurmasAbertas(null);
			
			return mapping.findForward("portal");
		}	
		
		return null;
	}
	
}
