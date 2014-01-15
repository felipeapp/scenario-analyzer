/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 29/12/2010
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

/**
 * Enumeração com os tipos de convocação possíveis para os candidatos aprovados
 * no vestibular. Um candidato pode ser convocado para a sua primeira opção de
 * curso escolhida durante a inscrição no vestibular; ou, caso não tenha se
 * classificado dentro das vagas, pode ser convocado para um outro turno do
 * mesmo curso escolhido na primeira opção caso surjam vagas; ou ainda, pode ser
 * convocado para a sua segunda opção de curso.
 * 
 * @author Leonardo Campos
 * 
 */
public enum TipoConvocacao {

	/* AVISO: ESTA ORDEM NÃO PODE SER MODIFICADA!! */
	/** Tipo de convocação por mudança de semestre. Valor ordinal = 0. */
	CONVOCACAO_MUDANCA_SEMESTRE("MUDANÇA DE SEMESTRE"),  
	/** Tipo de convocação por primeira opção de curso. Valor ordinal = 1. */
	CONVOCACAO_PRIMEIRA_OPCAO("PRIMEIRA OPÇÃO"),
	/** Tipo de convocação por turno distinto de curso. Valor ordinal = 2. */
	CONVOCACAO_TURNO_DISTINTO("TURNO DISTINTO"),
	/** Tipo de convocação por segunda opção de curso. Valor ordinal = 3. */
	CONVOCACAO_SEGUNDA_OPCAO("SEGUNDA OPÇÃO"),
	/** Tipo de convocação por respescagem de candidatos sem Argumento Mínimo de Aprovação. Valor ordinal = 4. */
	CONVOCACAO_SEM_ARGUMENTO_MINIMO_APROVACAO("SEM ARGUMENTO MÍNIMO DE APROVAÇÃO");
	
	/** Rótulo/Descrição do tipo de convocação. */ 
	private String label;
	
	/**
	 * Retorna um tipo de convocação de acordo com um número que
	 * representa a sua ordem de declaração.
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
