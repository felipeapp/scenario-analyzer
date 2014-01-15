/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 31/08/2011
 */
package br.ufrn.arq.tasks;

import java.util.concurrent.FutureTask;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import br.ufrn.arq.dao.ThreadScopedResourceCache;
import br.ufrn.arq.util.ReflectionUtils;

/**
 * Executor que utiliza um pool de threads para executar tarefas
 * periódicas. 
 * 
 * @author David Pereira
 *
 */
@Component
public class TarefaExecutor extends ThreadPoolExecutor {
	
	@Autowired
	private GerenciadorExecucaoTarefas execucao;
	
	@Autowired
	private PlatformTransactionManager tx;
	
	public TarefaExecutor() {
		super(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}
	
	/**
	 * Registra o início da tarefa e inicia uma transação
	 */
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		FutureTask<?> future = (FutureTask<?>) r;
		Object sync = ReflectionUtils.getFieldValue(future, "sync");
		Object callable = ReflectionUtils.getFieldValue(sync, "callable");
		TarefaTimer tt = (TarefaTimer) ReflectionUtils.getFieldValue(callable, "task");
		tt.tx = tx;
		
		execucao.iniciarExecucao(tt);
		execucao.info(tt, "Iniciando execução de tarefa");
	}
	
	/**
	 * Registra o fim ou cancelamento de uma tarefa e finaliza a transação,
	 * dando commit ou rollback, dependendo do sucesso da execução da tarefa.
	 */
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		FutureTask<?> future = (FutureTask<?>) r;
		Object sync = ReflectionUtils.getFieldValue(future, "sync");
		Object callable = ReflectionUtils.getFieldValue(sync, "callable");
		TarefaTimer tt = (TarefaTimer) ReflectionUtils.getFieldValue(callable, "task");

		boolean sucesso;
		
		try {
			if (t == null && !tt.cancelada) {
				execucao.info(tt, "Fim da execução da tarefa");
				sucesso = true;
			} else {
				if (t != null) execucao.error(tt, t);
				sucesso = false;
			}
		} catch(Exception e) {
			execucao.error(tt, e);
			sucesso = false;
		}

		ThreadScopedResourceCache.closeResources();
		execucao.finalizarExecucao(tt, sucesso);
	}
	
}