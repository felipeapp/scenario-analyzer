/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 22/11/2006
 *
 */
package br.ufrn.sigaa.arq.dao.ensino.latosensu;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.TurmaEntradaLato;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe responsável por realizar consultas específicas de discentes do lato sensu.
 *
 * @author Leonardo
 *
 */
public class DiscenteLatoDao extends DiscenteDao {

	/** Construtor padrão. */
	public DiscenteLatoDao(){
	}

	/** Retorna uma coleção de discentes de uma {@link TurmaEntradaLato turma de entrada}.
	 * @param idTurmaEntrada ID da turma de entrada (obrigatório).
	 * @param nivelEnsino nível de ensino do discente.
	 * @param possuiVinculoAtivo caso true, retorna apenas os discentes com status ATIVO OU CADASTRADO.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteAdapter> findByTurmaEntrada(int idTurmaEntrada, boolean possuiVinculoAtivo) throws DAOException {
		try {
			String projecao = "id, discente.id, discente.pessoa.nome, discente.matricula";

			StringBuffer hql = new StringBuffer();
			hql.append("SELECT " + projecao + " ");
			hql.append("from DiscenteLato" +
					" WHERE discente.nivel = 'L'" +
					" and turmaEntrada.id = " + idTurmaEntrada);
			if (possuiVinculoAtivo)
				hql.append(" and discente.status in "+ UFRNUtils.gerarStringIn(new int[] { StatusDiscente.ATIVO, StatusDiscente.CADASTRADO }) );
			hql.append(" order by discente.pessoa.nome asc");

			Query q = getSession().createQuery(hql.toString());
			Collection<DiscenteAdapter> lista = HibernateUtils.parseTo(q.list(), projecao, DiscenteLato.class);
			return lista;
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Consulta otimizada que retorna todos os alunos de um curso de especialização. 
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findAllAtivosByCurso(CursoLato curso) throws DAOException {
		try {
			int status[] = {StatusDiscente.CANCELADO, StatusDiscente.CONCLUIDO, StatusDiscente.JUBILADO, StatusDiscente.TRANCADO, StatusDiscente.EXCLUIDO};
			String projecao = "d.id, d.discente.matricula, d.discente.pessoa.nome, d.discente.pessoa.id, d.discente.curso.nome," +
					" d.discente.anoIngresso, d.discente.status, d.discente.curso.modalidadeEducacao.id";
			StringBuffer hql = new StringBuffer();
			hql.append(" select " + projecao +
					   " from DiscenteLato d where d.discente.status not in " + UFRNUtils.gerarStringIn(status) +
					   " and d.turmaEntrada.cursoLato.id = " + curso.getId() + " order by d.discente.pessoa.nome");
			
			@SuppressWarnings("unchecked")
			List<Object[]> res = getSession().createQuery(hql.toString()).list();
			Collection<Discente> result = new ArrayList<Discente>(0);
			if(res != null){
				result = HibernateUtils.parseTo(res, projecao, Discente.class, "d");
			}
			return result;
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}
	
	
	/**
	 * Realiza uma busca e retorna uma lista de discentes de um curso que
	 * participaram de uma Conclusão de Grau Coletiva, isto é, possuem a mesma
	 * data de movimentação de saída.
	 * 
	 * @param idCurso
	 * @param ano Ano da colação coletiva.
	 * @param semestre 1, caso a colação tenha sido entre janeiro e junho (inclusive). 2, caso tenha sido entre julho e dezembro (inclusive).
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findTurmasConclusaoByCursoAnoPeriodoConclusao(int idCurso, int ano, int periodo) throws DAOException {
		StringBuilder sql = new StringBuilder(
				"select curso.id_curso,"
				+ " curso.nome as curso,"
				+ " municipio.nome as municipio,"
				+ " unidade.nome as unidade,"
				+ " unidade.sigla as sigla,"
				+ " coalesce(data_colacao_grau, date_trunc('day', data_ocorrencia)) as data_colacao_grau,"
				+ " count(*) as quantidade"
				+ " from discente"
				+ " inner join curso using (id_curso) "
				+ " inner join ensino.movimentacao_aluno using (id_discente) "
				+ " left join comum.municipio using (id_municipio) "
				+ " left join comum.unidade using (id_unidade)"
				+ " where discente.nivel = :nivel"
				+ " and movimentacao_aluno.id_tipo_movimentacao_aluno = :tipoMovimentacao"
				+ " and movimentacao_aluno.ativo = trueValue()");
		if (idCurso > 0) {
			sql.append(" and curso.id_curso = :idCurso");
		}
		if (ano != 0 && periodo != 0) {
			sql.append(" and ano_referencia = :ano");
			sql.append(" and periodo_referencia = :periodo");
		}
		sql.append(" group by curso.id_curso, curso.nome, municipio.nome, unidade.nome, unidade.sigla, data_colacao_grau, date_trunc('day', data_ocorrencia)");
		sql.append("  having count(*) > 1");
		sql.append(" order by curso.nome, curso.id_curso, data_colacao_grau desc");
		SQLQuery q = getSession().createSQLQuery(sql.toString());
		q.setInteger("tipoMovimentacao", TipoMovimentacaoAluno.CONCLUSAO);
		q.setCharacter("nivel", NivelEnsino.LATO);
		if (ano != 0 && periodo != 0) {
			q.setInteger("ano", ano);
			q.setInteger("periodo", periodo);
		}
		if (idCurso > 0) {
			q.setInteger("idCurso", idCurso);
		}
		q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP);
		return q.list();
	}
	
	/**
	 * Realiza uma busca e retorna uma lista de discentes de um curso que  possuem a mesma
	 * data de movimentação de conclusão de curso.
	 * 
	 * @param idCurso
	 * @param dataColacao
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<Discente> findConcluidosByCursoDataColacao(int idCurso, Date dataConclusao) throws DAOException {
		String hql = "select discente from MovimentacaoAluno movimentacao" +
				" inner join movimentacao.discente discente" +
				" inner join discente.curso curso" +
				" inner join fetch discente.pessoa pessoa" +
				" where curso.id = :idCurso" +
				" and (movimentacao.dataOcorrencia = :dataConclusao or discente.dataColacaoGrau = :dataConclusao)" +
				" and movimentacao.tipoMovimentacaoAluno.id = " + TipoMovimentacaoAluno.CONCLUSAO +
				" and discente.nivel = :nivelEnsino" +
				" order by pessoa.nome";
		Query q = getSession().createQuery(hql);
		q.setInteger("idCurso", idCurso);
		q.setDate("dataConclusao", dataConclusao);
		q.setCharacter("nivelEnsino", NivelEnsino.LATO);
		return q.list();
	}
	
	/**
	 * Busca os discentes que não possuem os id's especificados.
	 * 
	 * @param ids
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByIds( Collection<Integer> ids ) throws DAOException{
		try {
				String projecao = "d.pessoa.nome, d.curso.nome, d.matricula";
				String hql = "SELECT " + projecao +
						" FROM Discente d " +
						" WHERE d.id not in " + gerarStringIn(ids) +
						" AND d.nivel = '" + NivelEnsino.LATO + "'" +
						" AND d.status = " + StatusDiscente.ATIVO +
						" ORDER BY d.curso.nome, d.pessoa.nome";
		
				@SuppressWarnings("unchecked")
				List<Object[]> res = getSession().createQuery(hql).list();
				Collection<Discente> result = new ArrayList<Discente>(0);
				if(res != null){
					result = HibernateUtils.parseTo(res, projecao, Discente.class, "d");
				}
			return result;
		} catch (Exception e) {
		 throw new DAOException(e.getMessage(), e);
		}
	}

	public boolean haDiscenteTurmaEntrada(int idTurmaEntrada) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("SELECT count(dl)" +
					" FROM DiscenteLato dl" +
					" WHERE dl.discente.nivel = 'L'" +
					" and dl.turmaEntrada.id = " + idTurmaEntrada);

			return (Long) getSession().createQuery(hql.toString()).uniqueResult() > 0;
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}

}