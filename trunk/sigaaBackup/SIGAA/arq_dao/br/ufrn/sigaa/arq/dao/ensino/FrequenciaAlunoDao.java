/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 12/12/2006
 *
 */
package br.ufrn.sigaa.arq.dao.ensino;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.EstacaoChamadaBiometrica;
import br.ufrn.sigaa.ava.dominio.EstacaoChamadaBiometricaTurma;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 *
 * Dao de Frequência de Aluno
 *
 * @author David Ricardo
 *
 */
@SuppressWarnings("unchecked")
public class FrequenciaAlunoDao extends GenericSigaaDAO {
 
	
	/**
	 * Retorna a lista de frequências por discente, agrupando por nome.
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public Map<String, Set<FrequenciaAluno>> findFrequenciasByTurma(Turma turma) throws DAOException {

		Map<String, Set<FrequenciaAluno>> mapa = new TreeMap<String, Set<FrequenciaAluno>>();

		try {
			Criteria c = getCriteria(FrequenciaAluno.class);
			c.add(Expression.eq("turma", turma));
			c.add(Restrictions.eq("ativo", true));
			c.createCriteria("discente");
			List<FrequenciaAluno> result = c.list();

			for (FrequenciaAluno freq : result) {
				Set<FrequenciaAluno> frequencias = mapa.get(freq.getDiscente().getNome());
				if (frequencias == null)
					frequencias = new TreeSet<FrequenciaAluno>(new Comparator<FrequenciaAluno>() {

						public int compare(FrequenciaAluno o1, FrequenciaAluno o2) {
							return o1.getData().compareTo(o2.getData());
						}

					});

				frequencias.add(freq);
				mapa.put(freq.getDiscente().getNome(), frequencias);
			}

		} catch (DAOException e) {
			throw new DAOException(e);
		}

		return mapa;
	}

	/**
	 * Retorna a lista de frequências por discente na turma.
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, Set<FrequenciaAluno>> findMatriculaFrequenciasByTurma(Turma turma) throws DAOException {

		Map<Integer, Set<FrequenciaAluno>> mapa = new TreeMap<Integer, Set<FrequenciaAluno>>();

		try {
			Criteria c = getCriteria(FrequenciaAluno.class);
			c.createCriteria("discente");
			c.add(Restrictions.eq("ativo", true));
			c.add(Expression.eq("turma", turma));
			List<FrequenciaAluno> result = c.list();

			for (FrequenciaAluno freq : result) {
				Set<FrequenciaAluno> frequencias = mapa.get(freq.getDiscente().getId());
				if (frequencias == null)
					frequencias = new TreeSet<FrequenciaAluno>(new Comparator<FrequenciaAluno>() {

						public int compare(FrequenciaAluno o1, FrequenciaAluno o2) {
							return o1.getData().compareTo(o2.getData());
						}

					});

				frequencias.add(freq);
				mapa.put(freq.getDiscente().getId(), frequencias);
			}

		} catch (DAOException e) {
			throw new DAOException(e);
		}

		return mapa;
	}

	/**
	 * Retorna a lista de frequências por discente em todas as sub-turmas
	 * da turma agrupadora passada como parâmetro.
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, Set<FrequenciaAluno>> findMatriculaFrequenciasBySubTurmas(Turma turma) throws DAOException {
		Map<Integer, Set<FrequenciaAluno>> mapa = new TreeMap<Integer, Set<FrequenciaAluno>>();

		try {
			Query q = getSession().createQuery("from FrequenciaAluno f where f.ativo = trueValue() and f.turma.id in (select t.id from Turma t where t.turmaAgrupadora.id = "+turma.getId()+")");
			List<FrequenciaAluno> result = q.list();

			for (FrequenciaAluno freq : result) {
				Set<FrequenciaAluno> frequencias = mapa.get(freq.getDiscente().getId());
				if (frequencias == null)
					frequencias = new TreeSet<FrequenciaAluno>(new Comparator<FrequenciaAluno>() {

						public int compare(FrequenciaAluno o1, FrequenciaAluno o2) {
							return o1.getData().compareTo(o2.getData());
						}

					});

				frequencias.add(freq);
				mapa.put(freq.getDiscente().getId(), frequencias);
			}

		} catch (DAOException e) {
			throw new DAOException(e);
		}

		return mapa;
	}

	
	/**
	 * Identifica se um professor lançou frequência para a turma na data especificada.
	 * @param data
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public boolean diaTemFrequencia(Date data, Turma turma) throws DAOException {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		int dia = c.get(Calendar.DAY_OF_MONTH);
		int mes = c.get(Calendar.MONTH);
		int ano = c.get(Calendar.YEAR);

		String mesStr = ((mes + 1) < 10 ? "0" + (mes+1): String.valueOf(mes+1) );
		SQLQuery q = getSession().createSQLQuery("select count(*) from ensino.frequencia_aluno f where f.ativo = trueValue() and id_turma = " + turma.getId() + " and data = '"+ano+"-"+mesStr+"-"+dia+"'");
		Number n = (Number) q.uniqueResult();
		return n.intValue() > 0;
	}

	/**
	 * Identifica se um professor lançou frequência para as subTurmas na data especificada.
	 * @param data
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public boolean diaTemFrequencia(Date data, List<Integer> idsTurma) throws DAOException {
		
		String ids = UFRNUtils.gerarStringIn(idsTurma);
		
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		int dia = c.get(Calendar.DAY_OF_MONTH);
		int mes = c.get(Calendar.MONTH);
		int ano = c.get(Calendar.YEAR);

		String mesStr = ((mes + 1) < 10 ? "0" + (mes+1): String.valueOf(mes+1) );
		SQLQuery q = getSession().createSQLQuery("select count(*) from ensino.frequencia_aluno f where f.ativo = trueValue() and id_turma in " + ids + " and data = '"+ano+"-"+mesStr+"-"+dia+"'");
		Number n = (Number) q.uniqueResult();
		return n.intValue() > 0;
	}
	
	/**
	 * Retorna a lista de frequência de um aluno em uma turma.
	 * @param discente
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<FrequenciaAluno> findFrequenciasByDiscente(Discente discente, Turma turma) throws DAOException {
		if (discente != null && turma != null)
			return getSession().createQuery("from FrequenciaAluno where ativo = trueValue() and discente.id = " + discente.getId() + " and turma.id = " + turma.getId() + " order by data asc").list();
		else
			return null;
	}

	/**
	 * Retorna lista de Discentes de uma Turma que tenha sua frequência registrada por Digital no sistema
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<Discente> findDiscentesComFrequenciaByTurmaDia(int idTurma, Date data) throws DAOException {
			Query q = getSession().createQuery("select discente from FrequenciaAluno freq where freq.ativo = trueValue() and freq.turma.id = ? and data = ?");
			q.setInteger(0, idTurma);
			q.setDate(1, data);
			
			return q.list();
	}
	
	/**
	 * Retorna o número de faltas de um aluno em uma turma.
	 * @param matricula
	 * @return
	 * @throws DAOException
	 */
	public Integer findFaltasAluno(MatriculaComponente matricula) throws DAOException {
		Number n = (Number) getSession().createSQLQuery("select sum(frequencia) from ensino.frequencia_aluno  where ativo = trueValue() and id_discente = "
				+ matricula.getDiscente().getId() + " and id_turma=" + matricula.getTurma().getId()).uniqueResult();
		if (n != null)
			return n.intValue();
		return 0;
	}

	/**
	 * Retorna uma lista das frequências de uma unidade para realizar o cálculo de faltas por unidade.
	 * @param dataInicial
	 * @param dataFinal
	 * @param idTurma
	 * @param quantidadeExec
	 * @return
	 * @throws DAOException
	 */
	public List<FrequenciaAluno> findTotalDeFaltasPorUnidade(Date dataInicial, Date dataFinal, int idTurma, int quantidadeExec) throws DAOException {

		ArrayList<FrequenciaAluno> listaFinal = new ArrayList<FrequenciaAluno>();

		if (quantidadeExec == 1) {

			String sql = localizarFaltasPorUnidadeVariando(dataInicial, dataFinal, idTurma, quantidadeExec);
			SQLQuery q = getSession().createSQLQuery(sql);
			List<Object[]> result = q.list();

			FrequenciaAluno frequenciaAluno = null;

			for ( Object[] row : result ) {

				frequenciaAluno = new FrequenciaAluno();
				frequenciaAluno.setDiscente(new Discente(0, ((Number)row[2]).longValue(), ((String) row[1]).toString()));
				frequenciaAluno.setFaltas( ((Number)row[0]).shortValue() );

				listaFinal.add(frequenciaAluno);
			}
		}

		if (quantidadeExec == 2) {

			FrequenciaAluno frequenciaAluno = null;
			
			String sql = localizarFaltasPorUnidadeVariando(dataInicial, dataFinal, idTurma, quantidadeExec);
			SQLQuery q = getSession().createSQLQuery(sql);
			List<Object[]> result = q.list();			
			
			for ( Object[] row : result ) {

				frequenciaAluno = new FrequenciaAluno();
				frequenciaAluno.setDiscente(new Discente(0, ((Number)row[2]).longValue(), ((String) row[1]).toString()));
				frequenciaAluno.setFaltas( ((Number)row[0]).shortValue() );

				listaFinal.add(frequenciaAluno);
			}
		}

		if (quantidadeExec == 3) {

			String sql = localizarFaltasPorUnidadeVariando(dataInicial, dataFinal, idTurma, quantidadeExec);
			SQLQuery q = getSession().createSQLQuery(sql);
			List<Object[]> result = q.list();

			FrequenciaAluno frequenciaAluno = null;

			for ( Object[] row : result ) {

				frequenciaAluno = new FrequenciaAluno();
				frequenciaAluno.setDiscente(new Discente(0, ((Number)row[2]).longValue(), ((String) row[1]).toString()));
				frequenciaAluno.setFaltas( ((Number)row[0]).shortValue() );

				listaFinal.add(frequenciaAluno);
			}
		}

		return listaFinal;
	}
	
	/**
	 * Retorna uma lista das frequências de uma unidade em todas as sub-turmas
	 * da turma passada como parâmetro para realizar o cálculo de faltas por unidade.
	 * @param dataInicial
	 * @param dataFinal
	 * @param idTurma
	 * @param quantidadeExec
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<FrequenciaAluno> findTotalDeFaltasPorUnidadeSubTurmas(Date dataInicial, Date dataFinal, int idTurma, int quantidadeExec) throws DAOException {
		ArrayList<FrequenciaAluno> listaFinal = new ArrayList<FrequenciaAluno>();

		if (quantidadeExec == 1) {

			String sql = localizarFaltasPorUnidadeVariandoSubTurmas(dataInicial, dataFinal, idTurma, quantidadeExec);
			SQLQuery q = getSession().createSQLQuery(sql);
			List<Object[]> result = q.list();

			FrequenciaAluno frequenciaAluno = null;

			for ( Object[] row : result ) {

				frequenciaAluno = new FrequenciaAluno();
				frequenciaAluno.setDiscente(new Discente(0, ((Number)row[2]).longValue(), ((String) row[1]).toString()));
				frequenciaAluno.setFaltas( ((Number)row[0]).shortValue() );

				listaFinal.add(frequenciaAluno);
			}
		}

		if (quantidadeExec == 2) {

			FrequenciaAluno frequenciaAluno = null;
			
			String sql = localizarFaltasPorUnidadeVariandoSubTurmas(dataInicial, dataFinal, idTurma, quantidadeExec);
			SQLQuery q = getSession().createSQLQuery(sql);
			List<Object[]> result = q.list();			
			
			for ( Object[] row : result ) {

				frequenciaAluno = new FrequenciaAluno();
				frequenciaAluno.setDiscente(new Discente(0, ((Number)row[2]).longValue(), ((String) row[1]).toString()));
				frequenciaAluno.setFaltas( ((Number)row[0]).shortValue() );

				listaFinal.add(frequenciaAluno);
			}
		}

		if (quantidadeExec == 3) {

			String sql = localizarFaltasPorUnidadeVariandoSubTurmas(dataInicial, dataFinal, idTurma, quantidadeExec);
			SQLQuery q = getSession().createSQLQuery(sql);
			List<Object[]> result = q.list();

			FrequenciaAluno frequenciaAluno = null;

			for ( Object[] row : result ) {

				frequenciaAluno = new FrequenciaAluno();
				frequenciaAluno.setDiscente(new Discente(0, ((Number)row[2]).longValue(), ((String) row[1]).toString()));
				frequenciaAluno.setFaltas( ((Number)row[0]).shortValue() );

				listaFinal.add(frequenciaAluno);
			}
		}

		return listaFinal;
	}

	/**
	 * Cria a consulta para retornar o número de faltas
	 * por unidade em uma turma.
	 * @param dataInicial
	 * @param dataFinal
	 * @param idTurma
	 * @param quantidadeExec
	 * @return
	 */
	private String localizarFaltasPorUnidadeVariando(Date dataInicial,
			Date dataFinal, int idTurma, int quantidadeExec) {

		SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
		String dataInicialStr = formate.format(dataInicial);
		String dataFinalStr = formate.format(dataFinal);

			if ( quantidadeExec == 1 ) {
				String sql = "select sum(f.frequencia), pessoa.nome, discente.matricula from ensino.frequencia_aluno f "
					+" inner join discente on discente.id_discente = f.id_discente "
					+" inner join comum.pessoa on discente.id_pessoa = pessoa.id_pessoa "
					+ " where f.ativo = trueValue() and f.id_turma = " + idTurma 
					+ " and f.data >= " + "'" + dataInicialStr + "'"
					+ " and f.data <= " + "'" + dataFinalStr + "'"
					+ " GROUP BY pessoa.nome, discente.id_discente, discente.matricula "
					+ " ORDER BY pessoa.nome ";
				return sql;				
			}
			else {
				String sql = "select sum(f.frequencia), pessoa.nome, discente.matricula from ensino.frequencia_aluno f "
					+" inner join discente on discente.id_discente = f.id_discente "
					+" inner join comum.pessoa on discente.id_pessoa = pessoa.id_pessoa "
					+ " where f.ativo = trueValue() and f.id_turma = " + idTurma
					+ " and f.data > " + "'" + dataInicialStr + "'"
					+ " and f.data <= " + "'" + dataFinalStr + "'"
					+ " GROUP BY pessoa.nome, discente.id_discente, discente.matricula "
					+ " ORDER BY pessoa.nome ";
				return sql;
			}
	}
	
	/**
	 * Cria a consulta para retornar o número de faltas
	 * por unidade em uma turma.
	 * @param dataInicial
	 * @param dataFinal
	 * @param idTurma
	 * @param quantidadeExec
	 * @return
	 */
	private String localizarFaltasPorUnidadeVariandoSubTurmas(Date dataInicial,
			Date dataFinal, int idTurma, int quantidadeExec) {

		SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
		String dataInicialStr = formate.format(dataInicial);
		String dataFinalStr = formate.format(dataFinal);

			if ( quantidadeExec == 1 ) {
				String sql = "select sum(f.frequencia), pessoa.nome, discente.matricula from ensino.frequencia_aluno f "
					+" inner join discente on discente.id_discente = f.id_discente "
					+" inner join comum.pessoa on discente.id_pessoa = pessoa.id_pessoa "					
					+ " where f.ativo = trueValue() and f.id_turma in (select id_turma from ensino.turma where id_turma_agrupadora = " + idTurma + ") "
					+ " and f.data >= " + "'" + dataInicialStr + "'"
					+ " and f.data <= " + "'" + dataFinalStr + "'"
					+ " GROUP BY pessoa.nome, discente.id_discente, discente.matricula "
					+ " ORDER BY pessoa.nome ";
				return sql;				
			}
			else {
				String sql = "select sum(f.frequencia), pessoa.nome, discente.matricula from ensino.frequencia_aluno f "
					+" inner join discente on discente.id_discente = f.id_discente "
					+" inner join comum.pessoa on discente.id_pessoa = pessoa.id_pessoa "					
					+ " where f.ativo = trueValue() and f.id_turma in (select id_turma from ensino.turma where id_turma_agrupadora = " + idTurma + ") "
					+ " and f.data > " + "'" + dataInicialStr + "'"
					+ " and f.data <= " + "'" + dataFinalStr + "'"
					+ " GROUP BY pessoa.nome, discente.id_discente, discente.matricula "
					+ " ORDER BY pessoa.nome ";
				return sql;
			}
	}

	/**
	 * Retorna um mapa contendo o número de faltas por aluno
	 * de uma turma.
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public HashMap<Integer, Integer> findFaltasAlunos(Turma turma , List<Date> datasAulas) throws DAOException {
		
		
		String idsTurmas = "(" + turma.getId() + ")";
		StringBuilder datasIn = null;
		
		if ( datasAulas != null && !datasAulas.isEmpty() ) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			datasIn = new StringBuilder();
			datasIn.append(" ( ");
			for ( int i = 0 ; i < datasAulas.size() ; i++ ) {
				datasIn.append("'"+sdf.format(datasAulas.get(i))+"'");
				if ( i < datasAulas.size()-1 )
					datasIn.append(",");
			}	
			datasIn.append(" ) ");		
		}
		
		if (turma.isAgrupadora())
			idsTurmas = UFRNUtils.gerarStringIn(getSession().createSQLQuery("select id_turma from ensino.turma where id_turma_agrupadora = "+turma.getId()).list());
		
		StringBuffer sql = new StringBuffer(); 
		
		sql.append("select  id_discente, sum(frequencia) " +
					"from ensino.frequencia_aluno  where ativo = trueValue() and id_turma in " + idsTurmas);
		if ( datasIn != null )
			sql.append(" and data in " + datasIn);		
		
		sql.append(" group by id_discente");
		
		SQLQuery q = getSession().createSQLQuery(sql.toString());

		List<Object[]> result = q.list();

		HashMap<Integer,Integer> resultMap = new HashMap<Integer, Integer>();

		for ( Object[] row : result ) {
			// Cast para Integer pode não funcionar
			if ( row[1] != null )
				resultMap.put( ((Number) row[0]).intValue() ,((Number) row[1]).intValue());
			else
				resultMap.put( ((Number) row[0]).intValue() ,null);
		}

		return resultMap;
	}

	/**
	 * Retorna uma lista de datas da turma nas quais o professor lançou frequência;
	 * @param turma
	 * @return
	 */
	public List<Date> getDatasComFrequencia(Turma turma) {
		return getJdbcTemplate().queryForList("select distinct data from ensino.frequencia_aluno where ativo = trueValue() and id_turma = ?", new Object[] { turma.getId() }, Date.class);
	}

	/**
	 * Retorna a estação de chamada biométrica pelo código da turma
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	 public EstacaoChamadaBiometrica findEstacaoBiometricaByTurma(String codigoTurma, String codigoComponente, int ano, int periodo) throws HibernateException, DAOException {
			StringBuilder sb = new StringBuilder();
			sb.append(" select estacaoTurma " +
				  " from EstacaoChamadaBiometricaTurma estacaoTurma " +
				  " inner join estacaoTurma.turma t " +
				  " inner join t.disciplina cc " +
				  " where t.codigo = :codigoTurma and cc.codigo = :codigoComponente " +
				  " and t.ano = :ano and t.periodo = :periodo");
			
			Query query = getSession().createQuery(sb.toString());
			query.setString("codigoTurma", codigoTurma);
			query.setString("codigoComponente", codigoComponente);
			query.setInteger("ano", ano);
			query.setInteger("periodo", periodo);
			
			EstacaoChamadaBiometricaTurma estacaoTurma = (EstacaoChamadaBiometricaTurma) query.uniqueResult();
			if (estacaoTurma != null)
				return estacaoTurma.getEstacaoChamadaBiometrica();
			else
				return new EstacaoChamadaBiometrica();
	 }
	
	/**
	 * Verifica se o discente informado no parâmetro já registrou presença para a determinada turma em determinado dia.
	 * Essa consulta é usada para evitar duplicação de chamadas feitas pelos discentes usando sua digital.  
	 * @param discente
	 * @param turma
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public boolean findFrequenciasByDiscenteDataTurma(Discente discente, Turma turma, Date date) throws HibernateException, DAOException {
			FrequenciaAluno freq = null;
			
			if (discente != null && turma != null)
				freq = (FrequenciaAluno) getSession().createQuery("from FrequenciaAluno where ativo = trueValue() and discente.id = ? and turma.id = ? and data = ?")
					.setInteger(0, discente.getId()).setInteger(1, turma.getId()).setDate(2, date).uniqueResult();
			
			if (freq != null)
				return true;
			else
				return false;
	}

	/**
	 * Retorna uma lista contendo os aluno que devem aparecer na planilha de frequência. 
	 * @param idTurma
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List <Object []> findMatriculasPlanilhaByTurma (int idTurma) throws DAOException{
		String auxSituacoes = "";
		for (SituacaoMatricula s : SituacaoMatricula.getSituacoesFrequencia())
			auxSituacoes = auxSituacoes.equals("") ? "" + s.getId() : auxSituacoes + "," + s.getId(); 

			String sql = "select m.id_matricula_componente, d.matricula, p.nome, " +
					"d.id_discente, case when m.id_situacao_matricula = "+SituacaoMatricula.TRANCADO.getId()+" then true else false end " +
					"from discente d " +
					"join ensino.matricula_componente m on m.id_discente = d.id_discente and m.id_situacao_matricula in ("+auxSituacoes+") " +
					"join comum.pessoa p using (id_pessoa) " +				
					"where m.id_turma = " + idTurma + " " +
					"order by p.nome";
				
		Query q = getSession().createSQLQuery(sql);
		
		List <Object []> rs = q.list();
		return rs;
	}
	
	/**
	 * Desativa as Frequências de um turma, caso for alterado o horário.
	 * @param turma
	 */
	public void desativarFrequencias(Turma turma, RegistroEntrada reg) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String data = sdf.format(new Date());
		
		String sql = "update ensino.frequencia_aluno set ativo = falseValue() , id_registro_atualizacao = " +reg.getId()+ " , data_atualizacao = '" +data+"' "+
				" where id_turma = "+turma.getId();
		getJdbcTemplate().update(sql);		
	}

	/**
	 *
	 * Retorna as frequencias associadas a turma na data informada
	 *
	 */
	public List<FrequenciaAluno> findAllFrequenciasByTurmaData(Turma turma, Date dataAula) throws HibernateException, DAOException{
		ArrayList<FrequenciaAluno> listaFinal = new ArrayList<FrequenciaAluno>();

		SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
		String dataF = formate.format(dataAula);
		
		String sql = "select f.id_frequencia, f.frequencia, discente.id_discente, discente.matricula from ensino.frequencia_aluno f "
				+" inner join discente on discente.id_discente = f.id_discente "					
				+ " where f.ativo = trueValue() and f.id_turma =" + turma.getId()
				+ " and f.data = " + "'" + dataF + "'"
				+ " GROUP BY f.id_frequencia, f.frequencia, discente.id_discente, discente.matricula";
		
		SQLQuery q = getSession().createSQLQuery(sql);
		
		List<Object[]> result = q.list();

		FrequenciaAluno frequenciaAluno = null;

		for ( Object[] row : result ) {
			
			frequenciaAluno = new FrequenciaAluno();
			frequenciaAluno.setDiscente(new Discente(((Integer)row[2]), ((Number)row[3]).longValue(),""));
			frequenciaAluno.setFaltas( ((Number)row[1]).shortValue() );
			frequenciaAluno.setId((Integer)row[0]);

			listaFinal.add(frequenciaAluno);
		}
		
		return listaFinal;
	}
}