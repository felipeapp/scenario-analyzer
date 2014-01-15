/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/03/2011
 *
 */
package br.ufrn.sigaa.ava.forum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeral;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeralMensagem;
import br.ufrn.sigaa.ava.forum.dominio.TipoForum;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO para operações relacionadas ao Fórum Geral.
 * Este DAO é responsável pela busca de fóruns e mensagens,
 * além da busca de informações relacionadas, como a quantidade de mensagens do fórum, etc. 
 *
 * @author David Ricardo
 * @author Ilueny Santos
 * 
 *
 */
public class ForumGeralDao extends GenericSigaaDAO {

	/**
	 * Busca os fóruns de um usuário.
	 * 
	 * @param turma
	 * @return
	 */
	public List<ForumGeral> findByUsuario(UsuarioGeral usuario) {
		String sql = "SELECT f.id_forum, f.descricao, f.titulo, f.data_criacao, u.id_foto, " 
			+ "tf.id_tipo_forum, tf.descricao as descricao_tipo, tf.permite_criar_topico, tf.permite_interromper_topico, " 
			+ "tf.permite_responder_topico, tf.permite_comentar_resposta, tf.permite_remover_topico, "
			+ "p.nome, f.total_topicos, um.id_ultima_mensagem, um.data "
			+ "FROM ava.forum f " +
					"JOIN ava.tipo_forum tf ON f.id_tipo_forum = tf.id_tipo_forum " +
					"JOIN comum.usuario u USING(id_usuario) " +
					"JOIN comum.pessoa p USING(id_pessoa) " +
					"LEFT JOIN ava.forum_mensagem um ON (f.id_ultima_mensagem = um.id_forum_mensagem) " +
					"LEFT JOIN ava.forum_curso_docente fp ON (f.id_forum = fp.id_forum AND fp.ativo = trueValue())  " 
			+ "WHERE f.ativo = trueValue() AND (f.id_usuario = ? OR fp.id_usuario = ? ) AND "
			+ "u.id_pessoa = p.id_pessoa ORDER BY f.titulo ";
		
		@SuppressWarnings("unchecked")
		List<ForumGeral> lista =  getJdbcTemplate().query(sql, new Object[] { usuario.getId(), usuario.getId() }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ForumGeral forum = new ForumGeral();
				forum.setId(rs.getInt("id_forum"));
				forum.setDescricao(rs.getString("descricao"));
				forum.setTitulo(rs.getString("titulo"));
				forum.setTipo(new TipoForum(rs.getInt("id_tipo_forum")));
				forum.getTipo().setDescricao(rs.getString("descricao_tipo"));
				forum.getTipo().setPermiteCriarTopico(rs.getBoolean("permite_criar_topico"));
				forum.getTipo().setPermiteInterromperTopico(rs.getBoolean("permite_interromper_topico"));
				forum.getTipo().setPermiteResponderTopico(rs.getBoolean("permite_responder_topico"));
				forum.getTipo().setPermiteComentarResposta(rs.getBoolean("permite_comentar_resposta"));
				forum.getTipo().setPermiteRemoverTopico(rs.getBoolean("permite_remover_topico"));				
				forum.setDataCadastro(rs.getTimestamp("data_criacao"));
				forum.setUsuario(new Usuario());
				forum.getUsuario().setIdFoto(rs.getInt("id_foto"));
				forum.getUsuario().setPessoa(new Pessoa());
				forum.getUsuario().getPessoa().setNome(rs.getString("nome"));
				forum.setTotalTopicos(rs.getInt("total_topicos"));
				forum.setUltimaMensagem(new ForumGeralMensagem(rs.getInt("id_ultima_mensagem")));
				forum.getUltimaMensagem().setData(rs.getTimestamp("data"));
				return forum;
			}
		});
		return lista;
	}
	
	/**
	 * Busca geral de fóruns cadastrados.
	 * 
	 * @throws DAOException 
	 * @throws HibernateException */
	@SuppressWarnings("unchecked")
	public List<ForumGeral> filter(String titulo) throws HibernateException, DAOException {		
		String projecao = " f.id, f.descricao, f.titulo, f.dataCadastro, f.usuario.pessoa.nome ";

		StringBuffer hql = new StringBuffer( "SELECT " + projecao + " FROM ForumGeral f " +
				" JOIN f.usuario " +
				" JOIN f.usuario.pessoa " +
				" WHERE f.ativo = trueValue() ");
		
		if ( titulo != null ) {
			hql.append(" AND " + UFRNUtils.toAsciiUpperUTF8("f.titulo") + " like "
					+ UFRNUtils.toAsciiUpperUTF8(":titulo"));
		}
		
		hql.append(" ORDER BY f.dataCadastro desc");
		Query q = getSession().createQuery(hql.toString());
		
		if ( titulo != null ) {
			q.setString("titulo", "%" + titulo.toUpperCase() +"%");
		}
		
		return  (List<ForumGeral>) HibernateUtils.parseTo(q.list(), projecao, ForumGeral.class, "f");
	}
	
}
