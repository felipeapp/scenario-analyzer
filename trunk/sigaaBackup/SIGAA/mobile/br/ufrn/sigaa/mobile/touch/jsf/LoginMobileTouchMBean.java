/*
 * Universidade Federal do Rio Grande do Norte

 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '28/09/2011'
 *
 */
package br.ufrn.sigaa.mobile.touch.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.caixa_postal.MensagemDAO;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.util.ArrayUtils;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.dominio.UsuarioMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;

import com.sun.star.lang.IllegalArgumentException;

/**
 * MBean respons�vel por controlar opera��es ligadas as turmas do 
 * discente com acesso ao sistema via dispositivos m�veis. 
 * 
 * @author ilueny santos
 *
 */
@Component("loginMobileTouch")
@Scope("request")
public class LoginMobileTouchMBean extends TurmaVirtualTouchMBean<Discente> {

	/** Login do usu�rio. */
	private String login = null;
	
	/** Senha do usu�rio. */	
	private String senha = null;
	
	/** Informa se o usu�rio possui apenas um v�nculo. */
	private boolean unicoVinculo = false;
	
	
	/** Construtor Padr�o.*/
	public LoginMobileTouchMBean () {		
	}

	/**
	 * Efetua o login no sistema, enviando para a tela de sele��o de v�nculo ou ao menu mobile touch.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\mobile\touch\menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String login () throws ArqException {
		
		HttpServletRequest request = getCurrentRequest();
		
		unicoVinculo = false;
		
		Usuario usuario = new Usuario();
		usuario.setLogin(login);
		usuario.setSenha(senha);
		
		prepareMovimento(SigaaListaComando.LOGON);
		
		UsuarioMov mov = new UsuarioMov();
		mov.setCodMovimento(SigaaListaComando.LOGON);
		mov.setIP(request.getRemoteAddr());
		mov.setIpInternoNat(request.getHeader("X-FORWARDED-FOR"));
		mov.setUsuario(usuario);
		mov.setHost(NetworkUtils.getLocalName());
		mov.setUserAgent(request.getHeader("User-Agent"));
		mov.setRequest(request);
		mov.setCanal(RegistroEntrada.CANAL_WEB_MOBILE);
		
		request.setAttribute("NO_LOGGER", Boolean.TRUE);
		request.getSession().invalidate();
		
		try {
			usuario = (Usuario) execute(mov, request);
			request.getSession(true).setAttribute("usuario", usuario);
		} catch (NegocioException e){
			addMensagemErro(e.getMessage());
			return null;
		}
		
		if (usuario == null) {
			addMensagemErro("Usu�rio/Senha inv�lidos");
			return null;
		}

		request.getSession(true).setAttribute("usuario", usuario);
		request.getSession(false).setAttribute("sistema", Sistema.SIGAA);
		configurarAcessoMobile(request, usuario);			
		
		if (!UserAutenticacao.usuarioAtivo(request, Sistema.SIGAA, usuario.getId())) {
			addMensagemErro("Usu�rio n�o autorizado a acessar o sistema.");
			return null;
		}
		
		if (UserAutenticacao.senhaExpirou(request, usuario.getId()))		
			return redirect ("/sigaa/alterar_dados.jsf?expirou=true");
		
		List<VinculoUsuario> vinculos = VinculoUsuario.processarVinculosUsuario(usuario, request);
		for (Iterator<VinculoUsuario> it = vinculos.iterator(); it.hasNext();) {
			VinculoUsuario vinculoUsuario = it.next();
			
			if (vinculoUsuario.getDiscente() != null) { 
				if ((vinculoUsuario.getDiscente().getStatus() == StatusDiscente.CADASTRADO &&
					!isAlunoPossuiSolicitacaoMatricula(vinculoUsuario.getDiscente().getDiscente(), request)) ||
					ArrayUtils.idContains(vinculoUsuario.getDiscente().getStatus(), new int[] {StatusDiscente.CANCELADO, StatusDiscente.EXCLUIDO} ))
				
				it.remove();
			}
		}
		
		if (vinculos == null || vinculos.isEmpty()){
			addMensagemErro("� necess�rio ter pelo menos um V�nculo Ativo para acessar o SIGAA Mobile.");
			return null;
		}
		
		usuario.setVinculos(vinculos);

		boolean multiplosVinculos = usuario.getVinculosPrioritarios().size() > 1;
		
		if (!multiplosVinculos){
			if (!isEmpty(usuario.getVinculosPrioritarios()))
				usuario.setVinculoAtivo(usuario.getVinculosPrioritarios().get(0).getNumero());
			else
				usuario.setVinculoAtivo(usuario.getVinculos().get(0).getNumero());
			
			unicoVinculo = true;
			return escolherVinculo();
		} else
			// Se o usu�rio tiver m�ltiplos v�nculos com a institui��o, ir�
			// para uma tela de escolha do v�nculo com o qual ele quer trabalhar
			return forward ("/mobile/touch/vinculos.jsf");
	}

	
	
	/** Configurando acesso Mobile. */
	public void configurarAcessoMobile(HttpServletRequest request, Usuario usuario) {
		DadosAcesso acesso = new DadosAcesso(usuario);
		//TODO: definir acesso do usu�rio para permitir altera��o para modo cl�ssico.		
		request.getSession(false).setAttribute("acesso", acesso);
	}
	
	/**
	 * Define o v�nculo do usu�rio para saber se deve redirecion�-lo ao portal do docente ou discente.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\mobile\touch\menu.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String escolherVinculo () throws DAOException {
		
		MensagemDAO dao = getDAO(MensagemDAO.class);

		try {
			Usuario usuario = (Usuario) getUsuarioLogado();
			
			// Se o usu�rio s� tinha um v�nculo, n�o foi direcionado � tela de escolha de v�nculos.
			if (!unicoVinculo){
				Integer vinculo = getParameterInt("vinculo");
				if (isEmpty(vinculo)) throw new NegocioException("Nenhum v�nculo foi escolhido.");
				usuario.setVinculoAtivo(vinculo);
			}

			VinculoUsuario.popularVinculoAtivo(usuario);
			
			if (usuario.getVinculoAtivo().isVinculoServidor()) {
				usuario.setServidor(usuario.getVinculoAtivo().getServidor());
				usuario.setDiscente(null);
			} else if (usuario.getVinculoAtivo().isVinculoDiscente()) {
				usuario.setDiscente(usuario.getVinculoAtivo().getDiscente().getDiscente());
				usuario.setServidor(null);
			} else if (usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
				usuario.setDocenteExterno(usuario.getVinculoAtivo().getDocenteExterno());
				usuario.setDiscente(null);
			} /*else if (usuario.getVinculoAtivo().isVinculoTutorOrientador()) {
				usuario.setTutor(usuario.getVinculoAtivo().getTutor());
			} else if (usuario.getVinculoAtivo().getTipoVinculo().isFamiliar()){
				usuario.setDiscente(usuario.getVinculoAtivo().getFamiliar().getDiscenteMedio().getDiscente());
			}*/ else{
				usuario.setDiscente(null);
				usuario.setDocenteExterno(null);
				usuario.setServidor(null);
			}

			HttpServletRequest request = getCurrentRequest();			
			request.setAttribute("usuario", usuario);
			
			// Decide se vai para a tela de docente ou discente.
			return acessarPortalMobile(usuario, request);
			
		} catch (NegocioException e){
			addMensagemErro(e.getMessage());
			return null;
		} finally {
			dao.close();
		}
	}
	
	
	/**
	 * M�todo para verifica��o e redirecionamento do us�rio para o portal adequado.
	 * Utilizado na opera��o de retorno ao Modo Mobile.
	 * N�o invocado por JSP's
	 * @param usuario
	 * @return
	 * @throws DAOException 
	 * @throws NegocioException 
	 * @throws IllegalArgumentException 
	 */
	public String acessarPortalMobile(Usuario usuario, HttpServletRequest request) throws DAOException, NegocioException {
		if (usuario == null) {
			throw new NegocioException("N�o autenticado.");
		}
		
		if (usuario.getDiscente() != null) {			
			DiscenteAdapter discente = usuario.getDiscenteAtivo();
			usuario.setNivelEnsino(discente.getNivel());
			request.getSession().setAttribute("nivel", discente.getNivel());
			
			ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(discente);
			request.getSession().setAttribute(PARAMETROS_SESSAO, param);				
			
			PortalDiscenteTouchMBean disBean = getMBean("portalDiscenteTouch");				
			disBean.setDiscente(usuario.getDiscente().getDiscente());
			return disBean.acessarPortal();
			
		} else if (usuario.getServidor() != null) {			
			PortalDocenteTouchMBean docBean = getMBean("portalDocenteTouch");				
			docBean.setServidor(usuario.getServidor());
			docBean.setDocenteExterno(usuario.getDocenteExterno());
			return docBean.acessarPortal();	
			
		} else {			
			throw new NegocioException("M�dulo Mobile atualmente dispon�vel somente para Docentes e Discentes da Institui��o.");
			
		}
		
	}
		
	/** Verifica se o Discente possui solicita��o de matr�cula. */
	private boolean isAlunoPossuiSolicitacaoMatricula(Discente discente, HttpServletRequest req) throws ArqException {
		SolicitacaoMatriculaDao matDao = getDAO(SolicitacaoMatriculaDao.class);
		try {
			int count = matDao.countByDiscenteAnoPeriodo(discente, 
					discente.getAnoIngresso(), 
					discente.getPeriodoIngresso(), 
					SolicitacaoMatricula.CADASTRADA, 
					SolicitacaoMatricula.ORIENTADO, 
					SolicitacaoMatricula.SUBMETIDA,
					SolicitacaoMatricula.VISTO_EXPIRADO,
					SolicitacaoMatricula.SOLICITADA_COORDENADOR,
					SolicitacaoMatricula.VISTA);
			
			if (count == 0)
				return false;
			
			return true;			
		} finally {
			matDao.close();
		}
	}	
	
	/**
	 *  Realiza o logoff do sistema mobile
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\mobile\touch\portal_discente.jsp</li>
	 *    <li>sigaa.war\mobile\touch\portal_docente.jsp</li>
	 * </ul>
	 * @throws Exception
	 */
	public String logoff () throws Exception {
		
		try {
			UsuarioMov mov = new UsuarioMov();
			mov.setCodMovimento(SigaaListaComando.LOGOFF);
			mov.setUsuario(getUsuarioLogado());
			mov.setUsuarioLogado(getUsuarioLogado());
			execute(mov);

			getCurrentRequest().getSession().removeAttribute("usuario");
			getCurrentRequest().getSession().removeAttribute("acesso");

			return forwardPaginaLogin();
			
		} catch(Exception e) {			
			addMensagemErro("Erro ao realizar logoff.");
			return null;
		}
	}
	
	public String forwardPaginaLogin() {
		return forward ("/mobile/touch/login.jsf");
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

	public boolean isUnicoVinculo() {
		return unicoVinculo;
	}

	public void setUnicoVinculo(boolean unicoVinculo) {
		this.unicoVinculo = unicoVinculo;
	}

}