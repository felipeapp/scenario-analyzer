/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 31/05/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dao;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;

/**
 * Classe de Dao com consultas sobre as s�ries de ensino m�dio.
 * 
 * @author Rafael Gomes
 *
 */
public class SerieDao extends GenericSigaaDAO{

	/**
	 * Retornar os s�rie com a descri��o, n�mero e/ou curso de ensino m�dio informados.
	 * 
	 * @param nome
	 * @param codigo
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Serie> findByDescricaoNumeroCurso(String descricao, Integer numero, CursoMedio curso, char nivelEnsino) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Serie.class);
			if ( descricao != null && isNotEmpty(descricao) ) 
				c.add(Restrictions.ilike("descricao", "%" + descricao + "%"));
			if ( numero != null && isNotEmpty(numero) )
				c.add(Restrictions.eq("numero", numero));
			if ( curso != null && isNotEmpty(curso) ){
				c.add(Restrictions.eq("cursoMedio", curso));
			}	
			
			c.addOrder(Order.asc("cursoMedio"));
			c.addOrder(Order.asc("numero"));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retornar os s�rie por curso de ensino m�dio informado.
	 * 
	 * @param nome
	 * @param codigo
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Serie> findByCurso(CursoMedio curso) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Serie.class);
			if ( curso != null && isNotEmpty(curso) ){
				c.add(Restrictions.eq("cursoMedio", curso));
			}	
			c.addOrder(Order.asc("cursoMedio"));
			c.addOrder(Order.asc("numero"));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * M�todo respons�vel por verificar a exist�ncia de s�ries ativas cadastradas com o mesmo n�mero para o mesmo curso.
	 * 
	 * @param nome
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public boolean existeSerieByMesmoNumeroCurso(Serie serie) throws DAOException {
		try {
			if ( serie.getCursoMedio().getId() > 0 ) {
				Criteria c = getSession().createCriteria(Serie.class);
				c.add(Restrictions.ne("id", serie.getId()));
				c.add(Restrictions.eq("numero", serie.getNumero()));
				c.add(Restrictions.eq("cursoMedio", serie.getCursoMedio()));
				
				c.setProjection(Projections.rowCount());
				return (Integer) c.uniqueResult() > 0;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
}
