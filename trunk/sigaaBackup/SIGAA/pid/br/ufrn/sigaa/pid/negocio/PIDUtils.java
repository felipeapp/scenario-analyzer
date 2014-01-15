 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '22/07/2013'
 *
 */
package br.ufrn.sigaa.pid.negocio;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;

/**
 * M�todos utilit�rios para opera��es no PID. 
 * 
 * @author Diego J�come
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
		// C�lculo arbitr�rio definido pelo gestor de gradua��o.
		//********************************************************
		
		if (dt.getTurma() != null && dt.getTurma().getDisciplina().getChEstagio() > 0)
			dt.setChDedicadaSemana( Math.round((double) dt.getChDedicadaPeriodo() / horasCreditosEstagio * 100d) / 100d ); 
		else
			dt.setChDedicadaSemana( Math.round((double) dt.getChDedicadaPeriodo() / horasCreditosAula * 100d) / 100d );
			
	}
	
}
