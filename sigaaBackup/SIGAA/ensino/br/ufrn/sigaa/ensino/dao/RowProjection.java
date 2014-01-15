/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * created on 23/11/2011
 * 
 */
package br.ufrn.sigaa.ensino.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe que representa uma linha de uma tabela
 * 
 * @author Henrique Andr�
 *
 */
public class RowProjection {

	/**
	 * Map contendo <nome da coluna, valor da coluna>
	 */
	private Map<String, Object> valor = new HashMap<String, Object>();

	public Map<String, Object> getValor() {
		return valor;
	}

	public void setValor(Map<String, Object> valor) {
		this.valor = valor;
	}

}
