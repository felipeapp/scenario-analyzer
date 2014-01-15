/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 26/10/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.util;

import br.ufrn.arq.erros.NegocioException;

/**
 *
 * <p>Execeção lançada quando um título é de autoria desconhecida, 
 * não possui nenhum campo de autoria 100, 110, 111, 700, 710, e 711</p>
 * 
 * @author jadson
 *
 */
public class AutoriaDesconhecidaException extends NegocioException{

	private String expressaoAutoriaPorTitulo;
	
	public AutoriaDesconhecidaException(){
	}
	
	/**
	 * Construtor utilizado quando o título não possui autoria e precisa retornar a expressão gerada
	 * pela substituição do Titulo no campo de autor, já que o método não vai seguir o seu fluxo normal
	 * @param tituloObra
	 */
	public AutoriaDesconhecidaException(String expressaoAutoriaPorTitulo){
		this.expressaoAutoriaPorTitulo = expressaoAutoriaPorTitulo;
	}

	public String getExpressaoAutoriaPorTitulo() {
		return expressaoAutoriaPorTitulo;
	}
	
}
