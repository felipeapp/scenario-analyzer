/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 29/07/2008 
 *
 */

package br.ufrn.sigaa.processamento.batch;

import java.util.List;
import java.util.Stack;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaDao;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaGraduacaoDao;

/**
 * Classe que armazena a lista de matrículas com co-requisitos a serem processadas.
 * 
 * @author David Pereira
 *
 */
public class ListaCoRequisitosProcessar {

	/** array de discentes */
	static Stack<Integer> discentes = new Stack<Integer>();

	/** processamento sendo realizado */
	static boolean bloqueada;

	static int totalDiscentes = 0;

	static int totalProcessados = 0;

	public static synchronized boolean possuiDiscentes() {
		return discentes.size() > 0;
	}

	/** entrega o próximo discente para ser processada */
	public static synchronized Integer getProximoDiscente() {
		return discentes.pop();
	}

	public static synchronized void registraProcessada() {
		totalProcessados++;
	}

	/**
	 * Carrega os Discentes
	 * @param ano
	 * @param periodo
	 * @param rematricula 
	 * @param nivel 
	 * @throws DAOException
	 */
	public static synchronized void carregarDiscentes(int ano, int periodo, char nivel, boolean rematricula) throws DAOException {

		ProcessamentoMatriculaDao dao = new ProcessamentoMatriculaGraduacaoDao();
		try {
			discentes.clear();

			List<Integer> discentesBuscados = dao.findDiscentesMatriculadosEmCoRequisitos(ano, periodo, rematricula);
			for ( Integer t : discentesBuscados )
				discentes.add(t);

			totalDiscentes = discentes.size();
		} finally {
			dao.close();
		}
	}

}
