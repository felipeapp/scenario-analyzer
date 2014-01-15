/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/06/2011
 */
package br.ufrn.sigaa.ava.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.negocio.ConsolidacaoUtil;

/**
 * DAO para realizar buscas de estatísticas de notas dos alunos
 * de uma turma virtual.
 * 
 * @author David Pereira
 *
 */
public class EstatisticasNotasDao extends GenericSigaaDAO {

	private ResultSetExtractor histogramaExtractor = new ResultSetExtractor() {
		public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
			@SuppressWarnings("unchecked")
			Map<Integer, Integer> result = new ListOrderedMap();
			while(rs.next()) {
				result.put(rs.getInt(1), rs.getInt(2));
			}
			return result;
		}
	};
	
	/**
	 * Retorna um mapa em que as chaves são nota de 0 a 10 e o valor
	 * é o número de alunos que obtiveram essa nota para uma determinada unidade.
	 * @param idTurma
	 * @param unidade
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Double, Integer> findHistogramaNotasUnidade(Integer idTurma, Integer unidade) {
		return (Map<Double, Integer>) getJdbcTemplate().query("select round(coalesce(cast(nota as numeric), 0), 0), count(*) from ensino.nota_unidade where ativo = trueValue() and id_matricula_componente "
				+ "in (select id_matricula_componente from ensino.matricula_componente where id_turma = ? and id_situacao_matricula in "+ UFRNUtils.gerarStringIn(ConsolidacaoUtil.getSituacaoesAConsolidar()) +") "
				+ "and unidade = ? group by round(coalesce(cast(nota as numeric), 0), 0) order by round(coalesce(cast(nota as numeric), 0), 0)", new Object[] { idTurma, unidade }, histogramaExtractor);
	}
	
	/**
	 * Verifica se foi lançada alguma nota de recuperação.
	 * Utilizado na renderização do gráfico de notas da recuperação.
	 * 
	 * @param idTurma
	 * @return
	 */
	public boolean hasRecuperacao(Integer idTurma) {
		int qtd = getJdbcTemplate().queryForInt("select count(*) from ensino.matricula_componente where id_turma = ? and recuperacao is not null and id_situacao_matricula in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesMatriculadas()), new Object[] { idTurma });
		return qtd > 0;
	}

	/**
	 * Retorna um mapa em que as chaves são nota de 0 a 10 e o valor
	 * é o número de alunos que obtiveram essa nota para a recuperação.
	 * @param idTurma
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Double, Integer> findHistogramaNotasRecuperacao(Integer idTurma) {
		return (Map<Double, Integer>) getJdbcTemplate().query("select round(coalesce(cast(recuperacao as numeric), 0), 0), count(*) from ensino.matricula_componente where id_turma = ? and id_situacao_matricula in " + UFRNUtils.gerarStringIn(ConsolidacaoUtil.getSituacaoesAConsolidar())
				+ "group by round(coalesce(cast(recuperacao as numeric), 0), 0) order by round(coalesce(cast(recuperacao as numeric), 0), 0)", new Object[] { idTurma }, histogramaExtractor);
	}

	/**
	 * Retorna um mapa em que as chaves são nota de 0 a 10 e o valor
	 * é o número de alunos que obtiveram essa nota para a média final.
	 * @param idTurma
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Double, Integer> findHistogramaNotasMediaFinal(Integer idTurma) {
		return (Map<Double, Integer>) getJdbcTemplate().query("select round(coalesce(cast(media_final as numeric), 0), 0), count(*) from ensino.matricula_componente where id_turma = ? and id_situacao_matricula in " + UFRNUtils.gerarStringIn(ConsolidacaoUtil.getSituacaoesAConsolidar())
				+ "group by round(coalesce(cast(media_final as numeric), 0), 0) order by round(coalesce(cast(media_final as numeric), 0), 0)", new Object[] { idTurma }, histogramaExtractor);
	}

	/**
	 * Retorna a média de notas da unidade passada como parâmetro.
	 * @param idTurma
	 * @param unidade
	 * @return
	 */
	public double findMediaUnidade(Integer idTurma, Integer unidade) {
		return getJdbcTemplate().queryForDouble("select avg(nota) from ensino.nota_unidade where ativo = trueValue() and id_matricula_componente " 
				+ "in (select id_matricula_componente from ensino.matricula_componente where id_turma = ?) and unidade = ?", new Object[] { idTurma, unidade });
	}
	
	/**
	 * Retorna o desvio padrão das notas da unidade passada como parâmetro.
	 * @param idTurma
	 * @param unidade
	 * @return
	 */
	public double findDesvioUnidade(Integer idTurma, Integer unidade) {
		return getJdbcTemplate().queryForDouble("select stddev(nota) from ensino.nota_unidade where ativo = trueValue() and id_matricula_componente " 
				+ "in (select id_matricula_componente from ensino.matricula_componente where id_turma = ?) and unidade = ?", new Object[] { idTurma, unidade });
	}
	
	/**
	 * Retorna a média de notas da recuperação.
	 * @param idTurma
	 * @return
	 */
	public double findMediaRecup(Integer idTurma) {
		return getJdbcTemplate().queryForDouble("select avg(coalesce(recuperacao, 0)) from ensino.matricula_componente where id_turma = ? and id_situacao_matricula in " + UFRNUtils.gerarStringIn(ConsolidacaoUtil.getSituacaoesAConsolidar()), new Object[] { idTurma });
	}
	
	/**
	 * Retorna o desvio padrão das notas da recuperação.
	 * @param idTurma
	 * @return
	 */
	public double findDesvioRecup(Integer idTurma) {
		return getJdbcTemplate().queryForDouble("select stddev(coalesce(recuperacao, 0)) from ensino.matricula_componente where id_turma = ? and id_situacao_matricula in " + UFRNUtils.gerarStringIn(ConsolidacaoUtil.getSituacaoesAConsolidar()), new Object[] { idTurma });
	}
	
	/**
	 * Retorna a média de notas da média final dos alunos.
	 * @param idTurma
	 * @return
	 */
	public double findMediaFinal(Integer idTurma) {
		return getJdbcTemplate().queryForDouble("select avg(coalesce(media_final, 0)) from ensino.matricula_componente where id_turma = ? and id_situacao_matricula in " + UFRNUtils.gerarStringIn(ConsolidacaoUtil.getSituacaoesAConsolidar()), new Object[] { idTurma });
	}
	
	/**
	 * Retorna o desvio padrão da média final dos alunos.
	 * @param idTurma
	 * @return
	 */
	public double findDesvioFinal(Integer idTurma) {
		return getJdbcTemplate().queryForDouble("select stddev(coalesce(media_final, 0)) from ensino.matricula_componente where id_turma = ? and id_situacao_matricula in " + UFRNUtils.gerarStringIn(ConsolidacaoUtil.getSituacaoesAConsolidar()), new Object[] { idTurma });
	}
	
	/**
	 * Retorna a média de notas da unidade passada como parâmetro.
	 * @param idTurma
	 * @param unidade
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<Integer> findUnidadesComNota(Integer idTurma) throws DAOException {
		String hql = "SELECT DISTINCT n.unidade " +
						"FROM NotaUnidade n " +
							"INNER JOIN n.matricula mc " +
							"INNER JOIN mc.turma t " +
						"WHERE t.id = :turma and n.ativo = trueValue() " +
						"ORDER BY n.unidade";
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger("turma", idTurma);
		
		@SuppressWarnings("unchecked")
		List<Integer> list = q.list();
		
		return list;
	}
	
	/**
	 * Busca matrículas em uma turma, trazendo informações da aptidão das matrículas.
	 * 
	 * @param turma
	 * @param quantidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findMatriculadosAptidaoByTurma(Integer turma) throws DAOException {
		String projecao = "mc.id, mc.apto";
		
		String hql = "select " + projecao +
				" from MatriculaComponente mc " +
				" inner join mc.turma turma " +
				" inner join mc.situacaoMatricula situacaoMatricula " +
				" where turma.id = ? " +
				" and situacaoMatricula.id in " + gerarStringIn(SituacaoMatricula.getSituacoesMatriculadas());
		
		
		Query q = getSession().createQuery(hql);
		q.setInteger(0, turma);
		
		@SuppressWarnings("unchecked")
		List <Object[]> valores = q.list();
		
		return HibernateUtils.parseTo(valores, projecao, MatriculaComponente.class, "mc");
	}
	
	/**
	 * Busca uma matrícula em uma turma de forma aleatória.
	 * 
	 * @param idTurma
	 * @param quantidade
	 * @return
	 * @throws DAOException
	 */
	public MatriculaComponente findMatriculaAleatoriaByTurma(Integer idTurma) throws DAOException {
		String projecao = "mc.id, mc.apto, mc.componente.unidade.id, mc.componente.nivel";
		
		String hql = "select " + projecao +
				" from MatriculaComponente mc " +
				" inner join mc.turma turma " +
				" inner join mc.situacaoMatricula situacaoMatricula " +
				" inner join mc.componente componente " +
				" inner join componente.unidade " +
				" where turma.id = ? " +
				" and situacaoMatricula.id in " + gerarStringIn(SituacaoMatricula.getSituacoesPagasEMatriculadas());
		
		
		Query q = getSession().createQuery(hql);
		q.setInteger(0, idTurma);
		
		q.setMaxResults(1);
		
		@SuppressWarnings("unchecked")
		List <Object[]> valores = q.list();
		
		Collection<MatriculaComponente> matriculas = HibernateUtils.parseTo(valores, projecao, MatriculaComponente.class, "mc");
		return matriculas != null && !matriculas.isEmpty() ? matriculas.iterator().next() : null;
	}
	
}
