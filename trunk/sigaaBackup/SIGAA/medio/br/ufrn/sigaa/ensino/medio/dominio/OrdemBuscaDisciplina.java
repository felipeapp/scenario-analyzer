/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 29/09/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dominio;

/**
 * Enumeração com as opções de ordenação possíveis para realizar a busca de turma ou disciplinas.
 * 
 * @author Rafael Gomes
 *
 */
public enum OrdemBuscaDisciplina {
	
	/** Indica se a ordenação dos dados deve ser feita pelo local. */
	ORDENAR_POR_SERIE_TURMA("Ordenar por Série e Turma"),
	/** Indica se a ordenação dos dados deve ser feita por componente curricular. */
	ORDENAR_POR_COMPONENTE_CURRICULAR("Ordenar por Componente Curricular"),
	/** Indica se a ordenação dos dados deve ser feita de acordo com os horários da turma. */
	ORDENAR_POR_DIAS_SEMANA_RESPECTIVOS_HORARIOS("Ordenar por dias da semana e respectivos horários"),
	/** Indica se a ordenação dos dados deve ser feita pelo nome do docente, da disciplina e descrição dos horários, nessa ordem de preferência. */
	ORDENAR_POR_DOCENTE_DISCIPLINA_HORARIOS("Ordenar por docente, disciplinas e respectivos horários"),
	/** Indica se a ordenação dos dados deve ser feita pelo local. */
	ORDENAR_POR_LOCAL(" Ordenar por Local");

	/** Rótulo/Descrição da opção de cadastramento. */ 
	private String label;
	
	/**
	 * Retorna uma ordenação de busca de acordo com um número que representa
	 * a sua ordem de declaração.
	 * 
	 * @param ordinal
	 * @return
	 */
	public static OrdemBuscaDisciplina valueOf(int ordinal) {
		return values()[ordinal];
	}
	
	/** Retorna a Ordenação com o label do enum.*/
	private OrdemBuscaDisciplina(){
		label = name();
	}
	
	/** Retorna a Ordenação com o label do enum, passando o label como parâmetro.*/
	private OrdemBuscaDisciplina(String label){
		this.label = label;
	}
	
	/** Retorna o label Ordenação.*/
	public String getLabel() {
		return label;
	}
	
}
