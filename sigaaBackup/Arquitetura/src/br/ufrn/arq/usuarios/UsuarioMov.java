/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/06/2007
 */
package br.ufrn.arq.usuarios;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.arq.seguranca.dominio.PassaporteLogon;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Esta classe adapta um Usuário para um Movimento, isso ocorre pois um usuário
 * carrega as informações para ativação de um comando mas não é persistente.
 *
 * @author Gleydson Lima
 * @author David Pereira
 *
 */
public class UsuarioMov extends AbstractMovimentoAdapter {

	private UsuarioGeral usuario;

	private String IP;
	
	private String ipInternoNat;

	private String host;

	private String userAgent;

	private String resolucao;

	private PassaporteLogon passaporte;

	private boolean apenasProtocolo;
	
	private String motivo;
	
	/** Enviado aos processadores para poder acessar os métodos de autenticação. */
	private HttpServletRequest request;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
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

	public String getResolucao() {
		return resolucao;
	}

	public void setResolucao(String resolucao) {
		this.resolucao = resolucao;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public UsuarioGeral getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioGeral usuario) {
		this.usuario = usuario;
	}

	public boolean isApenasProtocolo() {
		return apenasProtocolo;
	}

	public void setApenasProtocolo(boolean apenasProtocolo) {
		this.apenasProtocolo = apenasProtocolo;
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
	
}
