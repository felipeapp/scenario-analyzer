/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Criado em: 12/02/2008
 */
package br.ufrn.integracao.dto;

import java.util.Date;

/**
 * Classe que representa um documento simplificado em um processo.
 * @author Itamir Filho
 *
 */
public class DocumentoDTO {
	
	private Integer id;
	
	private Date dataCadastro;
	
	private Date dataDocumento; 
	
	private int idTipoDocumento;
	
	private String tipoDocumenoDescricao;
	
	private String observacao;
	
	private String identificacao;
	
	private int idUnidade;
	
	private Integer anoIdent;
	
	private int idMovimentoAtual;

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getTipoDocumenoDescricao() {
		return tipoDocumenoDescricao;
	}

	public void setTipoDocumenoDescricao(String tipoDocumenoDescricao) {
		this.tipoDocumenoDescricao = tipoDocumenoDescricao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDataDocumento() {
		return dataDocumento;
	}

	public void setDataDocumento(Date dataDocumento) {
		this.dataDocumento = dataDocumento;
	}

	public int getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(int idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public String getIdentificacao() {
		return identificacao;
	}

	public void setIdentificacao(String identificacao) {
		this.identificacao = identificacao;
	}

	public int getIdUnidade() {
		return idUnidade;
	}

	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}

	public Integer getAnoIdent() {
		return anoIdent;
	}

	public void setAnoIdent(Integer anoIdent) {
		this.anoIdent = anoIdent;
	}

	public int getIdMovimentoAtual() {
		return idMovimentoAtual;
	}

	public void setIdMovimentoAtual(int idMovimentoAtual) {
		this.idMovimentoAtual = idMovimentoAtual;
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
		final DocumentoDTO other = (DocumentoDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (id.intValue() != other.id.intValue())
			return false;
		return true;
	}
}
