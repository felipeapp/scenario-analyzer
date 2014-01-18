/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 25/08/2010
 */
package br.ufrn.arq.dao.dialect;

import java.util.ResourceBundle;

import br.ufrn.arq.erros.ConfiguracaoAmbienteException;

/**
 * Classe que deverá conter as chamadas para as especificidades do Dialeto do banco. 
 *  
 * @author Gleydson Lima
 *
 */
public class SQLDialect {

	private static SQLFunctions builder;
	
	private static String dialect;
	
	public static String TRUE;
	
	public static String FALSE;
	
	
	static { 
		ResourceBundle properties = ResourceBundle.getBundle("br.ufrn.arq.dao.dialect.dialect");
		dialect = properties.getString("dialect");
		try {
			builder =  (SQLFunctions) Class.forName( properties.getString(dialect) ).newInstance();
			TRUE = builder.getTrueValue();
			FALSE = builder.getFalseValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static SQLFunctions getInstance() {
		if ( builder == null ) {
			throw new ConfiguracaoAmbienteException("Dialeto do SQL não defindio");
		}
		return builder;
	}
	
	public static String extractDate(String campo, String...args) {
		return getInstance().extract(campo, args);
	}
	
	public static String nextVal(String seq) {
		return getInstance().nextVal(seq);
	}
	
	public static String limit(String sql, int init, int offset) {
		return getInstance().limit(sql, init, offset);
	}
	
	public static String now() {
		return getInstance().now();
	}
	
	public static String verificarExistenciaSequencia(String schema, String sequencia) {
		return getInstance().verificarExistenciaSequencia(schema, sequencia);
	}
	
	public static String  criarSequencia(String schema, String sequencia) {
		return getInstance().criarSequencia(schema, sequencia);
	}
	
	public static String getCurrentSeqValue(String schema, String sequencia) {
		return getInstance().getCurrentSeqValue(schema, sequencia);
	}
	
	public static String concat(String... strings) {
		return getInstance().concat(strings);
	}
	
	public static String toAscii(String campo,String formato) {
		return getInstance().toAscii(campo,formato);
	}
	
	public static String currentTimestamp() {
		return getInstance().currentTimestamp();
	}
	
	public static String interval(String tempo) {
		return getInstance().interval(tempo);
	}
	
	public static String regexpSplitToTable(String campo, String delimitador){
		return getInstance().regexpSplitToTable(campo, delimitador);
	}
	
	public String gerarMecanismoPesquisaTextual(String nomeColuna) {
		return getInstance().gerarMecanismoPesquisaTextual(nomeColuna);
	}
	
}
