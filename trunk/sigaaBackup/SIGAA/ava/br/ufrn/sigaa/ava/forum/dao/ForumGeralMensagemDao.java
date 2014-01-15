/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/04/2011
 *
 */
package br.ufrn.sigaa.ava.forum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeral;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeralMensagem;
import br.ufrn.sigaa.ava.forum.dominio.TipoForum;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO para operações relacionada às Mensagens do Fórum Geral.
 * Este DAO é responsável pela busca de mensagens,
 * além da busca de informações relacionadas, como a quantidade de mensagens do fórum, etc. 
 *
 * @author Ilueny Santos
 * @author David Ricardo
 * 
 *
 */
public class ForumGeralMensagemDao extends GenericSigaaDAO {

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
	public List<ForumGeralMensagem> findMensagensByMensagem(int idTopico, String ordenarPor, Boolean ordemAsc, PagingInformation paging) throws DAOException {
		try {
			Criteria c = getCriteria(ForumGeralMensagem.class);
			c.setFetchMode("usuario", FetchMode.JOIN);
			c.add(Expression.like("hierarquia", "%." + idTopico + ".%"));
			
			if (ordemAsc) {
				c.addOrder(Order.asc(ordenarPor));
			}else {
				c.addOrder(Order.desc(ordenarPor));
			}			
			c.add(Expression.eq("ativo", Boolean.TRUE));
			
			if (paging != null) {
				paging.setTotalRegistros(findCountMensagensByTopico(idTopico));
				c.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
				c.setMaxResults(paging.getTamanhoPagina());
			}
			
			List<ForumGeralMensagem> result = c.list();			
			return result;
			
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}

	
	/**
	 * Busca tópicos de um fórum.
	 * 
	 * @param turma
	 * @return
	 */
	public List<ForumGeralMensagem> findTopicosByForum(int idForum) {
		String sql = "SELECT fm.id_forum_mensagem, fm.ativo, fm.id_forum, fm.hierarquia, fm.titulo, fm.total_respostas, fm.id_arquivo, "
			+ "fm.id_ultima_mensagem, um.data, um.id_usuario as id_usuario_ultima_mensagem, p2.nome as nome_ultimo_usuario, " 
			+ "u.id_foto, p.nome, u.id_usuario, " 
			+ "tf.id_tipo_forum, tf.descricao as descricao_tipo, tf.permite_criar_topico, tf.permite_interromper_topico, " 
			+ "tf.permite_responder_topico, tf.permite_comentar_resposta, tf.permite_remover_topico " +
					"FROM ava.forum_mensagem fm " +
					"JOIN ava.forum f ON (f.id_forum = fm.id_forum) " +
					"JOIN ava.tipo_forum tf ON (tf.id_tipo_forum = f.id_tipo_forum) " +					
					"JOIN comum.usuario u ON (u.id_usuario = fm.id_usuario) " +
					"JOIN comum.pessoa p ON (p.id_pessoa = u.id_pessoa) " +
					"LEFT JOIN ava.forum_mensagem um ON (fm.id_ultima_mensagem = um.id_forum_mensagem) " +					
					"LEFT JOIN comum.usuario u2 ON (u2.id_usuario = um.id_usuario) " +
					"LEFT JOIN comum.pessoa p2 ON (p2.id_pessoa = u2.id_pessoa) " +
			 "WHERE fm.ativo=trueValue() AND fm.id_usuario = u.id_usuario AND " +
			 "u.id_pessoa = p.id_pessoa " +
			 "AND fm.id_forum = ? AND fm.id_topico = fm.id_forum_mensagem AND fm.id_mensagem_pai is null " +
			 "ORDER BY fm.data desc";
		@SuppressWarnings("unchecked")
		List<ForumGeralMensagem> lista =  getJdbcTemplate().query(sql, new Object[] { idForum }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ForumGeralMensagem fm = new ForumGeralMensagem(rs.getInt("id_forum_mensagem"));
				fm.setForum(new ForumGeral(rs.getInt("id_forum")));
				fm.getForum().setTipo(new TipoForum(rs.getInt("id_tipo_forum")));
				fm.getForum().getTipo().setDescricao(rs.getString("descricao_tipo"));
				fm.getForum().getTipo().setPermiteCriarTopico(rs.getBoolean("permite_criar_topico"));
				fm.getForum().getTipo().setPermiteInterromperTopico(rs.getBoolean("permite_interromper_topico"));
				fm.getForum().getTipo().setPermiteResponderTopico(rs.getBoolean("permite_responder_topico"));
				fm.getForum().getTipo().setPermiteComentarResposta(rs.getBoolean("permite_comentar_resposta"));
				fm.getForum().getTipo().setPermiteRemoverTopico(rs.getBoolean("permite_remover_topico"));
				fm.setAtivo(rs.getBoolean("ativo"));
				fm.setHierarquia(rs.getString("hierarquia"));
				fm.setTitulo(rs.getString("titulo"));
				fm.setTotalRespostas(rs.getInt("total_respostas"));
				fm.setIdArquivo(rs.getInt("id_arquivo"));
				fm.setUltimaMensagem(new ForumGeralMensagem(rs.getInt("id_ultima_mensagem")));
				fm.getUltimaMensagem().setData(rs.getTimestamp("data"));
				fm.getUltimaMensagem().setUsuario(new Usuario(rs.getInt("id_usuario_ultima_mensagem")));
				fm.getUltimaMensagem().getUsuario().setPessoa(new Pessoa());
				fm.getUltimaMensagem().getUsuario().getPessoa().setNome(rs.getString("nome_ultimo_usuario"));
				fm.setUsuario(new Usuario(rs.getInt("id_usuario")));
				fm.getUsuario().setIdFoto(rs.getInt("id_foto"));
				fm.getUsuario().setPessoa(new Pessoa());
				fm.getUsuario().getPessoa().setNome(rs.getString("nome"));
				return fm;
			}
		});
		return lista;
	}
	
	
	/**
	 * Busca mensagens de um tópico, sem paginação.
	 * 
	 * @param turma
	 * @return
	 */
	public List<ForumGeralMensagem> findMensagensByTopico(int idTopico) {
		String sql = "SELECT fm.id_forum_mensagem, fm.ativo, fm.hierarquia, fm.id_forum, fm.titulo, fm.id_arquivo, "
			+ "fm.conteudo, fm.data, p.nome, fm.id_topico, "
			+ "tf.id_tipo_forum, tf.descricao as descricao_tipo, tf.permite_criar_topico, tf.permite_interromper_topico, " 
			+ "tf.permite_responder_topico, tf.permite_comentar_resposta, tf.permite_remover_topico " 
			+ "FROM ava.forum_mensagem fm " +
				"JOIN ava.forum f ON (f.id_forum = fm.id_forum) " +
				"JOIN ava.tipo_forum tf ON (tf.id_tipo_forum = f.id_tipo_forum) " +					
				"JOIN comum.usuario u ON (u.id_usuario = fm.id_usuario) " +
				"JOIN comum.pessoa p ON (p.id_pessoa = u.id_pessoa) " 
			+ "WHERE fm.ativo=trueValue() AND fm.id_usuario = u.id_usuario AND "
			+ "u.id_pessoa = p.id_pessoa AND fm.id_topico = ? ORDER BY fm.data desc";
		@SuppressWarnings("unchecked")
		List<ForumGeralMensagem> lista =  getJdbcTemplate().query(sql, new Object[] { idTopico}, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ForumGeralMensagem fm = new ForumGeralMensagem(rs.getInt("id_forum_mensagem"));
				fm.setAtivo(rs.getBoolean("ativo"));
				fm.setForum(new ForumGeral(rs.getInt("id_forum")));
				fm.getForum().setTipo(new TipoForum(rs.getInt("id_tipo_forum")));
				fm.getForum().getTipo().setDescricao(rs.getString("descricao_tipo"));
				fm.getForum().getTipo().setPermiteCriarTopico(rs.getBoolean("permite_criar_topico"));
				fm.getForum().getTipo().setPermiteInterromperTopico(rs.getBoolean("permite_interromper_topico"));
				fm.getForum().getTipo().setPermiteResponderTopico(rs.getBoolean("permite_responder_topico"));
				fm.getForum().getTipo().setPermiteComentarResposta(rs.getBoolean("permite_comentar_resposta"));
				fm.getForum().getTipo().setPermiteRemoverTopico(rs.getBoolean("permite_remover_topico"));
				fm.setHierarquia(rs.getString("hierarquia"));
				fm.setTitulo(rs.getString("titulo"));
				fm.setIdArquivo(rs.getInt("id_arquivo"));
				fm.setConteudo(rs.getString("conteudo"));
				fm.setData(rs.getTimestamp("data"));
				fm.setUsuario(new Usuario());
				fm.getUsuario().setPessoa(new Pessoa());
				fm.getUsuario().getPessoa().setNome(rs.getString("nome"));
				fm.setTopico(new ForumGeralMensagem(rs.getInt("id_topico")));
				return fm;
			}
		});
		return lista;
	}

	/**
	 * Busca respostas a mensagem informada.
	 * Se um tópico for informado, retorna todas as mensagens do tópico.
	 * 
	 * @param turma
	 * @return
	 */
	public List<ForumGeralMensagem> findMensagensByMensagem(int idMensagem) {
		String sql = "SELECT fm.id_forum_mensagem, fm.ativo, fm.hierarquia, fm.id_forum, fm.titulo, fm.id_arquivo, "
			+ "fm.conteudo, fm.data, fm.id_usuario, p.nome, fm.id_topico, "
			+ "tf.id_tipo_forum, tf.descricao as descricao_tipo, tf.permite_criar_topico, tf.permite_interromper_topico, " 
			+ "tf.permite_responder_topico, tf.permite_comentar_resposta, tf.permite_remover_topico " 
			+ "FROM ava.forum_mensagem fm " +
				"JOIN ava.forum f ON (f.id_forum = fm.id_forum) " +
				"JOIN ava.tipo_forum tf ON (tf.id_tipo_forum = f.id_tipo_forum) " +					
				"JOIN comum.usuario u ON (u.id_usuario = fm.id_usuario) " +
				"JOIN comum.pessoa p ON (p.id_pessoa = u.id_pessoa) " 
			+ "WHERE fm.ativo = trueValue() AND fm.id_usuario = u.id_usuario AND "
			+ "u.id_pessoa = p.id_pessoa AND (fm.hierarquia like ?) ORDER BY fm.hierarquia ";
		@SuppressWarnings("unchecked")
		List<ForumGeralMensagem> lista =  getJdbcTemplate().query(sql, new Object[] { "%" + idMensagem +"%" }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ForumGeralMensagem fm = new ForumGeralMensagem(rs.getInt("id_forum_mensagem"));
				fm.setAtivo(rs.getBoolean("ativo"));
				fm.setForum(new ForumGeral(rs.getInt("id_forum")));
				fm.getForum().setTipo(new TipoForum(rs.getInt("id_tipo_forum")));
				fm.getForum().getTipo().setDescricao(rs.getString("descricao_tipo"));
				fm.getForum().getTipo().setPermiteCriarTopico(rs.getBoolean("permite_criar_topico"));
				fm.getForum().getTipo().setPermiteInterromperTopico(rs.getBoolean("permite_interromper_topico"));
				fm.getForum().getTipo().setPermiteResponderTopico(rs.getBoolean("permite_responder_topico"));
				fm.getForum().getTipo().setPermiteComentarResposta(rs.getBoolean("permite_comentar_resposta"));
				fm.getForum().getTipo().setPermiteRemoverTopico(rs.getBoolean("permite_remover_topico"));
				fm.setHierarquia(rs.getString("hierarquia"));
				fm.setTitulo(rs.getString("titulo"));
				fm.setIdArquivo(rs.getInt("id_arquivo"));
				fm.setConteudo(rs.getString("conteudo"));
				fm.setData(rs.getTimestamp("data"));
				fm.setUsuario(new Usuario(rs.getInt("id_usuario")));
				fm.getUsuario().setPessoa(new Pessoa());
				fm.getUsuario().getPessoa().setNome(rs.getString("nome"));
				fm.setTopico(new ForumGeralMensagem(rs.getInt("id_topico")));
				return fm;
			}
		});
		return lista;
	}
	
	
	/**
	 * Busca respostas a mensagem informada.
	 * Se um tópico for informado, retorna todas as mensagens do tópico.
	 * 
	 * @param turma
	 * @return
	 */
	public List<ForumGeralMensagem> findMensagensParaRemoverByMensagem(int idMensagem) {
		String sql = "SELECT fm.id_forum_mensagem, fm.ativo, fm.hierarquia "
			+ "FROM ava.forum_mensagem fm " 
			+ "WHERE fm.ativo = trueValue() AND (fm.hierarquia like ?) ";
		@SuppressWarnings("unchecked")
		List<ForumGeralMensagem> lista =  getJdbcTemplate().query(sql, new Object[] { "%" + idMensagem +"%"}, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ForumGeralMensagem fm = new ForumGeralMensagem(rs.getInt("id_forum_mensagem"));
				fm.setAtivo(rs.getBoolean("ativo"));
				fm.setHierarquia(rs.getString("hierarquia"));
				return fm;
			}
		});
		return lista;
	}
	
	
	/**
	 * Busca mensagens e tópicos do fórum informado.
	 * 
	 * @param turma
	 * @return
	 */
	public List<ForumGeralMensagem> findMensagensParaRemoverByForum(int idForum) {
		String sql = "SELECT fm.id_forum_mensagem, fm.ativo "
			+ "FROM ava.forum_mensagem fm " 
			+ "WHERE fm.ativo = trueValue() AND (fm.id_forum = ?) ";
		@SuppressWarnings("unchecked")
		List<ForumGeralMensagem> lista =  getJdbcTemplate().query(sql, new Object[] { idForum }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ForumGeralMensagem fm = new ForumGeralMensagem(rs.getInt("id_forum_mensagem"));
				fm.setAtivo(rs.getBoolean("ativo"));
				return fm;
			}
		});
		return lista;
	}


	/**
	 * Retorna a quantidade de respostas de um tópico.
	 * 
	 * @param idTopico
	 * @return
	 * @throws DAOException
	 */
	public int findCountMensagensByTopico(Integer idTopico) throws DAOException {
		return ((Long) getSession().createQuery("select count(*) from ForumGeralMensagem fm where fm.topico.id = ? and fm.ativo = trueValue()").setInteger(0, idTopico).uniqueResult()).intValue() - 1;
	}

	/**
	 * Retorna a quantidade de respostas de uma mensagem pai.
	 * 
	 * @param idTopico
	 * @return
	 * @throws DAOException
	 */
	public int findCountMensagensByMensagemPai(Integer idMensagemPai) throws DAOException {
		return ((Long) getSession().createQuery("select count(*) from ForumGeralMensagem fm where fm.mensagemPai.id = ? and fm.ativo = trueValue()").setInteger(0, idMensagemPai).uniqueResult()).intValue();
	}
	
	/**
	 * Retorna a quantidade de mensagens de um Fórum.
	 * Considerando todos os tópicos e respostas aos tópicos.
	 * 
	 * @param idForum
	 * @return
	 * @throws DAOException
	 */
	public int findCountMensagensByForum(Integer idForum) throws DAOException {
		return ((Long) getSession().createQuery("select count(*) from ForumGeralMensagem fm where fm.forum.id = ? and fm.ativo = trueValue()").setInteger(0, idForum).uniqueResult()).intValue();
	}
	
	/**
	 * Retorna a quantidade de tópicos em um fórum.
	 * 
	 * @param idForum
	 * @return
	 * @throws DAOException
	 */
	public int findCountTopicosByForum(Integer idForum) throws DAOException {
		return ((Long) getSession().createQuery("select count(*) from ForumGeralMensagem fm where fm.forum.id = ? and fm.ativo = trueValue() and fm.topico.id = fm.id and fm.mensagemPai is null").setInteger(0, idForum).uniqueResult()).intValue();
	}

	
	/**
	 * Retorna a quantidade de tópicos em um fórum de um usuário específico.
	 * 
	 * @param idForum
	 * @return
	 * @throws DAOException
	 */
	public int findCountTopicosByForum(Integer idForum, Integer idUsuario) throws DAOException {
		Query q = getSession().createQuery("select count(*) from ForumGeralMensagem fm where fm.forum.id = ? and fm.usuario.id = ? " +
				" and fm.ativo = trueValue() and fm.topico.id = fm.id and fm.mensagemPai is null");
		q.setInteger(0, idForum);
		q.setInteger(1, idUsuario);		
		return ((Long) q.uniqueResult()).intValue(); 
	}

	
	/**
	 * Retorna a quantidade de respostas do fórum.
	 * Não considera os tópicos cadastrados.
	 * 
	 * @param id_forum
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ForumGeralMensagem> findMensagesRespostasByForum(int idForum) throws DAOException {
		@SuppressWarnings("unchecked")
		Collection<ForumGeralMensagem> lista = getSession().createQuery("from ForumGeralMensagem fm where fm.forum.id = ? and fm.mensagemPai not is null and fm.ativo = trueValue()").setInteger(0, idForum).list(); 
		return lista;
	}
	
	/**
	 * Retorna uma lista com os e-mails dos usuários que participaram de um tópico.
	 * 
	 * @param topico
	 * @return
	 * @throws DAOException
	 */
	public List<String> findEmailsByTopico(ForumGeralMensagem topico) throws DAOException {
		Query q = getSession().createQuery("select distinct fm.usuario.email from ForumGeralMensagem fm where fm.topico.id = " + topico.getId() );
		@SuppressWarnings("unchecked")
		List<String> lista = q.list();
		return lista;
	}

	
	/**
	 * Retorna uma lista com os e-mails dos usuários que participaram de uma turma.
	 * 
	 * @param topico
	 * @return
	 * @throws DAOException
	 */
	public List<String> findEmailsByTurma(Turma turma) throws DAOException {
		String sql = "select distinct(u.email) from discente d " +
						"inner join ensino.matricula_componente mc on mc.id_discente = d.id_discente " +
						"inner join comum.usuario u on u.id_pessoa = d.id_pessoa " +
					 "where mc.id_turma = ? " +
					 "and mc.id_situacao_matricula = " + SituacaoMatricula.MATRICULADO.getId() + " " +
					 "and d.status in " + UFRNUtils.gerarStringIn(StatusDiscente.getStatusComVinculo());

		@SuppressWarnings("unchecked")
		List<String> lista =  getJdbcTemplate().query(sql, new Object[] { turma.getId() }, new RowMapper() {
			public Object mapRow(ResultSet rs, int row) throws SQLException {
				String emailDiscentes = rs.getString("email");
				return emailDiscentes;
			}
		});
		return lista;
	}

	
	/**
	 * Retorna a última resposta dada a um tópico.
	 * 
	 * @param topico
	 * @return
	 * @throws DAOException
	 */
	public ForumGeralMensagem findUltimaMensagemByTopico(ForumGeralMensagem topico) throws DAOException {
		String projecao = "id, titulo, conteudo, data";
		Query q = getSession().createQuery("select "+ projecao +" from ForumGeralMensagem fm where fm.topico.id = " + topico.getId() + " order by data desc limit 1");
		@SuppressWarnings("unchecked")
		Collection<ForumGeralMensagem> lista = HibernateUtils.parseTo(q.list(), projecao, ForumGeralMensagem.class);
		if(ValidatorUtil.isNotEmpty(lista)) {
			return lista.iterator().next();
		}
		return new ForumGeralMensagem();
	}
	
	/**
	 * Retorna a última resposta dada a um fórum.
	 * 
	 * @param topico
	 * @return
	 * @throws DAOException
	 */
	public ForumGeralMensagem findUltimaMensagemByForum(ForumGeral forum) throws DAOException {
		String projecao = "id, titulo, conteudo, data";
		Query q = getSession().createQuery("select "+ projecao +" from ForumGeralMensagem fm where fm.forum.id = " + forum.getId() + " order by data desc limit 1");
		@SuppressWarnings("unchecked")
		Collection<ForumGeralMensagem> lista = HibernateUtils.parseTo(q.list(), projecao, ForumGeralMensagem.class);
		if(ValidatorUtil.isNotEmpty(lista)) {
			return lista.iterator().next();
		}
		return new ForumGeralMensagem();
	}

	
	/**
	 * Retorna a quantidade de mensagens do tópico de um usuário específico.
	 * 
	 * @param idForum
	 * @return
	 * @throws DAOException
	 */
	public int findCountMensagensByTopico(Integer idTopico, Integer idUsuario) throws DAOException {
		Query q = getSession().createQuery("select count(*) from ForumGeralMensagem fm where fm.usuario.id = ? and fm.topico.id = ? " +
				" and fm.ativo = trueValue() and fm.topico.id != fm.id and fm.mensagemPai is not null");
		q.setInteger(0, idUsuario);		
		q.setInteger(1, idTopico);
		return ((Long) q.uniqueResult()).intValue(); 
	}

	
}