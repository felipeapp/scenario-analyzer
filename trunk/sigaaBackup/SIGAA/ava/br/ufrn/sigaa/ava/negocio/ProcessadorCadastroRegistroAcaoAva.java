package br.ufrn.sigaa.ava.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ava.dominio.RegistroAcaoAva;

/**
 * Processador para cadastro de registro ação ava
 * 
 * @author Diego Jácome
 *
 */
public class ProcessadorCadastroRegistroAcaoAva extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,	RemoteException {
		GenericDAO dao = null;
		MovimentoCadastro cMov = (MovimentoCadastro) mov;

		try {
			RegistroAcaoAva reg = cMov.getObjMovimentado();			
			dao = getGenericDAO(cMov);
			dao.create(reg);
		}finally {
			if (dao!=null)
				dao.close();
		}
		
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}

}
