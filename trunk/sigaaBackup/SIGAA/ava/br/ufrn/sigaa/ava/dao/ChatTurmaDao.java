/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '21/06/2011'
 *
 */
package br.ufrn.sigaa.ava.dao;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.ChatTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe de acesso a dados de chats de turmas virtuais.
 * 
 * @author ilueny santos.
 *
 */
public class ChatTurmaDao extends GenericSigaaDAO {

	/**
	 * Lista todos os chats da turma. 
	 * 
	 * @throws  
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public List<ChatTurma> findChatsByTurma(Turma turma) throws DAOException  {
		String projecao = " ct.id, ct.titulo, ct.descricao, ct.conteudo, ct.publicarConteudo, " +
				"ct.dataCadastro, ct.dataInicio, ct.dataFim, ct.horaInicio, ct.horaFim, ct.usuario.id, aula.id, aula.descricao, ct.turma.id, ct.videoChat ";
		Query q = getSession().createQuery(
				"SELECT " + projecao + " FROM ChatTurma ct LEFT JOIN ct.aula as aula WHERE ct.turma.id = :idTurma and ct.ativo = trueValue()");
		q.setInteger("idTurma", turma.getId());
		return (List<ChatTurma>) HibernateUtils.parseTo(q.list(), projecao, ChatTurma.class, "ct");
	}


	/**
	 * Lista todos os chats da turma. 
	 * 
	 * @throws  
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public List<ChatTurma> findChatsByTurma(Turma turma, boolean isDocente) throws DAOException  {
		String projecao = " ct.id, ct.titulo, ct.descricao, ct.conteudo, ct.publicarConteudo, " +
				"ct.dataCadastro, ct.dataInicio, ct.dataFim, ct.horaInicio, ct.horaFim, ct.usuario.id, aula.id, aula.descricao, ct.turma.id, ct.videoChat ";
		String hql = "SELECT " + projecao + " FROM ChatTurma ct LEFT JOIN ct.aula as aula WHERE ct.turma.id = :idTurma and ct.ativo = trueValue()";
		if (!isDocente) {
			hql += " and (aula is NULL OR aula.visivel = trueValue()) ";
		}
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idTurma", turma.getId());
		return (List<ChatTurma>) HibernateUtils.parseTo(q.list(), projecao, ChatTurma.class, "ct");
	}
	
}
