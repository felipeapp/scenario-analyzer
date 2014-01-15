package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Collection;

import br.ufrn.arq.util.EqualsUtil;

/**
 * DTO com informações referentes aos Cursos de Lato Sensu utilizado para comunicação entre
 * SIGAA e SIGPRH durante a criação de projetos de cursos e concursos vinculados a estes cursos.
 * @author Diogo Souto
 */
public class CursoLatoDTO implements Serializable {

	/**
	 * Identificador
	 */
	private int id;

	/**
	 * Nome do curso
	 */
	private String nome;

	/**
	 * Unidade a qual o curso pertence.
	 */
	private int idUnidade;

	/**
	 * Servidores coordenadores do curso de lato.
	 */
	private Collection<Integer> servidoresCoordenadores;

	public CursoLatoDTO() {
	}

	public CursoLatoDTO(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getIdUnidade() {
		return idUnidade;
	}

	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}

	public Collection<Integer> getServidoresCoordenadores() {
		return servidoresCoordenadores;
	}

	public void setServidoresCoordenadores(
			Collection<Integer> servidoresCoordenadores) {
		this.servidoresCoordenadores = servidoresCoordenadores;
	}
}