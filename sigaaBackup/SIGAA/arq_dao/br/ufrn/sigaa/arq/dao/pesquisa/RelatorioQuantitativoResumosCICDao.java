/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '21/10/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;

/**
 * Dao responsável pelas consultas do Relatório referente a Quantitativo de Resumos CIC(Congresso de Iniciação Científica).
 *
 * @author geyson
 *
 */
public class RelatorioQuantitativoResumosCICDao extends AbstractRelatorioSqlDao {

	/**
	 * Relatório Quantitativo de Resumos CID de acordo com Congresso de Iniciação Científica informado.
	 * @param congressoIniciacaoCientifica
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> relatorioRemusosCIC(Integer congressoIniciacaoCientifica) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder(" select distinct u.sigla as unidade, c.nome as curso, u.nome as centro, " +
				"count(a.id_resumo_congresso) as quantidade_resumos, m.nome as municipio " +
				"from " +
				"pesquisa.congresso_iniciacao_cientifica cic " +
				"inner join pesquisa.resumo_congresso r on (cic.id = r.id_congresso) " +
				"inner join pesquisa.autor_resumo_congresso a on (a.id_resumo_congresso = r.id_resumo_congresso) " +
				"inner join discente d on (a.id_discente=d.id_discente) " +
				"inner join curso c on (d.id_curso=c.id_curso) " +
				"inner join comum.municipio m on (c.id_municipio = m.id_municipio) " +
				"inner join comum.unidade u on (c.id_unidade=u.id_unidade) " +
				"where a.tipo_participacao=1 ");
		if(congressoIniciacaoCientifica > 0)
			sqlconsulta.append(" and cic.id = " + congressoIniciacaoCientifica + " ");
				
		sqlconsulta.append("group by u.sigla, c.nome, u.nome, m.nome ");
		
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
