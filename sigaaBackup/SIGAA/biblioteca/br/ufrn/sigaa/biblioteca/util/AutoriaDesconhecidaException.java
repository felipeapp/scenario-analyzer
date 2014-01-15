/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 26/10/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.util;

import br.ufrn.arq.erros.NegocioException;

/**
 *
 * <p>Exece��o lan�ada quando um t�tulo � de autoria desconhecida, 
 * n�o possui nenhum campo de autoria 100, 110, 111, 700, 710, e 711</p>
 * 
 * @author jadson
 *
 */
public class AutoriaDesconhecidaException extends NegocioException{

	private String expressaoAutoriaPorTitulo;
	
	public AutoriaDesconhecidaException(){
	}
	
	/**
	 * Construtor utilizado quando o t�tulo n�o possui autoria e precisa retornar a express�o gerada
	 * pela substitui��o do Titulo no campo de autor, j� que o m�todo n�o vai seguir o seu fluxo normal
	 * @param tituloObra
	 */
	public AutoriaDesconhecidaException(String expressaoAutoriaPorTitulo){
		this.expressaoAutoriaPorTitulo = expressaoAutoriaPorTitulo;
	}

	public String getExpressaoAutoriaPorTitulo() {
		return expressaoAutoriaPorTitulo;
	}
	
}
