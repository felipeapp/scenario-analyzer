/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '25/04/2011'
 *
 */
package br.ufrn.sigaa.ava.forum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeral;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeralMensagem;
import br.ufrn.sigaa.ava.forum.dominio.TipoForum;
import br.ufrn.sigaa.ava.forum.relacionamentos.dominio.ForumTurma;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO para operações relacionadas ao Fórum Específico de Turma.
 * Este DAO é responsável pela busca de fóruns e mensagens,
 * além da busca de informações relacionadas, como a quantidade de mensagens do fórum, etc. 
 *
 * 
 * @author Ilueny Santos
 *
 */
public class ForumTurmaDao extends GenericSigaaDAO {
	
	/**
	 * Busca os fóruns da turma informada.
	 * 
	 * @param turma
	 * @return
	 */
	public List<ForumTurma> findByTurma(final Turma turma) {
		String sql = "SELECT ft.id_forum_turma, f.id_forum, f.descricao, f.titulo, tf.id_tipo_forum, tf.descricao as descricao_tipo, f.data_criacao, u.id_foto, "
			+ "p.nome, f.total_topicos, um.id_ultima_mensagem, um.data, u.id_usuario, "
			+ "t.id_turma, t.codigo, "
			+ "ta.id_topico_aula, ta.descricao as descricao_topico_aula "
			+ "FROM ava.forum f " +
					"JOIN ava.forum_turma ft USING(id_forum) " +
					"JOIN ensino.turma t ON ft.id_turma = t.id_turma " +
					"LEFT JOIN ava.topico_aula ta USING(id_topico_aula) " +
					"JOIN ava.tipo_forum tf ON f.id_tipo_forum = tf.id_tipo_forum " +
					"JOIN comum.usuario u ON f.id_usuario = u.id_usuario " +
					"JOIN comum.pessoa p USING(id_pessoa) " +
					"LEFT JOIN ava.forum_mensagem um ON (f.id_ultima_mensagem = um.id_forum_mensagem) "  
			+ "WHERE f.ativo = trueValue() AND (ft.ativo = trueValue()) AND (ft.id_turma = ?) "
			+ "ORDER BY f.data_criacao ";
		
		@SuppressWarnings("unchecked")
		List<ForumTurma> lista =  getJdbcTemplate().query(sql, new Object[] { turma.getId() }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				ForumTurma ft = new ForumTurma(rs.getInt("id_forum_turma"));
				ft.setTurma(turma);
				ft.getTopicoAula().setId(rs.getInt("id_topico_aula"));
				ft.getTopicoAula().setDescricao(rs.getString("descricao_topico_aula"));
				
				ForumGeral forum = new ForumGeral();
				forum.setId(rs.getInt("id_forum"));
				forum.setDescricao(rs.getString("descricao"));
				forum.setTitulo(rs.getString("titulo"));
				forum.setTipo(new TipoForum(rs.getInt("id_tipo_forum")));
				forum.getTipo().setDescricao(rs.getString("descricao_tipo"));
				forum.setDataCadastro(rs.getTimestamp("data_criacao"));
				forum.setUsuario(new Usuario(rs.getInt("id_usuario")));
				forum.getUsuario().setIdFoto(rs.getInt("id_foto"));
				forum.getUsuario().setPessoa(new Pessoa());
				forum.getUsuario().getPessoa().setNome(rs.getString("nome"));
				forum.setTotalTopicos(rs.getInt("total_topicos"));
				forum.setUltimaMensagem(new ForumGeralMensagem(rs.getInt("id_ultima_mensagem")));
				forum.getUltimaMensagem().setData(rs.getTimestamp("data"));				
				ft.setForum(forum);
				return ft;
			}
		});
		return lista;
	}
	
	/**
	 * Busca os fóruns da turma informadade acordo com o acesso.
	 * 
	 * @param turma
	 * @return
	 */
	public List<ForumTurma> findByTurma(final Turma turma, boolean isDocente) {
		String sql = "SELECT ft.id_forum_turma, f.id_forum, f.descricao, f.titulo, tf.id_tipo_forum, tf.descricao as descricao_tipo, f.data_criacao, u.id_foto, "
			+ "p.nome, f.total_topicos, um.id_ultima_mensagem, um.data, u.id_usuario, "
			+ "t.id_turma, t.codigo, "
			+ "ta.id_topico_aula, ta.descricao as descricao_topico_aula "
			+ "FROM ava.forum f " +
					"JOIN ava.forum_turma ft USING(id_forum) " +
					"JOIN ensino.turma t ON ft.id_turma = t.id_turma " +
					"LEFT JOIN ava.topico_aula ta USING(id_topico_aula) " +
					"JOIN ava.tipo_forum tf ON f.id_tipo_forum = tf.id_tipo_forum " +
					"JOIN comum.usuario u ON f.id_usuario = u.id_usuario " +
					"JOIN comum.pessoa p USING(id_pessoa) " +
					"LEFT JOIN ava.forum_mensagem um ON (f.id_ultima_mensagem = um.id_forum_mensagem) "  
			+ "WHERE f.ativo = trueValue() AND (ft.ativo = trueValue()) AND (ft.id_turma = ?) ";
			
			
			if (!isDocente) {
				sql += " AND (ta is NULL OR ta.visivel = trueValue() )";
			}
			
			sql += "ORDER BY f.data_criacao ";
		
		@SuppressWarnings("unchecked")
		List<ForumTurma> lista =  getJdbcTemplate().query(sql, new Object[] { turma.getId() }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				ForumTurma ft = new ForumTurma(rs.getInt("id_forum_turma"));
				ft.setTurma(turma);
				ft.getTopicoAula().setId(rs.getInt("id_topico_aula"));
				ft.getTopicoAula().setDescricao(rs.getString("descricao_topico_aula"));
				
				ForumGeral forum = new ForumGeral();
				forum.setId(rs.getInt("id_forum"));
				forum.setDescricao(rs.getString("descricao"));
				forum.setTitulo(rs.getString("titulo"));
				forum.setTipo(new TipoForum(rs.getInt("id_tipo_forum")));
				forum.getTipo().setDescricao(rs.getString("descricao_tipo"));
				forum.setDataCadastro(rs.getTimestamp("data_criacao"));
				forum.setUsuario(new Usuario(rs.getInt("id_usuario")));
				forum.getUsuario().setIdFoto(rs.getInt("id_foto"));
				forum.getUsuario().setPessoa(new Pessoa());
				forum.getUsuario().getPessoa().setNome(rs.getString("nome"));
				forum.setTotalTopicos(rs.getInt("total_topicos"));
				forum.setUltimaMensagem(new ForumGeralMensagem(rs.getInt("id_ultima_mensagem")));
				forum.getUltimaMensagem().setData(rs.getTimestamp("data"));				
				ft.setForum(forum);
				return ft;
			}
		});
		return lista;
	}

	
	/**
	 * Monta um mapa dos tópicos de aula e cada um dos fóruns relacionados ao tópico.
	 * @param turma
	 * @return
	 * @throws DAOException 
	 */	
	public Map<TopicoAula, List<ForumTurma>> findTopicosByTurma(Turma turma) throws DAOException {
		List <ForumTurma> foruns = findByTurma(turma);
		
		Map <TopicoAula, List <ForumTurma>> result = new HashMap<TopicoAula, List<ForumTurma>>();
		for (ForumTurma f : foruns){
			if (f.getTopicoAula() != null){
				TopicoAula ta = f.getTopicoAula();
				if (result.get(ta) == null) {
					result.put(ta, new ArrayList <ForumTurma> ());
				}						
				result.get(ta).add(f);
			}
		}

		return result;
	}

}
