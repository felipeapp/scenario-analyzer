/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 25/08/2010
 */
package br.ufrn.arq.dao.dialect;

/**
 * Interface das funções específicas que não são contempladas pelo 
 * padrão SQL-92 ([11:29:17] William Coelho Rocha: http://www.microsoft.com/express/) 
 * 
 * @author Gleydson Lima
 *
 */
public interface SQLFunctions {
	
	/**
	 * Implementa o método para a função extract onde a faz a extração de parte do dado de um campo.
	 * 
	 * @param campo
	 * @param args
	 * @return
	 */
	public String extract(String campo, String...args);
	
	/**
	 * Implementa o valor booleano verdadeiro 
	 *  
	 * @return
	 */
	public String getTrueValue();
	
	/**
	 * Implementa o valor booleano falso
	 * 
	 * @return
	 */
	public String getFalseValue();
	
	/**
	 * Retorna o próximo valor para uma sequência
	 * 
	 * @param seq
	 * @return
	 */
	public String nextVal(String seq);
	
	/**
	 * Implementa a limitação do retorno de resultados por uma consulta.
	 * 
	 * @param sql
	 * @param limit
	 * @param offset
	 * @return
	 */
	public String limit(String sql, int limit, int offset);
	
	/**
	 * Implementa a função que retorna a data e hora atual.
	 * 
	 * @return
	 */
	public String now();
	
	/**
	 * Método que implementa como deve ser verificado se uma sequência já está criada no banco de dados
	 * @param schema
	 * @param sequencia
	 * @return
	 */
	public String verificarExistenciaSequencia(String schema , String sequencia);
	
	/**
	 * Método que implementa como deve ser feita a criação de uma sequência.
	 * 
	 * @param schema
	 * @param sequencia
	 * @return
	 */
	public String criarSequencia(String schema , String sequencia) ;
	
	/**
	 * Método que implementa a concatenação de strings numa consulta
	 * @param s1
	 * @param s2
	 * @return
	 */
	public String concat(String... strings);
	
	/**
	 * Recupera o valor atual de uma sequência.
	 * 
	 * @param schema
	 * @param sequencia
	 * @return
	 */
	public String getCurrentSeqValue(String schema, String sequencia);
	
	/**
	 * Método responsável por fazer a conversão de uma string para ascii em uma consulta.
	 * 
	 * @param texto
	 * @return
	 */
	public String toAscii(String campo,String formato);
	
	/**
	 * Implementa a função que retorna o tempo corrente
	 * @return
	 */
	public String currentTimestamp();
	
	/**
	 * Implementa a função que retorna um intervalo de tempo.
	 * 
	 * @param tempo intervalo tempo
	 * @return
	 */
	public String interval(String tempo);
	
	/**
	 * Retorna novos registros a partir da divisão de uma string pelo seu delimitador, 
	 * ou seja, irá gerar novas linhas para cada ocorrências do delimitador.
	 * <br /><br />
	 * Ex: <br />        
	 * SELECT 1, regexp_split_to_table('1,2,3',',')
	 * <br />
	 * Resultado:<br />
	 * | 1 | 1 |<br />
	 * | 1 | 2 |<br />
	 * | 1 | 3 |<br />  
	 * 
	 * @param campo
	 * @return
	 */
	public String regexpSplitToTable(String campo, String delimitador);
	
}