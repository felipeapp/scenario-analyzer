/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/01/2011
 *
 */
package br.ufrn.sigaa.ava.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.ForumCursoDocente;

/**
 * DAO para operações relacionadas ao acesso de docentes a Fórum de Curso.
 *  
 * @author arlindo
 *
 */
public class ForumCursoDocenteDao extends GenericSigaaDAO {
	
	/**
	 * Retorna os fóruns que o docente tem acesso
	 * @param idDocente
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<ForumCursoDocente> findByDocente(int idDocente) throws HibernateException, DAOException{
		
		String projecao = " f.id, f.forum.id, f.forum.descricao, f.forum.titulo, f.forum.data, " +
				" f.forum.usuario.pessoa.nome, f.forum.topicos, f.forum.idCursoCoordenador ";
				
		String hql = "select "+projecao+ " from ForumCursoDocente f " +
				" inner join f.forum " +
				" inner join f.forum.usuario " +
				" inner join f.forum.usuario.pessoa " +
				" where (f.forum.ativo = trueValue() or f.forum.ativo is null) " +
				"   and f.servidor.id = "+idDocente+
				" order by f.forum.data desc";
			
		
		Query q = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		List<ForumCursoDocente> lista = (List<ForumCursoDocente>) HibernateUtils.parseTo(q.list(), projecao, ForumCursoDocente.class, "f");		
		return lista;
	}


}
