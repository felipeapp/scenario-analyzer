/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/13 - 17:02:05
 */
package br.ufrn.sigaa.arq.expressao;

import java.util.Iterator;
import java.util.List;

/**
 * Iterator para os tokens uma expressão do SIGAA
 * 
 * @author David Pereira
 *
 */
public class ExpressaoIterator implements Iterator<String> {

	private final String[] tokens;

	private int loop;
	
	public ExpressaoIterator(String[] tokens) {
		this.tokens = tokens;
	}

	public ExpressaoIterator(List<String> lista) {
		this.tokens = new String[lista.size()];
		int i = 0;
		for (String token : lista) {
			tokens[i++] = token;
		}
	}
	
	public boolean hasNext() {
		return tokens != null && !(loop >= tokens.length);
	}

	public String next() {
		if (hasNext())
			return tokens[loop++];
		return null;
	}

	public void remove() {
		throw new UnsupportedOperationException("Não é possível remover um token da expressão.");
	}

}
