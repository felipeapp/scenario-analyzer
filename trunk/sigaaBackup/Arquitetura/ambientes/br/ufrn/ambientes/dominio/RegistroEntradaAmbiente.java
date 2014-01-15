/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 16/04/2010
 */
package br.ufrn.ambientes.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;


/**
 * Classe utilizada para registrar as informações sobre cada vez 
 * que um usuário entra em um ambiente (que não o de produção) que 
 * possua segurança ativa.
 *  
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Entity @Table(name="registro_entrada_ambiente", schema="comum")
public class RegistroEntradaAmbiente implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="comum.registro_entrada_ambiente_seq") })
	private int id;
	
	/** Usuário que acessou o ambiente */
	@ManyToOne @JoinColumn(name="id_usuario_ambiente")
	private UsuarioAmbiente usuario;
	
	/** Data de acesso */
	private Date data;
	
	/** IP do usuário */
	private String ip;

	/** IP da máquina dentro de uma rede com NAT */
	@Column(name="ip_interno_nat")
	private String ipInternoNat;
	
	/** Servidor no qual o usuário se logou */
	private String server;
	
	/** Informações do Browser do Cliente */
	@Column(name="user_agent")
	private String userAgent;
	
	/** Resolução utilizada no monitor do usuário */
	private String resolucao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UsuarioAmbiente getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioAmbiente usuario) {
		this.usuario = usuario;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
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
	
}
