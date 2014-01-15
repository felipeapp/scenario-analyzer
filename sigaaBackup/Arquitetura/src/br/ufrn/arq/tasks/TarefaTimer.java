package br.ufrn.arq.tasks;

import java.util.concurrent.Future;

import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import br.ufrn.arq.negocio.FacadeDelegate;

/**
 * Classe base das tarefas. As tarefas nos casos de uso devem herdar essa classe
 * e definir os tempos de execução no registro do método
 * TarefaScheduler.registrar
 *
 * @author Gleydson Lima
 *
 */
public abstract class TarefaTimer implements Runnable {

	/** Fachada para chamar o modelo */
	protected FacadeDelegate facade;
	
	/** Application context do Spring */
	protected ApplicationContext ac;
	
	/** Status da transação na qual a tarefa está executando */
	protected TransactionStatus status;
	
	/** Id da execução atual da tarefa */
	protected int idExecucao;
	
	/** Id da tarefa */
	protected int idTarefa;

	/** Objeto para controle da execução da tarefa, permitindo o seu cancelamento, por exemplo. */
	protected Future<?> future;
	
	/** Indica que a tarefa foi cancelada */
	protected boolean cancelada;
	
	/** Expressão do CRON que indica quando a tarefa deverá ser executada. */
	protected String expressaoCron;

	/** Gerenciador de transações. Possibilita que o método transacao() seja executado de forma transacional. */
	protected PlatformTransactionManager tx;

	/** Gerenciador de execução de tarefas para registro de logs */
	private GerenciadorExecucaoTarefas gerenciador;

	/**
	 * Método que adiciona transação à execução do método execucaoTransacional().
	 * @param args
	 * @return
	 * @throws Exception 
	 */
	public Object transacao(Object... args) throws Exception {
		status = tx.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
		
		try {
			Object result = execucaoTransacional(args);
			tx.commit(status);
			return result;
		} catch(Exception e) {
			tx.rollback(status);
			throw e;
		}
		
	}
	
	/**
	 * Método que deve ser sobrescrito pelas classes filhas e cujo conteúdo
	 * será executado dentro de uma transação.
	 * @param args
	 * @return
	 */
	public Object execucaoTransacional(Object... args) throws Exception {
		return null;
	}
	
	/**
	 * Interrompe a execução da tarefa.
	 */
	public void interrupt() {
		cancelada = true;
		future.cancel(true);
	}
	
	/**
	 * Registra um log de informação para a execução da tarefa.
	 * @param texto
	 */
	protected void info(String texto) {
		getGerenciador().info(this, texto);
	}
	
	/**
	 * Registra um log de debug para a execução da tarefa.
	 * @param texto
	 */
	protected void debug(String texto) {
		getGerenciador().debug(this, texto);	
	}
	
	/**
	 * Registra um log de aviso para a execução da tarefa.
	 * @param texto
	 */
	protected void warn(String texto) {
		getGerenciador().warn(this, texto);
	}
	
	/**
	 * Registra um log de erro para a execução da tarefa.
	 * @param t
	 */
	protected void error(Throwable t) {
		getGerenciador().error(this, t);
	}
	
	private GerenciadorExecucaoTarefas getGerenciador() {
		if (gerenciador == null)
			gerenciador = (GerenciadorExecucaoTarefas) ac.getBean("gerenciadorExecucaoTarefas");
		return gerenciador;
	}
	
}
