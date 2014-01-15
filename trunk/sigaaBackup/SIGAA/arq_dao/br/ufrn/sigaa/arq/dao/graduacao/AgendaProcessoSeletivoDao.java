/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '09/01/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.AgendaProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.EditalProcessoSeletivo;

/**
 * Classe DAO responsável por todas as consultas relacionado 
 * as datas de agendamento do processo seletivo de transferência voluntária.
 * 
 * @author Mário Rizzi Rocha
 */
public class AgendaProcessoSeletivoDao extends GenericSigaaDAO {

	/**
	 * Retorna todas as agendas disponíveis onde sua quantidade 
	 * seja menor que o campo quantidade máxima de inscritos por data agendada
	 * @return
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<AgendaProcessoSeletivo> findAllDisponiveisByEdital(Integer id) throws DAOException {
		
		
		return getJdbcTemplate().query("SELECT DISTINCT a.id_agenda_processo_seletivo," +
				" a.data_agendada,a.id_edital_processo_seletivo,a.qtd " +
				" FROM ensino.agenda_processo_seletivo a WHERE a.qtd > " +
				" ( SELECT count(*) FROM ensino.inscricao_selecao i WHERE " +
				"	i.id_agenda_processo_seletivo = a.id_agenda_processo_seletivo "+
				" ) AND a.id_edital_processo_seletivo = " + id + " ORDER BY a.data_agendada ASC",
				new RowMapper() {
			List<Date> feriados = findAllFeriados();
			public Object mapRow(ResultSet rs, int row) throws SQLException {
			
				AgendaProcessoSeletivo a = new AgendaProcessoSeletivo();
				 a.setId(rs.getInt("id_agenda_processo_seletivo"));
				 a.setDataAgenda(rs.getDate("data_agendada"));
				 a.setEditalProcessoSeletivo(new EditalProcessoSeletivo());
				 a.getEditalProcessoSeletivo().setId(rs.getInt("id_edital_processo_seletivo"));
				 a.setQtd(rs.getInt("qtd"));
				 if(!feriados.contains(a.getDataAgenda()))
					 return a;
				 else 
					 return null;
			}
		});
		
	}
	
	/**
	 * Consulta realizado na base de dados do SIPAC.
	 * Retorna uma Coleção de datas de feriados
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Date> findAllFeriados() throws DAOException {

		String sql = "	select data from comum.feriados "; 
		
		return getJdbcTemplate(Sistema.SIPAC).queryForList(sql, Date.class);

	}	
	
	
}
