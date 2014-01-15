/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '18/12/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscentesSolicitacao;

/** 
 * Dao para realizar consultas sobre a entidade DiscentesSolicitacao
 * @author Victor Hugo
 */
public class DiscentesSolicitacaoDao extends GenericSigaaDAO {

	/**
	 * retorna uma DiscenteSolicitacao com o discente e solicita��o informado
	 * @param idDiscente
	 * @param idSolicitacao
	 * @return
	 * @throws DAOException
	 */
	public DiscentesSolicitacao findByDiscenteSolicitacao(int idDiscente, int idSolicitacao) throws DAOException {
		Criteria c = getSession().createCriteria(DiscentesSolicitacao.class);
		c.add( Restrictions.eq( "discenteGraduacao.id", idDiscente) );
		c.add( Restrictions.eq( "solicitacaoTurma.id", idSolicitacao) );
		return (DiscentesSolicitacao) c.uniqueResult();
	}
	
}
