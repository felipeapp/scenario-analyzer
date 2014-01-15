/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 19/08/2011
 *
 */
package br.ufrn.sigaa.ensino.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.CursoGrauAcademicoEnade;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Classe responsável por consultas específicas relacionadas ao ENADE.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public class ParticipacaoEnadeDao extends GenericSigaaDAO {

	/** Retorna uma coleção de discentes de graduação para definir a situação para o ENADE Ingressante.
	 * 
	 * @param cursos Restringir a busca por discentes dos cursos desta coleção
	 * @param outrosCursos Busca por outros cursos que não são os listados na coleção de cursos.
	 * @param anoIngresso
	 * @param periodoIngresso
	 * @param idStatusDiscente
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<DiscenteGraduacao> findDiscentesParaEnadeIngressante(Collection<CursoGrauAcademicoEnade> cursos, boolean outrosCursos, int anoIngresso, int periodoIngresso, int idStatusDiscente) throws HibernateException, DAOException {
		Integer[] status = {StatusDiscente.CONCLUIDO,StatusDiscente.CANCELADO, StatusDiscente.EXCLUIDO, StatusDiscente.PENDENTE_CADASTRO, StatusDiscente.PRE_CADASTRADO};
		String projecao = "dg.id," +
				" discente.matricula as dg.discente.matricula," +
				" participacaoEnadeIngressante as dg.participacaoEnadeIngressante," +
				" dg.dataProvaEnadeIngressante," +
				" curso.id as dg.discente.curso.id," +
				" curso.nome as dg.discente.curso.nome," +
				" municipio.id as curso.municipio.id," +
				" municipio.nome as curso.municipio.nome," +
				" dg.matrizCurricular.id," +
				" dg.matrizCurricular.grauAcademico.id," +
				" dg.matrizCurricular.grauAcademico.descricao," +
				" dg.matrizCurricular.turno.id," +
				" dg.matrizCurricular.turno.sigla," +
				" curriculo.id as dg.discente.curriculo.id," +
				" discente.status as dg.discente.status," +
				" pessoa.id as dg.discente.pessoa.id," +
				" pessoa.nome as dg.discente.pessoa.nome";
		StringBuilder hql = new StringBuilder("select ");
		hql.append(HibernateUtils.removeAliasFromProjecao(projecao))
			.append(" from DiscenteGraduacao dg" +
					" inner join dg.discente discente" +
					" inner join discente.curso curso" +
					" inner join curso.municipio municipio" +
					" inner join discente.curriculo curriculo" +
					" inner join discente.pessoa pessoa" +
					" left join dg.participacaoEnadeIngressante participacaoEnadeIngressante" +
				" where discente.anoIngresso = :anoIngresso");
		if (periodoIngresso > 0)
				hql.append(" and discente.periodoIngresso = :periodoIngresso");
		hql.append(" and discente.status not in " + UFRNUtils.gerarStringIn(status));
		if (!isEmpty(cursos)) {
			Collection<Integer> idMatrizes = findIdMatriz(cursos);
			if (outrosCursos)
				hql.append(" and dg.matrizCurricular.id not in ").append(UFRNUtils.gerarStringIn(idMatrizes));
			else
				hql.append(" and dg.matrizCurricular.id in ").append(UFRNUtils.gerarStringIn(idMatrizes));
		}
		if (idStatusDiscente > 0)
			hql.append(" and discente.status = :idStatusDiscente");
		hql.append(" order by curso.nomeAscii, dg.matrizCurricular.grauAcademico.descricao, dg.matrizCurricular.turno.sigla, municipio.nome, pessoa.nomeAscii");
		Query q = getSession().createQuery(hql.toString());
		if (idStatusDiscente > 0)
			q.setInteger("idStatusDiscente", idStatusDiscente);
		q.setInteger("anoIngresso", anoIngresso);
		if (periodoIngresso > 0)
			q.setInteger("periodoIngresso", periodoIngresso);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return HibernateUtils.parseTo(lista, projecao, DiscenteGraduacao.class, "dg");
	}

	/**
	 * Consulta as matrizes curriculares dos cursos/graus acadêmicos
	 * @param cursos
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private Collection<Integer> findIdMatriz(
			Collection<CursoGrauAcademicoEnade> cursos) throws HibernateException, DAOException {
		StringBuilder sqlMatriz = new StringBuilder("select id_matriz_curricular" +
				" from graduacao.matriz_curricular" +
				" where (id_curso, id_grau_academico)" +
				" in (");
		for (CursoGrauAcademicoEnade curso : cursos) {
			sqlMatriz.append("(").append(curso.getCurso().getId())
					.append(",").append(curso.getGrauAcademico().getId())
					.append(")").append(", ");
		}
		sqlMatriz.delete(sqlMatriz.lastIndexOf(","), sqlMatriz.lastIndexOf(",") + 2);
		sqlMatriz.append(")");
		Query qMatriz = getSession().createSQLQuery(sqlMatriz.toString());
		@SuppressWarnings("unchecked")
		Collection<Integer> idMatrizes = qMatriz.list();
		return idMatrizes;
	}

	/** Retorna uma coleção de discentes de graduação para definir a situação para o ENADE Concluinte.
	 * 
	 * @param cursos Restringir a busca por discentes dos cursos desta coleção
	 * @param outrosCursos Busca por outros cursos que não são os listados na coleção de cursos.
	 * @param percentualConcluido
	 * @param idStatusDiscente
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<DiscenteGraduacao> findDiscentesParaEnadeConcluinte(Collection<CursoGrauAcademicoEnade> cursos, boolean outrosCursos, int percentualConcluido, int idStatusDiscente) throws HibernateException, DAOException {
		String projecao = "dg.id," +
				" discente.matricula as dg.discente.matricula," +
				" participacaoEnadeConcluinte as dg.participacaoEnadeConcluinte," +
				" dg.dataProvaEnadeConcluinte," +
				" curso.id as dg.discente.curso.id," +
				" curso.nome as dg.discente.curso.nome," +
				" municipio.id as curso.municipio.id," +
				" municipio.nome as curso.municipio.nome," +
				" dg.matrizCurricular.id," +
				" dg.matrizCurricular.grauAcademico.id," +
				" dg.matrizCurricular.grauAcademico.descricao," +
				" dg.matrizCurricular.turno.id," +
				" dg.matrizCurricular.turno.sigla," +
				" curriculo.id as dg.discente.curriculo.id," +
				" discente.status as dg.discente.status," +
				" pessoa.id as dg.discente.pessoa.id," +
				" pessoa.nome as dg.discente.pessoa.nome," +
				" discente.anoIngresso as dg.discente.anoIngresso";
		StringBuilder hql = new StringBuilder("select ");
		hql.append(HibernateUtils.removeAliasFromProjecao(projecao))
			.append(" from DiscenteGraduacao dg" +
				" inner join dg.discente discente" +
				" inner join discente.curso curso" +
				" inner join curso.municipio municipio" +
				" inner join discente.curriculo curriculo" +
				" inner join discente.pessoa pessoa" +
				" left join dg.participacaoEnadeConcluinte participacaoEnadeConcluinte" +
				" where 100 * ( 1 - cast(dg.chTotalPendente as double) / curriculo.chTotalMinima) >= :percentualConcluido");
		if (!isEmpty(cursos)) {
			Collection<Integer> idMatrizes = findIdMatriz(cursos);
			if (outrosCursos)
				hql.append(" and dg.matrizCurricular.id not in ").append(UFRNUtils.gerarStringIn(idMatrizes));
			else
				hql.append(" and dg.matrizCurricular.id in ").append(UFRNUtils.gerarStringIn(idMatrizes));
		}
		if (idStatusDiscente > 0) {
			hql.append(" and discente.status = :idStatusDiscente");
		} else {
			Integer[] status = {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO};
			hql.append(" and discente.status in ").append(UFRNUtils.gerarStringIn(status));
		}
		hql.append(" order by curso.nomeAscii, dg.matrizCurricular.grauAcademico.descricao, dg.matrizCurricular.turno.sigla, municipio.nome, pessoa.nomeAscii");
		Query q = getSession().createQuery(hql.toString());
		if (idStatusDiscente > 0)
			q.setInteger("idStatusDiscente", idStatusDiscente);
		q.setInteger("percentualConcluido", percentualConcluido);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return HibernateUtils.parseTo(lista, projecao, DiscenteGraduacao.class, "dg");
	}

}
