package br.ufrn.sigaa.ouvidoria.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaAssuntoManifestacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Dao para consultas em {@link CategoriaAssuntoManifestacao}.
 * 
 * @author Bernardo
 *
 */
public class CategoriaAssuntoManifestacaoDao extends GenericSigaaDAO {
    
	/**
	 * Retorna uma coleção contendo todas as categorias ativas cadastradas.
	 * 
	 * @return Collection<CategoriaAssuntoManifestacao>
	 * @throws DAOException
	 */
    public Collection<CategoriaAssuntoManifestacao> getAllCategoriasAtivas() throws DAOException {
    	return findAllAtivos(CategoriaAssuntoManifestacao.class, "descricao");
    }
    
    /**
     * Retorna todas as categorias de assunto cadastradas.
     * 
     * @return Collection<CategoriaAssuntoManifestacao>
     * @throws DAOException
     */
    public Collection<CategoriaAssuntoManifestacao> getAllCategorias() throws DAOException {
    	return findAll(CategoriaAssuntoManifestacao.class, "descricao","asc");
    }
    
    /**
     * Retorna as categorias que jah tiveram manifestações cadastradas para a unidade passada.
     * 
     * @param unidade
     * @return Collection<CategoriaAssuntoManifestacao>
     * @throws DAOException
     */
    public Collection<CategoriaAssuntoManifestacao> getAllCategoriasByUnidade(Unidade unidade) throws DAOException {
    	String hql = "SELECT DISTINCT c " +
    					"FROM HistoricoManifestacao h " +
    					"INNER JOIN h.manifestacao m " +
    					"INNER JOIN m.assuntoManifestacao a " +
    					"INNER JOIN a.categoriaAssuntoManifestacao c " +
					"WHERE h.unidadeResponsavel.id = :unidade ";
    	
    	Query q = getSession().createQuery(hql);
    	
    	q.setParameter("unidade", unidade.getId());
    	
    	@SuppressWarnings("unchecked")
		List<CategoriaAssuntoManifestacao> list = q.list();
    	
		return list;
    }
    
    /**
     * Retorna as categorias que jah tiveram manifestações cadastradas para a unidade passada.
     * 
     * @param unidade
     * @return Collection<CategoriaAssuntoManifestacao>
     * @throws DAOException
     */
    public Collection<CategoriaAssuntoManifestacao> getAllCategoriasByDesignado(Pessoa pessoa) throws DAOException {
    	String hql = "SELECT DISTINCT c " +
    					"FROM DelegacaoUsuarioResposta d " +
    					"INNER JOIN d.pessoa p " +
    					"INNER JOIN d.historicoManifestacao h " +
    					"INNER JOIN h.manifestacao m " +
    					"INNER JOIN m.assuntoManifestacao a " +
    					"INNER JOIN a.categoriaAssuntoManifestacao c " +
					"WHERE p.id = :pessoa ";
    	
    	Query q = getSession().createQuery(hql);
    	
    	q.setParameter("pessoa", pessoa.getId());
    	
    	@SuppressWarnings("unchecked")
		List<CategoriaAssuntoManifestacao> list = q.list();
    	
		return list;
    }

}
