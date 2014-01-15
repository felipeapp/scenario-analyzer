/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 12/05/2011
 *
 */
package br.ufrn.comum.gru.dominio;

/**
 * <p>Define os tipos de GRUs utilizados no sistema </p>
 * @author jadson
 *
 */
public enum TipoGRU{
	
	/** Indica que a GRU � simples*/ SIMPLES(0, "Simples", "GRUSimples.jasper"),
	/** Indica que a GRU � de coban�a */ COBRANCA(1, "Cobran�a", "GRUCobranca.jasper");
	
	/**
	 * O valor que representa o Enum
	 */
	private int valor;
	
	/**
	 * A descri��o para ser mostrada ao usu�rio
	 */
	private String descricao;
	
	/**
	 * O nome do arquivo jasper que vai gerar a GRU
	 */
	private String nomeArquivoJasper;

	private TipoGRU(int valor, String descricao, String nomeArquivoJasper) {
		this.valor = valor;
		this.descricao = descricao;
		this.nomeArquivoJasper = nomeArquivoJasper;
	}
	
	public int getValor() {
		return valor;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public String getNomeArquivoJasper() {
		return nomeArquivoJasper;
	}

	/**  M�todo chamado pelo hibernate para pegar o valor do enum nas consultas HQLs  */
	@Override
	public String toString() {
		return String.valueOf(valor);
	}
	
}
