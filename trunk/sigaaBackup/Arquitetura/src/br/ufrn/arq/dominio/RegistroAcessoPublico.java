/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 18/05/2009
 */
package br.ufrn.arq.dominio;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Classe utilizada para registrar a entrada na parte pública do sistema.
 * 
 * @author Gleydson Lima
 * @author David Pereira
 * 
 */
@Entity
@Table( name="registro_acesso_publico", schema="comum"  )
public class RegistroAcessoPublico implements PersistDB {

	/** Identificador */
	private int id;

	/** Data do registro de entrada */
	private Date data;

	/** IP da máquina do registro de entrada */
	private String IP;

	/** Servidor do Cluster que o usuário logou */
	private String server;

	/** Informações do Browser do Cliente */
	private String userAgent;

	/** Resolução do monitor que o cliente possui */
	private String resolucao;
	
	/** Sistema que o usuário está usando */
	private Integer sistema;

	/** Nome do sistema em uso */
	private String nomeSistema;
	
	/** Canal de acesso: (W) Web, (M) Mobile, (D) Desktop */
	private String canal;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public void setIP(String ip) {
		IP = ip;
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

	public Integer getSistema() {
		return sistema;
	}

	public void setSistema(Integer sistema) {
		this.sistema = sistema;
	}

	public String getNomeSistema() {
		return nomeSistema;
	}

	public void setNomeSistema(String nomeSistema) {
		this.nomeSistema = nomeSistema;
	}
	
}