/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 30/05/2011
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

/** Enumeração de possíveis semestres para convocações de candidatos aprovados no vestibular.
 * @author Édipo Elder F. de Melo
 *
 */
public enum SemestreConvocacao {

	// NÃO ALTERAR A ORDEM!
	/** Convocação para vagas remanescentes no 1º e 2º semestre. */
	CONVOCA_TODOS_SEMESTRES("Convocação para vagas remanescentes no 1º e 2º semestre"),
	/** Convocação para vagas remanescentes no 1º semestre. */
	CONVOCA_APENAS_PRIMEIRO_SEMESTRE("Convocação para vagas remanescentes no 1º semestre"),
	/** Convocação para vagas remanescentes no 2º semestre. */
	CONVOCA_APENAS_SEGUNDO_SEMESTRE("Convocação para vagas remanescentes no 2º semestre");

	/** Descrição textual da enumeração. */
	private String descricao;
	
	/**
	 * Retorna um tipo de convocação de acordo com um número que
	 * representa a sua ordem de declaração.
	 * @param ordinal
	 * @return
	 */
	public static SemestreConvocacao valueOf(int ordinal) {
		return values()[ordinal];
	}
	
	
	private SemestreConvocacao() {
		descricao = name();
	}
	
	private SemestreConvocacao(String descricao){
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
}
