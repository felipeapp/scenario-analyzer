package br.ufrn.arq.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.dominio.RegistroEntradaDevice;
import br.ufrn.arq.seguranca.log.LogOperacao;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Implementação do DAO de Registro de Entrada
 *
 * @author Gleydson Lima
 *
 */
@SuppressWarnings("unchecked")
public class RegistroEntradaImpl extends GenericDAOImpl implements RegistroEntradaDAO {
	
	private static final int RESULT_QUERY_LIMIT = 200;

	private static final String sql = "select * from comum.registro_entrada re, comum.usuario u, comum.pessoa p "
		+ "where re.id_usuario = u.id_usuario and u.id_pessoa = p.id_pessoa";
	
	/**
	 * RowMapper para converter um ResultSet em um Registro de Entrada.
	 */
	private static final RowMapper REGISTRO_ENTRADA_MAPPER = new RowMapper() {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			RegistroEntrada r = new RegistroEntrada();
			r.setId(rs.getInt("id_entrada"));
			r.setData(rs.getTimestamp("data"));
			r.setUsuario(new UsuarioGeral(rs.getInt("id_usuario")));
			r.getUsuario().setPessoa(new PessoaGeral());
			r.getUsuario().setLogin(rs.getString("login"));
			r.getUsuario().getPessoa().setNome(rs.getString("nome"));
			r.setIP(rs.getString("ip"));
			if (!isEmpty(rs.getInt("id_sistema")))
				r.setSistema(rs.getInt("id_sistema"));
			r.setServer(rs.getString("host"));
			r.setUserAgent(rs.getString("user_agent"));
			r.setResolucao(rs.getString("resolucao"));
			r.setDataSaida(rs.getTimestamp("data_saida"));
			//r.setPassaporte(rs.getInt("passaporte") == 0 ? null : rs.getInt("passaporte"));
			r.setCanal(rs.getString("canal"));
			return r;
		}
	};
	
	/**
	 * RowMapper para converter um ResultSet em um Log Operação.
	 */
	private static final RowMapper LOG_OPERACAO_MAPPER = new RowMapper() {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			LogOperacao log = new LogOperacao();
			log.setAction(rs.getString("action"));
			log.setParameters(rs.getString("parametros"));
			log.setHora(rs.getTimestamp("hora"));
			log.setTempo(rs.getInt("tempo"));
			log.setErro(rs.getBoolean("erro"));
			log.setTracing(rs.getString("trace"));
			log.setId(rs.getInt("id_operacao"));
			log.setIdRegistroEntrada(rs.getInt("id_registro_entrada"));
			log.setMensagens(rs.getString("mensagens"));
			
			int idAcessoPublico = rs.getInt("id_acesso_publico");
			log.setIdRegistroAcessoPublico(idAcessoPublico == 0 ? null : idAcessoPublico);
			return log;
		}
	};
	
	/**
	 * ResultSetExtractor usado pelos métodos que buscam requisições por mês, por dia, por hora, etc.
	 */
	private static final ResultSetExtractor REQUISICOES_EXTRACTOR = new ResultSetExtractor() {
		public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
			Map<Object, Object> result = new TreeMap<Object, Object>();
			while(rs.next()) {
				result.put(rs.getObject(1), rs.getObject(2));
			}
			return result;
		}
	};
	
	public RegistroEntradaImpl() {
		
	}
	
	public RegistroEntradaImpl(int sistema) {
		super(sistema);
	}

	/**
	 * Busca por registros de entradas que tiveram atividades nos ultimos
	 * minutos
	 *
	 * @param minutos
	 * @return
	 */
	public Collection<RegistroEntrada> findByAtividade(int minutos) {
		long dataComparacao = System.currentTimeMillis() - minutos * 60 * 1000;
		Date data = new Date(dataComparacao);

		return getJdbcTemplate(getLogDs()).query("select distinct id_registro_entrada "
				+ "from log_operacao where id_sistema = ? and hora >= ?", new Object[] { getSistema(), data }, new RowMapper() {
					public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
						return findRegistroById(rs.getInt(1));
					}
				});
	}
	
	/**
	 * Busca por logs de operação de acordo com as datas passadas como parâmetro.
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public Collection<LogOperacao> findByDatas(Date dataInicio, Date dataFim) {
		return findByDatas(dataInicio, dataFim, false);
	}

	/**
	 * Busca por logs de operação de acordo com as datas passadas como parâmetro. Pode retornar
	 * apenas os logs que possuem erro, caso o parâmetro apenasErros seja passado como true.
	 * @param dataInicio
	 * @param dataFim
	 * @param apenasErros
	 * @return
	 */
	public Collection<LogOperacao> findByDatas(Date dataInicio, Date dataFim, boolean apenasErros) {
		return getJdbcTemplate(getLogDs()).query("select * from log_operacao where id_sistema = ? and erro = ? and hora >= ? and hora <= ? order by hora asc", 
				new Object[] { getSistema(), apenasErros, dataInicio, dataFim }, LOG_OPERACAO_MAPPER);
	}

	/**
	 * Busca todos os registros de entrada que possuírem o IP passado como parâmetro.
	 * @param ip
	 * @return
	 */
	public Collection<RegistroEntrada> findByIP(String ip) {
		return getJdbcTemplate(getSistema()).query(sql + " and re.ip = ? order by re.data desc", new Object[] { ip }, REGISTRO_ENTRADA_MAPPER);
	}

	/**
	 * Busca todos os registros de entrada do usuário cujo login foi passado como parâmetro.
	 * @param login
	 * @return
	 */
	public Collection<RegistroEntrada> findByLogin(String login) {
		return getJdbcTemplate(getSistema()).query(sql + " and u.login = ? order by re.data desc", new Object[] { login }, REGISTRO_ENTRADA_MAPPER);
	}
	
	/**
	 * Busca os últimos n registros de entrada do usuário cujo login foi passado como parâmetro, onde
	 * n é o parâmetro maxResults.
	 * @param login
	 * @param maxResults
	 * @return
	 */
	public Collection<RegistroEntrada> findByLogin(String login, int maxResults) {
		return getJdbcTemplate(getSistema()).query(sql + " and u.login = ? order by re.data desc " + BDUtils.limit(maxResults), new Object[] { login }, REGISTRO_ENTRADA_MAPPER);
	}

	/**
	 * Busca um log de operação por id;
	 * @param idOperacao
	 * @return
	 */
	public LogOperacao findByOperacao(int idOperacao) {
		return (LogOperacao) getJdbcTemplate(getLogDs()).queryForObject("select * from log_operacao where id_operacao = ?", new Object[] { idOperacao }, LOG_OPERACAO_MAPPER);
	}

	/**
	 * Busca registros de entrada por um período (data inicial e data final).
	 * @param periodoInicio
	 * @param periodoFim
	 * @return
	 */
	public Collection<RegistroEntrada> findByPeriodo(Date periodoInicio, Date periodoFim) {
		return getJdbcTemplate(getSistema()).query(sql + " and re.data between ? and ? order by re.data desc", new Object[] { periodoInicio, periodoFim }, REGISTRO_ENTRADA_MAPPER);
	}

	/**
	 * Busca os registros de entrada que foram abertos nos últimos minutos.
	 * @param minutos
	 * @return
	 */
	public Collection<RegistroEntrada> findByTempo(int minutos) {
		long dataComparacao = System.currentTimeMillis() - minutos * 60 * 1000;
		return getJdbcTemplate(getSistema()).query(sql + " and re.data > ? order by re.data desc", new Object[] { new Date(dataComparacao) }, REGISTRO_ENTRADA_MAPPER);
	}

	/**
	 * Busca pela hora da ultima atividade realizada pelo usuário
	 * cujo registro de entrada foi passado como parâmetro. 
	 * @param idRegistroEntrada
	 * @return
	 */
	public Date findByUltimaAtividade(int idRegistroEntrada) {
		return (Date) getJdbcTemplate(getLogDs()).queryForObject("select max(hora) from log_operacao where id_registro_entrada = ? and id_sistema = ?",
				new Object[] { idRegistroEntrada, getSistema() }, Timestamp.class);
	}

	/**
	 * Busca os log de operações do usuário de acordo com
	 * o registro de entrada passado como parâmetro.
	 *
	 * @param idRegistroEntrada
	 * @return
	 */
	public Collection<LogOperacao> findLogByRegistro(int idRegistroEntrada) {
		return getJdbcTemplate(getLogDs()).query("select * from log_operacao where id_registro_entrada = ? and id_sistema = ? order by hora asc", 
				new Object[] { idRegistroEntrada, getSistema() }, LOG_OPERACAO_MAPPER);
	}

	/**
	 * Busca os logs de operação em que ocorreram erros nos últimos minutos.
	 * @param minutos
	 * @return
	 */
	public Collection<LogOperacao> findLogErro(int minutos) {
		long time = System.currentTimeMillis();
		time -= minutos * 1000 * 60;

		return getJdbcTemplate(getLogDs()).query("select * from log_operacao where erro = trueValue() and hora > ? and id_sistema = ?",
				new Object[] { new Timestamp(time), getSistema() }, LOG_OPERACAO_MAPPER);
	}

	/**
	 * Busca logs de operações de acordo com os parâmetros passados.
	 */
	@Override
	public List<LogOperacao> findLogOperacoes( int sistemaSelecionado, Date data, String url,
			Integer idRegistroEntrada, Integer idRegistroAcessoPublico, String excecao) {
		
		List<Object> params = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder("select * from log_operacao where 1 = 1");
		
		if (data != null) {
			sb.append(" and hora >= ? and hora <= ?");
			params.add(CalendarUtils.descartarHoras(data));
			params.add(CalendarUtils.adicionaUmDia(CalendarUtils.descartarHoras(data)));
		}
		
		if (!isEmpty(url)) {
			sb.append(" and action like ?");
			params.add("%" + url + "%");
		}
		
		if (!isEmpty(idRegistroEntrada)) {
			sb.append(" and id_registro_entrada = ?");
			params.add(idRegistroEntrada);
		}
		
		if (!isEmpty(idRegistroAcessoPublico)) {
			sb.append(" and id_acesso_publico = ?");
			params.add(idRegistroAcessoPublico);
		}
		
		if (!isEmpty(excecao)) {
			sb.append(" and upper(trace) like ?");
			params.add("%" + excecao.toUpperCase() + "%");
		}
		
		sb.append(" and id_sistema = ? order by hora asc");
		
		params.add( sistemaSelecionado );
		
		return getJdbcTemplate(getLogDs()).query(sb.toString(), params.toArray(), LOG_OPERACAO_MAPPER);
	}

	/**
	 * Busca um registro de entrada pelo seu id.
	 * @param idRegistro
	 * @return
	 */
	public RegistroEntrada findRegistroById(int idRegistro) {
		try {
			return (RegistroEntrada) getJdbcTemplate(getSistema())
					.queryForObject(sql + " and re.id_entrada = ?",
							new Object[] { idRegistro }, REGISTRO_ENTRADA_MAPPER);
		} catch (Exception e) {
			return null;
		}
		
	}

	/**
	 * Retorna o datasource do banco de log de acordo com o sistema com
	 * o qual se está trabalhando no momento.
	 * @return
	 */
	private DataSource getLogDs() {
		return Database.getInstance().getLogDs();
	}

	/**
	 * Retorna uma nova instancia do JdbcTemplate de acordo
	 * com o sistema passado como parâmetro. 
	 */
	public JdbcTemplate getJdbcTemplate(int sistema) {
		return new JdbcTemplate(getDataSource(sistema));
	}
	
	@Override
	public JdbcTemplate getJdbcTemplate(DataSource ds) {
		return new JdbcTemplate(ds);
	}
	
	/**
	 * Busca por registros de entrada que possuam referencia aos parâmetros informados em seu log de operações
	 * @param parametros
	 * @param periodoInicio
	 * @param periodoFim
	 * @return
	 */
	public Collection<RegistroEntrada> findByParametros(String parametros, Date periodoInicio, Date periodoFim) {
		Collection<RegistroEntrada> registrosEntrada = new ArrayList<RegistroEntrada>();
		
		StringBuilder where = new StringBuilder();
		
		if (!isEmpty(parametros))
			where.append("parametros like '%" + parametros + "%'");
		
		if (periodoInicio != null && periodoFim != null) {
			String dataInicio = CalendarUtils.format(periodoInicio, "dd/MM/yyyy");
			String dataFim = CalendarUtils.format(periodoFim, "dd/MM/yyyy");
			where.append(" and hora between '" + dataInicio + "' and '" + dataFim + "'");
		}
		
		List<LogOperacao> logOperacoes = findWithRegistroEntradaBy(getSistema(), where.toString(), "hora desc", RESULT_QUERY_LIMIT);
		
		if (logOperacoes != null && logOperacoes.size() > 0)
			registrosEntrada.addAll(getJdbcTemplate(getSistema()).query(sql + " and re.id_entrada in " + UFRNUtils.gerarStringIn(extractRegistroEntradaIDs(logOperacoes)) + " order by re.data desc", new Object[] { }, REGISTRO_ENTRADA_MAPPER));
		
		logOperacoes = null;
		
		return registrosEntrada;
	} 
	
	/**
	 * Busca por registros de entrada que possuam referencia a action informada em seu log de operações
	 * @param parametros
	 * @param periodoInicio
	 * @param periodoFim
	 * @return
	 */
	public Collection<RegistroEntrada> findByAction(String action, Date periodoInicio, Date periodoFim) {
		Collection<RegistroEntrada> registrosEntrada = new ArrayList<RegistroEntrada>();
		
		StringBuilder where = new StringBuilder();
		
		if (!isEmpty(action))
			where.append("action like '%" + action + "%'");
		
		if (periodoInicio != null && periodoFim != null) {
			String dataInicio = CalendarUtils.format(periodoInicio, "dd/MM/yyyy");
			String dataFim = CalendarUtils.format(periodoFim, "dd/MM/yyyy");
			where.append(" and hora between '" + dataInicio + "' and '" + dataFim + "'");
		}
		
		List<LogOperacao> logOperacoes = findWithRegistroEntradaBy(getSistema(), where.toString(), "hora desc", RESULT_QUERY_LIMIT);
		
		if (logOperacoes != null && logOperacoes.size() > 0)
			registrosEntrada.addAll(getJdbcTemplate(getSistema()).query(sql + " and re.id_entrada in " + UFRNUtils.gerarStringIn(extractRegistroEntradaIDs(logOperacoes)) + " order by re.data desc", new Object[] { }, REGISTRO_ENTRADA_MAPPER));
		
		logOperacoes = null;
		
		return registrosEntrada;
	}
	
	/**
	 * Busca pelo conteúdo de uma determinada coluna e seus possível valor em uma das bases de log dos sistemas 
	 * @param sistema
	 * @param field
	 * @param value
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	private List<LogOperacao> findWithRegistroEntradaBy(int sistema, String where, String orderBy, int limit) {
		StringBuilder sb = new StringBuilder("select * from log_operacao where id_sistema = ? and id_registro_entrada is not null ");
		
		if (!isEmpty(where))
			sb.append(" and " + where + "");
		
		if (!isEmpty(orderBy))
			sb.append(" order by " + orderBy);
		
		if (limit > 0)
			sb.append(" limit " + limit);
		
		return getJdbcTemplate(getLogDs()).query(sb.toString(), new Object[] { sistema }, LOG_OPERACAO_MAPPER);
	}
	
	/**
	 * Extrai uma lista de identificadores de registros de entrada a partir de uma lista de log de operações.
	 * @param logOperacoes
	 * @return
	 */
	private Collection<Integer> extractRegistroEntradaIDs(List<LogOperacao> logOperacoes) {
		Collection<Integer> idsRegistroEntrada = new ArrayList<Integer>();
		
		if (logOperacoes != null && logOperacoes.size() > 0) {
			for (LogOperacao o : logOperacoes) {
				if (!idsRegistroEntrada.contains(o.getIdRegistroEntrada()))
					idsRegistroEntrada.add(o.getIdRegistroEntrada());
			}
		}
		
		return idsRegistroEntrada;
	}

	@Override
	public Map<Integer, Double> findRequisicoesDiario(Date data, List<Integer> sistemas) {
		Date atual = CalendarUtils.descartarHoras(data);
		Date amanha = CalendarUtils.adicionaUmDia(atual);
		
		return (Map<Integer, Double>) getJdbcTemplate(getLogDs()).query("select extract(hour from hora), count(*) from log_operacao "
				+ "where hora >= ? and hora < ? and id_sistema IN " + UFRNUtils.gerarStringIn(sistemas) + " group by extract(hour from hora) order by extract(hour from hora)", new Object[] { atual, amanha }, REQUISICOES_EXTRACTOR);
	}

	@Override
	public Map<Integer, Double> findRequisicoesAnual(Integer ano, List<Integer> sistemas) {
		return (Map<Integer, Double>) getJdbcTemplate(getLogDs()).query("select extract(month from hora), count(*) from log_operacao "
				+ "where extract(year from hora) = ? and id_sistema IN " + UFRNUtils.gerarStringIn(sistemas) + " group by extract(month from hora) "
				+ "order by extract(month from hora)", new Object[] { ano }, REQUISICOES_EXTRACTOR);
	}
	
	@Override
	public Map<Integer, Double> findRequisicoesMensal(Integer mes, Integer ano, List<Integer> sistemas) {
		return (Map<Integer, Double>) getJdbcTemplate(getLogDs()).query("select extract(day from hora), count(*) from log_operacao "
				+ "where extract(year from hora) = ? and extract(month from hora) = ? and id_sistema IN " + UFRNUtils.gerarStringIn(sistemas) + " group by extract(day from hora) "
				+ "order by extract(day from hora)", new Object[] { ano, mes }, REQUISICOES_EXTRACTOR);
	}
	
	@Override
	public Map<Integer, Double> findRequisicoesSemanal(Date dataInicio, Date dataFim, List<Integer> sistemas) {
		Date inicio = CalendarUtils.descartarHoras(dataInicio);
		Date fim = CalendarUtils.adicionaUmDia(CalendarUtils.descartarHoras(dataFim));
		
		return (Map<Integer, Double>) getJdbcTemplate(getLogDs()).query("select extract(day from hora), count(*) from log_operacao "
				+ "where hora >= ? and hora < ? and id_sistema IN " + UFRNUtils.gerarStringIn(sistemas) + " group by extract(day from hora) order by extract(day from hora)", new Object[] { inicio, fim }, REQUISICOES_EXTRACTOR);
	}
	
	@Override
	public Map<Integer, Double> findRequisicoesHorario(Date data, Integer hora, List<Integer> sistemas) {
		Date atual = CalendarUtils.descartarHoras(data);
		Date amanha = CalendarUtils.adicionaUmDia(atual);
		
		return (Map<Integer, Double>) getJdbcTemplate(getLogDs()).query("select extract(minute from hora), count(*) from log_operacao "
				+ "where hora >= ? and hora < ? and extract(hour from hora) = ? and id_sistema IN " + UFRNUtils.gerarStringIn(sistemas) + " group by extract(minute from hora) order by extract(minute from hora)", new Object[] { atual, amanha, hora }, REQUISICOES_EXTRACTOR);
	}
	
	/**
	 * Método criado para persistir informações do dispositivo móvel.
	 * @param registroDevice
	 */
	public void registrarDeviceInfo(RegistroEntradaDevice registroDevice) {
		JdbcTemplate jtLog = getJdbcTemplate(getLogDs());
		int id = jtLog.queryForInt("select nextval('registro_device_seq')");
		String sql = "insert into registro_entrada_device (id, id_registro_entrada, device_info) values (?,?,?)";
		jtLog.update(sql, new Object[] { id, registroDevice.getRegistroEntrada().getId(), registroDevice.getDeviceInfo()});
		
	}
	
}
