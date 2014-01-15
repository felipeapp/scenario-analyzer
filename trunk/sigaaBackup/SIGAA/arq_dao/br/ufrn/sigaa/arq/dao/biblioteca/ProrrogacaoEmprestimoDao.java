/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/07/2009
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;

/**
 *
 * DAO para as consultadas de prorrogações dos empréstimos
 *
 * @author jadson
 * @since 10/07/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProrrogacaoEmprestimoDao  extends GenericSigaaDAO{
	
	
	
	/**
	 * 
	 *    Retorna os id das prorrogações por renovação do empréstimo passado.
	 *
	 * Chamado a partir da página: /biblioteca/
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	
	public Date findUltimaDataRenovacao(int idEmprestimo) throws DAOException{
	
		String hql = " SELECT DISTINCT p.dataCadastro  FROM ProrrogacaoEmprestimo p  " +
					 " WHERE p.emprestimo.id = :idEmprestimo AND p.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO
					 + " ORDER BY p.dataCadastro DESC ";
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idEmprestimo", idEmprestimo);                      
		q.setMaxResults(1);
		
		return (Date) q.uniqueResult();
		
	}
	
	
	/**
	 * 
	 *    Conta as renovações que um empréstimo teve
	 *
	 * Chamado a partir da página: /biblioteca/
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public int countProrrogacoesPorRenovacaoDoEmprestimo(Emprestimo e) throws DAOException{
	
		String hql = " SELECT count(DISTINCT p.id)  FROM ProrrogacaoEmprestimo p  " +
					 " WHERE p.emprestimo.id = :idEmprestimo AND p.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO; 
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idEmprestimo", e.getId());                      
		
		return  ((Long) q.uniqueResult()).intValue();
	}
	
	
	/**
	 * 
	 *   Retorna uma lista de datas em que o empréstimo foi renovado
	 *
	 * Chamado a partir da página: /biblioteca/
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public List<Date> getDatasRenovacoesEmprestimo(Emprestimo e) throws DAOException{
	
		StringBuilder hql = new StringBuilder(" SELECT p.dataCadastro  FROM ProrrogacaoEmprestimo p  ");
		hql.append(" WHERE p.emprestimo.id = :idEmprestimo AND p.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO); 
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idEmprestimo", e.getId());       
		
		@SuppressWarnings("unchecked")
		List<Date> lista = q.list();
		return lista;
	}
	
	/**
	 * <p>Retorna todas as prorrogações que sejam do tipo passado e do empréstimo passado.</p>
	 * 
	 * <p><i>Ordenadas pela data anterior do empréstimo em ordem descendente, ou seja, retorna primeiro as renovações mais recentes. </i></p>
	 * 
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public List <ProrrogacaoEmprestimo> findProrrogacoesByEmprestimoTipo(Emprestimo e, int tipo) throws DAOException{
		
		Criteria c = getSession().createCriteria(ProrrogacaoEmprestimo.class);
		c.add(Restrictions.eq("emprestimo.id", e.getId()));
		c.add(Restrictions.eq("tipo", tipo));
		c.addOrder(Order.desc("dataAnterior")); // IMPORTANTE: Não mudar essa ordenação
		
		@SuppressWarnings("unchecked")
		List<ProrrogacaoEmprestimo> lista = c.list();
		return lista;
	}
	
	
	
	/**
	 * Retorna todas as prorrogações feita por perda de material para os empréstimos passados
	 * 
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	
	public List <ProrrogacaoEmprestimo> findProrrogacoesPorPerdaDeMaterialDosEmprestimos(List<Integer> idsEmprestimos) throws DAOException{
		
		String hql = " SELECT p.id, p.emprestimo.id, p.dataAnterior, p.dataAtual, p.motivo, p.registroCadastro.usuario.pessoa.nome, p.dataCadastro FROM ProrrogacaoEmprestimo p  ";
		hql += " WHERE p.tipo = "+TipoProrrogacaoEmprestimo.PERDA_DE_MATERIAL;
		hql += " AND p.emprestimo.id in (:idsEmprestimo) ";
		hql += " ORDER BY p.emprestimo.id ";
		
		Query q = getSession().createQuery(hql);
		q.setParameterList("idsEmprestimo", idsEmprestimos);

		
		List<ProrrogacaoEmprestimo> lista = new ArrayList<ProrrogacaoEmprestimo>();
		
		@SuppressWarnings("unchecked")
		List<Object> objetos =  q.list();
		
		for (Object objeto : objetos) {
			Object[] array = ( Object[]) objeto;
		
			lista.add( new ProrrogacaoEmprestimo((Integer) array[0],  new Emprestimo( (Integer) array[1] ), (Date) array[2], (Date) array[3], (String) array[4], (String) array[5], (Date) array[6] ) );
			
		} 
		
		return lista;
	}
	
}
