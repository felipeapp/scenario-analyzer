package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

/**
 * Classe utilizada para armazenar a informa��es relativas � tentativa de registro de um material no invent�rio.
 * 
 * @author Felipe
 *
 */
public class ItemResultadoRegistraMateriaisInventario {
	/**
	 * C�digo de barras do material.
	 */
	private String codigoBarras;
	/**
	 * Indica se o registro do material foi conclu�do com sucesso ou n�o.
	 */
	private boolean concluido;
	/**
	 * Em caso de falha no registro do material, informa o motivo.
	 */
	private String mensagemErro;
	
	public ItemResultadoRegistraMateriaisInventario() {
		
	}
	
	public ItemResultadoRegistraMateriaisInventario(String codigoBarras, boolean concluido, String mensagemErro) {
		this.codigoBarras = codigoBarras;
		this.concluido = concluido;
		this.mensagemErro = mensagemErro;
	}

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public boolean isConcluido() {
		return concluido;
	}

	public void setConcluido(boolean concluido) {
		this.concluido = concluido;
	}

	public String getMensagemErro() {
		return mensagemErro;
	}

	public void setMensagemErro(String mensagemErro) {
		this.mensagemErro = mensagemErro;
	}
}
