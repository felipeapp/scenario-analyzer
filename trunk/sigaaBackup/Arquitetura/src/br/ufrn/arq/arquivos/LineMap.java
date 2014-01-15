/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/11/2007
 */
package br.ufrn.arq.arquivos;

import java.util.HashMap;

/**
 * Classe que mapeia os atributos de uma determinada linha.
 *
 * @author Gleydson Lima
 *
 */
public class LineMap {

	HashMap<String,String> campos = new HashMap<String,String>();

	public void putValue(String key, String value) {
		campos.put(key, value);
	}

	public final String getValue(String key) {
		return campos.get(key);
	}

	public final int getIntValue(String key) {
		return Integer.parseInt(getValue(key));
	}

	public final double getDoubleValue(String key) {
		return Double.parseDouble(getValue(key));
	}


}
