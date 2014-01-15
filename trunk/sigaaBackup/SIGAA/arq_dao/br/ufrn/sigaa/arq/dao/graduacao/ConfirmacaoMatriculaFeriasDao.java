/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '24/114/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.ConfirmacaoMatriculaFerias;

/**
 * Dao para realizar consultas sobre a entidade ConfirmacaoMatriculaFerias
 * @author Victor Hugo
 */
public class ConfirmacaoMatriculaFeriasDao extends GenericSigaaDAO {

	/**
	 * Retorna uma ConfirmacaoMatriculaFerias do discente/turma informado
	 * Só pode ter uma confirmação por discente/turma 
	 * @param discente
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public ConfirmacaoMatriculaFerias findByDiscenteTurmaConfirmado(DiscenteAdapter discente, Turma turma, Integer ano, Integer periodo) throws DAOException{
		Criteria c = getSession().createCriteria(ConfirmacaoMatriculaFerias.class);
		c.add( Restrictions.eq( "discente.id" , discente.getId()) );
		if( turma != null )
			c.add( Restrictions.eq( "turma.id" , turma.getId()) );
		
		Criteria critTurma = c.createCriteria("turma");
		if( ano != null )
			critTurma.add( Restrictions.eq( "ano" , ano) );
		if( periodo != null )
			critTurma.add( Restrictions.eq( "periodo" , periodo) );
		
		
		c.add( Restrictions.eq( "confirmou" , true) );
		return (ConfirmacaoMatriculaFerias) c.uniqueResult();
	}
	
}
