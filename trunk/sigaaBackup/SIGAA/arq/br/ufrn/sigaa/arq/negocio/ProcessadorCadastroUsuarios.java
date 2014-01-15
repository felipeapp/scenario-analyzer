
package br.ufrn.sigaa.arq.negocio;

import java.rmi.RemoteException;

import javax.sql.DataSource;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.usuarios.UsuarioLDAP;
import br.ufrn.arq.usuarios.UsuarioMov;
import br.ufrn.comum.dao.UsuarioGeralDAO;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.TipoUsuario;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Processador responsável pelo cadastro de usuários
 * no SIPAC, SIGAA, SIGRH e base comum
 *
 * @author David Pereira
 *
 */
public class ProcessadorCadastroUsuarios extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		DataSource sipacDs = null;
		DataSource sigaaDs = null;
		DataSource comumDs = null;

		try {
			validate(mov);

			sipacDs = Database.getInstance().getDataSource(Sistema.getSistemaAdministrativoAtivo());
			sigaaDs = Database.getInstance().getSigaaDs();
			comumDs = Database.getInstance().getComumDs();

			UsuarioGeralDAO dao = new UsuarioGeralDAO(sipacDs, sigaaDs, comumDs);

			UsuarioMov uMov = (UsuarioMov) mov;
			UsuarioGeral usuario = uMov.getUsuario();

			// Altera o login para se adequar as regras dos sistemas
			padroniza(usuario);

			// Escolher a operação a ser realizada
			if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_USUARIO)) {
				if (dao.existeLogin(usuario.getLogin()))
					throw new NegocioException("Já existe um usuário com o login especificado.");
				cadastrarUsuario(dao, usuario, uMov);
			} 

			return usuario;

		} catch(NegocioException e) {
			throw e;
		} catch(Exception e) {
			throw new ArqException(e.getMessage(), e);
		}

	}

	private void cadastrarUsuario(UsuarioGeralDAO dao, UsuarioGeral usuario, UsuarioMov mov) throws ArqException, NegocioException {
		dao.cadastrarUsuario(usuario, mov.getUsuarioLogado(), usuario.getPessoa().getId());

		UserAutenticacao.atualizaSenhaAtual(mov.getRequest(), usuario.getId(), null, usuario.getSenha());
		
		// Consultores externos do CNPq não precisam de entrada no LDAP
		if(usuario.getTipo().getId() != TipoUsuario.TIPO_CONSULTOR || usuario.getTipo().getId() != TipoUsuario.TIPO_COOPERACAO)
			UsuarioLDAP.addEntry(usuario.getLogin(), usuario.getSenha());
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

	private void padroniza(UsuarioGeral usuario) throws NegocioException {
		usuario.setLogin(UserAutenticacao.validarLogin(usuario.getLogin()));
		usuario.setLogin(usuario.getLogin().trim());

		if (usuario.getEmail() != null)
			usuario.setEmail(usuario.getEmail().trim());

		if (usuario.getRamal() != null)
			usuario.setRamal(usuario.getRamal().trim());

	}

}
