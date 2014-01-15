package br.ufrn.comum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.comum.dominio.ResponsavelUnidadeAvaliacao;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.rh.dominio.ProcessoAvaliacao;
import br.ufrn.rh.dominio.Servidor;

/**
 * DAO comum com consultas referentes tabela responsavel_unidade_avaliacao
 *
 * @author Adriana Alves
 *
 */
public class ResponsavelUnidadeAvaliacaoDAO extends GenericSharedDBDao{
	public ResponsavelUnidadeAvaliacaoDAO(){}
	
	public ResponsavelUnidadeAvaliacaoDAO(int sistema){
		setSistema(sistema);
	}
	
	RowMapper responsavelUniAvaliacaiMapper = new RowMapper() {
		public Object mapRow(ResultSet rs, int row) throws SQLException {
			ResponsavelUnidadeAvaliacao resp = new ResponsavelUnidadeAvaliacao();
			resp.setId(rs.getInt("id_resp_unid_avaliacao"));
			resp.setServidorAvaliador(new Servidor());
			resp.getServidorAvaliador().setPessoa(new PessoaGeral());
			resp.getServidorAvaliador().getPessoa().setNome(rs.getString("nome_servidor"));
			resp.getServidorAvaliador().setSiape(rs.getInt("matricula"));
			resp.setProcessoGDH(new ProcessoAvaliacao());
			resp.getProcessoGDH().setDescricao(rs.getString("descricao"));
			return resp;
		}
	};
	
	/**
	 * Retorna os responsáveis por uma determinada unidade.
	 *
	 * @param idUnidade
	 * @param nivelResponsabilidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Responsavel> getResponsaveisAvaliacaoByUnidade(int idUnidade) throws DAOException {

		String sql =
			"SELECT DISTINCT RU.id_resp_unid_avaliacao,U.ID_UNIDADE AS ID_UNIDADE,S.SIAPE AS MATRICULA,P.NOME AS NOME_SERVIDOR,PA.DESCRICAO AS DESCRICAO " +
			"FROM COMUM.RESPONSAVEL_UNIDADE_AVALIACAO RU,AVALIACAO.PROCESSO_AVALIACAO PA, comum.unidade U,RH.SERVIDOR S, COMUM.PESSOA P " + 
			"WHERE S.ID_SERVIDOR = RU.ID_RESPONSAVEL_AVALIACAO AND S.ID_PESSOA = P.ID_PESSOA " +
			"AND RU.ID_PROCESSO_AVALIACAO = PA.ID_PROCESSO_AVALIACAO "+
			"AND RU.ID_UNIDADE = ? AND U.ID_UNIDADE = RU.ID_UNIDADE AND DATA_INATIVACAO IS NULL ORDER BY P.NOME " ;
						
		Object[] lista = {idUnidade};
		
		try{
			return getJdbcTemplate(Sistema.getSistemaAdministrativoAtivo()).query(sql, lista, responsavelUniAvaliacaiMapper);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	/**
	 * Retorna o Processo de Avaliação mais atual
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Integer findProcessoAvaliacaoMaisAtual() throws DAOException{

		String sql ="SELECT ID_PROCESSO_AVALIACAO FROM AVALIACAO.PROCESSO_AVALIACAO ORDER BY DATA_CADASTRO DESC " + BDUtils.limit(1);
			
		try{
			return getJdbcTemplate(Sistema.getSistemaAdministrativoAtivo()).queryForInt(sql);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}

	}
	
	/**
	 * Verifica se o servidor já é responsável avaliativo pela unidade
	 *  
	 * @param idServidor, idUnidade
	 * @param 
	 * @return
	 * @throws DAOException
	 */
	public Boolean isAutorizadorResponsavelAvaliativo(int idUnidade,int idServidor) throws DAOException {
		Object[] lista;
		try{
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT count(r.id_resp_unid_avaliacao) FROM comum.responsavel_unidade_avaliacao r inner join comum.unidade u "+
				"ON u.id_unidade = r.id_unidade inner join rh.servidor s on s.id_servidor= r.id_responsavel_avaliacao WHERE r.id_responsavel_avaliacao = ? ");
			if(idUnidade > 0){
				sql.append(" AND r.id_unidade = ?  ");	
			}
			sql.append("and r.id_usuario_inativacao is null");
			
			if (idUnidade > 0 && idServidor > 0){
				lista = new Object[]{idServidor,idUnidade};
			} else
				lista = new Object[]{idServidor};
			try{
				return (getJdbcTemplate(Sistema.getSistemaAdministrativoAtivo()).queryForInt(sql.toString(), lista) > 0)?true:false;
			}catch (EmptyResultDataAccessException e) {
				return false;
			}

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

}
