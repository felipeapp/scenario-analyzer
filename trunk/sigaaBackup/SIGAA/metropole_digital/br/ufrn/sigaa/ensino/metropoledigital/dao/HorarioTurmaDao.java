package br.ufrn.sigaa.ensino.metropoledigital.dao;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
/**
 * Entidade responsável por aceso aos dados da entidade HorarioTurma.
 * 
 * @author Rafael Silva
 *
 */
public class HorarioTurmaDao extends GenericSigaaDAO{
	
	/**
	 * Remove os horários cadastrada para uma turma componente.
	 * 
	 * @param idTurma
	 * @throws DAOException
	 */
	public void removeHorariosTurma(int idTurma) throws DAOException{
		if (idTurma!=0 ) {
			StringBuilder hql = new StringBuilder();
			hql.append("delete from HorarioTurma ht where ht.turma.id=:idTurma");
			
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idTurma", idTurma);
		}
			
	}
	
	

}
