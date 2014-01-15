package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;

public class SolicitacaoServicoDTO implements Serializable {
	/** Atributo de serializa��o da classe */
	private static final long serialVersionUID = -1L;
	
	private int id;
	
	private int idUsuarioSolicitante;

	private TipoServicoDTO tipoServico;
	
	private Date dataCadastro;
	
	private String mensagem;
	
	private int numero;
	
	private int status;
		
	private int idInteressado;

	private UnidadeDTO unidade;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdUsuarioSolicitante() {
		return idUsuarioSolicitante;
	}

	public void setIdUsuarioSolicitante(int idUsuarioSolicitante) {
		this.idUsuarioSolicitante = idUsuarioSolicitante;
	}

	public TipoServicoDTO getTipoServico() {
		return tipoServico;
	}

	public void setTipoServico(TipoServicoDTO tipoServico) {
		this.tipoServico = tipoServico;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getIdInteressado() {
		return idInteressado;
	}

	public void setIdInteressado(int idInteressado) {
		this.idInteressado = idInteressado;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public UnidadeDTO getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadeDTO unidade) {
		this.unidade = unidade;
	}
	
	
	
}
