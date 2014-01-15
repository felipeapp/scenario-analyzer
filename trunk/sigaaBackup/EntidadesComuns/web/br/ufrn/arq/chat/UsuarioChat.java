/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 11/04/2011
 */
package br.ufrn.arq.chat;

import java.io.Serializable;

/**
 * Classe com informações dos usuários que estão utilizando o chat.
 * 
 * @author David Pereira
 *
 */
public class UsuarioChat implements Serializable {
	
	private static final long serialVersionUID = -1L;

	private int id;
	
	private String login;
	
	private String nome;

	private Integer idFoto;
	
	private boolean ministrante;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getIdFoto() {
		return idFoto;
	}

	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}

	public boolean isMinistrante() {
		return ministrante;
	}

	public void setMinistrante(boolean ministrante) {
		this.ministrante = ministrante;
	}
}