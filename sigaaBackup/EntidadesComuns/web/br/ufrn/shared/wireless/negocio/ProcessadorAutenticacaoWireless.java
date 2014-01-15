package br.ufrn.shared.wireless.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.comum.negocio.ComumListaComando;
import br.ufrn.comum.wireless.AutenticacaoUsersExt;
import br.ufrn.shared.wireless.dao.WirelessDAO;

/**
 * 
 * Processador responsavel pelo gerenciamento de Visitante Externo a rede Wireless 
 * 
 * @author agostinho
 *
 */

public class ProcessadorAutenticacaoWireless extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
	
		WirelessDAO genDAO = getDAO(WirelessDAO.class, mov);
		validate(mov);
		AutenticacaoUsersExt visitante = (AutenticacaoUsersExt) ((MovimentoCadastro) mov).getObjMovimentado();
		
		if (mov.getCodMovimento() == ComumListaComando.CADASTRAR_AUTENTICACAO_WIRELESS) {
			if(visitante.getId() == 0) {
				//Cadastro
				genDAO.create(visitante);
			}
			else {
				//Alteracao
				genDAO.update(visitante);
			}
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
	}
	
}
