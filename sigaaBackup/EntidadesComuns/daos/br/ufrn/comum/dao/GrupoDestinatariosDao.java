package br.ufrn.comum.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static org.hibernate.criterion.Restrictions.eq;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.Unidade;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.comum.dominio.notificacoes.GrupoDestinatarios;
import br.ufrn.comum.dominio.notificacoes.ParametroGrupo;
import br.ufrn.comum.dominio.notificacoes.UsuarioGrupoDestinatarios;

/**
 * DAO para consultas relacionadas a Grupos de Destinatários de Mensagens
 * 
 * @author Ricardo Wendell
 */
@Component @Scope("session")
public class GrupoDestinatariosDao extends GenericSharedDBDao {

	/**
	 * RowMapper que constrói um objeto Destinatário a partir das colunas retornadas
	 */
	private RowMapper destinatarioMapper = new RowMapper() {
		public Object mapRow(ResultSet rs, int row) throws SQLException {
			Destinatario d = new Destinatario();
			
			try {
				String email = rs.getString("email");
				if (email != null) {
					d.setEmail(email);
				}
			} catch (Exception e) {}
			
			try {
				int idUsuario = rs.getInt("id_usuario");
				if (idUsuario != 0) {
					d.setUsuario(new UsuarioGeral(idUsuario));
					d.getUsuario().setEmail( d.getEmail() );
				}
			} catch (Exception e) {}

			
			try {
				String nome = rs.getString("nome");
				if (nome != null) {
					d.setNome(nome);
				}
			} catch (Exception e) {}
			return d;
		}
	};
	
	/**
	 * RowMapper que constrói os dados de um destinatário para exibição da lista de um grupo
	 */
	private RowMapper detalhesDestinatarioMapper = new RowMapper() {
		public Object mapRow(ResultSet rs, int row) throws SQLException {
			UsuarioGeral usuario = new UsuarioGeral();
			usuario.setLogin(rs.getString("login"));
			usuario.getPessoa().setNome(rs.getString("nome_usuario"));
			usuario.setEmail(rs.getString("email"));
			usuario.setId(rs.getInt("id_usuario"));
			
			Unidade unidade = new Unidade();
			unidade.setNome(rs.getString("nome_unidade"));
			unidade.setCodigo(rs.getLong("codigo_unidade"));
			usuario.setUnidade(unidade);
			return new Destinatario(usuario);
		}
	};
	
	
	/**
	 * Busca os grupos de destinatários que podem ser utilizados para o envio de memorandos circulares.
	 * 
	 * @return
	 */
	public List<GrupoDestinatarios> findAllGruposMemorandoCircular() {
		return findAllGruposMemorandoCircular(null);
	}

	/**
	 * Busca os grupos de destinatários que podem ser utilizados para o envio de memorandos circulares.
	 *  
	 * @param descricao
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<GrupoDestinatarios> findAllGruposMemorandoCircular(String descricao) {
		
		String sql = "SELECT gd.*, s.descricao AS nome FROM comum.grupo_destinatarios gd " +
						"INNER JOIN comum.sistema s ON s.id = gd.id_sistema " +
					 "WHERE gd.memorando_circular = trueValue() AND gd.ativo = trueValue() " +
					 	((descricao != null) ? " AND gd.descricao LIKE ? " : "") +
					 "ORDER BY gd.descricao";
		
		
		List<Object> params = new ArrayList<Object>();
		
		if(descricao != null) {
			params.add("%" + descricao + "%");
		}
		
		return getJdbcTemplate().query(sql, 
									   params.toArray(),
										new RowMapper() {
											public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
												GrupoDestinatarios g = new GrupoDestinatarios();
												g.setId(rs.getInt("id"));
												g.setDescricao(rs.getString("descricao"));
												g.setSqlDestinatarios(rs.getString("sql_destinatarios"));
												g.setSistema(new Sistema(rs.getInt("id_sistema")));
												g.getSistema().setNome(rs.getString("nome"));
												g.setAtivo(true);
												g.setMemorandoCircular(true);
												return g;
											}
										});
	}
	
	/**
	 * Busca os grupos de destinatários de um determinado memorando circular.
	 * 
	 * @author Weinberg Souza
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<GrupoDestinatarios> findGruposMemorandoCircular(int idMemorando) {

		// Ids dos grupos associados ao memorando.
		List<Integer> idsGrupoDestinatario = getJdbcTemplate(Sistema.SIPAC).query("SELECT gd.id_grupo_destinatarios FROM protocolo.grupo_documento gd " +
																				  "WHERE gd.id_documento = ? ",
																				  new Object[] { idMemorando },
																				  new RowMapper() {
																					public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
																						Integer idGrupoDestinatario = rs.getInt("id_grupo_destinatarios");
																						return idGrupoDestinatario;
																					}
																				  });
		if( idsGrupoDestinatario != null && !idsGrupoDestinatario.isEmpty() ){
		
			// Populando informações dos grupos.
			return getJdbcTemplate().query("SELECT gd.*, s.descricao AS nome FROM comum.grupo_destinatarios gd " +
										   "INNER JOIN comum.sistema s ON s.id = gd.id_sistema " +
										   "WHERE gd.memorando_circular = trueValue() AND gd.ativo = trueValue() " +
										   "AND gd.id IN " + UFRNUtils.gerarStringIn(idsGrupoDestinatario) +
										   "ORDER BY gd.descricao",
										   new RowMapper() {
											public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
												GrupoDestinatarios g = new GrupoDestinatarios();
												g.setId(rs.getInt("id"));
												g.setDescricao(rs.getString("descricao"));
												g.setSqlDestinatarios(rs.getString("sql_destinatarios"));
												g.setSistema(new Sistema(rs.getInt("id_sistema")));
												g.getSistema().setNome(rs.getString("nome"));
												g.setAtivo(true);
												g.setMemorandoCircular(true);
												return g;
											}
										  });
		}else{
			return new ArrayList<GrupoDestinatarios>();
		}

	}
	
	/**
	 * Busca os grupos de destinatários de um determinado usuário, dentre todos os grupos de memorando circular.
	 * 
	 * @param idUsuario
	 * @return
	 */
	public List<GrupoDestinatarios> findGruposMemorandoCircularByUsuario(int idUsuario) {
		return findGruposMemorandoCircularByUsuario(idUsuario, null, null);
	}

	/**
	 * Busca os grupos de destinatários de um determinado usuário, dentre todos os grupos de memorando circular.
	 * 
	 * @param idUsuario
	 * @param descricaoGrupo
	 * @return
	 */
	public List<GrupoDestinatarios> findGruposMemorandoCircularByUsuario(int idUsuario, String descricaoGrupo) {
		return findGruposMemorandoCircularByUsuario(idUsuario, null, descricaoGrupo);
	}

	/**
	 * Busca os grupos de destinatários de um determinado usuário, dentre todos os grupos de memorando circular.
	 * 
	 * @param idUsuario
	 * @param grupos
	 * @return
	 */
	public List<GrupoDestinatarios> findGruposMemorandoCircularByUsuario(int idUsuario, List<GrupoDestinatarios> grupos) {
		return findGruposMemorandoCircularByUsuario(idUsuario, grupos, null);
	}
	
	/**
	 * Busca os grupos de destinatários de um determinado usuário, dentre os grupos de memorando circular informados.
	 * Caso não sejam informados os grupos, são considerados todos os grupos de memorando circular.
	 * 
	 * @param idUsuario
	 * @param grupos
	 * @param descricaoGrupo
	 * @return
	 */
	public List<GrupoDestinatarios> findGruposMemorandoCircularByUsuario(int idUsuario, List<GrupoDestinatarios> grupos, String descricaoGrupo) {
		
		if(ValidatorUtil.isEmpty(grupos)) {
			// Todos os grupos
			grupos = findAllGruposMemorandoCircular(descricaoGrupo);
		}
		
		// Verificando grupos que o usuário pertence
		List<GrupoDestinatarios> gruposUsuario = new ArrayList<GrupoDestinatarios>(); 
		
		if(!ValidatorUtil.isEmpty(grupos)) {
			for(GrupoDestinatarios grupo : grupos) {
				if(Sistema.isSistemaAtivo(grupo.getSistema().getId())) {
					Boolean usuarioPertence = (Boolean) new JdbcTemplate(getDataSource(grupo.getSistema().getId())).queryForObject(getSQLUsuarioPertenceGrupo(idUsuario, grupo.getSqlDestinatarios()), Boolean.class);
					
					if(usuarioPertence) {
						gruposUsuario.add(grupo);
					}
				}
			}
		}
		
		return gruposUsuario;
	}
	
	/**
	 * Identifica se um usuário está em um grupo de destinatários.
	 */
	public boolean verificaUsuarioGrupo(int idUsuario, int idGrupoDestinatarios) {
		try {
			GrupoDestinatarios grupo = (GrupoDestinatarios) getJdbcTemplate().queryForObject("select id, id_sistema, sql_destinatarios from comum.grupo_destinatarios where id = ?", 
					new Object[] { idGrupoDestinatarios }, new RowMapper() {
				@Override
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					GrupoDestinatarios grupo = new GrupoDestinatarios();
					grupo.setId(rs.getInt("id"));
					grupo.setSistema(new Sistema(rs.getInt("id_sistema")));
					grupo.setSqlDestinatarios(rs.getString("sql_destinatarios"));
					return grupo;
				}
			});
			
			return verificaUsuarioGrupo(idUsuario, grupo);
		} catch(EmptyResultDataAccessException e) {
			return false;
		}
	}
	
	/**
	 * Busca os grupos de destinatários de um determinado usuário, dentre os grupos de memorando circular informados.
	 * Caso não sejam informados os grupos, são considerados todos os grupos de memorando circular.
	 * 
	 * @param idUsuario
	 * @param grupos
	 * @param descricaoGrupo
	 * @return
	 */
	public boolean verificaUsuarioGrupo(int idUsuario, GrupoDestinatarios grupo) {
		try {
			return  (Boolean) new JdbcTemplate(getDataSource(grupo.getSistema().getId())).
				queryForObject(getSQLUsuarioPertenceGrupo(idUsuario, grupo.getSqlDestinatarios()), Boolean.class);
		} catch(EmptyResultDataAccessException e) {
			return false;
		}
	}

	/**
	 * Método utilizado para verificar se um usuário pertence a um determinado grupo de destinatários.
	 * sqlGrupo deve sempre retornar 'id_usuario'!
	 * 
	 * @author Weinberg Souza
	 * @param idUauario
	 * @param sqlGrupo
	 * @return
	 */
	private String getSQLUsuarioPertenceGrupo(int idUsuario, String sqlGrupo) {
		return "SELECT EXISTS( " +
					"SELECT id_usuario FROM ( " + sqlGrupo + " ) AS grupo" +
						" WHERE id_usuario = " + idUsuario +
				")";
	}
	
	/**
	 * Busca todos os grupos de destinatários
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<GrupoDestinatarios> findAll() throws DAOException {
		String hql = "select g FROM GrupoDestinatarios g left join fetch g.subSistema ss " +
			" where g.ativo = trueValue() " +
			" order by g.sistema.nome, ss.nome, g.descricao";
		return getSession().createQuery(hql).list();
	}
	
	/**
	 *  Verifica se a consulta informada é válida
	 */
	@SuppressWarnings("unchecked")
	public void testarConsultaDestinatarios(String sqlDestinatarios, List<ParametroGrupo> parametros, Sistema sistema) throws DAOException {
	
		if (sqlDestinatarios == null) {
			throw new DAOException("A consulta de destinatários não foi informada");
		}
			
		List<String> params = new ArrayList<String>();
		List<Integer> types = new ArrayList<Integer>();
		if (!isEmpty(parametros)) {
			for (ParametroGrupo pg : parametros) {
				params.add(pg.getValorDefault());
				types.add(pg.getTipo());
			}
		}
			
		// Executar a consulta para verificar erros de sintaxe
		Map<String, Object> result = null;
		
		try {
			result = getJdbcTemplate(sistema).queryForMap(
					sqlDestinatarios + BDUtils.limit(1), params.toArray(),
					ArrayUtils.toPrimitive(types.toArray(new Integer[types.size()])));
		} catch (BadSqlGrammarException e) {
			throw new DAOException("Um erro de sintaxe foi encontrado na consulta dos destinatários", e);
		} catch(EmptyResultDataAccessException e) {
			throw new DAOException("Não foram encontrados destinatários para a consulta informada", e);
		} catch (DataAccessException e) {
			throw new DAOException("Ocorreu um erro ao se testar a consulta: " + e.getCause().getMessage(), e);
		}
			
		// Percorrer os resultados e verificar os campos necessários
		Integer idUsuario = (Integer) result.get("id_usuario");
		String email = (String) result.get("email");
			
		// Verificar se os campos necessários foram encontrados
		if (idUsuario == null && email == null) {
			throw new DAOException("Não foram encontrados os campos necessários para o envio de notificações");
		}
			
	}

	/**
	 * Busca os destinatários de um grupo, seja ele definido por um papel no sistema ou por uma consulta
	 * personalizada. <br>
	 * Opcionalmente pode ser requisitado os detalhes do destinatário para exibição em uma relação.
	 * 
	 * @param grupo
	 * @return
	 * @throws DAOException 
	 */
	public Collection<? extends Destinatario> findDestinatarios(GrupoDestinatarios grupo, List<ParametroGrupo> parametros, boolean detalhesDestinatarios) throws DAOException {
		Collection<Destinatario> destinatarios = new ArrayList<Destinatario>();
		
		if (grupo.getTipo() == GrupoDestinatarios.PAPEL) {
			PermissaoDAO permissaoDao = new PermissaoDAO();
			Collection<UsuarioGeral> usuarios = permissaoDao.findByPapel(grupo.getPapel());
			for (UsuarioGeral usuario : usuarios) {
				destinatarios.add( new Destinatario(usuario) );
			}
		} else if(grupo.getTipo() == GrupoDestinatarios.CONSULTA_CADASTRADA) {
			destinatarios = findDestinatariosByConsulta(
							grupo.getSqlDestinatarios(), parametros, 
							grupo.getSistema()
							, detalhesDestinatarios);
		}
		
		return destinatarios;
	}

	/**
	 * Busca todos os destinatários de um grupo, seja ele definido por um papel no sistema ou por uma consulta
	 * personalizada
	 * 
	 * @param grupo
	 * @return
	 * @throws DAOException
	 */
	public Collection<? extends Destinatario> findDestinatarios(GrupoDestinatarios grupo, List<ParametroGrupo> parametros) throws DAOException {
		return findDestinatarios(grupo, parametros, true);
	}
	
	/**
	 * Busca dos destinatários a partir de uma consulta personalizada
	 * 
	 * @param sqlDestinatarios
	 * @param sistema
	 * @param detalhesDestinatarios 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Collection<Destinatario> findDestinatariosByConsulta(String sqlDestinatarios, List<ParametroGrupo> parametros, Sistema sistema, boolean detalhesDestinatarios) {
		
		List<String> params = new ArrayList<String>();
		List<Integer> types = new ArrayList<Integer>();
		if (!isEmpty(parametros)) {
			for (ParametroGrupo pg : parametros) {
				if (isEmpty(pg.getValor())) params.add(pg.getValorDefault());
				else params.add(pg.getValor());
				types.add(pg.getTipo());
			}
		}
		
		RowMapper rowMapper;
		
		if (detalhesDestinatarios) {
			sqlDestinatarios =
					"SELECT " +
					"	distinct p.nome as nome_usuario, un.nome as nome_unidade, un.codigo_unidade, " +
					"	un.id_gestora, us.login, us.email, us.id_usuario " +
					"FROM " +
					"	comum.usuario us " +
					"	JOIN comum.pessoa p using(id_pessoa) " +
					"	LEFT JOIN comum.unidade un using(id_unidade) "  +
					"	JOIN ( " + sqlDestinatarios + ") as consulta on (consulta.id_usuario = us.id_usuario) " +
					"WHERE us.tipo != 9 " +
					"ORDER BY p.nome";
			rowMapper = detalhesDestinatarioMapper;
		} else {
			rowMapper = destinatarioMapper;
		}
		
		return getJdbcTemplate(sistema).query(sqlDestinatarios, params.toArray(), ArrayUtils.toPrimitive(types.toArray(new Integer[types.size()])), rowMapper);
	}

	/**
	 * Busca os grupos de destinatários que podem ser utilizados para a exibição
	 * de telas de aviso após o logon.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<GrupoDestinatarios> findAllGruposTelaAviso() {
		return getJdbcTemplate().query("select * from comum.grupo_destinatarios where tela_aviso_logon = trueValue() and ativo = trueValue()", new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				GrupoDestinatarios g = new GrupoDestinatarios();
				g.setId(rs.getInt("id"));
				g.setDescricao(rs.getString("descricao"));
				g.setSqlDestinatarios(rs.getString("sql_destinatarios"));
				g.setAtivo(true);
				g.setMemorandoCircular(true);
				return g;
			}
		});
	}

	/**
	 * Retorna os possíveis valores para o parâmetro passado como argumento.
	 * Os valores serão utilizados para preencher um combobox na hora de enviar mensagens para os grupos de destinatários.
	 * 
	 * @param param
	 * @param usuarioLogado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> findValoresParametro(ParametroGrupo param, UsuarioGeral usuarioLogado) {
		if (param.isRestricaoUsuario()) {
			try {
				// Está nos usuários com acesso irrestrito
				getJdbcTemplate(Database.getInstance().getComumDs()).queryForInt(param.getSelectRestricaoUsuario(), new Object[] { usuarioLogado.getId() });
				return (Map<String, String>) getJdbcTemplate(param.getGrupo().getSistema()).query(param.getSelectCombo(), MAP_EXTRACTOR);
			} catch(EmptyResultDataAccessException e) {
				// Não está nos usuários com acesso irrestrito
				return (Map<String, String>) getJdbcTemplate(param.getGrupo().getSistema()).query(param.getSelectComboRestrito(), new Object[] { usuarioLogado.getId() }, MAP_EXTRACTOR);
			}
		} else {
			return (Map<String, String>) getJdbcTemplate(param.getGrupo().getSistema()).query(param.getSelectCombo(), MAP_EXTRACTOR);
		}
	}

	/**
	 * Retorna o número de destinatários de um grupo de acordo com o conjunto de
	 * parâmetros passado como argumento para o método.
	 * 
	 * @param sqlDestinatarios
	 * @param parametros
	 * @param sistema
	 * @return
	 */
	public int countDestinatariosGrupo(String sqlDestinatarios, List<ParametroGrupo> parametros, Sistema sistema) {
		List<String> params = new ArrayList<String>();
		List<Integer> types = new ArrayList<Integer>();
		if (!isEmpty(parametros)) {
			for (ParametroGrupo pg : parametros) {
				if (isEmpty(pg.getValor())) params.add(pg.getValorDefault());
				else params.add(pg.getValor());
				types.add(pg.getTipo());
			}
		}
		
		return getJdbcTemplate(sistema).queryForInt("select count(distinct q.id_usuario) from (" + sqlDestinatarios + ") as q", params.toArray(), ArrayUtils.toPrimitive(types.toArray(new Integer[types.size()])));
	}
	
	@Override
	public JdbcTemplate getJdbcTemplate(Sistema sistema) {
		return new JdbcTemplate(getDataSource(sistema.getId()));
	}

	/**
	 * Remove os parâmetros de um grupo. Utilizado na atualização
	 * de grupos de destinatários, para que os grupos apagados sejam removidos
	 * do banco de dados para dar lugar aos novos.
	 * 
	 * @param grupo
	 */
	public void removerParametros(GrupoDestinatarios grupo) {
		getJdbcTemplate().update("delete from comum.parametro_grupo where id_grupo_destinatarios = ?", new Object[] { grupo.getId() });
	}
		
	/**
	 * Retorna os grupos de destinatários para os quais o usuário informado tem permissão de envio de comunicação.
	 * 
	 * @see UsuarioGrupoDestinatarios
	 * @param idUsuario
	 * @return
	 * @throws DAOException
	 */
	public Collection<GrupoDestinatarios> findGruposPermitidosByUsuario(int idUsuario) throws DAOException {
		return findGruposPermitidosByUsuario(idUsuario, null);
	}
	
	/**
	 * Retorna os grupos de destinatários para os quais o usuário informado tem permissão de envio de comunicação.
	 * Considera a descrição do grupo.
	 * 
	 * @see UsuarioGrupoDestinatarios
	 * @param idUsuario
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<GrupoDestinatarios> findGruposPermitidosByUsuario(int idUsuario, String descricao) throws DAOException {
		
		String hql = "SELECT ugd.grupoDestinatarios FROM UsuarioGrupoDestinatarios ugd " +
						"WHERE ugd.usuario IS NOT NULL AND ugd.usuario.id = :idUsuario " +
							"AND ugd.ativo = trueValue() AND ugd.grupoDestinatarios.ativo = trueValue() AND ugd.grupoDestinatarios.memorandoCircular = trueValue() " +
							((descricao != null) ? " AND ugd.grupoDestinatarios.descricao LIKE :descricao " : " ") +	
						"ORDER BY ugd.grupoDestinatarios.descricao";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idUsuario", idUsuario);
		if(descricao != null) {
			q.setString("descricao", "%" + descricao + "%");
		}
		
		return q.list();
	}

	/**
	 * Busca a permissão para envio de notificações por usuário e grupo de destinatários. Retorna
	 * null caso não exista.
	 * @param usuario
	 * @param grupo
	 * @return
	 * @throws DAOException
	 */
	public UsuarioGrupoDestinatarios buscarPermissaoUsuarioGrupo(UsuarioGeral usuario, GrupoDestinatarios grupo) throws DAOException {
		Criteria c = getCriteria(UsuarioGrupoDestinatarios.class);
		c.add(eq("usuario", usuario))
			.add(eq("grupoDestinatarios", grupo))
			.add(eq("ativo", true));
		return (UsuarioGrupoDestinatarios) c.uniqueResult();
	}

	/**
	 * Busca a permissão para envio de notificações por usuário e papel. Retorna
	 * null caso não exista.
	 * @param usuario
	 * @param papel
	 * @return
	 * @throws DAOException
	 */
	public UsuarioGrupoDestinatarios buscarPermissaoNotificacaoUsuarioPapel(UsuarioGeral usuario, Papel papel) throws DAOException {
		Criteria c = getCriteria(UsuarioGrupoDestinatarios.class);
		c.add(eq("usuario", usuario)).add(eq("papel", papel));
		return (UsuarioGrupoDestinatarios) c.uniqueResult();
	}

	
	
}
