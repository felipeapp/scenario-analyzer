package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;

/**
 * Dao responsável pela(s) consulta(s) que é(são) inerente(s) aos bolsistas que renovaram a cota.
 * 
 * @author Jean Guerethes
 */
public class RelatorioBolsaPibicDao extends AbstractRelatorioSqlDao {


	/**
	 * Método responsável pela realização da consulta dos bolsistas, que permaneceram após o termino da
	 * sua primeira cota.
	 * 
	 * @param cotaBolsaAtual
	 * @param cotaBolsaAnterior
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findBolsaPibic(Integer cotaBolsaAtual, Integer cotaBolsaAnterior) throws DAOException {
		
		String sqlconsulta = ("select tbp.descricao, (case tbp.categoria when 1 then 'IC' when 2 then 'IT' end) as categoria, " +
				" count(distinct id_discente) from pesquisa.membro_projeto_discente mpd" +
				" join pesquisa.plano_trabalho pt using(id_plano_trabalho)" +
				" join pesquisa.tipo_bolsa_pesquisa tbp on (mpd.tipo_bolsa = tbp.id_tipo_bolsa)" +
				" join (select id_discente, mpd2.tipo_bolsa from pesquisa.membro_projeto_discente mpd2" +
				" join pesquisa.plano_trabalho pt2 using(id_plano_trabalho)" +
				" where id_cota_bolsas = "+cotaBolsaAnterior+") as planos_anteriores using (id_discente)" +
				" where id_cota_bolsas = "+cotaBolsaAtual+" and mpd.tipo_bolsa = planos_anteriores.tipo_bolsa" +
				" and mpd.ignorar is false group by tbp.descricao, tbp.categoria;");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}
	
}
