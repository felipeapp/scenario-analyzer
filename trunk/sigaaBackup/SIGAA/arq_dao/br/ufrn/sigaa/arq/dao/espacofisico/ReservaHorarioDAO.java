/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '29/12/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.espacofisico;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.espacofisico.dominio.ReservaHorario;

/**
 * 
 * @author Henrique Andre
 *
 */
public class ReservaHorarioDAO extends GenericSigaaDAO {
	
	/**
	 * Pega os horários ativos de um espaço físico
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public List<ReservaHorario> findAllAtivosByEspacoFisico(int id) throws DAOException {
		
		String hql = "select rh from ReservaHorario rh where rh.ativo = trueValue() and rh.reserva.espacoFisico.id = :id";

		Query q = getSession().createQuery(hql);
		
		q.setInteger("id", id);
		
		return q.list();
	}
	
}
