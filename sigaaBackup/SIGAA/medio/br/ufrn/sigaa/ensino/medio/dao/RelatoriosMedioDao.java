/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 05/10/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dao;

import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;

/**
 * Classe responsável por consultas específicas para a geração de relatórios de nível médio.
 * 
 * @author Rafael Gomes
 *
 */
public class RelatoriosMedioDao extends AbstractRelatorioSqlDao{

	
	/**
	 * Gera a lista de alunos matriculados numa escola do ensino médio num dado ano.
	 *
	 * @param ano
	 * @param periodo
	 * @param gestora
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaAlunosMatriculados(int ano, int gestora) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder(
				" select c.id_curso, c.nome as nome_curso, s.id_serie, s.numero as numero_serie, s.descricao as descricao_serie, " +
				" ts.id_turma_serie, ts.ano, ts.nome as nome_turma, d.matricula, p.nome as nome_discente, mds.dependencia" +
				" FROM comum.pessoa p" +
				" INNER JOIN discente d ON p.id_pessoa = d.id_pessoa" + 
				" INNER JOIN medio.discente_medio dm ON dm.id_discente = d.id_discente" +
				" INNER JOIN medio.matricula_discente_serie mds ON mds.id_discente = dm.id_discente" +
				" INNER JOIN medio.turma_serie ts ON ts.id_turma_serie = mds.id_turma_serie" +
				" INNER JOIN medio.serie s ON s.id_serie = ts.id_serie" +
				" INNER JOIN medio.curso_medio cm ON cm.id_curso = s.id_curso" +
				" INNER JOIN curso c ON c.id_curso = cm.id_curso" +
				" where d.nivel = '" + NivelEnsino.MEDIO + "'"+
				" and s.id_gestora_academica = " + gestora +
				" and mds.id_situacao_matricula_serie = " + SituacaoMatriculaSerie.MATRICULADO.getId() +
				" and ts.ano = " + ano +
				" group by c.id_curso, nome_curso, s.id_serie, numero_serie, descricao_serie, ts.id_turma_serie," +
				" ts.ano, nome_turma, d.matricula, nome_discente, mds.dependencia" +
				" order by ano, numero_serie, nome_turma, nome_discente");

		List<Map<String,Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return result;
	}
	
	/**
	 * Gera o quantitativo de alunos por ano de ingresso
	 *
	 * @param gestora
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findQuantitativoAlunosByAnoIngresso(int gestora) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder("" +
				" SELECT c.id_curso, c.nome as nome_curso, d.ano_ingresso, count(*) as total " +
				" FROM discente d " +
				" INNER JOIN comum.pessoa p ON p.id_pessoa = d.id_pessoa" +
				" INNER JOIN medio.discente_medio dm ON dm.id_discente = d.id_discente " +
				" INNER JOIN curso c on c.id_curso = d.id_curso " +
				" where d.id_gestora_academica = " + gestora +
				" group by c.id_curso, nome_curso, d.ano_ingresso " +
				" order by c.id_curso, nome_curso, d.ano_ingresso");

		List<Map<String,Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return result;
	}
	
}
