/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Criado em: 12/02/2008
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe que representa um movimento de um processo.
 * @author Itamir Filho
 *
 */
public class MovimentoDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private Date dataEnvio;
	
	private UnidadeDTO undDestino;
	
	private Date dataRecebimento;
	
	private UnidadeDTO undOrigem;
	
	private int idProcesso;
	
	private String observacao;
	
	private Integer usuarioOrigem;
	
	private Integer usuarioDestino;

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}


	public Date getDataRecebimento() {
		return dataRecebimento;
	}

	public void setDataRecebimento(Date dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getIdProcesso() {
		return idProcesso;
	}

	public void setIdProcesso(int idProcesso) {
		this.idProcesso = idProcesso;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	
	public UnidadeDTO getUndDestino() {
		return undDestino;
	}

	public void setUndDestino(UnidadeDTO undDestino) {
		this.undDestino = undDestino;
	}

	public UnidadeDTO getUndOrigem() {
		return undOrigem;
	}

	public void setUndOrigem(UnidadeDTO undOrigem) {
		this.undOrigem = undOrigem;
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
		final MovimentoDTO other = (MovimentoDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (id.intValue() != other.id.intValue())
			return false;
		return true;
	}

	public Integer getUsuarioOrigem() {
		return usuarioOrigem;
	}

	public void setUsuarioOrigem(Integer usuarioOrigem) {
		this.usuarioOrigem = usuarioOrigem;
	}

	public Integer getUsuarioDestino() {
		return usuarioDestino;
	}

	public void setUsuarioDestino(Integer usuarioDestino) {
		this.usuarioDestino = usuarioDestino;
	}

}
