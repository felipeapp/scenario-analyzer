/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/11/2008 
 * 
 */
package br.ufrn.sigaa.ava.cv.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.cv.dominio.ComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.ForumComunidade;
import br.ufrn.sigaa.cv.dominio.ForumMensagemComunidade;

/**
 * @author David Pereira
 *
 */
public class ForumComunidadeDao extends GenericSigaaDAO {

	/**
	 * @param idForumMensagemSelecionado
	 * @param paginacao
	 * @return
	 * @throws DAOException 
	 */
	public List<ForumMensagemComunidade> findMensagensByTopico(int idTopico, int primeiroRegistro, int registroPorPagina) throws DAOException {
		try {
			Criteria c = getCriteria(ForumMensagemComunidade.class);
			c.add(Expression.or(Expression.eq("id", idTopico), Expression
					.eq("topico.id", idTopico)));
			c.addOrder(Order.desc("data"));
			c.add(Expression.eq("ativo", Boolean.TRUE));

			if (registroPorPagina != 0) {
				c.setFirstResult(primeiroRegistro);
				c.setMaxResults(registroPorPagina);
			}

			//			if (paginacao != null) {
			//				paginacao
			//						.setTotalRegistros(findCountMensagensByTopico(idForumMensagemSelecionado));
			//
			//				// Paginacao
			//				c.setFirstResult(paginacao.getPaginaAtual()
			//						* paginacao.getTamanhoPagina());
			//				c.setMaxResults(paginacao.getTamanhoPagina());
			//			}

			@SuppressWarnings("unchecked")
			List<ForumMensagemComunidade> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	public Integer countMensagensForumCursos(int idForumMensagem) throws HibernateException, DAOException {
		Query q = getSession().createSQLQuery("select count(*) from cv.forum_mensagem " +
				"where id_forum_mensagem = " + idForumMensagem + " OR id_topico = " + idForumMensagem + " and ativo = trueValue()");
		return Integer.valueOf( String.valueOf( q.uniqueResult() ));
	}

	/**
	 * Está exibindo apenas as mensagens ativas = true
	 * Mensagens ativas = false significa que foram removidas
	 * pelo usuário
	 * 
	 * @param forumSelecionado
	 * @return
	 */
	public List<ForumMensagemComunidade> findListaMensagensForumByIDForum(int forumSelecionado, PagingInformation paging, boolean crescente) throws DAOException {

		// Garante que a página atual vai conter registros, se houver algum.
		if (paging.getPaginaAtual() * paging.getTamanhoPagina() > paging.getTotalRegistros())
			paging.setPaginaAtual(0);
		
		String hql = "select fm from ForumMensagemComunidade fm where fm.forum.id = ? " +
		"and fm.titulo is not null and fm.topico is null and fm.titulo <> ' ' and fm.titulo <> '' and ativo = trueValue() " +
		"order by coalesce(fm.ultimaPostagem, fm.data) " + (crescente ? "" : "DESC");

		Query q = getSession().createQuery(hql);
		
		if (paging != null){
			Query count = getSession().createQuery("select count(fm) from ForumMensagemComunidade fm where fm.forum.id = ? " +
			"and fm.titulo is not null and fm.topico is null and fm.titulo <> ' ' and fm.titulo <> '' and ativo = trueValue()");
			count.setInteger(0, forumSelecionado);
			
			paging.setTotalRegistros(((Long) count.uniqueResult()).intValue());
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}
		
		q.setInteger(0, forumSelecionado);

		@SuppressWarnings("unchecked")
		List <ForumMensagemComunidade> lista = q.list();
		return lista;
	}

	public Collection<ForumMensagemComunidade> findMensagesRespostasPorForum(int id_forumTeste) throws HibernateException, DAOException {
		@SuppressWarnings("unchecked")
		Collection<ForumMensagemComunidade> lista = getSession().createQuery("from ForumMensagemComunidade fm where fm.forum.id = ? and fm.ativo = trueValue()").setInteger(0, id_forumTeste).list();
		return lista;
	}

	/**
	 * @param forumSelecionado 
	 * @param idForumMensagemSelecionado
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<ForumMensagemComunidade> findListaForumMensagensByIDForum(int idforum, int idForumMensagem) throws HibernateException, DAOException {

		ArrayList<ForumMensagemComunidade> listaForumMensagens = (ArrayList<ForumMensagemComunidade>) findMensagesRespostasPorForum(idforum);
		ArrayList<ForumMensagemComunidade> listaFinal = new ArrayList<ForumMensagemComunidade>();

		if (listaForumMensagens != null) {
			for (ForumMensagemComunidade forumMensagem : listaForumMensagens) {

				if ( forumMensagem.getTopico() != null && idForumMensagem == forumMensagem.getTopico().getId() ) {
					listaFinal.add(forumMensagem);
				}

			}
		}
		return listaFinal;

	}

	/**
	 * @param comunidade
	 * @return
	 */
	public List<ForumComunidade> findByComunidade(ComunidadeVirtual comunidade) {
		return null;
	}

	public void removerTopicosIndividuais(int id) throws HibernateException, DAOException {
		update("delete from cv.forum_mensagem where id_forum_mensagem=?", id);
	}

	public ForumMensagemComunidade findForumMensagemComunidade(int id) throws HibernateException, DAOException {
		return (ForumMensagemComunidade) getSession().createQuery("select fm from ForumMensagemComunidade fm inner join fetch fm.topico t where fm.id = ?").setInteger(0, id).uniqueResult();
	}

	/**
	 * Conta as mensagens de resposta existentes e adicionar +1 ao resultado
	 * 
	 * @param id
	 * @return
	 */
	public int findCountMensagensByTopicoAdicao(Integer idTopico) throws DAOException {
		return ((Long) getSession().createQuery("select count(*) from ForumMensagemComunidade fm where fm.topico.id = ? and fm.ativo = trueValue()")
				.setInteger(0, idTopico).uniqueResult()).intValue() + 1;
	}

	/**
	 * Conta as mensagens de resposta existentes e diminui -1 ao resultado
	 * 
	 * @param idTopico
	 * @return
	 * @throws DAOException
	 */
	public int findCountMensagensByTopicoRemocao(Integer idTopico) throws DAOException {
		return ((Long) getSession().createQuery("select count(*) from ForumMensagemComunidade fm where fm.topico.id = ? and fm.ativo = trueValue()")
				.setInteger(0, idTopico).uniqueResult()).intValue() -1;
	}

}
