/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 14/06/2011 
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.Comparator;

import br.ufrn.sigaa.processamento.dominio.MatriculaEmProcessamento;

/**
 * Comparator para ordenar listas de matrículas de acordo
 * com os critérios definidos para a transferência de alunos entre turmas.
 * 
 * @author Rafael G. Rodrigues
 *
 */
public class TransferenciaTurmasComparator implements Comparator<MatriculaEmProcessamento>{

	/**
	 * Compara uma matrícula com as outras dentro de uma mesma reserva. Os critérios
	 * de ordenação para a transferência de alunos automática levará em consideração 
	 * a ordenação inversa das prioridades de matrícula definido no regulamento. 
	 * A prioridade de matrícula definidos no Regulamento dos cursos de graduação são os seguintes  
	 * 
	 *  1. Aluno recém ingresso através de vestibular (V)
	 *  2. Aluno nivelado (N)
	 *  3. Aluno formando (F)
	 *  4. Aluno em recuperação (R)
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
	 * Compara os Índices Acadêmicos de dois alunos.
	 */
	private int comparaIndice(MatriculaEmProcessamento o1, MatriculaEmProcessamento o2) {
		return Double.valueOf(o1.getIndice()).compareTo(o2.getIndice());
	}

	/**
	 * Compara os tipos de matrículas de dois alunos, de acordo
	 * com o atributo tipo, da classe {@link MatriculaEmProcessamento}.
	 */
	private int comparaTipo(MatriculaEmProcessamento o1, MatriculaEmProcessamento o2) {
		return Integer.valueOf(o1.getTipo()).compareTo(o2.getTipo());
	}
	
}
