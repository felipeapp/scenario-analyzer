/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 20/05/2008
 */
package br.ufrn.arq.tasks;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.arq.util.ReflectionUtils;

/**
 * Escalonador que põe as tarefas em execução
 * 
 * @author Gleydson Lima
 * @author David Pereira
 * @author Johnny Marçal
 * 
 */
@Component
public class TarefaScheduler extends Thread implements ApplicationContextAware {

	public static final Map<String, TarefaTimer> EM_EXECUCAO = new ConcurrentHashMap<String, TarefaTimer>();
	
	private Collection<TarefaDefinicao> registradas;
	
	@Autowired
	private GerenciadorExecucaoTarefas service;

	@Autowired
	private TarefaExecutor executor;
	
	private FacadeDelegate facade;
	
	private ApplicationContext ac;

	@Override
	public void run() {

		while (true) {

			try {
				Thread.sleep(1 * 60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Carrega a lista de tarefas que serão executadas
			registradas = service.carregarTarefasParaExecucao();

			for (TarefaDefinicao t : registradas) {
				// Verifica se a tarefa realmente deve ser executada 
				if (!t.estaSendoExecutada() && t.deveExecutarNesteServidor() && t.estaNaHoraDeExecutar()) {
					TarefaTimer tt = criaNovaTarefa(t);
					Future<?> future = executor.submit(tt);
					tt.future = future; // Para podermos ter controle sobre a execução da tarefa e possamos cancelá-la, se for solicitado.
				}
			}
		}
	}

	/**
	 * Cria uma nova instância da tarefa definida no objeto passado como parâmetro.
	 * @param t
	 * @return
	 */
	private TarefaTimer criaNovaTarefa(TarefaDefinicao t) {
		TarefaTimer tt = ReflectionUtils.newInstance(t.classe);
		tt.facade = facade;
		tt.ac = ac;
		tt.idTarefa = t.id;
		tt.expressaoCron = t.expressaoCron;
		return tt;
	}

	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		this.ac = ac;
	}
	
	public void setFacade(FacadeDelegate facade) {
		this.facade = facade;
	}
	
}
