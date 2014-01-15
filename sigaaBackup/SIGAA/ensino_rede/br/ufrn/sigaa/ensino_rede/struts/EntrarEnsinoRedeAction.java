package br.ufrn.sigaa.ensino_rede.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.Usuario;

public class EntrarEnsinoRedeAction extends SigaaAbstractAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		checkRole(new int[] { SigaaPapeis.COORDENADOR_GERAL_REDE, SigaaPapeis.COORDENADOR_UNIDADE_REDE}, request);
		
		Usuario u = (Usuario) getUsuarioLogado(request);
		
		if (u.getVinculoAtivo().getTipoVinculo().isCoordenadorGeralRede())
			return mapping.findForward("modulo");
		else if (u.getVinculoAtivo().getTipoVinculo().isCoordenadorUnidadeRede())
			return mapping.findForward("portal");
		
		return null;
	}
	
}
