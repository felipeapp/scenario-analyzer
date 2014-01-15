/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '17/04/2008'
 *
 */
package br.ufrn.sigaa.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.portal.jsf.PortalCoordenadorPoloMBean;

/**
 * Action respons�vel por redirecionar para o Portal do TUTOR EAD
 * 
 * @author ricardo
 *
 */
public class PortalCoordenadorPoloAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Usuario usuarioLogado = (Usuario) getUsuarioLogado(request);
		
		if( !usuarioLogado.getVinculoAtivo().isVinculoCoordenacaoPolo() ){
			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o!");
		}

		request.getSession().setAttribute("calendarioAcademico", CalendarioAcademicoHelper.getCalendarioEAD());
		setSubSistemaAtual(request, SigaaSubsistemas.PORTAL_COORDENADOR_POLO);
		PortalCoordenadorPoloMBean mbean = getMBean("portalCoordPolo", request, response);
		mbean.getTutorPorCoordenador();

		return mapping.findForward("sucesso");
	}

}
