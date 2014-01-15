/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 26/08/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.vestibular.dominio.TipoConvocacao;

/**
 * Classe responsável por consultas em SQL para geração de relatórios que
 * envolvam discentes de Graduação.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public class RelatorioDiscenteGraduacaoSqlDao extends GenericSigaaDAO {

	/** Retorna os dados necessários para a geração Relatório Quantitativo de Alunos que Entraram por Segunda Opção no Vestibular:
	 * id_matriz_curricular, curso, grau_academico, turno, total_vagas, aprovados_opcao_1, aprovados_opcao_2.
	 * @param processoSeletivo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> qtdIngressantesSegundaOpcao(Integer processoSeletivo) throws DAOException {
		int [] statusDiscente = new int[] {StatusDiscente.ATIVO, StatusDiscente.CADASTRADO}; 
		String consultaSql = " SELECT dg.id_matriz_curricular," +
				" m.nome as municipio," +
				" c.nome as curso," +
				" ga.descricao as grau_academico," +
				" t.sigla as turno," +
				" ovc.total_vagas,"
			+ " sum(CASE WHEN dg.id_matriz_curricular = op1.id_matriz_curricular THEN 1 ELSE 0 END) AS aprovados_opcao_1,"
			+ " sum(CASE WHEN dg.id_matriz_curricular != op1.id_matriz_curricular THEN 1 ELSE 0 END) AS aprovados_opcao_2"
			+ " FROM graduacao.discente_graduacao  dg"
			+ " INNER JOIN discente d on (id_discente = id_discente_graduacao)"
			+ " INNER JOIN vestibular.convocacao_processo_seletivo_discente cpsd USING (id_discente)"
			+ " INNER JOIN vestibular.inscricao_vestibular iv USING (id_inscricao_vestibular)"
			+ " INNER JOIN vestibular.processo_seletivo ps USING (id_processo_seletivo)"
			+ " LEFT JOIN vestibular.opcao_candidato op1 ON (op1.id_inscricao_vestibular = iv.id_inscricao_vestibular AND op1.ordem = 0)"
			+ " LEFT JOIN vestibular.opcao_candidato op2 ON (op2.id_inscricao_vestibular = iv.id_inscricao_vestibular AND op2.ordem = 1)"
			+ " INNER JOIN graduacao.matriz_curricular mc ON (dg.id_matriz_curricular = mc.id_matriz_curricular)"
			+ " INNER JOIN ensino.oferta_vagas_curso ovc ON (mc.id_matriz_curricular = ovc.id_matriz_curricular)"
			+ " LEFT JOIN ensino.grau_academico ga USING (id_grau_academico)"
			+ " INNER JOIN curso c ON mc.id_curso = c.id_curso"
			+ " INNER JOIN comum.municipio m USING (id_municipio)"
			+ " INNER JOIN ensino.turno t ON mc.id_turno = t.id_turno"
			+ " WHERE ps.id_processo_seletivo = " + processoSeletivo
			+ " AND d.ano_ingresso = ps.ano_entrada"
			+ " AND ovc.ano = ps.ano_entrada"
			+ " AND d.status IN " + UFRNUtils.gerarStringIn(statusDiscente)
			+ " AND ovc.id_forma_ingresso = ps.id_forma_ingresso"
			+ " AND cpsd.id_convocacao_processo_seletivo_discente NOT IN ("
			+ "    SELECT id_convocacao_processo_seletivo_discente"
			+ "      FROM graduacao.cancelamento_convocacao"
			+ "    UNION "
			+ "    SELECT id_convocacao_processo_seletivo_discente"
			+ "      FROM vestibular.convocacao_processo_seletivo_discente"
			+ "     WHERE tipo = "+TipoConvocacao.CONVOCACAO_MUDANCA_SEMESTRE.ordinal()+")"
			+ " GROUP BY dg.id_matriz_curricular, m.nome, c.nome, ga.descricao, t.sigla, ovc.total_vagas"
			+ " ORDER BY municipio, curso, grau_academico, turno";
	
		List<Map<String, Object>> result;
	
		try {
			SQLQuery q = getSession().createSQLQuery(consultaSql);
			q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
			result = q.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return result;
	}

}
