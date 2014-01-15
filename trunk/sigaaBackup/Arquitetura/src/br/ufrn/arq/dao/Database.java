/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 05/01/2005
 */
package br.ufrn.arq.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.compass.gps.device.jdbc.CannotGetJdbcConnectionException;

import br.ufrn.comum.dao.SistemaDao;
import br.ufrn.comum.dominio.Sistema;

/**
 * Singleton Utilitário para conexões com o BD
 *
 * @author Gleydson Lima
 * @author David Pereira
 *
 */
public class Database {

	/** Implementação do padrão Singleton */
	private static Database singleton;

	/** Permite o retorno da conexão com o banco dado o JNDI */
	private InitialContext ic;

	/** DataSources das bases */
	private DataSource logDs, arquivosDs, infraRecursos, logMirrorDs;
	
	/** Mapa que contém informações dos data sources de todos os sistemas */
	private Map<Integer, DataSource> dataSources = new ConcurrentHashMap<Integer, DataSource>();

	public static final String SESSION_ATRIBUTE = "hibernateSession";

	/**
	 * Usado para utilizar a configuração de teste que conecta diretamente ao banco.
	 */
	private static boolean directConnectionMode = false;

	private Database() {
		if (!directConnectionMode) {
			initDataSourceJndi();
		} else {
			initDataSourceDbcp();
		}
	}

	public synchronized static Database getInstance() {
		if (singleton == null)
			 singleton = new Database();
		return singleton;
	}
	
	/**
	 * Configura o sistema para usar conexão com o banco através
	 * de um pool com Commons DBCP, em vez de usar o pool do JBoss.
	 */
	public static void setDirectMode() {
		directConnectionMode = true;
	}
	
	/*
	 * Inicia os DataSources fazendo lookup JNDI dos datasources
	 * do JBoss.
	 */
	private void initDataSourceJndi() {
		try {
			ic = new InitialContext();
		} catch (NamingException e) {
			e.printStackTrace();
		}

		try {
			DataSource ds = (DataSource) ic.lookup("java:/jdbc/ComumDB");
			dataSources.put(Sistema.COMUM, ds);
		} catch (Exception e) {
			System.err.println("Erro no ComumDB");
		}
		
		try {
			logDs = (DataSource) ic.lookup("java:/jdbc/LogOperacaoDB");
		} catch (Exception e) {
			System.err.println("Erro ao instanciar LogOperacaoDB");
		}

		try {
			infraRecursos = (DataSource) ic.lookup("java:/jdbc/INFRADB");
		} catch (Exception e) {
			System.err.println("INFRADB");
		}

		try {
			arquivosDs = (DataSource) ic.lookup("java:/jdbc/ArquivosDB");
		} catch (Exception e) {
			System.err.println("Base de arquivos não configurada");
		}
		
		@SuppressWarnings("unchecked")
		Collection<Sistema> sistemas = new JdbcTemplate(getDataSource(Sistema.COMUM)).query("select * from comum.sistema s where s.ativo = trueValue() order by s.descricao asc", SistemaDao.SISTEMA_MAPPER);

		for (Sistema sistema : sistemas) {
			if (dataSources.get(sistema.getId()) == null && !isEmpty(sistema.getNomeDataSourceJndi())) {
				try {
					DataSource ds = (DataSource) ic.lookup(sistema.getNomeDataSourceJndi());
					dataSources.put(sistema.getId(), ds);
				} catch (NamingException e) {
					System.err.println("Não foi possível localizar o datasource do sistema \"" + sistema.getNome() + "\"");
				}
			}
		}
	}
	
	/*
	 * Inicia os DataSources instanciando um DataSource
	 * do Commons DBCP.
	 */
	private void initDataSourceDbcp() {
		logDs = createDataSource("jdbc:postgresql://bddesenv.info.ufrn.br:5432/administrativo_desenv", "sipac", "sipacDesenv");
		infraRecursos = createDataSource("jdbc:postgresql://bddesenv.info.ufrn.br:5432/administrativo_desenv", "sipac", "sipacDesenv");
		arquivosDs = createDataSource("jdbc:postgresql://bddesenv.info.ufrn.br:5432/base_arquivos", "arquivos", "arquivos");
		
		DataSource ds = createDataSource("jdbc:postgresql://bddesenv.info.ufrn.br:5432/administrativo_desenv", "sipac", "sipacDesenv");
		dataSources.put(Sistema.SIPAC, ds);
		
		ds = createDataSource("jdbc:postgresql://bddesenv.info.ufrn.br:5432/sigaa_desenv", "sigaa", "sigaa");
		dataSources.put(Sistema.SIGAA, ds);
		
		ds = createDataSource("jdbc:postgresql://bddesenv.info.ufrn.br:5432/sistemas_comum_desenv", "comum_user", "comum_user");
		dataSources.put(Sistema.COMUM, ds);
		
		ds = createDataSource("jdbc:postgresql://bddesenv.info.ufrn.br:5432/administrativo_desenv", "sipac", "sipacDesenv");
		dataSources.put(Sistema.SIGRH, ds);
		
		ds = createDataSource("jdbc:postgresql://desenvolvimento3.info.ufrn.br:5432/ambientes", "ambientes", "ambientes");
		dataSources.put(Sistema.AMBIENTES, ds);
	}
	
	private DataSource createDataSource(String url, String username, String password) {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("org.postgresql.Driver");
		ds.setUrl(url);
		ds.setUsername(username);
		ds.setPassword(password);
		return ds;
	}
	
	/** Conexão com a base de dados acadêmica 
	 * @throws SQLException */
	public Connection getSigaaConnection() {
		return getConnection(Sistema.SIGAA);
	}
	
	/** Conexão com a base de dados administrativa 
	 * @throws SQLException */
	public Connection getSipacConnection() {
		return getConnection(Sistema.SIPAC);
	}
	
	/** Conexão com a base de dados comum 
	 * @throws SQLException */
	public Connection getComumConnection() {
		return getConnection(Sistema.COMUM);
	}
	
	/** Conexão com a base de dados do SIGPRH 
	 * @throws SQLException */
	public Connection getSIGRHConnection() {
		return getConnection(Sistema.SIGRH);
	}
	
	/** Conexão com a base de dados de projetos da Infra Estrutura */
	public Connection getInfraConnection() throws SQLException {
		if (infraRecursos == null)
			throw new SQLException("Conexão com base de projetos não configurada");
		return infraRecursos.getConnection();
	}

	/** Conexão com a base de dados de log */
	public Connection getLogConnection() throws SQLException {
		if (logDs == null)
			throw new SQLException("Conexão com base de log não configurada");
		return logDs.getConnection();
	}

	/** Conexão com a base de dados de arquivos */
	public Connection getArquivosConnection() throws SQLException {
		if (arquivosDs == null)
			throw new SQLException("Conexão com base de arquivos ");
		return arquivosDs.getConnection();
	}

	/** Conexão com a base de dados de log (réplica) */
	public Connection getLogMirrorConnection() throws SQLException {
		if (logMirrorDs == null)
			throw new SQLException("Conexão com base de log não configurada");
		return logMirrorDs.getConnection();
	}

	/**
	 * Retorna o datasource para o sistema passado como parâmetro.
	 * @param sistema
	 * @return
	 */
	public DataSource getDataSource(Sistema sistema) {
		return getDataSource(sistema.getId());
	}
	
	/**
	 * Retorna o datasource para o sistema passado como parâmetro.
	 * @param sistema
	 * @return
	 */
	public DataSource getDataSource(int sistema) {
		return dataSources.get(sistema);
	}
	
	/**
	 * Retorna uma conexão de acordo com o sistema passado como parâmetro.
	 * @param sistema
	 * @return
	 * @throws SQLException 
	 */
	public Connection getConnection(Sistema sistema) throws SQLException {
		return getConnection(sistema.getId());
	}
	
	/**
	 * Retorna uma conexão de acordo com o sistema passado como parâmetro.
	 * @param sistema
	 * @return
	 * @throws SQLException 
	 */
	public synchronized Connection getConnection(int sistema) {
		return getConnection(sistema, true);
	}
	
	/**
	 * Retorna uma conexão de acordo com o sistema passado como parâmetro.
	 * @param sistema
	 * @return
	 * @throws SQLException 
	 */
	public synchronized Connection getConnection(int sistema, boolean criarNova) {
		if (!criarNova) {
			try {
				Map<Integer, Connection> connections = ThreadScopedResourceCache.connectionsCache.get();
				
				if (connections == null) {
					connections = new ConcurrentHashMap<Integer, Connection>();
					ThreadScopedResourceCache.connectionsCache.set(connections);
				}
				
				if (connections.get(sistema) == null || connections.get(sistema).isClosed()) {
					DataSource ds = getDataSource(sistema);
					if (ds == null)
						throw new CannotGetJdbcConnectionException("Erro ao pegar conexão: datasource não configurado (sistema = " + sistema + ").");
					Connection con = getDataSource(sistema).getConnection();
					connections.put(sistema, con);
				}
					
				return connections.get(sistema);
			} catch(SQLException e) {
				throw new CannotGetJdbcConnectionException(e.getMessage(), e);
			}
		} else {
			try {
				return getDataSource(sistema).getConnection();
			} catch (SQLException e) {
				throw new CannotGetJdbcConnectionException(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Fecha a conexão passada como parâmetro.
	 * @param con
	 */
	public void close(Connection con) {
		try {
			if (con != null) { con.close(); }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fecha o Statement passada como parâmetro.
	 * @param con
	 */
	public void close(Statement st) {
		try {
			if (st != null) { st.close(); }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Fecha o Statement passada como parâmetro.
	 * @param con
	 */
	public void close(ResultSet rs) {
		try {
			if (rs != null) { rs.close(); }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}		
	
	public DataSource getComumDs() {
		return getDataSource(Sistema.COMUM);
	}

	public DataSource getSigaaDs() {
		return getDataSource(Sistema.SIGAA);
	}

	public DataSource getSipacDs() {
		return getDataSource(Sistema.SIPAC);
	}

	public DataSource getSigrhDs() {
		return getDataSource(Sistema.SIGRH);
	}

	public DataSource getLogDs() {
		return logDs;
	}

	public DataSource getArquivosDs() {
		return arquivosDs;
	}

	public DataSource getInfraRecursos() {
		return infraRecursos;
	}

	public DataSource getAmbientesDs() {
		return getDataSource(Sistema.AMBIENTES);
	}

}