/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/09/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.medio.dominio.ForumMedio;
import br.ufrn.sigaa.ensino.medio.dominio.ForumMensagemMedio;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO para operações relacionadas ao Fórum.
 * Este DAO é responsável pela busca de fóruns e mensagens,
 * além da busca de informações relacionadas, como a quantidade de mensagens do fórum, etc. 
 *
 * @author Rafael Gomes
 *
 */
public class ForumMedioDao extends GenericSigaaDAO {

	/**
	 * Retorna o forum da turma Serie pelo ID.
	 * 
	 * @param idMensagemForum
	 * @return
	 * @throws DAOException
	 */
	public ForumMedio findForumTurmaSerieByID(int idTurmaSerie) throws DAOException {
		
			Criteria c = getCriteria(ForumMedio.class);
			c.add(Expression.eq("turmaSerie.id", idTurmaSerie) );
			c.addOrder(Order.asc("data"));
			
			return (ForumMedio) c.uniqueResult();
	}
	
	/**
	 * Retorna o forum da turma Serie pelo ID.
	 * 
	 * @param idMensagemForum
	 * @return
	 * @throws DAOException
	 */
	public List<ForumMedio> findForumTurmaSerieByDiscente(DiscenteAdapter discente, int ano) throws DAOException {
	
		String projecao = " fm.id, fm.titulo, fm.descricao, fm.nivel, fm.tipo ";
		
		String hql = " select " +projecao+
		" from ForumMedio fm, MatriculaDiscenteSerie mds " +
		" inner join fm.turmaSerie ts " +
		" inner join mds.discenteMedio dm " +
		" where ts.id = mds.turmaSerie.id and " +
		" fm.titulo is not null and " +
		" dm.id = " +discente.getId() + " and " +
		" fm.ativo = trueValue() and " +
		" ts.ano = " +ano;

		Query q = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		Collection<ForumMedio> lista = HibernateUtils.parseTo(q.list(), projecao, ForumMedio.class, "fm");
		return (List<ForumMedio>) lista;
	}
	
	/**
	 * Busca as mensagem de um tópico usando paginação.
	 * 
	 * @param idTopico
	 * @param primeiroRegistro
	 * @param registroPorPagina
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ForumMensagemMedio> findMensagensByTopico(int idTopico, int primeiroRegistro, int registroPorPagina) throws DAOException {
		try {
			Criteria c = getCriteria(ForumMensagemMedio.class);
			c.add(Expression.or(Expression.eq("id", idTopico), Expression.eq("topico.id", idTopico)));
			c.addOrder(Order.asc("data"));
			c.add(Expression.eq("ativo", Boolean.TRUE));
			
			if (registroPorPagina != 0) {
				c.setFirstResult(primeiroRegistro);
				c.setMaxResults(registroPorPagina);
			}
			
			List<ForumMensagemMedio> lista = c.list();
			
			if (!ValidatorUtil.isEmpty(lista)) {
				vinculaDiscentesAtivosAosDiscentesDoForum(lista);
			}
			
			return lista;
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Vincula os discentes dos objetos Usuario que estão presentes na lista de objetos
	 * ForumMensagemMedio aos objetos Discente que possuem o status ATIVO e FORMANDO.
	 * 
	 * @param listaMensagens
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private void vinculaDiscentesAtivosAosDiscentesDoForum(List<ForumMensagemMedio> listaMensagens) throws DAOException {
		
		List<Integer> listaIdPessoa = new ArrayList<Integer>();
		for (ForumMensagemMedio fm : listaMensagens) {
			listaIdPessoa.add(new Integer(fm.getUsuario().getPessoa().getId()));
		}
		
		Criteria criteriaDiscente = getCriteria(Discente.class);
		List<Object[]> discentesAtivos = criteriaDiscente.createAlias("pessoa", "pessoa")
						.add(Restrictions.in("pessoa.id", listaIdPessoa))
						.add(Restrictions.in("status", new Object[] {new Integer(StatusDiscente.ATIVO), new Integer(StatusDiscente.FORMANDO)}))
						.setProjection(Projections.projectionList()	
								.add(Projections.property("pessoa.id"))
								.add(Projections.property("id")))
						.list();
		
		for (Object[] objs : discentesAtivos) {
			int idPessoa = Integer.parseInt(objs[0].toString());
			int idDiscente = Integer.parseInt(objs[1].toString());
			
			for (ForumMensagemMedio fm : listaMensagens) {
				
				if (idPessoa == fm.getUsuario().getPessoa().getId()) {
					fm.getUsuario().setDiscente(new Discente(idDiscente));
					break;
				}
			}
		}
	}

	
	/**
	 * Retorna a quantidade de mensagens em um fórum.
	 * 
	 * @param idForumMensagemf
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Integer countMensagensForumCursos(int idForumMensagem) throws DAOException {
		Query q = getSession().createSQLQuery("select count(*) from medio.forum_mensagem_medio " +
					"where id_forum_mensagem_medio = " + idForumMensagem + " OR id_topico = " + idForumMensagem + " and ativo = trueValue()");
		return Integer.valueOf( String.valueOf( q.uniqueResult() ));
	}


	/**
	 * Retorna a quantidade de mensagens de um tópico.
	 * 
	 * @param idTopico
	 * @return
	 * @throws DAOException
	 */
	public int findCountMensagensByTopico(Integer idTopico) throws DAOException {
		return ((Long) getSession().createQuery("select count(*) from ForumMensagemMedio fm where fm.topico.id = ? and fm.ativo = trueValue()").setInteger(0, idTopico).uniqueResult()).intValue() + 1;
	}

	
	/**
	 * Retorna a quantidade de respostas por fórum.
	 * 
	 * @param id_forumTeste
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ForumMensagemMedio> findMensagesRespostasPorForum(int id_forumTeste) throws DAOException {
		@SuppressWarnings("unchecked")
		Collection<ForumMensagemMedio> lista = getSession().createQuery("from ForumMensagemMedio fm where fm.forum.id = ? and fm.ativo = trueValue()").setInteger(0, id_forumTeste).list(); 
		return lista;
	}
	
	/**
	 * Remove um tópico pelo id (DELETE).
	 * 
	 * @param id
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public void removerTopicosIndividuais(int id) throws DAOException {
		ForumMensagemMedio mensagem = findByPrimaryKey(id, ForumMensagemMedio.class);
		update("update medio.forum_mensagem_medio set respostas = ? where id_forum_mensagem_medio = ?", mensagem.getTopico().getRespostas() - 1, mensagem.getTopico().getId());
		
		update("delete from medio.forum_mensagem_medio where id_forum_mensagem_medio=?", id);
	}
	
	/**
	 * Remove um tópico e suas mensagens.
	 * 
	 * @param id
	 */
	public void removerTopicosComFilhos(int id) {
		update("delete from medio.forum_mensagem_medio where id_forum_mensagem_medio = ? or id_topico = ?", new Object[] { id, id });
	}
	
	/**
	 * Busca todas as mensagens de um fórum.
	 * 
	 * @param idForum
	 * @return
	 * @throws DAOException
	 */
	public Collection<ForumMensagemMedio> findListaMensagensForumByIDForum(int idForum) throws DAOException {
		return findListaMensagensForumByIDForum(idForum, null);
	}
	
	/**
	 * Retorna a lista de mensagens dos cursos informados
	 * @param idCursos
	 * @return
	 * @throws DAOException
	 */
	public Collection<ForumMensagemMedio> findListaMensagensForumByCursos(Collection<Curso> cursos) throws DAOException {
		String projecao = " fm.id, fm.titulo, fm.usuario.pessoa.id, fm.usuario.pessoa.nome, fm.usuario.login, " +
				"fm.usuario.id , fm.respostas, fm.ultimaPostagem, fm.data, fm.forum.id, fm.forum.nivel ";
		
		String hql = " select " +projecao+
		" from ForumMensagemMedio fm " +
		" inner join fm.forum " +
		" inner join fm.usuario " +
		" inner join fm.usuario.pessoa " +
		" where fm.forum.idCursoCoordenador in " +UFRNUtils.gerarStringIn(cursos)+
		" and fm.titulo is not null and " +
		" fm.topico is null and " +
		" fm.titulo is not empty and " +
		" fm.ativo = trueValue() " +
		" order by fm.ultimaPostagem desc";

		Query q = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		Collection<ForumMensagemMedio> lista = HibernateUtils.parseTo(q.list(), projecao, ForumMensagemMedio.class, "fm");
		return lista;
	}
	
	/**
	 * Busca todas as mensagens de um fórum com suporte a paginação.
	 * 
	 * @param idForum
	 * @return
	 * @throws DAOException
	 */
	public Collection<ForumMensagemMedio> findListaMensagensForumByIDForum(int idForum, PagingInformation paging) throws DAOException {
		String hql = " select fm.id, fm.titulo, fm.usuario.pessoa.id, fm.usuario.pessoa.nome, fm.usuario.login, fm.usuario.id , " +
				" fm.respostas, fm.ultimaPostagem, fm.data, fm.forum.id, fm.forum.nivel," +
				" fm.forum.turmaSerie.nome, fm.forum.turmaSerie.serie.numero, fm.forum.turmaSerie.serie.descricao " +
				" from ForumMensagemMedio fm " +
				" where fm.forum.id = :idForum and " +
				" fm.titulo is not null and " +
				" fm.topico is null and " +
				" fm.ativo = trueValue() " +
				" order by fm.ultimaPostagem desc";
	
		Query q = getSession().createQuery(hql);
		q.setInteger("idForum", idForum);
		
		if (paging != null) {
			Query count = getSession().createQuery("select count(fm.id) from ForumMensagemMedio fm where fm.forum.id = :idForum and " +
					" fm.titulo is not null and fm.topico is null and fm.ativo = trueValue()");
			count.setInteger("idForum", idForum);
	 
			paging.setTotalRegistros(((Long) count.uniqueResult()).intValue());
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		List<ForumMensagemMedio> resultado = null;
		
		for (int i = 0; i < lista.size(); i++) {
			if (resultado == null)
				resultado = new ArrayList<ForumMensagemMedio>();
			
			Object[] colunas = lista.get(i);
			int col = 0;
			
			ForumMensagemMedio fm = new ForumMensagemMedio();
			fm.setId( (Integer) colunas[col++] );
			fm.setTitulo((String) colunas[col++]);
			fm.setUsuario(new Usuario());
			fm.getUsuario().setPessoa(new Pessoa());
			fm.getUsuario().getPessoa().setId(((Number) colunas[col++]).intValue());
			fm.getUsuario().getPessoa().setNome((String) colunas[col++]);
			fm.getUsuario().setLogin((String) colunas[col++]);
			fm.getUsuario().setId((Integer) colunas[col++]);
			fm.setRespostas((Integer) colunas[col++]);
			fm.setUltimaPostagem((Date) colunas[col++]);
			fm.setData((Date) colunas[col++]);
			fm.setForum(new ForumMedio());
			fm.getForum().setId((Integer) colunas[col++]);
			fm.getForum().setNivel( String.valueOf( (colunas[col++]) ).charAt(0));
			fm.getForum().setTurmaSerie(new TurmaSerie());
			fm.getForum().getTurmaSerie().setNome( String.valueOf( (colunas[col++]) ));
			fm.getForum().getTurmaSerie().setSerie(new Serie());
			fm.getForum().getTurmaSerie().getSerie().setNumero( (Integer) (colunas[col++]) );
			fm.getForum().getTurmaSerie().getSerie().setDescricao( String.valueOf( (colunas[col++]) ));
			
			resultado.add(fm);
		}
		return resultado;
	}

	/**
	 * Busca todas as mensagens de um curso.
	 * 
	 * @param idCursoCoordenador
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public ForumMedio findForumMensagensByTurmaSerie(int idTurmaSerie) throws DAOException {
		
			Criteria c = getCriteria(ForumMedio.class);
			Criteria cTurmaSerie = c.createCriteria("turmaSerie");
			Criteria cSerie = cTurmaSerie.createCriteria("serie");
			c.add(Expression.eq("turmaSerie.id", idTurmaSerie) );
			c.addOrder(Order.asc("data"));
			cSerie.addOrder(Order.asc("numero"));
			cTurmaSerie.addOrder(Order.asc("nome"));
			List<ForumMedio> resultado = c.list();
			return  (resultado.size() > 0) ? resultado.get(0) : new ForumMedio();
	}
	
	/**
	 * Todas as mensagens de tópico.
	 * 
	 * @param idMensagemForum
	 * @return
	 * @throws DAOException
	 */
	public ForumMensagemMedio findForumMensagensByID(int idMensagemForum) throws DAOException {
		
			Criteria c = getCriteria(ForumMensagemMedio.class);
			c.add(Expression.eq("id", idMensagemForum) );
			c.addOrder(Order.asc("data"));
			
			return (ForumMensagemMedio) c.uniqueResult();
	}


	/**
	 * Retorna uma lista com os e-mails dos usuários que participaram de um tópico.
	 * 
	 * @param topico
	 * @return
	 * @throws DAOException
	 */
	public List<String> findEmailsByTopico(ForumMensagemMedio topico) throws DAOException {
		Query q = getSession().createQuery("select distinct fm.usuario.email from " +
				"ForumMensagemMedio fm where fm.topico.id = " + topico.getId() );

		@SuppressWarnings("unchecked")
		List<String> lista = q.list();
		return lista;
	}
	
	
	/**
	 * Encontra o e-mail de todos os alunos ativos de um curso.
	 * Isso é utilizado quando o coordenador de um curso cria
	 * um tópico e deseja enviar uma mensagem p/ os alunos.
	 * Um aluno está ativo quando tem status ATIVO, FORMANDO,
	 * GRADUANDO, EM_HOMOLOGACAO ou DEFENDIDO.
	 * 
	 * @param forumMensagem.getForum().getId()
	 * @return
	 * @throws DAOException
	 */
	public List<String> findEmailsTodosParticipantesForum(ForumMensagemMedio forumMensagem) throws DAOException {
		String sql = "select distinct(u.email) " +
					"from discente d " +
					"inner join curso c on c.id_curso = d.id_curso " +
					"inner join comum.usuario u on u.id_pessoa = d.id_pessoa " +
					"where c.id_curso = ? and d.status in (" +
					StatusDiscente.ATIVO + ", " + StatusDiscente.FORMANDO + ", " + StatusDiscente.GRADUANDO + ", " +
					StatusDiscente.EM_HOMOLOGACAO + ", " + StatusDiscente.DEFENDIDO + ")";
		
		@SuppressWarnings("unchecked")
		List<String> lista =  getJdbcTemplate().query(sql, new Object[] { forumMensagem.getForum().getTurmaSerie().getId() }, new RowMapper() {
			public Object mapRow(ResultSet rs, int row) throws SQLException {
				String emailDiscentes = rs.getString("email");
				return emailDiscentes;
			}
		});
		return lista;
	}

	
	/**
	 * Verifica se já existe tópico com mesmas informações e usuário cadastrado. 
	 * 
	 * @param idForum
	 * @return
	 * @throws DAOException
	 */
	public boolean existMensagensDuplicadaByForumUsuario(Integer idTopico, Integer idUsuario, String conteudo) throws DAOException {
		return ( ( (Long) getSession().createQuery(
					"select count(*) from ForumMensagemMedio fm " +
					"where fm.topico.id = ? " +
					"and fm.ativo = trueValue()" +
					"and fm.usuario.id = ?" +
					"and fm.conteudo = ?").setInteger(0, idTopico).setInteger(1, idUsuario).setString(2, conteudo).uniqueResult()).intValue() > 0 );
	}

	
	/**
	 * Verifica se existe registro de turma de ensino médio por docente e ano
	 * 
	 * @param idDocente
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public boolean existeTurmaMedioByDocenteAno(Integer idDocente, Integer idDocenteExterno, int ano) throws DAOException {
		
		StringBuffer hql = new StringBuffer();
		
		hql.append("select count(t.id) as qtdTurmasDoscente " +
				" from TurmaSerieAno tsa, DocenteTurma dt " +
				" inner join tsa.turma t " +
				" inner join tsa.turmaSerie ts " +
				" where dt.turma.id = t.id " +
				" and ts.ano = "+ ano +
				" and t.disciplina.nivel = '"+NivelEnsino.MEDIO+"'" +
				" and dt.");
		if (idDocente != null)
			hql.append("docente.id = " + idDocente);
		else if (idDocenteExterno != null)
			hql.append("docenteExterno.id = " + idDocenteExterno);
		else
			return false;
		
		Query q = getSession().createQuery(hql.toString());
		
		Long resultado = (Long) q.uniqueResult();
		return resultado > 0;
	}
	
}
