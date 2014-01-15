/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 21/03/2005
 */
package br.ufrn.arq.seguranca.log;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Queue;
import java.util.ResourceBundle;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;

/**
 * Classe consumidora dos logs pendentes. Os logs são gerados pelos diversos casos de usos 
 * e são enfileirados na fila recebida como parâmetro do LogConsumor.
 * 
 * Esta Thread é um Consumidor que no momento que há logs para consumir ela registra-o no local 
 * específico. 
 *
 * @author Gleydson Lima
 *
 */
public class LogConsumer extends Thread {

	Queue<? extends LogUFRN> queue;

	private boolean isLogMirror = ParametroHelper.getInstance().getParametroBoolean(ConstantesParametroGeral.REPLICAR_BASE_LOGS);
	
	private static String schema;
	
	private static final String ESQUEMA_PADRAO_LOG = "public";

	public LogConsumer(Queue<? extends LogUFRN> logQueue) {
		queue = logQueue;
		schema = obterSchema();
	}

	@Override
	public void run() {

		while (true) {
			LogUFRN log = null;
			synchronized (queue) {
				if (!queue.isEmpty()) {
					log = queue.poll();
				} else {
					try {
						queue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if (log != null) {
				writeLog(log);
			}
		}

	}

	/**
	 * Insere um log no banco de dados.
	 * @param log
	 */
	public void writeLog(LogUFRN log) {

		Connection conPadrao = null;
		Connection conReplica = null;

		if (isEmpty(log.getSistema()) && log instanceof LogDB) {
			LogDB logDB = (LogDB) log;
			if (logDB.getTabela().contains(".sigaa.")) {
				logDB.setSistema(Sistema.SIGAA);
			} else {
				logDB.setSistema(Sistema.SIPAC);
			}
		}
		
		try {
			conPadrao = Database.getInstance().getLogConnection();
			if (isLogMirror)
				conReplica = Database.getInstance().getLogMirrorConnection();
		} catch (SQLException e) {
			System.err.println("Erro ao obter base de Log do SIGAA");
			return;
		}

		try {

			if (log instanceof LogDB) {
				// Loga operaçao no banco de dados
				gravaLogDB((LogDB) log, conPadrao);
				if (isLogMirror) {
					gravaLogDB((LogDB) log, conReplica);
				}
			}

			if (log instanceof LogJdbcUpdate) {
				// Loga updates realizados com JDBC
				gravaLogJdbcUpdate((LogJdbcUpdate) log, conPadrao);
				if (isLogMirror) {
					gravaLogJdbcUpdate((LogJdbcUpdate) log, conReplica);	
				}
			}
			
			if (log instanceof LogMovimento) {
				// Grava Log de Movimento
				gravaLogMovimento((LogMovimento) log, conPadrao);
				if (isLogMirror) {
					gravaLogMovimento((LogMovimento) log, conReplica);	
				}
			}

			if (log instanceof LogOperacao) {
				// Loga operação no banco de dados de Log
				gravaLogOperacao((LogOperacao) log, conPadrao);
				if (isLogMirror) {
					gravaLogOperacao((LogOperacao) log, conReplica);	
				}
			}

		} catch (Exception e) {
			System.err.println("Erro de Log " + e.getMessage());
		} finally {
			Database.getInstance().close(conPadrao);
			if (isLogMirror) {
				Database.getInstance().close(conReplica);
			}
			
		}

	}

	/**
	 * Insere um log de updates JDBC no banco de dados
	 * @param log
	 * @param con
	 */
	public void gravaLogJdbcUpdate(LogJdbcUpdate log, Connection con) {
		
		try {
			PreparedStatement st = con.prepareStatement("insert into " + schema +"."+ "log_jdbc_update (id, sql, id_registro_entrada, "
					+ "id_usuario, data, cod_movimento, sistema, params) values ((select nextval('jdbc_update_seq')), ?, ?, ?, ?, ?, ?, ?)");
			st.setString(1, log.getSql());
			
			if (log.getIdRegistroEntrada() != null)
				st.setInt(2, log.getIdRegistroEntrada());
			else
				st.setNull(2, Types.INTEGER);
			
			st.setInt(3, log.getIdUsuario());
			st.setTimestamp(4, new Timestamp(log.getData().getTime()));
			st.setInt(5, log.getCodMovimento());
			st.setInt(6, log.getSistema());
			st.setString(7, Arrays.toString(log.getParams()));
			
			st.executeUpdate();
			st.close();
		} catch(Exception e) {
			System.out.println(log);
			e.printStackTrace();
		} finally {
			Database.getInstance().close(con);
		}
		
	}

	/**
	 * Grava o Log de alteração de registros no banco
	 *
	 * @param logDB
	 * @param con
	 */
	public void gravaLogDB(LogDB logDB, Connection con) {

		try {
			
			String sqlLog = null;
			
			if ( logDB.getOperacao() == 'R' ) {
				sqlLog = "INSERT INTO " + schema +"."+ "LOG_DB_LEITURA "
					+ " (ID_ELEMENTO, ID_REGISTRO_ENTRADA, DATA, TABELA, COD_MOVIMENTO, ID_USUARIO, id_sistema, id_turma_virtual) "
					+ " VALUES (?,?,?,?,?,?,?,?)";

			} else {
				sqlLog = "INSERT INTO " + schema +"."+ "LOG_DB "
				+ " (OPERACAO, ID_ELEMENTO, ID_REGISTRO_ENTRADA, DATA, TABELA, COD_MOVIMENTO, ALTERACOES, ID_USUARIO, id_sistema ) "
				+ " VALUES (?,?,?,?,?,?,?,?,?)";
			}
			
			
			PreparedStatement st = con.prepareStatement(sqlLog);
			
			if ( logDB.getOperacao() == 'R' ) {
				
				st.setInt(1, logDB.getIdElemento());
				st.setInt(2, logDB.getIdRegistroEntrada());
				st.setTimestamp(3, new Timestamp(logDB.getData().getTime()));
				st.setString(4, logDB.getTabela());
				st.setInt(5, logDB.getCodMovimento());
				st.setInt(6, logDB.getIdUsuario());
				st.setInt(7, logDB.getSistema());
				st.setInt(8, logDB.getTurmaVirtual());
			
			} else {
			
				st.setString(1, String.valueOf(logDB.getOperacao()));
				st.setInt(2, logDB.getIdElemento());
				st.setInt(3, logDB.getIdRegistroEntrada());
				st.setTimestamp(4, new Timestamp(logDB.getData().getTime()));
				st.setString(5, logDB.getTabela());
				st.setInt(6, logDB.getCodMovimento());
				st.setString(7, logDB.getAlteracao());
				st.setInt(8, logDB.getIdUsuario());
				st.setInt(9, logDB.getSistema());
			}
			
			st.executeUpdate();

			st.close();

		} catch (Exception e) {
			System.out.println(logDB);
			e.printStackTrace();
		} finally {
			Database.getInstance().close(con);
		}

	}

	/**
	 * Grava os logs de chamadas aos processadores no banco de dados.
	 * @param logMovimento
	 * @param con
	 */
	public void gravaLogMovimento(LogMovimento logMovimento, Connection con) {

		try {
			PreparedStatement st = con
					.prepareStatement("INSERT INTO "+ schema +"."+ "LOG_MOVIMENTO "
							+ "(COD_MOVIMENTO, DATA, ID_MOVIMENTO, ID_REGISTRO_ENTRADA, id_sistema ) "
							+ " VALUES (?,?,?,?,?)");

			st.setInt(1, logMovimento.getCodMovimento());
			st.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
			st.setInt(3, logMovimento.getIdMovimento());
			st.setInt(4, logMovimento.getRegistroEntrada());
			st.setInt(5, logMovimento.getSistema());

			st.executeUpdate();

			st.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Database.getInstance().close(con);
		}


	}

	/**
	 * Grava o log das operações realizadas pelos usuários no banco de dados.
	 * @param logOperacao
	 * @param con
	 */
	public void gravaLogOperacao(LogOperacao logOperacao, Connection con) {
		
		try {
			PreparedStatement st = con
					.prepareStatement("INSERT INTO " + schema +"."+ "LOG_OPERACAO "
							+ "(ID_REGISTRO_ENTRADA, ACTION, PARAMETROS, TEMPO, ERRO, TRACE, MENSAGENS, ID_ACESSO_PUBLICO, id_sistema ) "
							+ " VALUES (?,?,?,?,?,?,?,?,?)");
			
			if (logOperacao.getIdRegistroEntrada() != null)
				st.setInt(1, logOperacao.getIdRegistroEntrada());
			else
				st.setNull(1, Types.INTEGER);
			st.setString(2, logOperacao.getAction());
			st.setString(3, logOperacao.getParameters());
			st.setInt(4, (int) logOperacao.getTempo());
			if (logOperacao.getErro() != null) {
				st.setBoolean(5, logOperacao.getErro());
			} else {
				st.setBoolean(5, false);
			}
			st.setString(6, logOperacao.getTracing());
			st.setString(7, logOperacao.getMensagens());
			if (logOperacao.getIdRegistroAcessoPublico() != null)
				st.setInt(8, logOperacao.getIdRegistroAcessoPublico());
			else
				st.setNull(8, Types.INTEGER);
			
			st.setInt(9, logOperacao.getSistema());
			
			st.executeUpdate();

			st.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Database.getInstance().close(con);
		}
	}
	
	public String obterSchema()
	{	
		
		try {
			ResourceBundle config = ResourceBundle.getBundle("br.ufrn.arq.dao.banco");		
			schema = ValidatorUtil.isEmpty(config.getString("bancos_log_schema")) ? ESQUEMA_PADRAO_LOG : config.getString("bancos_log_schema");
		} catch (Exception e) {
			schema = ESQUEMA_PADRAO_LOG;
			e.printStackTrace();
		}
		
		return schema;		
	}

}