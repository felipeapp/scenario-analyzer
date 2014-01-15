/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Livraria
 * Data de Criação: 06/07/2009
 */
package br.ufrn.sigaa.mobile.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.sigaa.arq.dominio.UsuarioMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;

/**
 * Action para realizar o logon nos serviços mobile. 
 * 
 * @author David Pereira
 *
 */
public class LogonMobileAction extends SigaaAbstractAction {

	private static final String MENU_MOBILE = "menuMobile";
	private static final String SELECIONA_VINCULO = "selecionaVinculo";
	private static final String FORM_LOGON = "logonMobile";

	
	
	/**
	 *     Faz um login na parte mobile do sistema.
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward logon(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		try{
		
			String login = req.getParameter("login");
			String senha = req.getParameter("senha");
			
			if(isEmpty(login)){
				erro("Digite o login do usuário", req);
				return mapping.findForward(FORM_LOGON);
			}
			
			if(isEmpty(senha)){
				erro("Digite a senha do usuário", req);
				return mapping.findForward(FORM_LOGON);
			}
			
			// Autentica o usuário
	 		boolean autenticado = UserAutenticacao.autenticaUsuarioMobile(req, login, senha);

			if (autenticado) {
				int id = UserAutenticacao.getIdUsuario(req, login);
				Usuario usuario = new Usuario();
				usuario.setId(id);
				usuario.setLogin(login);
				usuario.setSenha(senha);
				
				UsuarioMov mov = new UsuarioMov();
				mov.setCodMovimento(SigaaListaComando.LOGON);
				mov.setIP(req.getRemoteAddr());
				mov.setUsuario(usuario);
				mov.setHost(NetworkUtils.getLocalName());
				mov.setUserAgent(req.getHeader("User-Agent"));
				mov.setRequest(req);
				mov.setCanal(RegistroEntrada.CANAL_WAP);
				
				try {
					req.getSession().invalidate();
					req.getSession().setAttribute("usuario", usuario);
					//req.getSession().removeAttribute("usuario");
					
					usuario = (Usuario) execute(mov, req);
					if (usuario == null) {
						erro("Usuário/Senha Inválidos", req);
					} else {
						req.getSession().setAttribute("usuario", usuario);
						req.getSession().setAttribute("mobile", true);
						// carrega os parametros de calendário
						carregarParametrosCalendarioAtual(req);
					}
	
				} catch (NegocioException e) {
					req.getSession().invalidate();
					erro(e.getMessage(), req);
					return mapping.findForward(FORM_LOGON);
				}
				
				if(usuario != null){
					
					List<VinculoUsuario> vinculos = VinculoUsuario.processarVinculosUsuario(usuario, req);
					usuario.setVinculos(vinculos);
		
					boolean multiplosVinculos = usuario.getVinculosPrioritarios().size() > 1;
		
					if (multiplosVinculos) {
						return mapping.findForward(SELECIONA_VINCULO);
					} else {
						/* Se  possuir  apenas um  vínculo  prioritário,  usá-lo */
						if (usuario.getVinculoAtivo() == null || usuario.getVinculoAtivo().getNumero() == 0) {
							
							if(usuario.getVinculosPrioritarios().size() == 0){
								erro("Usuário não possui vínculos ativos", req);
								req.getSession().invalidate();
								return mapping.findForward(FORM_LOGON);
							}else{
								usuario.setVinculoAtivo(usuario.getVinculosPrioritarios().get(0));
							}
							
						} else {
							if (usuario.getVinculos().isEmpty()) { // Se não possuir vinculos
								req.getSession().invalidate();
								erro("Usuário não possui acesso mobile", req);
								return mapping.findForward(FORM_LOGON);
							} else {
								// Pega qualquer vínculo não prioritário
								if (usuario.getVinculoAtivo() == null || usuario.getVinculoAtivo().getNumero() == 0)
									usuario.setVinculoAtivo(usuario.getVinculos().get(0));
							}
						}
						
						DadosAcessoMobile acesso = new DadosAcessoMobile(usuario);
						req.getSession().setAttribute("acesso", acesso);
						return mapping.findForward(MENU_MOBILE);
					}
					
				}else{
					erro("Erro ao realizar o login", req);
					req.getSession().invalidate();
					return mapping.findForward(FORM_LOGON);
				}
				
			} else { // Usuário não autenticado
				erro("Usuário ou senha inválidos.", req);
				req.getSession().invalidate();
				return mapping.findForward(FORM_LOGON);
			}
		
		}catch(Exception ex){
			erro("Erro ao realizar o login", req);
			ex.printStackTrace();
			req.getSession().invalidate();
			return mapping.findForward(FORM_LOGON);
		}	
			
	}
	
	
	
	/**
	 *    Realiza o logoff do sistema mobile
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward logoff(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		try {
			UsuarioMov mov = new UsuarioMov();
			mov.setCodMovimento(SigaaListaComando.LOGOFF);
			mov.setUsuario(getUsuarioLogado(req));
			mov.setUsuarioLogado(getUsuarioLogado(req));

			execute(mov, req);

			req.getSession().removeAttribute("usuario");
			req.getSession().removeAttribute("acesso");

			return mapping.findForward(FORM_LOGON);
			
		} catch(Exception e) {
			e.printStackTrace();
			erro("Erro ao efetuar o logoff do sistema.", req);
			return mapping.findForward(FORM_LOGON);
			
		}finally{
			req.getSession().invalidate();
			
		}
	}
	
	
	
	public ActionForward vinculo(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Usuario usuario = (Usuario) getUsuarioLogado(req);
		int vinculo = RequestUtils.getIntParameter(req, "vinculo", 0);
		usuario.setVinculoAtivo(vinculo);
		
		DadosAcessoMobile acesso = new DadosAcessoMobile(usuario);
		req.getSession().setAttribute("acesso", acesso);
		return mapping.findForward(MENU_MOBILE);
	}
	
	
	public void erro(String mensagem, HttpServletRequest req) {
		req.setAttribute("mensagem", mensagem);
	}

}
