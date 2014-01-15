/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 15/07/2010
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino.stricto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 * DAO para consultas referentes a aproveitamento de cr�ditos de discentes Stricto Sensu.
 * 
 * @author Igor Linnik
 * 
 */
public class AproveitamentoCreditoDao extends GenericSigaaDAO {
	
	/**
	 * 
	 * Retorna o total de cr�ditos aproveitados compulsoriamente por um discente. Leva em considera��o
	 * apenas aproveitamentos ativos.
	 * 
	 * Nesses aproveitamentos o gestor informa apenas o total de cr�ditos a aproveitar e uma observa��o.
	 * 
	 * @param idDiscenteStricto
	 * @return
	 * @throws DAOException
	 */
	public Integer findTotalAproveitamentosByDiscente(int idDiscenteStricto)throws DAOException {
		
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = " select sum(creditos) as soma " +
						 " from stricto_sensu.aproveitamento_credito as aproveitamento " +
						 " where aproveitamento.ativo = trueValue() and aproveitamento.id_discente = ? ";

			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			st.setInt(1, idDiscenteStricto);
			rs = st.executeQuery();
			rs.next();
			return rs.getInt("soma");
			
		} catch(Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}
}