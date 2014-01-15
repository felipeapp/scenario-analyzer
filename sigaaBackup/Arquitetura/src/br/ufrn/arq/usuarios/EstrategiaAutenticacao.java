/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 26/06/2009 
 */
package br.ufrn.arq.usuarios;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Interface que define as estratéfias de autenticação dos sistemas.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 */
public interface EstrategiaAutenticacao {

	/**
	 * Retorna o id do usuário cujo login foi passado como parâmetro.
	 * @param req
	 * @param login
	 * @return
	 * @throws ArqException
	 */
	public Integer getIdUsuario(HttpServletRequest req, String login) throws ArqException;
	
	/**
	 * Autentica o usuário com o login e senha passados como parâmetro.
	 * Retorna true se a autenticação foi realizada, false caso contrário.
	 * @param req
	 * @param login
	 * @param senha
	 * @return
	 * @throws ArqException
	 */
	public boolean autenticaUsuario(HttpServletRequest req, String login, String senha) throws ArqException;
	
	/**
	 * Autentica o usuário com o login e senha passados como parâmetro. 
	 * Retorna true se a autenticação foi realizada, false caso contrário.
	 * Se o parâmetro fazerHash for true, a senha será transformada em MD5.
	 * 
	 * @param req
	 * @param login
	 * @param senha
	 * @param fazerHash
	 * @return
	 * @throws ArqException
	 */
	public boolean autenticaUsuario(HttpServletRequest req, String login, String senha, boolean fazerHash) throws ArqException;

	/**
	 * Retorna true se a senha do usuário passado como parâmetro expirou, false caso contrário.
	 * @param req
	 * @param idUsuario
	 * @return
	 * @throws ArqException
	 */
	public boolean senhaExpirou(HttpServletRequest req, int idUsuario) throws ArqException;

	/**
	 * Atualiza a senha do usuário cujo id foi passado como parâmetro. É necessário passar a senha atual
	 * e a nova senha.
	 * @param req
	 * @param idUsuario
	 * @param senhaAtual
	 * @param novaSenha
	 * @throws ArqException
	 */
	public void atualizaSenhaAtual(HttpServletRequest req, int idUsuario, String senhaAtual, String novaSenha) throws ArqException;
	
	/**
	 * Retorna true se o existe um usuário mobile com o login passado como parâmetro.
	 * @param req
	 * @param login
	 * @return
	 * @throws ArqException
	 */
	public boolean localizaUsuarioMobile(HttpServletRequest req, String login) throws ArqException;
	
	/**
	 * Autentica o usuário mobile com o login e senha passados como parâmetro.
	 * Retorna true se a autenticação foi realizada, false caso contrário.
	 * 
	 * @param req
	 * @param login
	 * @param senha
	 * @return
	 * @throws ArqException
	 */
	public boolean autenticaUsuarioMobile(HttpServletRequest req, String login, String senha) throws ArqException;

	/**
	 * Atualiza a senha do usuário mobile cujo id foi passado como parâmetro.
	 * @param idUsuario
	 * @param senhaMobile
	 * @throws ArqException
	 */
	public void atualizaSenhaMobile(int idUsuario, String senhaMobile) throws ArqException;

	/**
	 * Carrega as permissões do usuário passado como parâmetro.
	 * @param req
	 * @param usuario
	 * @throws ArqException
	 */
	public void carregaPermissoes(HttpServletRequest req, UsuarioGeral usuario) throws ArqException;

	/**
	 * Sincroniza os dados do usuário com o banco de dados. Utilizado
	 * quando a estratégia de autenticação não utiliza os usuários
	 * cadastrados no banco de dados, e sim uma base externa (como um LDAP, por exemplo).
	 * @return
	 */
	public boolean sincronizarComBancoDados();
	
	/**
	 * Carrega as informações básicas do usuário cujo login foi passado como parâmetro.
	 * @param req
	 * @param login
	 * @return
	 * @throws ArqException
	 */
	public InfoUsuarioDTO carregaInfoUsuario(HttpServletRequest req, String login) throws ArqException;

	/**
	 * Identifica se o usuário com id passado como parâmetro está atico no sistema
	 * cujo id foi passado como parâmetro.
	 * @param req
	 * @param sistema
	 * @param idUsuario
	 * @return
	 */
	public boolean usuarioAtivo(HttpServletRequest req, int sistema, int idUsuario);

	/**
	 * 
	 * @param login
	 * @return
	 * @throws NegocioException
	 */
	public String validarLogin(String login) throws NegocioException;
	
}
