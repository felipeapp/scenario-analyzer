/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/08/2007
 *
 */
package br.ufrn.sigaa.arq.expressao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.EquivalenciaEspecifica;

/**
 * Classe Utilitária para método das expressões de pré-requisitos, co-requisitos e equivalência
 *
 * @author David Pereira
 * @author Gleydson
 *
 */
public class ExpressaoUtil {

	/** Constante de controle de avaliação de expressões equivalente ao operador OR. */
	public static final int OR = -1;

	/** Constante de controle de avaliação de expressões equivalente ao operador AND. */
	public static final int AND = -2;

	/** Constante de controle de avaliação de expressões. Indica que a expressão satisfaz o conjunto de componentes já pagos pelo discente .*/
	public static final int TRUE = -3;

	/** Constante de controle de avaliação de expressões. Indica que a expressão não satisfaz o conjunto de componentes já pagos pelo discente .*/
	public static final int FALSE = -4;

	/** Armazena os tokens válidos dentro de uma expressão de equivalência. São eles: <ul><li>)</li><li>(</li><li>OU</li><li>E</li></ul> */
	static List<String> validTokens = new ArrayList<String>(Arrays.asList(new String[] { ")", "(", "OU", "E" }));


	/**
	 * Transforma a expressão em uma pilha compilada onde o AND e OR são substituídos
	 * pela constante acima. A expressão é transformada na notação poloneza reversa
	 * @param expressao
	 * @return
	 * @throws ArqException
	 */
	public static List<Integer> expressaoToPolonezaReversa(String expressao) throws ArqException {

		List<String> tokens = expressaoToTokens(expressao);

		int escopo = 0;

		List<Integer> pilha = new ArrayList<Integer>();
		Stack<Integer> pilhaOperadores = new Stack<Integer>();

		Iterator<String> it = tokens.iterator();
		while (it.hasNext()) {

			String token = it.next();

			if (token.equals("(")) {
				escopo++;
				continue;
			}

			if (token.equals(")")) {
				escopo--;
				if (pilhaOperadores.size() > 0)
					pilha.add(pilhaOperadores.pop());
				continue;
			}

			if (token.equals("OU")) {
				pilhaOperadores.add(-1);
				continue;
			}

			if (token.equals("E")) {
				pilhaOperadores.add(-2);
				continue;
			}

			pilha.add(new Integer(token));

		}

		if (pilhaOperadores.size() > 0) {
			pilha.addAll(pilhaOperadores);
		}

		return pilha;

	}

	/**
	 * Avalia a expressão em questão dado o conjunto de IDs de componentes.
	 * Usado somente na avaliação DE EQUIVALÊNCIAS. Uma vez que as equivalências não são transitivas.
	 *
	 * Ex: Se A equivalente a B, para testar isso, se B for equivalente a X, não significa dizer
	 * que A -> X, a transitividade não vale neste caso.
	 *
	 * No entanto, se o avaliado for o pré-requisito, ou seja, A tem como pré-requisito B, e B é equivalente a X,
	 * neste caso a expressão deve analisar se o aluno possui B ou X, criando a árvore de dupla avaliação.
	 *
	 * @param expressao
	 * @param componentes
	 * @return
	 * @throws ArqException
	 */
	public static boolean eval(String expressao, TreeSet<Integer> componentes) throws ArqException {
		ArvoreExpressao arvore = ArvoreExpressao.fromExpressao(expressao);
		if( arvore == null )
			return false;
		return arvore.eval(componentes,null);
	}

	/**
	 * Este método avalia a expressão passada e considera também as equivalências dos componentes da expressão.
	 * 
	 * A equivalência é avaliada testando se o aluno posssui o componente em questão ou se possui algum dos equivalentes.
	 * Neste caso, a árvore funciona em dois níveis, é feita uma árvore com a expressão desejada, e caso a folha (a disciplina)
	 * seja avaliada como false, o algoritmo para para avaliar a equivalência do componente.
	 *
  	 * Recebe como parâmetro uma coleção de componentes e vai buscar as expressões dos componentes no banco de dados. 
	 * 
	 * Não usar para equivalência, apenas para pré e co-requisito!
	 *
	 * @param expressao
	 * @param componentes
	 * @return
	 * @throws ArqException
	 */
	public static boolean evalComTransitividade(String expressao, int idDiscente, Collection<ComponenteCurricular> componentes) throws ArqException {
		if (expressao == null || expressao.trim().equals(""))
			return true;
		Map<Integer, String> equivalencias =  new HashMap<Integer, String>();
		TreeSet<Integer> ids = new TreeSet<Integer>();

		equivalencias = getEquivalenciasExpressao(expressao, idDiscente);
		for(ComponenteCurricular c : componentes)
			ids.add(c.getId());

		return evalComTransitividade(expressao, ids, equivalencias);
	}
	
	/**
	 * Este método avalia a expressão passada e considera também as equivalências dos componentes da expressão.
	 * 
	 * A equivalência é avaliada testando se o aluno posssui o componente em questão ou se possui algum dos equivalentes.
	 * Neste caso, a árvore funciona em dois níveis, é feita uma árvore com a expressão desejada, e caso a folha (a disciplina)
	 * seja avaliada como false, o algoritmo para para avaliar a equivalência do componente.
	 *
	 * Não usar para equivalência, apenas para pré e co-requisito!
	 * 
	 * @param expressao
	 * @param componentes
	 * @param expressoesEquivalencias
	 * @return
	 * @throws ArqException
	 */
	public static boolean evalComTransitividade(String expressao, TreeSet<Integer> componentes, Map<Integer, String> expressoesEquivalencias) throws ArqException {
		ArvoreExpressao arvore = ArvoreExpressao.fromExpressao(expressao);
		return arvore.eval(componentes, expressoesEquivalencias);
	}
	
	/**
	 * Constrói um mapa para a avaliação em segundo nível da árvore. Este mapa é usado somente no caso da avaliação falso do nó.
	 * Ver evalComEquivalencias para mais detalhes.
	 * 
	 * @param expressao
	 * @return
	 * @throws ArqException 
	 */
	public static Map<Integer, String> getEquivalenciasExpressao(String expressao) throws ArqException {
		Map<Integer, String> equivalencias =  new HashMap<Integer, String>();
		Collection<ComponenteCurricular> ccs = expressaoToComponentes(expressao, null, true);

		for (ComponenteCurricular cc : ccs) {
			if (cc.getEquivalencia() != null)
				equivalencias.put(cc.getId(), cc.getEquivalencia());
		}

		return equivalencias;
	}

	/**
	 * Constrói um mapa para a avaliação em segundo nível da árvore. Este mapa é usado somente no caso da avaliação falso do nó.
	 * Ver evalComEquivalencias para mais detalhes.
	 * 
	 * @param expressao
	 * @return
	 * @throws ArqException 
	 */
	public static Map<Integer, String> getEquivalenciasExpressao(String expressao, int idDiscente) throws ArqException {
		Map<Integer, String> equivalencias =  new HashMap<Integer, String>();
		Collection<ComponenteCurricular> ccs = expressaoToComponentes(expressao, idDiscente, true);

		for (ComponenteCurricular cc : ccs) {
			if (cc.getEquivalencia() != null)
				equivalencias.put(cc.getId(), cc.getEquivalencia());
		}

		return equivalencias;
	}


	/**
	 * Avalia a expressão em questão dado o conjunto de objetos de componentes.
	 * 
	 * @param expressao
	 * @param componentes
	 * @return
	 * @throws ArqException
	 */
	public static boolean eval(String expressao, Collection<ComponenteCurricular> componentes) throws ArqException {
		if (expressao == null || expressao.trim().equals(""))
			return true;
		TreeSet<Integer> ts = new TreeSet<Integer>();
		for (ComponenteCurricular cc : componentes) {
			ts.add(cc.getId());
		}
		return eval(expressao, ts);
	}

	/**
	 * Avalia a expressão dada uma disciplina passada como parâmetro. 
	 * 
	 * @param expressao
	 * @param disciplina
	 * @return
	 * @throws ArqException
	 */
	public static boolean eval(String expressao, ComponenteCurricular disciplina) throws ArqException {
		Collection<ComponenteCurricular> componentes = new ArrayList<ComponenteCurricular>(0);
		componentes.add(disciplina);
		return eval(expressao, componentes);
	}

	/**
	 * Avalia a expresão em questão dado o conjunto de componentes
	 * @param expressao
	 * @param componentes
	 * @return true se a expresssao satisfaz o conjunto de componentes já pagos pelo discente
	 */
	public static boolean eval(List<Integer> expressao, TreeSet<Integer> componentes) {
		Stack<Integer> operandos = new Stack<Integer>();
		int size = expressao.size();

		if (size == 1) {
			return componentes.contains(expressao.get(0));
		} else {

			for (int a = 0; a < size; a++) {
				int item = expressao.get(a);

				if (item > 0) {
					operandos.push(item);
				} else {

					Integer op1 = operandos.pop();
					Integer op2 = operandos.pop();

					if (item == OR) {
						if (componentes.contains(op1) || componentes.contains(op2))
							operandos.push(TRUE);
						else
							operandos.push(FALSE);
					} else {
						if (componentes.contains(op1) && componentes.contains(op2))
							operandos.push(TRUE);
						else
							operandos.push(FALSE);
					}
				}
			}
		}

		return operandos.pop() == TRUE;

	}

	/**
	 * Transforma e expressão de String para uma lista de Tokens
	 */
	public static List<String> expressaoToTokens(String expressao) {

		if (expressao == null)
			throw new IllegalArgumentException("Expressão não pode ser null");

		int pos = 0;
		int countTokens = 0;
		List<String> tokens = new ArrayList<String>();
		expressao = expressao.toUpperCase();

		while (pos < expressao.length()) {

			if (expressao.charAt(pos) == '(' || expressao.charAt(pos) == ')') {
				countTokens++;
				tokens.add(String.valueOf(expressao.charAt(pos)));
				pos++;
				continue;
			}

			if ((expressao.length() - pos > 2)
					&& expressao.substring(pos, pos + 2).equals("OU")) {
				countTokens++;
				tokens.add(expressao.substring(pos, pos + 2));
				pos += 2;
				continue;
			}

			if ((expressao.length() - pos > 1)
					&& expressao.substring(pos, pos + 2).equals("E ")) {
				countTokens++;
				tokens.add(expressao.substring(pos, pos + 1));
				pos++;
				continue;
			}

			if (expressao.charAt(pos) == ' ') {
				pos++;
				continue;
			}

			// procura pelo digito até o fim
			StringBuffer numero = new StringBuffer(10);
			while (expressao.charAt(pos) != ' ' && expressao.charAt(pos) != ')') {
				numero.append(expressao.charAt(pos));
				pos++;
				//Evitar StringIndexOutOfBoundsException
				if(pos >= expressao.length())
					break;
			}
			countTokens++;
			tokens.add(numero.toString());

		}

		return tokens;

	}

	/**
	 * Retorna um mapa com os componentes curriculares para substituição pelos IDs
	 * @param expressao
	 * @return
	 * @throws ArqException
	 */
	public static Map<String, Integer> expressaoToMapa(String expressao)
			throws ArqException {
		List<String> tokens = expressaoToTokens(expressao);
		Map<String, Integer> mapa = new Hashtable<String, Integer>();
		for (String token : tokens) {
			if (!validTokens.contains(token)) {
				mapa.put(token, 0);
			}
		}
		return mapa;
	}

	/**
	 * Dada uma expressão com os IDs dos componentes, retorna uma lista com os componentes curriculares que fazem parte dela.
	 * @param expressao
	 * @return
	 * @throws ArqException 
	 */
	public static Collection<ComponenteCurricular> expressaoToComponentes(String expressao) throws ArqException {
		return expressaoToComponentes(expressao, null, false);
	}
	
	/**
	 * Dada uma expressão com os IDs dos componentes, retorna uma lista com os componentes curriculares que fazem parte dela.
	 * @param expressao
	 * @return
	 * @throws ArqException 
	 */
	public static Collection<ComponenteCurricular> expressaoToComponentes(String expressao, int idDiscente) throws ArqException {
		return expressaoToComponentes(expressao, idDiscente, false);
	}
	
	/**
	 * Dada uma expressão com os IDs dos componentes, retorna uma lista com os componentes curriculares que fazem parte dela.
	 * @param expressao
	 * @return
	 * @throws ArqException 
	 */
	public static Collection<ComponenteCurricular> expressaoToComponentesPorDiscente(String expressao, Integer idDiscente) throws ArqException {
		return expressaoToComponentes(expressao, idDiscente, false);
	}

	/**
	 * Dada uma expressão com os IDs dos componentes, retorna uma lista dos componentes que fazem parte da expressão, 
	 * carregando ou não a expressão de equivalência do componente de acordo com o parâmetro reload.
	 * @param expressao
	 * @param reload
	 * @return
	 * @throws ArqException 
	 * @throws NumberFormatException 
	 */
	public static Collection<ComponenteCurricular> expressaoToComponentes(String expressao, Integer idDiscente, boolean reload) throws ArqException {
		ComponenteCurricularDao dao = new ComponenteCurricularDao();

		try {

			ArrayList<ComponenteCurricular> ccs = new ArrayList<ComponenteCurricular>();
			List<String> tokens = expressaoToTokens(expressao);
			for (String token : tokens) {
				if (!validTokens.contains(token)) {
					if (reload) {
						if (idDiscente == null)
							ccs.add(dao.findEquivalencia(new Integer(token)));
						else
							ccs.add(dao.findEquivalenciaPorDiscente(new Integer(token), idDiscente));
					} else {
						ccs.add(new ComponenteCurricular(new Integer(token)));
					}
				}
			}
			return ccs;

		} finally {
			dao.close();
		}
	}


	/**
	 * Dada uma expressão com os códigos dos componentes, retorna uma lista com os componentes curriculares que fazem parte dela.
	 * @param expressao
	 * @return
	 * @throws DAOException
	 */
	public static Collection<ComponenteCurricular> expressaoCodigoToComponentes(String expressao) throws DAOException {
		ComponenteCurricularDao dao = new ComponenteCurricularDao();

		try {

			ArrayList<ComponenteCurricular> ccs = new ArrayList<ComponenteCurricular>();
			List<String> tokens = expressaoToTokens(expressao);
			for (String token : tokens) {
				if (!validTokens.contains(token)) {
					ComponenteCurricular cc = new ComponenteCurricular();
					cc.setCodigo(token);
					ccs.add(cc);
				}
			}
			return ccs;

		} finally {
			dao.close();
		}
	}	
	
	/**
	 * Compila a expressão com os inteiros dos componentes curriculares.
	 * 
	 * @param expressao
	 * @param mapa
	 * @return
	 * @throws ArqException
	 */
	public static String compileExpressao(String expressao, Map<String, Integer> mapa) throws ArqException {

		List<String> tokens = expressaoToTokens(expressao);
		StringBuffer expression = new StringBuffer(expressao.length());
		for (String token : tokens) {
			if (!validTokens.contains(token)) {
				expression.append(mapa.get(token));
			} else {
				expression.append(token);
			}
			expression.append(" ");
		}
		return expression.toString();

	}

	/**
	 * Cria um mapa onde a chave é o operando da expressão e o valor é uma String, que vai
	 * ser substituida por outros valores posteriormente.
     *
	 * @param expressao
	 * @return
	 * @throws ArqException
	 */
	private static Map<Integer, String> expressaoToMapaFormatado(String expressao) throws ArqException {
		List<String> tokens = expressaoToTokens(expressao);
		Map<Integer, String> mapa = new Hashtable<Integer, String>();
		for (String token : tokens) {
			if (!validTokens.contains(token)) {
				mapa.put(Integer.parseInt(token), "");
			}
		}
		return mapa;
	}

	/**
	 * Substitui os operandos da expressão para os valores associados com 
	 * esses operandos no mapa passado como parâmetro.
	 *  
	 * @param expressao
	 * @param mapa
	 * @return
	 * @throws ArqException
	 */
	private static String formatarExpressao(String expressao, Map<Integer, String> mapa) throws ArqException {

		List<String> tokens = expressaoToTokens(expressao);
		StringBuffer expression = new StringBuffer(expressao.length());
		for (String token : tokens) {
			if (!validTokens.contains(token)) {
				expression.append(mapa.get(new Integer(token)));
			} else {
				expression.append(token);
			}
			expression.append(" ");
		}
		return expression.toString();

	}

	/**
	 * Dada uma expressão, substitui seus operadores por uma tag acronum com a descrição
	 * dos componentes curriculares associados à expressão.
	 * @param expressao
	 * @param dao
	 * @return
	 * @throws ArqException
	 */
	public static String buildExpressaoFromDBTag(String expressao, ComponenteCurricularDao dao) throws ArqException {
		Map<Integer, String> mapa = expressaoToMapaFormatado(expressao);
		for (Integer k : mapa.keySet()) {
			ComponenteCurricular cc =  dao.findCodigoNomeById(k, false);
			mapa.put(k, "<ACRONYM title='"+cc.getDescricaoResumida()+"'>"+cc.getCodigo()+"</ACRONYM>");
		}
		return formatarExpressao(expressao, mapa);
	}

	/**
	 * <b>Busca Somente Componentes Ativos</b>
	 * <br>
	 * <br>
	 * 
	 * Dada uma expressão, substitui seus operadores, que são os ids dos componentes, pelos códigos
	 * dos componentes curriculares associados à expressão. Busca somente componentes ativos.
	 * 
	 * @param expressao
	 * @param dao
	 * @return
	 * @throws ArqException
	 */
	public static String buildExpressaoFromDB(String expressao, ComponenteCurricularDao dao) throws ArqException {
		return buildExpressaoFromDB(expressao, dao, true);
	}
	
	/**
	 * Dada uma expressão, substitui seus operadores, que são os ids dos componentes, pelos códigos
	 * dos componentes curriculares associados à expressão.
	 * 
	 * @param expressao
	 * @param dao
	 * @return
	 * @throws ArqException
	 */
	public static String buildExpressaoFromDB(String expressao, ComponenteCurricularDao dao, boolean somenteAtivos, Map<Integer, String> cacheComponente) throws ArqException {
		Map<Integer, String> mapa = expressaoToMapaFormatado(expressao);
		Map<Integer, String> mapa2 = dao.findCodigoNomeById(mapa.keySet(), somenteAtivos, cacheComponente);
		return formatarExpressao(expressao, mapa2);
	}

	/**
	 * Dada uma expressão, substitui seus operadores, que são os ids dos componentes, pelos códigos
	 * dos componentes curriculares associados à expressão.
	 * 
	 * @param expressao
	 * @param dao
	 * @return
	 * @throws ArqException
	 */
	public static String buildExpressaoFromDB(String expressao, ComponenteCurricularDao dao, boolean somenteAtivos) throws ArqException {
		Map<Integer, String> mapa = expressaoToMapaFormatado(expressao);
		Map<Integer, String> mapa2 = dao.findCodigoNomeById(mapa.keySet(), somenteAtivos, null);
		return formatarExpressao(expressao, mapa2);
	}	
	
	/**
	 * Dado um componente curricular, seta suas expressões de pré, co-requisito e equivalência com
	 * o formato apropriado para inserir no banco de dados..
	 * 
	 * @param cc
	 * @param dao
	 * @throws ArqException
	 */
	public static void buildExpressaoFromDB(ComponenteCurricular cc, ComponenteCurricularDao dao, boolean somenteAtivos) throws ArqException {
		if (!StringUtils.isEmpty(cc.getPreRequisito())) {
			String expP = ExpressaoUtil.buildExpressaoFromDB(cc.getPreRequisito(), dao, somenteAtivos);
			cc.setPreRequisito(expP);
		}

		if (!StringUtils.isEmpty(cc.getCoRequisito())) {
			String expC = ExpressaoUtil.buildExpressaoFromDB(cc.getCoRequisito(), dao, somenteAtivos);
			cc.setCoRequisito(expC);
		}

		if (!StringUtils.isEmpty(cc.getEquivalencia())) {
			String expE = ExpressaoUtil.buildExpressaoFromDB(cc.getEquivalencia(), dao, somenteAtivos);
			cc.setEquivalencia(expE);
		}
	}	
	
	/**
	 * Dado um componente curricular, seta suas expressões de pré, co-requisito e equivalência com
	 * o formato apropriado para inserir no banco de dados..<br>
	 * PROCURA SOMENTE COMPONENTES ATIVOS
	 * 
	 * @param cc
	 * @param dao
	 * @throws ArqException
	 */
	public static void buildExpressaoFromDB(ComponenteCurricular cc, ComponenteCurricularDao dao) throws ArqException {
		ExpressaoUtil.buildExpressaoFromDB(cc, dao, true);
	}

	/**
	 * Dada uma expressão, que deve estar com códigos como operandos, para o formato adequado para armazenamento 
	 * no banco, que é com ids como operandos.
	 * 
	 * @param expressao
	 * @param dao
	 * @param cc
	 * @param somenteAtivos
	 * @return
	 * @throws ArqException
	 */
	public static String buildExpressaoToDB(String expressao, ComponenteCurricularDao dao, ComponenteCurricular cc, Boolean somenteAtivos) throws ArqException {
		Map<String, Integer> mapa = expressaoToMapa(expressao);
		if( somenteAtivos == null )
			somenteAtivos = cc.getNivel() != NivelEnsino.TECNICO;
		for (String k : mapa.keySet()) {
			Integer id = dao.findIdByCodigo(k, cc.getUnidade().getId(), cc.getNivel(), somenteAtivos);
			if (id == null)
				throw new ArqException("Não existe componente com esse código: " + k);
			else
				mapa.put(k, id);
		}
		return compileExpressao(expressao, mapa);
	}

	/**
	 * Dado um componente curricular, formata suas expressões de pré-requisito, co-requisito e equivalência, que devem
	 * estar com códigos como operandos, para o formato adequado para armazenamento no banco, que é com ids como operandos.
	 * 
	 * @param cc
	 * @param dao
	 * @param somenteAtivos
	 * @throws NegocioException
	 */
	public static void buildExpressaoToDB(ComponenteCurricular cc, ComponenteCurricularDao dao, boolean somenteAtivos) throws NegocioException {
		if (cc.getPreRequisito() != null && !cc.getPreRequisito().trim().equals("")) {
			try {
				String expP = ExpressaoUtil.buildExpressaoToDB(cc.getPreRequisito(), dao, cc, somenteAtivos);
				cc.setPreRequisito(expP);
			} catch (Exception e) {
				e.printStackTrace();
				String msg = "Expressão de pré-requisitos mal formada";
				throw new NegocioException(msg + ": " + e.getMessage());
			}
		}
		
		if (cc.getCoRequisito() != null && !cc.getCoRequisito().trim().equals("")) {
			try {

				String expC = ExpressaoUtil.buildExpressaoToDB(cc.getCoRequisito(), dao, cc, somenteAtivos);
				cc.setCoRequisito(expC);
			} catch (Exception e) {
				e.printStackTrace();
				String msg = "Expressão de co-requisitos mal formada";
				throw new NegocioException(msg + ": " + e.getMessage());
			}
		}

		if (cc.getEquivalencia() != null && !cc.getEquivalencia().trim().equals("")) {
			try {
				String expE = ExpressaoUtil.buildExpressaoToDB(cc.getEquivalencia(), dao, cc, somenteAtivos);
				cc.setEquivalencia(expE);
			} catch (Exception e) {
				e.printStackTrace();
				String msg = "Expressão de equivalência mal formada";
				throw new NegocioException(msg + ": " + e.getMessage());
			}
		}
	}

	/**
	 * Avalia uma expressão sem transitividade (para equivalências), mas antes substitui a expressão
	 * de equivalência global pela expressão de equivalência do componente.
	 * @param equivalencia
	 * @param componente
	 * @param componentes
	 * @return
	 * @throws ArqException
	 */
	public static boolean eval(String equivalencia, ComponenteCurricular componente, Collection<ComponenteCurricular> componentes) throws ArqException {
		String expressao = equivalencia.replaceAll(EquivalenciaEspecifica.EQUIVALENCIA_GLOBAL, componente.getEquivalencia());
		return eval(expressao, componentes);
	}

	/**
	 * Junta várias expressões em uma só, conectando-as através do operador lógico OU e de parênteses.
	 * Retorna uma nova expressão formada pela disjunção das expressões passadas como parâmetro.
	 * @param expressao
	 * @return
	 */
	public static String juntarExpressoes(String... expressao) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < expressao.length; i++) {
			if ( !ValidatorUtil.isEmpty(expressao[i]) ) {
				result.append(" ( " + expressao[i] + " ) ");
				if ( (i < expressao.length - 1) && (!ValidatorUtil.isEmpty(expressao[i + 1])) )
					result.append(" OU ");
			}
		}
		return result.length() > 0 ? "( " + result.toString() + " )" : null;
	}
	
}
