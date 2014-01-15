/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 28/01/2008
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino;

import java.util.Collection;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.OrientacaoMatricula;

/**
 * @author Ricardo Wendell
 *
 */
public class OrientacaoMatriculaDao extends GenericSigaaDAO {

	/**
	 * Busca a orientação de matrícula cadastrada para um discente
	 * em um determinado ano-período.
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public OrientacaoMatricula findByDiscente(DiscenteAdapter discente, int ano, int periodo) throws DAOException {
		String hql = " FROM OrientacaoMatricula WHERE " +
				" discente.id =  " + discente.getId() +
				" and ano = " + ano +
				" and periodo = " + periodo;
		Query q = getSession().createQuery(hql);
		return (OrientacaoMatricula) q.setMaxResults(1).uniqueResult();
	}
	
	/**
	 * Busca todas as orientações de matrículas cadastradas para um discente
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<OrientacaoMatricula> findByDiscente(DiscenteAdapter discente) throws DAOException {
		String hql = " FROM OrientacaoMatricula WHERE " +
			" discente.id =  " + discente.getId() +
			" order by ano, periodo";
		Query q = getSession().createQuery(hql);
		return q.list();
	}
	
}
