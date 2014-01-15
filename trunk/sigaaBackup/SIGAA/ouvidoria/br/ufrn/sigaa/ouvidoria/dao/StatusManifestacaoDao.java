package br.ufrn.sigaa.ouvidoria.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ouvidoria.dominio.StatusManifestacao;

/**
 * Dao responsável por consultas em {@link StatusManifestacao}.
 * 
 * @author Bernardo
 *
 */
public class StatusManifestacaoDao extends GenericSigaaDAO {
    
	/**
	 * Retorna todos os status cadastrados.
	 * 
	 * @return
	 * @throws DAOException
	 */
    public Collection<StatusManifestacao> getAllStatus() throws DAOException {
		String hql = "FROM StatusManifestacao WHERE id in " + 
				UFRNUtils.gerarStringIn(StatusManifestacao.getAllStatusManifestacao());
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<StatusManifestacao> status = q.list();
		
		return status;
    }

}
