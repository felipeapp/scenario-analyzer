/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/04/2008
 *
 */
package br.ufrn.sigaa.vestibular.struts;

import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.PARAMETROS_SESSAO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * Action usada para verificar se o usuário tem acesso ao sistema de vestibular
 *
 * @author David Pereira
 *
 */
public class EntrarVestibularAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		setSubSistemaAtual(request, SigaaSubsistemas.VESTIBULAR);
		Usuario usr = (Usuario)getUsuarioLogado(request);
		usr.setNivelEnsino(NivelEnsino.GRADUACAO);
		request.getSession().setAttribute("nivel", NivelEnsino.GRADUACAO);
		request.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao());
		if (usr.isUserInRole(SigaaPapeis.BOLSISTA_VESTIBULAR)) {
			return mapping.findForward("sucessoBolsista");
		} else if (usr.isUserInRole(SigaaPapeis.VESTIBULAR)) {
			return mapping.findForward("sucesso");
		} else if (usr.getVinculoAtivo().isVinculoServidor() || usr.isUserInRole(SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS)) {
			return mapping.findForward("sucessoServidor");
		} else {
			throw new SegurancaException();
		}
	}

}
