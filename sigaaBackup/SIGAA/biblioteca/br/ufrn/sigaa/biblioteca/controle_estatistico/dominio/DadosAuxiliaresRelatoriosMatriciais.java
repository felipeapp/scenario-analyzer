/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 08/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import br.ufrn.sigaa.biblioteca.util.FormataDadosRelatoriosUtils;


/**
 *
 * <p>Ajuda a formatar os dados dos relatórios da biblioteca que deve exibir dados em formato de matriz para o usuário. </p>
 * 
 * @author jadson
 * @see FormataDadosRelatoriosUtils
 * 
 */
public class DadosAuxiliaresRelatoriosMatriciais {

	/** Opcional. O grupamento mais externo caso o usuário escolha agrupar os resultados por 3 agrupamentos*/
	private String descricaoAgrupamento1;

	/** O valor do agrupamento 1, para ser mostrado para o usuário na página do relatório. */
	private String valorAgrupamento1;
	
	/** Obrigatório. A descrição do primeiro agrupamento da matriz. */
	private String descricaoAgrupamento2;
	
	/** Obrigatório. A descrição do segundo agrupamento da matriz.*/
	private String descricaoAgrupamento3;
	
	/** A matriz de com os resultados da consulta já agrupados pelos dois agrupamentos mais internos.*/
	private Object[][] matrizDeResultados;

	/**
	 * Construtor quando não tem o primeiro agrupamento.
	 */
	public DadosAuxiliaresRelatoriosMatriciais(){
		
	}
	
	/**
	 * Construtor quando o relatório tem o 1º agrupamento.
	 * 
	 * @param descricaoAgrupamento1
	 * @param valorAgrupamento1
	 */
	public DadosAuxiliaresRelatoriosMatriciais(String descricaoAgrupamento1, String valorAgrupamento1) {
		this.descricaoAgrupamento1 = descricaoAgrupamento1;
		this.valorAgrupamento1 = valorAgrupamento1;
	}


	/**
	 * Retorna a quantidade de linha da matriz gerada.
	 *
	 * @return
	 */
	public int  getQtdLinhasTotalMatriz(){
		if(matrizDeResultados != null)
			return matrizDeResultados.length;
		return 0;
	}
	
	/**
	 * Retorna a quantidade de colunas da matriz gerada.
	 *
	 * @return
	 */
	public int  getQtdColunasTotalMatriz(){
		if(matrizDeResultados != null && matrizDeResultados.length > 0)
			return matrizDeResultados[0].length;
		return 0;
	}
	
	/**
	 * Retorna o valor total da matriz.
	 * 
	 * O valor da última linha e coluna sempre contem o total da matriz, então só precisa somar esse valor.
	 *
	 * @return
	 */
	public long getTotalMatrizResultados(){
		if(matrizDeResultados.length > 0)
			return(Long) matrizDeResultados[ (matrizDeResultados.length)-1  ][ matrizDeResultados[0].length-1];
		return 0;
	}
	
	
	/**
	 * Configura os agrupamentos obrigatório para se gerar a matriz
	 * @param descricaoAgrupamento2
	 * @param descricaoAgrupamento3
	 */
	public void configuraAgrupamentosObrigatorios(String descricaoAgrupamento2, String descricaoAgrupamento3) {
		this.descricaoAgrupamento2 = descricaoAgrupamento2;
		this.descricaoAgrupamento3 = descricaoAgrupamento3;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime* result+ ((descricaoAgrupamento1 == null) ? 0 : descricaoAgrupamento1.hashCode());
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
		DadosAuxiliaresRelatoriosMatriciais other = (DadosAuxiliaresRelatoriosMatriciais) obj;
		if (descricaoAgrupamento1 == null) {
			if (other.descricaoAgrupamento1 != null)
				return false;
		} else if (!descricaoAgrupamento1.equals(other.descricaoAgrupamento1))
			return false;
		return true;
	}
	
	/**
	 * Configura a descrição e o valor do agrupamento 1 que é o agrupamento mais externo. 
	 * Isso para quando os resultados possuem 3 agrupamentos. 
	 *
	 * @param descricaoAgrupamento1
	 * @param valorAgrupamento1
	 */
	public void setAgrupamento1(String descricaoAgrupamento1, String valorAgrupamento1) {
		this.descricaoAgrupamento1 = descricaoAgrupamento1;
		this.valorAgrupamento1 = valorAgrupamento1;
	}

	/**
	 * Configura a matriz de resultados e a descrição dos seus agrupamentos.
	 *
	 * @param descricaoAgrupamento2
	 * @param descricaoAgrupamento3
	 * @param matrizDeResultados
	 */
	public void setMatrizDeResultados(String descricaoAgrupamento2,  String descricaoAgrupamento3, Object[][] matrizDeResultados) {
		this.descricaoAgrupamento2 = descricaoAgrupamento2;
		this.descricaoAgrupamento3 = descricaoAgrupamento3;
		this.matrizDeResultados = matrizDeResultados;
	}
	
	public String getDescricaoAgrupamento1() {return descricaoAgrupamento1;}

	public String getDescricaoAgrupamento2() {return descricaoAgrupamento2;}

	public String getDescricaoAgrupamento3() {return descricaoAgrupamento3;}

	public Object[][] getMatrizDeResultados() {return matrizDeResultados;}

	public String getValorAgrupamento1() {return valorAgrupamento1;}
	
}
