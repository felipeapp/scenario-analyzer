package br.ufrn.sigaa.ouvidoria.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaSolicitante;

/**
 * Dao para consulta em {@link CategoriaSolicitante}.
 * 
 * @author Bernardo
 *
 */
public class CategoriaSolicitanteDao extends GenericSigaaDAO {
    
	/**
	 * Retorna todas as categorias associadas a pessoa internas à instituição.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
    public Collection<CategoriaSolicitante> getAllCategoriasSolicitanteInterno() throws HibernateException, DAOException {
    	return getAllCategoriasByIds(CategoriaSolicitante.getAllCategoriasComunidadeInterna());
    }
    
    /**
     * Retorna todas as categorias cadastradas.
     * 
     * @return
     * @throws HibernateException
     * @throws DAOException
     */
    public Collection<CategoriaSolicitante> getAllCategoriasSolicitante() throws HibernateException, DAOException {
    	return getAllCategoriasByIds(CategoriaSolicitante.getAllCategorias());
    }
    
    /**
     * Retorna os dados das categorias de acordo com seus ids.
     * 
     * @param cat
     * @return
     * @throws HibernateException
     * @throws DAOException
     */
    public Collection<CategoriaSolicitante> getAllCategoriasByIds(Collection<CategoriaSolicitante> cat) throws HibernateException, DAOException {
		String hql = "from CategoriaSolicitante where id in " + UFRNUtils.gerarStringIn(cat);
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<CategoriaSolicitante> categorias = q.list();
		
		return categorias;
    }

}
