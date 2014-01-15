package br.ufrn.sigaa.ead.dao;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;

/**
 *  Dao responsável pelas consultas que são inerentes a MetodologiacaAvaliacao
 * 
 * @author Henrique André
 */
public class MetodologiacaAvaliacaoEadDao extends GenericSigaaDAO {

	/**
	 * Todas as metodologias de um curso
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public List<MetodologiaAvaliacao> findByCurso(int id) throws DAOException {
		String hql = "select m from MetodologiaAvaliacao m where m.curso.id = " + id + " order by m.dataCadastro desc";
		Query query = getSession().createQuery(hql);
		return query.list();
	}

	/**
	 * Retorna a última metodologia que foi desativada
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public MetodologiaAvaliacao ultimaMetodologiaInativada(int id) throws DAOException {
		String hql = "select m from MetodologiaAvaliacao m where m.ativa = falseValue() and m.curso.id = " + id + " order by m.dataInativacao desc";
		Query query = getSession().createQuery(hql);
		query.setMaxResults(1);
		return (MetodologiaAvaliacao) query.uniqueResult();
	}

	/**
	 * Retorna a metodologia de um curso em um dado ano e periodo
	 * 
	 * @param curso
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public MetodologiaAvaliacao findByCursoAnoPeriodo(Curso curso, int ano, int periodo) throws DAOException {
		
		int p;
		//caso o periodo seja se fériasdeverá seja do periodo regular anterior
		if (periodo == 3)
			p = 1;
		else if (periodo == 4)
			p = 2;
		else
			p = periodo;
		
		int anoPeriodo = (ano*10)+p;
		
		String sql = "select ma from MetodologiaAvaliacao ma" +
				" inner join fetch ma.curso curso" +
				" left join fetch ma.itens " +
				"	where curso.id = :idCurso " +
				"		and coalesce((ma.anoInicio * 10) + ma.periodoInicio, 0)   		<= :anoPeriodo " +
				"		and coalesce((ma.anoFim    * 10) + ma.periodoFim, :anoPeriodo)  >= :anoPeriodo " +
				" order by ma.dataCadastro desc";
		
		
		Query query = getSession().createQuery(sql);
		query.setParameter("idCurso", curso.getId());
		query.setParameter("anoPeriodo", anoPeriodo);
		
		return (MetodologiaAvaliacao) query.uniqueResult();
	}

}
