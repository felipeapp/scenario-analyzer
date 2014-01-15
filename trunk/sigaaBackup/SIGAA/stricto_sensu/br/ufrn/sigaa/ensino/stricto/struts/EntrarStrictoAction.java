/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.struts;

import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CALENDARIO_SESSAO;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.PARAMETROS_SESSAO;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.PROGRAMA_ATUAL;

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
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.portal.jsf.PortalCoordenacaoStrictoMBean;

/**
 * Action usada para verificar se o usu�rio tem acesso ao sistema de stricto sensu
 *
 * @author Gleydson
 *
 */
public class EntrarStrictoAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if(CalendarioAcademicoHelper.getCalendarioUnidadeGlobalStricto() == null)
			throw new ConfiguracaoAmbienteException("N�o h� nenhum Calend�rio ativo");		
		
		setSubSistemaAtual(request, SigaaSubsistemas.STRICTO_SENSU);

		Usuario usr = (Usuario)getUsuarioLogado(request);
		usr.setNivelEnsino(NivelEnsino.STRICTO);
		request.getSession().setAttribute("nivel", NivelEnsino.STRICTO);
		
		request.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalStricto());

		if ( (getSubSistemaAtual(request) == SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.getId() || "programa".equalsIgnoreCase(request.getParameter("portal")) ) 
				&& ( getAcessoMenu(request).isSecretariaPosGraduacao() || getAcessoMenu(request).isCoordenadorCursoStricto() ) ) {
			
			DadosAcesso dados = getAcessoMenu(request);
			
			if (usr.getServidorAtivo() != null) {
				// Verificar se o usu�rio � coordenador de algum PROGRAMA DE POS GRADUACAO
				CoordenacaoCursoDao coordCursoDao = getDAO(CoordenacaoCursoDao.class, request);
				ArrayList<CoordenacaoCurso> coordenacoesPos = (ArrayList<CoordenacaoCurso>) coordCursoDao.findCoordPosByServidor(usr.getServidor() .getId(), true, null);
				if (!ValidatorUtil.isEmpty(coordenacoesPos)) {
					dados.setProgramas(new ArrayList<Unidade>(0));
					for( CoordenacaoCurso cc: coordenacoesPos ){
						dados.getProgramas().add(cc.getUnidade());
						
						Unidade programa = cc.getUnidade();
						request.getSession().setAttribute(PROGRAMA_ATUAL, programa);
						configurarCalendarioSessao(programa, request,response);
						request.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametros(programa, NivelEnsino.STRICTO, null, null, null) );
						
						if(cc.getUnidade().getMunicipio() != null)
							cc.getUnidade().getMunicipio().getNomeUF();
					}
					usr.setNivelEnsino(NivelEnsino.STRICTO);
					request.getSession().setAttribute("nivel", NivelEnsino.STRICTO);
				}			
			}			
			
			if (usr.getVinculoAtivo().isVinculoSecretaria()) {
				Unidade programa = usr.getVinculoAtivo().getSecretariaUnidade().getUnidade();
				request.getSession().setAttribute(PROGRAMA_ATUAL, programa);
				configurarCalendarioSessao(programa, request,response);				
			}
			
			setSubSistemaAtual(request, SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO);
			
			/**
			 * Atualiza a exibi��o das mensagens do Forum no Portal Coordena��o Stricto
			 * Quando o usu�rio alterna entre v�nculos distintos, o Forum correspondente
			 * ao vinculo n�o estava sendo exibido
			 */
			PortalCoordenacaoStrictoMBean portalCoordenacaoStricto = getMBean("portalCoordenacaoStrictoBean", request, response);
			if (portalCoordenacaoStricto != null) {
				portalCoordenacaoStricto.setSolicitacoesMatricula(null);
				portalCoordenacaoStricto.setSolicitacoesTrancamento(null);
				portalCoordenacaoStricto.setSolicitacoesMatriculaOutroPrograma(null);				
				//portalCoordenacaoStricto.recarregarInformacoesPortal();
			}
			
			return mapping.findForward("coordenacaoStricto");
		} else if ( (getSubSistemaAtual(request) == SigaaSubsistemas.STRICTO_SENSU.getId() || "ppg".equalsIgnoreCase( request.getParameter("portal") ) ) 
				&& (getAcessoMenu(request).isPpg() || getAcessoMenu(request).isMembroApoioDocenciaAssistida())) {
			setSubSistemaAtual(request, SigaaSubsistemas.STRICTO_SENSU);
			request.getSession().setAttribute(CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendario(usr));
			return mapping.findForward("sucesso");
		} else if (isUserInRole(request, SigaaPapeis.CONSULTADOR_ACADEMICO)) {
			setSubSistemaAtual(request, SigaaSubsistemas.CONSULTA);			
			request.getSession().setAttribute(CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendarioUnidadeGlobalStricto());
			return mapping.findForward("consultas");			
		}

		throw new SegurancaException("Usu�rio n�o identificado");
	}
	
	/**
	 * 
	 * Configura na sess�o o calend�rio correto a ser utilizado.
	 * Se o calend�rio setado na sess�o for o calend�rio de um curso, o curso deve ent�o ser setado
	 * na sess�o como o curso atual.
	 * 
	 * @param programa
	 * @param request
	 * @param response
	 * @throws ArqException
	 */
	private void configurarCalendarioSessao(Unidade programa, HttpServletRequest request, HttpServletResponse response) throws ArqException {		
		
		CursoDao dao = getDAO(CursoDao.class,request);						
		CalendarioAcademico calendarioSessao = CalendarioAcademicoHelper.getCalendario(programa);		
		request.getSession().setAttribute(SigaaAbstractController.CURSO_ATUAL_COORDENADOR_STRICTO, null);
		
		//Verifica se o programa possui calend�rio cadastrado, se tiver usa ele, sen�o 
		//encontrou calend�rio global.
		//Verifica tamb�m se � permitido na institui��o definir calend�rios por curso.
		//Caso tenha curso com calendario cadastrado, seta o curso na sess�o e tamb�m o calend�rio associado.
		if(ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.CALENDARIO_POR_CURSO) &&
			calendarioSessao.getUnidade().getId() == UnidadeGeral.UNIDADE_DIREITO_GLOBAL) {		
			
			Collection<Curso> cursosDoPrograma = dao.findByPrograma( programa.getId() );		
			if(!ValidatorUtil.isEmpty(cursosDoPrograma) ) {
				Curso curso = cursosDoPrograma.iterator().next();
				CalendarioAcademico calendarioCurso = CalendarioAcademicoHelper.getCalendario(curso);
				if(!ValidatorUtil.isEmpty(calendarioCurso) && 
						!ValidatorUtil.isEmpty(calendarioCurso.getCurso()) && 
						calendarioCurso.getCurso().getId() == curso.getId()) {			
					calendarioSessao = CalendarioAcademicoHelper.getCalendario(curso);
					request.getSession().setAttribute(SigaaAbstractController.CURSO_ATUAL_COORDENADOR_STRICTO, curso);
				}		
			}
		}
		
		request.getSession().setAttribute(CALENDARIO_SESSAO, calendarioSessao);
	}

}
