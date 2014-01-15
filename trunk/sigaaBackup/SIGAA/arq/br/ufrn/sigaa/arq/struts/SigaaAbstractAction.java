/*
 * SIGAA - Sistema Integrado de Gerencia de Atividades Academicas
 * Superintendencia de Informatica - UFRN
 *
 */
package br.ufrn.sigaa.arq.struts;

import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CALENDARIO_SESSAO;
import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.PARAMETROS_SESSAO;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.Link;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.negocio.SigaaHelper;
import br.ufrn.sigaa.arq.util.SigaaUtils;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * AbstractAction do SIGAA
 *
 * @author Gleydson Lima
 *
 */
public abstract class SigaaAbstractAction extends AbstractAction {

	@Override
	public GenericDAO getGenericDAO(HttpServletRequest req) throws ArqException {
		return getDAO(GenericSigaaDAO.class, req);
	}

	@Override
	public <T extends GenericDAO> T getDAO(Class<T> classe, HttpServletRequest req)
			throws ArqException {
		return DAOFactory.getInstance().getDAO(classe, getUsuarioLogado(req),
				getCurrentSession(req));
	}

	/**
	 * Retorna a sess�o corrente do Hibernate que est� em request
	 *
	 * @param req
	 * @return
	 */
	@Override
	public Session getCurrentSession(HttpServletRequest req) {
		return DAOFactory.getInstance().getSessionRequest(req);
	}

	/**
	 * Fecha a sess�o que est� ativa em request. 
	 */
	public void forceCloseConnection(HttpServletRequest req) {

		Session s = getCurrentSession(req);
		s.close();
	}

	public SubSistema getSubSistemaCorrente(HttpServletRequest req) {
		return (SubSistema) req.getSession().getAttribute("subsistema");
	}

	@Override
	public int getUnidadeGestora(HttpServletRequest req) throws ArqException {

		try {
			return getParametrosAcademicos(req).getUnidade().getId();
		} catch (Exception e) {
			throw new ArqException("� necess�rio unidade acad�mica definida para acessar essa opera��o");
		}
		
//		UnidadeGeral unidade = getUsuarioLogado(req).getUnidade()
//				.getGestoraAcademica();
		/*
		 * int idUnidadeGestora = unidade.getId(); if (unidade.getTipo() ==
		 * UnidadeGeral.UNIDADE_FATO) { idUnidadeGestora =
		 * unidade.getUnidadeGestora().getId(); }
		 */
//		return unidade.getId();
	}

	public char getNivelEnsino(HttpServletRequest req) throws ArqException {
		char nivel = SigaaSubsistemas.getNivelEnsino(getSubSistemaCorrente(req));
		if (nivel == ' ') {
			Character nivelSession = (Character) req.getSession().getAttribute(
					"nivel");
			if (nivelSession == null) {
				throw new ArqException(
						"Nivel Necessita de defini��o para esta opera��o");
			}
			return nivelSession;
		}
		return nivel;
	}

	/**
	 * Verifica se existem mensagens de erro em request. Retorna true se existirem,
	 * false caso contr�rio. 
	 */
	public boolean flushOnlyErros(HttpServletRequest req) {
		ListaMensagens msgs = (ListaMensagens) req.getSession()
				.getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION);
		return (msgs != null && msgs.isErrorPresent());
	}

	/**
	 * Chama o processador baseado no movimento passado sem fechar a sess�o do
	 * hibernate
	 *
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public Object executeWithoutClosingSession(Movimento mov,
			HttpServletRequest req) throws NegocioException, ArqException,
			RemoteException {
		mov.setApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext()));
		return getUserDelegate(req).execute(mov, req);

	}

	/**
	 * Retorna os par�metros da gestora acad�mica em quest�o
	 *
	 * @return
	 * @throws DAOException
	 */
	public ParametrosGestoraAcademica getParametrosAcademicos(HttpServletRequest req) throws ArqException {
		if ( req.getSession().getAttribute(PARAMETROS_SESSAO) == null ) {
			return ParametrosGestoraAcademicaHelper.getParametros((Usuario)getUsuarioLogado(req));
		} else {
			return (ParametrosGestoraAcademica) req.getSession().getAttribute(PARAMETROS_SESSAO);
		}
	}
	
	/**
	 * Define os par�metros acad�micos utilizados pra as opera��es realizadas pelo usu�rio
	 * 
	 * @param req
	 * @param parametros
	 */
	public void setParametrosGestoraAcademica(HttpServletRequest req, ParametrosGestoraAcademica parametros) {
		req.getSession().setAttribute(PARAMETROS_SESSAO, parametros);
	}

	public CalendarioAcademico getCalendarioVigente(HttpServletRequest req) {
		return (CalendarioAcademico) req.getSession().getAttribute(
				"calendarioAcademico");
	}

	public DadosAcesso getAcessoMenu(HttpServletRequest req) {
		return (DadosAcesso) req.getSession().getAttribute("acesso");
	}

	/**
	 * carrega em sess�o tanto os par�metros como o calend�rio atual, baseados
	 * na unidade e n�vel de ensino do usu�rio
	 *
	 * @param request
	 */
	public void carregarParametrosCalendarioAtual(HttpServletRequest request) {
		try {
			Usuario usr = (Usuario) getUsuarioLogado(request);
			ParametrosGestoraAcademica param = (ParametrosGestoraAcademica) request.getSession().getAttribute(PARAMETROS_SESSAO);
			CalendarioAcademico cal = (CalendarioAcademico) request.getSession().getAttribute(CALENDARIO_SESSAO);
			
			// unidade do usu�rio que est� logando no sistema
			Unidade unidadeAtiva = null;
			if ( usr.getVinculoAtivo() != null && usr.getVinculoAtivo().getUnidade() != null) 
				unidadeAtiva = usr.getVinculoAtivo().getUnidade();
			else
				unidadeAtiva = usr.getUnidade();
			
			
			if ( !NivelEnsino.isValido(usr.getNivelEnsino()) )
				usr.setNivelEnsino( NivelEnsino.GRADUACAO );
			// se o usu�rio tem gestora acad�mica E (se ainda n�o foram carregadas em sess�o
			// os par�metros e calend�rio da unidade acad�mica OU os par�metros e calend�rio carregados s�o de um n�vel de ensino
			// diferente do setado no usu�rio)
			// E o n�vel de ensino do usu�rio � valido !
			if( unidadeAtiva != null && unidadeAtiva.getGestoraAcademica() != null &&
					((param == null &&  cal == null) || (param.getNivel() != usr.getNivelEnsino() && cal.getNivel() != usr.getNivelEnsino())) &&
					NivelEnsino.isValido(usr.getNivelEnsino()) ) {
				request.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametros(usr));
				request.getSession().setAttribute( CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendario(usr));

			} else if ( usr.isUserInRole( new int[] { SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE } )  &&
					request.getSession().getAttribute(PARAMETROS_SESSAO) == null &&
					request.getSession().getAttribute(CALENDARIO_SESSAO) == null)  {

				// caso o usu�rio seja habilitado no Sistema de Gradua��o
				request.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao());
				request.getSession().setAttribute( CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad());

			} else if (usr.isUserInRole( new int[] { SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_PEDAGOGICO_EAD, SigaaPapeis.COORDENADOR_TUTORIA_EAD } )  &&
				request.getSession().getAttribute(PARAMETROS_SESSAO) == null &&
				request.getSession().getAttribute(CALENDARIO_SESSAO) == null) {

				request.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao());
				request.getSession().setAttribute( CALENDARIO_SESSAO, CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad());
			} else if (usr.isUserInRole( new int[] { SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO }) &&
					request.getSession().getAttribute(PARAMETROS_SESSAO) == null){
				request.getSession().setAttribute(PARAMETROS_SESSAO, ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalLato());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void clearSessionWeb(HttpServletRequest req) {
		SigaaUtils.clearSessionWEB(req);
	}

	public ActionForward unspecified(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		return mapping.findForward(getSubSistemaCorrente(req).getForward());
	}
	
	/**
	 * Retorna Curso atual manipulado pelo coordenador logado
	 * @return
	 */
	public Curso getCursoAtualCoordenacao(HttpServletRequest req) {
		return SigaaHelper.getCursoAtualCoordenacao();
	}
	
	/**
	 * Verifica se uma opera��o est� ativa. Se n�o estiver, retorna para
	 * o menu do subsistema
	 */
	public boolean checkOperacaoAtiva(HttpServletRequest req, HttpServletResponse res, String operacao) throws ServletException, IOException {
		boolean ativa = false;

		if (req.getSession().getAttribute("operacaoAtiva") != null) {
			if (req.getSession().getAttribute("operacaoAtiva").toString().equals(operacao))
				ativa = true;
		}

		if (!ativa) {
			String msg = (String) req.getSession().getAttribute("mensagemOperacaoAtiva");

			if (msg == null)
				msg = "Solicita��o j� processada.";

			Link link = getLinkSubSistema(req, getSubSistemaAtual(req));
			link.setUrl(link.getUrl());
			
			addMensagemErro(msg, req);
			
			res.sendRedirect(req.getContextPath() + link.getUrl());
		}

		return ativa;
	}

	/**
	 * Verifica se uma opera��o est� ativa dentro de um form usado por
	 * v�rias opera��es, para evitar a valida��o em opera��es
	 * inativas.
	 */
	public boolean checkOperacoesAtivas(HttpServletRequest req, HttpServletResponse res, String[] operacoes) throws ServletException, IOException {

		boolean ativa = false;
		String opAtiva = (String) req.getSession().getAttribute("operacaoAtiva");

		if (opAtiva != null) {
			for (int i = 0; i < operacoes.length; i++) {
				if (opAtiva.equals(operacoes[i])) {
					ativa = true;
					break;
				}
			}
		}

		if (!ativa) {
			String msg = (String) req.getSession().getAttribute("mensagemOperacaoAtiva");

			if (msg == null)
				msg = "Solicita��o j� processada.";

			Link link = getLinkSubSistema(req, getSubSistemaAtual(req));
			link.setUrl(link.getUrl());

			addMensagemErro(msg, req);
			
			res.sendRedirect(req.getContextPath() + link.getUrl());
		}

		return ativa;
	}
	
	private Link getLinkSubSistema(HttpServletRequest req, int subsistema) {
		SubSistema ss = null;
		
		try {
			ss = getGenericDAO(req).findByPrimaryKey(subsistema, SubSistema.class);
		} catch (DAOException e) {
			e.printStackTrace();
		} catch (ArqException e) {
			e.printStackTrace();
		}
		
		if (ss != null) {
			return new Link(ss.getNome(), ss.getLink());
		} else {
			return new Link();
		}
	}
	
}