/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '07/10/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;

/**
 * Dao utilizado para efetuar as consultas que geram os relatórios de vagas ofertadas.
 * @author geyson
 *
 */
public class RelatorioVagasOfertadasDao extends AbstractRelatorioSqlDao {
	
	
	/**
	 * Retorna uma lista de vagas ofertadas por ano e forma de ingresso.
	 * @param ano
	 * @param formaIngresso
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> relatorioVagasOfertadas(Integer ano, Integer formaIngresso) throws DAOException {

		StringBuilder sqlconsulta = new StringBuilder(" select ovc.ano as ano, c.nome as nome, " +
			"u.sigla as unidade_sigla, tu.sigla as turno_sigla , fi.descricao as forma_descricao, " +
			"vagas_periodo_1 as vagas1, vagas_periodo_2 as vagas2, vagas_ociosas_periodo_1 as vagas_ociosas1, " +
			"vagas_ociosas_periodo_2 as vagas_ociosas2 from ensino.oferta_vagas_curso ovc " +
			"join curso c on ovc.id_curso = c.id_curso " +
			"join comum.unidade u on c.id_unidade = u.id_unidade " +
			"join ensino.forma_ingresso fi on ovc.id_forma_ingresso = fi.id_forma_ingresso " +
			"left join graduacao.matriz_curricular mc on ovc.id_matriz_curricular = mc.id_matriz_curricular " +
			"left join ensino.turno tu on mc.id_turno = tu.id_turno " +
			"where c.nivel = 'G'  ");
		if(ano > 0)
			sqlconsulta.append(" and ovc.ano = "+ ano +"");
		if(formaIngresso > 0 )
			sqlconsulta.append(" and fi.id_forma_ingresso = "+formaIngresso+" ");
		
		sqlconsulta.append(" order by ovc.ano, c.nome ");
			
		
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
