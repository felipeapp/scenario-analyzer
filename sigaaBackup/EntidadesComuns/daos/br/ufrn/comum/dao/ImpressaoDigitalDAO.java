/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 20/01/2012
 */
package br.ufrn.comum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.integracao.dto.ImpressaoDigitalDTO;

/**
 * DAO contendo métodos para buscas de impressões digitais das pessoas no
 * banco comum.
 * 
 * @author David Pereira
 *
 */
public class ImpressaoDigitalDAO extends GenericSharedDBDao {

	/**
	 * Busca o conjunto de impressões digitais cadastradas para as pessoas cujos CPFs
	 * foram passados como parâmetro. Retorna um mapa onde a chave é o CPF da pessoa e o valor
	 * é a lista de impressões digitais cadastradas para essa pessoa.
	 * 
	 * @param cpfs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Long, List<ImpressaoDigitalDTO>> buscarImpressoesDigitais(Long... cpfs) {
		
		StringBuilder sql = new StringBuilder("select cpf, digital, dedo_coletado from comum.identificacao_pessoa where cpf in (");
		int total = cpfs.length;
		for (int i = 0; i < total; i++) {
			sql.append(cpfs[i]);
			if (i < (total - 1)) {
				sql.append(",");
			}
		}
		sql.append(" )");
		
		return (Map<Long, List<ImpressaoDigitalDTO>>) getJdbcTemplate().query(sql.toString(), new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<Long, List<ImpressaoDigitalDTO>> result = new HashMap<Long, List<ImpressaoDigitalDTO>>();
				
				while(rs.next()) {
					long cpf = rs.getLong("cpf");
					byte[] digital = rs.getBytes("digital");
					char dedo = rs.getString("dedo_coletado").charAt(0);
					
					if (result.get(cpf) == null) {
						result.put(cpf, new ArrayList<ImpressaoDigitalDTO>());
					} 

					ImpressaoDigitalDTO dto = new ImpressaoDigitalDTO();
					dto.setCpf(cpf);
					dto.setDigital(digital);
					dto.setDedoColetado(dedo);
					
					result.get(cpf).add(dto);
				}
				
				return result;
			}
		});
	}
	
}
