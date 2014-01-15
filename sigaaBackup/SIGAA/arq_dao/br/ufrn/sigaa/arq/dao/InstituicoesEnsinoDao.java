/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/04/2007
 *
 */
package br.ufrn.sigaa.arq.dao;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;

/**
 * DAO para consulta especifica de institui��es de ensino
 * @author Andre M Dantas
 *
 */
public class InstituicoesEnsinoDao extends GenericSigaaDAO {

	/**
	 * Retorna uma cole��o de Institui��es de Ensino, cujo nome seja semelhante ao par�metro de busca informado.
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<InstituicoesEnsino> findByNome(String nome) throws DAOException {
		try {
			String hql = "select new InstituicoesEnsino(id, nome, sigla) from InstituicoesEnsino " +
					"where " + UFRNUtils.toAsciiUpperUTF8("nome") + " " +
							" like " + UFRNUtils.toAsciiUpperUTF8("'%"+nome+"%'") + " or " +
					UFRNUtils.toAsciiUpperUTF8("sigla") + " like "
					+ UFRNUtils.toAsciiUpperUTF8( "'"+nome+"%'" );

			return getSession().createQuery(hql).list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna todas as institui��es de Ensino, menos as institui��es cujo nome seja uma string vazia
	 */
	@SuppressWarnings("unchecked")
	public Collection<InstituicoesEnsino> findAll() throws DAOException {
		Criteria c = getCriteria(InstituicoesEnsino.class);
		c.add(Expression.ne("nome", ""));
		c.setCacheable(true);
		c.addOrder(Order.asc("nome"));
		return c.list();
	}

	/**
	 * Retorna uma Institui��o de Ensino, que possua o identificador id�ntico ao passado como par�metro de busca.
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public InstituicoesEnsino findInstituicaoEnsinoByPK(int id) throws DAOException {
		try {
			String hql = "select new InstituicoesEnsino(id, nome, sigla) from InstituicoesEnsino " +
					"where id = :id ";
			
			Query q = getSession().createQuery(hql);
			q.setInteger("id", id);
			
			return (InstituicoesEnsino) q.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
		
	/**
	 * Retorna a cole��o de Institui��es de Ensino, que estejam relacionadas ao identificador do curso passado por par�metro.
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<InstituicoesEnsino> findByCurso(int id) throws DAOException {
		try {
			String hql = "select new InstituicoesEnsino(cie.instituicaoEnsino.id, cie.instituicaoEnsino.nome, cie.instituicaoEnsino.sigla) from CursoInstituicoesEnsino cie " +
					"where cie.curso.id = :id ";
			
			Query q = getSession().createQuery(hql);
			q.setInteger("id", id);
			
			return q.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

}
