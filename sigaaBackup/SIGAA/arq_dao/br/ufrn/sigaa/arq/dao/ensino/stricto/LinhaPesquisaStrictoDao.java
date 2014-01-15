/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 17/072008
 *
 */
package br.ufrn.sigaa.arq.dao.ensino.stricto;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinFragment;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dominio.LinhaPesquisaStricto;

/**
 *
 * @author Victor Hugo
 */
public class LinhaPesquisaStrictoDao extends GenericSigaaDAO {

	/**
	 * retorna todas as LinhaPesquisaStricto que são de um programa e que NÃO ESTÃO RELACIONADAS A NENHUMA ÁREA
	 * @param area
	 * @return
	 * @throws DAOException
	 */
	public Collection<LinhaPesquisaStricto> findByProgramaSemArea(Unidade programa) 
	throws DAOException{
		Criteria c = getSession().createCriteria(LinhaPesquisaStricto.class);
		c.add( Restrictions.eq("programa", programa) );
		c.add( Restrictions.isNull("area") );
		return c.list();
	}
	
	/**
	 * retorna todas as LinhaPesquisaStricto que são de um programa e que NÃO ESTÃO RELACIONADAS A NENHUMA ÁREA
	 * @param area
	 * @return
	 * @throws DAOException
	 */
	public Collection<LinhaPesquisaStricto> findByCurso(Curso curso) 
	throws DAOException{
		Criteria c = getSession().createCriteria(LinhaPesquisaStricto.class);
		c.add( Restrictions.eq("programa.id", curso.getUnidade().getId()) );
		c.createCriteria("area","a",JoinFragment.LEFT_OUTER_JOIN);
		c.add( Restrictions.or(Restrictions.isNull("a.id"), Restrictions.eq("a.nivel",curso.getNivel()) ) );
		return c.list();
	}
}
