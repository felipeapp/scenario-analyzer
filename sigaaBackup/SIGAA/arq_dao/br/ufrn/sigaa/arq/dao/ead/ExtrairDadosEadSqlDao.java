/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/06/2009
 *
 */
package br.ufrn.sigaa.arq.dao.ead;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.ead.dominio.TabelaDadosEadSql;


/**
 * Dao que retorna os dados formatados para serem exportados em SQL para a EAD.
 * 
 * @author Fred_Castro
 *
 */
public class ExtrairDadosEadSqlDao extends GenericDAOImpl {

	public String extraiDadoTabelaEad(int idTabela, boolean metropoleDigital) throws ArqException{
	
		TabelaDadosEadSql tabela = new TabelaDadosEadSql (idTabela, metropoleDigital);
		
		Connection con = null;

		try {
			try {
				con = Database.getInstance().getSigaaConnection();
	
				ResultSet rs = con.createStatement().executeQuery(tabela.getSql());
				String resultado = "\n\n\n----------------------------------\n-- " + tabela.getNome() + "\n----------------------------------\n\n" + UFRNUtils.resultSetToSQLInserts(tabela.getNome(), rs);
				
				return resultado;
				
			} finally {
				if (con != null)
					con.close();
			}
		} catch (SQLException e){
			throw new DAOException (e.getMessage());
		}
	}
}
