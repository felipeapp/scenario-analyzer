/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/07/2009
 * 
 */
package br.ufrn.sigaa.avaliacao.dominio;

import java.util.Collection;
import java.util.Stack;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Lista estática de turmas de um ano/período utilizado no processamento das
 * Avaliação Institucional.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class ListaAvaliacaoInstitucionalProcessar {
	
	/** Pilha de DocenteTurma utilizados para o processamento do resultado avaliação institucional. */
	private static Stack<PersistDB> docenteTurmas = new Stack<PersistDB>();
	
	/** Lista de resultados da avaliação institucional. */
	private static Stack<ResultadoAvaliacaoDocente> resultados = new Stack<ResultadoAvaliacaoDocente>(); 
	
	/** Total de turmas a processar. */
	public static int totalAProcessar = 0;

	/** Total de turmas processadas. */
	public static int totalProcessado = 0;
	
	/** Total de resultados restantes para persistir. */
	public static int restanteSalvar = 0;

	/** Total de registros a persistir. */
	public static int totalSalvar = 0;
	
	/** Erro ocorrido no processamento. */
	private static Exception erro;
	
	/** Indica se possui turmas a processar. */
	public static synchronized boolean possuiDocenteTurma() {
		if (erro != null) return false;
		else return docenteTurmas.size() > 0;
	}

	/** Retorna a próxima turma a processar. */
	public static synchronized PersistDB getProximo() {
		return docenteTurmas.pop();
	}

	/** Contabiliza o processamento de uma turma (incrementa o total de turmas processadas) */
	public static synchronized void registraProcessada() {
		totalProcessado++;
	}
	
	/** Adiciona um resultado à lista de resultados.
	 * @param resultado
	 */
	public static synchronized void addResultado(ResultadoAvaliacaoDocente resultado) {
		resultados.add(resultado);
		totalSalvar = restanteSalvar = resultados.size();
	}
	
	/** Adiciona uma coleção de resultados à lista de resultados.
	 * @param resultadosAdicionar
	 */
	public static synchronized void addResultados(Collection<ResultadoAvaliacaoDocente> resultadosAdicionar) {
		resultados.addAll(resultadosAdicionar);
		totalSalvar = restanteSalvar = resultados.size();
	}
	
	/** Retorna a próxima turma a processar. */
	public static synchronized ResultadoAvaliacaoDocente getProximoResultado() {
		restanteSalvar = resultados.size() - 1;
		return resultados.pop();
	}

	/** Indica se possui turmas a processar. */
	public static synchronized boolean possuiResultados() {
		if (erro != null) return false;
		else return resultados.size() > 0;
	}
	/**
	 * Seta a lista de turmas utilizados para o processamento do resultado
	 * avaliação institucional.
	 * 
	 * @param listaTurma
	 */
	public static synchronized void setDocenteTurmas(Collection<? extends PersistDB> listaDocenteTurma) {
		docenteTurmas.clear();
		if (listaDocenteTurma != null)
			docenteTurmas.addAll(listaDocenteTurma);
		totalProcessado = 0;
		totalAProcessar = docenteTurmas.size();
	}

	/** Seta a exceção que pode ocorrer durante o processamento. 
	 * @param e
	 */
	public static void setErro(Exception exception) {
		erro = exception;
	}

	/** Retorna a exceção que pode ocorrer durante o processamento.
	 * @return
	 */
	public static Exception getErro() {
		return erro;
	}

	/** Zera todas as variáveis da lista */
	public static void reset() {
		totalAProcessar = 0;
		totalProcessado = 0;
		restanteSalvar = 0;
		erro = null;
		docenteTurmas = new Stack<PersistDB>();
		resultados = new Stack<ResultadoAvaliacaoDocente>();
	}

}
