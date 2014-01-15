/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 14/01/2010
 */
package br.ufrn.arq.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;

import br.ufrn.arq.tasks.TimerProfiling;

/**
 * DAO chamado pelo {@link TimerProfiling} para gravar
 * as informações de profiling no banco de dados.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class ProfilingDao {

	private JdbcTemplate jt;

	/**
	 * Atualiza o número de vezes que um método de uma classe foi chamado.
	 * @param classe
	 * @param metodo
	 * @param countCall
	 */
	public void atualizaCountCall(String classe, String metodo, int countCall) {
		try {
			int count = getJdbcTemplate().queryForInt("select count_call from infra.profiling where classe = ? and metodo = ?", new Object[] { classe, metodo });
			getJdbcTemplate().update("update infra.profiling set count_call = ? where classe = ? and metodo = ?", new Object[] { count + countCall, classe, metodo });
		} catch(EmptyResultDataAccessException e) {
			getJdbcTemplate().update("insert into infra.profiling (id, classe, metodo, count_call, max_time, mean_time, mean_time_count, ultima_atualizacao) "
					+ "values ((select nextval('infra.seq_profiling')), ?, ?, ?, 0, 0, 0, now())", new Object[] { classe, metodo, countCall });
		}
	}

	/**
	 * Atualiza o tempo máximo de execução de um método de uma classe.
	 * @param classe
	 * @param metodo
	 * @param maxTime
	 */
	public void atualizaMaxTime(String classe, String metodo, long maxTime) {
		try {
			long time = getJdbcTemplate().queryForInt("select max_time from infra.profiling where classe = ? and metodo = ?", new Object[] { classe, metodo });
			if (maxTime > time) {
				getJdbcTemplate().update("update infra.profiling set max_time = ? where classe = ? and metodo = ?", new Object[] { maxTime, classe, metodo });
			}
		} catch(EmptyResultDataAccessException e) {
			getJdbcTemplate().update("insert into infra.profiling (id, classe, metodo, count_call, max_time, mean_time, mean_time_count, ultima_atualizacao) "
					+ "values ((select nextval('infra.seq_profiling')), ?, ?, 0, ?, 0, 0, now())", new Object[] { classe, metodo, maxTime });
		}
	}

	/**
	 * Atualiza o tempo médio de execução de um método de uma classe.
	 * @param classe
	 * @param metodo
	 * @param count
	 * @param meanTime
	 */
	@SuppressWarnings("unchecked")
	public void atualizaMeanTime(String classe, String metodo, int count, long meanTime) {
		try {
			Map<String, Object> time = getJdbcTemplate().queryForMap("select mean_time, mean_time_count from infra.profiling where classe = ? and metodo = ?", new Object[] { classe, metodo });
			Long timeAtual = (Long) time.get("mean_time");
			Integer countAtual = (Integer) time.get("mean_time_count");
			
			int novoCount = count + countAtual;
			if (novoCount == 0) novoCount = 1;
			long novoMean = ((timeAtual * countAtual) + (meanTime * count)) / novoCount;
			
			getJdbcTemplate().update("update infra.profiling set mean_time = ?, mean_time_count = ? where classe = ? and metodo = ?", new Object[] { novoMean, novoCount, classe, metodo });
		} catch(EmptyResultDataAccessException e) {
			getJdbcTemplate().update("insert into infra.profiling (id, classe, metodo, count_call, max_time, mean_time, mean_time_count, ultima_atualizacao) "
					+ "values ((select nextval('infra.seq_profiling')), ?, ?, 0, 0, ?, ?, now())", new Object[] { classe, metodo, meanTime, count });
		}
	}

	private JdbcTemplate getJdbcTemplate() {
		if (jt == null) jt = new JdbcTemplate(Database.getInstance().getComumDs());
		return jt;
	}

	/**
	 * Busca informações de profiling no banco de dados e as retorna em uma lista. 
	 * @param classe
	 * @param metodo
	 * @param chamadas
	 * @param tempo
	 * @param ordenacao
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> buscar(String classe, String metodo, Integer chamadas, Long tempo, String ordenacao) {
		StringBuilder sb = new StringBuilder("select * from infra.profiling where 1 = 1 ");
		List<Object> params = new ArrayList<Object>();
		if (!isEmpty(classe)) {
			sb.append("and classe like ? ");
			params.add("%" + classe + "%");
		}
		if (!isEmpty(metodo)) {
			sb.append("and metodo like ? ");
			params.add("%" + metodo + "%");
		}
		if (!isEmpty(chamadas)) {
			sb.append("and count_call >= ? ");
			params.add(chamadas);
		}
		if (!isEmpty(tempo)) {
			sb.append("and mean_time >= ? ");
			params.add(tempo);
		}

		if (!isEmpty(ordenacao))
			sb.append("order by " + ordenacao);
		
		return getJdbcTemplate().queryForList(sb.toString(), params.toArray());
	}
	
}
