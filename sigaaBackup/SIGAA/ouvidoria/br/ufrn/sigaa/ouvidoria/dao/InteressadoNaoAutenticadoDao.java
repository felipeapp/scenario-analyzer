package br.ufrn.sigaa.ouvidoria.dao;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ouvidoria.dominio.InteressadoNaoAutenticado;

/**
 * Dao responsável por realizar consultas de {@link InteressadoNaoAutenticado}.
 * 
 * @author Diego Jácome
 * 
 */
public class InteressadoNaoAutenticadoDao extends GenericSigaaDAO {

	/**
	 * <p>Método usado buscas de interessados em manifestações da ouvidoria que são usuário externos.
	 *  Com o objetivo de após a busca confirmar sua senha.</p>
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
		
		return interessado;   // usuário não cadastrado
	}	
		
	
}
