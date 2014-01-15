/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/04/2009
 */
package br.ufrn.arq.seguranca.autenticacao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.dominio.TokenAutenticacao;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe para possibilitar o uso de Tokens Single Sign On
 * nos sistemas.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Transactional(propagation=Propagation.NOT_SUPPORTED)
public class TokenGenerator {

	private JdbcTemplate jt = new JdbcTemplate(Database.getInstance().getComumDs());
	
	/**
	 * RowMapper para converter um ResultSet em um Registro de Entrada.
	 */
	private static final RowMapper TOKEN_AUTENTICACAO_MAPPER = new RowMapper() {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	
			TokenAutenticacao token = new TokenAutenticacao();
			token.setId(rs.getInt("id_token_autenticacao"));
			token.setData(rs.getTimestamp("data"));
			token.setKey(rs.getString("key"));
			token.setValido(rs.getBoolean("valido"));
			
			Integer idRegistroEntrada = rs.getInt("id_registro_entrada");
			if(idRegistroEntrada != null) {
				RegistroEntrada registroEntrada = new RegistroEntrada();
				registroEntrada.setId(idRegistroEntrada);
				token.setEntrada(registroEntrada);
			}
			
			Integer idSistemaOrigem = rs.getInt("id_sistema_origem");
			if(idSistemaOrigem != null) 
				token.setOrigem(new Sistema(idSistemaOrigem));
			
			return token;
		}
	};
	
	/**
	 * Cria um token para o usuário passado como parâmetro, gerando sua chave com base nas informações passadas
	 * como parâmetro e inserindo-o no banco de dados.
	 */
	public TokenAutenticacao generateToken(UsuarioGeral usuario, int sistema, String... info) {
		TokenAutenticacao token = new TokenAutenticacao();
		token.setData(new Date());
		token.setOrigem(new Sistema(sistema));
		token.setValido(true);
		token.setEntrada(usuario.getRegistroEntrada());
		token.setKey(generateKey(info));
		
		registerToken(token);
		return token;
	}

	/**
	 * Cria um token para o registro de entrada passado como parâmetro, gerando sua chave com base nas informações passadas
	 * como parâmetro e inserindo-o no banco de dados.
	 */
	public TokenAutenticacao generateToken(Integer idRegistroEntrada, int sistema, String... info) {
		TokenAutenticacao token = new TokenAutenticacao();
		token.setData(new Date());
		token.setOrigem(new Sistema(sistema));
		token.setValido(true);
		token.setKey(generateKey(info));
		
		if (idRegistroEntrada != null) {
			RegistroEntrada reg = new RegistroEntrada();
			reg.setId(idRegistroEntrada);
			token.setEntrada(reg);
		}
		
		registerToken(token);
		return token;
	}
	
	/**
	 * Cria um token para o registro de entrada passado como parâmetro, gerando sua chave com base nas informações passadas
	 * como parâmetro e inserindo-o no banco de dados.
	 */
	public String generateTokenAndGetKey(Integer idRegistroEntrada, int sistema, String... info) {
		TokenAutenticacao token = new TokenAutenticacao();
		token.setData(new Date());
		token.setOrigem(new Sistema(sistema));
		token.setValido(true);
		token.setKey(generateKey(info));
		
		if (idRegistroEntrada != null) {
			RegistroEntrada reg = new RegistroEntrada();
			reg.setId(idRegistroEntrada);
			token.setEntrada(reg);
		}
		
		registerToken(token);
		return token.getKey();
	}
	
	
	/**
	 * Cria uma chave baseada no conjunto de informações
	 * passadas como parâmetro. 
	 */
	public String generateKey(String... info) {
		StringBuilder sb = new StringBuilder("tokenSSOn$$" + System.currentTimeMillis());
		for (String i : info) sb.append(i);
		return UFRNUtils.toMD5(sb.toString());
	}

	/**
	 * Indica se o token é válido, ou seja, se ele existe e ainda não foi usado.
	 */
	public boolean isTokenValid(int idToken, String key) {
		try {
			String keyDb = (String) jt.queryForObject("select key from infra.token_autenticacao where id_token_autenticacao = ? and valido = trueValue()", new Object[] { idToken }, String.class);
			return keyDb.equals(key);
		} catch(EmptyResultDataAccessException e) {
			return false;
		}
	}
	
	/**
	 * Insere o token no banco de dados.
	 */
	public void registerToken(TokenAutenticacao token) {
		int id = jt.queryForInt("select nextval('infra.seq_token_autenticacao')");
		jt.update("insert into infra.token_autenticacao (id_token_autenticacao, key, valido, id_sistema_origem, id_registro_entrada, data) values (?, ?, ?, ?, ?, ?)", new Object[] { id, token.getKey(), token.isValido(), 
				token.getOrigem().getId(), token.getEntrada() == null ? null : token.getEntrada().getId(), token.getData() });
		token.setId(id);
	}
	
	/**
	 * Invalida o token, ou seja, diz que o token já foi usado uma vez e não
	 * pode ser mais usado.  
	 */
	public void invalidateToken(int idToken) {
		jt.update("update infra.token_autenticacao set valido = falseValue() where id_token_autenticacao = ?", new Object[] { idToken });
	}

	/**
	 * Retorna a data de geração do token.
	 * @param idToken
	 * @param key
	 * @return
	 */
	public TokenAutenticacao getToken(int idToken) {
		return  (TokenAutenticacao) jt.queryForObject("select * from infra.token_autenticacao where id_token_autenticacao = ? and valido = trueValue()", new Object[] { idToken }, TOKEN_AUTENTICACAO_MAPPER);
	}
	
}
