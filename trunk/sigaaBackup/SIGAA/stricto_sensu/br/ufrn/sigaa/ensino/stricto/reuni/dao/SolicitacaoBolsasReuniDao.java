/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 26/01/2009
 *
 */	
package br.ufrn.sigaa.ensino.stricto.reuni.dao;

import java.util.Collection;

import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.EditalBolsasReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.SolicitacaoBolsasReuni;

/**
 * DAO para consultas relacionadas a solicita��es de bolsas REUNI de assist�ncia ao ensino
 * 
 * @author wendell
 *
 */
public class SolicitacaoBolsasReuniDao extends GenericSigaaDAO {

	/**
	 * Busca uma solicita��o de bolsas por programa de p�s-gradua��o e edital de concess�o
	 * 
	 * @param programa
	 * @param edital
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public SolicitacaoBolsasReuni findByProgramaAndEdital(Unidade programa, EditalBolsasReuni edital) throws DAOException {
		return (SolicitacaoBolsasReuni) getSession()
			.createQuery("from SolicitacaoBolsasReuni where programa = :programa and edital = :edital")
			.setParameter("programa", programa)
			.setParameter("edital", edital)
			.uniqueResult();
	}
	
	/**
	 * Busca todas as solicita��es de bolsas por programa de p�s-gradua��o e edital de concess�o
	 * 
	 * @param programa
	 * @param edital
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public Collection<SolicitacaoBolsasReuni> findAllByProgramaAndEdital(Unidade programa, EditalBolsasReuni edital) throws DAOException {		
		@SuppressWarnings("unchecked")
		Collection<SolicitacaoBolsasReuni> list = getSession()
		.createQuery("from SolicitacaoBolsasReuni where programa = :programa and edital = :edital")
		.setParameter("programa", programa)
		.setParameter("edital", edital)
		.list();			 
		 return list;
	}	

	/**
	 * Retorna a quantidade de solicita��es j� cadastradas para um edital
	 * 
	 * @param obj
	 * @return
	 * @throws DAOException 
	 */
	public long countByEdital(EditalBolsasReuni edital) throws DAOException {
		return count("from stricto_sensu.solicitacao_bolsas_reuni where id_edital_bolsas_reuni = " + edital.getId());
	}

	/**
	 * Busca todas as solicita��es de bolsa cadastradas por um programa de p�s-gradua��o
	 * 
	 * @param programa
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoBolsasReuni> findByPrograma(Unidade programa) throws DAOException {
		@SuppressWarnings("unchecked")
		Collection<SolicitacaoBolsasReuni> list = getSession()		
		    .createCriteria(SolicitacaoBolsasReuni.class)
			.createAlias("edital", "edital")
			.add( Expression.eq("programa", programa))
			.addOrder( Order.asc("edital.descricao") )
			.list();
		return list;
	}

	/**
	 * Busca todas as solicita��es de bolsa cadastradas por um programa de p�s-gradua��o, tendo como base 
	 * o edital escolhido.
	 * 
	 * @param edital
	 * @return
	 * @throws DAOException
	 */

	public Collection<SolicitacaoBolsasReuni> findByEdital(EditalBolsasReuni edital) throws DAOException {
		@SuppressWarnings("unchecked")
		Collection<SolicitacaoBolsasReuni> list = getSession()
		    .createCriteria(SolicitacaoBolsasReuni.class)
			.createAlias("edital", "edital")
			.add( Expression.eq("edital", edital))
			.addOrder( Order.asc("edital.descricao") )
			.list();
		return list;
	}
}