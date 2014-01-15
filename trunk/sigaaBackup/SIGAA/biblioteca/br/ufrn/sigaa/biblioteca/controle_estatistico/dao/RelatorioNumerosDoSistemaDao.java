/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 08/09/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;

/**
 *
 * <p>Dao exclusivo para o relatório que conta os números gerais do sistema de biblioteca.  </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public class RelatorioNumerosDoSistemaDao extends GenericSigaaDAO{

	
	/**
	 * <p>Conta o Total de Títulos Ativos do sistema </p>  
	 * 
	 * @throws DAOException 
	 */
	public Long countTotalTitulos() throws DAOException {

		String hql = new String("  SELECT  COUNT(DISTINCT t.id) FROM TituloCatalografico t WHERE t.ativo = :true AND catalogado = :true ");
		Query query = getSession().createQuery(hql);
		query.setBoolean("true", true);
		return (Long) query.uniqueResult();	
	}
	
	
	/**
	 * <p>Conta o Total de Títulos Ativos do sistema </p>  
	 * 
	 * @throws DAOException 
	 */
	public Long countTotalAutoridades() throws DAOException {

		String hql = new String("  SELECT  COUNT(DISTINCT a.id) FROM Autoridade a WHERE a.ativo = :true AND catalogada = :true ");
		Query query = getSession().createQuery(hql);
		query.setBoolean("true", true);
		return  (Long) query.uniqueResult();	
	}
	
	/**
	 * <p>Conta o Total de Títulos Ativos do sistema </p>  
	 * 
	 * @throws DAOException 
	 */
	public Long countTotalArtigos() throws DAOException {

		String hql = new String("  SELECT  COUNT(DISTINCT a.id) FROM ArtigoDePeriodico a WHERE a.ativo = :true ");
		Query query = getSession().createQuery(hql);
		query.setBoolean("true", true);
		return  (Long) query.uniqueResult();	
	}
	
	
	/**
	 * <p>Conta o Total de Títulos Ativos do sistema </p>  
	 * 
	 * @throws DAOException 
	 */
	public Long countTotalExemplares() throws DAOException {

		String hql = new String("  SELECT  COUNT(DISTINCT e.id) FROM Exemplar e " +
								" INNER JOIN e.situacao s "+
								" WHERE e.ativo = :true AND s.situacaoDeBaixa = :false ");
		Query query = getSession().createQuery(hql);
		query.setBoolean("true", true);
		query.setBoolean("false", false);
		return  (Long) query.uniqueResult();	
	}
	
	/**
	 * <p>Conta o Total de Títulos Ativos do sistema </p>  
	 * 
	 * @throws DAOException 
	 */
	public Long countTotalFasciculos() throws DAOException {

		String hql = new String(" SELECT  COUNT(DISTINCT f.id) FROM Fasciculo f " +
								" INNER JOIN f.situacao s "+
								" WHERE f.ativo = :true AND s.situacaoDeBaixa = :false ");
		Query query = getSession().createQuery(hql);
		query.setBoolean("true", true);
		query.setBoolean("false", false);
		return  (Long) query.uniqueResult();	
	}
	
	/**
	 * <p>Conta o Total de Títulos Ativos do sistema </p>  
	 * 
	 * @throws DAOException 
	 */
	public Long countTotalAssinaturas() throws DAOException {

		String hql = new String("  SELECT  COUNT(DISTINCT a.id) FROM Assinatura a WHERE a.ativa = :true ");
		Query query = getSession().createQuery(hql);
		query.setBoolean("true", true);
		return  (Long) query.uniqueResult();
	}
	
	
	/**
	 * <p>Conta o Total de Títulos Ativos do sistema </p>  
	 * 
	 * @throws DAOException 
	 */
	public Long countTotalEmprestimos() throws DAOException {
		
		String hql1 = new String(" SELECT  COUNT(DISTINCT e.id)  AS quantidade FROM Emprestimo e WHERE e.ativo = :true ");
		
		String hql2 = new String(" SELECT  COUNT(DISTINCT pro.id) AS quantidade  FROM ProrrogacaoEmprestimo pro " +
								" INNER JOIN pro.emprestimo e "+
								" WHERE e.ativo = :true AND pro.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO+" ");
		
		Query query1 = getSession().createQuery(hql1);
		query1.setBoolean("true", true);
		long qtdEmpretimos =  (Long) query1.uniqueResult();
		
		
		Query query2 = getSession().createQuery(hql2);
		query2.setBoolean("true", true);
		long qtdRenovacoes =  (Long) query2.uniqueResult();
		
		
		return qtdEmpretimos + qtdRenovacoes;	
	
	}
	
	
	/**
	 * <p>Conta o Total de Títulos Ativos do sistema </p>  
	 * 
	 * @throws DAOException 
	 */
	public Long countTotalUsuarios() throws DAOException {

		String hql = new String("  SELECT  COUNT(DISTINCT u.id) FROM UsuarioBiblioteca u WHERE u.ativo = :true AND u.quitado = :false ");
		Query query = getSession().createQuery(hql);
		query.setBoolean("true", true);
		query.setBoolean("false", false);
		return  (Long) query.uniqueResult();
	}
	
}
