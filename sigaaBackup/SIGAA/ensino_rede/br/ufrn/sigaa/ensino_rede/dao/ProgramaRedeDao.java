/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/08/2013
 *
 */

package br.ufrn.sigaa.ensino_rede.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;

/**
 * DAO responsável por consultas que envolvem o Programa de ensino em rede.
 *
 * @author Diego Jácome
 *
 */
public class ProgramaRedeDao extends GenericSigaaDAO  {

	@SuppressWarnings("unchecked")
	public List<InstituicoesEnsino> findInstituicoesByPrograma ( int idPrograma ) throws HibernateException, DAOException{
		
		String hql = " select distinct i from DadosCursoRede d " +
					 " join d.campus c " +
					 " join c.instituicao i " +
					 " where d.programaRede.id = " + idPrograma;
		
		Query q = getSession().createQuery(hql.toString());
		List<InstituicoesEnsino> result = q.list();
		
		return result;
	}
	
}
