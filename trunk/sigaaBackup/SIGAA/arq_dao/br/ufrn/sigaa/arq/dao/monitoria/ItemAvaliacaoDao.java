/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '11/10/2006'
 *
 */
package br.ufrn.sigaa.arq.dao.monitoria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.monitoria.dominio.GrupoItemAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.ItemAvaliacaoMonitoria;

/**
 * Classe utilizada para realizar as consultas sobre os ítens de uma avaliação.
 * 
 * @author UFRN
 *
 */
public class ItemAvaliacaoDao extends GenericSigaaDAO {

	/**
	 * Método usado para retornar os ítens de avaliação de um grupo.
	 * 
	 * @param grupo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ItemAvaliacaoMonitoria> findByGrupo(
			GrupoItemAvaliacao grupo) throws DAOException {
		Criteria c = getSession().createCriteria(ItemAvaliacaoMonitoria.class);
		if (grupo.getId() != 0) {
			c.add(Expression.eq("grupo", grupo));
		}
		return c.list();
	}
	
	/**
	 * Retorna os ítens de ação de extensão.  
	 * 
	 * @param grupo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ItemAvaliacaoMonitoria> findByGrupoExtensao(
			GrupoItemAvaliacao grupo) throws DAOException {
		
		
		String hql = " select item.id, item.descricao, item.notaMaxima, item.peso, grupo.denominacao, grupo.id " +
				     " from ItemAvaliacaoMonitoria item " +
					 " inner join item.grupo grupo " +
					 " where item.ativo = trueValue() and grupo.ativo = trueValue() and grupo.tipo = 'E'";
		
		if(grupo.getId() !=0 )
			hql = hql + " and grupo.id = :idGrupo ";
		
		Query q = getSession().createQuery(hql);
		
		if(grupo.getId() !=0 )
		q.setInteger("idGrupo", grupo.getId());
		
		List<Object> lista = q.list();

		Collection<ItemAvaliacaoMonitoria> result = new ArrayList<ItemAvaliacaoMonitoria>();
		
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			ItemAvaliacaoMonitoria itemAvaliacao = new ItemAvaliacaoMonitoria();
			itemAvaliacao.setId((Integer) colunas[col++]);
			itemAvaliacao.setDescricao((String) colunas[col++]);
			itemAvaliacao.setNotaMaxima((Double) colunas[col++]);
			itemAvaliacao.setPeso((Double) colunas[col++]);
			itemAvaliacao.getGrupo().setDenominacao((String) colunas[col++]);
			itemAvaliacao.getGrupo().setId((Integer) colunas[col++]);

			result.add(itemAvaliacao);
		}
		return result;
		
		
	}
	
	
	/**
	 * Retorna os ítens de projetos/relatórios de monitoria
	 * 
	 * @param grupo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ItemAvaliacaoMonitoria> findByGrupoMonitoria(
			GrupoItemAvaliacao grupo) throws DAOException {
		
		
		String hql = " select item.id, item.descricao, item.notaMaxima, item.peso, grupo.denominacao, grupo.id " +
				     " from ItemAvaliacaoMonitoria item " +
					 " inner join item.grupo grupo " +
					 " where item.ativo = trueValue() and grupo.ativo = trueValue() and (grupo.tipo = 'P' or grupo.tipo = 'R') ";
		
		if(grupo.getId() !=0 )
			hql = hql + " and grupo.id = :idGrupo ";
		
		Query q = getSession().createQuery(hql);
		
		if(grupo.getId() !=0 )
		q.setInteger("idGrupo", grupo.getId());
		
		List<Object> lista = q.list();

		Collection<ItemAvaliacaoMonitoria> result = new ArrayList<ItemAvaliacaoMonitoria>();
		
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			ItemAvaliacaoMonitoria itemAvaliacao = new ItemAvaliacaoMonitoria();
			itemAvaliacao.setId((Integer) colunas[col++]);
			itemAvaliacao.setDescricao((String) colunas[col++]);
			itemAvaliacao.setNotaMaxima((Double) colunas[col++]);
			itemAvaliacao.setPeso((Double) colunas[col++]);
			itemAvaliacao.getGrupo().setDenominacao((String) colunas[col++]);
			itemAvaliacao.getGrupo().setId((Integer) colunas[col++]);

			result.add(itemAvaliacao);
		}
		return result;
		
		
	}
	
	/**
	 * Retorna a nota total de todos os ítens ativos, que são a soma de todas as notas máximas dos ítens. 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public double findNotaTotalAtivos() throws DAOException {
		Query q = getSession().createQuery("select sum(item.notaMaxima) " +
				"from ItemAvaliacaoMonitoria item " +
				"inner join item.grupo grupo " +
				"where item.ativo = trueValue() and grupo.ativo = trueValue() and grupo.tipo = 'PEM'");
		Number result = (Number) q.uniqueResult();
		if (result != null)
			return (result).doubleValue();
		return 0;
	}	
}
