/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '20/04/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pesquisa.dominio.LinhaPesquisa;

/**
 * @author ricardo
 *
 */
public class LinhaPesquisaDao extends GenericSigaaDAO {

	public LinhaPesquisaDao() {
		daoName = "LinhaPesquisaDao";
	}

	@SuppressWarnings("unchecked")
	public Collection<LinhaPesquisa> findByNomeProjeto(String nomeProjeto) throws DAOException {
		try {
			return getSession().createCriteria(LinhaPesquisa.class)
				.createCriteria("projetosPesquisa")
					.add( Restrictions.like("projeto.titulo", nomeProjeto + "%") )
				.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Buscar linhas de pesquisa ativas cujo nome contem o trecho informado
	 *
	 * @param trecho
	 * @param idGrupo
	 * @return
	 * @throws DAOException
	 */
//	@SuppressWarnings("unchecked")
	public Collection<LinhaPesquisa> findByTrecho(String trecho, Integer idGrupo) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(LinhaPesquisa.class);
			if (trecho != null)
				c.add( Restrictions.ilike("nome", "%" + trecho + "%") );
			c.add( Restrictions.eq("inativa", false) );
			if (idGrupo != null && idGrupo > 0) {
				c.add( Restrictions.eq("grupoPesquisa.id", idGrupo ) );
			} else {
				c.add( Restrictions.isNull("grupoPesquisa") );
			}

			c.addOrder( Order.asc("nome") );

			return c.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

}
