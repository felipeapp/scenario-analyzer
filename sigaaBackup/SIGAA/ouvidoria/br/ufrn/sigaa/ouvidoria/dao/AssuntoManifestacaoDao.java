package br.ufrn.sigaa.ouvidoria.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ouvidoria.dominio.AssuntoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaAssuntoManifestacao;

/**
 * Dao responsável por consultas feitas em {@link AssuntoManifestacao}.
 * 
 * @author Bernardo
 *
 */
public class AssuntoManifestacaoDao extends GenericSigaaDAO {

	/**
	 * Retorna uma coleção contendo todos os assuntos ativos cadastrados.
	 * 
	 * @return
	 * @throws DAOException
	 */
    public Collection<AssuntoManifestacao> getAllAssuntosAtivos() throws DAOException {
    	return findAllAtivos(AssuntoManifestacao.class, "descricao");
    }
    
    /**
     * Retorna uma coleção contendo todos os assuntos cadastrados.
     * 
     * @return
     * @throws DAOException
     */
    public Collection<AssuntoManifestacao> getAllAssuntos() throws DAOException {
    	String campos[] = {"categoriaAssuntoManifestacao", "descricao"};
    	String ordem[]  = {"asc","asc"};
    	return findAll(AssuntoManifestacao.class, campos, ordem );
     }
    
    /**
     * Busca os assuntos cadastrados para a categoria informada.
     * 
     * @param categoria
     * @return
     * @throws DAOException
     */
    public List<AssuntoManifestacao> getAllAssuntosAtivosByCategoria(CategoriaAssuntoManifestacao categoria) throws DAOException {
		String hql = "from AssuntoManifestacao where categoriaAssuntoManifestacao.id = ? and ativo = ?";
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger(0, categoria.getId());
		q.setBoolean(1, true);
		
		@SuppressWarnings("unchecked")
		List<AssuntoManifestacao> assuntos = q.list();
		
		return assuntos;
    }

}
