/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Lista est�tica de turmas de um ano/per�odo utilizado no processamento das
 * Avalia��o Institucional.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
public class ListaAvaliacaoInstitucionalProcessar {
	
	/** Pilha de DocenteTurma utilizados para o processamento do resultado avalia��o institucional. */
	private static Stack<PersistDB> docenteTurmas = new Stack<PersistDB>();
	
	/** Lista de resultados da avalia��o institucional. */
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

	/** Retorna a pr�xima turma a processar. */
	public static synchronized PersistDB getProximo() {
		return docenteTurmas.pop();
	}

	/** Contabiliza o processamento de uma turma (incrementa o total de turmas processadas) */
	public static synchronized void registraProcessada() {
		totalProcessado++;
	}
	
	/** Adiciona um resultado � lista de resultados.
	 * @param resultado
	 */
	public static synchronized void addResultado(ResultadoAvaliacaoDocente resultado) {
		resultados.add(resultado);
		totalSalvar = restanteSalvar = resultados.size();
	}
	
	/** Adiciona uma cole��o de resultados � lista de resultados.
	 * @param resultadosAdicionar
	 */
	public static synchronized void addResultados(Collection<ResultadoAvaliacaoDocente> resultadosAdicionar) {
		resultados.addAll(resultadosAdicionar);
		totalSalvar = restanteSalvar = resultados.size();
	}
	
	/** Retorna a pr�xima turma a processar. */
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
	 * avalia��o institucional.
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

	/** Seta a exce��o que pode ocorrer durante o processamento. 
	 * @param e
	 */
	public static void setErro(Exception exception) {
		erro = exception;
	}

	/** Retorna a exce��o que pode ocorrer durante o processamento.
	 * @return
	 */
	public static Exception getErro() {
		return erro;
	}

	/** Zera todas as vari�veis da lista */
	public static void reset() {
		totalAProcessar = 0;
		totalProcessado = 0;
		restanteSalvar = 0;
		erro = null;
		docenteTurmas = new Stack<PersistDB>();
		resultados = new Stack<ResultadoAvaliacaoDocente>();
	}

}
