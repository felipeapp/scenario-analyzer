/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 03/12/2007
 */
package br.ufrn.arq.migracao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import br.ufrn.arq.dao.BDUtils;

/**
 * Classe utilitária para migrar os dados de uma tabela origem para uma tabela
 * destino
 * 
 * @author Gleydson Lima
 * 
 */
public class MigraTabela {

	/**
	 * Lista de campos na tabela origem
	 */
	private ArrayList<String> camposOrigem = new ArrayList<String>();

	private ArrayList<String> camposDestino = new ArrayList<String>();

	private String sequencePKName;

	private String tabelaOrigem;

	private String tabelaDestino;

	private boolean disabled = false;

	private String whereRestrict;

	private ArrayList<ColumnReplace> substituicaoColuna = new ArrayList<ColumnReplace>();

	/*
	 * Modo de atualização de dados
	 */
	private boolean updateMode;

	/**
	 * Updates que serão realizados no final
	 */
	private ArrayList<String> updatesOnFinish = new ArrayList<String>();

	/**
	 * COluna de Merg na origem
	 */
	private String mergOrigem;

	/**
	 * Coluna de merg no destino
	 */
	private String mergDestino;

	/**
	 * Se verdadeiro gera a chave ao invï¿½s de pegar a da origem
	 */
	private boolean generatedKey;

	private static Hashtable<String, Integer> siapes = new Hashtable<String, Integer>();
	private static Hashtable<String, Integer> cpfs = new Hashtable<String, Integer>();

	/**
	 * Se tiver ativo, a propria classe seta os campos de origem e destino. Isto
	 * só é feito se as tabelas de origem e destino forem iguais.
	 */
	private boolean colunasGeradasAutomaticamente;
	
	public ArrayList<String> getPK(String tableName, Connection con) throws Exception {
		if (tableName == null || "".equals(tableName.trim()))
			throw new Exception("O nome da tabela não foi informado ou não é válido!");
		
		ArrayList<String> pk = new ArrayList<String>();
		
		String schema = "public";
		String tabela = "";
		
		if (tableName.contains(".")) {
			String[] ns = tableName.split("\\.");
		
			schema = ns[0];
			tabela = ns[1];
			
			ns = null;
		} else {
			tabela = tableName;
		}
		
		
		StringBuffer sqlToGetPK = new StringBuffer();
		
		sqlToGetPK.append("SELECT pg_namespace.nspname || '.' || pg_class.relname AS nome_da_tabela,");
		sqlToGetPK.append("       replace(substr(pg_get_constraintdef(pg_constraint.oid), 14, length(pg_get_constraintdef(pg_constraint.oid))), ')', '') AS colunas_da_pk");
		sqlToGetPK.append("  FROM pg_namespace JOIN pg_class ON pg_namespace.oid=pg_class.relnamespace");
		sqlToGetPK.append("                    JOIN pg_constraint ON pg_class.oid=pg_constraint.conrelid");
		sqlToGetPK.append(" WHERE pg_class.relkind = 'r'");
		sqlToGetPK.append("   AND pg_namespace.nspname = '" + schema + "'");
		sqlToGetPK.append("   AND pg_class.relname = '" + tabela + "'");
		sqlToGetPK.append("   AND pg_get_constraintdef(pg_constraint.oid) LIKE 'PRIMARY%'");
		sqlToGetPK.append(" ORDER BY pg_namespace.nspname, pg_class.relname");
		
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(sqlToGetPK.toString());
		
		if (rs.next()) {
			String colunasDaPK = rs.getString("colunas_da_pk");
			
			if (colunasDaPK != null && colunasDaPK.contains(",")) {
				String[] pkCols = colunasDaPK.trim().split(",");
				
				for (int i = 0; i < pkCols.length; i++)
					pk.add(pkCols[i].trim());
				
				pkCols = null;
				
			} else if (colunasDaPK != null && !"".equals(colunasDaPK.trim())) {
				pk.add(colunasDaPK.trim());
			}
			
			colunasDaPK = null;
		}
		
		rs.close();
		st.close();
		
		rs = null;
		st = null;
		
		return pk;
	}

	public Object getObject(ResultSet rs, String coluna, Connection con)
			throws SQLException {
		if (coluna.equalsIgnoreCase("siape")) {
			return getIdServidor(rs.getString(coluna), con);
		} else if (coluna.equalsIgnoreCase("Matricula")) {
			return getIdServidor(rs.getString(coluna), con);
		} else if (coluna.equalsIgnoreCase("Servidor")) {
			return getIdPessoa(rs.getString(coluna), con);
		} else if (coluna.equalsIgnoreCase("Regime")) {
			return getIdTipoRegimeJuridico(rs.getString(coluna));
		} else {
			// procura por substituições de coluna
			for (ColumnReplace col : substituicaoColuna) {
				if (col.getNomeColuna().equals(coluna)) {
					Object value = rs.getObject(coluna);
					if (value != null && value.equals(col.getOrigemValue())) {
						return col.getDestValue();
					}
				}
			}
			return rs.getObject(coluna);
		}
	}

	public Object getObject(ResultSet rs, int index, Connection con)
			throws SQLException {

		String name = rs.getMetaData().getColumnName(index);
		return getObject(rs, name, con);
	}

	@SuppressWarnings("unchecked")
	public void migrarDados(Connection conOrigem, Connection conDestino)
			throws Exception {

		if (disabled)
			return;

		int inseridos = 0;
		int atualizados = 0;
		
		Statement stOrigem = conOrigem.createStatement();
		Statement stDestino = conDestino.createStatement();

		if (colunasGeradasAutomaticamente) {

			camposOrigem = carregaCamposTabela(conOrigem, tabelaOrigem);
			camposDestino = carregaCamposTabela(conDestino, tabelaDestino);
			/* Validar quantidade de colunas entre tabelas. */
			ArrayList<String> diff = new ArrayList<String>();
			
			if (camposDestino.size() > camposOrigem.size()){
				diff = (ArrayList<String>) camposDestino.clone();
				diff.removeAll(camposOrigem);
				System.out.print("[ERRO] Quantidade de colunas na tabela de origem: "+tabelaOrigem+" diferente. Colunas:");
				for (String coluna : diff) {
					System.out.print(coluna+" | ");
				}
				System.out.println();
			}else if (camposDestino.size() < camposOrigem.size()){
				diff = (ArrayList<String>) camposOrigem.clone();
				diff.removeAll(camposDestino);
				System.out.print("[ERRO] Quantidade de colunas na tabela de origem: "+tabelaDestino+" diferente. Colunas:");
				for (String coluna : diff) {
					System.out.print(coluna+" | ");
				}
				System.out.println();
			}
		}

		Statement stDestinoAuxiliar = conDestino.createStatement();
		ResultSet rs = stOrigem.executeQuery("select " + geraCamposOrigem()
				+ " from " + tabelaOrigem
				+ (whereRestrict != null ? " where " + whereRestrict : ""));

		ResultSet rsDestino = stDestino.executeQuery("select "
				+ geraCamposDestino() + " from " + tabelaDestino + BDUtils.limit(1));

		String generatedKeyStr = "";

		int progresso = 0;

		while (rs.next()) {

			boolean atualizouRegistro = false;
			boolean achouRegistro = false;
			boolean alterouBD = false;

			// chave primaria de origem
			String pkeyOrigem = null;
			
			ArrayList<String> pkeysOrigem = getPK(tabelaOrigem, conOrigem);
			
			boolean useArrayOrigem = false;
			
			if (pkeysOrigem != null && pkeysOrigem.size() == 1)
				pkeyOrigem = pkeysOrigem.get(0);
			else if (pkeysOrigem != null && pkeysOrigem.size() > 1)
				useArrayOrigem = true;
			else
				pkeyOrigem = camposOrigem.get(0);
			
			
			// chave primaria de destino
			String pkeyDestino = null;
			
			ArrayList<String> pkeysDestino = getPK(tabelaDestino, conDestino);
			
			boolean useArrayDestino = false;
			
			if (pkeysDestino != null && pkeysDestino.size() == 1)
				pkeyDestino = pkeysDestino.get(0);
			else if (pkeysDestino != null && pkeysDestino.size() > 1)
				useArrayDestino = true;
			else
				pkeyDestino = camposDestino.get(0);
			
			
			Object obj = null;
			
			if (!useArrayOrigem)
				obj = getObject(rs, pkeyOrigem, conDestino);

			if (!generatedKey) {

				if (mergOrigem != null) {

					ResultSet rsExistencia = stDestinoAuxiliar
							.executeQuery("select " + mergDestino + " from "
									+ tabelaDestino + " where " + mergDestino
									+ " = '" + rs.getString(mergOrigem) + "'");

					achouRegistro = rsExistencia.next();

				} else {

					String consulta = null;
					
					if (useArrayOrigem && useArrayDestino) {
						
						consulta = "select " + getPKeysColumns(pkeysDestino) + " " +
								   "  from " + tabelaDestino + 
								   " where " + getPKeysWhere(pkeysOrigem, rs);
						
						
					} else {
						consulta = "select " + pkeyDestino + " from "
							+ tabelaDestino + " where " + pkeyDestino + " = "
							+ obj;
					}

					ResultSet rsExistencia = stDestinoAuxiliar
							.executeQuery(consulta);

					achouRegistro = rsExistencia.next();

				}

			}

			if (updateMode) {

				if (generatedKey) {
					throw new SQLException(
							"Update MOde não pode ser feito com Generate Key habilitado");
				} else {

					if (achouRegistro) {
						StringBuffer sqlUpd = new StringBuffer();
						
						sqlUpd.append("UPDATE " + tabelaDestino + " SET ");
						
						String sqlUpdate = "UPDATE " + tabelaDestino + " SET ";

						for (int i = 1; i < camposDestino.size(); i++) {
							int columnType = rsDestino.getMetaData().getColumnType(i + 1);
							
							Object value = getObject(rs, (i + 1), conOrigem);
							
							sqlUpd.append(camposDestino.get(i) + " = ");
							
							if (columnType == Types.CHAR || columnType == Types.VARCHAR || columnType == Types.DATE || columnType == Types.TIME || columnType == Types.TIMESTAMP) {
								sqlUpd.append((value != null ? "'" + value.toString().trim() + "'" : null));
							} else {
								sqlUpd.append((value != null ? value.toString().trim() : null));
							}
							
							value = null;
							
							sqlUpdate += camposDestino.get(i) + " = ? ";
							if (i < camposDestino.size() - 1) {
								sqlUpd.append(",");
								sqlUpdate += ",";
							}
						}
						
						if (useArrayOrigem && useArrayDestino) {
							sqlUpd.append(" where " + getPKeysWhere(pkeysDestino, rs));
							
							sqlUpdate += " where " + getPKeysWhere(pkeysDestino, rs);
						} else {
							sqlUpd.append(" where " + pkeyDestino + " = " + obj);
							
							sqlUpdate += " where " + pkeyDestino + " = " + obj;
						}
						
						
						// SQL mostrando valores para uso posterior
						System.out.println(sqlUpd.toString());
						
						sqlUpd = null;

						PreparedStatement pSt = conDestino
								.prepareStatement(sqlUpdate);

						for (int i = 2; i <= camposOrigem.size(); i++) {
							pSt.setObject(i - 1, getObject(rs, i, conDestino),
									rsDestino.getMetaData().getColumnType(i));
						}

						// Não mostra valores, apenas ?
						//System.out.println(pSt);

						pSt.executeUpdate();
						atualizados++;
						progresso++;
						atualizouRegistro = true;
						alterouBD = true;

					} else {
						atualizouRegistro = false;
					}

				}
			}

			if (!atualizouRegistro && !achouRegistro) {

				if (generatedKey) {
					generatedKeyStr = "(select nextval('" + sequencePKName
							+ "')),";
				}

				int totalInterrogacoes = camposDestino.size();
				int deslocamentoCampos = 0;
				if (generatedKey) {
					totalInterrogacoes--;
					deslocamentoCampos = 1;
				}

				String insertDestino = "INSERT INTO " + tabelaDestino + "("
						+ geraCamposDestino() + ") VALUES ( " + generatedKeyStr
						+ geraInterrogacoes(totalInterrogacoes) + ")";

				PreparedStatement pStDestino = conDestino
						.prepareStatement(insertDestino);
				
				StringBuffer sqlInsert = new StringBuffer();
				
				sqlInsert.append("INSERT INTO " + tabelaDestino + " (" + geraCamposDestino() + ")");
				sqlInsert.append(" VALUES (" + generatedKeyStr);

				for (int i = 1; i <= camposOrigem.size(); i++) {
					pStDestino.setObject(i, getObject(rs, i, conDestino),
							rsDestino.getMetaData().getColumnType(
									i + deslocamentoCampos));
					int columnType = rsDestino.getMetaData().getColumnType(i + deslocamentoCampos);
					
					Object value = getObject(rs, i, conDestino);
					
					if (columnType == Types.CHAR || columnType == Types.VARCHAR || columnType == Types.DATE || columnType == Types.TIME || columnType == Types.TIMESTAMP) {
						sqlInsert.append((i > 1 ? "," : "") + (value != null ? "'" + value.toString().trim() + "'" : null));
					} else {
						sqlInsert.append((i > 1 ? "," : "") + (value != null ? value.toString().trim() : null));
					}
					
					value = null;
				}

				sqlInsert.append(");");
				
				// SQL mostrando valores para uso posterior
				System.out.println(sqlInsert.toString());
				
				sqlInsert = null;
				
				//SQL do statement, não mostra valores, apenas ?
				//System.out.println(pStDestino);

				pStDestino.executeUpdate();
				alterouBD = true;
				inseridos++;

				progresso++;

			}

			if (alterouBD) {

				if (progresso % 100 == 0) {
					System.out.print(".");
				}
				if (progresso % 2000 == 0) {
					System.out.print(".");
				}
			}
		}

		System.out.println("Migração finalizada na tabela: " + tabelaOrigem + "." + inseridos + " inseridos, "
				+ atualizados + " atualizados.");

	}

	private String getPKeysWhere(ArrayList<String> pkeys, ResultSet rs) throws SQLException {
		String where = null;
		
		for (int i = 0; i < pkeys.size(); i++) {
			String pk = pkeys.get(i);
			
			Object value = rs.getObject(pk);
			
			if (value != null) {
				if (where == null)
					where = "";
				if (value instanceof String)
					value = "'" + value + "'";
				
				where += (i > 0 ? " AND " : " ") + pk + " = " + value;
			}
		}
		
		return where;
	}
	
	private String getPKeysColumns(ArrayList<String> pkeys) {
		String columns = "";
		
		for (int i = 0; i < pkeys.size(); i++)
			columns += (i > 0 ? ", " : "") + pkeys.get(i);
		
		return columns;
	}

	public void addReplace(String coluna, Object valueOrigem,
			Object valueDestino) {
		substituicaoColuna.add(new ColumnReplace(coluna, valueOrigem,
				valueDestino));
	}

	public String geraInterrogacoes(int count) {
		String result = "";
		for (int a = 0; a < count; a++) {
			result += "?";
			if (a + 1 != count) {
				result += ",";
			}
		}
		return result;
	}

	public String geraCamposOrigem() {
		String result = "";
		for (int a = 0; a < camposOrigem.size(); a++) {
			result += camposOrigem.get(a);
			if (a + 1 != camposOrigem.size()) {
				result += ",";
			}
		}
		return result;
	}

	public String geraCamposDestino() {
		String result = "";
		for (int a = 0; a < camposDestino.size(); a++) {
			result += camposDestino.get(a);
			if (a + 1 != camposDestino.size()) {
				result += ",";
			}
		}
		return result;
	}

	/**
	 * Carrega automaticamente os campos da tabela. Usado nos casos odne tabela
	 * de origem e destino são iguais para evitar colocar os campos e fazer
	 * automático.
	 * 
	 * @throws SQLException
	 */
	public ArrayList<String> carregaCamposTabela(Connection con, String tabela)
			throws SQLException {

		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("select * from " + tabela + " " + BDUtils.limit(1));
		ResultSetMetaData rsmd = rs.getMetaData();
		int total = rsmd.getColumnCount();
		ArrayList<String> campos = new ArrayList<String>();
		for (int i = 1; i <= total; i++) {
			campos.add(rsmd.getColumnName(i));
		}

		rs.close();
		rs.close();
		
		return campos;

	}

	public static Integer getIdServidor(String siape, Connection con)
			throws SQLException {

		Integer idServidor = siapes.get(siape);
		if (idServidor != null) {
			return idServidor;
		}

		Statement stServidor = null;
		ResultSet rsServidor = null;
		try {
			stServidor = con.createStatement();
			rsServidor = stServidor
					.executeQuery("SELECT id_servidor FROM rh.servidor WHERE siape = "
							+ Integer.parseInt(siape.substring(0, siape
									.length() - 1))
							+ " AND digito_siape="
							+ Integer.parseInt(siape
									.substring(siape.length() - 1)));
			if (rsServidor.next()) {
				idServidor = rsServidor.getInt("id_servidor");
				siapes.put(siape, idServidor);
				return idServidor;
			} else {
				return null;
			}
		} finally {
			try {
				if (stServidor != null) 
					stServidor.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (rsServidor != null) 
					rsServidor.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public static Integer getIdPessoa(String cpf, Connection con)
			throws SQLException {

		Integer idPessoa = cpfs.get(cpf);
		if (idPessoa != null) {
			return idPessoa;
		}

		Statement stPessoa = null;
		ResultSet rsPessoa = null;
		try {
			stPessoa = con.createStatement();
			rsPessoa = stPessoa
					.executeQuery("SELECT id_pessoa FROM comum.pessoa WHERE cpf_cnpj = '"
							+ Long.parseLong(cpf) + "'");
			if (rsPessoa.next()) {
				idPessoa = rsPessoa.getInt("id_pessoa");
				cpfs.put(cpf, idPessoa);
				return idPessoa;
			} else {
				return null;
			}
		} finally {
			try {
				if (stPessoa != null) stPessoa.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (rsPessoa != null) rsPessoa.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public static int getIdTipoRegimeJuridico(String regime)
			throws SQLException {

		if (regime == null) {
			return 5;
		}
		if (regime.equals("EST")) {
			return 1;
		} else if (regime.equals("CLT")) {
			return 2;
		} else if (regime.equals("MRD")) {
			return 3;
		} else if (regime.equals("CDT")) {
			return 4;
		} else {
			return 5;
		}
		/*
		 * 1;"EST";"Estatutário" 2;"CLT";"Celetista" 3;"MRD";"Médico Residente"
		 * 4;"CDT";"Contrato Temporário"
		 */

	}

	public ArrayList<String> getCamposDestino() {
		return camposDestino;
	}

	public void setCamposDestino(ArrayList<String> camposDestino) {
		this.camposDestino = camposDestino;
	}

	public ArrayList<String> getCamposOrigem() {
		return camposOrigem;
	}

	public void setCamposOrigem(ArrayList<String> camposOrigem) {
		this.camposOrigem = camposOrigem;
	}

	public String getSequencePKName() {
		return sequencePKName;
	}

	public void setSequencePKName(String sequencePKName) {
		this.sequencePKName = sequencePKName;
	}

	public String getTabelaDestino() {
		return tabelaDestino;
	}

	public void setTabelaDestino(String tabelaDestino) {
		this.tabelaDestino = tabelaDestino;
	}

	public String getTabelaOrigem() {
		return tabelaOrigem;
	}

	public void setTabelaOrigem(String tabelaOrigem) {
		this.tabelaOrigem = tabelaOrigem;
	}

	public void addCamposOrigem(String... origem) {
		camposOrigem.addAll(Arrays.asList(origem));
	}

	public void addCamposDestino(String... destino) {
		camposDestino.addAll(Arrays.asList(destino));
	}

	public boolean isGeneratedKey() {
		return generatedKey;
	}

	public void setGeneratedKey(boolean generatedKey) {
		this.generatedKey = generatedKey;
	}

	public boolean isUpdateMode() {
		return updateMode;
	}

	public void setUpdateMode(boolean updateMode) {
		this.updateMode = updateMode;
	}

	public ArrayList<String> getUpdatesOnFinish() {
		return updatesOnFinish;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getWhereRestrict() {
		return whereRestrict;
	}

	public void setWhereRestrict(String whereRestrict) {
		this.whereRestrict = whereRestrict;
	}

	public void addReplaceBoolean(String string) {
	}

	public void addReplaceStringToBoolean(String string) {
		addReplace(string, "S", Boolean.TRUE);
		addReplace(string, "N", Boolean.FALSE);
	}

	public void addReplaceIntToBoolean(String string) {
		addReplace(string, 1, Boolean.TRUE);
		addReplace(string, 0, Boolean.FALSE);
	}

	public String getMergDestino() {
		return mergDestino;
	}

	public void setMergDestino(String mergDestino) {
		this.mergDestino = mergDestino;
	}

	public String getMergOrigem() {
		return mergOrigem;
	}

	public void setMergOrigem(String mergOrigem) {
		this.mergOrigem = mergOrigem;
	}

	public boolean isColunasGeradasAutomaticamente() {
		return colunasGeradasAutomaticamente;
	}

	public void setColunasGeradasAutomaticamente(
			boolean colunasGeradasAutomaticamente) {
		this.colunasGeradasAutomaticamente = colunasGeradasAutomaticamente;
	}
}
