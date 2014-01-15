 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/06/2013'
 *
 */
package br.ufrn.sigaa.ava.negocio;

import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AulaExtra;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe auxiliar para operações sobre Frequências.
 * 
 * @author Diego Jácome
 *
 */
public class FrequenciaHelper {

	/**
	 * Retorna o número máximo de faltas na turma na data especificada.
	 * 
	 * @param turma
	 * @param data
	 * @throws DAOException 
	 * @throws ArqException
	 */
	public static short getMaxFaltasData(Turma turma, Date data) throws DAOException  {		
		
		TopicoAulaDao topDao = null;

		try {
			short maxFaltas = getPresencasDia(turma, data);
			
			List<AulaExtra> aulasExtra = getDAO(TurmaVirtualDao.class).buscarAulasExtra(turma, data);
			topDao = getDAO(TopicoAulaDao.class);
			List<TopicoAula> aulasCanceladas = topDao.findTopicosSemAula(turma.getId(),data);
			TopicoAula ultimaAulaCancelada = null;
			
			if (!aulasCanceladas.isEmpty()){
				maxFaltas = 0;
				ultimaAulaCancelada = aulasCanceladas.get(0);
			}
			if (aulasExtra != null) {
				for (AulaExtra aula : aulasExtra) {
					if (ultimaAulaCancelada == null || (ultimaAulaCancelada != null && aula.getCriadoEm().getTime() > ultimaAulaCancelada.getDataCadastro().getTime()))
						maxFaltas += aula.getNumeroAulas();
				}
			}

			return maxFaltas;
		} finally {
			if (topDao != null)
				topDao.close();
		}	
	}
	
	/**
	 * Retorna o número total de horários de um determinado dia de aula de uma turma.
	 * 
	 * @param turma
	 * @param diaSemana
	 * @return
	 */
	public static short getPresencasDia(Turma turma, Date data) {
		
		int diaSemana = CalendarUtils.getDiaSemanaByData(data);
		
		short presencas = 0;
		for( HorarioTurma ht: turma.getHorarios()){
			if(ht.getDataInicio().getTime() <= data.getTime() && ht.getDataFim().getTime() >= data.getTime() && diaSemana == Character.getNumericValue(ht.getDia()) ){
				presencas++;
			}
		}

		return presencas;
	}
	
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}
	
}
