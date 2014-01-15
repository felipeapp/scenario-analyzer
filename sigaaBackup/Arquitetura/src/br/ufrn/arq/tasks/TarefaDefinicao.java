/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 30/08/2011
 */
package br.ufrn.arq.tasks;

import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import br.ufrn.arq.util.AmbienteUtils;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe contendo as definições dos timers que estão registrados
 * nos sistemas. 
 * 
 * @author David Pereira
 *
 */
public final class TarefaDefinicao {

	Logger logger = Logger.getLogger(getClass());
	
	public static final char TEMPO = 'T';

	public static final char DIARIO = 'D';

	public static final char SEMANAL = 'S';

	public static final char MENSAL = 'M';
	
	/** Identificador */
	int id;
	
	/** Classe que implementa a tarefa a ser executada. Essa classe deve estender TarefaTimer. */
	String classe;

	/** 
	 * Expressão do CRON que define quando a tarefa deverá ser executada. 
	 * Uma visão geral da construção de expressões do CRON pode ser encontrada nos
	 * seguintes endereços: 
	 * 
	 * http://en.wikipedia.org/wiki/Cron#CRON_expression
	 * http://sanjaal.com/java/tag/cron-expression-every-minute/ 
	 */
	String expressaoCron;

	/**
	 * Servidor em que a tarefa será executada
	 */
	String servidorRestricao;

	// usado pelo escalonador
	/**
	 * Armazena a data e hora da última execução do timer. 
	 */
	DateTime ultimaExecucao;
	
	/**
	 * Executar a tarefa compulsoriamente na próxima execução do TarefaScheduler
	 */
	boolean executarAgora;

	
	
	public UsuarioGeral getUsuario() {
		return new UsuarioGeral(UsuarioGeral.TIMER_SISTEMA);
	}
	
	/**
	 * A tarefa deve ser executada no servidor que está rodando o Scheduler no momento.
	 * 
	 * @return
	 */
	public boolean deveExecutarNesteServidor() {
		String servidorRestricao = AmbienteUtils.getInstanceName();
		
		if (servidorRestricao == null)
			servidorRestricao = NetworkUtils.getLocalName();
		
		return this.servidorRestricao == null || servidorRestricao != null && servidorRestricao.equals(this.servidorRestricao);
	}

	/**
	 * A tarefa está na hora de ser executada.
	 * @return
	 */
	public boolean estaNaHoraDeExecutar() {
		if (executarAgora) {
			return true;
		} else {
			Date dataAtual = new Date();
			
			try {
			
				CronExpression cron = new CronExpression(expressaoCron);
				
				logger.debug("Executando tarefa '" + classe + "' -- Expressão: " + cron.getExpressionSummary()
						+ " / Satisfeita pela data atual? " + cron.isSatisfiedBy(dataAtual)
						+ " / Próxima data válida: " + cron.getNextValidTimeAfter(dataAtual));

				return cron.isSatisfiedBy(dataAtual);
				
			} catch (ParseException e) {
				logger.error("Não foi possível executar a tarefa '" + classe + "' porque a expressão cron utilizada (" + expressaoCron + ") é inválida.");
				return false;
			}			
			
		}
	}

	/**
	 * Indica que a tarefa já está em execução neste servidor
	 * @return
	 */
	public boolean estaSendoExecutada() {
		return TarefaScheduler.EM_EXECUCAO.containsKey(classe);
	}

}