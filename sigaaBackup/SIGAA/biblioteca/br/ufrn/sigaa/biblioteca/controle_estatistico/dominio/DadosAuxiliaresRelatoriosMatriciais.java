/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 08/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import br.ufrn.sigaa.biblioteca.util.FormataDadosRelatoriosUtils;


/**
 *
 * <p>Ajuda a formatar os dados dos relat�rios da biblioteca que deve exibir dados em formato de matriz para o usu�rio. </p>
 * 
 * @author jadson
 * @see FormataDadosRelatoriosUtils
 * 
 */
public class DadosAuxiliaresRelatoriosMatriciais {

	/** Opcional. O grupamento mais externo caso o usu�rio escolha agrupar os resultados por 3 agrupamentos*/
	private String descricaoAgrupamento1;

	/** O valor do agrupamento 1, para ser mostrado para o usu�rio na p�gina do relat�rio. */
	private String valorAgrupamento1;
	
	/** Obrigat�rio. A descri��o do primeiro agrupamento da matriz. */
	private String descricaoAgrupamento2;
	
	/** Obrigat�rio. A descri��o do segundo agrupamento da matriz.*/
	private String descricaoAgrupamento3;
	
	/** A matriz de com os resultados da consulta j� agrupados pelos dois agrupamentos mais internos.*/
	private Object[][] matrizDeResultados;

	/**
	 * Construtor quando n�o tem o primeiro agrupamento.
	 */
	public DadosAuxiliaresRelatoriosMatriciais(){
		
	}
	
	/**
	 * Construtor quando o relat�rio tem o 1� agrupamento.
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
	 * O valor da �ltima linha e coluna sempre contem o total da matriz, ent�o s� precisa somar esse valor.
	 *
	 * @return
	 */
	public long getTotalMatrizResultados(){
		if(matrizDeResultados.length > 0)
			return(Long) matrizDeResultados[ (matrizDeResultados.length)-1  ][ matrizDeResultados[0].length-1];
		return 0;
	}
	
	
	/**
	 * Configura os agrupamentos obrigat�rio para se gerar a matriz
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
	 * Configura a descri��o e o valor do agrupamento 1 que � o agrupamento mais externo. 
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
	 * Configura a matriz de resultados e a descri��o dos seus agrupamentos.
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
