/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/01 - 11:55:57
 */
package br.ufrn.sigaa.arq.expressao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.EquivalenciaEspecifica;

/**
 * Árvore para verificação de expressões
 *
 * @author David Pereira
 *
 */
public class ArvoreExpressao {

	/** Nó raiz da árvore de expressões */
	private NoArvore raiz;

	/** Elementos da árvore que fizeram uma expressão ser verdadeira */
	private List<Integer> matches;

	public ArvoreExpressao(NoArvore raiz) {
		this.raiz = raiz;
		this.matches = new ArrayList<Integer>();
	}

	/**
	 * Retorna a raiz da árvore.
	 * @return
	 */
	public NoArvore getRaiz() {
		return raiz;
	}

	/**
	 * Constrói a árvore a partir de uma expressão. Se a expressão for null ou vazia,
	 * o será retornado null.
	 */
	public static ArvoreExpressao fromExpressao(String expressao) {
		if (expressao != null) {
			AnalisadorSintatico as = new AnalisadorSintatico(expressao);
			Stack<String> pilha = as.analizar();

			if (as.isParentesesSobrando()) {
				throw new ExpressaoInvalidaException("Expressão inválida, parênteses sobrando: " + expressao);
			}
			
			if (!pilha.empty()) {
				NoArvore raiz = new NoArvore(pilha.pop());
				adicionaFilhos(pilha, raiz);
				ArvoreExpressao arvore = new ArvoreExpressao(raiz);

				return arvore;
			}
		}

		return null;
	}

	/*
	 * Adiciona nós filhos ao nó passado como parâmetro de acordo com a posição atual
	 * sendo percorrida na expressão. 
	 */
	private static void adicionaFilhos(Stack<String> pilha, NoArvore no) {
		if (!pilha.empty()) {
			String op1 = pilha.pop();

			if (isOperador(op1)) {
				NoArvore novo = new NoArvore(op1);
				no.adicionaFilho(novo);
				adicionaFilhos(pilha, novo);
			} else {
				no.adicionaFilho(new NoArvore(op1));
			}

			String op2 = pilha.pop();
			if (isOperador(op2)) {
				NoArvore novo = new NoArvore(op2);
				no.adicionaFilho(novo);
				adicionaFilhos(pilha, novo);
			} else {
				no.adicionaFilho(new NoArvore(op2));
			}
		}
	}

	/**
	 * Verifica se um token da expressão é operando
	 */
	public static boolean isOperando(String token) {
		return StringUtils.extractInteger(token) != null;
	}

	/**
	 * Verifica se um token da expressão é operador
	 */
	public static boolean isOperador(String token) {
		return "E".equals(token) || "OU".equals(token);
	}

	/**
	 * Dado um conjunto de disciplinas, verifica se a expressão é verdadeira.
	 *
	 * ou falsa.
	 */
	public boolean eval(Collection<Integer> disciplinas, Map<Integer, String> equivalencias ) {
		boolean result = eval(raiz, disciplinas, equivalencias);
		if (result)
			raiz.visitado();
		else
			raiz.naoVisitado();

		return result;
	}

	/**
	 * Identifica se a expressão é verdadeira para os componentes passados como parâmetro
	 * considerando também as expressões de equivalência desses componentes.
	 * @param comps
	 * @return
	 */
	public boolean evalEquivalentes(Collection<ComponenteCurricular> comps) {
		Collection<Integer> ids = new ArrayList<Integer>(0);
		Map<Integer, String> expressoes = new HashMap<Integer, String>();
		for ( ComponenteCurricular c : comps) {
			ids.add(c.getId());
			expressoes.put(c.getId(), c.getEquivalencia());
		}
		return eval(ids, expressoes);
	}

	/** 
	 * Identifica se a expressão é verdadeira para os componentes passados como parâmetro
	 * considerando também as expressões de equivalência desses componentes.
	 * @param componentes
	 * @return
	 */ 
	public boolean eval(Collection<Integer> componentes) {
		return eval(componentes,null);
	}
	
	/**
	 * Avalia a expressão de acordo com o conjunto de componentes passados
	 * @param raiz
	 * @param disciplinas
	 * @return
	 */
	private boolean eval(NoArvore raiz, Collection<Integer> disciplinas, Map<Integer, String> equivalencias ) {

		// entra aqui se for folha, ou seja, não há ramos de nenhum dos dois lados
		if (raiz.getDireita() == null && raiz.getEsquerda() == null) {
			String label = raiz.getLabel();
			int id = Integer.parseInt(label);

			boolean contemComponente =  disciplinas.contains(id);

			// verifica se satisfaz a alguma expressão de equivalência
			if ( ! contemComponente && equivalencias != null ) {
				String equivalencia = equivalencias.get(id);
				if ( equivalencia != null ) {
					ArvoreExpressao expressaoArvore = ArvoreExpressao.fromExpressao(equivalencia);
					if (expressaoArvore != null) {
						boolean resultEquiv = expressaoArvore.eval(disciplinas, null) ;
						if ( resultEquiv ) {
							return true;
						}
					}
				}
			}

			return contemComponente;

		} else {
			// operação (E ou OU)
			String operacao = raiz.getLabel();

			// avalia o lado esquerdo da expressão  (chamada recursiva)
			boolean result1 = eval(raiz.getEsquerda(), disciplinas, equivalencias);

			// Curto-Circuito
			if (result1 && "OU".equals(operacao)) {
				raiz.getEsquerda().visitado();
				return true;
			} else if (!result1 && "E".equals(operacao)) {
				return false;
			} else {
				boolean result2 = eval(raiz.getDireita(), disciplinas, equivalencias );

				if ("E".equals(operacao)) {
					if (result1 && result2) {
						raiz.getEsquerda().visitado();
						raiz.getDireita().visitado();
					} else {
						raiz.getEsquerda().naoVisitado();
						raiz.getDireita().naoVisitado();
					}

					return result1 && result2;
				} else {
					if (result2) {
						raiz.getDireita().visitado();
					} else {
						raiz.getDireita().naoVisitado();
					}

					return result1 || result2;
				}
			}
		}
	}

	@Override
	public String toString() {
		return inOrdem(raiz, false, false).toString();
	}

	/**
	 * Retorna a lista de componentes de uma expressão
	 * desconsiderando os parênteses e operadores
	 * @return
	 */
	public Integer[] componentesIsolados() {
		String[] matches = inOrdem(raiz, false, true).toString().replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("OU", "").split(" ");
		List<Integer> lista = new ArrayList<Integer>();
		for (String m : matches) {
			Integer componente = StringUtils.extractInteger(m);
			if (componente != null)
				lista.add(componente);
		}
		return lista.toArray(new Integer[lista.size()]);
	}
	
	/**
	 * Retorna a lista de disciplinas que casaram com a expressão (com curto-circuito)
	 */
	public List<Integer> getMatches() {
		getMatch(raiz);
		return matches;
	}

	/**
	 * Retorna quais componentes tornaram a expressão verdadeira.
	 * @param expressao
	 * @param componentes
	 * @return
	 * @throws ArqException
	 */
	public static Collection<ComponenteCurricular> getMatchesComponentes(String expressao, Collection<ComponenteCurricular> componentes) throws ArqException {
		ArvoreExpressao arvore = ArvoreExpressao.fromExpressao(expressao);
		if (arvore != null) {
			TreeSet<Integer> ids = extractIds(componentes);

			arvore.eval(ids,null);
			List<Integer> idsEquivalentes = arvore.getMatches();
			ArrayList<ComponenteCurricular> componentesEquivalentes = new ArrayList<ComponenteCurricular>(0);
			for (Integer id : idsEquivalentes) {
				componentesEquivalentes.add(new ComponenteCurricular(id));
			}
			return componentesEquivalentes;
		} else {
			return null;
		}
	}

	/**
	 * Retorna o trecho da expressão que tornou a expressão completa verdadeira. 
	 * Pode ser igual à própria expressão, caso esta seja composta por apenas
	 * um componente.
	 * @param expressao
	 * @param componentes
	 * @return
	 * @throws ArqException
	 */
	public static String getMatchesExpression(String expressao, Collection<ComponenteCurricular> componentes) throws ArqException {
		ArvoreExpressao arvore = ArvoreExpressao.fromExpressao(expressao);
		if (arvore != null) {
			TreeSet<Integer> ids = extractIds(componentes);

			arvore.eval(ids,null);
			return arvore.getMatchExpression();
		} else {
			return null;
		}
	}

	/*
	 * Extrai os ids de um conjunto de componentes
	 * curriculares passado como parâmetro. 
	 */
	private static TreeSet<Integer> extractIds(Collection<ComponenteCurricular> componentes) {
		TreeSet<Integer> ids = new TreeSet<Integer>();
		for (ComponenteCurricular c : componentes) {
			ids.add(c.getId());
		}
		return ids;
	}

	/*
	 * Percorre a árvore de expressões a partir do nó
	 * passado como parâmetro para identificar que componentes
	 * tornaram a expressão verdadeira.
	 */
	private void getMatch(NoArvore no) {
		if (no.isFolha() && no.isVisitado()) {
			Integer id = Integer.valueOf(no.getLabel());
			if (!matches.contains(id))
				matches.add(id);
		}

		if (no.getEsquerda() != null)
			getMatch(no.getEsquerda());
		if (no.getDireita() != null)
			getMatch(no.getDireita());
	}

	/**
	 * Retorna o trecho da expressão que tornou a avaliação true
	 */
	public String getMatchExpression() {
		return inOrdem(raiz, true, false).toString();
	}

	/*
	 * Percorre a árvore em in-ordem
	 */
	private StringBuilder inOrdem(NoArvore no, boolean apenasVisitados, boolean retirarAnd) {
		StringBuilder sb = new StringBuilder();

		if (no != null && (!apenasVisitados || no.isVisitado() || no.isFolha())) {
			if (!retirarAnd || (retirarAnd && !"E".equals(no.getLabel()))) {
				if (!no.isFolha()) {
					sb.append("(");
					sb.append(inOrdem(no.getEsquerda(), apenasVisitados, retirarAnd));
				}
	
				sb.append(" " + no.getLabel() + " ");
	
				if (!no.isFolha()) {
					sb.append(inOrdem(no.getDireita(), apenasVisitados, retirarAnd));
					sb.append(")");
				}
			}
		}

		return sb;
	}

	/**
	 * Dada uma expressão e um conjunto de componentes, retorna os componentes
	 * que tornaram a expressão verdadeira.
	 * @param equivalencia
	 * @param componente
	 * @param componentes
	 * @return
	 * @throws ArqException
	 */
	public static Collection<ComponenteCurricular> getMatchesComponentes(String equivalencia, ComponenteCurricular componente, Collection<ComponenteCurricular> componentes) throws ArqException {
		String expressao = equivalencia.replaceAll(EquivalenciaEspecifica.EQUIVALENCIA_GLOBAL, componente.getEquivalencia());
		return getMatchesComponentes(expressao, componentes);
	}

	/**
	 * Dada uma expressão e um conjunto de componentes, retorna o trecho
	 * da expressão que tornou a expressão completa verdadeira.
	 * @param equivalencia
	 * @param componente
	 * @param componentes
	 * @return
	 * @throws ArqException
	 */
	public static String getMatchesExpression(String equivalencia, ComponenteCurricular componente, Collection<ComponenteCurricular> componentes) throws ArqException {
		String expressao = equivalencia.replaceAll(EquivalenciaEspecifica.EQUIVALENCIA_GLOBAL, componente.getEquivalencia());
		return getMatchesExpression(expressao, componentes);
	}

	/**
	 * Dada uma expressão e um conjunto de ids de componentes, retorna os componentes
	 * que tornaram a expressão verdadeira.
	 * 
	 * @param expressao
	 * @param cc
	 * @param idsDisciplinas
	 * @return
	 */
	public static Collection<ComponenteCurricular> getMatchesComponentes(String expressao, ComponenteCurricular cc, List<Integer> idsDisciplinas) {
		ArvoreExpressao arvore = ArvoreExpressao.fromExpressao(expressao);
		if (arvore != null) {
			arvore.eval(idsDisciplinas, null);
			List<Integer> idsEquivalentes = arvore.getMatches();
			ArrayList<ComponenteCurricular> componentesEquivalentes = new ArrayList<ComponenteCurricular>(0);
			for (Integer id : idsEquivalentes) {
				componentesEquivalentes.add(new ComponenteCurricular(id));
			}
			return componentesEquivalentes;
		} else {
			return null;
		}
	}
	
}

