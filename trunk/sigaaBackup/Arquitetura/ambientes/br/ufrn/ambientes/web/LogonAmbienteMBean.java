/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 16/04/2010
 */
package br.ufrn.ambientes.web;

import javax.servlet.http.Cookie;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.ambientes.dominio.ConstantesParametrosAmbientes;
import br.ufrn.ambientes.dominio.TipoAmbiente;
import br.ufrn.ambientes.dominio.UsuarioAmbiente;
import br.ufrn.ambientes.negocio.MovimentoLogonAmbiente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.comum.dominio.Sistema;

/**
 * Managed bean para realizar o logon de um usuário em
 * um tipo de ambiente.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Component @Scope("request")
public class LogonAmbienteMBean extends AbstractController {

	/** Login digitado pelo usuário na página de logon */
	private String login;
	
	/** Senha digitado pelo usuário na página de logon */
	private String senha;
	
	/** URL para a qual o usuário será redirecionado */
	private String url;

	private TipoAmbiente ambienteAtual;
	
	/**
	 * Realiza o logon do usuário.
	 * Método utilizado pelas seguintes JSP(s):
	 * <ul>
	 * 	<li>/ambientes/login.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String login() throws ArqException {
		
		prepareMovimento(ArqListaComando.LOGON_AMBIENTE);
		
		MovimentoLogonAmbiente mov = new MovimentoLogonAmbiente();
		mov.setLogin(login);
		mov.setSenha(senha);
		mov.setIp(getCurrentRequest().getRemoteAddr());
		mov.setIpInternoNat(getCurrentRequest().getHeader("X-FORWARDED-FOR"));
		mov.setServer(NetworkUtils.getLocalName());
		mov.setUserAgent(getCurrentRequest().getHeader("User-Agent"));
		mov.setResolucao(getParameter("width") + "x" + getParameter("height"));
		
		if (url == null)
			url = "/admin/public/loginunificado/index.jsf";
		
		try {
			UsuarioAmbiente usuario = executeWithoutClosingSession(mov);
			
			Cookie cookie = new Cookie("usuarioAmbiente", usuario.getLogin());
			cookie.setPath("/");
			cookie.setMaxAge(3 * 60 * 60); // 3 horas
			getCurrentResponse().addCookie(cookie);
			
			return redirectSemContexto(url);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}		
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public TipoAmbiente getAmbienteAtual() throws DAOException {
		if (ambienteAtual == null) {
			int idAmbiente = ParametroHelper.getInstance().getParametroInt(ConstantesParametrosAmbientes.TIPO_AMBIENTE_ATUAL);
			GenericDAO dao = getDAO(GenericDAOImpl.class, Sistema.AMBIENTES);
			ambienteAtual = dao.findByPrimaryKey(idAmbiente, TipoAmbiente.class);
		}
		return ambienteAtual;
	}
	
}
