/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 29/10/2007
 */
package br.ufrn.arq.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Permissao;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.comum.dominio.TipoUsuario;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.dominio.UsuarioUnidade;

/**
 * DAO para busca de permissoes para os usuários dos sistemas.
 *
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@SuppressWarnings("unchecked")
public class PermissaoDAO extends GenericSharedDBDao {

	private static final String SQL =
		"SELECT " +
		"	pe.id as id_permissao, " +
		"	pe.data_expiracao, " +
		"	pe.data_atribuicao, " +
		"	pe.id_unidade_papel as id_unidade_papel, " +
		"	pa.id as id_papel, " +
		"	pa.descricao, " +
		"	pa.nome, " +
		"	pa.restrito, " +
		"	pa.exige_unidade, " +
		"	pa.id_sistema, " +
		"	si.id as id_sistema, " +
		"	si.descricao as nome_sistema, " +
		"	ss.id as id_subsistema, " +
		"	ss.nome as nome_subsistema, " +
		"	ss.link_entrada as link_subsistema, " +
		"	pe.id_atribuidor, " +
		"	a.login as login_atribuidor, " +
		"	u.id_usuario, " +
		"	u.login, " +
		"	p.nome as nome_usuario, " +
		"	un.codigo_unidade, " +
		"	un.nome as nome_unidade, " +
		"	un.sigla as sigla_unidade, " +
		"	pe.autorizada, " +
		"	pe.numero_chamado, " +
		"	pe.motivo, " +
		"	unpa.codigo_unidade AS codigo_unidade_papel, " +
		"	unpa.nome AS nome_unidade_papel, " +
		"	unpa.sigla AS sigla_unidade_papel " +
		"FROM " +
		"	comum.papel pa, " +
		"	comum.permissao pe " +
		"		LEFT JOIN comum.usuario a on (pe.id_atribuidor = a.id_usuario) " +
		"		LEFT JOIN comum.unidade unpa ON (pe.id_unidade_papel = unpa.id_unidade ) ," +
		"	comum.subsistema ss, " +
		"	comum.usuario u, " +
		"	comum.pessoa p, " +
		"	comum.unidade un, " +
		"	comum.sistema si " +
		"WHERE " +
		"	pa.id = pe.id_papel " +
		"	AND pa.id_subsistema = ss.id " +
		"	AND pe.id_usuario = u.id_usuario " +
		"	AND pa.id_sistema = si.id " +
		"	AND u.id_pessoa = p.id_pessoa " +
		"	AND u.id_unidade = un.id_unidade ";

	RowMapper papelMapper = new RowMapper() {
		public Object mapRow(ResultSet rs, int row) throws SQLException {
			Papel p = new Papel();
			p.setId(rs.getInt("id_papel"));
			p.setNome(rs.getString("nome"));
			p.setDescricao(rs.getString("descricao"));
			p.setRestrito(rs.getBoolean("restrito"));
			p.setExigeUnidade(rs.getBoolean("exige_unidade"));
			p.setSistema(new Sistema());
			p.getSistema().setId(rs.getInt("id_sistema"));
			p.getSistema().setNome(rs.getString("nome_sistema"));
			p.setSubSistema(new SubSistema());
			p.getSubSistema().setId(rs.getInt("id_subsistema"));
			p.getSubSistema().setNome(rs.getString("nome_subsistema"));
			p.getSubSistema().setLink(rs.getString("link_subsistema"));
			p.getSubSistema().setSistema(new Sistema());
			p.getSubSistema().getSistema().setId(rs.getInt("id_sistema"));
			p.getSubSistema().getSistema().setNome(rs.getString("nome_sistema"));
			return p;
		}
	};

	RowMapper permissaoMapper = new RowMapper() {

		public Object mapRow(ResultSet rs, int row) throws SQLException {
			Permissao p = new Permissao();
			p.setId(rs.getInt("id_permissao"));
			p.setDataExpiracao(rs.getDate("data_expiracao"));
			p.setDataCadastro(rs.getDate("data_atribuicao"));
			p.setSistema((rs.getInt("id_sistema")));
			int idUnidadePapel = rs.getInt("id_unidade_papel");
			if(idUnidadePapel > 0) {
				p.setUnidadePapel(new UnidadeGeral(idUnidadePapel));
				p.getUnidadePapel().setCodigo(rs.getLong("codigo_unidade_papel"));
				p.getUnidadePapel().setNome(rs.getString("nome_unidade_papel"));
				p.getUnidadePapel().setSigla(rs.getString("sigla_unidade_papel"));
			}
			p.setPapel(new Papel());
			p.getPapel().setId(rs.getInt("id_papel"));
			p.getPapel().setNome(rs.getString("nome"));
			p.getPapel().setDescricao(rs.getString("descricao"));
			p.getPapel().setRestrito(rs.getBoolean("restrito"));
			p.getPapel().setExigeUnidade(rs.getBoolean("exige_unidade"));
			p.getPapel().setSistema(new Sistema());
			p.getPapel().getSistema().setId((rs.getInt("id_sistema")));
			p.getPapel().getSistema().setNome(rs.getString("nome_sistema"));
			p.getPapel().setSubSistema(new SubSistema());
			p.getPapel().getSubSistema().setId(rs.getInt("id_subsistema"));
			p.getPapel().getSubSistema().setNome(rs.getString("nome_subsistema"));
			p.getPapel().getSubSistema().setLink(rs.getString("link_subsistema"));
			p.getPapel().getSubSistema().setSistema(new Sistema());
			p.getPapel().getSubSistema().getSistema().setId(rs.getInt("id_sistema"));
			p.getPapel().getSubSistema().getSistema().setNome(rs.getString("nome_sistema"));
			p.setUsuario(new UsuarioGeral());
			p.getUsuario().setId(rs.getInt("id_usuario"));
			p.getUsuario().setLogin(rs.getString("login"));
			p.getUsuario().setNome(rs.getString("nome_usuario"));
			p.getUsuario().setUnidade(new UnidadeGeral());
			p.getUsuario().getUnidade().setCodigo(rs.getLong("codigo_unidade"));
			p.getUsuario().getUnidade().setNome(rs.getString("nome_unidade"));
			p.getUsuario().getUnidade().setSigla(rs.getString("sigla_unidade"));
			p.setUsuarioCadastro(new UsuarioGeral());

			int idAtribuidor = rs.getInt("id_atribuidor");
			if (idAtribuidor > 0) {
				p.getUsuarioCadastro().setId(idAtribuidor);
				p.getUsuarioCadastro().setLogin(rs.getString("login_atribuidor"));
			}

			p.setNumeroChamado(rs.getInt("numero_chamado"));
			p.setMotivo(rs.getString("motivo"));
			p.setAutorizada(rs.getBoolean("autorizada"));
			return p;
		}

	};
	
	RowMapper permissaoMapperWithDesignacao = new RowMapper() {

		public Object mapRow(ResultSet rs, int row) throws SQLException {
			Permissao p = new Permissao();
			p.setId(rs.getInt("id_permissao"));
			p.setDataExpiracao(rs.getDate("data_expiracao_permissao"));
			p.setDataExpiracaoDesignacao(rs.getDate("data_expiracao_designacao"));
			p.setDataCadastro(rs.getDate("data_atribuicao"));
			p.setSistema((rs.getInt("id_sistema")));
			int idUnidadePapel = rs.getInt("id_unidade_papel");
			if(idUnidadePapel > 0) {
				p.setUnidadePapel(new UnidadeGeral(idUnidadePapel));
				p.getUnidadePapel().setNome(rs.getString("nome_unidade"));
				p.getUnidadePapel().setSigla(rs.getString("sigla_unidade"));
				p.getUnidadePapel().setCodigo(rs.getLong("codigo_unidade"));
			}
			p.setPapel(new Papel());
			p.getPapel().setId(rs.getInt("id_papel"));
			p.getPapel().setNome(rs.getString("nome"));
			p.getPapel().setDescricao(rs.getString("descricao"));
			p.getPapel().setRestrito(rs.getBoolean("restrito"));
			p.getPapel().setExigeUnidade(rs.getBoolean("exige_unidade"));
			p.getPapel().setSistema(new Sistema());
			p.getPapel().getSistema().setId((rs.getInt("id_sistema")));
			p.getPapel().getSistema().setNome(rs.getString("nome_sistema"));
			p.getPapel().setSubSistema(new SubSistema());
			p.getPapel().getSubSistema().setId(rs.getInt("id_subsistema"));
			p.getPapel().getSubSistema().setNome(rs.getString("nome_subsistema"));
			p.getPapel().getSubSistema().setLink(rs.getString("link_subsistema"));
			p.getPapel().getSubSistema().setSistema(new Sistema());
			p.getPapel().getSubSistema().getSistema().setId(rs.getInt("id_sistema"));
			p.getPapel().getSubSistema().getSistema().setNome(rs.getString("nome_sistema"));
			p.setUsuario(new UsuarioGeral());
			p.getUsuario().setId(rs.getInt("id_usuario"));
			p.getUsuario().setLogin(rs.getString("login"));
			p.getUsuario().setNome(rs.getString("nome_usuario"));
			p.getUsuario().setUnidade(new UnidadeGeral());
			p.getUsuario().getUnidade().setCodigo(rs.getLong("codigo_unidade"));
			p.getUsuario().getUnidade().setNome(rs.getString("nome_unidade"));
			p.getUsuario().getUnidade().setSigla(rs.getString("sigla_unidade"));
			p.setUsuarioCadastro(new UsuarioGeral());

			int idAtribuidor = rs.getInt("id_atribuidor");
			if (idAtribuidor > 0) {
				p.getUsuarioCadastro().setId(idAtribuidor);
				p.getUsuarioCadastro().setLogin(rs.getString("login_atribuidor"));
			}

			p.setNumeroChamado(rs.getInt("numero_chamado"));
			p.setMotivo(rs.getString("motivo"));
			p.setAutorizada(rs.getBoolean("autorizada"));
			return p;
		}

	};

	/**
	 * Busca todas as permissões de um usuário.
	 * @param usuario Usuário a ter permissões buscadas.
	 * @return Lista de permissões do usuário.
	 */
	public List<Permissao> findPermissoesByUsuario(final UsuarioGeral usuario) {
		String query = SQL + "and u.id_usuario = ? and pe.autorizada = trueValue()";
		return getJdbcTemplate().query(query, new Object[] { usuario.getId() }, permissaoMapper);
	}

	/**
	 * Retorna uma permissão de acordo com o usuário e o papel desejados
	 */
	public List<Permissao> findPermissoes(UsuarioGeral usuario, Papel papel) {
		String query = SQL + "and pe.autorizada = trueValue() and u.id_usuario = ? and pa.id = ?";
		return getJdbcTemplate().query(query, new Object[] { usuario.getId(), papel.getId() }, permissaoMapper);
	}
	
	/**
	 * Retorna uma permissão de acordo com o usuário e o papel desejados
	 */
	public List<Permissao> findPermissoes(UsuarioGeral usuario, Papel papel, int idUnidade) {
		String query = SQL + "and pe.autorizada = trueValue() and p.id_unidade_papel = ? and u.id_usuario = ? and pa.id = ?";
		return getJdbcTemplate().query(query, new Object[] { idUnidade, usuario.getId(), papel.getId() }, permissaoMapper);
	}

	/**
	 * Retorna uma permissão de acordo com o usuário e o papel desejados
	 */
	public Permissao findPermissao(int idPermissao) {
		String query = SQL + "and pe.id = ?";
		return (Permissao) getJdbcTemplate().queryForObject(query, new Object[] { idPermissao }, permissaoMapper);
	}

	/**
	 * Busca todos os papéis de um usuário desde que eles não tenham data de expiração ou a data de expiração
	 * seja posterior a data passada como parâmetro.
	 */
	public List<Papel> findPapeisAtivosByUsuario(int idUsuario, Date data) {
		String query = SQL + "and u.id_usuario = ? and pe.autorizada = trueValue() and (pe.data_expiracao is null or (pe.data_expiracao is not null and pe.data_expiracao >= ?))";
		return getJdbcTemplate().query(query, new Object[] { idUsuario, data }, papelMapper);
	}

	/**
	 * Busca todos os papéis de um usuário desde que eles não tenham data de expiração ou a data de expiração
	 * seja posterior a data passada como parâmetro.
	 */
	public List<Permissao> findPermissaosAtivosByUsuario(int idUsuario, Date data) {
		List<Permissao> permissoes = new ArrayList<Permissao>();
		
		String sql =
				"SELECT " +
				"		pe.id AS id_permissao, " +
				"		pe.data_expiracao AS data_expiracao_permissao, " +
				"       (SELECT d.fim " +
				"			FROM rh.designacao d " +
				"			WHERE d.id_designacao = pe.id_designacao " +
				"		) AS data_expiracao_designacao, " +
				"		pe.data_atribuicao, " +
				"		pe.id_unidade_papel AS id_unidade_papel, " +
				"		pa.id AS id_papel, " +
				"		pa.descricao, pa.nome, " +
				"       pa.restrito, " +
				"		pa.exige_unidade, " +
				"		pa.id_sistema, " +
				"		si.id AS id_sistema, " +
				"		si.descricao AS nome_sistema, " +
				"       ss.id AS id_subsistema, " +
				"		ss.nome AS nome_subsistema, " +
				"		ss.link_entrada AS link_subsistema, " +
				"		pe.id_atribuidor, " +
				"       a.login AS login_atribuidor, " +
				"		u.id_usuario, " +
				"		u.login, " +
				"		p.nome AS nome_usuario, " +
				"		un.codigo_unidade, " +
				"       un.nome AS nome_unidade, " +
				"		un.sigla AS sigla_unidade, " +
				"		pe.autorizada, " +
				"		pe.numero_chamado, " +
				"		pe.motivo " +
				"	FROM " +
				"		comum.papel pa, " +
				"		comum.permissao pe " +
				"			LEFT JOIN comum.usuario a ON (pe.id_atribuidor = a.id_usuario) " +
				"			LEFT JOIN comum.unidade un ON (pe.id_unidade_papel = un.id_unidade), " +
				"		comum.subsistema ss, " +
				"       comum.usuario u, " +
				"		comum.pessoa p, " +
				"		comum.sistema si " +
				"	WHERE " +
				"		pa.id = pe.id_papel AND " +
				"		pa.id_subsistema = ss.id AND " +
				"		pe.id_usuario = u.id_usuario AND " +
				"		pa.id_sistema = si.id AND " +
				"		u.id_pessoa = p.id_pessoa AND " +
				"		u.id_usuario = ? AND " +
				"		(" +
				"			pe.data_expiracao IS NULL OR " +
				"			(pe.data_expiracao IS NOT NULL AND pe.data_expiracao >= ?)" +
				"		)" +
				"	ORDER BY si.descricao, pa.nome";
		
		// percorre as permissoes e verifica se a data de expiracao da designacao (se existir) 
		// é MAIOR ou IGUAL e/ou já esta expirada, caso positivo a permissão é adicionada a lista.
		List<Permissao> permissoesDB = getJdbcTemplate().query(sql, new Object[] { idUsuario, data }, permissaoMapperWithDesignacao);
		
		if (permissoesDB != null && permissoesDB.size() > 0) {
			Iterator<Permissao> itPermissoes = permissoesDB.iterator();
			
			Permissao p = new Permissao();
			
			while (itPermissoes.hasNext()) {
				p = itPermissoes.next();
				
				// se ambas as datas existirem
				if (p.getDataExpiracaoDesignacao() != null && p.getDataExpiracao() != null) {
					// se a data de expiracao da designacao for MAIOR ou IGUAL que a data de expiracao da permissao e que a data atual, então a permissao é adicionada
					if (p.getDataExpiracaoDesignacao().getTime() >= p.getDataExpiracao().getTime() && p.getDataExpiracaoDesignacao().getTime() >= data.getTime()) {
						permissoes.add(p);
					}
					
				// se apenas a data de expiração da designação exitir
				} else if (p.getDataExpiracaoDesignacao() != null) {
					// se a data de expiracao da designacao for MAIOR ou IGUAL que a data atual, então a permissão é removida da lista
					if (p.getDataExpiracaoDesignacao().getTime() >= data.getTime()) {
						permissoes.add(p);
					}
					
				// em qualquer outro caso a permissao é adicionada (quando não houver expiração)
				} else {
					permissoes.add(p);
				}
			}
			
			p = null;
			itPermissoes = null;
			permissoesDB = null;
		}
		
		sql = null;
		
		//String query = sql + "and u.id_usuario = ? and (pe.data_expiracao is null or (pe.data_expiracao is not null and pe.data_expiracao >= ?)) order by si.descricao, pa.nome";
		return permissoes;
	}

	/**
	 * Efetua a busca por um papel específico passando seu identificador como parâmetro.
	 * 
	 * @param id Identificador do Papel
	 * @return Papel
	 */
	public Papel findPapelByPrimaryKey(int id) {
		return (Papel) getJdbcTemplate().queryForObject("select * from comum.papel where id = ?", new Object[] { id }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Papel p = new Papel();
				p.setId(rs.getInt("id"));
				p.setNome(rs.getString("nome"));
				p.setDescricao(rs.getString("descricao"));
				p.setExigeUnidade(rs.getBoolean("exige_unidade"));
				p.setRestrito(rs.getBoolean("restrito"));
				p.setSistema(new Sistema(rs.getInt("id_sistema")));
				p.setSubSistema(new SubSistema(rs.getInt("id_subsistema")));
				return p;
			}
		});
	}
	
	/**
	 * Efetua uma busca por todos os papéis com seus respectivos identificadores informados no parâmetro.
	 * 
	 * @param id Array de identificadores de papéis
	 * @return Lista de objetos do tipo Papel
	 */
	public List<Papel> findPapeis(int[] id) {
		return getJdbcTemplate().query("select * from comum.papel where id in " + UFRNUtils.gerarStringIn(id), new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Papel p = new Papel();
				p.setId(rs.getInt("id"));
				p.setNome(rs.getString("nome"));
				p.setDescricao(rs.getString("descricao"));
				p.setExigeUnidade(rs.getBoolean("exige_unidade"));
				p.setRestrito(rs.getBoolean("restrito"));
				p.setSistema(new Sistema(rs.getInt("id_sistema")));
				p.setSubSistema(new SubSistema(rs.getInt("id_subsistema")));
				return p;
			}
		});
	}
	
	
	/**
	 * Verifica se o usuário tem algum papel do SIPAC.
	 */
	public boolean isUserInSipac(int idUsuario) {
		return getJdbcTemplate().queryForInt("select count(*) from comum.permissao where id_usuario = " + idUsuario + " and autorizada = trueValue() and id_sistema = " + Sistema.SIPAC ) > 0;
	}

	/**
	 * Verifica se o usuário tem algum papel do SIGAA.
	 */
	public boolean isUserInSigaa(int idUsuario) {
		return getJdbcTemplate().queryForInt("select count(*) from comum.permissao where id_usuario = " + idUsuario + " and autorizada = trueValue() and id_sistema = " + Sistema.SIGAA) > 0;
	}

	/**
	 * Retorna os papéis que fazem parte de um subsistema.
	 */
	public Collection<Papel> findPapelBySubSistema(int idSubsistema) {
		return getJdbcTemplate().query("select id as id_papel, nome, descricao, restrito, exige_unidade, id_sistema, id_subsistema, '' as nome_subsistema, '' as nome_sistema, "
				+ " '' as link_subsistema from comum.papel where id_subsistema=? order by nome asc", new Object[] { idSubsistema }, papelMapper);
	}

	/**
	 * Busca os subsistemas pelo sistema.
	 * @param idSistema
	 * @return
	 * @throws DAOException
	 */
	public Collection<SubSistema> findSubsistemaBySistema(int idSistema) throws DAOException {
		Criteria c = getSession().createCriteria(SubSistema.class);
		c.add(Restrictions.eq("sistema.id", idSistema));
		c.addOrder(Order.asc("nome"));
		return c.list();
	}

	/**
	 * Busca as permissões pelo papel.
	 * @param codigoPapel
	 * @param sistema
	 * @return
	 */
	public Collection<Permissao> findByPapel(int codigoPapel, int sistema) {
		String query = SQL + "and pa.id = ? and pe.autorizada = trueValue() and pa.id_sistema = ? ";
		return getJdbcTemplate().query(query, new Object[]{codigoPapel, sistema},permissaoMapper);
	}

	/**
	 * Busca as permissões pelo papel retornando todas colunas da entidade Permissão
	 * @param codigoPapel
	 * @param sistema
	 * @return
	 * @throws DAOException 
	 */
	public Collection<Permissao> findByPapelCriteria(int codigoPapel, int codigoUnidade) throws DAOException 
	{	
		Criteria c = getSession().createCriteria(Permissao.class);
		c.add(Expression.eq("papel.id", codigoPapel));
		
		Criteria cUsuario = c.createCriteria("usuario");
		
		cUsuario.createCriteria("pessoa").addOrder(Order.asc("nome"));

		if (codigoUnidade > 0)
			cUsuario.add(Expression.eq("unidade.id", codigoUnidade));

		Collection<Permissao> lista = c.list();

		return lista;
	}
	
	/**
	 * Busca os usuários por sistema e unidade.
	 * @param idSubsistema
	 * @param idUnidade
	 * @param considerarUnidadesDireito
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findUsuarioBySubsistemaUnidade(int idSubsistema, int idUnidade, boolean considerarUnidadesDireito) 	throws DAOException {

		StringBuilder sql = new StringBuilder("select usuario.ID_USUARIO, usuario.LOGIN, usuario.SENHA, ");
		sql.append("perm.ID_USUARIO, papel.ID, papel.DESCRICAO, papel.ID_SISTEMA, papel.ID_SUBSISTEMA, ");
		sql.append("sist.ID, sist.DESCRICAO, subsistema.ID, subsistema.NOME, subsistema.LINK_ENTRADA, ");
		sql.append("subsistema.ID_SISTEMA from comum.USUARIO usuario inner join comum.PERMISSAO perm on ");
		sql.append("usuario.ID_USUARIO=perm.ID_USUARIO inner join comum.PAPEL papel on perm.ID_PAPEL=papel.ID ");
		sql.append("left outer join comum.SISTEMA sist on papel.ID_SISTEMA=sist.ID left outer join comum.SUBSISTEMA subsistema ");
		sql.append("on papel.ID_SUBSISTEMA=subsistema.ID ");
		sql.append("join comum.unidade un on un.id_unidade = usuario.id_unidade where papel.ID_SUBSISTEMA=? and perm.autorizada = trueValue() and un.id_unidade = ? ");
		
		if (!ParametroHelper.getInstance().getParametroBoolean(ConstantesParametroGeral.PERMITE_CRIAR_USUARIO_UNIDADE_GESTORA))
			sql.append("and un.tipo <> ?");

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {

			con = Database.getInstance().getComumConnection();

			ps = con.prepareStatement(sql.toString()) ;
			ps.setInt(1, idSubsistema);
			ps.setInt(2, idUnidade);
			
			if (!ParametroHelper.getInstance().getParametroBoolean(ConstantesParametroGeral.PERMITE_CRIAR_USUARIO_UNIDADE_GESTORA))
				ps.setInt(3, UnidadeGeral.UNIDADE_GESTORA);
			
			rs = ps.executeQuery();

			ArrayList<UsuarioGeral> usuarios = new ArrayList<UsuarioGeral>();

			UsuarioGeral user;

			while (rs.next()){
				int i = 0;
				user = new UsuarioGeral();
				user.setId(rs.getInt(++i));
				user.setLogin(rs.getString(++i));
				user.setSenha(rs.getString(++i));

				usuarios.add(user);
			}

			return usuarios;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			closeConnection(con);
		}
	}
	
	
	/**
	 *   <p>Encontra todos os usuários que possuem o papel passado com permissão na unidade passada ou nas unidades pai da unidade passada.  </p>  
	 *   
     *	 <p> <i>Por exemplo, se um usuário X tem pemissão na UFRN e a unidade de permissão passada 
     *   é SINFO, vai retornar todos que tem permissão na  SINFO mais o Usuário X,
     *   porque como SINFO é finha da UFRN e o usuário X tem permissão da UFRN, vai ter permissão também na SINFO automaticamente.  </i> </p>  
     *		
     *
	 * @param papel
	 * @param unidadePermissao se passado o valor nulo, busca apenas os usuário que possuem o papel, independentemente da unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findUsuariosByPapelEUnidadadePermissaoHierarquicamente(Papel papel, UnidadeGeral unidadePermissao) throws DAOException {
		
		
		Collection<UsuarioGeral> usuariosGeral = new ArrayList<UsuarioGeral>(); 
		
		if(papel == null || papel.getId() <= 0)
			throw new DAOException(" Dados do Papel e Unidade da permissão não foram passados correntamente. ");
		
		
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT u.id_usuario, u.login, pessoa.nome, u.email FROM comum.usuario u ");
		sql.append(" INNER JOIN comum.pessoa pessoa on u.id_pessoa = pessoa.id_pessoa ");
		sql.append(" INNER JOIN comum.permissao p on p.id_usuario = u.id_usuario ");
		
		
		sql.append(" WHERE p.id_papel = "+ papel.getId()+" ");
		sql.append(" AND u.inativo = falseValue() AND p.autorizada = trueValue() ");
		sql.append(" AND ( p.data_expiracao IS NULL OR p.data_expiracao <= ? ) ");     // retorna permissões que não tenham expirado ainda.
		
		
		/* ************************************************************************************************************
		 *   Caso seja passado uma unidade com sua hierarquia, vai ser buscado a unidade do papel nessa hierarquia
		 *   
		 *   Caso negativo, somente os usuários que possuem o papel passado, independente da unidade
		 *   
		 * ************************************************************************************************************/
		if(unidadePermissao != null && unidadePermissao.getId() > 0 && unidadePermissao.getHierarquia() != null){
		
			String[] unidadesHierarquia = unidadePermissao.getHierarquia().split("\\.");
			
			int[] idsUnidadeHierarquia = new int[unidadesHierarquia.length];
			
			for (int index = 0; index < unidadesHierarquia.length; index++) {
				if(StringUtils.notEmpty(unidadesHierarquia[index]))
						idsUnidadeHierarquia[index] = Integer.valueOf( unidadesHierarquia[index] );
			}
			
			sql.append(" AND p.id_unidade_papel in "+UFRNUtils.gerarStringIn(idsUnidadeHierarquia)+" "); 
		}	
		
		
		Connection con = null;
		ResultSet rs = null;
		
		try {
			con = Database.getInstance().getComumConnection();
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setDate(1, new java.sql.Date(new Date().getTime()));
			rs = pst.executeQuery();
			
			while(rs.next()){
				UsuarioGeral usuario = new UsuarioGeral();
				int contador = 0;
				
				usuario.setId(rs.getInt(++contador));
				usuario.setLogin(rs.getString(++contador));
				
				PessoaGeral pessoa = new PessoaGeral();
				pessoa.setNome(rs.getString(++contador));
				usuario.setPessoa(pessoa);
				
				usuario.setEmail(rs.getString(++contador));
				usuariosGeral.add(usuario);
			}
			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			closeConnection(con);
		}
		
		return usuariosGeral;
	}
	
	
	/**
	 *   <p>Encontra todos os usuários que possuem o papel passado com permissão na unidade passada  </p>  
     *
	 * @param papel
	 * @param unidadePermissao se passado o valor nulo, busca apenas os usuário que possuem o papel, independentemente da unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findUsuariosByPapelEUnidadadePermissao(Papel papel, UnidadeGeral unidadePermissao) throws DAOException {
		
		/* 
		 * Altera temporariamente a hierarquia para trazer os usuário com permissão apenas na unidade atual, 
		 * não tráz os usuários com permissão nas unidades pai dessa. 
		 */
		
		if(papel == null || papel.getId() <= 0 )
			throw new DAOException(" Dados do Papel não foi passados correntamente. ");
		
		UnidadeGeral unidadePermissaoTemp = null;
		
		if( unidadePermissao != null && unidadePermissao.getId() > 0){
			unidadePermissaoTemp = new UnidadeGeral();
			unidadePermissaoTemp.setId(unidadePermissao.getId());
			unidadePermissaoTemp.setHierarquia("."+unidadePermissao.getId()+".");
		}
		
		return findUsuariosByPapelEUnidadadePermissaoHierarquicamente(papel, unidadePermissaoTemp);
	}
	

	
	
	/**
	 * Busca os usuários de acordo com os parâmetros passados.
	 * @param papel
	 * @param unidade
	 * @param gestora
	 * @param hierarquia
	 * @param ativo
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findByPapel(Papel papel,
			UnidadeGeral unidade, UnidadeGeral gestora, int hierarquia,boolean ativo) throws DAOException {

		Connection con = null;
		ResultSet rs = null;

		try {

			con = Database.getInstance().getComumConnection();
			if (papel == null) {
				return null;
			}

			StringBuilder from = new StringBuilder();
			StringBuilder where = new StringBuilder();

			from.append("from comum.permissao p left join comum.usuario u on p.id_usuario = u.id_usuario ");
			from.append(" left join comum.unidade unidade on unidade.id_unidade = u.id_unidade ");
			from.append("left join comum.usuario_unidade uu on uu.id_usuario = u.id_usuario ");
			from.append("join comum.pessoa pessoa on pessoa.id_pessoa = u.id_pessoa ");

			where.append(" where p.autorizada = trueValue() and p.id_papel = " + papel.getId());

			if(ativo) {
				where.append(" and u.inativo = falseValue() ");
			}

			if (unidade != null && unidade.getId() > 0) {
				from.append(" join comum.unidade uniUser on uniUser.id_unidade = u.id_unidade ") ;
				from.append(" left join comum.unidade uniUserUnidade on uniUserUnidade.id_unidade = uu.id_unidade ") ;
				where.append(" and (uniUser.id_unidade = " + unidade.getId());
				where.append(" or uniUserUnidade.id_unidade = " + unidade.getId() + ") ");
			}
			else if (gestora != null && gestora.getId() > 0) {

				from.append(" join comum.unidade uniUser2 on uniUser2.id_unidade = u.id_unidade ") ;
				from.append(" left join comum.unidade uniUserUnidade2 on uniUserUnidade2.id_unidade = uu.id_unidade ") ;
				from.append(" join comum.unidade uniUserGestora on uniUserGestora.id_unidade = uniUser2.id_gestora ") ;
				from.append(" left join comum.unidade uniUserUnidadeGestora on uniUserUnidadeGestora.id_unidade = uniUserUnidade2.id_gestora ") ;

				where.append(" and (uniUserGestora.id_unidade = " + gestora.getId());
				where.append(" or uniUserUnidadeGestora.id_unidade = " + gestora.getId() + ") ");

			}
			// Restringe busca a usuários na hierarquia da unidade
			else if (hierarquia > 0) {

				from.append(" join comum.unidade uniUser3 on uniUser3.id_unidade = u.id_unidade ") ;
				from.append(" left join comum.unidade uniUserUnidade3 on uniUserUnidade3.id_unidade = uu.id_unidade ") ;

				where.append("and (uniUser3.id_unidade in (select uni.id_unidade from comum.unidade uni where uni.hierarquia like '%."+ hierarquia +".%')");
				where.append("or uniUserUnidade3.id_unidade in (select uni2.id_unidade from comum.unidade uni2 where uni2.hierarquia like '%."+ hierarquia +".%') )");
			}

			int count = 0;
			Statement st = con.createStatement();
			rs = st.executeQuery("select count(distinct(u.id_usuario)) " + from.toString() + where.toString());
			if (rs.next()){
				count = rs.getInt(1);
			}

			String sql = "SELECT  u.id_usuario, u.login, u.email, u.funcionario, u.inativo, u.ramal, u.tipo, ";
			sql += " u.id_aluno, u.id_servidor, u.id_unidade, pessoa.id_pessoa, pessoa.nome, pessoa.cpf_cnpj, ";
			sql += " unidade.codigo_unidade, unidade.nome, unidade.sigla, unidade.tipo, unidade.categoria, unidade.id_gestora " ;
			sql += from.toString() + where.toString();
			sql += " group by u.id_usuario, p.id, p.id_papel, p.id_usuario, u.login, u.email,u.funcionario, u.inativo, u.ramal,";
			sql += " u.tipo, u.id_aluno, u.id_servidor, pessoa.id_pessoa, pessoa.nome, pessoa.cpf_cnpj, u.id_unidade,";
			sql += " unidade.codigo_unidade, unidade.nome, unidade.sigla, unidade.tipo, unidade.categoria, unidade.id_gestora";
			sql += " order by pessoa.nome ";

			TreeSet<UsuarioGeral> usuarios = new TreeSet<UsuarioGeral>();

			if (isPaginable()) {
				setCount(count);
				sql += " "+ BDUtils.limit(getPageSize()) + " offset " + (getPageNum() - 1) * getPageSize();
			}
			rs = st.executeQuery(sql);
			while(rs.next()){
				UsuarioGeral usuario = new UsuarioGeral();
				int i = 0;
				usuario.setId(rs.getInt(++i));
				usuario.setLogin(rs.getString(++i));
				usuario.setEmail(rs.getString(++i));
				usuario.setFuncionario(rs.getBoolean(++i));
				usuario.setInativo(rs.getBoolean(++i));
				usuario.setRamal(rs.getString(++i));
				usuario.setTipo(new TipoUsuario(rs.getInt(++i)));
				usuario.setIdAluno(rs.getInt(++i));
				usuario.setIdServidor(rs.getInt(++i));
				usuario.setUnidade(new UnidadeGeral(rs.getInt(++i)));
				PessoaGeral pessoa = new PessoaGeral();
				pessoa.setId(rs.getInt(++i));
				pessoa.setNome(rs.getString(++i));
				pessoa.setCpf_cnpj(rs.getLong(++i));
				usuario.setPessoa(pessoa);
				usuario.getUnidade().setCodigo(rs.getLong(++i));
				usuario.getUnidade().setNome(rs.getString(++i));
				usuario.getUnidade().setSigla(rs.getString(++i));
				usuario.getUnidade().setTipo(rs.getInt(++i));
				usuario.getUnidade().setCategoria(rs.getInt(++i));
				usuario.getUnidade().setGestora(new UnidadeGeral(rs.getInt(++i)));

				usuarios.add(usuario);
			}

			return usuarios;

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			closeConnection(con);
		}
	}
	
	/**
	 * Busca os usuários por papel.
	 * @param papel
	 * @param unidade
	 * @param hierarquia
	 * @param ativo
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findByPapel(Papel papel, UnidadeGeral unidade, int hierarquia,boolean ativo) throws DAOException {
		return findByPapel(papel, unidade, null, hierarquia,ativo);
	}
	
	/**
	 * Busca os usuários por papel.
	 * @param papel
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findByPapel(Papel papel) throws DAOException {
		return findByPapel(papel, null, null, 0, true);
	}
	
	/**
	 * Busca os usuários por papel e gestora.
	 * @param papel
	 * @param gestora
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findByPapelGestora(Papel papel, UnidadeGeral gestora) throws DAOException {
		return findByPapel(papel, null, gestora, 0, true);
	}

	/**
	 * Busca usuarios por unidade.
	 * @param idUsuario
	 * @param sipac
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioUnidade> findUsuarioUnidadeByUsuario(int idUsuario, boolean sipac) throws DAOException {
		String hql = "from UsuarioUnidade uu where uu.usuario.id = "
			+ idUsuario + " and uu.unidade.unidadeSipac = " + sipac + " order by nome";
		Query q = getSession().createQuery(hql);

		return q.list();
	}
	
	/**
	 * Retorna o número de usuários por papel.
	 * @param papel
	 * @return
	 * @throws DAOException
	 */
	public int countUsuariosByPapel(Papel papel) throws DAOException {
		return getJdbcTemplate().queryForInt("select count (*) from comum.permissao pe " +
				" where id_papel = ? and pe.autorizada = trueValue() " +
				" and (pe.data_expiracao is null or (pe.data_expiracao is not null and pe.data_expiracao >= ?))",
				new Object[] {papel.getId(), DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)});
	}

	/**
	 * Efetua uma busca por todos os papéis disponíveis na base de dados.
	 * 
	 * @return Lista de objetos do tipo Papel
	 */
	public List<Papel> findTodosPapeis() {
		return getJdbcTemplate().query("select p.id as id_papel, p.nome, p.descricao, p.restrito, p.exige_unidade, p.id_sistema, p.id_subsistema, "
				+ " ss.nome as nome_subsistema, s.descricao as nome_sistema, ss.link_entrada as link_subsistema "
				+ "from comum.papel p, comum.sistema s, comum.subsistema ss where p.id_sistema = s.id and p.id_subsistema = ss.id "
				+ "order by nome_sistema, nome_subsistema", papelMapper);
	}

	/**
	 * Faz a autorização de uma determinada permissão informada no parâmetro.
	 * 
	 * @param permissao Permissão que será autorizada
	 */
	public void autorizar(Permissao permissao) {
		getJdbcTemplate().update("update comum.permissao set autorizada = trueValue() where id = ?", new Object[] { permissao.getId() });
	}

	/**
	 * Busca todas as permissões disponíveis de um usuário.
	 * 
	 * @param usuario Usuário que terá suas permissões listadas
	 * @return Lista de permissões (objeto Permissao) de um determinado usuário
	 */
	public List<Permissao> findPermissoesPendentesByUsuario(UsuarioGeral usuario) {
		String query = SQL + "and pe.autorizada = falseValue() and u.id_usuario = ?";
		return getJdbcTemplate(Sistema.COMUM).query(query, new Object[] { usuario.getId() }, permissaoMapper);
	}

	/**
	 * Carrega as informações dos papéis da lista passada como parâmetro.
	 * A lista deve conter papéis populados apenas com o ID.
	 * @param papeisTemporarios
	 * @return
	 */
	public Collection<Papel> findPapeisByIds(Collection<Papel> papeisTemporarios) {
		String query = "select * from comum.papel where id in " + UFRNUtils.gerarStringIn(papeisTemporarios); 
		return getJdbcTemplate(Sistema.COMUM).query(query, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Papel p = new Papel();
				p.setId(rs.getInt("id"));
				p.setNome(rs.getString("nome"));
				p.setDescricao(rs.getString("descricao"));
				p.setRestrito(rs.getBoolean("restrito"));
				p.setExigeUnidade(rs.getBoolean("exige_unidade"));
				p.setSistema(new Sistema());
				p.getSistema().setId(rs.getInt("id_sistema"));
				p.setSubSistema(new SubSistema());
				p.getSubSistema().setId(rs.getInt("id_subsistema"));
				p.getSubSistema().setSistema(new Sistema());
				p.getSubSistema().getSistema().setId(rs.getInt("id_sistema"));
				return p;
			}
		});
	}
	
	/**
	 * Retorna a lista de papéis do usuário passado como parâmetro
	 * que são incompatíveis com o papel passado como parâmetro.
	 * @param idUsuario
	 * @param idPapel
	 * @return
	 */
	public Collection<Papel> papeisIncompativeisUsuario(int idUsuario, int idPapel) {
		return getJdbcTemplate().query("select * from comum.papel where id in ("
				+ "select case when id_papel_1 = ? then id_papel_2 else id_papel_1 end as id_papel from comum.papeis_incompativeis where (id_papel_1 in ( "
				+ "select distinct id_papel from comum.permissao where id_usuario = ? and autorizada = true and (data_expiracao is null or data_expiracao < now())) "
				+ "and id_papel_2 = ?) or (id_papel_2 in ( "
				+ "select distinct id_papel from comum.permissao where id_usuario = ? and autorizada = true and (data_expiracao is null or data_expiracao < now())) "
				+ "and id_papel_1 = ?))", new Object[] { idPapel, idUsuario, idPapel, idUsuario, idPapel }, new RowMapper() {
					public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
						Papel p = new Papel();
						p.setId(rs.getInt("id"));
						p.setNome(rs.getString("nome"));
						p.setDescricao(rs.getString("descricao"));
						p.setExigeUnidade(rs.getBoolean("exige_unidade"));
						p.setRestrito(rs.getBoolean("restrito"));
						p.setSistema(new Sistema(rs.getInt("id_sistema")));
						p.setSubSistema(new SubSistema(rs.getInt("id_subsistema")));
						return p;
					}
				});
	}
	
	/**
	 * Busca as permissões do usuário agrupadas por sub-sistema e sistema.
	 * @param id
	 * @return
	 */
	public Map<Sistema, Map<SubSistema, List<Permissao>>> findPermissoesPorSistema(int idUsuario) {
		return (Map<Sistema, Map<SubSistema, List<Permissao>>>) getJdbcTemplate().query(SQL + " and pe.id_usuario = ? order by pa.nome", new Object[] { idUsuario }, new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<Sistema, Map<SubSistema, List<Permissao>>> permissoesPorSistema = new ConcurrentSkipListMap<Sistema, Map<SubSistema,List<Permissao>>>(new Comparator<Sistema>() {
					public int compare(Sistema s1, Sistema s2) {
						return s1.getNome().compareTo(s2.getNome());
					}
				});
				while(rs.next()) {
					Sistema sistema = new Sistema(rs.getInt("id_sistema"));
					sistema.setNome(rs.getString("nome_sistema"));
					if (!permissoesPorSistema.containsKey(sistema)) {
						permissoesPorSistema.put(sistema, new ConcurrentSkipListMap<SubSistema, List<Permissao>>(new Comparator<SubSistema>() {
							public int compare(SubSistema s1, SubSistema s2) {
								return s1.getNome().compareTo(s2.getNome());
							}
						}));
					} 
					
					Map<SubSistema, List<Permissao>> subSistemas = permissoesPorSistema.get(sistema);
					SubSistema subSistema = new SubSistema(rs.getInt("id_subsistema"));
					subSistema.setNome(rs.getString("nome_subsistema"));
					if (!subSistemas.containsKey(subSistema)) {
						subSistemas.put(subSistema, new ArrayList<Permissao>());
					}
					
					List<Permissao> permissoes = subSistemas.get(subSistema);
					permissoes.add((Permissao) permissaoMapper.mapRow(rs, 0));
				}
				
				return permissoesPorSistema;
			}
		});
	}

	/**
	 * Retorna um mapa contendo as unidades possíveis de serem associadas ao
	 * papel passado como parâmetro.
	 * @param p
	 * @return
	 */
	public Map<Integer, String> buscarUnidadesPermitidasPapel(Papel p) {
		return (Map<Integer, String>) getJdbcTemplate().query(p.getSqlFiltroUnidades(), MAP_EXTRACTOR);
	}
	
}
