/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 01/09/2011
 */
package br.ufrn.comum.dominio;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO para representação das principais informações de um questionário.
 * 
 * @author David Pereira
 *
 */
public class QuestionarioAplicadoDTO implements Serializable {

	private static final long serialVersionUID = -1L;
	
	private int id;
	
	private int idQuestionario;
	
	private String titulo;
	
	private String descricao;
	
	private String identificador;
	
	private Date inicio;
	
	private Date fim;
	
	private boolean ativo;

	private int idGrupoDestinatarios;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdQuestionario() {
		return idQuestionario;
	}

	public void setIdQuestionario(int idQuestionario) {
		this.idQuestionario = idQuestionario;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public int getIdGrupoDestinatarios() {
		return idGrupoDestinatarios;
	}

	public void setIdGrupoDestinatarios(int idGrupoDestinatarios) {
		this.idGrupoDestinatarios = idGrupoDestinatarios;
	}

}
