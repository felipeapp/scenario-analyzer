package br.ufrn.sigaa.assistencia.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;


/**
 * Dao responsável por consultas relativas 
 * a acessos ao restaurante universitário - RU.
 * 
 * @author geyson
 */
public class RelatorioAcessoRUDao extends AbstractRelatorioSqlDao {

	/**
	 * retorna os acessos que foram feitos no RU através da catraca. 
	 *
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String, Object>> relatorioAcessoRUCatraca(Date dataInicial, Date dataFinal) throws DAOException {
		StringBuilder sql = new StringBuilder();
		
			sql.append(" SELECT " +
					" SUM(CASE WHEN turno = 'C' and id_tipo_bolsa = 1 THEN 1 ELSE 0 END) as cafe_residente_graduacao, " +
					" SUM(CASE WHEN turno = 'C' and id_tipo_bolsa = 2 THEN 1 ELSE 0 END) as cafe_residente_pos, " +
					" SUM(CASE WHEN turno = 'C' and id_tipo_bolsa = 3 THEN 1 ELSE 0 END) as cafe_alimentacao, " +
					" SUM(CASE WHEN turno = 'A' and id_tipo_bolsa = 1 THEN 1 ELSE 0 END) as almoco_residente_graduacao, " +
					" SUM(CASE WHEN turno = 'A' and id_tipo_bolsa = 2 THEN 1 ELSE 0 END) as almoco_residente_pos, " +
					" SUM(CASE WHEN turno = 'A' and id_tipo_bolsa = 3 THEN 1 ELSE 0 END) as almoco_alimentacao, " +
					" SUM(CASE WHEN turno = 'J' and id_tipo_bolsa = 1 THEN 1 ELSE 0 END) as janta_residente_graduacao, " +
					" SUM(CASE WHEN turno = 'J' and id_tipo_bolsa = 2 THEN 1 ELSE 0 END) as janta_residente_pos, " +
					" SUM(CASE WHEN turno = 'J' and id_tipo_bolsa = 3 THEN 1 ELSE 0 END) as janta_alimentacao, " +
					" data_acesso_ru " +
					" from sae.registro_acesso_ru where data_acesso_ru between " + "'" + CalendarUtils.format(dataInicial, "yyyy-MM-dd") + "'" + " and " + "'" + CalendarUtils.format(dataFinal, "yyyy-MM-dd") + "' " +
					" group by data_acesso_ru ");
			List<Map<String, Object>> result;

			try {
				result = executeSql(sql.toString());
			} catch (Exception e) {
				e.printStackTrace();
				throw new DAOException(e);
			}
			return result;
	}
	
	
	/**
	 * retorna os acessos detalhado que foram feitos no RU através da catraca. 
	 *
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String, Object>> relatorioAcessoRUCatracaDetalhes(Date dataInicial, Date dataFinal) throws DAOException {
		StringBuilder sql = new StringBuilder();

		sql.append("select dis.matricula, p.nome as nome_discente, c.nome as nome_curso, tb.denominacao, ru.data_hora, ru.turno, ru.data_acesso_ru " +
				" from " + 
				" sae.registro_acesso_ru ru " + 
				" inner join discente dis on (dis.matricula = ru.matricula_discente) " +
				" inner join comum.pessoa p on (p.id_pessoa = dis.id_pessoa) " +
				" inner join curso c on (dis.id_curso = c.id_curso ) " +
				" inner join sae.tipo_bolsa_auxilio tb on (tb.id_tipo_bolsa_auxilio = ru.id_tipo_bolsa) " +
				" where " +
				" ru.data_acesso_ru between " + "'" + CalendarUtils.format(dataInicial, "yyyy-MM-dd") + "'" + " and " + "'" + CalendarUtils.format(dataFinal, "yyyy-MM-dd") + "' " +
		"order by ru.data_hora ");

		List<Map<String, Object>> result;
		try {
			result = executeSql(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}
}
