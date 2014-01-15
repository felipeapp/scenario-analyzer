/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 25/05/2011
 */
package br.ufrn.sigaa.ensino.dao;

import java.util.Collection;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ExpressaoComponenteCurriculo;

/**
 * Método responsável por realizar consultas sobre a entidade ExpressaoComponenteCurriculo
 * @author Victor Hugo
 *
 */
public class ExpressaoComponenteCurriculoDao extends GenericSigaaDAO {

	/**
	 * Retorna a expressão dado o curriculo e componente no parametro. 
	 * Retorna no máximo 1 pois não pode haver mais de uma expressão cadastrada para o mesmo componente/curriculo
	 * @param idCurriculo
	 * @param idComponente
	 * @return
	 * @throws DAOException
	 */
	public ExpressaoComponenteCurriculo findByComponenteCurriculo(int idCurriculo, int idComponente) throws DAOException {
		
		StringBuilder hql = new StringBuilder(" SELECT e FROM ExpressaoComponenteCurriculo e ");
		hql.append( " WHERE e.curriculo.id = :idCurriculo " );
		hql.append( " AND e.componente.id = :idComponente " );
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idCurriculo", idCurriculo);
		q.setInteger("idComponente", idComponente);
		
		return (ExpressaoComponenteCurriculo) q.uniqueResult();
	}

	
	/**
	 * Retorna uma lista de ExpressaoComponenteCurriculo de acordo com os parametros informados
	 * @param idCurriculo
	 * @param idComponente
	 * @param ativo
	 * @return
	 * @throws DAOException
	 */
	public Collection<ExpressaoComponenteCurriculo> findByComponenteCurriculo(Integer idCurriculo, int idComponente, Boolean ativo) throws DAOException {
		
		StringBuilder hql = new StringBuilder(" SELECT e FROM ExpressaoComponenteCurriculo e ");
		hql.append( " WHERE 1=1  " );
		if( idCurriculo != null )
			hql.append( " AND e.curriculo.id = :idCurriculo " );
		hql.append( " AND e.componente.id = :idComponente " );
		if( ativo != null )
			hql.append( " AND e.ativo = :ativo " );
		
		Query q = getSession().createQuery(hql.toString());
		if( idCurriculo != null )
			q.setInteger("idCurriculo", idCurriculo);
		q.setInteger("idComponente", idComponente);
		if( ativo != null )
			q.setBoolean("ativo", ativo);
		
		return q.list();
	}
	
}
