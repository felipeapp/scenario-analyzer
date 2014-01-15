package br.ufrn.sigaa.ouvidoria.dao;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ouvidoria.dominio.InteressadoNaoAutenticado;

/**
 * Dao respons�vel por realizar consultas de {@link InteressadoNaoAutenticado}.
 * 
 * @author Diego J�come
 * 
 */
public class InteressadoNaoAutenticadoDao extends GenericSigaaDAO {

	/**
	 * <p>M�todo usado buscas de interessados em manifesta��es da ouvidoria que s�o usu�rio externos.
	 *  Com o objetivo de ap�s a busca confirmar sua senha.</p>
	 * 
	 * @param codigoAcesso
	 * @param idInteressadoNaoAutenticado
	 * @return
	 * @throws DAOException
	 */	
	public InteressadoNaoAutenticado findInteressadoByIdCodigoAcesso(String codigoAcesso, Integer idInteressadoNaoAutenticado) throws DAOException {
		
		String hql = " SELECT DISTINCT interessado " +
				" FROM  InteressadoNaoAutenticado interessado " +
				" WHERE interessado.codigoAcesso = :codigoAcesso AND interessado.id = :idInteressadoNaoAutenticado ";
			
		Query query = getSession().createQuery(hql);
		query.setString("codigoAcesso", codigoAcesso);
		query.setInteger("idInteressadoNaoAutenticado", idInteressadoNaoAutenticado);
		query.setMaxResults(1);
		
		InteressadoNaoAutenticado interessado =  (InteressadoNaoAutenticado) query.uniqueResult();
		
		return interessado;   // usu�rio n�o cadastrado
	}	
		
	
}
