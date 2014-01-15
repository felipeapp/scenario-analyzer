/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p> Classe utilit�ria para realizar a pagina��o das consultas do acervo da biblioteca</p>
 *
 * 
 * @author jadson
 *
 */
public class PaginacaoBibliotecaUtil {

	
	/**
	 *  Calcula a quantidade de p�ginas necess�rias para mostras todos os resultados.
	 *
	 * @return
	 */
	public static int calculaQuantidadePaginas(final int quantidadeTotalResultados, final int quantideResultadosPorPagina){
		return (quantidadeTotalResultados / quantideResultadosPorPagina) + (quantidadeTotalResultados % quantideResultadosPorPagina == 0  ?  0 : 1 );
	}
	
	
	/**
	 * <p>Retorna uma lista de no m�ximo 10 p�gina que o usu�rio pode percorrer</p>
	 * 
	 * <p>Caso seja permitido, retorna as 5 p�gina anteriores e 4 p�gina posteriores � p�gina atual, mais a pr�pria p�gina atual.</p> 
	 *  
	 * @return
	 */
	public static List<Integer> getListaPaginasVisiveis(int paginaAtual, int quantidadePaginas){
		
		List<Integer> paginas = new ArrayList<Integer>();
		
		int pagina = paginaAtual;
		
		int contador = 0;
		
		while (pagina > 1 && contador < 5) { // Conta a quantidade de p�ginas anteriores a p�gina atual que podem ser mostradas, no limite m�ximo de 5.
			pagina--;
			contador++;
		}
		
		for (; pagina < paginaAtual+( 10-contador ) && pagina <=  quantidadePaginas && quantidadePaginas > 1; pagina++) {
			paginas.add(pagina);
			
		}
		
		return paginas;
	}
	
	
	/** M�todo que realiza a p�gina��o em mem�ria */
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
