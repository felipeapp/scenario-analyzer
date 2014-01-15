/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 11/07/2008
 *
 */
package br.ufrn.sigaa.arq.dao.ensino.stricto;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.stricto.dominio.RenovacaoAtividadePos;

/**
 * DAO Responsavel pelas consultas referente a renovação de atividade de Pos-Graduação
 *
 * @author Victor Hugo
 */
public class RenovacaoAtividadePosDao extends GenericSigaaDAO {

	/**
	 * Buscar renovação para um determinada solicitação de matrícula
	 * 
	 * @param idSolicitacao
	 * @param ativo
	 * @return
	 * @throws DAOException
	 */
	public RenovacaoAtividadePos findBySolicitacaoMatriculaStatus( int idSolicitacao, Boolean ativo ) throws DAOException{
		Criteria c = getSession().createCriteria(RenovacaoAtividadePos.class);
		c.add( Restrictions.eq("solicitacaoMatricula.id", idSolicitacao) );
		if( ativo == null ){

		} else
			c.add( Restrictions.eq("ativo", ativo) );

		return (RenovacaoAtividadePos) c.uniqueResult();
	}

	/**
	 * Conta a quantidade de renovações total de rematriculas 
	 * de um determinado discente e uma determinada atividade. 
	 * 
	 * @param discente
	 * @param atividade
	 * @return
	 */
	public long countTotalRenovacoes(DiscenteAdapter discente, ComponenteCurricular atividade) {
		return (Long) getHibernateTemplate().uniqueResult("select count(*) from RenovacaoAtividadePos " +
				" where  ativo = trueValue() " +
				" and matricula.discente.id = ? and matricula.componente.id = ?" ,
				new Object[] {discente.getId(), atividade.getId()});
	}

	/**
	 * Buscar renovação de uma matrícula em um determinado ano/período
	 * 
	 * @param matricula
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public RenovacaoAtividadePos findByMatricula(MatriculaComponente matricula, int ano, int periodo) throws DAOException {
		Criteria c = getSession().createCriteria(RenovacaoAtividadePos.class);
		c.add( Restrictions.eq("matricula.id", matricula.getId()) );
		c.add( Restrictions.eq("ano", ano) );
		c.add( Restrictions.eq("periodo", periodo) );
		c.add( Restrictions.eq("ativo", true) );
		return (RenovacaoAtividadePos) c.uniqueResult();	
	}

}
