/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '15/05/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 * Classe Abstrata que auxilia na consulta de relatórios sql
 *
 * @author Eric Moura
 *
 */
public class AbstractRelatorioSqlDao extends GenericSigaaDAO {


	 /**
	 * Constante para os tipos de filtros
	 */
	 /** Constante para o filtro CURSO utilizado nos relatórios. */ 
	 public static final int CURSO = 1;
	 /** Constante para o filtro MATRIZ CURRICULAR utilizado nos relatórios. */ 
	 public static final int MATRIZ_CURRICULAR = 2;
	 /** Constante para o filtro ANO e PERIODO utilizado nos relatórios. */ 
	 public static final int ANO_PERIODO = 3;
	 /** Constante para o filtro ATIVO utilizado nos relatórios. */ 
	 public static final int ATIVO = 4;
	 /** Constante para o filtro UNIDADE utilizado nos relatórios. */ 
	 public static final int UNIDADE = 5;
	 /** Constante para o filtro INGRESSO utilizado nos relatórios. */ 
	 public static final int INGRESSO = 6;
	 /** Constante para o filtro EGRESSO utilizado nos relatórios. */ 
	 public static final int EGRESSO = 7;
	 /** Constante para o filtro ANO de SAIDA utilizado nos relatórios. */ 
	 public static final int ANO_SAIDA = 8;
	 /** Constante para o filtro MATRICULADO utilizado nos relatórios. */ 
	 public static final int MATRICULADO = 9;
	 /** Constante para o filtro CENTRO utilizado nos relatórios. */ 
	 public static final int CENTRO = 10;
	 /** Constante para o filtro DEPARTAMENTO utilizado nos relatórios. */ 
	 public static final int DEPARTAMENTO = 11;
	 /** Constante para o filtro SITUACAO da TURMA utilizado nos relatórios. */ 
	 public static final int SITUACAO_TURMA = 12;
	 /** Constante para o filtro RESERVA de CURSO utilizado nos relatórios. */ 
	 public static final int RESERVA_CURSO = 13;
	 /** Constante para o filtro MOTIVO para TRANCAMENTO utilizado nos relatórios. */ 
	 public static final int MOTIVO_TRANCAMENTO = 14;
	 /** Constante para o filtro CODIGO utilizado nos relatórios. */ 
	 public static final int CODIGO = 15;
	 /** Constante para o filtro AFASTAMENTO PERMANENTE utilizado nos relatórios. */ 
	 public static final int AFASTAMENTO_PERMANENTE = 16;
	 /** Constante para o filtro PRE REQUISITOS utilizado nos relatórios. */ 
	 public static final int PRE_REQUISITOS = 17;
	 /** Constante para o filtro ANO e PERIODO utilizado nos relatórios. */ 

	/**
	 * Método que realiza a consulta sql para um relatório, e retorna uma Lista
	 * das linhas da consulta
	 *
	 * @param consultaSql
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> executeSql(String consultaSql) throws DAOException {
		SQLQuery q = getSession().createSQLQuery(consultaSql);
		q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
		return q.list();
	}

	/** Retorna uma coleção de anos registrados para as turmas. */
	@SuppressWarnings("unchecked")
	public Collection<Integer> getAnos() throws DAOException {

		Query q = getSession().createQuery(
				"select distinct t.ano from Turma t order by t.ano desc");
		return q.list();

	}

	/** Retornar uma coleção de anos de conclusão utilizados pelos discente. */
	@SuppressWarnings("unchecked")
	public Collection<String> getAnosConclusao() throws DAOException {

		Query q = getSession().createQuery(
				"select distinct substring(d.prazoConclusao,1,4) from Discente d order by substring(d.prazoConclusao,1,4)desc");
		return q.list();

	}

	/** Retornar a coleção de períodos distintos registrados para os alunos. */
	@SuppressWarnings("unchecked")
	public Collection<Integer> getPeriodos() throws DAOException {

		Query q = getSession()
				.createQuery(
						"select distinct t.periodo from Turma t order by t.periodo asc");
		return q.list();

	}

	/** Retornar a coleção de períodos de conclusão distintos registrados para os alunos. */
	@SuppressWarnings("unchecked")
	public Collection<String> getPeriodosConclusao() throws DAOException {

		Query q = getSession()
				.createQuery(
				"select distinct substring(d.prazoConclusao,5,1)  from Discente d  order by substring(d.prazoConclusao,5,1) desc");
		return q.list();

	}

}
