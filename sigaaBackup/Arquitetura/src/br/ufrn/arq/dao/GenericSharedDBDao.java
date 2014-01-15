/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 17/01/2007
 */
package br.ufrn.arq.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;

/**
 * Usado para acessar o SessionFactory da base compartilhada
 *
 * @author Gleydson Lima
 *
 */
public class GenericSharedDBDao extends GenericDAOImpl {

	public GenericSharedDBDao() {
		super(Sistema.COMUM);
	}
	
	public GenericSharedDBDao(int sistema) {
		super(sistema);
	}
	
	/**
	 * Recupera o próximo valor da sequence
	 *
	 * @param sequence
	 * @return
	 * @throws SQLException
	 */
	public int getNextSeq(String seq) throws DAOException {
		Connection con = null;
		try {
			con = Database.getInstance().getComumConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select nextval('" + seq + "')");
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			Database.getInstance().close(con);
		}
	}

}
