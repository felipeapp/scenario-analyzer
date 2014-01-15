package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import java.math.BigInteger;

public class ResultadoInternoRelatorioMulta {

	/** O valor da multa para exibição para o usuário */
	String valorFormatado;
	
	
	/** A descrição do tipo de quitação realizada. */
	String descricaoQuitacao;
	
	
	
	////////////////////////////////////////////////////////////////////////
	
	/** O usuário que fez a quitação para o caso de quitação manual */
	String usuarioQuitou;
	
	
	/** A data em que a multa foi quitada */
	String dataQuitacaoFormatada;
	
	/** Observação que pode ser dada no ato do pagamento manual */
	String observacaoPagamento;
	
	/** O número de referência da GRU que pagou a multa*/
	Long numeroReferencia;
	
	
	///////////////////////////////////////////////////////////////////////
	
	
	
	
	////////////////////////////////////////////////////////////////////////
	/** O usuário que fez o estorno */
	String usuarioEstorno;
	
	/** A data de extorno da multa formatada para exibição para o usuario */
	String dataEstornoFormatada;
	
	/**  O motivo informado para o estorno */
	String motivoEstorno;

	////////////////////////////////////////////////////////////////////
	
	/**
	 * Construtor para multas quitadas
	 * @param valorFormatado
	 * @param usuarioQuitou
	 * @param dataQuitacao
	 * @param descricaoQuitacao
	 * @param observacaoPagamento
	 */
	public ResultadoInternoRelatorioMulta(String valorFormatado, String usuarioQuitou, String dataQuitacao, String descricaoQuitacao, String observacaoPagamento, BigInteger numeroReferencia) {
		this.valorFormatado = valorFormatado;
		this.usuarioQuitou = usuarioQuitou;
		this.dataQuitacaoFormatada = dataQuitacao;
		this.descricaoQuitacao = descricaoQuitacao;
		this.observacaoPagamento = observacaoPagamento;
		if(numeroReferencia != null)
			this.numeroReferencia = numeroReferencia.longValue();
	}

	
	/**
	 * Construtor para multas em aberto
	 * @param valorFormatado
	 */
	public ResultadoInternoRelatorioMulta(String valorFormatado) {
		this.valorFormatado = valorFormatado;
	}


	/**
	 * Construtor para multas estornadas
	 * @param valorFormatado
	 * @param usuarioEstorno
	 * @param dataEstorno
	 * @param motivoEstorno
	 */
	public ResultadoInternoRelatorioMulta(String valorFormatado, String usuarioEstorno, String dataEstorno, String motivoEstorno) {
		this.valorFormatado = valorFormatado;
		this.usuarioEstorno = usuarioEstorno;
		this.dataEstornoFormatada = dataEstorno;
		this.motivoEstorno = motivoEstorno;
	}



	public String getValorFormatado() {
		return valorFormatado;
	}

	public void setValorFormatado(String valorFormatado) {
		this.valorFormatado = valorFormatado;
	}

	public String getDescricaoQuitacao() {
		return descricaoQuitacao;
	}

	public void setDescricaoQuitacao(String descricaoQuitacao) {
		this.descricaoQuitacao = descricaoQuitacao;
	}

	public String getUsuarioQuitou() {
		return usuarioQuitou;
	}

	public void setUsuarioQuitou(String usuarioQuitou) {
		this.usuarioQuitou = usuarioQuitou;
	}

	public String getDataQuitacaoFormatada() {
		return dataQuitacaoFormatada;
	}

	public void setDataQuitacaoFormatada(String dataQuitacaoFormatada) {
		this.dataQuitacaoFormatada = dataQuitacaoFormatada;
	}

	public String getObservacaoPagamento() {
		return observacaoPagamento;
	}

	public void setObservacaoPagamento(String observacaoPagamento) {
		this.observacaoPagamento = observacaoPagamento;
	}

	public String getUsuarioEstorno() {
		return usuarioEstorno;
	}

	public void setUsuarioEstorno(String usuarioEstorno) {
		this.usuarioEstorno = usuarioEstorno;
	}


	public String getDataEstornoFormatada() {
		return dataEstornoFormatada;
	}


	public void setDataEstornoFormatada(String dataEstornoFormatada) {
		this.dataEstornoFormatada = dataEstornoFormatada;
	}


	public String getMotivoEstorno() {
		return motivoEstorno;
	}


	public void setMotivoEstorno(String motivoEstorno) {
		this.motivoEstorno = motivoEstorno;
	}


	public Long getNumeroReferencia() {
		return numeroReferencia;
	}


	public void setNumeroReferencia(Long numeroReferencia) {
		this.numeroReferencia = numeroReferencia;
	}
	
	///////////////////////////////////////////////////////////////////
	
	
	
	
}
