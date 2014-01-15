package br.ufrn.sigaa.ensino.graduacao.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;


/**
 * Dao responsável por consultas específicas às Turmas de Graduação.
 * 
 * @author Henrique André
 *
 */
public class TurmaGraduacaoDao extends GenericSigaaDAO {
	
	/**
	 * Retorna a quantidade de turmas de acordo com os parâmetros passados.
	 * 
	 * @param disciplina
	 * @param ano
	 * @param periodo
	 * @param tipo
	 * @param situacaoes
	 * @return
	 * @throws DAOException
	 */
	public long countByDisciplinaAnoPeriodoTipoSituacao(ComponenteCurricular disciplina, CampusIes campus, int ano, int periodo, int tipo, Collection<SituacaoTurma> situacaoes) throws DAOException {
		
		String hql = "select count(t.id) from Turma t " +
				" where t.disciplina.id = :idDisciplina" +
				" and t.ano = :ano " +
				" and t.periodo = :periodo " +
				" and t.tipo = :tipo " +
				" and t.campus.id = :campus " +
				" and t.situacaoTurma in (:situacoes)" +
				" and t.agrupadora = :agrupadora";
		
		Query query = getSession().createQuery(hql);
		query.setInteger("idDisciplina", disciplina.getId());
		query.setInteger("ano", ano);
		query.setInteger("periodo", periodo);
		query.setInteger("campus", campus.getId());
		query.setInteger("tipo", tipo);
		query.setParameterList("situacoes", situacaoes);
		query.setBoolean("agrupadora", false);
		
		return (Long) query.uniqueResult();
		
	}
	
	/** Retorna uma coleção de turmas com a quantidade de discentes matriculados especificada.
	 * @param ano
	 * @param periodo
	 * @param qtdMatriculado
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Collection<Turma> findByQuantidadeMatriculado(int ano, int periodo, int qtdMatriculado, int tipoTurma) throws HibernateException, DAOException {
		// consulta turmas com o número menor ou igual de discentes
		String sql = "select id_turma, count(*) as qtd" +
				" from ensino.turma" +
				" inner join ensino.componente_curricular using (id_disciplina)" +
				" left join ensino.matricula_componente using (id_turma)" +
				" where turma.agrupadora = falseValue()" +
				" and turma.distancia = falseValue()" +
				" and turma.ano = :ano" +
				" and turma.periodo = :periodo" +
				" and turma.tipo = :tipoTurma" +
				" and nivel = :nivel" +
				" and matricula_componente.id_situacao_matricula in " +
				  UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesComVinculoTurma()) +
				" group by turma.id_turma" +
				" having count(id_discente) <= :qtdMatriculado";
		Query qIdTurmas = getSession().createSQLQuery(sql);
		qIdTurmas.setInteger("ano", ano);
		qIdTurmas.setInteger("periodo", periodo);
		qIdTurmas.setInteger("tipoTurma", tipoTurma);
		qIdTurmas.setCharacter("nivel", NivelEnsino.GRADUACAO);
		qIdTurmas.setInteger("qtdMatriculado", qtdMatriculado);
		@SuppressWarnings("unchecked")
		List<Object[]> ids = qIdTurmas.list();
		if (!isEmpty(ids)) {
			Map<Integer, Long> matriculados = new HashMap<Integer, Long>();
			for (Object[] obj : ids)
				matriculados.put((Integer) obj[0], ((BigInteger) obj[1]).longValue());
			Criteria c = getSession().createCriteria(Turma.class)
					.add(Restrictions.in("id", matriculados.keySet()))
					.createCriteria("disciplina")
					.createCriteria("unidade")
					.addOrder(Order.asc("nome"));
			@SuppressWarnings("unchecked")
			Collection<Turma> lista = c.list();
			if (lista != null) {
				for (Turma turma : lista) {
					turma.setQtdMatriculados(matriculados.get(turma.getId()));
				}
			}
			return lista;
		} else {
			return null;
		}
	}
	
	/**
	 * Retorna as turmas com as seguintes informações
	 *
	 * @param idComponente
	 * @param tipo
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findByComponenteTipoAnoPeriodo(int idComponente, Integer tipo, int ano, int periodo) throws DAOException {
		Criteria c = getSession().createCriteria(Turma.class);
		c.add( Restrictions.eq("disciplina.id", idComponente) );
		c.add( Restrictions.eq("tipo", tipo) );
		c.add( Restrictions.eq("ano", ano) );
		c.add( Restrictions.eq("periodo", periodo) );

		Collection<Integer> situacoes = new ArrayList<Integer>();
		situacoes.add( SituacaoTurma.ABERTA );
		situacoes.add( SituacaoTurma.A_DEFINIR_DOCENTE );
		c.add( Restrictions.in("situacaoTurma.id", situacoes) );

		@SuppressWarnings("unchecked")
		List<Turma> lista = c.list();
		return lista;
	}

}
