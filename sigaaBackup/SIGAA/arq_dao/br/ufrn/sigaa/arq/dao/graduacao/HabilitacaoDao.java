/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '01/02/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;

/**
 * DAO para consultas especializada aos dados de habilitação de cursos de graduação.
 *
 * @author André
 *
 */
public class HabilitacaoDao extends GenericSigaaDAO {

	/** Retorna uma coleção de habilitações, dado uma parte do nome, ordenado pelo nome.
	 * @param nome
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Habilitacao> findByNome(String nome, PagingInformation paging) throws DAOException {
		try {
			String hql = "FROM Habilitacao WHERE " +
					 UFRNUtils.toAsciiUpperUTF8("nome")
					 + " like " + UFRNUtils.toAsciiUpperUTF8("'%"+nome+"%'")
					 + " order by " + UFRNUtils.toAsciiUpperUTF8("nome");
			Query q = getSession().createQuery(hql);
			preparePaging(paging, q);
			return q.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}


	/**
	 * Retorna uma coleção de habilitações do curso especificado.
	 * @param idCurso
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Habilitacao> findByCurso( int idCurso ) throws HibernateException, DAOException {
		
		StringBuffer hql = new StringBuffer();
		hql.append(" SELECT new Habilitacao(h.id, h.nome) ");
		hql.append(" FROM Habilitacao h");
		hql.append(" WHERE h.curso.id = :idCurso ");
		hql.append(" ORDER BY h.nome ");
		//hql.append(" FROM MatrizCurricular m, Habilitacao h");
		//hql.append(" WHERE m.habilitacao.id = h.id ");

		Query q = getSession().createQuery( hql.toString() );
		
		q.setInteger("idCurso", idCurso);
		
		return q.list();
		
	}

}
