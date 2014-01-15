/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.struts;

import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CALENDARIO_SESSAO;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.PARAMETROS_SESSAO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.dao.ensino.CalendarioAcademicoDao;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * Action que controla a entrada no menu da graduação
 *
 * @author Gleydson
 *
 */
public class EntrarGraduacaoAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response) throws Exception {
		
		// Verifica as permissões do usuário
		verificarPermissoes(req);
		// verifica se existe calendário acadêmico
		verificarCalendarioAcademico(req);
		
		//desini o subsistema atual
		definirSubSistema(req);
		
		Usuario usuario = (Usuario) getUsuarioLogado(req);
		usuario.setNivelEnsino(NivelEnsino.GRADUACAO);
		
		req.getSession().setAttribute("nivel", NivelEnsino.GRADUACAO);
		req.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao());
		req.getSession().setAttribute(CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad());

		// Faz o redirecionamento para o portal correto
		if (isUserInRole(req, SigaaPapeis.CADASTRA_DISCENTE_GRADUACAO))
			return mapping.findForward("cadastroDiscente");
		else if (isUserInRole(req, SigaaPapeis.SECRETARIA_DEPARTAMENTO))
			return mapping.findForward("departamento");
		else if (isUserInRole(req, SigaaPapeis.CONSULTADOR_ACADEMICO))
			return mapping.findForward("consultas");
		else
			return mapping.findForward("geral");
		
	}

	/**
	 * Verifica se o usuário tem os papéis necessários para continuar a navegação
	 * 
	 * @param req
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	private void verificarPermissoes(HttpServletRequest req) throws SegurancaException, ArqException {
		checkRole(new int[] { SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE,
				SigaaPapeis.GESTOR_PROBASICA,
				SigaaPapeis.SECRETARIA_DEPARTAMENTO,
				SigaaPapeis.SECRETARIA_CENTRO,
				SigaaPapeis.CONSULTADOR_ACADEMICO,
				SigaaPapeis.CADASTRA_DISCENTE_GRADUACAO
				}, req);
	}

	/**
	 * Verifica se existe um calendário acadêmico global para Graduação
	 * 
	 * @param req
	 * @throws ArqException
	 * @throws DAOException
	 */
	private void verificarCalendarioAcademico(HttpServletRequest req) throws ArqException, DAOException {
		CalendarioAcademicoDao calDao = getDAO(CalendarioAcademicoDao.class, req);
		
		if( !calDao.existeCalendarioUnidadeGlobalByNivel(NivelEnsino.GRADUACAO) )
			throw new ConfiguracaoAmbienteException("Não há nenhum Calendário ativo");
	}

	/**
	 * Defini o subsistema que o usuário deseja entrar
	 * 
	 * @param req
	 * @throws ArqException 
	 */
	private void definirSubSistema(HttpServletRequest req) throws ArqException {

		// recebe o valor de destino
		Usuario usuario = (Usuario) getUsuarioLogado(req);
		
		// caso destino seja diferente de null, seu valor é atualizado
		
		if(!usuario.isUserInRole( SigaaPapeis.CONSULTADOR_ACADEMICO ) ){
			setSubSistemaAtual(req, SigaaSubsistemas.GRADUACAO);
		} else {
			setSubSistemaAtual(req, SigaaSubsistemas.CONSULTA);
		}
	}

}
