/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 04/07/2007 
 *
 */
package br.ufrn.sigaa.portal.struts;

import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CALENDARIO_SESSAO;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.PARAMETROS_SESSAO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * Classe utilizada para redirect para entrar no portal da Planejamento.
 *
 * @author gleydson
 */
public class EntrarPlanejamentoAction extends AbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		checkRole(new int[]{SigaaPapeis.PORTAL_PLANEJAMENTO,SigaaPapeis.ACOMPANHA_DESEMPENHO_SERVIDORES_NA_POSGRADUACAO}, request);
		request.setAttribute("hideSubsistema", Boolean.TRUE);
		setSubSistemaAtual(request, SigaaSubsistemas.PORTAL_PLANEJAMENTO);

		request.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao());
		request.getSession().setAttribute( CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad());

		return mapping.findForward("sucesso");

	}
}