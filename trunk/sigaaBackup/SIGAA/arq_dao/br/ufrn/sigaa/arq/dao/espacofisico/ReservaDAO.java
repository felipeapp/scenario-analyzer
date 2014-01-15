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

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.espacofisico.dominio.Reserva;

/**
 * 
 * @author Henrique Andre
 *
 */
public class ReservaDAO extends GenericSigaaDAO {

	/**
	 * Busca uma reserva de um espaço físico apenas com os horários de reserva ativos
	 * Busca por espaço físico
	 * 
	 * @param id
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Reserva> findByEspacoFisicoWithHorariosAtivos(int id) throws DAOException {
		
		String hql = "select rh.reserva from ReservaHorario rh where rh.ativo = trueValue() and rh.reserva.espacoFisico.id = :id";
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger("id", id);
		
		return q.list();
	}

	/**
	 * Busca uma reserva de um espaço físico apenas com os horários de reserva ativos
	 * Busca pelo id da reserva
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Reserva findByHorarioAtivo(int id) throws DAOException {
		String hql = "select rh.reserva from ReservaHorario rh where rh.ativo = trueValue() and rh.id = :id";
		Query q = getSession().createQuery(hql);
		
		q.setInteger("id", id);
		
		return (Reserva) q.uniqueResult();
	}
	
}
