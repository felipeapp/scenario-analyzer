/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/03/2009
 */
package br.ufrn.arq.parametrizacao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;

/**
 * Repositório contendo informações sobre os parâmetros
 * de configuração dos sistemas.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@SuppressWarnings("unchecked")
public class RepositorioDadosInstitucionais {

	private static final JdbcTemplate jt = new JdbcTemplate(Database.getInstance().getComumDs());
	
	/**
	 * Retorna todos os parâmetros de configuração do sistema
	 */
	public static Map<String, String> getAll() {
		return (Map<String, String>) jt.query("select * from comum.dados_institucionais", new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, String> result = new HashMap<String, String>();
				while(rs.next())
					result.put(rs.getString("nome"), rs.getString("valor"));
				return result;
			}
		});
	}

	/**
	 * Dado o nome de um parâmetro, retorna o seu valor 
	 */
	public static String get(String nome) {
		try {
			return (String) jt.queryForObject("select valor from comum.dados_institucionais where nome = ?", new Object[] { nome }, String.class);
		} catch(EmptyResultDataAccessException e) {
			return "";
		}
	}

	/**
	 * Retorna o parâmetro que é o link para o sistema SIGAA
	 */
	public static String getLinkSigaa() {
		return get("linkSigaa");
	}

	/**
	 * Retorna o parâmetro que é o link para o sistema SIPAC
	 */
	public static String getLinkSipac() {
		return get("linkSipac");
	}
	
	/**
	 * Retorna o parâmetro que é o link para o sistema SIGRH
	 */
	public static String getLinkSigrh() {
		return get("linkSigrh");
	}
	
	/**
	 * Retorna o parâmetro que é o link para o sistema SIGAdmin
	 */
	public static String getLinkSigadmin() {
		return get("linkSigadmin");
	}
	
	/**
	 * Retorna o parâmetro que é o link para o sistema SIGPP
	 */
	public static String getLinkSigpp() {
		return get("linkSigpp");
	}
	
	/**
	 * Retorna o parâmetro que é o link para a caixa postal dos sistemas
	 */
	public static String getLinkCaixaPostal() {
		return get("linkCaixaPostal");
	}
	
	/**
	 * Retorna o parâmetro que é o link para o sistema SIGED
	 */
	public static String getLinkSiged() {
		return get("linkSiged");
	}
	
	
	public static String getNomeInstituicao() {
		return get("nomeInstituicao");
	}
	
	public static String getSiglaInstituicao() {
		return get("siglaInstituicao");
	}
	
	public static String getSiglaNomeInstituicao() {
		return ( getSiglaInstituicao() + " - " + getNomeInstituicao() );
	}
	
}
