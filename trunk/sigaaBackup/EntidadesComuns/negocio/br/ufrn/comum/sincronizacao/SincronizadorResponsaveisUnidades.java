package br.ufrn.comum.sincronizacao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.seguranca.log.LogJdbcUpdate;
import br.ufrn.arq.seguranca.log.LogProcessorDelegate;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.comum.dominio.ResponsavelUnidadeAvaliacao;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe para sincronizar o cadastro de responsáveis feitas no SigAdmin entre
 * as bases de dados COMUM, SIPAC e SIGAA.
 *
 * @author Adriana Alves
 *
 */

public class SincronizadorResponsaveisUnidades {

	/** Conexão do banco com o qual se irá sincronizar os responsáveis */
	private JdbcTemplate jt;
	private UsuarioGeral usuario;
	private int sistema;
	private Comando comando;

	private SincronizadorResponsaveisUnidades(Connection con) {
		if (con != null)
			this.jt = new JdbcTemplate(new SingleConnectionDataSource(con, true));
	}

	private SincronizadorResponsaveisUnidades(DataSource ds) {
		if (ds != null)
			this.jt = new JdbcTemplate(ds);
	}

	private SincronizadorResponsaveisUnidades(JdbcTemplate jt) {
		if (jt != null)
			this.jt = jt;
	}

	public void removerResponsavel(Responsavel r) {
		String sql = "delete from comum.responsavel_unidade where id = ? ";
		String sqlUnidade = "update comum.unidade set id_responsavel = null where id_responsavel = ? ";
		
		Object[] params = new Object[] { r.getId() };
		
		jt.update(sqlUnidade, params);
		jt.update(sql, params);
		
		log(sqlUnidade, params);
		log(sql, params);
	}

	/**
	 * Verifica se o Responsável informado está na base de dados
	 *
	 * @param r
	 * @return
	 * @throws SQLException
	 */
	public boolean hasResponsavel(Responsavel r) {
		int count = jt.queryForInt("select count(*) from comum.responsavel_unidade where id = ? AND ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL ", new Object[] { r.getId() });
		return count > 0;
	}

	/**
	 * Sincroniza registro do responsável de unidade
	 *
	 * @param r
	 * @throws Exception
	 */
	public void sincronizarResponsavel(final Responsavel r) {
		StringBuilder sqlBuilder = new StringBuilder();

		if (hasResponsavel(r)) {

			// Atualização

			sqlBuilder.append("update comum.responsavel_unidade set  ");
			sqlBuilder.append("data_cadastro = ?, ");
			sqlBuilder.append("data_inicio = ?, ");
			sqlBuilder.append("data_fim = ?, ");
			sqlBuilder.append("nivel_responsabilidade = ?, ");
			sqlBuilder.append("id_servidor = ?, ");
			sqlBuilder.append("id_usuario = ?, ");
			sqlBuilder.append("id_unidade = ?, ");
			sqlBuilder.append("id_cargo = ?, ");
			sqlBuilder.append("ultima_atualizacao = ?, ");
			sqlBuilder.append("observacao = ?, ");
			sqlBuilder.append("origem = ?, ");
			sqlBuilder.append("id_designacao = ?, ");
			sqlBuilder.append("id_registro_entrada_exclusao = ?, ");
			sqlBuilder.append("data_exclusao = ? ");
			sqlBuilder.append("where id = ? ");

		} else {
			// Cadastro
			sqlBuilder.append("insert into comum.responsavel_unidade ( ");
			sqlBuilder.append("data_cadastro , ");
			sqlBuilder.append("data_inicio , ");
			sqlBuilder.append("data_fim , ");
			sqlBuilder.append("nivel_responsabilidade , ");
			sqlBuilder.append("id_servidor , ");
			sqlBuilder.append("id_usuario , ");
			sqlBuilder.append("id_unidade , ");
			sqlBuilder.append("id_cargo , ");
			sqlBuilder.append("ultima_atualizacao, ");
			sqlBuilder.append("observacao, ");
			sqlBuilder.append("origem, ");
			sqlBuilder.append("id_designacao, ");
			sqlBuilder.append("id_registro_entrada_exclusao, ");
			sqlBuilder.append("data_exclusao, ");
			sqlBuilder.append("id  ) ");

			sqlBuilder.append("values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)  ");

		}

		jt.update(sqlBuilder.toString(), new PreparedStatementSetter() {
			public void setValues(PreparedStatement st) throws SQLException {
				int j = 1;

				if (r.getDataCadastro() == null)
					st.setNull(j++, Types.DATE);
				else
					st.setTimestamp(j++, new Timestamp(r.getDataCadastro()
							.getTime()));

				st.setTimestamp(j++, new Timestamp(r.getInicio().getTime()));

				if (r.getFim() == null)
					st.setNull(j++, Types.DATE);
				else
					st.setTimestamp(j++, new Timestamp(r.getFim().getTime()));

				if (r.getNivelResponsabilidade() != null)
					st.setString(j++, String.valueOf(r.getNivelResponsabilidade()));
				else
					st.setNull(j++, Types.CHAR);
				
				st.setInt(j++, r.getServidor().getId());

				if (r.getUsuario() != null)
					st.setInt(j++, r.getUsuario().getId());
				else
					st.setNull(j++, Types.INTEGER);

				st.setInt(j++, r.getUnidade().getId());

				if (!isEmpty(r.getCargo()))
					st.setInt(j++, r.getCargo().getId());
				else
					st.setNull(j++, Types.INTEGER);

				st.setTimestamp(j++, new Timestamp(r.getUltimaAtualizacao()
						.getTime()));

				if (r.getObservacao() != null && !r.getObservacao().isEmpty())
					st.setString(j++, r.getObservacao());
				else
					st.setNull(j++, Types.VARCHAR);

				if (r.getOrigem() != null)
					st.setString(j++, String.valueOf(r.getOrigem()));
				else
					st.setNull(j++, Types.CHAR);
				
				if (r.getIdDesignacao() != null && r.getIdDesignacao() > 0)
					st.setInt(j++, r.getIdDesignacao());
				else
					st.setNull(j++, Types.INTEGER);
				
				if (r.getRegistroEntradaExclusao() != null && r.getRegistroEntradaExclusao().getId() > 0)
					st.setInt(j++, r.getRegistroEntradaExclusao().getId());
				else
					st.setNull(j++, Types.INTEGER);
				
				if (r.getDataExclusao() == null)
					st.setNull(j++, Types.DATE);
				else
					st.setTimestamp(j++, new Timestamp(r.getDataExclusao().getTime()));
				
				st.setInt(j++, r.getId());
			}
		});

		log(sqlBuilder.toString());
	}

	/**
	 * Cadastra o responsável Avaliativo da Unidade
	 *
	 * @param r
	 * @return
	 * @throws SQLException
	 */
	public void cadastrarResponsavelAvaliacao(final ResponsavelUnidadeAvaliacao r) {
		Date data = new Date();
		r.setDataCadastro(data);

		String sql = "insert into comum.responsavel_unidade_avaliacao (id_resp_unid_avaliacao, "
			+ "id_unidade , id_usuario_cadastro , data_cadastro , id_responsavel_avaliacao , id_processo_avaliacao ) "
			+ "values ((select nextval('hibernate_sequence')), ?, ?, ?, ?, ?) ";

		jt.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement st) throws SQLException {
					int j = 1;
					st.setInt(j++, r.getUniResponsavelAvaliacao().getId());
					st.setInt(j++, r.getUsuarioCadastro().getId());
					if (r.getDataCadastro() == null)
						st.setTimestamp(j++, new Timestamp(Types.DATE));
					else
						st.setTimestamp(j++, new Timestamp(r.getDataCadastro()
								.getTime()));
					st.setInt(j++, r.getServidorAvaliador().getId());
					st.setInt(j++, r.getProcessoGDH().getId());
				}
			});

		log(sql);
	}

	/**
	 * Inativa o responsável avaliativo da unidade
	 *
	 * @param r
	 * @param
	 * @throws Exception
	 */
	public void removerResponsavelAvaliacao(ResponsavelUnidadeAvaliacao r) {
		String sql = "update comum.responsavel_unidade_avaliacao set data_inativacao = ?, id_usuario_inativacao = ? where id_resp_unid_avaliacao= ? ";
		Object[] params = new Object[] { new Date(), r.getUsuarioCadastro().getId(), r.getId() };
		jt.update(sql, params);
		log(sql, params);
	}

	private void log(String sql, Object... params) {
		Integer idUsuario = usuario == null ? null : usuario.getId();
		Integer idRegistro = (usuario == null || usuario.getRegistroEntrada() == null) ? null : usuario.getRegistroEntrada().getId();

		LogJdbcUpdate log = new LogJdbcUpdate();
		if (comando != null)
			log.setCodMovimento(comando.getId());
		log.setData(new Date());
		log.setSql(sql);
		log.setParams(params);
		log.setSistema(sistema);
		log.setIdUsuario(idUsuario);
		log.setIdRegistroEntrada(idRegistro);

		LogProcessorDelegate.getInstance().writeJdbcUpdateLog(log);
	}

	public static SincronizadorResponsaveisUnidades usandoConexao(Connection con) {
		if (con == null) {
			return MOCK;
		} else {
			return new SincronizadorResponsaveisUnidades(con);
		}
	}

	public static SincronizadorResponsaveisUnidades usandoJdbcTemplate(JdbcTemplate jt) {
		 if (jt == null) {
			 return MOCK;
		 } else {
			 return new SincronizadorResponsaveisUnidades(jt);
		 }
	 }

	public static SincronizadorResponsaveisUnidades usandoConexao(Connection con, Movimento mov, int sistema) {
		SincronizadorResponsaveisUnidades sincronizador = usandoConexao(con);
		sincronizador.usuario = mov.getUsuarioLogado();
		sincronizador.sistema = sistema;
		sincronizador.comando = mov.getCodMovimento();
		return sincronizador;
	}

	public static SincronizadorResponsaveisUnidades usandoDataSource(DataSource ds) {
		 if (ds == null) {
			 return MOCK;
		 } else {
			 return new SincronizadorResponsaveisUnidades(ds);
		 }
	 }

	public static SincronizadorResponsaveisUnidades usandoDataSource(DataSource ds, Movimento mov, int sistema) {
		SincronizadorResponsaveisUnidades sincronizador = usandoDataSource(ds);
		sincronizador.usuario = mov.getUsuarioLogado();
		sincronizador.sistema = sistema;
		sincronizador.comando = mov.getCodMovimento();
		return sincronizador;
	 }

	public static SincronizadorResponsaveisUnidades usandoJdbcTemplate(JdbcTemplate jt, Movimento mov) {
		SincronizadorResponsaveisUnidades sincronizador = usandoJdbcTemplate(jt);
		sincronizador.comando = mov.getCodMovimento();
		sincronizador.usuario = mov.getUsuarioLogado();
		sincronizador.sistema = 0;
		 return sincronizador;
	 }

	private static final SincronizadorResponsaveisUnidades MOCK = new SincronizadorResponsaveisUnidades((DataSource) null) {
		public void cadastrarResponsavelAvaliacao(ResponsavelUnidadeAvaliacao r) { }
		public boolean hasResponsavel(Responsavel r) { return false; }
		public void removerResponsavel(Responsavel r) { }
		public void removerResponsavelAvaliacao(ResponsavelUnidadeAvaliacao r) { }
		public void sincronizarResponsavel(Responsavel r) { }
	};

}
