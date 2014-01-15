/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 24/02/2010
 */
package br.ufrn.comum.sincronizacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Métodos utilitários para trabalhar com a base
 * de dados de recursos humanos.
 * 
 * @author Itamir Filho
 *
 */
public class RhUtil {

	public static int getIdPessoaSipac(String cpf, Connection con) {

		Statement st = null;
		ResultSet rs = null;

		try {
			st = con.createStatement();
			rs = st
					.executeQuery("SELECT ID_PESSOA FROM COMUM.PESSOA WHERE CPF_CNPJ = "
							+ cpf);
			if (rs.next()) {
				return rs.getInt("ID_PESSOA");
			} else {
				return 0;
			}
		} catch (Exception e) {
			return 0;
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}

	}

	public static int getIdPessoaSigaa(long cpf, Connection con) {

		Statement st = null;
		ResultSet rs = null;

		try {
			st = con.createStatement();
			rs = st
					.executeQuery("SELECT ID_PESSOA FROM COMUM.PESSOA WHERE CPF_CNPJ = "
							+ cpf);
			if (rs.next()) {
				return rs.getInt("ID_PESSOA");
			} else {
				return 0;
			}
		} catch (Exception e) {
			return 0;
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}

	}
	
	

	public static Timestamp getUltimaAtualizacaoPessoa(int id_pessoa, Connection con) {

		Statement st = null;
		ResultSet rs = null;

		try {
			st = con.createStatement();
			rs = st
					.executeQuery("SELECT ULTIMA_ATUALIZACAO FROM COMUM.PESSOA WHERE ID_PESSOA =  " + id_pessoa);
			if (rs.next()) {
				return rs.getTimestamp("ULTIMA_ATUALIZACAO");
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}

	}

	public static int getIdServidor(String siape, Connection con) {

		Statement st = null;
		ResultSet rs = null;

		try {
			st = con.createStatement();
			rs = st
					.executeQuery("SELECT ID_SERVIDOR FROM rh.SERVIDOR WHERE SIAPE = "
							+ Integer.parseInt(siape.substring(0, siape
									.length() - 1)) + " AND DIGITO_SIAPE=" + Integer.parseInt(siape.substring(siape
											.length() - 1)));
			if (rs.next()) {
				return rs.getInt("ID_SERVIDOR");
			} else {
				return 0;
			}
		} catch (Exception e) {
			return 0;
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}

	}

	public static boolean existeIdServidor(int idServidor, Connection con) {

		Statement st = null;
		ResultSet rs = null;

		try {
			st = con.createStatement();
			rs = st
					.executeQuery("SELECT ID_SERVIDOR FROM rh.SERVIDOR WHERE ID_SERVIDOR = " + idServidor);
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}

	}

	public static int getIdAtividade(int atividade, String origem,
			Connection con) {

		Statement st = null;
		ResultSet rs = null;

		try {

			st = con.createStatement();
			rs = st
					.executeQuery("SELECT ID_ATIVIDADE FROM rh.ATIVIDADE WHERE CODIGO_RH = "
							+ atividade);

			if (rs.next()) {
				return rs.getInt("ID_ATIVIDADE");
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			closeResultSet(rs);
			closeStatement(st);
		}

	}

	/**
	 * Busca a Unidade Federativa do banco do Sigaa
	 *
	 * @param uf
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	public static int getIdUnidadeFederativaSigaa(String uf, Connection con)
			throws SQLException {

		Statement st = con.createStatement();
		ResultSet rs = st
				.executeQuery("SELECT ID_UNIDADE_FEDERATIVA FROM COMUM.UNIDADE_FEDERATIVA WHERE SIGLA = '"
						+ uf + "'");
		return rs.getInt(1);

	}

	public static int getIdMunicipio(String municipio, Connection con)
			throws SQLException {

		Statement st = con.createStatement();
		ResultSet rs = st
				.executeQuery("SELECT ID_MUNICIPIO FROM MUNICIPIO WHERE upper(to_ascii(NOME, 'LATIN9')) = to_ascii('"
						+ municipio.toUpperCase() + "', 'LATIN9')");
		return rs.getInt(1);

	}
	
	/*
	public static int createOrRecoveryTelefone(String numero, int codigo, Connection con) throws SQLException {

		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("SELECT ID_TELEFONE FROM TELEFONE WHERE NUMERO = ? AND CODIGO = 84");

		if( rs.next() ) {
			return rs.getInt(1);
		} else {
			st.executeUpdate("INSERT INTO TELEFONE (CODIGO,NUMERO) VALUES(84,?)");
			return createOrRecoveryTelefone(numero,84,con);
		}
	}
	*/
	
	/**
	 * Busca o ID_PESSOA no SIPAC
	 *
	 * @param agencia
	 * @param numero
	 * @param banco
	 * @param con
	 * @return
	 */
	public static int getIdPessoaSipac(Long cpf, Connection con) {

		PreparedStatement st = null;

		try {
			st = con
					.prepareStatement("SELECT ID_PESSOA FROM COMUM.PESSOA WHERE CPF_CNPJ = ?");

			st.setLong(1, cpf);

			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}

		} catch (Exception e) {
			if (!e.getMessage().startsWith("ERROR: duplicate key")) {
				System.out.println(e.getMessage());
			}
			return 0;
		} finally {
			closeStatement(st);
		}

	}
	
	/**
	 * busca o nome da unidade a partir do id da mesma
	 *
	 * @param id
	 * @param con
	 * @return
	 */

	public static String getNomeUnidade(int id, Connection con) {
		PreparedStatement st = null;

		try {
			st = con
					.prepareStatement("SELECT NOME FROM comum.unidade WHERE ID_UNIDADE = ?");

			st.setInt(1, id);

			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			} else {
				return "";
			}

		} catch (Exception e) {
			return "";
		} finally {
			closeStatement(st);
		}
	}

	public static int getIdAtividade(int codigoAtividade, int origemAtividade, Connection con) {
		PreparedStatement st = null;

		try {
			st = con
					.prepareStatement("SELECT ID_ATIVIDADE FROM rh.ATIVIDADE WHERE CODIGO_RH = ? and ORIGEM_RH = ?");

			st.setInt(1, codigoAtividade);
			st.setString(2, String.valueOf(origemAtividade));

			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStatement(st);
		}
		return 0;
	}

	public static int getIdServidor(ResultSet rsRH, Connection sipacCon) {
		Statement st = null;
		ResultSet rs = null;

		try {
			st = sipacCon.createStatement();
			rs = st
					.executeQuery("SELECT ID_SERVIDOR FROM rh.SERVIDOR WHERE SIAPE = "
							+ rsRH.getInt("siape") + " AND DIGITO_SIAPE='" + rsRH.getInt("DIGITO_SIAPE") + "'");
			if (rs.next()) {
				return rs.getInt("ID_SERVIDOR");
			} else {
				return 0;
			}
		} catch (Exception e) {
			return 0;
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
	}

	public static int getIdServidor(int idServidor, Connection sigaaCon, Connection sipacCon) {
		Statement st = null;
		ResultSet rs = null;
		Statement stSigaa = null;
		ResultSet rsSigaa = null;
		try {
			st = sipacCon.createStatement();
			stSigaa = sigaaCon.createStatement();
			rs = st.executeQuery("SELECT SIAPE, DIGITO_SIAPE FROM rh.SERVIDOR WHERE ID_SERVIDOR = " + idServidor);
			
			if (rs.next()) {				
				int siape = rs.getInt("SIAPE");
				int digitoSiape = rs.getInt("DIGITO_SIAPE");
				rsSigaa = stSigaa.executeQuery("SELECT id_servidor FROM rh.SERVIDOR WHERE SIAPE = "
						+ siape +" and DIGITO_SIAPE = '" + digitoSiape + "'");
				if (rsSigaa.next()) {
					return rsSigaa.getInt("ID_SERVIDOR");
				} else {
					return 0;
				}
			} else {
				return 0;
			}
		} catch (Exception e) {
			return 0;
		} finally {
			closeStatement(st);
			closeStatement(stSigaa);
			closeResultSet(rs);
			closeResultSet(rsSigaa);
		}
	}
	
	private static void closeStatement(Statement st) {
		try {
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void closeResultSet(ResultSet rs) {
		try {
			if(rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
