/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 09/05/2008
 */
package br.ufrn.arq.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

/**
 * Classe que estende as funcionalidades do JdbcTemplate
 * do Spring.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class JdbcTemplate extends org.springframework.jdbc.core.JdbcTemplate {

	public JdbcTemplate(DataSource ds) {
		super(ds);
	}
	
	/** Dado um SQL estático, retorna o double referente ao resultado da consulta. */
	public double queryForDouble(String sql) {
		return queryForDouble(sql, (Object[]) null);
	}
	
	/** Dado um SQL com parâmetros e um array de parâmetros, retorna o double referente ao resultado da consulta. */
	public double queryForDouble(String sql, Object... args) {
		return (Double) queryForObject(sql, args, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getDouble(1);
			}
		});
	}
	
	/** Dado um SQL estático, retorna o float referente ao resultado da consulta. */
	public float queryForFloat(String sql) {
		return queryForFloat(sql, (Object[]) null);
	}
	
	/** Dado um SQL com parâmetros e um array de parâmetros, retorna o float referente ao resultado da consulta. */
	public float queryForFloat(String sql, Object... args) {
		return (Float) queryForObject(sql, args, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getFloat(1);
			}
		});
	}

	public int queryForInt(String sql, Object param) {
		return super.queryForInt(sql, new Object[] { param });
	}
	
	public long queryForLong(String sql, Object param) {
		return super.queryForLong(sql, new Object[] { param });
	}

	public Character queryForChar(String sql, Object... params) {
		try {
			String value = (String) queryForObject(sql, params, String.class);
			return value.charAt(0);
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
}
