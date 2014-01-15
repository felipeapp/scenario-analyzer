/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 28/04/2010
 *
 */
package br.ufrn.sigaa.arq.dao.extensao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;

/**
 * DAO responsável pelas consultas referentes ao total de ações de extensão que concorreram a editais públicos.
 *  
 * @author Geyson
 */
public class RelatorioTotalAcaoExtensaoEditalDao extends AbstractRelatorioSqlDao {
	
	/**
	 * Relatório de ações de extensão que concorreram a editais públicos.
	 * @param areTematica
	 * @param situacaoAcao
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> relatorioAcaoEdital(Integer areaTematica, Integer situacaoAcao, Date inicio, Date fim) throws DAOException {
		
		StringBuilder sqlconsulta = 
			new StringBuilder(" select upper(p.titulo) as projeto, upper(p.descricao_financiador_externo) as edital, ta.descricao as tipo_atividade, " +
					"at.descricao as area_tematica, upper(cf.descricao) as classificacao_financiadora, tsp.descricao as situacao, " +
					"date(p.data_inicio) as inicio, date(p.data_fim) as fim from extensao.atividade a, extensao.tipo_atividade_extensao ta, " +
					"projetos.projeto p, projetos.tipo_situacao_projeto tsp, projetos.classificacao_financiadora cf, extensao.area_tematica at " +
					"where a.id_tipo_atividade_extensao = ta.id_tipo_atividade_extensao " +
					"and a.id_projeto = p.id_projeto " +
					"and p.id_tipo_situacao_projeto = tsp.id_tipo_situacao_projeto " +
					"and p.financiamento_externo = true " +
					"and p.id_classificacao_financiadora = cf.id_classificacao_financiadora " +
					"and a.id_area_tematica_principal = at.id_area_tematica ");
		
		if(areaTematica > 0){
			sqlconsulta.append(" and at.id_area_tematica = " + areaTematica +"");
		}
		if(situacaoAcao > 0){
			sqlconsulta.append(" and tsp.id_tipo_situacao_projeto = "  + situacaoAcao + "");
		}
		if ((inicio != null) && (fim != null)) {
			sqlconsulta.append(" and (date(p.data_inicio) >= '" + inicio + "' AND date(p.data_fim) <= '" + fim + "' ) " );
		}
		
		sqlconsulta.append(" order by tipo_atividade, area_tematica, classificacao_financiadora, situacao, projeto ");
				
		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}

}
