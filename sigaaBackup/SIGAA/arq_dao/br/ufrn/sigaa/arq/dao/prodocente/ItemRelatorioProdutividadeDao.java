/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '28/03/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ItemRelatorioProdutividade;


/**
 * DAO que realiza consultas sobre a entidade ItemRelatorioProdutividade
 * @author Victor Hugo
 *
 */
public class ItemRelatorioProdutividadeDao extends GenericSigaaDAO {

	/**
	 * retorna o item dado o número topico informado
	 * @param topico
	 * @return
	 * @throws DAOException
	 */
	public ItemRelatorioProdutividade findByTopico(int topico) throws DAOException{

		Criteria c;
		try {
			c = getSession().createCriteria(ItemRelatorioProdutividade.class);
			c.add(Expression.eq("numeroTopico",topico));
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
		return (ItemRelatorioProdutividade) c.uniqueResult();
	}

	/**
	 * retorna os itens do relatório de produtividade relacionados ao relatório de produtividade informado
	 * @param idRelatorio
	 * @param producao
	 * @return
	 * @throws DAOException
	 */
	public List<ItemRelatorioProdutividade> findByRelatorioProdutividade( int idRelatorio, Boolean producao ) throws DAOException{
		
		StringBuilder hql = new StringBuilder(" SELECT item.id, item.producaoMapper, item.producaoIntelectual FROM GrupoItem grupo ");
		hql.append( " JOIN grupo.grupoRelatorioProdutividade gr JOIN gr.relatorioProdutividade rel JOIN grupo.itemRelatorioProdutividade item " );
		
		hql.append( " WHERE rel.id = :idRel " );
		if( producao != null )
			hql.append( " AND item.producaoIntelectual = :tipo " );
		
		Query q = getSession().createQuery( hql.toString() );
		q.setInteger("idRel", idRelatorio);
		if( producao != null )
		q.setBoolean("tipo", producao);
		
		List<Object[]> resultado = q.list();
		List<ItemRelatorioProdutividade> itens = new ArrayList<ItemRelatorioProdutividade>();
		
		for (Object[] linha : resultado) {
			int col = 0;
			ItemRelatorioProdutividade item = new ItemRelatorioProdutividade();
			
			item.setId( (Integer) linha[col++] );
			item.setProducaoMapper( (String) linha[col++] );
			item.setProducaoIntelectual( (Boolean) linha[col++] );
			
			itens.add(item);
		}
		
		return itens;
	}
}
