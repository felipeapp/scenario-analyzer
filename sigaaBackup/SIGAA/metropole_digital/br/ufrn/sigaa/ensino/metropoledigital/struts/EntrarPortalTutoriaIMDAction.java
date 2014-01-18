package br.ufrn.sigaa.ensino.metropoledigital.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.Usuario;

public class EntrarPortalTutoriaIMDAction extends SigaaAbstractAction{
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Usuario usr = (Usuario) getUsuarioLogado(request);
		usr.setNivelEnsino(NivelEnsino.TECNICO);
		request.getSession().setAttribute("nivel", NivelEnsino.TECNICO);
		request.setAttribute("hideSubsistema", Boolean.TRUE);
		
		
		setSubSistemaAtual(request, SigaaSubsistemas.PORTAL_TUTOR_IMD);
		

		return mapping.findForward("sucesso");

	}

}
