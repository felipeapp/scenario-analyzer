package br.ufrn.sigaa.ouvidoria.dao;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ouvidoria.dominio.TipoManifestacao;

/**
 * Dao responsável pelas consultas em {@link TipoManifestacao}.
 * 
 * @author Bernardo
 *
 */
public class TipoManifestacaoDao extends GenericSigaaDAO {
    
	/**
	 * Retorna todos os tipos cadastrados.
	 * 
	 * @return
	 * @throws DAOException
	 */
    public Collection<TipoManifestacao> getAllTipos() throws DAOException {
    	return findAll(TipoManifestacao.class, "id", "asc");
    }

}
