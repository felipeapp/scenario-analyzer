/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 14/04/2010
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * Data Transfer Object para armazenar as
 * informações das permissões de um usuário. Utilizado
 * na integração entre sistemas através de chamada
 * remota de métodos.
 * 
 * @author David Pereira
 *
 */
public class PermissaoDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	private int id;
	
	private int idPapel;
	
	private String nomePapel;
	
	private int idUsuario;
	
	private String loginUsuario;
	
	private Integer unidadePermissao;
	
	private Integer designacao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdPapel() {
		return idPapel;
	}

	public void setIdPapel(int idPapel) {
		this.idPapel = idPapel;
	}

	public String getNomePapel() {
		return nomePapel;
	}

	public void setNomePapel(String nomePapel) {
		this.nomePapel = nomePapel;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getLoginUsuario() {
		return loginUsuario;
	}

	public void setLoginUsuario(String loginUsuario) {
		this.loginUsuario = loginUsuario;
	}

	public Integer getUnidadePermissao() {
		return unidadePermissao;
	}

	public void setUnidadePermissao(Integer unidadePermissao) {
		this.unidadePermissao = unidadePermissao;
	}

	public Integer getDesignacao() {
		return designacao;
	}

	public void setDesignacao(Integer designacao) {
		this.designacao = designacao;
	}
	
}
