/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 31/08/2011
 */
package br.ufrn.arq.tasks;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.NetworkUtils;

/**
 * Classe para gerenciar a execução das tarefas, fazendo registro
 * de quando elas começam, quando terminam, quando são canceladas, etc.
 * 
 * @author David Pereira
 *
 */
@Component
public class GerenciadorExecucaoTarefas {

	public static final char LOG_DEBUG = 'D';
	public static final char LOG_INFO = 'I';
	public static final char LOG_WARN = 'W';
	public static final char LOG_ERROR = 'E';
	
	@Autowired
	private TarefaTimerDao dao;
	
	/**
	 * Registra o início da execução de uma tarefa.
	 * @param tt
	 */
	public void iniciarExecucao(TarefaTimer tt) {
		TarefaScheduler.EM_EXECUCAO.put(tt.getClass().getName(), tt);
		
		int idExecucao = dao.inserirExecucao(tt);
		tt.idExecucao = idExecucao;
	}
	
	/**
	 * Adiciona um log de informação à execução da tarefa
	 * @param tt
	 * @param log
	 */
	public void info(TarefaTimer tt, String log) {
		dao.inserirLog(tt, log, LOG_INFO);
	}
	
	/**
	 * Adiciona um log de debug à execução da tarefa
	 */
	public void debug(TarefaTimer tt, String log) {
		dao.inserirLog(tt, log, LOG_DEBUG);
	}
	
	/**
	 * Adiciona um log de erro à execução da tarefa
	 * @param tt
	 * @param t
	 */
	public void error(TarefaTimer tt, Throwable t) {
		dao.inserirLog(tt, t.getMessage(), LOG_ERROR);
		enviarEmailException(tt.getClass().getName(), (Exception) t);
	}
	
	/**
	 * Adiciona um log de warning à execução da tarefa
	 */
	public void warn(TarefaTimer tt, String log) {
		dao.inserirLog(tt, log, LOG_WARN);
	}
	
	/**
	 * Registra o fim da execução de uma tarefa.
	 * @param tt
	 */
	public void finalizarExecucao(TarefaTimer tt, boolean sucesso) {
		TarefaScheduler.EM_EXECUCAO.remove(tt.getClass().getName());
		dao.finalizarExecucao(tt, sucesso);
	}
	/**
	 * Envia e-mail informando que houve um erro na execução de uma tarefa.
	 * 
	 * @param nomeTimer
	 * @param e
	 */
	private void enviarEmailException(String nomeTimer, Exception e) {
		String email = ParametroHelper.getInstance().getParametro(
				ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "ERRO AO EXECUTAR TIMER " + nomeTimer + " em " + new Date();
		String mensagem = "Server: "
				+ NetworkUtils.getLocalName()
				+ "<br>"
				+ e.getMessage()
				+ "<br><br><br>"
				+ Arrays.toString(e.getStackTrace()).replace(",", "\n")
				+ (e.getCause() != null ? Arrays.toString(e.getCause().getStackTrace()).replace(",", "\n")
						: "");

		MailBody mail = new MailBody();
		mail.setEmail(email);
		mail.setAssunto(assunto);
		mail.setMensagem(mensagem);

		Mail.send(mail);
	}

	/**
	 * Busca a lista de tarefas que podem ser executadas no momento.
	 * @return
	 */
	public Collection<TarefaDefinicao> carregarTarefasParaExecucao() {
		Collection<TarefaDefinicao> tarefas = dao.carregarTarefasParaExecucao();
		
		for (Iterator<TarefaDefinicao> it = tarefas.iterator(); it.hasNext(); ) {
			TarefaDefinicao tarefa = it.next();
			
			try {
				Class.forName(tarefa.classe);
			} catch(ClassNotFoundException e) {
				it.remove();
			} catch(Exception e) {
				enviarEmailException(tarefa.classe, e);
			}
		}
		
		return tarefas;
	}

}
