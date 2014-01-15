/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 10/02/2010
 */
package br.ufrn.arq.tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.util.AmbienteUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * DAO para busca de informações de timers no banco de dados.
 * 
 * @author David Pereira
 *
 */
@Component
public class TarefaTimerDao {

	private JdbcTemplate jt;
	
	@PostConstruct
	public void init() {
		jt = new JdbcTemplate(Database.getInstance().getComumDs());
	}
	
	/**
	 * Remove um timer do banco de dados
	 * @param id
	 */
	public void remover(int id) {
		jt.update("delete from infra.registro_timer where id = ?", new Object[] { id });
	}
	
	/**
	 * Insere ou atualiza um timer no banco de dados. Insere se o id passado
	 * como parâmetro for 0 e atualiza caso contrário.
	 * @param id
	 * @param classe
	 * @param repeticao
	 * @param diaExecucao
	 * @param horaExecucao
	 * @param tempo
	 * @param ativa
	 * @param servidor
	 */
	public void salvar(int id, String classe, String expressaoCron, Boolean ativa, String servidor) {
		if (id == 0) {
			jt.update("insert into infra.registro_timer (id, classe, expressao_cron, ativa, servidor_restricao) values ((select nextval('hibernate_sequence')), ?, ?, ?, ?)", new Object[] { classe, expressaoCron, ativa, servidor });
		} else {
			jt.update("update infra.registro_timer set classe = ?, expressao_cron = ?, ativa = ?, servidor_restricao = ? where id = ?", new Object[] { classe, expressaoCron, ativa, servidor, id });
		}
	}

	/**
	 * Busca informações de todos os timers do banco de dados ordenado
	 * pelo nome da classe que implementa o timer.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findAll() {
		return jt.queryForList("select * from infra.registro_timer order by classe asc");
	}

	/**
	 * Busca as informações do timer cujo id for igual
	 * ao id passado como parâmetro.
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> findById(int id) {
		return jt.queryForMap("select * from infra.registro_timer where id = ?", new Object[] { id });
	}
	
	/**
	 * Marca uma tarefa para ser executada na próxima execução do TarefaScheduler.
	 * @param id
	 */
	public void marcarParaExecucao(int id) {
		jt.update("update infra.registro_timer set executar_agora = trueValue() where id = ?", new Object[] { id });
	}

	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<TarefaDefinicao> carregarTarefasParaExecucao() {
		return (Collection<TarefaDefinicao>) jt.query("select * from infra.registro_timer where (em_execucao is null or em_execucao = falseValue()) and ativa = trueValue()", new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TarefaDefinicao tarefa = new TarefaDefinicao();
				tarefa.id = rs.getInt("id");
				tarefa.classe = rs.getString("classe");
				tarefa.expressaoCron = rs.getString("expressao_cron");
				tarefa.servidorRestricao = rs.getString("servidor_restricao");
				tarefa.executarAgora = rs.getBoolean("executar_agora");

				if(!ValidatorUtil.isEmpty(rs.getTimestamp("ultima_execucao")))
					tarefa.ultimaExecucao = new DateTime(rs.getTimestamp("ultima_execucao").getTime());
				
				return tarefa;
			}
		});
	}

	/**
	 * Adiciona ao banco de dados informações que indicam que a tarefa passada como parâmetro começou a ser executada.
	 * @param tt
	 * @return
	 */
	public int inserirExecucao(TarefaTimer tt) {
		int idExecucao = jt.queryForInt("select nextval('infra.registro_timer_execucao_seq')");
		jt.update("insert into infra.registro_timer_execucao (id, id_tarefa, inicio_execucao, servidor_execucao) values (?, ?, ?, ?)", new Object[] { idExecucao, tt.idTarefa, new Date(), AmbienteUtils.getNomeServidorComInstancia() });
		jt.update("update infra.registro_timer set em_execucao = trueValue() where id = ?", new Object[] { tt.idTarefa });
		return idExecucao;
	}

	/**
	 * Adiciona ao banco de dados informações que indicam que a tarefa passada como parâmetro foi finalizada.
	 * @param tt
	 * @param sucesso
	 */
	public void finalizarExecucao(TarefaTimer tt, boolean sucesso) {
		jt.update("update infra.registro_timer_execucao set fim_execucao=?, sucesso=? where id = ?", new Object[] { new Date(), sucesso, tt.idExecucao });
		jt.update("update infra.registro_timer set ultima_execucao = ?, executar_agora = falseValue(), em_execucao = falseValue(), servidor_execucao = ? where id = ?", new Object[] { new Date(), AmbienteUtils.getNomeServidorComInstancia(), tt.idTarefa });
	}

	/**
	 * Adiciona um log à execução de uma tarefa
	 * @param tt
	 * @param log
	 * @param logInfo
	 */
	public void inserirLog(TarefaTimer tt, String log, char logInfo) {
		jt.update("insert into infra.registro_timer_log (id, id_execucao, data_log, texto_log, nivel_log) values ((select nextval('infra.registro_timer_log_seq')), ?, ?, ?, ?)", new Object[] { tt.idExecucao, new Date(), log, String.valueOf(logInfo) });
	}

	/**
	 * Sinaliza que a tarefa deve ser cancelada na próxima execução do CancelamentoTarefasTimer
	 * @param idTarefa
	 * @param usuario
	 */
	public void marcarParaCancelamento(int idTarefa, UsuarioGeral usuario) {
		jt.update("update infra.registro_timer set cancelar = trueValue(), id_usuario_cancelamento=? where id = ?", new Object[] { usuario.getId(), idTarefa });		
	}

	/**
	 * Lista todas as execuções de uma tarefa
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> buscarExecucoesTimer(int id) {
		return jt.queryForList("select * from infra.registro_timer_execucao where id_tarefa = ? order by inicio_execucao desc", new Object[] { id });
	}

	/**
	 * Lista todos os logs de uma execução de uma tarefa
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> buscarLogsExecucao(int id) {
		return jt.queryForList("select * from infra.registro_timer_log where id_execucao = ? order by data_log asc", new Object[] { id });
	}

	/**
	 * Retorna as informações de uma execução específica de uma tarefa.
	 * @param idExecucao
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> findExecucao(int idExecucao) {
		return jt.queryForMap("select * from infra.registro_timer_execucao where id = ?", new Object[] { idExecucao });
	}
	
}
