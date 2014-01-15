/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 29/09/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dominio;

/**
 * Enumera��o com as op��es de ordena��o poss�veis para realizar a busca de turma ou disciplinas.
 * 
 * @author Rafael Gomes
 *
 */
public enum OrdemBuscaDisciplina {
	
	/** Indica se a ordena��o dos dados deve ser feita pelo local. */
	ORDENAR_POR_SERIE_TURMA("Ordenar por S�rie e Turma"),
	/** Indica se a ordena��o dos dados deve ser feita por componente curricular. */
	ORDENAR_POR_COMPONENTE_CURRICULAR("Ordenar por Componente Curricular"),
	/** Indica se a ordena��o dos dados deve ser feita de acordo com os hor�rios da turma. */
	ORDENAR_POR_DIAS_SEMANA_RESPECTIVOS_HORARIOS("Ordenar por dias da semana e respectivos hor�rios"),
	/** Indica se a ordena��o dos dados deve ser feita pelo nome do docente, da disciplina e descri��o dos hor�rios, nessa ordem de prefer�ncia. */
	ORDENAR_POR_DOCENTE_DISCIPLINA_HORARIOS("Ordenar por docente, disciplinas e respectivos hor�rios"),
	/** Indica se a ordena��o dos dados deve ser feita pelo local. */
	ORDENAR_POR_LOCAL(" Ordenar por Local");

	/** R�tulo/Descri��o da op��o de cadastramento. */ 
	private String label;
	
	/**
	 * Retorna uma ordena��o de busca de acordo com um n�mero que representa
	 * a sua ordem de declara��o.
	 * 
	 * @param ordinal
	 * @return
	 */
	public static OrdemBuscaDisciplina valueOf(int ordinal) {
		return values()[ordinal];
	}
	
	/** Retorna a Ordena��o com o label do enum.*/
	private OrdemBuscaDisciplina(){
		label = name();
	}
	
	/** Retorna a Ordena��o com o label do enum, passando o label como par�metro.*/
	private OrdemBuscaDisciplina(String label){
		this.label = label;
	}
	
	/** Retorna o label Ordena��o.*/
	public String getLabel() {
		return label;
	}
	
}
