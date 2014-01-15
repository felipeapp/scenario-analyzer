/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 21/03/2013
*/

package br.ufrn.sigaa.negocio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dao.PeriodoAcademicoDao;
import br.ufrn.sigaa.dominio.PeriodoAcademico;

/**
 * Classe utilitaria para manipular o PeriodoAcademico
 * 
 * @author Henrique André
 *
 */
public class PeriodoAcademicoHelper {
	
	/**
	 * Singleton
	 */
	private static PeriodoAcademicoHelper singleton = new PeriodoAcademicoHelper();
	
	/**
	 * Construtor padrão
	 */
	public PeriodoAcademicoHelper() {
		try {
			inicializar();
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Todos os períodos ativos
	 */
	private List<PeriodoAcademico> periodos;	
	
	/**
	 * Retorna uma instância de PeriodoAcademicoHelper
	 * @return
	 */
	public synchronized static PeriodoAcademicoHelper getInstance() {
		return singleton;
	}
	
	/**
	 * Carrega todos os periodos ativos disponíveis
	 * @throws DAOException
	 */
	private void inicializar() throws DAOException {
		if (periodos == null) {
			
			PeriodoAcademicoDao dao = DAOFactory.getInstance().getDAO(PeriodoAcademicoDao.class);

			try {
				periodos = (List<PeriodoAcademico>) dao.findAllAtivos(PeriodoAcademico.class, "periodo");
			} finally {
				dao.close();
			}
			
		}
	}
	
	/**
	 * Retorna os períodos regulares
	 * 
	 * @return
	 */
	public List<PeriodoAcademico> getPeriodosRegulares() {
		List<PeriodoAcademico> resultado = null;
		
		for (PeriodoAcademico periodo : periodos) {
			if (periodo.isRegular()) {
				if (resultado == null)
					resultado = new ArrayList<PeriodoAcademico>();
				resultado.add(periodo);
			}
		}
				
		return resultado;
	}

	/**
	 * Retorna os periodos Intervalares
	 * 
	 * @return
	 */
	public List<PeriodoAcademico> getPeriodosIntervalares() {
		List<PeriodoAcademico> resultado = null;
		
		for (PeriodoAcademico periodo : periodos) {
			if (periodo.isIntervalar()) {
				if (resultado == null)
					resultado = new ArrayList<PeriodoAcademico>();
				resultado.add(periodo);
			}
		}
				
		return resultado;
	}
	
	/**
	 * Indica se o período informado é regular ou não
	 * 
	 * @param periodo
	 * @return
	 */
	public boolean isPeriodoRegular(int periodo) {
		for (PeriodoAcademico pa : periodos) {
			if (pa.getPeriodo() == periodo)
				return pa.isRegular();
		}
		return false;
	}
	
	/**
	 * Indica se o período informado é intervalar ou não
	 * 
	 * @param periodo
	 * @return
	 */

	public boolean isPeriodoIntervalar(int periodo) {
		for (PeriodoAcademico pa : periodos) {
			if (pa.getPeriodo() == periodo)
				return pa.isIntervalar();
		}
		return false;
	}	
	
	/**
	 * Retorna uma string com os períodos regulares separados por vírgula.
	 * @return
	 */
	public String getPeriodosRegularesFormatado() {
		StringBuilder sb = new StringBuilder();
		
		for (PeriodoAcademico pa : getPeriodosRegulares()) {
			sb.append(pa.getPeriodo() + ", ");
		}
		
		int lastIndexOf = sb.lastIndexOf(",");
		sb.delete(lastIndexOf, sb.length());
		
		return sb.toString();
	}
}
