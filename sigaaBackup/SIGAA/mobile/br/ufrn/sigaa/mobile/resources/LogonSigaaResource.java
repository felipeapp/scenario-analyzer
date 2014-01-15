/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 26/10/2011
 */
package br.ufrn.sigaa.mobile.resources;

import java.util.Date;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mobile.utils.JSONProcessor;
import br.ufrn.arq.seguranca.autenticacao.TokenGenerator;
import br.ufrn.arq.seguranca.dominio.TokenAutenticacao;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dominio.UsuarioMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.mobile.dto.UsuarioDTO;
import br.ufrn.sigaa.mobile.dto.VinculoDTO;

import com.sun.jersey.core.util.Base64;

/**
 * Resource para logon no SIGAA.
 * 
 * @author David Pereira
 * 
 */
@Path("/logon")
public class LogonSigaaResource extends SigaaGenericResource {

	private TokenGenerator tokenGenerator;
	private long tokenExpirationInDays;

	public LogonSigaaResource(@Context ServletContext context) {
		tokenGenerator = (TokenGenerator) context.getAttribute("token");

		try {

			ResourceBundle res = PropertyResourceBundle.getBundle("br.ufrn.sigaa.mobile.resources.SIGAAMobile");
			tokenExpirationInDays = Integer.parseInt(res.getString("token.expiration.days"));
		} catch (Exception e) {
			tokenExpirationInDays = 30;
		}

	}

	/**
	 * Realiza logon no SIGAA através de uma requisição HTTP utilizando o método
	 * POST. Retorna uma representação em JSON do usuário logado ou erro 403
	 * (Forbidden) caso o logon não tenha sido realizado com sucesso.
	 * 
	 * @param login
	 * @param senha
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws Exception
	 */

	@GET
	@Path("/token")
	@Produces(MediaType.APPLICATION_JSON)
	public Response logonToken(@Context HttpHeaders headers) throws ArqException, NegocioException {
		Response response = null;

		List<String> authenticationTokenHeaders = headers.getRequestHeader("X-UserAuthenticationToken");
		List<String> loginHeaders = headers.getRequestHeader("X-UserLogin");

		try {

			String tokenCredentials[] = getTokenCredentials(authenticationTokenHeaders);

			if (tokenCredentials != null && tokenCredentials.length == 2 && loginHeaders != null && loginHeaders.size() == 1) {

				String tokenIdStr = tokenCredentials[0];
				int tokenId = Integer.parseInt(tokenIdStr);
				String tokenKey = tokenCredentials[1];
				String login = loginHeaders.get(0);

				if (tokenGenerator.isTokenValid(tokenId, tokenKey)) {

					TokenAutenticacao token = tokenGenerator.getToken(tokenId);
					Date tokenDate = token.getData();

					// Verifica se o token ainda está válido baseado no tempo de
					// validade do mesmo

					if ((System.currentTimeMillis() - tokenDate.getTime()) < tokenExpirationInDays * (24 * 60 * 60 * 1000)) {

						UsuarioDao dao = new UsuarioDao();

						Usuario usuario = dao.findByLogin(login);
						usuario.setRegistroEntrada(token.getEntrada());

						UsuarioDTO usuarioDTO = construirUsuarioDTO(usuario);
						usuarioDTO.setTokenAutenticacao(tokenIdStr+":"+tokenKey);

						iniciarSessao(usuario);

						response = Response.ok(JSONProcessor.toJSON(usuarioDTO)).build();
						
						dao.close();
					}

					else {

						tokenGenerator.invalidateToken(tokenId);
						response = Response.status(Status.PRECONDITION_FAILED).build();

						// tratar melhor o caso de expirado (pra solicitar um
						// novo login)

					}
				} else {
					// token invalido
					response = Response.status(Status.PRECONDITION_FAILED).build();
				}
			}

			else {
				// header invalido
				response = Response.status(Status.PRECONDITION_FAILED).build();
			}

		} catch (ArqException e) {
			if (!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
			// } catch (NegocioException e) {
			// if (!e.isNotificavel())
			// response = montarResponseException(e);
			// else
			// throw e;
		}

		return response;
	}

	/**
	 * Realiza logon no SIGAA através de uma requisição HTTP utilizando o método
	 * POST. Retorna uma representação em JSON do usuário logado ou erro 403
	 * (Forbidden) caso o logon não tenha sido realizado com sucesso.
	 * 
	 * @param login
	 * @param senha
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws Exception
	 */

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response logonUsuarioSenha(@Context HttpHeaders headers) throws ArqException, NegocioException {
		Response response = null;

		String userPasswordCredentials[] = getCredentials(headers);

		if (userPasswordCredentials != null && userPasswordCredentials.length == 2) {

			String login = userPasswordCredentials[0];
			String senha = userPasswordCredentials[1];

			try {

				if (login != null && senha != null && UserAutenticacao.autenticaUsuario(null, login, senha)) {

					Usuario usuarioLogin = new Usuario();

					usuarioLogin.setLogin(login);
					usuarioLogin.setSenha(senha);

					Usuario usuario = executarMovimentoLogin(usuarioLogin);

					UsuarioDTO usuarioDTO = construirUsuarioDTO(usuario);

					String tokenInfo = gerarTokenAutenticacao(usuario);
					usuarioDTO.setTokenAutenticacao(tokenInfo);

					response = Response.ok(JSONProcessor.toJSON(usuarioDTO)).build();
				} else {
					// autenticação invalida
					response = Response.status(Status.PRECONDITION_FAILED).build();
				}
			} catch (ArqException e) {
				if (!e.isNotificavel())
					response = montarResponseException(e);
				else
					throw e;
			} catch (NegocioException e) {
				if (!e.isNotificavel())
					response = montarResponseException(e);
				else
					throw e;
			}
		} else {
			// header invalido
			response = Response.status(Status.PRECONDITION_FAILED).build();
		}

		return response;
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response logoff(@Context HttpHeaders headers) throws ArqException, NegocioException {
		Response response = null;

		List<String> authenticationTokenHeaders = headers.getRequestHeader("X-UserAuthenticationToken");
		Usuario usuarioLogado = getUsuarioLogado();

		try {

			String tokenCredentials[] = getTokenCredentials(authenticationTokenHeaders);

			if (tokenCredentials != null && tokenCredentials.length == 2) {

				String tokenIdStr = tokenCredentials[0];
				int tokenId = Integer.parseInt(tokenIdStr);

				tokenGenerator.invalidateToken(tokenId);

				UsuarioMov mov = new UsuarioMov();
				mov.setCodMovimento(SigaaListaComando.LOGOFF);
				mov.setUsuario(usuarioLogado);
				mov.setUsuarioLogado(usuarioLogado);
				executarMovimento(mov);
				
				request.getSession().invalidate();

				response = Response.ok().build();
			}

			else {
				response = Response.status(Status.PRECONDITION_FAILED).build();
			}

		} catch (Exception e) {
			response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
			e.printStackTrace();

		}

		return response;
	}

	/*
	 * public ActionForward logOff(ActionMapping mapping, ActionForm form,
	 * HttpServletRequest request, HttpServletResponse response) throws
	 * Exception { try { if (request.getSession() != null) { UsuarioMov mov =
	 * new UsuarioMov(); mov.setCodMovimento(SigaaListaComando.LOGOFF);
	 * mov.setUsuario(getUsuarioLogado(request));
	 * mov.setUsuarioLogado(getUsuarioLogado(request)); execute(mov, request);
	 * request.getSession().invalidate(); } } catch (Exception e) {
	 * e.printStackTrace(); }
	 * 
	 * return mapping.findForward(TELA_PUBLICA); }
	 */
	private UsuarioDTO construirUsuarioDTO(Usuario usuario) throws ArqException {
		UsuarioDTO usuarioDTO = new UsuarioDTO();

		usuarioDTO.setId(usuario.getId());
		usuarioDTO.setLogin(usuario.getLogin());

		usuarioDTO.setNome(usuario.getPessoa().getNome());
		usuarioDTO.setEmail(usuario.getPessoa().getEmail());
		usuarioDTO.setRegistroEntrada(usuario.getRegistroEntrada().getId());
		
		int numeroVinculos = VinculoResource.carregarInformacoesVinculos(usuario);
		
		//usuarioDTO.setNumeroVinculos(numeroVinculos);
		
		if(numeroVinculos == 1){
			VinculoDTO vinculoAtivo = VinculoResource.montarVinculo(usuario, usuario.getVinculoAtivo());
			usuarioDTO.setVinculoAtivo(vinculoAtivo);
		}
		
		if (usuario.getIdFoto() != null) {
			usuarioDTO.setIdFoto(usuario.getIdFoto());
			usuarioDTO.setChaveFoto(UFRNUtils.generateKey(usuario.getIdFoto().toString()));
		} else {
			usuarioDTO.setIdFoto(0);
			usuarioDTO.setChaveFoto("0");
		}
		return usuarioDTO;
	}

	private String gerarTokenAutenticacao(Usuario usuario) {
		String deviceInfo = request.getHeader("DEVICE-INFO");
		TokenAutenticacao token = tokenGenerator.generateToken(usuario, Sistema.SIGAA, deviceInfo);
		tokenGenerator.registerToken(token);
		String tokenInfo = token.getId() + ":" + token.getKey();

		return tokenInfo;
	}

	private void iniciarSessao(Usuario usuario) {

		HttpSession session = request.getSession();

		session.setAttribute("device", true);
		session.setAttribute("usuario", usuario);
		
		//Necessário para acessar corretamente as páginas web a partir do aplicativo
		DadosAcesso acesso = new DadosAcesso(usuario);		
		session.setAttribute("acesso", acesso);
	}

	private Usuario executarMovimentoLogin(Usuario usuario) throws NegocioException, ArqException {

		String deviceInfo = request.getHeader("DEVICE-INFO");

		UsuarioMov mov = new UsuarioMov();
		mov.setCodMovimento(SigaaListaComando.LOGON);
		mov.setIP(request.getRemoteAddr());
		mov.setIpInternoNat(request.getHeader("X-FORWARDED-FOR"));
		mov.setUsuario(usuario);
		mov.setHost(NetworkUtils.getLocalName());
		mov.setUserAgent(request.getHeader("User-Agent"));
		mov.setCanal(RegistroEntrada.CANAL_DEVICE);
		mov.setResolucao(deviceInfo);
		mov.setRequest(request);

		usuario = (Usuario) executarMovimento(mov);

		iniciarSessao(usuario);

		return usuario;
	}

	private String[] getCredentials(HttpHeaders headers) {
		List<String> header = headers.getRequestHeader("authorization");

		if (header != null && !header.isEmpty()) {
			String auth = header.get(0);

			auth = auth.substring("Basic ".length());
			String[] values = new String(Base64.base64Decode(auth)).split(":");

			return values;
		}

		return null;
	}

	private String[] getTokenCredentials(List<String> authenticationTokenHeaders) {

		String[] values = null;

		if (authenticationTokenHeaders != null && authenticationTokenHeaders.size() > 0) {

			String authenticationToken = authenticationTokenHeaders.get(0);

			values = new String(authenticationToken).split(":");
		}

		return values;
	}

}
