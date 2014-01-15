/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 15/07/2009 
 *
 */
package br.ufrn.sigaa.processamento.dominio;

import java.util.Comparator;

/**
 * Comparator para ordenar listas de matr�culas de f�rias de acordo
 * com os crit�rios definidos para o processamento de matr�culas
 * no regulamento da institui��o.
 * 
 * @author David Pereira
 *
 */
public class ProcessamentoGraduacaoFeriasComparator implements Comparator<MatriculaEmProcessamento>{

	/**
	 * Compara uma matr�cula com as outras dentro de uma mesma reserva. Os crit�rios
	 * de ordena��o est�o definidos no Regulamento dos cursos de gradua��o e s�o os seguintes:
	 * 
	 *  1. Aluno rec�m ingresso atrav�s de vestibular (V)
	 *  2. Aluno nivelado (N)
	 *  3. Aluno formando (F)
	 *  4. Aluno em recupera��o (R)
	 *  5. Aluno adiantado (A)
	 *  6. Aluno pagando componente eletivo (E)
	 */
	public int compare(MatriculaEmProcessamento o1, MatriculaEmProcessamento o2) {
		int comparaTipo = comparaTipo(o1, o2);
		if (comparaTipo > 0) return 1;
		else if (comparaTipo == 0) return comparaIndice(o1, o2);
		else return -1;
	}
	
	/**
	 * Compara os �ndices Acad�micos de dois alunos.
	 */
	private int comparaIndice(MatriculaEmProcessamento o1, MatriculaEmProcessamento o2) {
		return Double.valueOf(o1.getIndice()).compareTo(o2.getIndice());
	}

	/**
	 * Compara os tipos de matr�culas de dois alunos, de acordo
	 * com o atributo tipo, da classe {@link MatriculaEmProcessamento}.
	 */
	private int comparaTipo(MatriculaEmProcessamento o1, MatriculaEmProcessamento o2) {
		return Integer.valueOf(o1.getTipo()).compareTo(o2.getTipo());
	}
	
}
