package br.ufrn.sigaa.ensino.stricto.dao;

import java.util.Collection;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.stricto.dominio.Recomendacao;

/**
 * DAO's utilizados no CRUD associado ao domínio {@link Recomendacao}
 * @author Mário Rizzi
 */
public class RecomendacaoDAO extends GenericSigaaDAO  {

	/**
	 * Retorna todas as recomendações para listagem.
	 * @return
	 * @throws DAOException
	 */
	public Collection<Recomendacao> findAllProjection() throws DAOException{
			
			StringBuilder hql = new StringBuilder();
			
			StringBuilder projecao =   
				new StringBuilder("id, curso.nome, curso.nivel, curso.unidade.sigla, conceito, portaria ");
			hql.append(" SELECT ");
			hql.append(projecao);
			hql.append(" FROM Recomendacao ");
			hql.append(" ORDER BY ");
			hql.append(" curso.nome ");
			
			Query q = getSession().createQuery(hql.toString());
			
			return HibernateUtils.parseTo(q.list(), projecao.toString(), Recomendacao.class);

	}
	
	/**
	 * Retorna a recomendação de um curso.
	 * @return
	 * @throws DAOException
	 */
	public Recomendacao findbyCurso(Integer idCurso) throws DAOException{
			
			StringBuilder hql = new StringBuilder();
			
			hql.append(" FROM Recomendacao WHERE curso.id = ");
			hql.append( idCurso );
			hql.append(" ORDER BY ");
			hql.append(" curso.nome ");
			
			Query q = getSession().createQuery(hql.toString());
			
			return (Recomendacao) q.setMaxResults(1).uniqueResult();

	}
}
