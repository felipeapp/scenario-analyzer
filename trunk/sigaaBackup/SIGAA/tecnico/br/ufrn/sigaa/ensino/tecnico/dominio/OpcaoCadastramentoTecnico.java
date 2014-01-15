/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;



/**
 * Enumeração com as opções que podem ser informadas para cada aluno na operação
 * de cadastramento. Após uma convocação do vestibular, os alunos devem
 * comparecer ao cadastramento para confirmar sua vaga. A operação do
 * cadastramento permite informar se o aluno compareceu ou não, ou ainda ignorar
 * um determinado aluno para informar posteriormente.
 * 
 * @author Leonardo Campos
 * @author Fred_Castro
 * 
 */
public enum OpcaoCadastramentoTecnico {

	/** Opção indicando que o aluno deve ser cadastrado. Valor ordinal = 0. */
	DEFERIR("DEFERIR"),
	/** Opção indicando que o aluno não cumpriu com todo o regulamento. Valor ordinal = 1. */
	INDEFERIR("INDEFERIR"),
	/** Opção indicando que não se deseja alterar o discente. Valor ordinal = 2. */
	IGNORAR("IGNORAR");
	
	
	
	/** Rótulo/Descrição da opção de cadastramento. */ 
	private String label;

	/**
	 * Retorna uma opção de cadastramento de acordo com um número que representa
	 * a sua ordem de declaração.
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
	 * Verifica se a opção de cadastramento do discente informado corresponde à
	 * opção atual.
	 * 
	 * @param discente
	 * @return
	 */
	public boolean verificar(DiscenteTecnico discente){
		return discente.getOpcaoCadastramento() != null && ordinal() == discente.getOpcaoCadastramento();
	}
}
