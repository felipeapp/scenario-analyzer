/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/07/2011
 *
 */


package br.ufrn.sigaa.ensino.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Dao respons�vel por consultas espec�ficas a altera��o de turmas.
 * @author Igor Linnik
 */
public class AlteracaoTurmaDao extends GenericSigaaDAO {

	/** Construtor padr�o. */
	public AlteracaoTurmaDao() {
	}
	
	
	/**
	 * 
	 * Retorna a situa��o anterior mais recente da turma.
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Integer getSituacaoAnteriorMaisRecenteTurma(Turma turma) throws DAOException {
		StringBuilder sql = new StringBuilder();
		sql.append(" select id_situacao_antiga " +
				   " from ensino.alteracao_status_turma " +
				   " where id_turma = " + turma.getId() +
				   " order by data desc   ");		

		Query q = getSession().createSQLQuery(sql.toString());		
		q.setMaxResults(1);		
		Integer situacaoAnteriorMaisRecente = (Integer)q.uniqueResult();		
		return situacaoAnteriorMaisRecente;
	}
	
	
}