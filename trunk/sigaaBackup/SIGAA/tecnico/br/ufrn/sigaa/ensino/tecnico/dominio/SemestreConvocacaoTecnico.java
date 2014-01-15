/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;

/** Enumera��o de poss�veis semestres para convoca��es de candidatos aprovados no vestibular.
 * @author �dipo Elder F. de Melo
 * @author Fred_Castro
 *
 */
public enum SemestreConvocacaoTecnico {

	// N�O ALTERAR A ORDEM!
	/** Convoca��o para vagas remanescentes no 1� e 2� semestre. */
	CONVOCA_TODOS_SEMESTRES("Convoca��o para vagas remanescentes no 1� e 2� semestre"),
	/** Convoca��o para vagas remanescentes no 1� semestre. */
	CONVOCA_APENAS_PRIMEIRO_SEMESTRE("Convoca��o para vagas remanescentes no 1� semestre"),
	/** Convoca��o para vagas remanescentes no 2� semestre. */
	CONVOCA_APENAS_SEGUNDO_SEMESTRE("Convoca��o para vagas remanescentes no 2� semestre");

	/** Descri��o textual da enumera��o. */
	private String descricao;
	
	/**
	 * Retorna um tipo de convoca��o de acordo com um n�mero que
	 * representa a sua ordem de declara��o.
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
