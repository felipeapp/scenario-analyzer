package br.ufrn.sigaa.ensino.negocio;

import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/**
 * Faz as validações referentes aos dados de alunos da graduação
 * 
 * @author Henrique André
 * 
 */
public class DiscenteGraduacaoValidator extends DiscenteValidator {

	/**
	 * Retorna true se os dois discente possuem o mesmo tipo, ou seja, ambos sao
	 * regulares ou ambos especiais
	 * 
	 * @param disc
	 * @param reg
	 * @return
	 */
	public static boolean isMesmoTipoDiscente(DiscenteAdapter disc,	DiscenteAdapter reg) {
		return disc.isRegular() == reg.isRegular();
	}

}
