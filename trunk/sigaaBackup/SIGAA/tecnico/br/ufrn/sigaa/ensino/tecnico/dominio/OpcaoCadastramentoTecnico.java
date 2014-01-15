/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;



/**
 * Enumera��o com as op��es que podem ser informadas para cada aluno na opera��o
 * de cadastramento. Ap�s uma convoca��o do vestibular, os alunos devem
 * comparecer ao cadastramento para confirmar sua vaga. A opera��o do
 * cadastramento permite informar se o aluno compareceu ou n�o, ou ainda ignorar
 * um determinado aluno para informar posteriormente.
 * 
 * @author Leonardo Campos
 * @author Fred_Castro
 * 
 */
public enum OpcaoCadastramentoTecnico {

	/** Op��o indicando que o aluno deve ser cadastrado. Valor ordinal = 0. */
	DEFERIR("DEFERIR"),
	/** Op��o indicando que o aluno n�o cumpriu com todo o regulamento. Valor ordinal = 1. */
	INDEFERIR("INDEFERIR"),
	/** Op��o indicando que n�o se deseja alterar o discente. Valor ordinal = 2. */
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
	public static OpcaoCadastramentoTecnico valueOf(int ordinal) {
		return values()[ordinal];
	}
	
	private OpcaoCadastramentoTecnico(){
		label = name();
	}
	
	private OpcaoCadastramentoTecnico(String label){
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
	public boolean verificar(DiscenteTecnico discente){
		return discente.getOpcaoCadastramento() != null && ordinal() == discente.getOpcaoCadastramento();
	}
}
