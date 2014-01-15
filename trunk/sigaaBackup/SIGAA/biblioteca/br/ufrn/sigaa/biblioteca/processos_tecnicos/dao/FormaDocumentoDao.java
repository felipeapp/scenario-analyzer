/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/04/2012
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormaDocumento;

/**
 * <p>Dao para as buscas específicas em formadas de documento.</p>
 * 
 * @author jadson
 *
 */
public class FormaDocumentoDao extends GenericSigaaDAO {

	/**
	 *       Encontra as formas de documento ativas que possuem a mesma denominação passada.
	 *
	 * @param biblioteca
	 * @param tipoUsuario
	 * @return
	 * @throws DAOException
	 */
	public FormaDocumento findFormaDocumentoMaterialAtivoByDenominacao(String denominacao) throws DAOException{
		
		Criteria c = getSession().createCriteria( FormaDocumento.class );
		c.setProjection(Projections.projectionList().add(Projections.property("id")).add(Projections.property("denominacao")) );
		c.add( Restrictions.eq( "ativo" , true ) );
		c.add( Restrictions.ilike( "denominacao" , denominacao ) );
		c.addOrder( Order.asc("denominacao") );
		c.setMaxResults(1);
		
		Object[] object = (Object[]) c.uniqueResult();
		
		if(object != null)
			return new FormaDocumento((Integer)object[0], (String) object[1]);
		else
			return null;
	}
	
}
