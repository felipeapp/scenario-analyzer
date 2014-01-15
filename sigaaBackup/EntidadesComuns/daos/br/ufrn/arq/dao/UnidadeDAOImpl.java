/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 19/11/2008
 */

package br.ufrn.arq.dao;

 import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.log.LogInterceptor;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.CategoriaUnidade;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.Unidade;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.rh.dominio.Servidor;



/**
 * Implementa os métodos cuja interface está em UnidadeDAO.
 *
 * @author Edson Anibal (ambar@info.ufrn.br)
 */
public class UnidadeDAOImpl extends GenericDAOImpl {

	public UnidadeDAOImpl() {
		daoName = "UnidadeDAO";
	}

	public UnidadeDAOImpl(int sistema) {
		orderBy = new ArrayList<String>();
		ascDesc = new ArrayList<String>();
		interceptor = new LogInterceptor();
		interceptor.setSistema(sistema);
		daoName = "UnidadeDAO";
		setSistema(sistema);
	}

	protected String daoName;

	RowMapper unidadeMapper = new RowMapper() {
		public Object mapRow(ResultSet rs, int row) throws SQLException {
			UnidadeGeral u = new UnidadeGeral();
			u.setId(rs.getInt("id_unidade"));
			u.setNome(rs.getString("nome"));
			u.setCodigo(rs.getLong("codigo_unidade"));
			u.setMetas(rs.getBoolean("metas"));
			u.setUnidadeAcademica(rs.getBoolean("unidade_academica"));
			u.setResponsavelOrganizacional(new Unidade(rs.getInt("id_unid_resp_org")));
			u.setHierarquia(rs.getString("hierarquia"));
			u.setHierarquiaOrganizacional(rs.getString("hierarquia_organizacional"));

			try{
				u.setQtdServidoresLotados(rs.getInt("qtd_lotados"));
			}catch (SQLException e) {
				u.setQtdServidoresLotados(0);
			}

			return u;
		}
	};


	/**
	 * Procura as unidades gestoras, ou seja, unidades cujo tipo seja gestora
	 * e cuja categoria seja unidade.
	 *
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @return Unidade
	 */
	public Collection<Unidade> findGestoras() throws DAOException {

		String hql = "select u.id, u.sigla, u.codigo, u.nome, u.responsavelOrganizacional, u.unidadeResponsavel from Unidade u where u.categoria=" + CategoriaUnidade.UNIDADE
		+ " and u.tipo=" + UnidadeGeral.UNIDADE_GESTORA + " AND u.unidadeResponsavel is not null and u.organizacional = trueValue() and u.dataExtincao is null order by u.id, u.codigo, u.nome";
		Query q = getSession().createQuery(hql);
		List<?> resultQuery = q.list();
		Collection<Unidade> unidades = new ArrayList<Unidade>();
		for (int i = 0; i < resultQuery.size(); i++) {
			Object[] obj = (Object[]) resultQuery.get(i);
			int j = 0;
			Unidade unidade = new Unidade();
			unidade.setId((Integer) obj[j++]);
			unidade.setSigla((String) obj[j++]);
			unidade.setCodigo((Long) obj[j++]);
			unidade.setNome((String) obj[j++]);
			unidade.setResponsavelOrganizacional((UnidadeGeral) obj[j++]);
			unidade.setUnidadeResponsavel((UnidadeGeral) obj[j++]);
			unidades.add(unidade);
		}
		return unidades;
	}
	
	/**
	 * Busca todas as unidades trazendo as informações mínimas. Utilizado,
	 * por exemplo, na arvore organizacional de unidades do SIGAdmin.
	 * @param apenasOrganizacionais 
	 * @param idUnidade
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<Unidade> buscarUnidadesComProjecao(boolean apenasOrganizacionais) {
		String sql = "select * from comum.unidade where ativo = true and id_unidade != ? ";
		if (apenasOrganizacionais) sql += "and organizacional = true";
		
		return getJdbcTemplate(Database.getInstance().getComumDs()).query(sql + " order by nome asc",
				new Object[] { Unidade.UNIDADE_DIREITO_GLOBAL },
				new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Unidade u = new Unidade();
				u.setId(rs.getInt("id_unidade"));
				u.setNome(rs.getString("nome"));
				u.setCodigo(rs.getLong("codigo_unidade"));
				u.setHierarquia(rs.getString("hierarquia"));
				u.setOrganizacional(rs.getBoolean("organizacional"));
				u.setUnidadeResponsavel(new Unidade(rs.getInt("unidade_responsavel")));
				u.setResponsavelOrganizacional(new Unidade(rs.getInt("id_unid_resp_org")));
				return u;
			}
		});
	}

	/**
	 * Procura as Unidades que São CENTRO. Procura por Unidades cujo
	 * "Tipo" seja "UnidadeGestora" e a "Categoria" seja "Unidade" e seja UNIDADE ACADEMICA
	 *
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @return Unidade
	 */
	public Collection<Unidade> findGestorasAcademicas() throws DAOException {
		String hql = "select u.id, u.sigla, u.codigo, u.nome from Unidade u where u.tipo="
			+ UnidadeGeral.UNIDADE_GESTORA + " and u.categoria=" + CategoriaUnidade.UNIDADE
			+ " AND u.unidadeResponsavel is not null and u.organizacional = trueValue() "
			+ " and u.unidadeAcademica = trueValue() and u.dataExtincao is null order by u.id, u.codigo, u.nome";
		Query q = getSession().createQuery(hql);
		List<?> resultQuery = q.list();
		Collection<Unidade> unidades = new ArrayList<Unidade>();
		for (int i = 0; i < resultQuery.size(); i++) {
			Object[] obj = (Object[]) resultQuery.get(i);
			int j = 0;
			Unidade unidade = new Unidade();
			unidade.setId((Integer) obj[j++]);
			unidade.setSigla((String) obj[j++]);
			unidade.setCodigo((Long) obj[j++]);
			unidade.setNome((String) obj[j++]);
			unidades.add(unidade);
		}
		return unidades;
	}

	/**
	 * Procura unidades que são "acadêmicas" ou seja, tudo que tiver
	 * unidade_academica setado como true Usado para buscar as unidades filhas
	 * da árvore de unidades acadêmicas
	 *
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public Collection<Unidade> findFilhasAcademicas(int idUnidade)throws DAOException {
		String hql = "select u.id, u.codigo, u.nome, u.sigla, u.unidadeAcademica "
			+ "from Unidade u where u.unidadeAcademica = trueValue() AND u.unidadeResponsavel.id = :idResponsavel " +
			"order by u.nome";

		Query query = getSession().createQuery(hql);
		query.setInteger("idResponsavel", idUnidade);

		@SuppressWarnings("unchecked")
		ArrayList<Object[]> results = (ArrayList<Object[]>) query.list();

		Collection<Unidade> unidades = new ArrayList<Unidade>();
		Unidade unidade = null;
		for (Object[] result : results) {

			unidade = new Unidade();
			int i = 0;
			unidade.setId((Integer) result[i++]);
			unidade.setCodigo((Long) result[i++]);
			unidade.setNome((String) result[i++]);
			unidade.setSigla((String) result[i++]);
			unidade.setUnidadeAcademica((Boolean) result[i++]);
			unidades.add(unidade);
		}

		return unidades;
	}

	/**
	 * Procura as unidades filhas da unidade passada como parâmetro que são "unidades orçamentárias" 
	 * ou seja, tudo que tiver o atributo unidadeOrcamentaria setado como true. 
	 *
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public Collection<Unidade>findFilhasOrcamentarias(int idUnidade)throws DAOException {
		String hql = "select u.id, u.codigo, u.nome, u.sigla, u.unidadeOrcamentaria "
			+ "from Unidade u where u.unidadeOrcamentaria = trueValue() AND u.unidadeResponsavel.id = :idResponsavel order by u.id, u.nome, u.codigo";

		Query query = getSession().createQuery(hql);
		query.setInteger("idResponsavel", idUnidade);

		@SuppressWarnings("unchecked")
		ArrayList<Object[]> results = (ArrayList<Object[]>) query.list();

		Collection<Unidade> unidades = new ArrayList<Unidade>();
		Unidade unidade = null;
		for (Object[] result : results) {

			unidade = new Unidade();
			int i = 0;
			unidade.setId((Integer) result[i++]);
			unidade.setCodigo((Long) result[i++]);
			unidade.setNome((String) result[i++]);
			unidade.setSigla((String) result[i++]);
			unidade.setUnidadeOrcamentaria((Boolean) result[i++]);
			unidades.add(unidade);
		}

		return unidades;
	}

	/**
	 * Procura unidades que são Responsáveis Organizacionais. Usado na árvore de
	 * unidade para buscar os filhos que são organizacionais.
	 */
	public Collection<Unidade> findFilhasOrganizacionais(int idUnidade) throws DAOException {
		String hql = "select u.id, u.codigo, u.nome, u.sigla, u.organizacional "
			+ "from Unidade u where u.responsavelOrganizacional.id = :idResponsavel AND u.responsavelOrganizacional.id != null  order by nome, codigo";

		Query query = getSession().createQuery(hql);
		query.setInteger("idResponsavel", idUnidade);

		@SuppressWarnings("unchecked")
		ArrayList<Object[]> results = (ArrayList<Object[]>) query.list();

		Collection<Unidade> unidades = new ArrayList<Unidade>();
		Unidade unidade = null;
		for (Object[] result : results) {

			unidade = new Unidade();
			int i = 0;
			unidade.setId((Integer) result[i++]);
			unidade.setCodigo((Long) result[i++]);
			unidade.setNome((String) result[i++]);
			unidade.setSigla((String) result[i++]);
			unidade.setOrganizacional((Boolean) result[i++]);
			unidades.add(unidade);
		}

		return unidades;
	}

	/**
	 * Busca todas as unidades que podem ser acessadas pelo sistema de
	 * protocolos
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> findAllUnidadeProtocolo() throws DAOException {
		String hql = "select id, sigla, codigo, nomeCapa, nome from Unidade where organizacional = trueValue() and dataExtincao is null order by nomeCapa";
		Query q = getSession().createQuery(hql);

		List<?> resultQuery = q.list();
		Collection<Unidade> unidades = new ArrayList<Unidade>();
		for (int i = 0; i < resultQuery.size(); i++) {

			Object[] obj = (Object[]) resultQuery.get(i);
			int j = 0;
			Unidade unidade = new Unidade();
			unidade.setId((Integer) obj[j++]);
			unidade.setSigla((String) obj[j++]);
			unidade.setCodigo((Long) obj[j++]);
			unidade.setNomeCapa((String) obj[j++] + " ("
					+ unidade.getCodigoFormatado() + ")");
			unidade.setNome((String) obj[j++]);
			unidades.add(unidade);

		}

		return unidades;
	}

	/**
	 * Busca todas as unidades que podem ser acessadas pelo sistema de
	 * protocolos e que têm usuários cadastrados. Não retorna unidades gestoras
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Unidade> findAllUnidadeProtocoloWithUsuarios() throws DAOException {
		String sql = "select id_unidade, nome_abreviado, codigo_unidade, nome_capa, nome from comum.unidade "
			+ " where tipo != " + UnidadeGeral.UNIDADE_GESTORA + " and id_unidade in "
			+ " (select distinct u.id_unidade from comum.unidade u, comum.usuario us, comum.permissao pe, comum.papel pa "
			+ "	where u.protocolo = trueValue() and us.id_unidade = u.id_unidade and pe.id_usuario = us.id_usuario and "
			+ "	pa.id_papel = pe.id_papel and pa.id_subsistema = 3"
			+ " union select distinct u.id_unidade from comum.unidade u, comum.usuario_unidade us, comum.permissao pe, comum.papel pa  "
			+ "	where u.protocolo = trueValue() and us.id_unidade = u.id_unidade and pe.id_usuario = us.id_usuario and "
			+ "	pa.id_papel = pe.id_papel and pa.id_subsistema = 3 ) "
			+ "order by nome_capa";

		return getJdbcTemplate(Sistema.SIPAC).query(sql, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Unidade unidade = new Unidade();
				unidade.setId(rs.getInt("id_unidade"));
				unidade.setSigla(rs.getString("nome_abreviado"));
				unidade.setCodigo(rs.getLong("codigo_unidade"));
				unidade.setNomeCapa(rs.getString("nome_capa") + " (" + unidade.getCodigoFormatado() + ")");
				unidade.setNome(rs.getString("nome"));
				return unidade;
			}
		});
	}

	/**
	 * Busca todas as unidades da hierarquia organizacional da instituição.
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> findAllUnidadesOrganizacionais() throws DAOException {
		String hql =
				"select id, codigo, nome, hierarquia, responsavelOrganizacional.id " +
				"from Unidade " +
				"where " +
				"	responsavelOrganizacional is not null " +
				"	and organizacional = trueValue() " +
				"	and dataExtincao is null " +
				"	and ativo = trueValue() " +
				"order by nome";
		
		Query q = getSession().createQuery(hql);

		List<?> resultQuery = q.list();

		Collection<Unidade> unidades = new ArrayList<Unidade>();
		for (int i = 0; i < resultQuery.size(); i++) {
			int j = 0;
			Object[] obj = (Object[]) resultQuery.get(i);

			Unidade unidade = new Unidade();
			unidade.setId((Integer) obj[j++]);
			unidade.setCodigo((Long) obj[j++]);
			unidade.setNome((String) obj[j++]);
			unidade.setHierarquia((String) obj[j++]);

			Unidade respOrganizacional =  new Unidade();
			respOrganizacional.setId((Integer) obj[j++]);
			unidade.setResponsavelOrganizacional(respOrganizacional);

			unidades.add(unidade);
		}
		return unidades;
	}

	/**
	 * Busca uma unidade pelo seu código.
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public UnidadeGeral findByCodigo(long codigo) throws DAOException {
		try {
			return (UnidadeGeral) getJdbcTemplate(Sistema.SIPAC).queryForObject("select * from comum.unidade where codigo_unidade = ?", new Object[] { codigo }, new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					UnidadeGeral u = new UnidadeGeral();
					u.setId(rs.getInt("id_unidade"));
					u.setNome(rs.getString("nome"));
					u.setCodigo(rs.getLong("codigo_unidade"));
					u.setOrganizacional(rs.getBoolean("organizacional"));
					u.setHierarquia(rs.getString("hierarquia"));
					return u;
				}
			});
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * Retorna as unidades COM SERVIDORES LOTADOS
	 * Para as quais o servidor assume certo nível de responsabilidade atualmente.
	 *
	 * @param idServidor
	 * @param nivelResponsabilidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<UnidadeGeral> findUnidadesLotacaoByResponsavel(int idServidor, String nivelResponsabilidade) throws DAOException {
		return findUnidadesLotacaoByResponsavel(idServidor, nivelResponsabilidade, false);
	}

	/**
	 * Retorna as unidades COM SERVIDORES LOTADOS
	 * Para as quais o servidor assume certo nível de responsabilidade atualmente.
	 * Considera também unidades filhas se o parâmetro considerarHierarquia for true.
	 *
	 * @param idServidor
	 * @param nivelResponsabilidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<UnidadeGeral> findUnidadesLotacaoByResponsavel(int idServidor, String nivelResponsabilidade, boolean considerarHierarquia) throws DAOException {

		String sql = " SELECT DISTINCT U.* FROM COMUM.RESPONSAVEL_UNIDADE RU, COMUM.UNIDADE U, RH.SERVIDOR S WHERE nivel_responsabilidade = ? ";
			sql += " AND RU.ID_SERVIDOR = ? AND S.ID_UNIDADE = U.ID_UNIDADE AND S.DATA_DESLIGAMENTO IS NULL ";

			if(considerarHierarquia)
				sql += " AND U.HIERARQUIA_ORGANIZACIONAL LIKE '%.'||RU.ID_UNIDADE||'.%'";
			else
				sql += " AND U.ID_UNIDADE = RU.ID_UNIDADE ";

			sql += " AND (RU.DATA_FIM IS NULL OR RU.DATA_FIM >= CURRENT_DATE ) ORDER BY U.NOME";

		return getJdbcTemplate().query(sql, new Object[] { nivelResponsabilidade, idServidor }, unidadeMapper);
	}

	/**
	 * Retorna todas as unidades COM SERVIDORES LOTADOS
	 *
	 * @param considerarHierarquia
	 * @param considerarOrganizacionais
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> findAllUnidadesOrganizacionaisLotacao(boolean considerarHierarquia, boolean considerarOrganizacionais) throws DAOException {
		StringBuilder sql = new StringBuilder("SELECT DISTINCT U.* " +
											  "  FROM COMUM.RESPONSAVEL_UNIDADE RU, COMUM.UNIDADE U " +
											  " WHERE EXISTS (SELECT ID_SERVIDOR FROM RH.SERVIDOR S WHERE S.DATA_DESLIGAMENTO IS NULL AND ID_UNIDADE = U.ID_UNIDADE) " );

		if (considerarHierarquia)
			sql.append(" AND U.HIERARQUIA_ORGANIZACIONAL LIKE '%.'||RU.ID_UNIDADE||'.%' ");
		else
			sql.append(" AND U.ID_UNIDADE = RU.ID_UNIDADE ");

		if (considerarOrganizacionais)
			sql.append(" AND U.ID_UNID_RESP_ORG IS NOT NULL " +
					   " AND U.ORGANIZACIONAL = trueValue() " +
					   " AND U.DATA_EXTINCAO IS NULL ");

		sql.append(" AND (RU.DATA_FIM IS NULL OR RU.DATA_FIM >= CURRENT_DATE ) " +
				   " ORDER BY U.NOME");

		@SuppressWarnings("unchecked")
		List<UnidadeGeral> ugList = getJdbcTemplate().query(sql.toString(), unidadeMapper);
		Collection<Unidade> unidades = new ArrayList<Unidade>();

		if (ugList != null && ugList.size() > 0) {
			Unidade u = null;

			for (UnidadeGeral ug : ugList) {
				u = new Unidade(ug.getId());
				u.setNome(ug.getNome());
				u.setCodigo(ug.getCodigo());
				u.setMetas(ug.getMetas());
				u.setUnidadeAcademica(ug.isUnidadeAcademica());
				u.setResponsavelOrganizacional(ug.getResponsavelOrganizacional());
				u.setHierarquia(ug.getHierarquia());

				unidades.add(u);
			}
		}

		return unidades;
	}

	/**
	 * Retorna TODAS as unidades para as quais o servidor assume certo nível de responsabilidade atualmente.
	 * @param idServidor
	 * @param nivelResponsabilidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<UnidadeGeral> findUnidadesByResponsavel(int idServidor, String nivelResponsabilidade) throws DAOException {

		String sql =
			" SELECT DISTINCT U.* FROM COMUM.RESPONSAVEL_UNIDADE RU, COMUM.UNIDADE U, RH.SERVIDOR S " +
			" WHERE nivel_responsabilidade = ? AND RU.ID_SERVIDOR = ? " +
			" AND U.ID_UNIDADE = RU.ID_UNIDADE AND S.DATA_DESLIGAMENTO IS NULL " +
			" AND (RU.DATA_FIM IS NULL OR RU.DATA_FIM > now()) ORDER BY U.NOME";

		return getJdbcTemplate().query(sql, new Object[] { nivelResponsabilidade, idServidor }, unidadeMapper);
	}

	/**
	 * Retorna TODAS as unidades para as quais o servidor assume certo nível de responsabilidade atualmente.
	 * @param idServidor
	 * @param nivelResponsabilidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<UnidadeGeral> findUnidadesByResponsavel(int idServidor, Character[] nivelResponsabilidade) throws DAOException {
		return findUnidadesByResponsavel(idServidor, nivelResponsabilidade, false, false);
	}

	/**
	 * Retorna TODAS as unidades para as quais o servidor assume certo nível de responsabilidade atualmente.
	 * @param idServidor
	 * @param nivelResponsabilidade
	 * @param considerarHierarquia
	 * @return
	 * @throws DAOException
	 */
	public Collection<UnidadeGeral> findUnidadesByResponsavel(int idServidor, Character[] nivelResponsabilidade, boolean considerarHierarquia) throws DAOException {
		return findUnidadesByResponsavel(idServidor, nivelResponsabilidade, considerarHierarquia, Boolean.FALSE);
	}
	
	/**
	 * Retorna TODAS as unidades para as quais o servidor assume certo nível de responsabilidade atualmente.
	 * @param idServidor
	 * @param nivelResponsabilidade
	 * @param considerarHierarquia
	 * @param apenasOrganizacionais
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<UnidadeGeral> findUnidadesByResponsavel(int idServidor, Character[] nivelResponsabilidade, boolean considerarHierarquia, boolean apenasOrganizacionais) throws DAOException {

		String sql = "SELECT DISTINCT U.*, (SELECT count(DISTINCT id_servidor) FROM rh.servidor WHERE id_unidade = u.id_unidade AND data_desligamento IS NULL ) AS QTD_LOTADOS ";
		sql += " FROM comum.responsavel_unidade ru INNER JOIN rh.servidor s ON (s.id_servidor = ru.id_servidor) ";
		sql += " INNER JOIN comum.unidade u ON ( ";
			sql += considerarHierarquia ? " u.hierarquia_organizacional LIKE '%.'||ru.id_unidade||'.%' " : " u.id_unidade = ru.id_unidade ";
		sql += " ) WHERE s.id_servidor = ? AND s.data_desligamento IS NULL AND (ru.data_fim IS NULL OR ru.data_fim >=  cast(now() as date)) ";

		if(nivelResponsabilidade != null) {
			sql += " AND ru.nivel_responsabilidade IN "+ UFRNUtils.gerarStringIn(nivelResponsabilidade);
		}

		if (apenasOrganizacionais)
			sql += " AND u.organizacional IS " + SQLDialect.TRUE;
		
		sql += " ORDER BY U.NOME";

		return getJdbcTemplate().query(sql, new Object[]{idServidor}, unidadeMapper);
	}

	/**
	 * Retorna TODAS as responsabilidades por unidades para as quais o servidor assume certo nível de responsabilidade atualmente.
	 * @param idServidor
	 * @param nivelResponsabilidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Responsavel> findResponsabilidadeUnidadeByServidor(int idServidor, Character[] nivelResponsabilidade) throws DAOException {

		String sql = "select u.id_unidade, u.nome, u.codigo_unidade, u.metas, u.unidade_academica, ru.nivel_responsabilidade, s.id_servidor, s.id_ativo, s.siape "
			+ " from comum.responsavel_unidade ru, comum.unidade u, rh.servidor s "
			+ " where ru.id_unidade = u.id_unidade and ru.id_servidor = s.id_servidor "
			+ " and nivel_responsabilidade in " + UFRNUtils.gerarStringIn(nivelResponsabilidade)
			+ " and ru.id_servidor = ? and s.data_desligamento is null and (ru.data_fim is null or ru.data_fim > now()) "
			+ " order by nome";

		return getJdbcTemplate().query(sql, new Object[] { idServidor }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Unidade u = new Unidade();
				u.setId(rs.getInt("id_unidade"));
				u.setNome(rs.getString("nome"));
				u.setCodigo(rs.getLong("codigo_unidade"));
				u.setMetas(rs.getBoolean("metas"));
				u.setUnidadeAcademica(rs.getBoolean("unidade_academica"));

				Responsavel resp = new Responsavel();
				resp.setUnidade(u);
				resp.setNivelResponsabilidade(rs.getString("nivel_responsabilidade").charAt(0));
				resp.setServidor(new br.ufrn.rh.dominio.Servidor());
				resp.getServidor().setId(rs.getInt("id_servidor"));
				resp.getServidor().setAtivo(new Ativo());
				resp.getServidor().getAtivo().setId(rs.getInt("id_ativo"));
				resp.getServidor().setSiape(rs.getInt("siape"));
				return resp;
			}
		});
	}
	
	/**
	 * Retorna TODAS as responsabilidades por unidades academicas para as quais o servidor assume certo nível de responsabilidade atualmente.
	 * @param idServidor
	 * @param nivelResponsabilidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Responsavel> findResponsabilidadeUnidadeAcademicaByServidor(int idServidor, Character[] nivelResponsabilidade) throws DAOException {

		String sql = "select u.id_unidade, u.nome, u.codigo_unidade, u.metas, u.unidade_academica, ru.nivel_responsabilidade, s.id_servidor, s.id_ativo, s.siape " +
				" from comum.responsavel_unidade ru " +
				" 	join comum.unidade u on (ru.id_unidade = u.id_unidade) " +
				" 	join rh.servidor s on (ru.id_servidor = s.id_servidor) " +
				" where nivel_responsabilidade in " + UFRNUtils.gerarStringIn(nivelResponsabilidade) +
				" 	and ru.id_servidor = ? " +
				" 	and s.data_desligamento is null " +
				" 	and (ru.data_fim is null or ru.data_fim > now()) " +
				"	and ( " +
				"		unidade_academica = trueValue() " +
				"		or " +
				"		exists (select id_servidor from rh.servidor where id_unidade = u.id_unidade) " +
				"	) " +
				
				" order by nome";

		return getJdbcTemplate().query(sql, new Object[] { idServidor }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Unidade u = new Unidade();
				u.setId(rs.getInt("id_unidade"));
				u.setNome(rs.getString("nome"));
				u.setCodigo(rs.getLong("codigo_unidade"));
				u.setMetas(rs.getBoolean("metas"));
				u.setUnidadeAcademica(rs.getBoolean("unidade_academica"));

				Responsavel resp = new Responsavel();
				resp.setUnidade(u);
				resp.setNivelResponsabilidade(rs.getString("nivel_responsabilidade").charAt(0));
				resp.setServidor(new br.ufrn.rh.dominio.Servidor());
				resp.getServidor().setId(rs.getInt("id_servidor"));
				resp.getServidor().setAtivo(new Ativo());
				resp.getServidor().getAtivo().setId(rs.getInt("id_ativo"));
				resp.getServidor().setSiape(rs.getInt("siape"));
				return resp;
			}
		});
	}	
	
	/**
	 * Verifica se existe uma unidade cadastrada com o código de unidade gestora (SIAFI) e código de gestão (SIAFI). 
	 * Retorna a unidade, caso exista.
     *
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Unidade buscarPorCodigoSIAFI(Unidade unidade) throws HibernateException, DAOException {
		
		if (!ValidatorUtil.isEmpty(unidade.getCodigoGestaoSIAFI())
				&& !ValidatorUtil.isEmpty(unidade
						.getCodigoUnidadeGestoraSIAFI())) {

			String hql = "from Unidade where codigoGestaoSIAFI = :codigoGS and codigoUnidadeGestoraSIAFI = :codigoUGS";
			Query q = getSession().createQuery(hql);

			q.setInteger("codigoGS", unidade.getCodigoGestaoSIAFI());
			q.setInteger("codigoUGS", unidade.getCodigoUnidadeGestoraSIAFI());

			return (Unidade) q.uniqueResult();

		} else
			return null;		
	}
	
	/**
	 * Verifica se existe uma unidade cadastrada com o código SIORG. 
	 * Retorna a unidade, caso exista.
     *
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Unidade buscarPorCodigoSIORG(Unidade unidade) throws HibernateException, DAOException {
		
		if (!ValidatorUtil.isEmpty(unidade.getCodigoSIORG())){

			String hql = "from Unidade where codigoSIORG = :codigoSIORG";
			Query q = getSession().createQuery(hql);

			q.setInteger("codigoSIORG", unidade.getCodigoSIORG());
			q.setMaxResults(1);

			return (Unidade) q.uniqueResult();

		} else
			return null;		
	}

	/**
	 * Busca o responsável pelo unidade.
	 * @param idServidor
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	public Responsavel findResponsavelUnidade(int idServidor, int idUnidade) throws DAOException{

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			Responsavel responsavel = null;

			StringBuffer sql = new StringBuffer();

			sql.append("select resp.id as id, resp.id_unidade as idUnidade, resp.id_servidor as idServidor, ");
			sql.append("resp.data_inicio as dataInicio, resp.data_fim as dataFim,  resp.id_usuario as idUsuario, ");
			sql.append("resp.data_cadastro as dataCadastro, resp.nivel_responsabilidade as nivel ");
			sql.append("from comum.responsavel_unidade resp ");
			sql.append("where resp.id_servidor = ? ");
			sql.append("and (resp.data_fim >= ? or resp.data_fim is null) ");
			if(idUnidade > 0)
				sql.append("and resp.id_unidade = ? ");

			con = Database.getInstance().getSipacConnection();

			ps = con.prepareStatement(sql.toString());
			ps.setInt(1, idServidor);
			ps.setDate(2, new Date(new java.util.Date().getTime()));
			if(idUnidade > 0)
				ps.setInt(3, idUnidade);

			rs = ps.executeQuery();

			while (rs.next()){
				responsavel = new Responsavel();
				responsavel.setId(rs.getInt("id"));
				responsavel.setUnidade(new Unidade(rs.getInt("idUnidade")));
				responsavel.setServidor(new Servidor(rs.getInt("idServidor")));
				responsavel.setInicio(rs.getDate("dataInicio"));
				responsavel.setFim(rs.getDate("dataFim"));
				responsavel.setUsuario(new UsuarioGeral(rs.getInt("idUsuario")));
				responsavel.setDataCadastro(rs.getDate("dataCadastro"));
				responsavel.setNivelResponsabilidade(rs.getString("nivel").charAt(0));
			}

			return responsavel;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			closeConnection(con);
		}
	}


	/**
	 * Busca informações de responsabilidade de unidade
	 * para o servidor passado como parâmetro.
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public Responsavel findResponsavelUnidade(int idServidor) throws DAOException{
		return findResponsavelUnidade(idServidor, -1);
	}

	/**
	 * Método utilizado para buscar unidades por nome.
	 *
	 * @param nome
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Unidade> findByNome(int idUnidadeRaiz, String nome) {
		
		String condicaoUnidade = "";
		
		if (!isEmpty(idUnidadeRaiz)) {
			condicaoUnidade = " and hierarquia_organizacional like '%." + idUnidadeRaiz + ".%'";
		}
		
		return getJdbcTemplate().query("select * from comum.unidade where organizacional = trueValue() and upper(nome_ascii) like upper(?) " + condicaoUnidade + " order by nome asc", new Object[] { nome + "%" }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Unidade u = new Unidade();
				u.setId(rs.getInt("id_unidade"));
				u.setNome(rs.getString("nome"));
				u.setCodigo(rs.getLong("codigo_unidade"));
				u.setSigla(rs.getString("sigla"));
				return u;
			}
		});
	}

	/**
	 * Retornar o maior valor de código dentro da hierarquia passada.
	 */
	public long findMaxCodigo( String base ){
		
		return getJdbcTemplate().queryForLong(
				"SELECT max(codigo_unidade) FROM comum.unidade " +
				"WHERE CAST(codigo_unidade AS VARCHAR) LIKE ? ",
				new Object[]{ base + "__" });
	}
	
	/**
	 * Retorna coleção de unidades conforme uma combinação de informações
	 *
	 * @param codigoSiapecad - código do SIAPECARD
	 * @param codigo - código da unidade
	 * @param nomeLike - Nome da unidade começando por
	 * @param idResponsavelOrcamentaria - Unidades orçamentárias filhas na hierarquia da unidade com este id
	 * @param idResponsavelOrganizacional - Unidades orçamentárias filhas na hierarquia da unidade com este id
	 * @param orcamentarias - Apenas unidades orçamentárias
	 * @param organizacionais - Apenas unidades organizacionais
	 * @param gestoras - Apenas unidades gestoras
	 * @param patrimonial - Apenas unidades patrimoniais
	 * @param protocolizadora - Apenas unidade protocolizadoras
	 * @param onlyWithSiapecad - Apenas unidade com SIAPECARD
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<UnidadeGeral> findByInformacoes(Long codigoSiapecad, Long codigo, String nomeLike, Integer idResponsavelOrcamentaria, Integer[] idsResponsavelOrganizacional,
													  Boolean orcamentarias, Boolean organizacionais, Boolean gestoras, Boolean patrimonial, Boolean protocolizadora, Boolean onlyWithSiapecad) throws DAOException{



		StringBuilder hql = new StringBuilder();
		hql.append("select distinct u.id, u.codigo, u.codigoSiapecad, u.nome, u.sigla,  ur, uro, u.ativo, u.tipo ");
		hql.append("from Unidade u left join u.unidadeResponsavel ur left join u.responsavelOrganizacional uro where u.ativo = true ");

		if (codigo != null && codigo > 0)
			hql.append(" and (u.codigo = :codigo or u.codigoUnidadeGestoraSIAFI = :codigo)");

		if (codigoSiapecad != null && codigoSiapecad > 0)
			hql.append(" and u.codigoSiapecad = :codigoSiapecad ");

		if (onlyWithSiapecad != null && onlyWithSiapecad)
			hql.append(" and u.codigoSiapecad is not null ");

		if (nomeLike != null && !nomeLike.isEmpty()){
			hql.append(" and (u.nomeAscii like :nomeAscii or u.sigla like :nomeAscii)");
		}
		
		if (idResponsavelOrcamentaria != null && idResponsavelOrcamentaria > 0){
			hql.append(" and u.hierarquia like :hierarquia ");
		}
		
		if(!isEmpty(idsResponsavelOrganizacional)){
			hql.append(" AND ( ");

			List<Integer> ids = Arrays.asList(idsResponsavelOrganizacional);
			
			Iterator<Integer> it = ids.iterator();

			while(it.hasNext()){
				Integer id = it.next();
				hql.append(" ( u.hierarquiaOrganizacional LIKE '%."+id+".%' ) ");

				if(it.hasNext())
					hql.append(" OR ");

			}

			hql.append(" ) ");
		}
		
		if (orcamentarias != null){
			hql.append(" and u.unidadeOrcamentaria = :unidadeOrcamentaria ");
		}
		
		if (organizacionais != null){
			hql.append(" and u.organizacional = :organizacional and u.dataExtincao is null ");
		}

		if (gestoras != null && gestoras){
			hql.append(" and u.categoria = :categoria and u.tipo = :tipo ");
		}else if (gestoras != null && !gestoras){
			hql.append(" and u.tipo = :tipo ");
		}

		if (patrimonial != null){
			hql.append(" and u.unidadeSipac = :unidadeSipac ");
		}
		
		if (protocolizadora != null){
			hql.append(" and u.protocolizadora = :unidadeProtocolizadora ");
		}
		
		hql.append("order by u.nome, u.codigo");

		Query q = getSession().createQuery(hql.toString());

		if (codigo != null && codigo > 0)
			q.setLong("codigo", codigo);

		if (codigoSiapecad != null && codigoSiapecad > 0)
			q.setLong("codigoSiapecad", codigoSiapecad);

		if (nomeLike != null && !nomeLike.isEmpty()){
			q.setString("nomeAscii", "%" + StringUtils.toAscii(nomeLike.toUpperCase()) + "%");
		}
		
		if (idResponsavelOrcamentaria != null && idResponsavelOrcamentaria > 0){
			q.setString("hierarquia", "%." + idResponsavelOrcamentaria + ".%");
		}
		
		if (orcamentarias != null){
			q.setBoolean("unidadeOrcamentaria", orcamentarias);
		}
		
		if (organizacionais != null){
			q.setBoolean("organizacional", organizacionais);
		}
		
		if (gestoras != null && gestoras){
			q.setInteger("categoria", CategoriaUnidade.UNIDADE);
			q.setInteger("tipo", UnidadeGeral.UNIDADE_GESTORA);
		}else if (gestoras != null && !gestoras){
			q.setInteger("tipo", UnidadeGeral.UNIDADE_FATO);
		}

		if (patrimonial != null){
			q.setBoolean("unidadeSipac", patrimonial);
		}
		
		if (protocolizadora != null){
			q.setBoolean("unidadeProtocolizadora", protocolizadora);
		}
		
		@SuppressWarnings("unchecked")
		List result = q.list();

		List<UnidadeGeral> unidades = new ArrayList<UnidadeGeral>();

		if (result != null){
			for (int i = 0; i < result.size(); i++) {

				Object[] obj = (Object[]) result.get(i);
				UnidadeGeral u = new UnidadeGeral();
				int j =0;
				u.setId((Integer) obj[j++]);
				u.setCodigo((Long) obj[j++]);
				u.setCodigoSiapecad((Long) obj[j++]);
				u.setNome((String)obj[j++]);
				u.setSigla((String)obj[j++]);

				u.setUnidadeResponsavel((UnidadeGeral)obj[j++]);
				u.setResponsavelOrganizacional((UnidadeGeral)obj[j++]);

				u.setAtivo((Boolean) obj[j++]);

				u.setTipo((Integer)obj[j++]);
				
				unidades.add(u);
			}
		}

		return unidades;
	}


	/**
	 * Busca todas as sub-unidades de uma unidade passada como parâmetro.
	 * @param pai
	 * @param categoria
	 * @param orcamentaria
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Unidade> findBySubUnidades(Unidade pai, int categoria,
			boolean orcamentaria, int tipo) throws DAOException {

			try {
				String hql = "select new Unidade(u.id, u.codigo, u.nome, u.sigla,"
						+ " u.nomeCapa, u.hierarquia, u.organizacional) from Unidade u where 1=1 "
						+ (pai.getId() > 0 ? " and u.unidadeResponsavel = :ID_UNIDADE_PAI" : " ")
						+ (categoria > 0 ? " and u.categoria = :categoria " : " ")
						+ (tipo > 0 ? " and u.tipo = :tipo" : " ")
						+ (orcamentaria ? " and u.unidadeOrcamentaria = trueValue() " : " ")
						+ " ORDER BY u.nomeCapa";

				Query q = getSession().createQuery(hql);
				if (pai.getId() > 0) {
					q.setInteger("ID_UNIDADE_PAI", pai.getId());
				}
				if (categoria > 0) {
					q.setInteger("categoria", categoria);
				}
				if (tipo > 0) {
					q.setInteger("tipo", tipo);
				}


				return q.list();
			} catch (Exception e) {
				throw new DAOException(e);
			}
	}

	/**
	 * Busca todas as sub-unidades ativas de uma unidade passada como parâmetro.
	 * @param pai
	 * @param categoria
	 * @param orcamentaria
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Unidade> findBySubUnidadesAtivas(Unidade pai, int categoria,
			boolean orcamentaria, int tipo) throws DAOException {

			try {
				String hql = "select new Unidade(u.id, u.codigo, u.nome, u.sigla,"
						+ " u.nomeCapa, u.hierarquia, u.organizacional) from Unidade u where 1=1 "
						+ (pai.getId() > 0 ? " and u.unidadeResponsavel = :ID_UNIDADE_PAI" : " ")
						+ (categoria > 0 ? " and u.categoria = :categoria " : " ")
						+ (tipo > 0 ? " and u.tipo = :tipo" : " ")
						+ (orcamentaria ? " and u.unidadeOrcamentaria = trueValue() " : " ")
						+ "and ativo = trueValue() ORDER BY u.nomeCapa";

				Query q = getSession().createQuery(hql);
				if (pai.getId() > 0) {
					q.setInteger("ID_UNIDADE_PAI", pai.getId());
				}
				if (categoria > 0) {
					q.setInteger("categoria", categoria);
				}
				if (tipo > 0) {
					q.setInteger("tipo", tipo);
				}


				return q.list();
			} catch (Exception e) {
				throw new DAOException(e);
			}
	}

	/**
	 * Busca as unidades que são organizacionais e que são filhas da unidade
	 * cujo id foi passado como parâmetro.
	 * @param idUnidade
	 * @param organizacional
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> findFilhasOrganizacionais(int idUnidade, boolean organizacional) throws DAOException {

		try {

			StringBuffer hql = new StringBuffer();
			hql.append("select u.id, u.codigo, u.nome, u.sigla, u.organizacional, u.unidadeResponsavel.id ");
			hql.append("from Unidade u where u.responsavelOrganizacional.id = :idResponsavel and u.ativo = trueValue()");

			if(organizacional==true)
				hql.append("and u.organizacional = trueValue()");

			hql.append("order by u.nome, u.codigo");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idResponsavel", idUnidade);

			@SuppressWarnings("unchecked")
			ArrayList<Object[]> results = (ArrayList<Object[]>) query.list();

			List<Unidade> unidades = new ArrayList<Unidade>();
			Unidade unidade = null;
			for (Object[] result : results) {

				unidade = new Unidade();
				unidade.setId((Integer) result[0]);

				if (!unidades.contains(unidade)) {
					unidade.setCodigo((Long) result[1]);
					unidade.setNome((String) result[2]);
					unidade.setSigla((String) result[3]);
					unidade.setOrganizacional((Boolean) result[4]);
					UnidadeGeral unidadeResponsavel = new UnidadeGeral();
					unidadeResponsavel.setId((Integer) result[5]);
					unidade.setUnidadeResponsavel(unidadeResponsavel);


					unidades.add(unidade);
				} else {
					unidade = unidades.get(unidades.indexOf(unidade));
				}
			}

			return unidades;

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna as unidades filhas da unidade passada como parâmetro. 
	 * 
	 * @param idUnidade
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	@SuppressWarnings("unchecked")
	public Collection<Unidade> findUnidadesFilhas(int idUnidade) throws DAOException {
		Query q = getSession().createQuery("select u from Unidade u where u.unidadeResponsavel.id = ? and u.ativo = true order by u.nome, u.codigo");
		q.setInteger(0, idUnidade);
		return q.list();
	}

}