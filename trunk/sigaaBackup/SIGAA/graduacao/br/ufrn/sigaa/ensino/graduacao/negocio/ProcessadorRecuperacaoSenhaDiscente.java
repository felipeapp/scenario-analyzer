/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 03/06/2008 
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dao.UsuarioDAO;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processador para gerar uma nova senha para discentes. Utilizado
 * pelos administradores DAE.
 * 
 * @author David Pereira
 *
 */
public class ProcessadorRecuperacaoSenhaDiscente extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, mov);
		
		MovimentoCadastro cmov = (MovimentoCadastro) mov;
		
		Usuario usr = cmov.getObjMovimentado();
		
		UsuarioGeral usrg = getDAO(UsuarioDAO.class, mov).findByPrimaryKey(usr.getId(), UsuarioGeral.class);
		
		String novaSenha = UFRNUtils.geraSenhaAleatoria();
		
		// Atualiza usuário com nova senha aleatória
		UserAutenticacao.atualizaSenhaAtual(cmov.getRequest(), usrg.getId(), usrg.getSenha(), novaSenha);
		
		return novaSenha;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
