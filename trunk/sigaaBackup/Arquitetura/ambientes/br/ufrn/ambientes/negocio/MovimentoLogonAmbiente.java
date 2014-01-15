/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 16/04/2010
 */
package br.ufrn.ambientes.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.arq.negocio.ArqListaComando;

/**
 * Movimento para passar as informações do logon
 * do managed bean de logon em um ambiente para o processador
 * de logon em um ambiente.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class MovimentoLogonAmbiente extends AbstractMovimentoAdapter {

	public MovimentoLogonAmbiente() {
		setCodMovimento(ArqListaComando.LOGON_AMBIENTE);
	}
	
	private String login;
	
	private String senha;
	
	private String ip;
	
	private String ipInternoNat;
	
	private String userAgent;
	
	private String server;
	
	private String resolucao;

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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIpInternoNat() {
		return ipInternoNat;
	}

	public void setIpInternoNat(String ipInternoNat) {
		this.ipInternoNat = ipInternoNat;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getResolucao() {
		return resolucao;
	}

	public void setResolucao(String resolucao) {
		this.resolucao = resolucao;
	}
	
}
