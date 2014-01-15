/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/06/2009
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;

/**
 *
 * Dao para as coleções do sistema.
 *
 * @author jadson
 * @since 05/06/2009
 * @version 1.0 criacao da classe
 *
 */
public class ColecaoDao extends GenericSigaaDAO{
	
	
	/**
	 *    Encontra as coleções ativas que podem ser usadas para registrar o consulta de materias nas estantes.
	 * 
	 * @param biblioteca
	 * @param tipoUsuario
	 * @return
	 * @throws DAOException
	 */
	public List<Colecao> findColecoesAtivasParaRegistroConsulta() throws DAOException{
		
		Criteria c = getSession().createCriteria( Colecao.class );
		c.add( Restrictions.eq( "contagemMovimentacao" , true ) );
		c.add( Restrictions.eq( "ativo" , true ) );
		c.addOrder( Order.asc("descricao") );

		@SuppressWarnings("unchecked")
		List<Colecao> lista = c.list();
		return lista;
	}
	

	/**
	 * Busca a descrição de coleções ativas a partir de seus id's.
	 * 
	 * @param idColecaoList Lista de id's de coleções
	 * @return Lista das descrições das coleções
	 * @throws DAOException
	 */
	public List<String> findDescricaoColecoesAtivas(Collection<Integer> idColecaoList) throws DAOException {
		Criteria c = getSession().createCriteria(Colecao.class);
		
		c.add(Restrictions.eq("ativo", true));
		
		if (idColecaoList != null && idColecaoList.size() > 0) {
			c.add(Restrictions.in("id", idColecaoList));
		}
		
		c.setProjection(Projections.property("descricao"));
		
		c.addOrder(Order.asc("id"));
		
		@SuppressWarnings("unchecked")
		List<String> lista = c.list();
		
		return lista;
	}
	
}
