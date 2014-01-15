/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 26/05/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;


/**
 * <p> Classe auxiliar para organizar os dados para serem mostrados ao usuário na página</p>
 *
 * 
 * @author jadson
 *
 */
public class DadosMateriaisTombados{

	private String codigoBarras;
	private String descricaoUnidade;
	
	
	
	public DadosMateriaisTombados(String codigoBarras, String descricaoUnidade) {
		super();
		this.codigoBarras = codigoBarras;
		this.descricaoUnidade = descricaoUnidade;
	}
	
	public String getCodigoBarras() {
		return codigoBarras;
	}
	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}
	public String getDescricaoUnidade() {
		return descricaoUnidade;
	}
	public void setDescricaoUnidade(String descricaoUnidade) {
		this.descricaoUnidade = descricaoUnidade;
	}	
}
