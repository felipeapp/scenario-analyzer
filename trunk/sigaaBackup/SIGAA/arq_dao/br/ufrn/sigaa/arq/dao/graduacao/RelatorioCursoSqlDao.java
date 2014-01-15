/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '20/04/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;

/**
 * Classe responsável pelas consultas de cursos
 * @author Eric Moura (Eriquim)
 *
 */
public class RelatorioCursoSqlDao extends AbstractRelatorioSqlDao {

	/**
	 * Método que retorna o Índice de trancamento e cancelamentos em disciplinas no curso
	 * @param unidade
	 * @param curso
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String,Object>> findListaCursoIndiceTrancamento(MatrizCurricular matrizCurricular, int ano, int periodo, Map<Integer, Boolean> filtros) throws DAOException{
		//sql de consulta
		StringBuilder sqlconsulta = new StringBuilder("select mc.ano, mc.periodo, u.sigla, c.nome as curso_nome,  " +
				"  sum(case when mc.id_situacao_matricula =5 then 1 else 0 end) as trancamentos,    " +
				"  sum(case when mc.id_situacao_matricula =3 then 1 else 0 end) as cancelamentos,     " +
				"  count(mc.id_matricula_componente) as total_matriculas,   " +
				"  ga.descricao as modalidade_nome, upper(t.descricao) as turno_descricao,    " +
				"  h.nome as habilitacao_nome, upper(m.nome) as municipio_nome, mcu.id_matriz_curricular, c.id_curso   " +
				" from comum.unidade u, curso c, discente d,graduacao.discente_graduacao dg,      " +
				"  ensino.matricula_componente mc, comum.municipio m, ensino.grau_academico ga, ensino.turno t,    " +
				"  graduacao.matriz_curricular mcu left outer join graduacao.habilitacao h using (id_habilitacao)    " +
				" where u.id_unidade = c.id_unidade     " +
				"  and   c.id_curso = d.id_curso     " +
				"  and   dg.id_discente_graduacao = d.id_discente     " +
				"  and   d.id_discente = mc.id_discente   " +
				"  and   dg.id_matriz_curricular = mcu.id_matriz_curricular    " +
				"  and   mcu.id_grau_academico = ga.id_grau_academico    " +
				"  and   mcu.id_turno = t.id_turno    " +
				"  and   c.id_municipio = m.id_municipio  ");

		if(filtros.get(MATRIZ_CURRICULAR))
			sqlconsulta.append("     and mcu.id_matriz_curricular = "+matrizCurricular.getId());//adicionar matriz no select
		else if(filtros.get(CURSO))
			sqlconsulta.append("     and c.id_curso = "+matrizCurricular.getCurso().getId());
		else if(filtros.get(UNIDADE))
			sqlconsulta.append("     and c.id_unidade = "+matrizCurricular.getCurso().getUnidade().getId());

		if(!filtros.get(ANO_PERIODO)){
			sqlconsulta.append("    and mc.ano= "+ano);
			sqlconsulta.append("    and mc.periodo= "+periodo);
		}

		sqlconsulta.append(" group by  mc.ano, mc.periodo, u.sigla, c.nome, ga.descricao, mcu.id_matriz_curricular, c.id_curso, t.descricao, h.nome, m.nome " +
				"            order by mc.ano, mc.periodo, u.sigla, c.nome, mcu.id_matriz_curricular ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);			
		}

		return result;
	}


	/**
	 * Método que retorna a lista de curso, turno, cidade, modalidade, habilitação, currículo, limite ideal
	 * @param unidade
	 * @param curso
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String,Object>> findListaCursoLimiteIdealSemestre(Unidade unidade, Curso curso) throws DAOException{
		//sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(" select u.sigla_academica, c.nome as curso_nome, t.sigla as turno_sigla, m.nome as cidade,  " +
				" ga.descricao as modalidade, h.nome as habilitacao_nome, cr.codigo as curriculo_codigo,  " +
				" cr.semestre_conclusao_ideal  " +
				" from comum.unidade u, curso c, ensino.turno t, ensino.grau_academico ga,  " +
				" graduacao.curriculo cr, graduacao.habilitacao h, graduacao.matriz_curricular mc,  " +
				" comum.municipio m  " +
				" where u.id_unidade = c.id_unidade  " +
				" and   c.id_curso = cr.id_curso  " +
				" and   c.id_curso = mc.id_curso  " +
				" and   m.id_municipio = c.id_municipio " +
				" and   mc.id_turno = t.id_turno  " +
				" and   mc.id_habilitacao = h.id_habilitacao  " +
				" and   mc.id_grau_academico = ga.id_grau_academico  " );

		if(curso !=null && curso.getId()!=0)
			sqlconsulta.append("     and c.id_curso = "+curso.getId());

		if(unidade !=null && unidade.getId()!=0)
			sqlconsulta.append(" and u.id_unidade = "+unidade.getId());

		sqlconsulta.append(" order by u.sigla_academica, c.nome , t.sigla, m.nome,ga.descricao, h.nome, " +
				" cr.codigo, cr.semestre_conclusao_ideal  ");

		List<Map<String,Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);			
		}

		return result;
	}


}
