/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 20/06/2007
 */
package br.ufrn.sigaa.arq.dao.ensino;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;

/**
 * DAo para efetuar consultas de formas de ingresso de discentes
 * 
 * @author leonardo
 * 
 */
public class FormaIngressoDao extends GenericSigaaDAO {

	/**
	 * Busca todas as formas de entrada ativas de determinado nível e
	 * determinado tipo
	 * 
	 * @param nivel
	 * @param tipo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<FormaIngresso> findByNivelTipo(char nivel, char tipo) throws DAOException {
		Criteria c = getSession().createCriteria(FormaIngresso.class);
		c.add(Expression.eq("nivel", nivel));

		Collection<Character> tipos = adicionarTodosTipos(tipo);

		c.add(Expression.in("tipo", tipos));
		c.add(Expression.eq("ativo", Boolean.TRUE));

		c.addOrder(Order.asc("descricao"));
		return c.list();
	}

	/**
	 * 
	 * Busca as formas de ingresso válidas para um discente de acordo com o seu
	 * tipo e nível.
	 * 
	 * @param nivel
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<FormaIngresso> findAllFormasEntradaHabilitadasByNivelTipo(Character nivel, Character tipo)
			throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" select distinct f "
					+ " from FormaIngresso f "
					+
					// Ao cadastrar uma nova forma de ingresso válida para todos
					// os níveis/tipo na área administrativa do Sigaa,
					// o campo nivel/tipo é setado como nulo, ou seja,
					// nível=nulo implica que a forma de ingresso é valida
					// para todos os níveis de ensino. Para tipo=nulo implica
					// que a forma é valida para todos os tipos de
					// discente(REGULAR,ESPECIAL).
					" where  (f.nivel = :nivel or f.nivel is null) "
					+ "        and (f.tipo in (:tipos) or f.tipo is null) " + "        and f.ativo = trueValue() ");

			Collection<Character> tipos = adicionarTodosTipos(tipo);
			Query q = getSession().createQuery(hql.toString());
			q.setCharacter("nivel", nivel);
			q.setParameterList("tipos", tipos);

			@SuppressWarnings("unchecked")
			Collection<FormaIngresso> lista = q.list();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	/**
	 * Indica se existe forma de ingresso com a descrição informada.
	 * 
	 * @param descricao
	 * @return true, caso exista forma de ingresso com a descrição informada.
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public boolean hasFormaIngresso(FormaIngresso formaIngresso) throws HibernateException, DAOException {
		if (formaIngresso == null)
			return false;
		StringBuilder hql = new StringBuilder("from FormaIngresso" + " where "
				+ UFRNUtils.toAsciiUpperUTF8("descricao") + " = " + UFRNUtils.toAsciiUpperUTF8(":descricao"));
		// atualização
		if (formaIngresso.getId() != 0) {
			hql.append(" and id != :id");
		}
		
		Query q = getSession().createQuery(hql.toString());
		q.setString("descricao", formaIngresso.getDescricao());
		if (formaIngresso.getId() != 0) {
			q.setInteger("id", formaIngresso.getId());
		}
		
		FormaIngresso formaIngressoCadastrada = (FormaIngresso) q.setMaxResults(1).uniqueResult();
		return formaIngressoCadastrada != null;
	}

	/**
	 * Busca formas de ingresso dos ids passados como parâmetro
	 * 
	 * @param ids
	 * @return
	 */
	public Collection<FormaIngresso> findByIds(Integer... ids) throws DAOException {
		Criteria c = getSession().createCriteria(FormaIngresso.class);
		c.add(Restrictions.in("id", ids));
		c.addOrder(Order.asc("descricao"));

		@SuppressWarnings("unchecked")
		Collection<FormaIngresso> lista = c.list();
		return lista;
	}

	private Collection<Character> adicionarTodosTipos(char tipo) {
		Collection<Character> tipos = new ArrayList<Character>();
		tipos.add(tipo);
		tipos.add('A');
		return tipos;
	}

}
