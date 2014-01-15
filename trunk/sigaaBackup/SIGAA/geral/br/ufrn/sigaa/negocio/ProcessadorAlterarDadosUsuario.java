/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '31/08/2007'
 *
 */
package br.ufrn.sigaa.negocio;

import java.rmi.RemoteException;
import java.sql.Connection;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processador para alteração de e-mail e senha dos usuários do SIGAA
 * 
 * @author David Pereira
 *
 */
public class ProcessadorAlterarDadosUsuario extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		Usuario usr = (Usuario) cMov.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		
		Connection conSipac = null;
		Connection conComum = null;
		
		try {
			// Atualiza usuário
			dao.update(usr);
		
			conSipac = Database.getInstance().getSipacConnection();
			conComum = Database.getInstance().getComumConnection();
		
			// Sincroniza com sipac e base comum
			SincronizacaoUsuarioSipac sinc = new SincronizacaoUsuarioSipac();
			sinc.alteraDadosPessoais(usr, conSipac);
			sinc.alteraDadosPessoais(usr, conComum);
		} catch(Exception e) {
			throw new ArqException(e);
		} finally {
			dao.close();
			Database.getInstance().close(conSipac);
			Database.getInstance().close(conComum);
		}
		
		return usr;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
