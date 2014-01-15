/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '13/06/2007'
 *
 */
package br.ufrn.sigaa.struts;

import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CALENDARIO_SESSAO;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.PARAMETROS_SESSAO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * Action responsável por redirecionar para o Portal do TUTOR EAD
 * 
 * @author ricardo
 *
 */
public class PortalTutorAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Usuario usuario = (Usuario) getUsuarioLogado(request);
		
		if( !usuario.getVinculoAtivo().isVinculoTutorOrientador() ){
			throw new SegurancaException("Usuário não autorizado a realizar esta operação!");
		} else {
			setSubSistemaAtual(request, SigaaSubsistemas.PORTAL_TUTOR);
	
			if (usuario.getTutor().isPresencial()){
				ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros( usuario.getTutor().getPoloCurso().getCurso() );
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario( usuario.getTutor().getPoloCurso().getCurso() );
	
				request.getSession().setAttribute(PARAMETROS_SESSAO, param);
				request.getSession().setAttribute(CALENDARIO_SESSAO, cal);
			}
		}
		return mapping.findForward("sucesso");
	}

}
