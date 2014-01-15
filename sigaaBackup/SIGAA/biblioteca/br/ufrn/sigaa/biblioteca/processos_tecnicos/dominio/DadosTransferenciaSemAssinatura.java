/*
 * DadosTransferenciaSemAssinatura.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 02/02/2010
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;



/**
 *
 * Classe transiente que guarda as informações dos Fascículos que foram transferidos para a biblioteca 
 *   escolhida pelo usuário sem a biblioteca ter a assinatura
 *
 * @author jadson
 * @since 02/02/2010
 * @version 1.0 criacao da classe
 *
 */
public class DadosTransferenciaSemAssinatura implements Comparable<DadosTransferenciaSemAssinatura>{

	private String codigoBarras;
	
	private int idFasciculo;
	
	private String anoCronologico;
	
	private String ano;
	
	private String volume;
	
	private String numero;
	
	private String edicao;
	
	private int idRegistroMovimentacao;
	
	private int idTitulo;
	
	private String descricaoTitulo;
	
	private String titulo;
	
	private String nomeUsuarioSolicitouTranferencia;
	
	public DadosTransferenciaSemAssinatura(int idRegistroMovimentacao, int idFasciculo, String codigoBarras,
			String anoCronologico, String ano, String volume, String numero, String edicao, int idTitulo, String descricaoTitulo, String titulo, String nomeUsuarioSolicitouTranferencia) {
		super();
		this.codigoBarras = codigoBarras;
		this.idFasciculo = idFasciculo;
		this.idRegistroMovimentacao = idRegistroMovimentacao;
		this.idTitulo = idTitulo;
		this.descricaoTitulo = descricaoTitulo;
		this.titulo = titulo;
		this.anoCronologico = anoCronologico;
		this.ano = ano;
		this.volume = volume;
		this.numero = numero;
		this.edicao = edicao;
		this.nomeUsuarioSolicitouTranferencia = nomeUsuarioSolicitouTranferencia;
	}

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public int getIdFasciculo() {
		return idFasciculo;
	}

	public void setIdFasciculo(int idFasciculo) {
		this.idFasciculo = idFasciculo;
	}

	public int getIdRegistroMovimentacao() {
		return idRegistroMovimentacao;
	}

	public void setIdRegistroMovimentacao(int idRegistroMovimentacao) {
		this.idRegistroMovimentacao = idRegistroMovimentacao;
	}

	public int getIdTitulo() {
		return idTitulo;
	}

	public void setIdTitulo(int idTitulo) {
		this.idTitulo = idTitulo;
	}

	public String getDescricaoTitulo() {
		return descricaoTitulo;
	}

	public void setDescricaoTitulo(String descricaoTitulo) {
		this.descricaoTitulo = descricaoTitulo;
	}
	
	public String getAnoCronologico() {
		return anoCronologico;
	}

	public String getAno() {
		return ano;
	}

	public String getVolume() {
		return volume;
	}

	public String getNumero() {
		return numero;
	}

	public String getEdicao() {
		return edicao;
	}
	
	public String getTitulo() {
		return titulo;
	}
	
	public String getNomeUsuarioSolicitouTranferencia() {
		return nomeUsuarioSolicitouTranferencia;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(DadosTransferenciaSemAssinatura arg0) {
		
		return new Integer(this.getIdTitulo()).compareTo(new Integer(arg0.getIdTitulo()));
	}

	@Override
	public String toString() {
		return codigoBarras+" Descricao Título: "+descricaoTitulo+" "+anoCronologico+" "+ano+" "+volume+" "+numero+" "+edicao+" - "+idFasciculo+" - "+idRegistroMovimentacao;
	}

	
}
