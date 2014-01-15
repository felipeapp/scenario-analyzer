/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Data Transfer Object para informa��es de registro de entrada.
 * 
 * @author David Pereira
 *
 */
public class RegistroEntradaDTO implements Serializable {

	/** Identificador */
	private int id;

	/** Usu�rio associado � entrada no sistema */
	private Integer idUsuario;

	/** Data do registro de entrada */
	private Date data;

	/** IP da m�quina do registro de entrada */
	private String IP;
	
	/** IP da m�quina dentro de uma rede com NAT */
	private String ipInternoNat;

	/** Servidor do Cluster que o usu�rio logou */
	private String server;

	/** Informa��es do Browser do Cliente */
	private String userAgent;

	/** Resolu��o do monitor que o cliente possui */
	private String resolucao;

	/** Canal de acesso: (W) Web, (M) Mobile, (D) Desktop */
	private String canal;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getIpInternoNat() {
		return ipInternoNat;
	}

	public void setIpInternoNat(String ipInternoNat) {
		this.ipInternoNat = ipInternoNat;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
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

	public String getCanal() {
		return canal;
	}

	public void setCanal(String canal) {
		this.canal = canal;
	}
	
	
}
