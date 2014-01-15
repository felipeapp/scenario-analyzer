/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 25/02/2008
 */
package br.ufrn.arq.usuarios;


import java.sql.Types;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.ValidadorCPFCNPJ;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Esta classe é utilizada para rotinas de senhas com os usuários
 *
 * @author Gleydson Lima
 *
 */
public class UserAutenticacao {

	private static EstrategiaAutenticacao estrategia;

	static {
		String classe = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.ESTRATEGIA_AUTENTICACAO);
		estrategia = ReflectionUtils.newInstance(classe);
	}
	
	/**
	 * Autentica se o usuário e senha são válidos
	 */
	public static Integer getIdUsuario(HttpServletRequest req, String login) throws ArqException {
		return estrategia.getIdUsuario(req, login);
	}

	/**
	 * Autentica se o usuário e senha são válidos. Compara a senha do banco
	 * com o MD5 da senha passada como parâmetro.
	 * @throws ArqException 
	 */
	public static boolean autenticaUsuario(HttpServletRequest req, String login, String senha) throws ArqException {
		boolean autenticado = estrategia.autenticaUsuario(req, login, senha);
		sincronizaUsuario(req, login);
		return autenticado;
	}
	
	/**
	 * Autentica se o usuário e senha são válidos. Compara a senha do banco
	 * com a senha passada como parâmetro. Caso o parâmetro fazerHash seja true,
	 * a senha é transformada em hash MD5, caso contrário, a senha passada deverá 
	 * ter sido previamente transformada em hash MD5. 
	 */
	public static boolean autenticaUsuario(HttpServletRequest req, String login, String senha, boolean fazerHash) throws ArqException {
		boolean autenticado = estrategia.autenticaUsuario(req, login, senha, fazerHash);
		sincronizaUsuario(req, login);
		return autenticado;
	}

	private static void sincronizaUsuario(HttpServletRequest req, String login)
			throws ArqException {
		if (sincronizarComBancoDados()) {
			InfoUsuarioDTO info = carregaInfoUsuario(req, login);
			DataSource sipacDs = Database.getInstance().getSipacDs();
			DataSource comumDs = Database.getInstance().getComumDs();
			DataSource sigaaDs = null;
			if (Sistema.isSigaaAtivo())
				sigaaDs = Database.getInstance().getSigaaDs();
			
			sincronizarCadastroUsuario(sipacDs, comumDs, sigaaDs, info);
		}
	}
	
	/**
	 * Identifica se o usuário possui papéis que necessitam de alteração de senha e se
	 * o prazo para alteração de senha já passou.
	 * @param idUsuario
	 * @return
	 */
	public static boolean senhaExpirou(HttpServletRequest req, int idUsuario) throws ArqException {
		return estrategia.senhaExpirou(req, idUsuario);
	}
	
	/**
	 * Altera a senha do usuário
	 */
	public static void atualizaSenhaAtual(HttpServletRequest req, int idUsuario, String senhaAtual, String novaSenha) throws ArqException {
		estrategia.atualizaSenhaAtual(req, idUsuario, senhaAtual, novaSenha);
	}
	
	/**
	 * Carrega as permissões do usuário e as armazena no objeto passado
	 * como parâmetro
	 * @param usuario
	 */
	public static void carregaPermissoes(HttpServletRequest req, UsuarioGeral usuario) throws ArqException {
		estrategia.carregaPermissoes(req, usuario);
	}
	
	/**
	 * 
	 * @param login
	 * @return
	 * @throws NegocioException
	 */
	public static String validarLogin(String login) throws NegocioException {
		return estrategia.validarLogin(login);
	}
	
	public static boolean localizaUsuarioMobile(HttpServletRequest req, String login) throws ArqException {
		return estrategia.localizaUsuarioMobile(req, login);
	}
	
	public static boolean autenticaUsuarioMobile(HttpServletRequest req, String login, String senha) throws ArqException {
		return estrategia.autenticaUsuarioMobile(req, login, senha);
	}

	public static void atualizaSenhaMobile(int idUsuario, String senhaMobile) throws ArqException {
		estrategia.atualizaSenhaMobile(idUsuario, senhaMobile);
	}

	public static InfoUsuarioDTO carregaInfoUsuario(HttpServletRequest req, String login) throws ArqException {
		return estrategia.carregaInfoUsuario(req, login);
	}
	
	public static boolean sincronizarComBancoDados() {
		return estrategia.sincronizarComBancoDados();
	}

	public static boolean usuarioAtivo(HttpServletRequest req, int sistema, int idUsuario) throws ArqException {
		return estrategia.usuarioAtivo(req, sistema, idUsuario);
	}
	
	private static void sincronizarCadastroUsuario(DataSource sipacDs, DataSource comumDs, DataSource sigaaDs, InfoUsuarioDTO info) throws ArqException {
		sincronizaPessoa(comumDs, info);
		sincronizaPessoa(sipacDs, info);
		
		if (Sistema.isSigaaAtivo()) {
			sincronizaPessoa(sigaaDs, info);
		}

		JdbcTemplate comumTemplate = new JdbcTemplate(comumDs);
		JdbcTemplate sipacTemplate = new JdbcTemplate(sipacDs);
		
		// Carrega a unidade com o siapecad passado
		Integer idUnidade = null;
		try {
			idUnidade = comumTemplate.queryForInt("select id_unidade from comum.unidade where codigo_siapecad = ?", new Object[] { info.getCodigoUnidade() });
		} catch(EmptyResultDataAccessException e) { 
			
		}

		// Pega um id_usuario unico para todos os sistemas
		int idUsuario = comumTemplate.queryForInt("select nextval('comum.usuario_seq')");
		
		// Verifica se o usuário existe
		if (existeUsuario(sipacDs, info.getLogin())) {
			sipacTemplate.update("update comum.usuario set email = ?, inativo = ?, id_unidade = ?, autorizado = "+SQLDialect.TRUE+" where login = ?", new Object[] { info.getEmail(), info.isInativo(), idUnidade, info.getLogin() });
		} else {
			sipacTemplate.update("insert into comum.usuario (id_usuario, email, inativo, id_unidade, login, autorizado) values (?, ?, ?, ?, ?,  "+SQLDialect.TRUE+" )", new Object[] { idUsuario, info.getEmail(), info.isInativo(), idUnidade, info.getLogin() });
		}
		
		if (existeUsuario(comumDs, info.getLogin())) {
			comumTemplate.update("update comum.usuario set email = ?, inativo = ?, id_unidade = ?, autorizado =  "+SQLDialect.TRUE+"  where login = ?", new Object[] { info.getEmail(), info.isInativo(), idUnidade, info.getLogin() });
		} else {
			comumTemplate.update("insert into comum.usuario (id_usuario, email, inativo, id_unidade, login, autorizado) values (?, ?, ?, ?, ?,  "+SQLDialect.TRUE+" )", new Object[] { idUsuario, info.getEmail(), info.isInativo(), idUnidade, info.getLogin() });
		}
		
		if (Sistema.isSigaaAtivo()) {
			JdbcTemplate sigaaTemplate = new JdbcTemplate(comumDs);
			
			if (existeUsuario(sigaaDs, info.getLogin())) {
				sigaaTemplate.update("update comum.usuario set email = ?, inativo = ?, id_unidade = ?, autorizado =  "+SQLDialect.TRUE+"  where login = ?", new Object[] { info.getEmail(), info.isInativo(), idUnidade, info.getLogin() });
			} else {
				sigaaTemplate.update("insert into comum.usuario (id_usuario, email, inativo, id_unidade, login, autorizado) values (?, ?, ?, ?, ?,  "+SQLDialect.TRUE+" )", new Object[] { idUsuario, info.getEmail(), info.isInativo(), idUnidade, info.getLogin() });
			}
		}
		
	}

	private static void sincronizaPessoa(DataSource ds, InfoUsuarioDTO info) {
		JdbcTemplate conexao = new JdbcTemplate(ds);
		if (existePessoa(ds, info.getIdPessoa())) {
			conexao.update("update comum.pessoa set nome = ?, cpf_cnpj = ? where id_pessoa = ?", new Object[] { info.getNome(), info.getCpfCnpj(), info.getIdPessoa() });
		} else {
			conexao.update("insert into comum.pessoa (id_pessoa, nome, cpf_cnpj, tipo) values (?, ?, ?, ?)", new Object[] { info.getIdPessoa(), info.getNome(), info.getCpfCnpj(), getTipo(info.getCpfCnpj()) }, new int[] { Types.INTEGER, Types.VARCHAR, Types.BIGINT, Types.CHAR });
		}
	}
	
	private static char getTipo(Long cpfCnpj) {
		String cpfCnpjStr = Formatador.getInstance().formatarCPF_CNPJ(cpfCnpj);
		if (ValidadorCPFCNPJ.getInstance().validaCPF(cpfCnpjStr)) {
			return PessoaGeral.PESSOA_FISICA;
		} else {
			return PessoaGeral.PESSOA_JURIDICA;
		}
	}
	
	private static boolean existePessoa(DataSource ds, Integer idPessoa) {
		return new JdbcTemplate(ds).queryForInt("select count(*) from comum.pessoa where id_pessoa = ?", new Object[] { idPessoa }) > 0;
	}
	
	private static boolean existeUsuario(DataSource ds, String login) {
		return new JdbcTemplate(ds).queryForInt("select count(*) from comum.usuario where login = ?", new Object[] { login }) > 0;
	}
	
}
