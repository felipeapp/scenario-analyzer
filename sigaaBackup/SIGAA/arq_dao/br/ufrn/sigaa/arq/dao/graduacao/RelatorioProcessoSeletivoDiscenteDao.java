/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 19/01/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.arq.dao.graduacao;

import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivo;

/**
 * DAO que contém as consultas para os relatórios referentes a Processo Seletivo para discentes de graduação.
 * 
 * @author Rafael Gomes
 *
 */
public class RelatorioProcessoSeletivoDiscenteDao extends AbstractRelatorioSqlDao{

	/**
	 * Retorna um Mapa de discentes para compor o relatório de discentes,
	 * que não compareceram ao cadastramento e encontram-se com status EXCLUÍDO.
	 * 
	 * @param convocacao
	 * @param processoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findalunosConvocadosExcluidos(ConvocacaoProcessoSeletivo convocacao, Integer processoSeletivo) throws DAOException{
		
		StringBuilder sql = new StringBuilder(" " +
			" SELECT iv.numero_inscricao as numeroInscricao, p.nome, p.numero_identidade as identidade, d.matricula," +
			" c.nome||' ('||SUBSTR(grau.descricao,1,1)||') ('||turno.sigla||')'  as curso," +
			" hab.nome as habilitacao, grau.descricao as grauAcademico, turno.sigla as turno, " +
			" municipio.nome as cidade, cps.descricao as descricaoConvocacao, mpo.nome as municipio_polo" +
			" FROM vestibular.convocacao_processo_seletivo_discente cpsd" +
			" INNER JOIN vestibular.convocacao_processo_seletivo cps using(id_convocacao_processo_seletivo)" +
			" INNER JOIN discente d using(id_discente)" +
			" INNER JOIN comum.pessoa p using(id_pessoa)" +
			" INNER JOIN curso c using(id_curso)" +
			" INNER JOIN vestibular.inscricao_vestibular iv using(id_inscricao_vestibular)" +
			" INNER JOIN comum.municipio municipio ON municipio.id_municipio = c.id_municipio" +
			" INNER JOIN graduacao.discente_graduacao dg ON dg.id_discente_graduacao = d.id_discente" +
			" LEFT JOIN ead.polo po on (dg.id_polo = po.id_polo)" +
			" LEFT JOIN comum.municipio mpo on (mpo.id_municipio = po.id_cidade)" +
			" INNER JOIN graduacao.matriz_curricular matriz on(matriz.id_matriz_curricular = dg.id_matriz_curricular)" +
			" INNER JOIN ensino.turno turno ON turno.id_turno = matriz.id_turno" +
			" INNER JOIN ensino.grau_academico grau ON grau.id_grau_academico = matriz.id_grau_academico" +
			" LEFT  JOIN graduacao.habilitacao hab ON hab.id_habilitacao = matriz.id_habilitacao" +
			" WHERE d.status = " + StatusDiscente.EXCLUIDO );
		
		if ( convocacao.getId() > 0 )
			sql.append( " AND cps.id_convocacao_processo_seletivo = " + convocacao.getId() );
		else 
			sql.append( " AND cps.id_processo_seletivo = " + processoSeletivo );
		
		sql.append(	" ORDER BY c.nome, grau.descricao, turno.sigla, p.nome" );
		
		List<Map<String, Object>> result;

		try {
			result = executeSql(sql.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}
}
