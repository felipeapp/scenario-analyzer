package br.ufrn.sigaa.arq.dao.ensino.latosensu;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaSelecaoProposta;

/**
 * DAO para realizar consultas a sobre as formas de Seleção da proposta de Curso Lato Sensu.
 * 
 * @author Jean Guerethes
 */
public class FormaSelecaoPropostaDao extends GenericSigaaDAO {

	/**
	 * Busca todas as formas de Seleção cadastrada para uma determinada proposta de Curso.
	 * 
	 * @param idProposta
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<FormaSelecaoProposta> findByFormaSelecaoProposta( int idProposta ) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(FormaSelecaoProposta.class);
			c.add( Expression.eq("proposta.id", idProposta));

			return c.list();
		} catch( Exception e){
			throw new DAOException(e.getMessage(), e);
		}
	}

}