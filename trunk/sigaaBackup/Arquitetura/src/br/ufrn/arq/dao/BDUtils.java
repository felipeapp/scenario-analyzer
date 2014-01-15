/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 19/10/2009
 */
package br.ufrn.arq.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import javax.persistence.GenerationType;

import org.apache.commons.lang.StringUtils;

import br.ufrn.arq.util.Formatador;

/**
 * Classe que auxilia a construção de SQL nos sistemas.
 * 
 * @author Emerson Senna
 * @author Johnny Marçal
 * @author Gleydson Lima
 */

public class BDUtils {

	/** Valor definido para o tipo de estratégia de GeneratedValue */
	public static GenerationType GENERATIONTYPE = GenerationType.SEQUENCE;

	/**
	 * Dialeto atual configurado
	 */
	private static String dialect = "postgresql";

	/**
	 * Retorna o valor limit para consultas SQL e HQL de acordo com o dialeto
	 * 
	 * @param n
	 * @return
	 */
	public static String limit(int n) {

		if (dialect.equals("postgresql")) {
			return " limit " + n;
		} else if (dialect.equals("oracle")) {
			return " numrows <= " + n;
		}
		return dialect;
	}


	/**
	 * Transforma um ResultSet em um HashMap para uso em relatórios
	 *
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static String resultSetToCSV(ResultSet rs) throws SQLException {

		StringBuffer buf = new StringBuffer(100);
		for (int a = 1; a <= rs.getMetaData().getColumnCount(); a++) {
			String campo = rs.getMetaData().getColumnName(a);
			if (campo != null)
				campo = campo.replace(";", " ");
			buf.append(campo + ";");
		}
		buf.append("\n");

		while (rs.next()) {
			for (int a = 1; a <= rs.getMetaData().getColumnCount(); a++) {
				String campo;
				if (rs.getMetaData().getColumnType(a) == Types.DATE)
					campo = Formatador.getInstance().formatarData(rs.getDate(a));
				else 
					campo = rs.getString(a);
				if (campo != null) {
					campo = campo.replace(";", " ");
					campo = campo.replace("\n", " ");
					campo = campo.replace("\r", " ");
				} else {
					campo = "";
				}
				buf.append(campo + ";");
			}
			buf.append("\n");
		}
		return buf.toString();

	}
	
	/** Cria uma definição para a tabela a ser exportada.
	 * @param rs
	 * @param nomeTabela
	 * @return
	 * @throws SQLException
	 */
	public static String ddlFromResultSet(ResultSet rs, String nomeTabela) throws SQLException {
		StringBuilder ddl = new StringBuilder();
		if (rs != null && !isEmpty(nomeTabela)) {
			ddl.append("CREATE TABLE "+nomeTabela+" (\n");
			ResultSetMetaData metadata = rs.getMetaData();
		    for (int i = 1; i <= metadata.getColumnCount(); i++) {
		    	if (i > 1)
		    		ddl.append(",\n");
		    	ddl.append("\t"+metadata.getColumnLabel(i) +" " + metadata.getColumnTypeName(i));
	        }
		    ddl.append("\n);");
		}
		return ddl.toString();
	}
	
	/**
	 * Converte os dados do resultset passado para inserts em SQL na tabela passada.
	 *
	 * @param nomeTabela
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static String resultSetToSQLInserts(String nomeTabela, ResultSet rs) throws SQLException {
		 return resultSetToSQLInserts(nomeTabela, rs, false);
	 }

	/**
	 * Converte os dados do resultset passado para inserts em SQL na tabela passada.
	 *
	 * @param nomeTabela
	 * @param rs
	 * @param incluiDefinicaoTabela caso true, incluirá a DDL para criar a tabela
	 * @return
	 * @throws SQLException
	 */
	public static String resultSetToSQLInserts(String nomeTabela, ResultSet rs, boolean incluiDefinicaoTabela) throws SQLException {

		StringBuffer buf = new StringBuffer(100);

		if (!isEmpty(nomeTabela) && rs != null) {
			if (incluiDefinicaoTabela) {
				buf.append(ddlFromResultSet(rs, nomeTabela)+"\n");
			}
			
			String colunas = "";
	
			for (int a = 1; a <= rs.getMetaData().getColumnCount(); a++) {
				String campo = rs.getMetaData().getColumnName(a).toString();
				if (campo != null)
					campo = campo.replace(";", " ");
	
				colunas = colunas.length() == 0 ? campo : colunas + ", " + campo;
			}
	
			String inicio = "insert into " + nomeTabela + " (" + colunas + ") values (";
	
			while (rs.next()) {
	
				String dados = "";
	
				for (int a = 1; a <= rs.getMetaData().getColumnCount(); a++) {
	
					String campo = (rs.getObject(a) + "").trim();
					campo = campo.replace(";", " ");
					campo = campo.replace("\n", " ");
					campo = campo.replace("\r", " ");
					campo = campo.replace("'", "''");
	
					if (campo.length() == 0 || campo.equalsIgnoreCase("null"))
						campo = "null";
					else if (!StringUtils.isNumeric(campo) && !campo.equalsIgnoreCase("true") && !campo.equalsIgnoreCase("false"))
						campo = "\'"+campo+"\'";
	
					dados = dados.length() == 0 ? campo : dados + ", " + campo ;
				}
	
				buf.append(inicio + dados + ");\n");
			}
		}
		return buf.toString();

	}
	
	/**
	 * Retorna a palavra chave correta para o operador EXCEPT. <strong>Não utilizar com ALL</strong>,
	 * o Oracle e o SQL Server não têm suporte.
	 */
	public static String except() {
		if ( dialect.equals("oracle") )
			return " MINUS ";
		else
			return " EXCEPT ";
	}
	
}
