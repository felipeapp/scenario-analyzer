package br.ufrn.sigaa.ead.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * Action usada para verificar se o usuário tem acesso ao sistema
 * de stricto sensu
 *
 * @author Gleydson
 *
 */
public class EntrarEadAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		checkRole(new int[] { SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_TUTORIA_EAD, SigaaPapeis.COORDENADOR_PEDAGOGICO_EAD }, request);
		setSubSistemaAtual(request, SigaaSubsistemas.SEDIS);

		// enquanto não tiver calendários específicos de curso ou da SEDIS, carrega o padrão da Graduação
		ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametrosEAD();
		CalendarioAcademico c = CalendarioAcademicoHelper.getCalendarioEAD();
		request.getSession().setAttribute(SigaaAbstractController.PARAMETROS_SESSAO, param);
		request.getSession().setAttribute(SigaaAbstractController.CALENDARIO_SESSAO, c);

		return mapping.findForward("sucesso");

	}

}
