package br.ufrn.sigaa.ensino.graduacao.struts;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CALENDARIO_SESSAO;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CURSO_ATUAL;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.PARAMETROS_SESSAO;

import java.util.ArrayList;
import java.util.Collection;

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
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.ensino.CalendarioAcademicoDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

public class EntrarPortalCoordenadorGradAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		// Verifica as permissões do usuário
		verificarPermissoes(req);
		// verifica se existe calendário acadêmico
		verificarCalendarioAcademico(req);
		
		//definir o subsistema atual
		setSubSistemaAtual(req, SigaaSubsistemas.PORTAL_COORDENADOR);
		
		Usuario usuario = (Usuario) getUsuarioLogado(req);
		usuario.setNivelEnsino(NivelEnsino.GRADUACAO);
		
		req.getSession().setAttribute("nivel", NivelEnsino.GRADUACAO);
		req.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao());
		req.getSession().setAttribute(CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad());

		DadosAcesso dadosAcesso = getAcessoMenu(req);
		secretarioCoordenacao(req, usuario.getVinculoAtivo());
		
		coordenador(req, dadosAcesso, usuario);
			
		// Faz o redirecionamento para o portal correto
		if (usuario.isUserInRole(SigaaPapeis.COORDENADOR_ESTAGIOS))
			return mapping.findForward("coordenadorEstagios");
		else
			return mapping.findForward("coordenador");
	}

	/**
	 * Verifica se o usuário tem os papéis necessários para continuar a navegação
	 * 
	 * @param req
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	private void verificarPermissoes(HttpServletRequest req) throws SegurancaException, ArqException {
		checkRole(new int[] { SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.COORDENADOR_ESTAGIOS}, req);
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
	 * Carrega as coordenações do usuário
	 * 
	 * @param req
	 * @param dadosAcesso
	 * @param usuario
	 * @throws ArqException
	 * @throws DAOException
	 */
	private void coordenador(HttpServletRequest req, DadosAcesso dadosAcesso, Usuario usuario) throws ArqException, DAOException {
		
		if (isUserInRole(req, SigaaPapeis.COORDENADOR_CURSO)) {
			CoordenacaoCursoDao coordCursoDao = getDAO(CoordenacaoCursoDao.class, req);
			
			// Verifica se o usuário possui servidor (Coordenadores ou Gestores)
			if( !isEmpty(usuario.getServidor()) ){
				
				Collection<CoordenacaoCurso> coordenacoes = coordCursoDao.findByServidor(usuario.getServidor().getId(), null, NivelEnsino.GRADUACAO, null);
				if (coordenacoes != null && !coordenacoes.isEmpty()) {
					
					Curso curso = coordenacoes.iterator().next().getCurso();
					req.getSession().setAttribute(CURSO_ATUAL, curso);
					req.getSession().setAttribute(CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendario(curso));
					
					dadosAcesso.setCursos(new ArrayList<Curso>());
					
					for (CoordenacaoCurso c : coordenacoes) {
						dadosAcesso.getCursos().add(c.getCurso());
						c.getCurso().getDescricaoCompleta();
						c.getCurso().getDescricao();
						if (c.getCurso().isADistancia()) {
							dadosAcesso.setCursoEad(true);
						}
					}
				}
			}
		}
	}

	/**
	 * Carrega as secretárias de coordenação
	 * 
	 * @param req
	 * @param usuario 
	 * @param dadosAcesso
	 * @param secretarias
	 * @throws DAOException
	 */
	private void secretarioCoordenacao(HttpServletRequest req, VinculoUsuario vinculo) throws DAOException {
		if (vinculo.getSecretariaUnidade() != null && vinculo.getSecretariaUnidade().getCurso() != null) {
			req.getSession().setAttribute(CURSO_ATUAL, vinculo.getSecretariaUnidade().getCurso());
			req.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametros(vinculo.getSecretariaUnidade().getCurso()));
			req.getSession().setAttribute(CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendario(vinculo.getSecretariaUnidade().getCurso()));
		}		
	}
}
