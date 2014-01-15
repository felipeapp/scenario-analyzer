/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '08/02/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.TurmaSolicitacaoTurma;

/**
 * Dao com as consultas de Reservas
 * @author Leonardo
 * @author Victor Hugo
 */
public class ReservaCursoDao extends GenericSigaaDAO {

	public Collection<ReservaCurso> findBySolicitacao(int idSolicitacao) throws DAOException{
			Criteria c = getSession().createCriteria(ReservaCurso.class);
			c.add( Expression.eq("solicitacao.id", idSolicitacao) );
			return c.list();
	}

	/**
	 * retorna uma TurmaSolicitacaoTurma a partir de uma turma e uma solicitação
	 * @param idTurma
	 * @param idSolicitacao
	 * @return
	 * @throws DAOException
	 */
	public TurmaSolicitacaoTurma findByTurmaSolicitacao( int idTurma, int idSolicitacao) throws DAOException{
		Criteria c = getSession().createCriteria(TurmaSolicitacaoTurma.class);
		c.add( Expression.eq("solicitacao.id", idSolicitacao) );
		c.add( Expression.eq("turma.id", idTurma) );
		return (TurmaSolicitacaoTurma) c.uniqueResult();
	}

	/**
	 * retorna a ReservaCurso de uma matriz, componente, ano, período informado
	 * @param matriz
	 * @param componente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public ReservaCurso findByMatrizTurma( MatrizCurricular matriz, Turma turma ) throws DAOException{
		Criteria c = getSession().createCriteria( ReservaCurso.class );
		c.add( Restrictions.eq("matrizCurricular", matriz) );
		c.add( Restrictions.eq("turma.id", turma.getId()) );
		return (ReservaCurso) c.uniqueResult();
	}
	
	/**
	 * retorna a coleção de reservas de curso, conforme os indicadores de turma passados por parâmetro.
	 * @param idSolicitacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<ReservaCurso> findByTurmas(List<Integer> idTurmas) throws DAOException{
		Criteria c = getSession().createCriteria(ReservaCurso.class);
		c.add( Expression.in("turma.id", idTurmas) );
		return c.list();
}


}
