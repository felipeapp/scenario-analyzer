/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 12/11/2010
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.LevantamentoInfraEstrutura;

/**
 * DAO que realiza consultas referentes aos levantamentos de infra-estrutura.
 *
 * @author Bráulio
 */
public class LevantamentoInfraDao extends GenericSigaaDAO {

	/**
	 * Retorna todos levantamentos de infra-estrutura solicitados pela pessoa
	 * passada como parâmetro. A lista é ordenada por data de forma decrescente.
	 */
	public List<LevantamentoInfraEstrutura> findLevantamentosDoUsuario( int idPessoa )
			throws DAOException {
		
		Criteria c = getSession().createCriteria(LevantamentoInfraEstrutura.class);
		c.add( Restrictions.eq("solicitante.id", idPessoa) );
		c.addOrder(Order.desc("dataSolicitacao"));
		
		@SuppressWarnings("unchecked")
		List<LevantamentoInfraEstrutura> lista = c.list();
		
		return lista;
		
	}
	
	/**
	 * Retorna uma lista de levantamentos bibliográficos, de acordo com os filtros
	 * passados. A lista é ordenada por data de forma decrescente.
	 */
	public List<LevantamentoInfraEstrutura> findLevantamentos(
			Collection<Integer> bibliotecas, int situacao ) throws DAOException {
		
		Criteria c = getSession().createCriteria(LevantamentoInfraEstrutura.class);
		
		c.add( Restrictions.in("biblioteca.id", bibliotecas.toArray()) );
		if ( situacao > 0 )
			c.add( Restrictions.eq("situacao", situacao) );
		
		c.addOrder(Order.desc("dataSolicitacao"));
		
		@SuppressWarnings("unchecked")
		List<LevantamentoInfraEstrutura> lista = c.list();
		
		return lista;
	}
	
}
