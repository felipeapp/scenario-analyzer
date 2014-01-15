/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * created on 23/11/2011
 * 
 */
package br.ufrn.sigaa.ensino.dao;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.util.StringUtils;

/**
 * 
 * @author Henrique André
 *
 */
public class ProjectionUtil {
	
	/**
	 * Retorna um ProjectionResultSet
	 * 
	 * @param queryResult
	 * @param projecao
	 * @return
	 */
	public static ProjectionResultSet execute(List<Object[]> queryResult, String projecao) {
		String[] atributos = StringUtils.split(projecao, ',');
		List<RowProjection> result = new ArrayList<RowProjection>();

		for (Object[] colunas : queryResult) {
			RowProjection linha = new RowProjection();
			
			for (int indiceColuna = 0; indiceColuna < atributos.length; indiceColuna++) {
				linha.getValor().put(atributos[indiceColuna].trim(), colunas[indiceColuna]);
			}
			result.add(linha);
		}
		
		return new ProjectionResultSet(result);
	}	
	
}
