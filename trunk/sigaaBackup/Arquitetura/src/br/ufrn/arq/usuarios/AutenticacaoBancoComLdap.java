/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.arq.usuarios;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.PermissaoDAO;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.AmbienteUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Implementação da estratégia de autenticação por banco de dados
 * sincronizando com a base LDAP.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class AutenticacaoBancoComLdap implements EstrategiaAutenticacao {

	@Override
	public Integer getIdUsuario(HttpServletRequest req, String login) throws ArqException {
		return (Integer) getJdbcTemplate().queryForObject("select id_usuario from comum.usuario where lower(login)=lower(?)", new Object[] { login }, Integer.class);
	}
	
	@Override
	public boolean autenticaUsuario(HttpServletRequest req, String login, String senha) throws ArqException {
		return autenticaUsuario(req, login, senha, true);
	}
	
	@Override
	public boolean autenticaUsuario(HttpServletRequest req, String login, String senha, boolean fazerHash) throws ArqException {
		String senhaBD = null;
		
		try {
			senhaBD = (String) getJdbcTemplate().queryForObject("select senha from comum.usuario where autorizado = true and lower(login)=lower(?)", new Object[] { login }, String.class);
		} catch(EmptyResultDataAccessException e) {
			return false;
		}
		
		String senhaComparacao = (fazerHash ? UFRNUtils.toMD5(senha, ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.SALT_SENHAS_USUARIOS)) : senha);
		if (senhaComparacao.equals(senhaBD)) {
			new UsuarioLDAP(login,senha).start();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean senhaExpirou(HttpServletRequest req, int idUsuario) throws ArqException {
		if (!AmbienteUtils.isServidorProducao())
			return false;
		
		return getJdbcTemplate().queryForInt("select count(*) from (select distinct date(u.ultima_troca_senha) + pa.tempo_alteracao_senha < now() as expirou "
				+ "from comum.usuario u, comum.papel pa, comum.permissao pe " 
				+ "where u.id_usuario = ? and pe.id_usuario = u.id_usuario " 
				+ "and pe.id_papel = pa.id and pa.tempo_alteracao_senha is not null) as q "
				+ "where q.expirou = trueValue()", new Object[] { idUsuario }) > 0;
	}

	@Override
	public void atualizaSenhaAtual(HttpServletRequest req, int idUsuario, String senhaAtual, String novaSenha) throws ArqException {
		getJdbcTemplate().update("update comum.usuario set senha = ?, ultima_troca_senha = now() where id_usuario = ?", new Object[] { UFRNUtils.toMD5(novaSenha, ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.SALT_SENHAS_USUARIOS)), idUsuario });
		
		String login = (String) getJdbcTemplate().queryForObject("select login from comum.usuario where id_usuario = ?", new Object[] { idUsuario }, String.class);
		new UsuarioLDAP(login, novaSenha).start();
	}

	@Override
	public boolean autenticaUsuarioMobile(HttpServletRequest req, String login, String senha) throws ArqException {
		String senhaBD = (String) getJdbcTemplate().queryForObject("select senha_mobile from comum.usuario where lower(login)=lower(?)", new Object[] { login }, String.class);

		if (UFRNUtils.toMD5(senha, ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.SALT_SENHAS_USUARIOS)).equals(senhaBD)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean localizaUsuarioMobile(HttpServletRequest req, String login) throws ArqException {
		String sql = " select m.senha_mobile "
				+ " from mobile.usuario_mobile m "
				+ " inner join comum.usuario u ON u.id_usuario = m.id_usuario "
				+ " where u.login = ? ";

		String senhaMobileHashBD = (String) getJdbcTemplateSigaa()
				.queryForObject(sql, new Object[] { login }, String.class);

		if (senhaMobileHashBD != null)
			return true;
		else
			return false;
	}

	@Override
	public void carregaPermissoes(HttpServletRequest req, UsuarioGeral usuario) throws ArqException {
		PermissaoDAO dao = new PermissaoDAO();
		usuario.setPapeis(dao.findPapeisAtivosByUsuario(usuario.getId(), new Date()));
		usuario.setPermissoes(dao.findPermissaosAtivosByUsuario(usuario.getId(), new Date()));		
	}

	/*
	 * Retorna o JdbcTemplate associado ao banco comum.
	 */
	private JdbcTemplate getJdbcTemplate() {
		return new JdbcTemplate(Database.getInstance().getComumDs());
	}
		
	/*
	 * Retorna o JdbcTemplate associado ao banco do sigaa.
	 */
	private JdbcTemplate getJdbcTemplateSigaa() {
		return new JdbcTemplate(Database.getInstance().getSigaaDs());
	}

	@Override
	public void atualizaSenhaMobile(int idUsuario, String senhaMobile) throws ArqException {
		getJdbcTemplate().update("update comum.usuario set senha_mobile = ? where id_usuario = ?", new Object[] { UFRNUtils.toMD5(senhaMobile, ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.SALT_SENHAS_USUARIOS)), idUsuario });		
	}

	@Override
	public boolean sincronizarComBancoDados() {
		return false;
	}

	@Override
	public InfoUsuarioDTO carregaInfoUsuario(HttpServletRequest req, final String login) throws ArqException {
		String sql = "select u.id_usuario, u.inativo, u.id_pessoa, p.cpf_cnpj, p.nome, u.email, un.codigo_unidade, s.siape "
			+ "from comum.usuario u left join comum.pessoa p using (id_pessoa) left join comum.unidade un using (id_unidade) " 
			+ "left outer join rh.servidor s using (id_pessoa) where u.login = ?";
		
		return (InfoUsuarioDTO) getJdbcTemplate().queryForObject(sql, new Object[] { login }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				InfoUsuarioDTO dto = new InfoUsuarioDTO();
				dto.setId(rs.getInt("id_usuario"));
				dto.setLogin(login);
				dto.setIdPessoa(rs.getInt("id_pessoa"));
				dto.setCodigoUnidade(rs.getLong("codigo_unidade"));
				dto.setCpfCnpj(rs.getLong("cpf_cnpj"));
				dto.setEmail(rs.getString("email"));
				dto.setInativo(rs.getBoolean("inativo"));
				dto.setSiape(rs.getInt("siape"));
				dto.setNome(rs.getString("nome"));
				return dto;
			}
		});
	}

	@Override
	public boolean usuarioAtivo(HttpServletRequest req, int sistema, int idUsuario) {
		return (Boolean) getJdbcTemplate().queryForObject("select "
				+ "not exists(select * from comum.usuario_permissao_sistema where id_usuario = ? and id_sistema = ? and permissao = 'D') "
				+ "and not exists(select * from comum.usuario_permissao_sistema where id_usuario = ? and id_sistema is null and permissao = 'D' "
				+ "and not exists(select * from comum.usuario_permissao_sistema where id_usuario = ? and id_sistema = ? and permissao = 'G' ))", 
				new Object[] { idUsuario, sistema, idUsuario, idUsuario, sistema }, Boolean.class);
	}

	@Override
	public String validarLogin(String login) throws NegocioException {
		if (login.indexOf(" ") != -1) 
			throw new NegocioException("Não é possível cadastrar login com espaços em brancos");
		
		String chars = "`~!@#$%^&*()=+[{]}|\\\'\":;/?,<>Áª£¢°¤¦¥»¼­ÒÔÇ¾ÉÂûÆú©Ä¶§ŒÏ·«¨ ´¬ö¿¹³²µ÷º";
		
		for (int i = 0; i < chars.length(); i++) {
			if (login.indexOf(chars.charAt(i)) != -1) {
				throw new NegocioException("Caractere inválido encontrado no login: " + chars.charAt(i));
			}
		}
		
		if (!login.equals(StringUtils.toAscii(login))) {
			throw new NegocioException("Login não pode ter acentos ou caracteres especiais.");
		}
		
		try {
			Integer.parseInt(login);
			throw new NegocioException("Não é permitido cadastrar um login apenas com números.");
		} catch(NumberFormatException e) { 	}
		
		return login.toLowerCase();
	}

}
