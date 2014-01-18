/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 12/12/2006
 *
 */
package br.ufrn.sigaa.ava.dao;

import static br.ufrn.arq.util.ValidatorUtil.isAllEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

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

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.Forum;
import br.ufrn.sigaa.ava.dominio.ForumMensagem;
import br.ufrn.sigaa.cv.dominio.ForumMensagemComunidade;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO para operações relacionadas ao Fórum.
 * Este DAO é responsável pela busca de fóruns e mensagens,
 * além da busca de informações relacionadas, como a quantidade de mensagens do fórum, etc. 
 *
 * @author David Ricardo
 *
 */
public class ForumDao extends GenericSigaaDAO {

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
	public List<ForumMensagem> findMensagensByTopico(int idTopico, int primeiroRegistro, int registroPorPagina) throws DAOException {
		try {
			Criteria c = getCriteria(ForumMensagem.class);
			c.add(Expression.or(Expression.eq("id", idTopico), Expression.eq("topico.id", idTopico)));
			c.addOrder(Order.asc("data"));
			c.add(Expression.eq("ativo", Boolean.TRUE));
			
			if (registroPorPagina != 0) {
				c.setFirstResult(primeiroRegistro);
				c.setMaxResults(registroPorPagina);
			}
			
			List<ForumMensagem> lista = c.list();
			
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
	 * ForumMensagem aos objetos Discente que possuem o status ATIVO e FORMANDO.
	 * 
	 * @param listaMensagens
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private void vinculaDiscentesAtivosAosDiscentesDoForum(List<ForumMensagem> listaMensagens) throws DAOException {
		
		List<Integer> listaIdPessoa = new ArrayList<Integer>();
		for (ForumMensagem fm : listaMensagens) {
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
			
			for (ForumMensagem fm : listaMensagens) {
				
				if (idPessoa == fm.getUsuario().getPessoa().getId()) {
					fm.getUsuario().setDiscente(new Discente(idDiscente));
					break;
				}
			}
		}
	}

	/**
	 * Lista de matrículas dos vínculos ativos do discente que postou a mensagem no 
	 * fórum (caso o usuário seja um discente).
	 * @param idUsuario
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<Long> findMatriculasDiscentePostaramMensagens(int idUsuario, Integer idCurso) throws HibernateException, DAOException {
		String sql = "select d.matricula from comum.usuario u " +
			 		 " inner join comum.pessoa p on p.id_pessoa = u.id_pessoa " + 
			 		 " inner join discente d on d.id_pessoa = p.id_pessoa " +
					 " where u.id_usuario = ? and matricula is not null and d.status = 1";
		
		if (isNotEmpty(idCurso))
			 sql += " and d.id_curso = " + idCurso;
		
		Query q = getSession().createSQLQuery(sql).setInteger(0, idUsuario);
		@SuppressWarnings("unchecked")
		List<Long> lista = q.list();
		return lista;
	}
	
	/**
	 * Retorna a quantidade de mensagens em um fórum.
	 * 
	 * @param idForumMensagemf
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Integer countMensagensForum(int idForumMensagem) throws DAOException {
		Query q = getSession().createSQLQuery("select count(*) from ava.forum_mensagem " +
					"where id_forum_mensagem = " + idForumMensagem + " OR id_topico = " + idForumMensagem + " and ativo = trueValue()");
		return Integer.valueOf( String.valueOf( q.uniqueResult() ));
	}

	/**
	 * Busca os fóruns de uma mensagem.
	 * 
	 * @param turma
	 * @return
	 */
	public List<Forum> findByTurma(Turma turma) {
		String sql = "select f.id_forum, f.descricao, f.titulo, f.id_turma, f.data_criacao, "
			+ "p.nome, (select count(*) from ava.forum_mensagem where id_forum = f.id_forum and id_topico is null) as total, "
			+ "(select data from ava.forum_mensagem fm where fm.id_forum = f.id_forum order "
			+ "by data desc " + BDUtils.limit(1) + ") as data, f.topicos from ava.forum f, comum.usuario u, comum.pessoa p "
			+ "where (f.ativo=trueValue() or f.ativo is null) and f.id_turma = ? and f.id_usuario = u.id_usuario and "
			+ "u.id_pessoa = p.id_pessoa and f.tipo = " + Forum.TURMA + " order by f.data_criacao desc";
		@SuppressWarnings("unchecked")
		List<Forum> lista =  getJdbcTemplate().query(sql, new Object[] { turma.getId() }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Forum forum = new Forum();
				forum.setId(rs.getInt("id_forum"));
				forum.setDescricao(rs.getString("descricao"));
				forum.setTitulo(rs.getString("titulo"));
				forum.setTurma(new Turma(rs.getInt("id_turma")));
				forum.setData(rs.getTimestamp("data_criacao"));
				forum.setUsuario(new Usuario());
				forum.getUsuario().setPessoa(new Pessoa());
				forum.getUsuario().getPessoa().setNome(rs.getString("nome"));
				forum.setTotalTopicos(rs.getInt("total"));
				forum.setDataUltimaMensagem(rs.getTimestamp("data"));
				forum.setTopicos(rs.getBoolean("topicos"));
				return forum;
			}
		});
		return lista;
	}
	
	/**
	 * Busca os fóruns de um curso.
	 * 
	 * @param turma
	 * @return
	 */
	public List<Forum> findForunsSemPermissaoByDocenteCurso(int idDocente, int idCurso) {
		String sql = "select f.id_forum, f.descricao, f.titulo, f.data_criacao, p.nome, f.topicos, " +
				" (select data from ava.forum_mensagem fm where fm.id_forum = f.id_forum order "
			+ "by data desc " + BDUtils.limit(1) + ") as data, " +
			 	" (select count(*) from ava.forum_mensagem where id_forum = f.id_forum and id_topico is null) as total " +
			"  from ava.forum f" +
			"  inner join comum.usuario u on (f.id_usuario = u.id_usuario)" +
			"  inner join comum.pessoa p on (u.id_pessoa = p.id_pessoa) "
			+ " where (f.ativo=trueValue() or f.ativo is null) and f.idcursocoordenador = ? "
			+ " and f.tipo = " + Forum.TURMA 
			+ " and not exists (select id_forum from ava.forum_curso_docente " +
					             " where id_servidor = ? " +
					                " and id_forum = f.id_forum ) "+
			" order by f.data_criacao desc ";
		@SuppressWarnings("unchecked")
		List<Forum> lista =  getJdbcTemplate().query(sql, new Object[] { idCurso, idDocente }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Forum forum = new Forum();
				forum.setId(rs.getInt("id_forum"));
				forum.setDescricao(rs.getString("descricao"));
				forum.setTitulo(rs.getString("titulo"));
				forum.setData(rs.getTimestamp("data_criacao"));
				forum.setUsuario(new Usuario());
				forum.getUsuario().setPessoa(new Pessoa());
				forum.getUsuario().getPessoa().setNome(rs.getString("nome"));
				forum.setTotalTopicos(rs.getInt("total"));
				forum.setDataUltimaMensagem(rs.getTimestamp("data"));
				forum.setTopicos(rs.getBoolean("topicos"));
				return forum;
			}
		});
		return lista;
	}	

	/**
	 * Busca todos os murais de uma turma.
	 * 
	 * @param turma
	 * @return
	 */
	public Forum findMuralByTurma(Turma turma) {
		return (Forum) getHibernateTemplate().uniqueResult("from Forum f where f.turma.id = ? and f.tipo = ?", new Object[] { turma.getId(), Forum.MURAL });
	}

	/**
	 * Retorna a quantidade de mensagens de um tópico.
	 * 
	 * @param idTopico
	 * @return
	 * @throws DAOException
	 */
	public int findCountMensagensByTopico(Integer idTopico) throws DAOException {
		return ((Long) getSession().createQuery("select count(*) from ForumMensagem fm where fm.topico.id = ? and fm.ativo = trueValue()").setInteger(0, idTopico).uniqueResult()).intValue() + 1;
	}

	
	/**
	 * Retorna a quantidade de respostas por fórum.
	 * 
	 * @param id_forumTeste
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ForumMensagem> findMensagesRespostasPorForum(int id_forumTeste) throws DAOException {
		@SuppressWarnings("unchecked")
		Collection<ForumMensagem> lista = getSession().createQuery("from ForumMensagem fm where fm.forum.id = ? and fm.ativo = trueValue()").setInteger(0, id_forumTeste).list(); 
		return lista;
	}
	
	/**
	 * Remove uma mensagem ou tópico conforme o parâmetro setado.
	 * Este método deve ser invocado por um processador.
	 * @param id
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public void removerMensagemTopicoIndividual(int id) throws DAOException {
		
		ForumMensagem mensagem = findByPrimaryKey(id, ForumMensagem.class);
		Date dataUltimaMensagem = mensagem.getUltimaPostagem();
		
		//Remove a mensagem selecionada do topico
		getJdbcTemplate().update("update ava.forum_mensagem set ativo = false WHERE id_forum_mensagem=?",
				new Integer[]{mensagem.getId()});
		
		if( !isEmpty(mensagem.getTopico()) ){
			//Retorna a data da ultima mensagem postada no tópico
			List<Date> lista = getJdbcTemplate().
				queryForList("SELECT data FROM ava.forum_mensagem f " +
						"WHERE id_topico = ? ORDER BY data DESC LIMIT 1", 
						new Object[] { mensagem.getTopico().getId() }, Date.class);
			
			if( !isAllEmpty(lista) ){
				dataUltimaMensagem = lista.get(0);
			}else
				dataUltimaMensagem = mensagem.getTopico().getData();
			
			//Atualiza o número de respostas e a data da ultima mensagem postada.
			getJdbcTemplate().update("UPDATE ava.forum_mensagem SET ultima_postagem = ?, respostas = ? " +
					"WHERE id_forum_mensagem = ?", new Object[] { dataUltimaMensagem, 
					mensagem.getTopico().getRespostas() - 1, mensagem.getTopico().getId()});
		}
				

	}
	/**
	 * Denuncia uma mensagem ou tópico conforme o parâmetro setado.
	 * Este método deve ser invocado por um processador.
	 * @param id
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public void denunciarMensagemTopicoIndividual(int id) throws DAOException {
		
		ForumMensagem mensagem = findByPrimaryKey(id, ForumMensagem.class);
		Date dataUltimaMensagem = mensagem.getUltimaPostagem();
		
	}
	
	/**
	 * Remove um tópico e suas mensagens.
	 * 
	 * @param id
	 */
	public void removerTopicosComFilhos(int id) {
		update("update ava.forum_mensagem set ativo = false where id_forum_mensagem = ? or id_topico = ?", new Object[] { id, id });
	}

	/**
	 * Busca todas as mensagens de um fórum.
	 * 
	 * @param idForum
	 * @return
	 * @throws DAOException
	 */
	public Collection<ForumMensagem> findListaMensagensForumByIDForum(int idForum) throws DAOException {
		return findListaMensagensForumByIDForum(idForum, null);
	}
	
	/**
	 * Retorna a lista de mensagens dos cursos informados
	 * @param idCursos
	 * @return
	 * @throws DAOException
	 */
	public Collection<ForumMensagem> findListaMensagensForumByCursos(Collection<Curso> cursos) throws DAOException {
		String projecao = " fm.id, fm.titulo, fm.usuario.pessoa.id, fm.usuario.pessoa.nome, fm.usuario.login, " +
				"fm.usuario.id , fm.respostas, fm.ultimaPostagem, fm.data, fm.forum.id, fm.forum.nivel ";
		
		String hql = " select " +projecao+
		" from ForumMensagem fm " +
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
		Collection<ForumMensagem> lista = HibernateUtils.parseTo(q.list(), projecao, ForumMensagem.class, "fm");
		return lista;
	}
	
	/**
	 * Busca todas as mensagens de um fórum com suporte a paginação.
	 * 
	 * @param idForum
	 * @return
	 * @throws DAOException
	 */
	public Collection<ForumMensagem> findListaMensagensForumByIDForum(int idForum, PagingInformation paging) throws DAOException {
		String hql = " select fm.id, fm.titulo, fm.usuario.pessoa.id, fm.usuario.pessoa.nome, fm.usuario.login, fm.usuario.id , fm.respostas, fm.ultimaPostagem, fm.data, fm.forum.id, fm.forum.nivel" +
				" from ForumMensagem fm " +
				" where fm.forum.id = :idForum and " +
				" fm.titulo is not null and " +
				" fm.topico is null and " +
				" fm.ativo = trueValue() " +
				" order by fm.ultimaPostagem desc";
	
		Query q = getSession().createQuery(hql);
		q.setInteger("idForum", idForum);
		
		if (paging != null) {
			Query count = getSession().createQuery("select count(fm.id) from ForumMensagem fm where fm.forum.id = :idForum and " +
					" fm.titulo is not null and fm.topico is null and fm.ativo = trueValue()");
			count.setInteger("idForum", idForum);
	 
			paging.setTotalRegistros(((Long) count.uniqueResult()).intValue());
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		List<ForumMensagem> resultado = null;
		
		for (int i = 0; i < lista.size(); i++) {
			if (resultado == null)
				resultado = new ArrayList<ForumMensagem>();
			
			Object[] colunas = lista.get(i);
			int col = 0;
			
			ForumMensagem fm = new ForumMensagem();
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
			fm.setForum(new Forum());
			fm.getForum().setId((Integer) colunas[col++]);
			fm.getForum().setNivel( String.valueOf( (colunas[col++]) ).charAt(0));
			
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
	public Forum findForumMensagensByIDCurso(int idCursoCoordenador) throws DAOException {
		
			Criteria c = getCriteria(Forum.class);
			c.add(Expression.eq("idCursoCoordenador", idCursoCoordenador) );
			c.addOrder(Order.asc("data"));
			List<Forum> resultado = c.list();
			return  (resultado.size() > 0) ? resultado.get(0) : new Forum();
	}
	
	/**
	 * Todas as mensagens de tópico.
	 * 
	 * @param idMensagemForum
	 * @return
	 * @throws DAOException
	 */
	public ForumMensagem findForumMensagensByID(int idMensagemForum) throws DAOException {
		
			Criteria c = getCriteria(ForumMensagem.class);
			c.add(Expression.eq("id", idMensagemForum) );
			c.addOrder(Order.asc("data"));
			
			return (ForumMensagem) c.uniqueResult();
	}

	/**
	 * Lista de fóruns com nome e descrição dos cursos.
	 * 
	 * @param idCurso
	 * @return
	 */
	public List<Forum> findNomeCursoByID(int idCurso) {
		String sql = "select * from Curso c where c.id_curso = ?";
		@SuppressWarnings("unchecked")
		List<Forum> lista = getJdbcTemplate().query(sql,
				new Object[] { idCurso }, new RowMapper() {
					public Forum mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						Forum f = new Forum();
						f.setTitulo(rs.getString("nome"));
						f.setDescricao(rs.getString("nome"));
						return f;
					}
				});
		return lista;
	}

	/**
	 * Busca os fóruns de curso pelo id do curso.
	 * 
	 * @param idCurso
	 * @return
	 */
	public List<Forum> findForunsDeCursoByIDCurso(int idCurso) {
		String sql = "select f.id_forum, f.descricao, f.titulo, f.id_turma, f.data_criacao, "
				+ "p.nome, (select count(*) from ava.forum_mensagem where id_forum = f.id_forum and id_topico is null) as total, "
				+ "(select data from ava.forum_mensagem fm where fm.id_forum = f.id_forum order "
				+ "by data desc " + BDUtils.limit(1) + ") as data, f.topicos from ava.forum f, comum.usuario u, comum.pessoa p "
				+ "where (f.ativo=trueValue() or f.ativo is null) and f.idcursocoordenador = ? and f.curso = 't' and f.id_usuario = u.id_usuario and "
				+ "u.id_pessoa = p.id_pessoa and f.tipo = " + Forum.TURMA + " order by f.data_criacao desc";
			
		@SuppressWarnings("unchecked")
		List<Forum> lista =  getJdbcTemplate().query(sql, new Object[] { idCurso }, new RowMapper() {
				public Forum mapRow(ResultSet rs, int rowNum) throws SQLException {
					Forum forum = new Forum();
					forum.setId(rs.getInt("id_forum"));
					forum.setDescricao(rs.getString("descricao"));
					forum.setTitulo(rs.getString("titulo"));
					forum.setTurma(new Turma(rs.getInt("id_turma")));
					forum.setData(rs.getTimestamp("data_criacao"));
					forum.setUsuario(new Usuario());
					forum.getUsuario().setPessoa(new Pessoa());
					forum.getUsuario().getPessoa().setNome(rs.getString("nome"));
					forum.setTotalTopicos(rs.getInt("total"));
					forum.setDataUltimaMensagem(rs.getTimestamp("data"));
					forum.setTopicos(rs.getBoolean("topicos"));
					return forum;
				}
			});
		return lista;
	}

	/**
	 * Retorna uma lista com os e-mails dos usuários que participaram de um tópico.
	 * 
	 * @param topico
	 * @return
	 * @throws DAOException
	 */
	public List<String> findEmailsByTopico(ForumMensagem topico) throws DAOException {
		Query q = getSession().createQuery("select distinct fm.usuario.email from " +
				"ForumMensagem fm where fm.topico.id = " + topico.getId() );

		@SuppressWarnings("unchecked")
		List<String> lista = q.list();
		return lista;
	}
	
	/**
	 * Retorna uma lista com os e-mails dos usuários que participaram de um tópico.
	 * 
	 * @param topico
	 * @return
	 * @throws DAOException
	 */
	public List<String> findEmailsComunidadeByTopico(ForumMensagemComunidade topico) throws DAOException {
		Query q = getSession().createQuery("select distinct fm.usuario.email from " +
				"ForumMensagemComunidade fm where fm.topico.id = " + topico.getId() );

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
	public List<String> findEmailsTodosParticipantesForum(ForumMensagem forumMensagem) throws DAOException {
		String sql = "select distinct(u.email) " +
					"from discente d " +
					"inner join curso c on c.id_curso = d.id_curso " +
					"inner join comum.usuario u on u.id_pessoa = d.id_pessoa " +
					"where c.id_curso = ? and d.status in (" +
					StatusDiscente.ATIVO + ", " + StatusDiscente.FORMANDO + ", " + StatusDiscente.GRADUANDO + ", " +
					StatusDiscente.EM_HOMOLOGACAO + ", " + StatusDiscente.DEFENDIDO + ")";
		
		@SuppressWarnings("unchecked")
		List<String> lista =  getJdbcTemplate().query(sql, new Object[] { forumMensagem.getForum().getIdCursoCoordenador() }, new RowMapper() {
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
					"select count(*) from ForumMensagem fm " +
					"where fm.topico.id = ? " +
					"and fm.ativo = trueValue()" +
					"and fm.usuario.id = ?" +
					"and fm.conteudo = ?").setInteger(0, idTopico).setInteger(1, idUsuario).setString(2, conteudo).uniqueResult()).intValue() > 0 );
	}

	/**
	 * Busca todas as mensagens de um fórum com suporte a paginação.
	 * 
	 * @param idForum
	 * @return
	 * @throws DAOException
	 */
	public ArrayList<ForumMensagem> findMensagensByIdForumFiltro(int idForum, String filtro , PagingInformation paging) throws DAOException {
	
		String projecao = " select distinct fm.id_forum_mensagem, fm.titulo, fm.respostas, fm.ultima_postagem, fm.data, "+
						  "	fc.id_forum, fc.nivel, " +
						  "	fm.id_usuario, u.login, u.id_pessoa, p.nome ";
		
		String from = " from ava.forum_mensagem fm " + 
					  " join ava.forum fc on fc.id_forum = fm.id_forum "+
					  " join comum.usuario u on u.id_usuario = fm.id_usuario "+
					  " join comum.pessoa p on p.id_pessoa = u.id_pessoa "+
					  " left join ava.forum_mensagem filho on filho.id_topico = fm.id_forum_mensagem "+
					  " left join comum.usuario uf on uf.id_usuario = filho.id_usuario "+
					  " left join comum.pessoa pf on pf.id_pessoa = uf.id_pessoa ";
	
		String where =  " where fm.id_forum = "+idForum+" and fm.titulo is not null " +
				        " and fm.id_topico is null and fm.ativo = trueValue() ";
	 			
		if ( filtro != null ) {
			where +=  " and ( " + UFRNUtils.toAsciiUpperUTF8("fm.titulo") + " like " + UFRNUtils.toAsciiUpperUTF8("'%" + filtro + "%'") +
					" or " + UFRNUtils.toAsciiUpperUTF8("fm.conteudo") + " like " + UFRNUtils.toAsciiUpperUTF8("'%" + filtro + "%'") +
					" or " + UFRNUtils.toAsciiUpperUTF8("p.nome") + " like " + UFRNUtils.toAsciiUpperUTF8("'%" + filtro + "%'") +
					" or " + UFRNUtils.toAsciiUpperUTF8("filho.titulo") + " like " + UFRNUtils.toAsciiUpperUTF8("'%" + filtro + "%'") +
					" or " + UFRNUtils.toAsciiUpperUTF8("filho.conteudo") + " like " + UFRNUtils.toAsciiUpperUTF8("'%" + filtro + "%'") +
					" or " + UFRNUtils.toAsciiUpperUTF8("pf.nome") + " like " + UFRNUtils.toAsciiUpperUTF8("'%" + filtro + "%'") +
					" ) "; 
		}
		
		String order = " order by fm.ultima_postagem desc ";
		
		String sql = projecao + from + where + order;
		
		Query q = getSession().createSQLQuery(sql);
		
		if (paging != null) {
			Query count = getSession().createSQLQuery("select count(distinct fm.id_forum_mensagem) " + from + where);
	 
			paging.setTotalRegistros(((Number) count.uniqueResult()).intValue());
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		ArrayList<ForumMensagem> resultado = null;
		
		for (int i = 0; i < lista.size(); i++) {
			if (resultado == null)
				resultado = new ArrayList<ForumMensagem>();
			
			Object[] colunas = lista.get(i);
			int col = 0;
			
			ForumMensagem fm = new ForumMensagem();
			fm.setId( (Integer) colunas[col++] );
			fm.setTitulo((String) colunas[col++]);
			fm.setRespostas((Integer) colunas[col++]);
			fm.setUltimaPostagem((Date) colunas[col++]);
			fm.setData((Date) colunas[col++]);
			fm.setForum(new Forum());
			fm.getForum().setId((Integer) colunas[col++]);
			fm.getForum().setNivel( String.valueOf( (colunas[col++]) ).charAt(0));
			fm.setUsuario(new Usuario());
			fm.getUsuario().setId((Integer) colunas[col++]);
			fm.getUsuario().setLogin((String) colunas[col++]);
			fm.getUsuario().setPessoa(new Pessoa());
			fm.getUsuario().getPessoa().setId(((Number) colunas[col++]).intValue());
			fm.getUsuario().getPessoa().setNome((String) colunas[col++]);
			
			resultado.add(fm);
		}
		return resultado;
	}


	/**
	 * Busca todas as mensagens de um programa.
	 * 
	 * @param idPrograma
	 * @return
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public Forum findForumMensagensByIDPrograma(int idPrograma) throws DAOException {
		
		Criteria c = getCriteria(Forum.class);
		c.add(Expression.eq("programaRede.id", idPrograma) );
		c.addOrder(Order.asc("data"));
		List<Forum> resultado = c.list();
		return  (resultado.size() > 0) ? resultado.get(0) : new Forum();
	}
	

	/**
	 * Busca os fóruns de programa pelo id do programa.
	 * 
	 * @param idCurso
	 * @return
	 */
	public List<Forum> findForunsDeProgramaByIDPrograma(int idPrograma) {
		String sql = "select f.id_forum, f.descricao, f.titulo, f.id_turma, f.data_criacao, "
				+ "p.nome, (select count(*) from ava.forum_mensagem where id_forum = f.id_forum and id_topico is null) as total, "
				+ "(select data from ava.forum_mensagem fm where fm.id_forum = f.id_forum order "
				+ "by data desc " + BDUtils.limit(1) + ") as data, f.topicos from ava.forum f, comum.usuario u, comum.pessoa p "
				+ "where (f.ativo=trueValue() or f.ativo is null) and f.id_programa_rede = ? and f.programa = trueValue() and f.id_usuario = u.id_usuario and "
				+ "u.id_pessoa = p.id_pessoa and f.tipo = " + Forum.TURMA + " order by f.data_criacao desc";
			
		@SuppressWarnings("unchecked")
		List<Forum> lista =  getJdbcTemplate().query(sql, new Object[] { idPrograma }, new RowMapper() {
				public Forum mapRow(ResultSet rs, int rowNum) throws SQLException {
					Forum forum = new Forum();
					forum.setId(rs.getInt("id_forum"));
					forum.setDescricao(rs.getString("descricao"));
					forum.setTitulo(rs.getString("titulo"));
					forum.setTurma(new Turma(rs.getInt("id_turma")));
					forum.setData(rs.getTimestamp("data_criacao"));
					forum.setUsuario(new Usuario());
					forum.getUsuario().setPessoa(new Pessoa());
					forum.getUsuario().getPessoa().setNome(rs.getString("nome"));
					forum.setTotalTopicos(rs.getInt("total"));
					forum.setDataUltimaMensagem(rs.getTimestamp("data"));
					forum.setTopicos(rs.getBoolean("topicos"));
					return forum;
				}
			});
		return lista;
	}
}
