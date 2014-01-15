/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
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
 * Action responsável por redirecionar para o Portal do TUTOR EAD
 * 
 * @author ricardo
 *
 */
public class PortalCoordenadorPoloAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Usuario usuarioLogado = (Usuario) getUsuarioLogado(request);
		
		if( !usuarioLogado.getVinculoAtivo().isVinculoCoordenacaoPolo() ){
			throw new SegurancaException("Usuário não autorizado a realizar esta operação!");
		}

		request.getSession().setAttribute("calendarioAcademico", CalendarioAcademicoHelper.getCalendarioEAD());
		setSubSistemaAtual(request, SigaaSubsistemas.PORTAL_COORDENADOR_POLO);
		PortalCoordenadorPoloMBean mbean = getMBean("portalCoordPolo", request, response);
		mbean.getTutorPorCoordenador();

		return mapping.findForward("sucesso");
	}

}
