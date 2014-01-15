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
public enum OpcaoPreCadastramentoTecnico {

	/** Opção indicando que o aluno compareceu ao cadastramento. Valor ordinal = 0. */
	PRESENTE("PRESENTE"),
	/** Opção indicando que o aluno não compareceu ao cadastramento. Valor ordinal = 1. */
	AUSENTE("AUSENTE"),
	/** Opção indicando que deseja informar se o aluno compareceu ou não posteriormente. Valor ordinal = 2. */
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
	public static OpcaoPreCadastramentoTecnico valueOf(int ordinal) {
		return values()[ordinal];
	}
	
	private OpcaoPreCadastramentoTecnico(){
		label = name();
	}
	
	private OpcaoPreCadastramentoTecnico(String label){
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
