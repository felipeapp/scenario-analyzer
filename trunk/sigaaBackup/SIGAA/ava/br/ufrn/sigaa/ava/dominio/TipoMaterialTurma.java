/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '16/05/2011'
 *
 */
package br.ufrn.sigaa.ava.dominio;


 
/**
 * Enumeração com os tipos de material utilizados no Ambiente Virtual de Aprendizagem.
 * 
 * @author Ilueny Santos
 * 
 */
public enum TipoMaterialTurma {

	/* AVISO: ESTA ORDEM NÃO PODE SER MODIFICADA!! */
	
	/** Arquivo. */
	ARQUIVO("ARQUIVO"), //0
	
	/** Conteúdo. */
	CONTEUDO("CONTEÚDO"), //1
	
	/** Questionário. */
	QUESTIONARIO("QUESTIONÁRIO"), //2
	
	/** Referência. */
	REFERENCIA("REFERÊNCIA"), //3
	
	/** Rótulo. */
	ROTULO("RÓTULO"), //4

	/** Tarefa. */
	TAREFA("TAREFA"), //5
	
	/** Vídeo. */
	VIDEO("VÍDEO"), //6
	
	/** Fórum da Turma. */
	FORUM("FÓRUM"), //7
	
	/** Enquete. */
	ENQUETE("ENQUETE"), //8
	
	/** Chat da Turma. */
	CHAT("CHAT"); //9
	
	
	/**
	 * Retorna um tipo de material utilizado na turma.
	 * Representa a sua ordem de declaração.
	 * 
	 * @param ordinal
	 * @return
	 */
	public static TipoMaterialTurma valueOf(int ordinal) {
		return values()[ordinal];
	}
	
	/** Texto com o nome do tipo de material.*/
	private String label;
	
	private TipoMaterialTurma() {
		label = name();
	}
	
	private TipoMaterialTurma(String label){
		this.label = label;
	}
	
	/** Retorna o nome do tipo de material. */
	public String label() {
		return label;
	}
}

