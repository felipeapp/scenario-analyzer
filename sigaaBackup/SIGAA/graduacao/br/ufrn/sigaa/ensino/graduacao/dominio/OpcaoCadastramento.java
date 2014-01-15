/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 19/09/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;


/**
 * Enumera��o com as op��es que podem ser informadas para cada aluno na opera��o
 * de cadastramento. Ap�s uma convoca��o do vestibular, os alunos devem
 * comparecer ao cadastramento para confirmar sua vaga. A opera��o do
 * cadastramento permite informar se o aluno compareceu ou n�o, ou ainda ignorar
 * um determinado aluno para informar posteriormente.
 * 
 * @author Leonardo Campos
 * 
 */
public enum OpcaoCadastramento {

	/** Op��o indicando que o aluno compareceu ao cadastramento. Valor ordinal = 0. */
	PRESENTE("PRESENTE"),
	/** Op��o indicando que o aluno n�o compareceu ao cadastramento. Valor ordinal = 1. */
	AUSENTE("AUSENTE"),
	/** Op��o indicando que deseja informar se o aluno compareceu ou n�o posteriormente. Valor ordinal = 2. */
	IGNORAR("IGNORAR");
	
	/** R�tulo/Descri��o da op��o de cadastramento. */ 
	private String label;

	/**
	 * Retorna uma op��o de cadastramento de acordo com um n�mero que representa
	 * a sua ordem de declara��o.
	 * 
	 * @param ordinal
	 * @return
	 */
	public static OpcaoCadastramento valueOf(int ordinal) {
		return values()[ordinal];
	}
	
	private OpcaoCadastramento(){
		label = name();
	}
	
	private OpcaoCadastramento(String label){
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}

	/**
	 * Verifica se a op��o de cadastramento do discente informado corresponde �
	 * op��o atual.
	 * 
	 * @param discente
	 * @return
	 */
	public boolean verificar(DiscenteGraduacao discente){
		return discente.getOpcaoCadastramento() != null && ordinal() == discente.getOpcaoCadastramento();
	}
}
