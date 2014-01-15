/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/06/2009
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;

/**
 *
 *   Dao para os status dos materiais informacionais da biblioteca
 *
 * @author jadson
 * @since 03/06/2009
 * @version 1.0 criacao da classe
 *
 */
public class StatusMaterialInformacionalDao extends GenericSigaaDAO{
	
	/** 
	 * Trás todos status ativos menos o não circula.
	 *
	 * @return
	 * @throws DAOException
	 */
	public List<StatusMaterialInformacional> findStatusMaterialAtivosPermiteEmprestimo()throws DAOException{
		Criteria c = getSession().createCriteria( StatusMaterialInformacional.class );
		c.add( Restrictions.eq( "ativo" , true ) );
		c.add( Restrictions.eq("permiteEmprestimo", true));
		c.addOrder( Order.asc("descricao") );
		
		@SuppressWarnings("unchecked")
		List<StatusMaterialInformacional> lista = c.list();
		return lista;
	}
	
	
	/**
	 *       Encontra os tipos de empréstimo ativos que possuem prazos fixos. São aqueles que o usuário 
	 *    pode definir Prazos pela política de empréstimo.
	 *
	 * @param biblioteca
	 * @param tipoUsuario
	 * @return
	 * @throws DAOException
	 */
	
	public List<StatusMaterialInformacional> findAllStatusMaterialAtivos() throws DAOException{
		
		String projecao = " s.id, s.descricao";
		
		String hql = " SELECT "+projecao
		+ " FROM StatusMaterialInformacional s "
		+ " WHERE s.ativo = trueValue()"
		+ " ORDER BY s.descricao ";
		
		Query q = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		List<Object[]> dadosStatus = q.list();
		
		List<StatusMaterialInformacional> lista = new ArrayList<StatusMaterialInformacional>( HibernateUtils.parseTo(dadosStatus, projecao, StatusMaterialInformacional.class, "s"));
		return lista;
	}
	
	/**
	 *       Encontra os tipos de empréstimo ativos que possuem prazos fixos. São aqueles que o usuário 
	 *    pode definir Prazos pela política de empréstimo.
	 *
	 * @param biblioteca
	 * @param tipoUsuario
	 * @return
	 * @throws DAOException
	 */
	public StatusMaterialInformacional findStatusMaterialAtivoByDescricao(String descricao) throws DAOException{
		
		Criteria c = getSession().createCriteria( StatusMaterialInformacional.class );
		c.setProjection(Projections.projectionList().add(Projections.property("id")).add(Projections.property("descricao")) );
		c.add( Restrictions.eq( "ativo" , true ) );
		c.add( Restrictions.ilike( "descricao" , descricao ) );
		c.addOrder( Order.asc("descricao") );
		c.setMaxResults(1);
		
		Object[] object = (Object[]) c.uniqueResult();
		
		if(object != null)
			return new StatusMaterialInformacional((Integer)object[0], (String) object[1]);
		else
			return null;
	}
	
}
