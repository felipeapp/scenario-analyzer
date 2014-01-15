/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 30/03/2005
 */
package br.ufrn.arq.seguranca.log;

import java.util.Date;
import java.util.LinkedList;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe que encapsula a lógica de Gravador de Logs
 * 
 * @author Gleydson Lima
 * 
 */
public class LogProcessorDelegate {

	private static LogProcessorDelegate singleton;

	/** Logs relacionados a ações feitas em processadores */
	private java.util.Queue<LogMovimento> movimentos;

	/** Log relacionado a uma operação sobre uma coluna de uma tabela da base de dados */
	private java.util.Queue<LogDB> databaseLog;
	
	/** Log relacionado a updates realizados no banco de dados com jdbc */
	private java.util.Queue<LogJdbcUpdate> jdbcLog;

	/** Log relacionado ao conjunto de passos efetuados pelo usuário ao longo de um registro de entrada */
	private java.util.Queue<LogOperacao> operacaoLog;

	private LogProcessorDelegate() {

		movimentos = new LinkedList<LogMovimento>();
		databaseLog = new LinkedList<LogDB>();
		jdbcLog = new LinkedList<LogJdbcUpdate>();
		operacaoLog = new LinkedList<LogOperacao>();

		new LogConsumer(movimentos).start();
		new LogConsumer(databaseLog).start();
		new LogConsumer(operacaoLog).start();
		new LogConsumer(jdbcLog).start();
		
		// connect();
	}

	public static synchronized LogProcessorDelegate getInstance() {

		try {
			if (singleton == null) {
				singleton = new LogProcessorDelegate();
			}
			return singleton;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * Adiciona um log na fila para consumo
	 * 
	 * @param mov
	 */
	public void writeMovimentoLog(Movimento mov) {

		try {

			if (mov.getUsuarioLogado() != null
					&& mov.getUsuarioLogado().getRegistroEntrada() != null) {
				LogMovimento log = new LogMovimento();
				log.setCodMovimento(mov.getCodMovimento().getId());
				log.setData(new Date());
				log.setSistema(mov.getSistema());
				log.setIdMovimento(mov.getId());
				log.setRegistroEntrada(mov.getUsuarioLogado()
						.getRegistroEntrada().getId());

				synchronized (movimentos) {
					movimentos.add(log);
					movimentos.notifyAll();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeDatabaseLog(LogDB log) {

		synchronized (databaseLog) {
			databaseLog.add(log);
			databaseLog.notifyAll();
		}

	}
	
	public void writeJdbcUpdateLog(LogJdbcUpdate log) {

		synchronized (jdbcLog) {
			jdbcLog.add(log);
			jdbcLog.notifyAll();
		}

	}

	/**
	 * Adiciona na fila o log do banco de dados
	 * 
	 * @param persist
	 * @param operacao
	 * @param codMovimento
	 * @param user
	 * @param sistema
	 */
	public void writeDatabaseLog(PersistDB persist, char operacao, int codMovimento, UsuarioGeral user, int sistema, String alteracoes) {

		try {

			LogDB log = new LogDB();
			log.setData(new Date());
			log.setOperacao(operacao);
			log.setIdElemento(persist.getId());
			log.setCodMovimento(codMovimento);
			log.setTabela(persist.getClass().getName());
			log.setSistema(sistema);
			log.setAlteracao(alteracoes);

			if (user != null) {
				log.setIdUsuario(user.getId());
				if (user.getRegistroEntrada() != null)
					log.setIdRegistroEntrada(user.getRegistroEntrada().getId());
			}

			synchronized (databaseLog) {
				databaseLog.add(log);
				databaseLog.notifyAll();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adiciona na fila o log do banco de dados
	 * 
	 * @param classObj
	 * @param codigo
	 * @param operacao
	 * @param codMovimento
	 * @param user
	 * @param sistema
	 * @param alteracoes
	 */
	public void writeDatabaseLog(Class<?> classObj, int codigo,  char operacao,
			int codMovimento, UsuarioGeral user, int sistema, String alteracoes) {

		try {

			LogDB log = new LogDB();
			log.setData(new Date());
			log.setOperacao(operacao);
			log.setCodMovimento(codMovimento);
			log.setIdElemento(codigo);
			log.setTabela(classObj.getName());
			log.setSistema(sistema);
			log.setAlteracao(alteracoes);

			if (user != null) {
				log.setIdUsuario(user.getId());
				if (user.getRegistroEntrada() != null)
					log.setIdRegistroEntrada(user.getRegistroEntrada().getId());
			}

			synchronized (databaseLog) {
				databaseLog.add(log);
				databaseLog.notifyAll();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Log as operações solicitadas pelos usuários na web
	 * 
	 * @param log
	 */
	public void writeOperacaoLog(LogOperacao log) {

		synchronized (operacaoLog) {
			operacaoLog.add(log);
			operacaoLog.notifyAll();
		}

	}
	
	/**
	 * Faz o log de visualização a uma entidade. 
	 *  
	 * @param classe
	 * @param id
	 * @param registroEntrada
	 * @param sistema
	 */
	public void writeAcessoLog(String classe, int idEntidade, UsuarioGeral user,  int sistema, int idTurmaVirtual) {
		
		LogDB log = new LogDB();
		log.setData(new Date());
		log.setCodMovimento(0);
		log.setOperacao('R');
		log.setSistema(sistema);
		log.setTabela(classe);
		log.setTurmaVirtual(idTurmaVirtual);
		
		log.setIdElemento(idEntidade);
		if ( user != null) {
			log.setIdRegistroEntrada( user.getRegistroEntrada().getId() );
			log.setIdUsuario(user.getId());
		}
		
		LogProcessorDelegate.getInstance().writeDatabaseLog(log);
	
	}

}