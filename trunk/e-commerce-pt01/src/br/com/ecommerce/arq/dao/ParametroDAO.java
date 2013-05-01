package br.com.ecommerce.arq.dao;

import org.hibernate.HibernateException;

import br.com.ecommerce.arq.erros.DAOException;

/**
 * DAO para trato de par�metros.
 * @author Rodrigo Dutra de Oliveira
 *
 */
public class ParametroDAO extends GenericDAOImpl{

	public ParametroDAO(){
		this.daoName = ParametroDAO.class.getName();
	}
	
	/**
	 * M�todo usado para se buscar por um determinado par�metro.
	 * @return o valor do parametro encontrado.
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String getParametro(String codigo) throws HibernateException, DAOException{
		String hql = "select p.valor from Parametro as p where p.codigo = :codigo";
		
		return (String) getSession().createQuery(hql).setString("codigo", codigo).uniqueResult();
	}
}
