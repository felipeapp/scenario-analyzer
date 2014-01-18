/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.struts;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.util.ReflectionUtils;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.PassaporteDao;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.dominio.PassaporteLogon;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.comum.jsf.VerTelaAvisoLogonMBean;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.dominio.UsuarioMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.LogonForm;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.caixa_postal.dominio.MensagensHelper;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe responsável pelo login/logoff no SIGAA
 * 
 * @author Marcos Alexandre, Gleydson Lima
 *
 */
public class LoginActions extends SigaaAbstractAction {

	// Strings de redirecionamento do struts
	/** String de redirecionamento para Sucesso */
	public static final String SUCESSO = "sucesso";
	
	/** String de redirecionamento para Progresso */
	public static final String PROGRESSO = "progresso";

	/** String de redirecionamento para Falha */
	public static final String FALHA = "falha";

	/** String de redirecionamento para Caixa Postal */
	public static final String CAIXA_POSTAL = "caixaPostal";

	/** String de redirecionamento para Tela Pública */
	public static final String TELA_PUBLICA = "portalPublico";

	// Parâmetros manipulados pelas requisições
	/** Parâmetro de Ação */
	public static final String ACAO = "acao";

	/** Parâmetro de Aviso Browser */
	private static final String AVISO_BROWSER = "avisoBrowser";

	/** Parâmetro de Escolha de Vínculo */
	private static final String ESCOLHA_VINCULO = "escolhaVinculo";

	
	/**
	 * Faz o logOn do usuário no sistema
	 *
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward logOn(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {

			LogonForm logonForm = (LogonForm) form;
			Usuario usuario = logonForm.getUser();
			Boolean acessibilidade = false;
			
			UsuarioMov mov = new UsuarioMov();
			mov.setCodMovimento(SigaaListaComando.LOGON);
			mov.setIP(request.getRemoteAddr());
			mov.setIpInternoNat(request.getHeader("X-FORWARDED-FOR"));
			mov.setUsuario(usuario);
			mov.setHost(NetworkUtils.getLocalName());
			mov.setUserAgent(request.getHeader("User-Agent"));
			mov.setResolucao(logonForm.getWidth() + "x" + logonForm.getHeight());
			mov.setRequest(request);
			
			request.setAttribute("NO_LOGGER", Boolean.TRUE);

			// Ação a ser executada após o logon.
			String acao = null;
			
			try {
				request.getSession().invalidate();

				if (logonForm.isPassaporte()) {
	
					PassaporteDao dao = new PassaporteDao();
					try {
						PassaporteLogon p = dao.findPassaporte(usuario.getLogin(), Sistema.SIGAA);
		
						if (p == null) {
							return falha(mapping, request, response, "Passaporte não validado");
						} else {
							acao = p.getAcao();
							mov.setPassaporte(p);
						}
					} finally {				
						dao.close();
					}
				}

				acessibilidade = (request.getParameter("acessibilidade") == null ? Boolean.FALSE 
						: Boolean.parseBoolean(request.getParameter("acessibilidade")));
				request.getSession(true).setAttribute("usuario", usuario);
				usuario = (Usuario) execute(mov, request);

				if (usuario == null) {
					return falha(mapping, request, response, "Usuário/Senha Inválidos");					
				} else {
					request.getSession(true).setAttribute("usuario", usuario);
					request.getSession(false).setAttribute("sistema", Sistema.SIGAA);
					request.getSession(true).setAttribute("acessibilidade", acessibilidade);
				}

			} catch (NegocioException e) {
				request.getSession().invalidate();
				return falha(mapping, request, response, e.getMessage());
			}
			
			request.getSession().removeAttribute(ACAO);

			if (!UFRNUtils.identificaBrowserRecomendado(request.getHeader("User-Agent"))) {
				request.getSession().invalidate();
				return mapping.findForward(AVISO_BROWSER);
			}
			
			if (!UserAutenticacao.usuarioAtivo(request, Sistema.SIGAA, usuario.getId())) {
				request.getSession().invalidate();
				return falha(mapping, request, response, "Usuário não autorizado a acessar o sistema.");
			}
			
			if (UserAutenticacao.senhaExpirou(request, usuario.getId())) {
				response.sendRedirect("/sigaa/alterar_dados.jsf?expirou=true");
				return null;
			}
			
			List<VinculoUsuario> vinculos = VinculoUsuario.processarVinculosUsuario(usuario, request);
//			for (Iterator<VinculoUsuario> it = vinculos.iterator(); it.hasNext();) {
//				VinculoUsuario vinculoUsuario = it.next();
//				if( vinculoUsuario.getDiscente() != null && 
//						vinculoUsuario.getDiscente().getStatus() == StatusDiscente.CADASTRADO && 
//						!isAlunoPossuiSolicitacaoMatricula(vinculoUsuario.getDiscente().getDiscente(), request) )
//					it.remove();
//			}
			
			usuario.setVinculos(vinculos);
			usuario.setPassaporte(logonForm.isPassaporte());

			boolean multiplosVinculos = usuario.getVinculosPrioritarios().size() > 1;
			
			if (!multiplosVinculos)
				if (!isEmpty(usuario.getVinculosPrioritarios()))
					usuario.setVinculoAtivo(usuario.getVinculosPrioritarios().get(0));
				else
					usuario.setVinculoAtivo(usuario.getVinculos().get(0));
			
			//Esta ação é informada na parte pública do SIGAA, quando o servidor tenta realizar alguma consulta
			//e deve ser redirecionado para a tela de logon.
			if (acao == null)
				acao = request.getParameter("acao");
			
			if ( !isEmpty(acao) ) {
				String[] partes = acao.split("\\.");
				Object mbean = getMBean(partes[0], request, response);
				ReflectionUtils.findMethod(mbean.getClass(), partes[1]).invoke(mbean, (Object[]) null);
				return null;
			}
			
			// Se tiver redirecionamento passado no formulário de login, redirecionar
			if ( request.getParameter("urlRedirect") != null && !request.getParameter("urlRedirect").equals("") ) {
				request.getSession().setAttribute("urlRedirect", request.getParameter("urlRedirect"));
				
				// Se tiver subsistema passado no formulário de login
				if ( request.getParameter("subsistemaRedirect") != null && !request.getParameter("subsistemaRedirect").equals("") ) {
					request.getSession().setAttribute("subsistemaRedirect", request.getParameter("subsistemaRedirect"));
				}
			}
			
			
			// Adicionando atributo em sessão para determinar se o usuário verá ou não o link para caixa postal
			if (Sistema.isCaixaPostalAtiva(Sistema.SIGAA)) {
				request.getSession().setAttribute("acessarCaixaPostal", MensagensHelper.acessoCaixaPostal(getUsuarioLogado(request).getId()) ? false : true);
			}
			
			
			// Se o usuário tiver múltiplos vínculos com a universidade, irá
			// para uma tela de escolha do vínculo com o qual ele quer trabalhar
			if (multiplosVinculos) {
				return mapping.findForward(ESCOLHA_VINCULO);
			} else {
				VinculoUsuario.popularVinculoAtivo(usuario);
			}

			VerTelaAvisoLogonMBean telaAviso = getBean(request, "verTelaAvisoLogonMBean");
			//if ( ((Usuario) getUsuarioLogado(request)).getVinculoAtivo().getDiscente() != null ) 
			//	telaAviso.iniciar("/progresso.jsf", getUsuarioLogado(request), getSistema(request), request, response);
			//else 
			telaAviso.iniciar("/paginaInicial.do", getUsuarioLogado(request), getSistema(request), request, response);
			
			return null;
			
		} catch (Exception e) {
			request.getSession(true).invalidate();
			throw e;
		}
	}

	/**
	 * Faz o logOff do usuário no sistema
	 *
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward logOff(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			if (request.getSession() != null) {
				UsuarioMov mov = new UsuarioMov();
				mov.setCodMovimento(SigaaListaComando.LOGOFF);
				mov.setUsuario(getUsuarioLogado(request));
				mov.setUsuarioLogado(getUsuarioLogado(request));
				execute(mov, request);
				request.getSession().invalidate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mapping.findForward(TELA_PUBLICA);
	}

	/**
	 * Retorna para o usuário original quando o usuário está logado como outro.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward retornarUsuario(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession sessao = request.getSession();
		sessao.setAttribute("usuario", sessao.getAttribute("usuarioAnterior"));
		sessao.setAttribute("acesso", sessao.getAttribute("acessoAnterior"));

		SubSistema subsistemaAnterior = (SubSistema) sessao.getAttribute("subsistemaAnterior");
		
		removerAtributosSessao(sessao);
		
		if (subsistemaAnterior != null) {
			sessao.setAttribute("subsistema", subsistemaAnterior );
			sessao.removeAttribute("subsistemaAnterior");
			return mapping.findForward(subsistemaAnterior.getForward());
		} else {
			return mapping.findForward("menuPrincipal");
		}

	}

	/**
	 * Retorna para o usuário original quando o usuário está logado como outro.
	 * Utilizado pelo pessoal da SEDIS, quando vão logar como tutores. Retorna
	 * para o subsistema de ensino a distância.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward retornarUsuarioSedis(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return retornarUsuario(mapping, form, request, response);
	}

	
	/**
	 * Remove os atributos de sessão, exceto usuário e dados de acesso. 
	 * Implementação extraída de br.ufrn.arq.web.jsf.AbstractController.removerAtributosSessao()
	 * 
	 * @param session
	 */
	@SuppressWarnings("unchecked")
	private void removerAtributosSessao(HttpSession session) {
		Enumeration<String> atributos = session.getAttributeNames();
		while(atributos.hasMoreElements()) {
			String atributo = atributos.nextElement();
			if (!"usuario".equals(atributo) && !"acesso".equals(atributo)) {
				session.removeAttribute(atributo);
			}
		}
	}	
	
	/**
	 * Adiciona uma mensagem de erro e retorna para a tela de logon
	 * ou para a tela de logon unificado.
	 * @param mapping
	 * @param request
	 * @param response
	 * @param msg
	 * @return
	 * @throws IOException
	 */
	public ActionForward falha(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response, String msg) throws IOException {
		if (request.getParameter("unificado") != null) {
			response.sendRedirect("/admin/public/loginunificado/index.jsf?selectedTab=sigaa&mensagem=" + msg);
			return null;
		} else {
			request.setAttribute("mensagem", msg);
			return mapping.findForward(FALHA);
		}
	}
}