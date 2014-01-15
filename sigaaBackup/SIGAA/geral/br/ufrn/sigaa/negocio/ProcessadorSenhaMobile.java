/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '20/06/2008'
 *
 */
package br.ufrn.sigaa.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.usuarios.UserAutenticacao;

public class ProcessadorSenhaMobile extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoSenhaMobile pMov = (MovimentoSenhaMobile) mov; 
		
		UserAutenticacao.atualizaSenhaMobile(pMov.getUsuarioMobile().getUsuario().getId(), pMov.getUsuarioMobile().getSenhaMobile());
		
		return null;
	}
	
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}
}