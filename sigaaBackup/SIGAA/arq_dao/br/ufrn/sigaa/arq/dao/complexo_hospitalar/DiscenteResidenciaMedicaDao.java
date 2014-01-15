/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/19/2010
 *
 */
package br.ufrn.sigaa.arq.dao.complexo_hospitalar;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 * DAO responsável pelas consultas sobre DiscenteResidenciaMedica
 * 
 * @author Thalisson Muriel
 *
 */
public class DiscenteResidenciaMedicaDao extends GenericSigaaDAO {
	
	public DiscenteResidenciaMedicaDao() {
	}
		
	public Long findByCrm(int id, int crm) throws DAOException{
		List<Integer> situacao = new ArrayList<Integer>();
		situacao.add(StatusDiscente.EXCLUIDO);
		situacao.add(StatusDiscente.CANCELADO);
		
		String hql = "select count(*) from DiscenteResidenciaMedica d where d.id = :id or d.crm = :crm and d.discente.status not in ( :status ) ";
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("id", id);
		q.setInteger("crm", crm);
		q.setParameterList("status", situacao);
		
		Long discentes = (Long) q.uniqueResult();
		
		return discentes;
	}

}
