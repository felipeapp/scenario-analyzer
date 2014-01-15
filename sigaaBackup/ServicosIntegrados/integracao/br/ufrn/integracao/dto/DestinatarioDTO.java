/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * Data transfer object para guardar as informações
 * de destinatários de notificações. Ver classe Destinatario
 * do SIGAdmin.
 * 
 * @author David Pereira
 *
 */
public class DestinatarioDTO implements Serializable {

	private String nome;
	
	private String email;
	
	private UsuarioDTO usuario;
	
	public DestinatarioDTO() {
		
	}

	public DestinatarioDTO(UsuarioDTO usuario) {
		this.usuario = usuario;
	}	
	
	public DestinatarioDTO(String nome, String email) {
		this.nome = nome;
		this.email = email;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UsuarioDTO getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioDTO usuario) {
		this.usuario = usuario;
	}

}
