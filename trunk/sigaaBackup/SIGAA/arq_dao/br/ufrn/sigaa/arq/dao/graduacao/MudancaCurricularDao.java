/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '06/09/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.MudancaCurricular;

/**
 * DAO para realizar consultas sobre MudancaCurricular
 * @author Victor Hugo
 *
 */
public class MudancaCurricularDao extends GenericSigaaDAO {

	/**
	 * Retorna as mudan�as curriculares do aluno informado
	 * caso seja informado um tipo, apenas do tipo especificado
	 * @param idDiscente
	 * @param tipoMudanca
	 * @return
	 * @throws DAOException
	 */
	public Collection<MudancaCurricular> findByDiscenteTipo(int idDiscente, Integer tipoMudanca) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(MudancaCurricular.class);
			c.add( Expression.eq("discente.id", idDiscente) );
			c.add( Expression.eq("ativo", true) );
			if( tipoMudanca != null )
				c.add( Expression.eq("tipoMudanca", tipoMudanca) );
			
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

}
