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
 * Implementação da estratégia de autenticação por LDAP.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class AutenticacaoLdap implements EstrategiaAutenticacao {

	@Override
	public void atualizaSenhaAtual(HttpServletRequest req, int idUsuario,
			String senhaAtual, String novaSenha) throws ArqException {
		
	}

	@Override
	public void atualizaSenhaMobile(int idUsuario, String senhaMobile)
			throws ArqException {
		
	}

	@Override
	public boolean autenticaUsuario(HttpServletRequest req, String login,
			String senha) throws ArqException {
		return false;
	}

	@Override
	public boolean autenticaUsuario(HttpServletRequest req, String login,
			String senha, boolean fazerHash) throws ArqException {
		return false;
	}

	@Override
	public boolean autenticaUsuarioMobile(HttpServletRequest req, String login,
			String senha) throws ArqException {
		return false;
	}

	@Override
	public void carregaPermissoes(HttpServletRequest req, UsuarioGeral usuario)
			throws ArqException {
		
	}

	@Override
	public Integer getIdUsuario(HttpServletRequest req, String login)
			throws ArqException {
		return null;
	}

	@Override
	public boolean localizaUsuarioMobile(HttpServletRequest req, String login)
			throws ArqException {
		return false;
	}

	@Override
	public boolean senhaExpirou(HttpServletRequest req, int idUsuario)
			throws ArqException {
		return false;
	}

	@Override
	public boolean sincronizarComBancoDados() {
		return false;
	}

	@Override
	public InfoUsuarioDTO carregaInfoUsuario(HttpServletRequest req,
			String login) throws ArqException {
		return null;
	}

	@Override
	public boolean usuarioAtivo(HttpServletRequest req, int sistema,
			int idUsuario) {
		return false;
	}

	@Override
	public String validarLogin(String login) throws NegocioException {
		return null;
	}

}
