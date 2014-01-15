/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 26/05/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;

/**
 * Classe de Dao com consultas sobre os cursos de ensino médio.
 * 
 * @author Rafael Gomes
 *
 */
public class CursoMedioDao extends CursoDao {

	/**
	 * Busca os cursos da unidade gestora acadêmica e do nível de ensino informados.
	 * 
	 */
	public Collection<CursoMedio> findByUnidadeNivel(Unidade unidade, char nivel) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(CursoMedio.class);
			if (unidade != null) {
				c.add(Restrictions.eq("unidade.id", unidade.getId()));
			}
			if (nivel != 0) {
				c.add(Restrictions.eq("nivel", nivel));
			}
			c.add(Restrictions.eq("ativo", Boolean.TRUE));

			@SuppressWarnings("unchecked")
			List<CursoMedio> lista =  c.list();
			return lista;
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}
	
	
	/**
	 * Retornar os cursos com o nome e/ou código informado.
	 * 
	 * @param nome
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Collection<Curso> findByNomeOrCodigo(String nome, String codigo, char nivelEnsino) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Curso.class);
			if (nome != null && !nome.equals("")) 
				c.add(Restrictions.ilike("nome", "%" + nome + "%"));
			if (codigo != null && !codigo.equals(""))
				c.add(Restrictions.ilike("codigo", codigo));

			c.add(Restrictions.eq("nivel", nivelEnsino));
			c.addOrder(Order.asc("nome"));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retornar os cursos com o nome e/ou código informado.
	 * 
	 * @param nome
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public boolean existeCursoByMesmoCodigo(CursoMedio curso) throws DAOException {
		try {
			if (ValidatorUtil.isNotEmpty(curso.getCodigo())) { 
				Criteria c = getSession().createCriteria(CursoMedio.class);
				c.add(Restrictions.ne("id", curso.getId()));
				c.add(Restrictions.eq("codigo", curso.getCodigo()));
				
				c.add(Restrictions.eq("nivel", curso.getNivel()));
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
