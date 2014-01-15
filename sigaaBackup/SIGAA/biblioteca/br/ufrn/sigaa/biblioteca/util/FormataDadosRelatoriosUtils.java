/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 08/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosAuxiliaresRelatoriosMatriciais;
import br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.RelatorioConsultasLocaisPorTurnoMBean;

/**
 * <p>Classe utilit�rio que ajuda a formatar os dados para exibi��o nos relat�rios. </p>
 * 
 * @author jadson
 * 
 * @see DadosAuxiliaresRelatoriosMatriciais
 * @see RelatorioConsultasLocaisPorTurnoMBean
 */
public class FormataDadosRelatoriosUtils {
	
	/**
	 * Formata a lista de dados passados no formato de uma matriz de dados, para facilitar a exibi��o de dados em formato de matriz.
	 *
	 * <p>Esse m�todo foi criado porque em alguns relat�rios  da biblioteca, pricipalmente os relat�rios em que o usu�rio pode escolher at� 2 agrupamentos diferente, os dados 
	 * precisam ser mostrados em formato de matriz (linhas e colunas ) e as consultas
	 * no banco sempre retornam uma lista de dados, ent�o toda vida fica complicado a formata��o desses dados para o usu�rio.</p>
	 *
	 * <p>Esse m�todo j� totaliza os resultado tanto das linha quanto das colunas da matriz.</p>
	 *
	 * <p> <strong> Esse m�todo recebe um lista com 3 agrupamento: List[agrupamento1, agrupamento2, agrupamento 2, quantidade] </strong> </p>
	 *
	 * @param resultados2
	 * @param i
	 */
	public static List<DadosAuxiliaresRelatoriosMatriciais> formataMatrizDadosApartirDeLista(
			String descricaoAgrupamento1, String descricaoAgrupamento2, String descricaoAgrupamento3, List<Object[]> resultado) {

		String valorAgrupamento1 = "";
		
		List<DadosAuxiliaresRelatoriosMatriciais> retorno = new ArrayList<DadosAuxiliaresRelatoriosMatriciais>();
		
		List<Object[]> resultadosInternos = new ArrayList<Object[]>();
				
		
		for (Object[] objects : resultado) {
			
			boolean mudouValorPrimeiraClassificacao = ! valorAgrupamento1.equalsIgnoreCase( (String) objects[0]);
			
			if( mudouValorPrimeiraClassificacao ){ // mudou o valor do primeiro agrupamento
				
				/* se j� tem resultados internos � porque j� est� mudando para o 2� valor do 1� agrupamento, 
				 * ent�o cria a matriz de resultados com o resultados internos e cria o objeto que agrupa os dados para o usu�rio.
				 */
				if( resultadosInternos.size() > 0 ) { // se h� dados internos acumulados 
					
					DadosAuxiliaresRelatoriosMatriciais dados = FormataDadosRelatoriosUtils.formataMatrizDadosApartirDeLista(descricaoAgrupamento2, descricaoAgrupamento3, resultadosInternos);
					dados.setAgrupamento1(descricaoAgrupamento1, valorAgrupamento1);
					retorno.add( dados );
					
					
					resultadosInternos = new ArrayList<Object[]>(); 
					
					valorAgrupamento1 = (String) objects[0];
					resultadosInternos.add(new Object[]{objects[1], objects[2], objects[3]});
					
					//contabilizaTotalPorClassificacao((String)objects[2], ((BigInteger)objects[3]).longValue());
					//contabilizaTotalPorAgrupamento((String)objects[1], ((BigInteger)objects[3]).longValue());
					
				}else{
					valorAgrupamento1 = (String) objects[0];
					resultadosInternos.add(new Object[]{objects[1], objects[2], objects[3]});
					
					//contabilizaTotalPorClassificacao((String)objects[2], ((BigInteger)objects[3]).longValue());
					//contabilizaTotalPorAgrupamento((String)objects[1], ((BigInteger)objects[3]).longValue());
				}
				
			}else{
				
				resultadosInternos.add(new Object[]{objects[1], objects[2], objects[3]});
				
				//contabilizaTotalPorClassificacao((String)objects[2], ((BigInteger)objects[3]).longValue());
				//contabilizaTotalPorAgrupamento((String)objects[1], ((BigInteger)objects[3]).longValue());
				
			}
			
		}
		
		if(resultadosInternos.size() > 0){ // coloca os resultados do �ltimo valor do primeiro agrupamento
			DadosAuxiliaresRelatoriosMatriciais dados = FormataDadosRelatoriosUtils.formataMatrizDadosApartirDeLista(descricaoAgrupamento2, descricaoAgrupamento3, resultadosInternos);
			dados.setAgrupamento1(descricaoAgrupamento1, valorAgrupamento1);
			retorno.add( dados );
		}
		
		
		return retorno;
		
	}
	
	
	
	
	/**
	 * Formata a lista de dados passados no formato de uma matriz de dados, para facilitar a exibi��o de dados em formato de matriz
	 *
	 * <p>Esse m�todo foi criado porque em alguns relat�rios  da biblioteca, pricipalmente os relat�rios em que o usu�rio pode escolher at� 2 agrupamentos diferente, os dados 
	 * precisam ser mostrados em formato de matriz (linhas e colunas ) e as consultas
	 * no banco sempre retornam uma lista de dados, ent�o toda vida fica complicado a formata��o desses dados para o usu�rio.</p>
	 *
	 * <p>Esse m�todo j� totaliza os resultado tanto das linha quanto das colunas da matriz.</p>
	 *
	 * <p> <strong> Esse m�todo recebe um lista com 2 agrupamento: List[agrupamento1, agrupamento2, quantidade] </strong> </p>
	 *
	 * @param resultados2
	 * @param i
	 */
	public static DadosAuxiliaresRelatoriosMatriciais formataMatrizDadosApartirDeLista(String descricaoPrimeiroAgruapamento, String descricaoSegundoAgrupamento, List<Object[]> resultado) {
		
		DadosAuxiliaresRelatoriosMatriciais temp = new DadosAuxiliaresRelatoriosMatriciais(descricaoPrimeiroAgruapamento, descricaoSegundoAgrupamento);
		
		if(resultado == null)
			return null;
		
		if(resultado.size() == 0)
			return new DadosAuxiliaresRelatoriosMatriciais();
			
		List<String> primeiroAgrupamento = new ArrayList<String>();
		List<String> segundoAgrupamento = new ArrayList<String>();
		
		// Obt�m as informa��es que ser�o impressas na primeira linha e coluna da matriz (os cabe�alhos) //
		for (Object[] dados : resultado) {
			if(! primeiroAgrupamento.contains( dados[0].toString())) primeiroAgrupamento.add( dados[0].toString() ); // para contar a quantidade de valores do primeiro agrupamento n�o repetidos
			if(! segundoAgrupamento.contains( dados[1].toString()) ) segundoAgrupamento.add ( dados[1].toString() ); // para contar a quantidade de valores do primeiro agrupamento n�o repetidos
		}
		
		Collections.sort(primeiroAgrupamento);
		Collections.sort(segundoAgrupamento);
		
		Object[][] matrizResultados = new Object[primeiroAgrupamento.size()+2][segundoAgrupamento.size()+2]; // + 2 para poder conter o caba�alho e a totaliza��o
		
		int qtdLinhas = matrizResultados.length;
		int qtdColunas = matrizResultados[0].length;
		
		// preenche a primeira linha com o cabecalho
		for (int indexColuna = 1; indexColuna <= qtdColunas-2; indexColuna++) {
			matrizResultados[0][indexColuna] = segundoAgrupamento.get(indexColuna-1);
		}
		
		// preenche a primeira coluna com o cabecalho
		for (int indexLinha = 1; indexLinha <= qtdLinhas-2; indexLinha++) {
			matrizResultados[indexLinha][0] = primeiroAgrupamento.get(indexLinha-1);
		}
		
		// atribui os valores � matriz
		for (Object[] dados : resultado) {
			
			int indexLinha = getIndexLinha(dados[0].toString(), matrizResultados);
			int indexColuna =  getIndexColuna(dados[1].toString(), matrizResultados);
			
			matrizResultados[indexLinha][indexColuna] = dados[2]; // o valor retornado do banco para a linha e coluna da matriz
		
		}
		
	
		// Inicia a totaliza��o dos resultados //
		matrizResultados[0][qtdColunas-1] = "Total"; // primeira linha da �ltima coluna
		matrizResultados[qtdLinhas-1][0] = "Total"; // ultima linha da primeira coluna
		
		
		// totaliza os resultados das colunas //
		
		for (int coluna = 1; coluna < qtdColunas-1; coluna++) {
			
			long totalColuna = 0l;
			
			for (int linha = 1; linha < qtdLinhas-1; linha++) {
			
				BigInteger valor = ((BigInteger) matrizResultados[linha][coluna]);
				if(valor != null){
					totalColuna += valor.longValue();
				}
			}
			
			matrizResultados[qtdLinhas-1][coluna] = totalColuna;
			totalColuna = 0l;	
		}
		
		// totaliza os resultados das linhas //
		
		for (int linha = 1; linha < qtdLinhas-1; linha++) {
			
			long totalLinhas = 0l;
			
			for (int coluna = 1; coluna < qtdColunas-1; coluna++) {
			
				BigInteger valor = ((BigInteger) matrizResultados[linha][coluna]);
				if(valor != null){
					totalLinhas += valor.longValue();
				}
			}
			
			matrizResultados[linha][qtdColunas-1] = totalLinhas;
			totalLinhas = 0l;	
		}
		
		
		// totaliza o resultado final //
		
		long totalGeral = 0l;
		
		for (int coluna = 1; coluna < qtdColunas-1; coluna++) {
		
			Long valor = ((Long) matrizResultados[qtdLinhas-1][coluna]);
			if(valor != null){
				totalGeral += valor;
			}
		}
		
		matrizResultados[qtdLinhas-1][qtdColunas-1] = totalGeral;
		totalGeral = 0l;	
	
		temp.setMatrizDeResultados(descricaoPrimeiroAgruapamento, descricaoSegundoAgrupamento, matrizResultados);
		
		return temp;
	}

	
	
	
	
	/**
	 * Pega a posi��o em que o dados deve ficar dentro da matriz a partir do valor do cabe�alho da linha
	 *
	 * @param object
	 * @return
	 */
	private static int getIndexLinha(String cabecalhoLinha, Object[][] matrizResultados) {
		// preenche a primeira linha com o cabecalho
		for (int indexLinha = 1; indexLinha <= matrizResultados.length-2; indexLinha++) {
			if( matrizResultados[indexLinha][0].toString().equalsIgnoreCase( cabecalhoLinha ) ){
				return indexLinha;
			}
		}
		return 0;
	}
	
	/**
	 *  Pega a posi��o em que o dados deve ficar dentro da matriz a partir do valor do cabe�alho da coluna
	 *
	 * @param object
	 * @return
	 */
	private static int getIndexColuna(String cabecalhoColuna, Object[][] matrizResultados) {
		for (int indexColuna = 1; indexColuna <= matrizResultados[0].length-2; indexColuna++) {
			if( matrizResultados[0][indexColuna].toString().equalsIgnoreCase( cabecalhoColuna ) ){
				return indexColuna;
			}
		}
		return 0;
	}
	
	
	
}
