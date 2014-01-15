package br.ufrn.sigaa.arq.dao.ensino.latosensu;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaAvaliacaoProposta;

/**
 * DAO para realizar consultas a sobre as formas de Avaliação da proposta de Curso Lato Sensu.
 * 
 * @author Jean Guerethes
 */
public class FormaAvaliacaoPropostaDao extends GenericSigaaDAO {

	/**
	 * Busca todas as formas de Avaliação cadastrada para uma determinada proposta de Curso.
	 * 
	 * @param idProposta
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<FormaAvaliacaoProposta> findByFormaAvaliacaoProposta( int idProposta ) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(FormaAvaliacaoProposta.class);
			c.add( Expression.eq("proposta.id", idProposta));

			return c.list();
		} catch( Exception e){
			throw new DAOException(e.getMessage(), e);
		}
	}

	
}
