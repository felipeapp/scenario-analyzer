package br.ufrn.sigaa.ava.forum.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.forum.dominio.GestorForumCurso;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO responsável por todas as consulta associadas aos gestores de fórum de curso.
 * @author Mário Rizzi
 *
 */
public class GestorForumCursoDao  extends GenericSigaaDAO {

	/**
	 * Verifica se o servidor ou docente externo é gestor de fórum.
	 * @param servidor
	 * @param docenteExterno
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public boolean isGestorForumCurso(Servidor servidor, DocenteExterno docenteExterno, Curso curso) 
			throws DAOException{
		
		Criteria c = getCriteria(GestorForumCurso.class);
		c.add( Expression.eq("curso.id", curso.getId() ) );
		if( !isEmpty(servidor) ){
			c.add( Expression.eq("servidor.id", servidor.getId()) );
		}else if ( !isEmpty(docenteExterno) ){
			c.add( Expression.eq("docenteExterno.id", docenteExterno.getId()) );
		}	
		
		return ( (Integer)c.setProjection( Projections.countDistinct("id") ).uniqueResult() ) > 0; 
		
	}
	
}
