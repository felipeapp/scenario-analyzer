package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CargaHorariaSemanalDisciplina;

/**
 * Dao respons�vel pelo acesso aos dados da entidade CargaHor�riaSemanalDisciplina
 * 
 * @author Rafael Silva
 *
 */
public class CargaHorariaSemanalDisciplinaDao extends GenericSigaaDAO{

	/**
	 * Retorna as cargas hor�rias das disciplinas do per�odo informado. 
	 * 
	 * @param idPeriodo
	 * @return
	 * @throws DAOException
	 */
	public List<CargaHorariaSemanalDisciplina> findByPeriodo(int idPeriodo) throws DAOException{
		Criteria c = getSession().createCriteria(CargaHorariaSemanalDisciplina.class);
		c.createAlias("c.periodo.id", "idPeriodo");
		c.add(Expression.eq("idPeriodo", idPeriodo));
		
		return c.list();
	}
	
	
	/**
	 * Retorna a carga hor�ria semanal da disciplina no cronograma informado.
	 * 
	 * @param idCronograma
	 * @param idDisciplina
	 * @return
	 * @throws DAOException
	 */
	public List<CargaHorariaSemanalDisciplina> findByDisciplina(int idCronograma, int idDisciplina) throws DAOException{
		Criteria c = getSession().createCriteria(CargaHorariaSemanalDisciplina.class);
		Criteria cDisciplina = c.createCriteria("disciplina");
		Criteria cPeriodo = c.createCriteria("periodoAvaliacao");
		Criteria cCrono = cPeriodo.createCriteria("cronogramaExecucaoAulas");
		
		cDisciplina.add(Expression.eq("id", idDisciplina));
		cCrono.add(Expression.eq("id", idCronograma));
		if (c.list().size()==0) {
			return Collections.emptyList();
		}
		
		return c.list();
	}
	
	
}
