package br.ufrn.sigaa.ead.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.usuarios.UsuarioLDAP;
import br.ufrn.comum.dao.UsuarioGeralDAO;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Processaodr utilizado para alterar dados de email e senha de um aluno ead.
 * 
 * @author Bernardo
 *
 */
public class ProcessadorDadosUsuarioAluno extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		MovimentoCadastro mdc = (MovimentoCadastro) mov;
		
		UsuarioGeral user = mdc.getUsuario();

		UsuarioGeralDAO dao = new UsuarioGeralDAO(Database.getInstance().getSipacDs(), Database.getInstance().getSigaaDs(), Database.getInstance().getComumDs());
		GenericDAO genericDao = new GenericDAOImpl(Sistema.COMUM);
		
		try {
			UsuarioGeral usuarioGeral = genericDao.findByPrimaryKey(user.getId(), UsuarioGeral.class);
			dao.atualizarDadosPessoaisTodosOsBancos(usuarioGeral.getId(),usuarioGeral.getPessoa().getCpf_cnpj(), user.getEmail(), user.getRamal());
			
			genericDao.detach(usuarioGeral);
			
			if ( user.getSenha() != null ) {
				UserAutenticacao.atualizaSenhaAtual(mdc.getRequest(), user.getId(), usuarioGeral.getSenha(), user.getSenha());
				UsuarioLDAP.modifyEntry(user.getLogin(), user.getSenha());
			}
		} finally {
			genericDao.close();
		}

		return user;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
