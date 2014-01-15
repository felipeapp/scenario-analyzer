/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Created on 14/04/2005
 *
 */
package br.ufrn.sigaa.arq.dominio;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.dominio.PassaporteLogon;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Esta classe adapta um Usuário para um Movimento, isso ocorre pois um usuário
 * carrega as informações para ativação de um comando mas não é persistente.
 * 
 * @author Gleydson Lima
 * 
 */
public class UsuarioMov extends AbstractMovimentoAdapter {

	private Usuario usuario;

	private String IP;

	private String ipInternoNat;

	private String host;

	private String userAgent;

	private String motivo;

	private PassaporteLogon passaporte;

	private String resolucao;

	private String canal = RegistroEntrada.CANAL_WEB;

	/** Enviado aos processadores para poder acessar os métodos de autenticação. */
	private HttpServletRequest request;

	/**
	 * Indica se o processador de Logon irá ou não transformar a senha em hash
	 * antes de fazer a comparação da senha informada com a senha do banco.
	 * 
	 * Utilizado pelos aplicativos desktops, que enviam uma senha em hash md5
	 * para o servidor.
	 */
	private boolean autenticarComHash = true;

	public boolean isWebMobile() {
		if (canal != null) {
			return canal.equals(RegistroEntrada.CANAL_WEB_MOBILE);
		}
		return false;
	}
	
	public boolean isWap() {
		if (canal != null) {
			return canal.equals(RegistroEntrada.CANAL_WAP);
		}
		return false;
	}

	/**
	 * Caso o canal de acesso não esteja configurado para ser wap ou desktop,
	 * retorna o padrão que é web.
	 * 
	 * @return
	 */
	public boolean isWeb() {

		if (canal != null) {
			return canal.equals(RegistroEntrada.CANAL_WEB);
		}
		return false;

	}

	@Override
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String ip) {
		IP = ip;
	}

	public PassaporteLogon getPassaporte() {
		return passaporte;
	}

	public void setPassaporte(PassaporteLogon passaporte) {
		this.passaporte = passaporte;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getResolucao() {
		return resolucao;
	}

	public void setResolucao(String resolucao) {
		this.resolucao = resolucao;
	}

	public boolean isAutenticarComHash() {
		return autenticarComHash;
	}

	public void setAutenticarComHash(boolean autenticarComHash) {
		this.autenticarComHash = autenticarComHash;
	}

	public String getIpInternoNat() {
		return ipInternoNat;
	}

	public void setIpInternoNat(String ipInternoNat) {
		this.ipInternoNat = ipInternoNat;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getCanal() {
		return canal;
	}

	public void setCanal(String canal) {
		this.canal = canal;
	}

}