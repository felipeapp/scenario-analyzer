/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 31/08/2011
 */
package br.ufrn.arq.tasks;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.util.AmbienteUtils;

/**
 * Thread que procura tarefas que foram marcadas para cancelamento
 * e, se elas estiverem sendo executadas no servidor da thread, 
 * interrompe a sua execução.
 * 
 * @author David Pereira
 *
 */
public class CancelamentoTarefasScheduler extends Thread {

	private JdbcTemplate jt = new JdbcTemplate(Database.getInstance().getComumDs());
	
	@Override
	public void run() {

		while(true) {
		
			try {
				Thread.sleep(1 * 60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> defs = jt.queryForList("select rt.*, u.login, p.nome from infra.registro_timer rt left join comum.usuario u on (u.id_usuario = rt.id_usuario_cancelamento) left join comum.pessoa p using (id_pessoa) where rt.cancelar = trueValue()");
			for (Map<String, Object> def : defs) {
				TarefaTimer tt = TarefaScheduler.EM_EXECUCAO.get(def.get("classe"));
				if (tt != null) { // Apenas as tarefas que estão em execução neste servidor
					tt.interrupt();
					
					jt.update("insert into infra.registro_timer_log (id, id_execucao, data_log, texto_log, nivel_log) values ((select nextval('infra.registro_timer_log_seq')), ?, ?, ?, ?)", 
							new Object[] { tt.idExecucao, new Date(), "Tarefa interrompida manualmente por " + def.get("nome") + " (" + def.get("login") + ")", "W" });
					
					TarefaScheduler.EM_EXECUCAO.remove(tt.getClass().getName());
					
					jt.update("update infra.registro_timer_execucao set fim_execucao=?, sucesso=? where id = ?", new Object[] { new Date(), false, tt.idExecucao });
					jt.update("update infra.registro_timer set cancelar=false, id_usuario_cancelamento=null, ultima_execucao = ?, executar_agora = falseValue(), em_execucao = falseValue(), servidor_execucao = ? where id = ?", new Object[] { new Date(), AmbienteUtils.getNomeServidorComInstancia(), tt.idTarefa });
				}
			}
			
		}
		
	}

}
