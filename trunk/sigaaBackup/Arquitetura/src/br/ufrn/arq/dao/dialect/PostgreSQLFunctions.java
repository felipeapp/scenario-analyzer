/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 25/08/2010
 */
package br.ufrn.arq.dao.dialect;


/**
 * Implementação da interface de funções para o PostgreSQL
 * 
 * @author Gleydson Lima
 *
 */
public class PostgreSQLFunctions implements SQLFunctions {

	@Override
	public String extract(String campo, String... args) {
		return "extract(" + args[0] + " from " + campo + ")"; 
	}

	@Override
	public String getFalseValue() {
		return "false";
	}

	@Override
	public String getTrueValue() {
		return "true";
	}

	@Override
	public String nextVal(String seq) {
		return "nextval('" + seq + "')"; 
	}
	
	@Override
	public String limit(String sql, int limit, int offset) {
		return sql + " limit " + limit + (offset > 0 ? " offset " + offset : "" );
	}

	@Override
	public String now() {
		return "now()"; 
	}

	@Override
	public String criarSequencia(String schema, String nomeSequencia) {
		String sequencia = schema == null ? nomeSequencia : schema + "." + nomeSequencia;
		
		String createSeq = "CREATE SEQUENCE  " + sequencia +
				" INCREMENT 1 " +
				" MINVALUE 1 " +
				" MAXVALUE 9223372036854775807 " +
				" START 1 " +
				" CACHE 1 ";
				//" ALTER TABLE " + sequencia + " OWNER TO sipac; " +
				//" GRANT ALL ON TABLE  " + sequencia + " TO sipac; ";
		
		return createSeq;
	}

	@Override
	public String verificarExistenciaSequencia(String esquema, String sequencia) {
		
		String schema = esquema == null ? "public" : esquema;
		
		String sqlVerificaSequencia = "SELECT count(distinct c.relnamespace) "
			+ "FROM pg_catalog.pg_class c, pg_catalog.pg_user u, pg_catalog.pg_namespace n "
			+ "WHERE c.relnamespace=n.oid AND c.relkind = 'S' AND "
			+ "n.nspname='" + schema + "' and  c.relname='" + sequencia + "'";
		
		return sqlVerificaSequencia;
	}
	
	@Override
	public String getCurrentSeqValue(String schema, String nomeSequencia){
		String sequencia = schema == null ? nomeSequencia : schema + "." + nomeSequencia;
		
		return "SELECT last_value FROM " + sequencia;
	}
	
	public String concat(String... strings) {
		
		String concat = strings[0];
		
		for(int i = 1; i < strings.length; i++)
			concat += " || " + strings[i];
		
		return concat;
	}
	
	@Override
	public String toAscii(String campo,String formato) {
		// TODO Auto-generated method stub
		return "to_ascii( "+campo+","+formato+" )";
	}

	@Override
	public String currentTimestamp() {
		return " CURRENT_TIMESTAMP "; 
	}

	@Override
	public String interval(String tempo) {
		return " INTERVAL '" + tempo +  "' ";
	}

	@Override
	public String regexpSplitToTable(String campo, String delimitador) {
		return " regexp_split_to_table(" + campo + ", '" + delimitador + "') ";
	}

	@Override
	public String gerarMecanismoPesquisaTextual(String nomeColuna) {
		return " to_tsvector('portuguese', " + nomeColuna + ") @@ to_tsquery( ? ) ";
	}
	
}