/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 29/12/2010
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

/**
 * Enumera��o com os tipos de convoca��o poss�veis para os candidatos aprovados
 * no vestibular. Um candidato pode ser convocado para a sua primeira op��o de
 * curso escolhida durante a inscri��o no vestibular; ou, caso n�o tenha se
 * classificado dentro das vagas, pode ser convocado para um outro turno do
 * mesmo curso escolhido na primeira op��o caso surjam vagas; ou ainda, pode ser
 * convocado para a sua segunda op��o de curso.
 * 
 * @author Leonardo Campos
 * 
 */
public enum TipoConvocacao {

	/* AVISO: ESTA ORDEM N�O PODE SER MODIFICADA!! */
	/** Tipo de convoca��o por mudan�a de semestre. Valor ordinal = 0. */
	CONVOCACAO_MUDANCA_SEMESTRE("MUDAN�A DE SEMESTRE"),  
	/** Tipo de convoca��o por primeira op��o de curso. Valor ordinal = 1. */
	CONVOCACAO_PRIMEIRA_OPCAO("PRIMEIRA OP��O"),
	/** Tipo de convoca��o por turno distinto de curso. Valor ordinal = 2. */
	CONVOCACAO_TURNO_DISTINTO("TURNO DISTINTO"),
	/** Tipo de convoca��o por segunda op��o de curso. Valor ordinal = 3. */
	CONVOCACAO_SEGUNDA_OPCAO("SEGUNDA OP��O"),
	/** Tipo de convoca��o por respescagem de candidatos sem Argumento M�nimo de Aprova��o. Valor ordinal = 4. */
	CONVOCACAO_SEM_ARGUMENTO_MINIMO_APROVACAO("SEM ARGUMENTO M�NIMO DE APROVA��O");
	
	/** R�tulo/Descri��o do tipo de convoca��o. */ 
	private String label;
	
	/**
	 * Retorna um tipo de convoca��o de acordo com um n�mero que
	 * representa a sua ordem de declara��o.
	 * @param ordinal
	 * @return
	 */
	public static TipoConvocacao valueOf(int ordinal) {
		return values()[ordinal];
	}
	
	private TipoConvocacao() {
		label = name();
	}
	
	private TipoConvocacao(String label){
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
