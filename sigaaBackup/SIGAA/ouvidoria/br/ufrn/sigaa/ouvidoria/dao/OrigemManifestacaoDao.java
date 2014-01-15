package br.ufrn.sigaa.ouvidoria.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ouvidoria.dominio.OrigemManifestacao;

/**
 * Dao responsável por consultas em {@link OrigemManifestacao}.
 * 
 * @author Bernardo
 *
 */
public class OrigemManifestacaoDao extends GenericSigaaDAO {
    
	/**
	 * Retorna todas as origens cadastradas.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
    public Collection<OrigemManifestacao> getAllOrigensManifestacao() throws HibernateException, DAOException {
    	return getAllOrigensByIds(OrigemManifestacao.getAllOrigensManifestacao());
    }
    
    /**
     * Retorna uma coleção de origens de acordo com seus ids.
     * 
     * @param ori
     * @return
     * @throws HibernateException
     * @throws DAOException
     */
    public Collection<OrigemManifestacao> getAllOrigensByIds(Collection<OrigemManifestacao> ori) throws HibernateException, DAOException {
		String hql = "from OrigemManifestacao where id in " + UFRNUtils.gerarStringIn(ori);
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<OrigemManifestacao> origens = q.list();
		
		return origens;
    }

}
