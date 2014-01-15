/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 21/05/2009
 */
package br.ufrn.arq.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dominio.RegistroAcessoPublico;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.log.LogOperacao;
import br.ufrn.comum.dominio.Sistema;

/**
 * Implementação do DAO de Registro de Acesso Público
 *
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@SuppressWarnings("unchecked")
public class RegistroAcessoPublicoDao extends GenericDAOImpl {

	private static final String sql = "select re.*, s.descricao from infra.registro_acesso_publico re, comum.sistema s where re.sistema = s.id ";
	
	private static final RowMapper mapper = new RowMapper() {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			RegistroAcessoPublico r = new RegistroAcessoPublico();
			r.setId(rs.getInt("id"));
			r.setData(rs.getTimestamp("data"));
			r.setIP(rs.getString("ip"));
			r.setSistema(rs.getInt("sistema"));
			r.setNomeSistema(rs.getString("descricao"));
			r.setServer(rs.getString("host"));
			r.setUserAgent(rs.getString("user_agent"));
			r.setCanal(rs.getString("canal"));
			return r;
		}
	};
	
	public DataSource getLogDs() {
		return Database.getInstance().getLogDs();
	}

	/**
	 * Busca a lista de logs de operação associados a um registro de acesso público.
	 * @param idRegistroAcessoPublico
	 * @param dsLog
	 * @return
	 * @throws DAOException
	 */
	public Collection<LogOperacao> findLogByRegistro(int idRegistroAcessoPublico, DataSource dsLog) throws DAOException {
		return getJdbcTemplate(dsLog).query("select * from log_operacao where id_acesso_publico = ? order by hora asc", new Object[] { idRegistroAcessoPublico }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				LogOperacao log = new LogOperacao();
				log.setAction(rs.getString("action"));
				log.setParameters(rs.getString("parametros"));
				log.setHora(rs.getTimestamp("hora"));
				log.setTempo(rs.getInt("tempo"));
				log.setErro(rs.getBoolean("erro"));
				log.setTracing(rs.getString("trace"));
				log.setId(rs.getInt("id_operacao"));
			
				return log;
			}
		});
	}

	/**
	 * Busca todos os logs que tiveram erro nos últimos minutos.
	 * @param minutos
	 * @return
	 * @throws DAOException
	 */
	public Collection<LogOperacao> findLogErro(int minutos) throws DAOException {
		long time = System.currentTimeMillis();
		time -= minutos * 1000 * 60;

		return getJdbcTemplate(getLogDs()).query("select * from log_operacao where erro = trueValue() and hora > ? ", new Object[] { new Timestamp(time) }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				LogOperacao log = new LogOperacao();
				log.setAction(rs.getString("action"));
				log.setParameters(rs.getString("parametros"));
				log.setHora(rs.getTimestamp("hora"));
				log.setTempo(rs.getInt("tempo"));
				log.setTracing(rs.getString("trace"));
				log.setId(rs.getInt("id_operacao"));
				log.setIdRegistroEntrada(rs.getInt("id_registro_entrada"));
				
				return log;
			}
		});
	}

	/**
	 * Busca a data do último log do registro de acesso público passado como parâmetro.
	 * @param idRegistroAcessoPublico
	 * @return
	 * @throws DAOException
	 */
	public Date findByUltimaAtividade(int idRegistroAcessoPublico) throws DAOException {
		try {
			return (Date) getJdbcTemplate(getLogDs()).queryForObject("select max(hora) from log_operacao where id_registro_entrada = ?", new Object[] { idRegistroAcessoPublico }, Date.class);
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * Busca um log de operação pela sua chave primária.
	 * @param idOperacao
	 * @return
	 * @throws DAOException
	 */
	public LogOperacao findByOperacao(int idOperacao) throws DAOException {
		try {
			return (LogOperacao) getJdbcTemplate(getLogDs()).queryForObject("select * from log_operacao where id_operacao = ?", new Object[] { idOperacao }, new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					LogOperacao log = new LogOperacao();
					log.setAction(rs.getString("action"));
					log.setParameters(rs.getString("parametros"));
					log.setHora(rs.getTimestamp("hora"));
					log.setTempo(rs.getInt("tempo"));
					log.setErro(rs.getBoolean("erro"));
					log.setTracing(rs.getString("trace"));
					log.setId(rs.getInt("id_operacao"));
	
					return log;
				}
			});
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * Busca todos os logs de operação entre duas datas passadas como parâmetro.
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 */
	public Collection<LogOperacao> findByDatas(Date dataInicio, Date dataFim) throws DAOException {
		return findByDatas(dataInicio, dataFim, false);
	}

	/**
	 * Busca todos os logs de operação entre duas datas com a possibilidade de filtrar ou não
	 * apenas os logs de erro.
	 * @param dataInicio
	 * @param dataFim
	 * @param apenasErros
	 * @return
	 * @throws DAOException
	 */
	public Collection<LogOperacao> findByDatas(Date dataInicio, Date dataFim, boolean apenasErros) throws DAOException {
		String sql = "select * from log_operacao where erro = ? and hora >= ? and hora <= ? order by hora asc";
		return getJdbcTemplate(getLogDs()).query(sql, new Object[] { apenasErros, dataInicio, dataFim }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				LogOperacao log = new LogOperacao();
				log.setAction(rs.getString("action"));
				log.setParameters(rs.getString("parametros"));
				log.setHora(rs.getTimestamp("hora"));
				log.setTempo(rs.getInt("tempo"));
				log.setErro(rs.getBoolean("erro"));
				log.setTracing(rs.getString("trace"));
				log.setId(rs.getInt("id_operacao"));
				return log;
			}
		});
	}

	/**
	 * Busca todos os registros de acesso público entre duas datas para o sistema
	 * passado como parâmetro.
	 * @param sistemaSelecionado
	 * @param inicio
	 * @param fim
	 * @return
	 */
	public Collection<RegistroAcessoPublico> findRegistrosByDatas(Integer sistemaSelecionado, Date inicio, Date fim) {
		return getJdbcTemplate(Sistema.COMUM).query(sql + " and re.sistema = ? and re.data >= ? and re.data <= ? order by re.data desc", new Object[] { sistemaSelecionado, inicio, fim }, mapper);
	}

	/**
	 * Busca todos os registros de acesso público associados ao sistema passado como
	 * parâmetro.
	 * @param sistemaSelecionado
	 * @return
	 */
	public Collection<RegistroAcessoPublico> findBySistema(Integer sistemaSelecionado) {
		return getJdbcTemplate(Sistema.COMUM).query(sql + " and re.sistema = ? order by re.data desc", new Object[] { sistemaSelecionado }, mapper);
	}

	/**
	 * Busca um registro de acesso público pela sua chave primária.
	 * @param idRegistro
	 * @return
	 */
	public RegistroAcessoPublico findRegistroById(int idRegistro) {
		try {
			return (RegistroAcessoPublico) getJdbcTemplate(Sistema.COMUM).queryForObject(sql + " and re.id = ?", new Object[] { idRegistro }, mapper);
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * Insere um registro de acesso público no banco de dados.
	 * @param registro
	 */
	public void inserir(RegistroAcessoPublico registro) {
		int id = getJdbcTemplate(Sistema.COMUM).queryForInt("select nextval('acesso_publico_seq')");
		getJdbcTemplate(Sistema.COMUM).update("insert into infra.registro_acesso_publico (id, data, ip, sistema, host, user_agent, resolucao, canal) values (?, ?, ?, ?, ?, ?, ?, ?)",
				new Object[] { id, registro.getData(), registro.getIP(), registro.getSistema(), registro.getServer(), registro.getUserAgent(), registro.getResolucao(), registro.getCanal() });
		registro.setId(id);
	}

}
