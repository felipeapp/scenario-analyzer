/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 14/07/2008
 *
 */
package br.ufrn.sigaa.arq.dao.ensino.stricto;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dominio.ParametrosProgramaPos;

/**
 * Dao para realizar consultas sobre a entidade ParametrosProgramaPos
 * @author Victor Hugo
 */
public class ParametrosProgramaPosDao extends GenericSigaaDAO {

	/**
	 * retorna os parâmetros de um programa especificado
	 * @param programa
	 * @return
	 * @throws DAOException
	 */
	public ParametrosProgramaPos findByPrograma( Unidade programa ) throws DAOException{

		Criteria c = getSession().createCriteria( ParametrosProgramaPos.class );
		c.add( Restrictions.eq("programa", programa) );

		return (ParametrosProgramaPos) c.uniqueResult();

	}

}
