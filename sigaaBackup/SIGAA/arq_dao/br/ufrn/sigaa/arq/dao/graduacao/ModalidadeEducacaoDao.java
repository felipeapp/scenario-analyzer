/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '02/06/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;

public class ModalidadeEducacaoDao extends GenericSigaaDAO {

	public Collection<ModalidadeEducacao> findByCurso(Curso curso)
			throws HibernateException, DAOException {
		StringBuffer hql = new StringBuffer();
		hql.append(" SELECT new ModalidadeEducacao(m.id, m.descricao) ");
		hql.append(" FROM ModalidadeEducacao m, Curso c");
		hql.append(" WHERE c.modalidadeEducacao.id = m.id ");
		hql.append(" AND c.id = :idCurso ");
		hql.append(" ORDER BY m.descricao ");

		Query q = getSession().createQuery(hql.toString());

		q.setInteger("idCurso", curso.getId());

		return q.list();
	}

}
