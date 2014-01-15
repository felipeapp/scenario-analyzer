/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Criado em: 12/02/2008
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;


/**
 * Classe que representa um interessado simplificado de um processo
 * 
 * @author Itamir Filho
 *
 */
public class InteressadoDTO implements Serializable {
	
	private Integer id;
	
	private String identificador;
	
	private String nome;
	
	private Integer idTipo;
	
	private Integer idPessoa;
	
	private Integer idServidor;
	
	private Integer idAluno;
	
	private Integer idUnidade;
	
	private boolean ativo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(Integer idTipo) {
		this.idTipo = idTipo;
	}

	public Integer getIdPessoa() {
		return idPessoa;
	}

	public void setIdPessoa(Integer idPessoa) {
		this.idPessoa = idPessoa;
	}

	public Integer getIdServidor() {
		return idServidor;
	}

	public void setIdServidor(Integer idServidor) {
		this.idServidor = idServidor;
	}

	public Integer getIdAluno() {
		return idAluno;
	}

	public void setIdAluno(Integer idAluno) {
		this.idAluno = idAluno;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Integer getIdUnidade() {
		return idUnidade;
	}

	public void setIdUnidade(Integer idUnidade) {
		this.idUnidade = idUnidade;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final InteressadoDTO other = (InteressadoDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (id.intValue() != other.id.intValue())
			return false;
		return true;
	}
	
}
