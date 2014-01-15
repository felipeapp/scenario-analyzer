/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 04/11/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;

/**
 *
 * <p> Classe utilitária para realizar a paginação das consultas do acervo da biblioteca</p>
 *
 * 
 * @author jadson
 *
 */
public class PaginacaoBibliotecaUtil {

	
	/**
	 *  Calcula a quantidade de páginas necessárias para mostras todos os resultados.
	 *
	 * @return
	 */
	public static int calculaQuantidadePaginas(final int quantidadeTotalResultados, final int quantideResultadosPorPagina){
		return (quantidadeTotalResultados / quantideResultadosPorPagina) + (quantidadeTotalResultados % quantideResultadosPorPagina == 0  ?  0 : 1 );
	}
	
	
	/**
	 * <p>Retorna uma lista de no máximo 10 página que o usuário pode percorrer</p>
	 * 
	 * <p>Caso seja permitido, retorna as 5 página anteriores e 4 página posteriores à página atual, mais a própria página atual.</p> 
	 *  
	 * @return
	 */
	public static List<Integer> getListaPaginasVisiveis(int paginaAtual, int quantidadePaginas){
		
		List<Integer> paginas = new ArrayList<Integer>();
		
		int pagina = paginaAtual;
		
		int contador = 0;
		
		while (pagina > 1 && contador < 5) { // Conta a quantidade de páginas anteriores a página atual que podem ser mostradas, no limite máximo de 5.
			pagina--;
			contador++;
		}
		
		for (; pagina < paginaAtual+( 10-contador ) && pagina <=  quantidadePaginas && quantidadePaginas > 1; pagina++) {
			paginas.add(pagina);
			
		}
		
		return paginas;
	}
	
	
	/** Método que realiza a páginação em memória */
	public static List<CacheEntidadesMarc> realizaPaginacaoEmMemoria(List<CacheEntidadesMarc> resultadosRecuperados, int paginaAtual, int quantideResultadosPorPagina, int quantidadeTotalResultados){
		
		List<CacheEntidadesMarc> resultadosPaginados = new ArrayList<CacheEntidadesMarc>();
		
		int primeiroResutado = ((paginaAtual-1) * quantideResultadosPorPagina );
		int ultimoResutado = quantideResultadosPorPagina * paginaAtual < quantidadeTotalResultados ? quantideResultadosPorPagina * paginaAtual : quantidadeTotalResultados;
		
		for (int index = primeiroResutado ; index < ultimoResutado; index++ ) {
			resultadosPaginados.add(resultadosRecuperados.get(index));
		}
		
		return resultadosPaginados;
	}
	
	
}
