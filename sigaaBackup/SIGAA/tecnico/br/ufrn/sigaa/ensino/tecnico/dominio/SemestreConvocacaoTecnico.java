/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;

/** Enumeração de possíveis semestres para convocações de candidatos aprovados no vestibular.
 * @author Édipo Elder F. de Melo
 * @author Fred_Castro
 *
 */
public enum SemestreConvocacaoTecnico {

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
	public static SemestreConvocacaoTecnico valueOf(int ordinal) {
		return values()[ordinal];
	}
	
	
	private SemestreConvocacaoTecnico() {
		descricao = name();
	}
	
	private SemestreConvocacaoTecnico(String descricao){
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
}
