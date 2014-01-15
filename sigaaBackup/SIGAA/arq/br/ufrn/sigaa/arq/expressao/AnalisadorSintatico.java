/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/13 - 17:17:48
 */
package br.ufrn.sigaa.arq.expressao;

import static br.ufrn.sigaa.arq.expressao.ExpressaoTokens.ABRE_PARENTESES;
import static br.ufrn.sigaa.arq.expressao.ExpressaoTokens.E;
import static br.ufrn.sigaa.arq.expressao.ExpressaoTokens.FECHA_PARENTESES;
import static br.ufrn.sigaa.arq.expressao.ExpressaoTokens.OU;

import java.util.Iterator;
import java.util.Stack;

import br.ufrn.arq.util.StringUtils;

/**
 * Analisador Sintático de expressões do SIGAA
 *
 * @author David Pereira
 *
 */
public class AnalisadorSintatico {

	private Iterator<String> iterator;
	private Stack<String> npr; // Notação Polonesa Reversa
	private String nextToken;

	private String expressaoAnalisada;


	public AnalisadorSintatico(Iterator<String> iterator) {
		this.iterator = iterator;
		this.npr = new Stack<String>();
	}

	public AnalisadorSintatico(String expressao) {
		this(new ExpressaoIterator(ExpressaoUtil.expressaoToTokens(expressao)));
		this.expressaoAnalisada = expressao;
	}

	public Stack<String> analizar() {
		analex();
		expressao();
		
		if (FECHA_PARENTESES.equals(nextToken))
			throw new ExpressaoInvalidaException("Expressão mal-formada: " + expressaoAnalisada);
		
		return npr;
	}

	private void termo() {
		fator();
		while (E.equals(nextToken)) {
			String s = nextToken;
			analex();
			fator();

			npr.add(s);
		}
	}

	private void fator() {
		if (ABRE_PARENTESES.equals(nextToken)) {
			analex();
			expressao();
			if (!FECHA_PARENTESES.equals(nextToken))
				throw new ExpressaoInvalidaException("Expressão mal-formada. Faltando fechar parenteses.: " + expressaoAnalisada);
			analex();
		} else if (isNumero(nextToken)) {
			npr.add(nextToken);
			analex();
		} else if (FECHA_PARENTESES.equals(nextToken)) {
			throw new ExpressaoInvalidaException("Expressão mal-formada: " + expressaoAnalisada);
		}
		
	}

	private void expressao() {
		termo();
		while (OU.equals(nextToken)) {
			String s = nextToken;
			analex();
			termo();

			npr.add(s);
		}
	}

	private boolean isNumero(String token) {
		return StringUtils.extractInteger(token) != null;
	}

	private void analex() {
		nextToken = iterator.next();
	}

	public boolean isParentesesSobrando() {
		return iterator.hasNext();
	}

}
