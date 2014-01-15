/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/03/2011
 *
 */
package br.ufrn.sigaa.ava.forum.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeralParticipante;

/**
 * DAO que disponibiliza buscas de Participantes de Fórum.
 *  
 * @author Ilueny Santos
 *
 */
public class ForumGeralParticipanteDao extends GenericSigaaDAO {

	/**
	 * Retorna os fóruns que o usuário tem acesso.
	 * 
	 * @param idDocente
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ForumGeralParticipante> findByUsuario(int idUsuario) throws HibernateException, DAOException{
		
		String projecao = " fb.id, fb.forum.id, fb.forum.descricao, fb.forum.titulo, fb.forum.dataCadastro, " +
				" fb.forum.usuario.pessoa.nome ";
				
		String hql = "SELECT " + projecao + " FROM ForumGeralParticipacao fb " +
				" JOIN fp.forum " +
				" JOIN fp.forum.usuario " +
				" JOIN fp.forum.usuario.pessoa " +
				" WHERE fb.ativo = trueValue() AND fp.forum.ativo = trueValue() " +
				"   AND (fp.usuario.id = :idUsuario OR fb.forum.usuario.id = :idUsuario) " +
				" ORDER BY fp.forum.dataCadastro desc";
			
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idUsuario", idUsuario);
		return  (List<ForumGeralParticipante>) HibernateUtils.parseTo(q.list(), projecao, ForumGeralParticipante.class, "fb");
	}
	
}
