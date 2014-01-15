 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '22/07/2013'
 *
 */
package br.ufrn.sigaa.pid.negocio;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;

/**
 * Métodos utilitários para operações no PID. 
 * 
 * @author Diego Jácome
 *
 */
public class PIDUtils {

	/**
	 * Retorna a CH do docente diluida nas semanas do semestre. 
	 * 
	 * @throws DAOException 
	 */
	public static void calcularChDedicadaSemana(DocenteTurma dt, Short horasCreditosAula, Short horasCreditosEstagio) throws DAOException {
		
		// Valores Default
		if (horasCreditosAula == null || horasCreditosAula == 0)
			horasCreditosAula = 15;
		if (horasCreditosEstagio == null || horasCreditosEstagio == 0)
			horasCreditosEstagio = 45;
		
		//********************************************************
		// Cálculo arbitrário definido pelo gestor de graduação.
		//********************************************************
		
		if (dt.getTurma() != null && dt.getTurma().getDisciplina().getChEstagio() > 0)
			dt.setChDedicadaSemana( Math.round((double) dt.getChDedicadaPeriodo() / horasCreditosEstagio * 100d) / 100d ); 
		else
			dt.setChDedicadaSemana( Math.round((double) dt.getChDedicadaPeriodo() / horasCreditosAula * 100d) / 100d );
			
	}
	
}
