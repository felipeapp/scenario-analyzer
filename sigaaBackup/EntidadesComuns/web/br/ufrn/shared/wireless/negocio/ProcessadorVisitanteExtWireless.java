package br.ufrn.shared.wireless.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.comum.negocio.ComumListaComando;
import br.ufrn.comum.wireless.AutorizacaoUsersExt;
import br.ufrn.shared.wireless.dao.WirelessDAO;

/**
 * 
 * Processador responsável pelo gerenciamento de Visitante Externo a rede Wireless 
 * 
 * @author agostinho
 *
 */

public class ProcessadorVisitanteExtWireless extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
	
		WirelessDAO genDAO = getDAO(WirelessDAO.class, mov);
		validate(mov);
		AutorizacaoUsersExt visitante = (AutorizacaoUsersExt) ((MovimentoCadastro) mov).getObjMovimentado();
		
		if (mov.getCodMovimento() == ComumListaComando.CADASTRAR_VISITANTE_EXT_WIRELESS) {
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

	/**
	 * Valida os dados antes de executar o processador.
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

		AutorizacaoUsersExt autorizacaoUserExt = (AutorizacaoUsersExt) ((MovimentoCadastro) mov).getObjMovimentado();

		if (autorizacaoUserExt.getCpf() == 0 && autorizacaoUserExt.getPassaporte() == null)
			throw new NegocioException("É necessário informar CPF OU Passaporte.");
		
		if (autorizacaoUserExt.getPassaporte() == null && autorizacaoUserExt.getCpf() == null)
			throw new NegocioException("É necessário informar CPF OU Passaporte.");
	}
	
}
