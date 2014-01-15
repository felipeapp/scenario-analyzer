package br.ufrn.arq.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.rh.dominio.AtividadeServidor;
import br.ufrn.rh.dominio.Designacao;
import br.ufrn.rh.dominio.Servidor;

/**
 * DAO para efetuar busca por designacoes, usado para evitar o mapeamento da entidade Designacao e suas 
 * dependencias.
 * 
 * @author Raphael Medeiros
 *
 */
public class DesignacaoDAO extends GenericDAOImpl {

	RowMapper<Designacao> designacaoMapper = new RowMapper<Designacao>() {
		public Designacao mapRow(ResultSet rs, int row) throws SQLException {
			Designacao d = new Designacao();
			
			d.setId(rs.getInt("id_designacao"));
			d.setIdUnidade(rs.getInt("id_unidade"));
			
			d.setAtividade(new AtividadeServidor());
			d.getAtividade().setId(rs.getInt("id_atividade"));
			d.getAtividade().setDescricao(rs.getString("descricao_atividade"));
			
			d.setGerencia(rs.getString("gerencia"));
			d.setInicio(rs.getDate("inicio"));
			d.setFim(rs.getDate("fim"));
			
			d.setServidor(new Servidor());
			d.getServidor().setId(rs.getInt("id_servidor"));
			
			d.setUnidade(rs.getString("nome_unidade"));
			d.setUorg(rs.getString("uorg"));
			
			return d;
		}
	};
	
	/**
	 * Recupera todas as designacoes ATIVAS do servidor informado
	 * 
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public List<Designacao> getDesignacoesAtivas(int idServidor) throws DAOException {
		StringBuffer sql = new StringBuffer();
		
		sql.append("select d.id_designacao, d.id_unidade, d.id_atividade, a.descricao as descricao_atividade, d.gerencia, d.inicio, d.fim, d.id_servidor, d.nome_unidade, d.uorg " +
				   "  from rh.designacao d " +
				   "       join rh.atividade a using (id_atividade) " +
				   " where (d.fim is null or d.fim >= current_date) " +
				   "   and id_servidor = ? " +
				   " order by d.inicio desc ");
		
		return getJdbcTemplate().query(sql.toString(), new Object[] { idServidor }, designacaoMapper);
	}
	
	/**
	 * Recupera TODAS as designacoes do servidor informado
	 * 
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public List<Designacao> getDesignacoes(int idServidor) throws DAOException {
		StringBuffer sql = new StringBuffer();
		
		sql.append("select d.id_designacao, d.id_unidade, d.id_atividade, a.descricao as descricao_atividade, d.gerencia, d.inicio, d.fim, d.id_servidor, d.nome_unidade, d.uorg " +
				   "  from rh.designacao d " +
				   "       join rh.atividade a using (id_atividade) " +
				   " where id_servidor = ? " +
				   " order by d.inicio desc ");
		
		return getJdbcTemplate().query(sql.toString(), new Object[] { idServidor }, designacaoMapper);
	}

	/**
	 * Recupera as informações de uma determinada designação
	 * 
	 * @param id
	 * @return
	 */
	public Designacao findDesignacaoById(int id) {
		StringBuffer sql = new StringBuffer();
		
		sql.append("select d.id_designacao, d.id_unidade, d.id_atividade, a.descricao as descricao_atividade, d.gerencia, d.inicio, d.fim, d.id_servidor, d.nome_unidade, d.uorg " +
				   "  from rh.designacao d " +
				   "       join rh.atividade a using (id_atividade) " +
				   " where id_designacao = ? ");
		
		return (Designacao) getJdbcTemplate().queryForObject(sql.toString(), new Object[] { id }, designacaoMapper);
	}
}
