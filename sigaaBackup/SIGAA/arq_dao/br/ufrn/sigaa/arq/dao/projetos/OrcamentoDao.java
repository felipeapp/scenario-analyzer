/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '11/12/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.OrcamentoConsolidado;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;

/**
 * Responsável por realizar operações de busca relacionadas a orçamento.
 * 
 * @author ilueny
 *
 */
public class OrcamentoDao extends GenericSigaaDAO {

	public OrcamentoDao() {
	}	
	
	
	/**
	 * Retorna todos os elementos do tipo informado do projeto 
	 * 
	 * @param idAtividade
	 * @param idElemento
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<OrcamentoDetalhado> findByProjetoElementoDespesa(int idProjeto, int idElemento) throws DAOException{
		
		try {
    		String hql ="select od.id as id, od.discriminacao as discriminacao from OrcamentoDetalhado od where od.projeto.id = :idProjeto and od.elementoDespesa.id = :idElemento";
    		Query query = getSession().createQuery(hql);
    		query.setInteger("idProjeto", idProjeto);
    		query.setInteger("idElemento", idElemento);    		
    		
    		return query.setResultTransformer(new AliasToBeanResultTransformer(OrcamentoDetalhado.class)).list();
    		
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	
	/**
	 * Retorna todos os Orçamentos consolidados do projeto informado
	 * 
	 * @param idAtividade
	 * @param idElemento
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<OrcamentoConsolidado> findOrcamentosConsolidadosByProjeto(int idProjeto) throws DAOException{
		
		try {
			String projecao = "oc.id, oc.elementoDespesa.id, oc.projeto.id";
    		String hql ="select " + projecao + " from OrcamentoConsolidado oc where oc.projeto.id = :idProjeto";
    		Query query = getSession().createQuery(hql);
    		query.setInteger("idProjeto", idProjeto);
    		
    		return	HibernateUtils.parseTo(query.list(), projecao, OrcamentoConsolidado.class, "oc");
    		
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	
}