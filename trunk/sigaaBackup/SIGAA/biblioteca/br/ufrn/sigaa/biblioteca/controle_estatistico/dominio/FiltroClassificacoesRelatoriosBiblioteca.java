/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 23/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

/**
 * <p>Representa as classificações que podem ser escolhidas nos relatórios da biblioteca</p>
 * 
 * @author jadson
 *
 */
public enum FiltroClassificacoesRelatoriosBiblioteca {

	/** As colunas utilizadas nas consultas para a classificação bibliográfica 1 */
	CLASSIFICACAO1(1, "classe_principal_classificacao_1", "classificacao_1", "id_area_conhecimento_cnpq_classificacao_1"),
	/** As colunas utilizadas nas consultas para a classificação bibliográfica 2 */
	CLASSIFICACAO2(2, "classe_principal_classificacao_2", "classificacao_2", "id_area_conhecimento_cnpq_classificacao_2"),
	/** As colunas utilizadas nas consultas para a classificação bibliográfica 3 */
	CLASSIFICACAO3(3, "classe_principal_classificacao_3", "classificacao_3", "id_area_conhecimento_cnpq_classificacao_3");
	
	/** O valor do filtro */
	private int valor;
	
	/** A coluna para realizar as consultas */
	private String colunaClassePrincipal;
	
	/** A coluna que contém a classificação completa para realizar as consultas */
	private String colunaClassificacao;

	/** A coluna da área de conhecimento */
	private String colunaAreaConhecimentoCNPq;
	
	private FiltroClassificacoesRelatoriosBiblioteca(int valor, String colunaClassePrincipal, String colunaClassificacao, String colunaAreaConhecimentoCNPq) {
		this.valor = valor;
		this.colunaClassePrincipal = colunaClassePrincipal;
		this.colunaClassificacao = colunaClassificacao;
		this.colunaAreaConhecimentoCNPq = colunaAreaConhecimentoCNPq;
	}

	public int getValor() {
		return valor;
	}

	public String getColunaClassePrincipal() {
		return colunaClassePrincipal;
	}
	
	public String getColunaClassificacao() {
		return colunaClassificacao;
	}
	
	
	public String getColunaAreaConhecimentoCNPq() {
		return colunaAreaConhecimentoCNPq;
	}

	/**
	 * Retorna o agrupamento correspondente à constante.
	 * 
	 * @param constAgrup
	 * @return
	 */
	public static FiltroClassificacoesRelatoriosBiblioteca getFiltroClassificacao(int valorFiltro) {
		
		if(valorFiltro == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO1.valor)
			return FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO1;
		
		if(valorFiltro == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO2.valor)
			return FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO2;
		
		if(valorFiltro == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO3.valor)
			return FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO3;
		
		return null;
	}
	
}
