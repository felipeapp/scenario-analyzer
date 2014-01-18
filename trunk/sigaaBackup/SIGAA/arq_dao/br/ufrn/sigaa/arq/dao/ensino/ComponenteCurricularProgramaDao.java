package br.ufrn.sigaa.arq.dao.ensino;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricularPrograma;

/**
 * DAo para acessar dados do programa do componente curricular
 *
 * @author Andre M Dantas
 *
 */
public class ComponenteCurricularProgramaDao extends GenericSigaaDAO {

	/**
	 * Retorna o componente curricular de um programa de acordo com o parâmetro setado. 
	 * @param componente
	 * @return
	 * @throws DAOException
	 */
	public ComponenteCurricularPrograma findAtualByComponente(int componente) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(ComponenteCurricularPrograma.class);
			c.add(Expression.eq("componenteCurricular", new ComponenteCurricular(componente)));
			c.addOrder(Order.desc("ano"));
			c.addOrder(Order.desc("periodo"));
			c.setMaxResults(1);
			return (ComponenteCurricularPrograma) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna o componente curricular de um programa de acordo com os parâmetros ano e período setados.  
	 * @param componente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public ComponenteCurricularPrograma findAtualByComponente(int componente, int ano, int periodo) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(ComponenteCurricularPrograma.class);
			c.add(Expression.eq("componenteCurricular", new ComponenteCurricular(componente)));
			c.add(Expression.eq("ano", ano));
			c.add(Expression.eq("periodo", periodo));
			c.addOrder(Order.desc("id"));
			c.setMaxResults(1);
			return (ComponenteCurricularPrograma) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	
	/**
	 * Retorna os ítens de um Programa de Ensino a Distancia
	 *  
	 * @param componente
	 * @return
	 * @throws DAOException 
	 */
	public ComponenteCurricular findAtualByComponenteItemPrograma(Integer componente) throws DAOException {
		
		try {
			Criteria c = getSession().createCriteria(ComponenteCurricular.class);
			c.add( Expression.eq("id", componente));
			return (ComponenteCurricular) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna o componente curricular programa de acordo com o componente passado por parâmetro.  
	 * @param componente
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricularPrograma> findByComponente(ComponenteCurricular cc) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(ComponenteCurricularPrograma.class);
			c.add(Expression.eq("componenteCurricular", cc));
			c.addOrder(Order.desc("ano"));
			c.addOrder(Order.desc("periodo"));
			c.setMaxResults(100);
			
			@SuppressWarnings("unchecked")
			Collection<ComponenteCurricularPrograma> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
}
