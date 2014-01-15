/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 14/07/2009
 */
package br.ufrn.arq.seguranca.log;

import java.util.Date;

/**
 * Classe utilizada para Log de updates realizados
 * com JDBC.
 *
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class LogJdbcUpdate implements LogUFRN {

	private int id;

	/** SQL utilizado para o update no banco de dados */
	private String sql;

	/** Parâmetros enviados à consulta */
	private Object[] params;
	
	/** Parâmetros concatenados em uma String */
	private String paramsStr;
	
	/** Registro de entrada do usuário que efetua a operação */
	private Integer idRegistroEntrada;
	
	/** Registro de entrada do usuário que efetua a operação */
	private Integer idUsuario;
	
	/** Data da realização da operação. */
	private Date data;

	/** Código do processador onde a operação é processada. */
	private Integer codMovimento;

	/** Sistema em que se realiza a operação. 1 - SIPAC, 2 - PROTOCOLO, 3 - SCO, 4 - SIGAA */
	private int sistema;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public Integer getIdRegistroEntrada() {
		return idRegistroEntrada;
	}

	public void setIdRegistroEntrada(Integer idRegistroEntrada) {
		this.idRegistroEntrada = idRegistroEntrada;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Integer getCodMovimento() {
		return codMovimento;
	}

	public void setCodMovimento(Integer codMovimento) {
		this.codMovimento = codMovimento;
	}

	public int getSistema() {
		return sistema;
	}

	public void setSistema(int sistema) {
		this.sistema = sistema;
	}

	public String getParamsStr() {
		return paramsStr;
	}

	public void setParamsStr(String paramsStr) {
		this.paramsStr = paramsStr;
	}

}