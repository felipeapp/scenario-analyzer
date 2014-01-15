package br.ufrn.sigaa.assistencia.remoto;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;

/**
 * Responsável por identificar cartão de acesso ao RU
 * @author geyson
 *
 */

public class IdentificacaoCartaoRu  {

	
	public long identificarCartaoRu(String cartaoRu) {
		
		JdbcTemplate jt = new JdbcTemplate(Database.getInstance().getSigaaDs());
		return (Long) jt.query("SELECT p.cpf_cnpj FROM sae.cartao_bolsa_alimentacao cardAli " +
				" inner join sae.cartao_beneficio_discente cardBen on(cardAli.id_cartao_bolsa_alimentacao = cardBen.id_cartao_bolsa_alimentacao) " +
				" inner join public.discente d on (cardBen.id_discente = d.id_discente) " +
				" inner join comum.pessoa p on (d.id_pessoa = p.id_pessoa) " +
				" where cardAli.bloqueado = false and cardBen.id_status_cartao_beneficio = 1 and cardAli.cod_barras = '"+ cartaoRu +"'", new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				try {
					long cpf = 0;
					while (rs.next()) {
						cpf = rs.getLong("cpf_cnpj");
						break;
					}
					return cpf;
				} catch(Exception e) {
					e.printStackTrace();
					return 0;
				}
			}
		});
	}

}
