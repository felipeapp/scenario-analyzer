/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '15/02/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import java.util.Collection;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.prodocente.producao.dominio.CategoriaFuncional;

public class CategoriaFuncionalDAO extends GenericSigaaDAO{
	
	public CategoriaFuncionalDAO(){
	
		daoName="CategoriaFuncionalDAO";
	}
	
	public Collection<CategoriaFuncional> getAllCategorias() throws DAOException{
		
		StringBuffer hql = new StringBuffer();
		
		hql.append("From br.ufrn.sigaa.prodocente.producao.dominio.CategoriaFuncional");
		Query q = getSession().createQuery(hql.toString());
		
		return q.list();
		
	}
}
