/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 21/12/2006
 *
 */
package br.ufrn.sigaa.arq.dao.ensino.latosensu;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.latosensu.dominio.HistoricoSituacao;

/**
 * Consultas de Histórico de Situações de Proposta
 *
 * @author Leonardo
 *
 */
public class HistoricoSituacaoDao extends GenericSigaaDAO {

	public HistoricoSituacaoDao(){
		daoName = "historicoSituacaoDao";
	}

	public Collection<HistoricoSituacao> findByProposta(int idProposta) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(HistoricoSituacao.class);
			c.add( Expression.eq("proposta.id", idProposta));
			c.addOrder( Order.asc("dataCadastro"));

			return c.list();
		} catch( Exception e){
			throw new DAOException(e.getMessage(), e);
		}
	}

	public HistoricoSituacao findLastHistorico(int idProposta) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(HistoricoSituacao.class);
			c.add( Expression.eq("proposta.id", idProposta));
			c.addOrder( Order.desc("dataCadastro"));
			c.setMaxResults(1);
			
			return (HistoricoSituacao) c.uniqueResult();
		} catch( Exception e){
			throw new DAOException(e.getMessage(), e);
		}
	}

}