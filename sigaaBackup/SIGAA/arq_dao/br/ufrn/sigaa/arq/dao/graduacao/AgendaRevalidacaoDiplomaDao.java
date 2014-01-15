/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '09/01/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.AgendaRevalidacaoDiploma;
import br.ufrn.sigaa.ensino.graduacao.dominio.EditalRevalidacaoDiploma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoRevalidacaoDiploma;

/**
 * Classe DAO respons�vel por todas as consultas relacionada 
 * a agenda de revalida��o de diplomas
 * 
 * @author M�rio Rizzi Rocha
 */
public class AgendaRevalidacaoDiplomaDao extends GenericSigaaDAO {

	/**
	 * Retorna a pr�xima agenda dispon�vel onde sua quantidade de solicita��es 
	 * seja menor que o campo qtd
	 * @return
	 */
	public AgendaRevalidacaoDiploma findProximoDisponivel() {
		
		Integer id = getJdbcTemplate().queryForInt(" SELECT a.id_agenda_revalidacao_diploma FROM graduacao.agenda_revalidacao_diploma a  WHERE a.qtd > " +
				" (select count(*) FROM graduacao.solicitacao_revalidacao_diploma s WHERE id_agenda_revalidacao_diploma = a.id_agenda_revalidacao_diploma) " +
				" ORDER BY data ASC " +
				BDUtils.limit(1));
		
		if (id == null) {
			return null;
		} else {
			return new AgendaRevalidacaoDiploma(id);
		}
		
	}
	
	/**
	 * Retorna todas as agendas dispon�veis onde sua quantidade de solicita��es 
	 * seja menor que o campo qtd
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<AgendaRevalidacaoDiploma> findAllDatasDisponiveis(SolicitacaoRevalidacaoDiploma solicitacao) {
		
		return getJdbcTemplate().query(" SELECT DISTINCT a.data FROM graduacao.agenda_revalidacao_diploma a  WHERE a.qtd > " +
				" (SELECT COUNT(*) FROM graduacao.solicitacao_revalidacao_diploma s WHERE id_agenda_revalidacao_diploma = a.id_agenda_revalidacao_diploma" +
				(solicitacao.getId()>0?(" AND s.id_solicitacao_revalidacao_diploma <> " + solicitacao.getId() + " "):"") + 
				" AND id_edital_revalidacao_diploma = a.id_edital_revalidacao_diploma) AND a.id_edital_revalidacao_diploma = " + 
				solicitacao.getEditalRevalidacaoDiploma().getId() +	" ORDER BY data ASC", new RowMapper() {
			public Object mapRow(ResultSet rs, int row) throws SQLException {
				AgendaRevalidacaoDiploma a = new AgendaRevalidacaoDiploma();
					a.setData(rs.getDate("data"));
				return a;
			}
		});
		
	}
	
	/**
	 * Retorna verdadeiro se data estiver dispon�vel
	 * seja menor que o campo qtd
	 * @return
	 */
	public boolean findDataDisponivel(AgendaRevalidacaoDiploma agenda, Integer idSolicitacao) {
		
		Integer qtd = getJdbcTemplate().queryForInt(" SELECT count(*) FROM graduacao.agenda_revalidacao_diploma a  WHERE a.qtd > " +
				" (SELECT COUNT(*) FROM graduacao.solicitacao_revalidacao_diploma s WHERE id_agenda_revalidacao_diploma = " +
				" a.id_agenda_revalidacao_diploma AND id_edital_revalidacao_diploma = a.id_edital_revalidacao_diploma " +
				" AND a.id_agenda_revalidacao_diploma = " + agenda.getId() + 
				(!isEmpty(idSolicitacao)?(" AND s.id_solicitacao_revalidacao_diploma <> " + idSolicitacao + " "):"") + 
				") AND a.id_edital_revalidacao_diploma = " + 
				agenda.getEditalRevalidacaoDiploma().getId() + " AND a.id_agenda_revalidacao_diploma = " + agenda.getId());
			
		 if(!ValidatorUtil.isEmpty(qtd))
			 return true;

		 return false;
		
	}
	
	/**
	 * Retorna todos dias e hor�rios associdados a um edital do processo de revalida��o de diploma.
	 * @param data
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AgendaRevalidacaoDiploma> findHorariosByData(Date data, String horario, Integer id) throws DAOException{

		String sql = " SELECT a.horario, a.id_agenda_revalidacao_diploma FROM graduacao.agenda_revalidacao_diploma a  WHERE a.qtd > " +
		"( SELECT COUNT(*) from graduacao.solicitacao_revalidacao_diploma s WHERE id_agenda_revalidacao_diploma = a.id_agenda_revalidacao_diploma"+
		(!isEmpty(id)?(" AND s.id_solicitacao_revalidacao_diploma <> " + id + " "):"")+")";

		
		if(!isEmpty(data))
			sql += " AND a.data= '" + data + "'";
		if(!isEmpty(horario))
			sql += " AND a.horario = '" + horario + "'";
		
		
		sql += " ORDER BY a.horario";
		
		return getJdbcTemplate().query(sql, new RowMapper() {
			public Object mapRow(ResultSet rs, int row) throws SQLException {
				AgendaRevalidacaoDiploma a = new AgendaRevalidacaoDiploma();
				a.setHorario(rs.getString("horario"));
				a.setId(rs.getInt("id_agenda_revalidacao_diploma"));
				return a;
			}
		});
		
	}
	
	/**
	 * Retorna todos os hor�rios associados a data enviada pelo par�metro 
	 * @param data
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AgendaRevalidacaoDiploma> findAllHorariosByData(Date data) throws DAOException{

		String sql = " SELECT DISTINCT ON (a.horario) a.horario, a.id_agenda_revalidacao_diploma FROM graduacao.agenda_revalidacao_diploma a  WHERE " +
		"  a.data= '" + data + "' ORDER BY a.horario ";
		
		return getJdbcTemplate().query(sql, new RowMapper() {
			public Object mapRow(ResultSet rs, int row) throws SQLException {
				AgendaRevalidacaoDiploma a = new AgendaRevalidacaoDiploma();
				a.setHorario(rs.getString("horario"));
				a.setId(rs.getInt("id_agenda_revalidacao_diploma"));
				return a;
			}
		});
		
	}
	
	/**
	 * Retorna todas as datas dispon�veis associadas a um processo de revalida��o de diploma.
	 * @param data
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AgendaRevalidacaoDiploma> findAllDatas(EditalRevalidacaoDiploma edital) throws DAOException{

		StringBuilder sql = new StringBuilder(" SELECT DISTINCT a.data FROM graduacao.agenda_revalidacao_diploma as a ");
		
		if(!isEmpty(edital) && edital.getId()>0)
			sql.append(" WHERE a.id_edital_revalidacao_diploma = " + edital.getId());
		sql.append(" ORDER BY a.data ");
		
		return getJdbcTemplate().query(sql.toString(), new RowMapper() {
			public Object mapRow(ResultSet rs, int row) throws SQLException {
				AgendaRevalidacaoDiploma a = new AgendaRevalidacaoDiploma();
				a.setData(rs.getDate("data"));
				return a;
			}
		});
		
	}
	
	
}
