/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 28/05/2008
 */
package br.ufrn.arq.seguranca.log;

import br.ufrn.arq.util.UFRNUtils;


/**
 * Classe para registrar um log utilizando a API do
 * Log4J. 
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class Logger {

	/**
	 * Registra um log com nível debug.
	 * @param log
	 */
	public static void debug(String log) {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UFRNUtils.buscaClasseInvocadora());
		logger.debug(log);
	}
	
	/**
	 * Registra um log com nível info.
	 * @param log
	 */
	public static void info(String log) {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UFRNUtils.buscaClasseInvocadora());
		logger.info(log);
	}

	/**
	 * Registra um log com nível warn.
	 * @param log
	 */
	public static void warn(String log) {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UFRNUtils.buscaClasseInvocadora());
		logger.warn(log);
	}

	/**
	 * Registra um log com nível error.
	 * @param log
	 */
	public static void error(String log) {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UFRNUtils.buscaClasseInvocadora());
		logger.error(log);
	}

	/**
	 * Registra um log com nível fatal.
	 * @param log
	 */
	public static void fatal(String log) {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UFRNUtils.buscaClasseInvocadora());
		logger.fatal(log);
	}
	
}
