/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/04/2007
 *
 */
/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/04/2007
 *
 */

package br.ufrn.sigaa.arq.dao;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoArtistico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;




/**
 * @author Edson Anibal (ambar@info.ufrn.br)
**/
public class TipoArtisticoDao extends GenericSigaaDAO
{
	public TipoArtisticoDao()
	{
		daoName ="TipoArtisticoDao";
	}

	/**
	 * Retorna o TipoArtistico de uma determinada Produção Intelectual.
	 * @param tipoartistico
	 * @return
	 * @throws DAOException
	 */
	public Collection<TipoArtistico> findTipoArtisticoByProducao(TipoProducao producao) throws DAOException
	{

		try
		{
			Criteria c = getCriteria(TipoArtistico.class);
			c.add(Expression.eq("tipoProducao", producao));
			c.addOrder(Order.asc("descricao"));

			return c.list();

		} catch (Exception e) { throw new DAOException(e);}
	}

}
