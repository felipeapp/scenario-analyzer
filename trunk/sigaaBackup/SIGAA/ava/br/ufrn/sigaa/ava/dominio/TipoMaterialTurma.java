/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '16/05/2011'
 *
 */
package br.ufrn.sigaa.ava.dominio;


 
/**
 * Enumera��o com os tipos de material utilizados no Ambiente Virtual de Aprendizagem.
 * 
 * @author Ilueny Santos
 * 
 */
public enum TipoMaterialTurma {

	/* AVISO: ESTA ORDEM N�O PODE SER MODIFICADA!! */
	
	/** Arquivo. */
	ARQUIVO("ARQUIVO"), //0
	
	/** Conte�do. */
	CONTEUDO("CONTE�DO"), //1
	
	/** Question�rio. */
	QUESTIONARIO("QUESTION�RIO"), //2
	
	/** Refer�ncia. */
	REFERENCIA("REFER�NCIA"), //3
	
	/** R�tulo. */
	ROTULO("R�TULO"), //4

	/** Tarefa. */
	TAREFA("TAREFA"), //5
	
	/** V�deo. */
	VIDEO("V�DEO"), //6
	
	/** F�rum da Turma. */
	FORUM("F�RUM"), //7
	
	/** Enquete. */
	ENQUETE("ENQUETE"), //8
	
	/** Chat da Turma. */
	CHAT("CHAT"); //9
	
	
	/**
	 * Retorna um tipo de material utilizado na turma.
	 * Representa a sua ordem de declara��o.
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

