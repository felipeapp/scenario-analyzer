package br.ufrn.integracao.dto;

import java.io.Serializable;

import br.ufrn.arq.util.EqualsUtil;

/**
 * DTO com informações referentes às Turmas dos cursos de Lato Sensu utilizado para comunicação entre SIGAA e SIGPRH
 * durante a criação de requisições de pagamento de projetos de cursos e concursos vinculados a cursos de Lato Sensu.
 * @author Diogo Souto
 */
public class TurmaLatoDTO implements Serializable {

	/**
	 * Identificador da turma
	 */
	private int id;

	/**
	 * Nome da turma
	 */
	private String nome;

	/**
	 * Situação da turma
	 */
	private String situacao;

	/**
	 * Carga Horária total da turma
	 */
	private Integer cargaHoraria;

	public TurmaLatoDTO() {
	}

	public TurmaLatoDTO(int id) {
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

	public void setNome(String denominacao) {
		this.nome = denominacao;
	}

	public Integer getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCargaHoraria(Integer cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
}