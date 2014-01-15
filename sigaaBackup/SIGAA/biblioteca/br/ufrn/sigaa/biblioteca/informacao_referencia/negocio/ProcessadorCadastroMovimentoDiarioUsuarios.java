/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 12/11/2008
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.RegistroFrequenciaUsuariosBiblioteca;

/**
 * Processador responsável pelo cadastramento
 * da entidade RegistroFrequenciaUsuariosBiblioteca
 * 
 * @author Agostinho
 * @author Bráulio
 */
public class ProcessadorCadastroMovimentoDiarioUsuarios extends AbstractProcessador {
	
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		GenericDAO dao = null;
		
		try {
			MovimentoCadastro movimento = (MovimentoCadastro) mov;
			
			@SuppressWarnings("unchecked")
			List<RegistroFrequenciaUsuariosBiblioteca> reg = (List<RegistroFrequenciaUsuariosBiblioteca>) movimento.getColObjMovimentado();
			
			dao = getGenericDAO(mov);
	
			for (RegistroFrequenciaUsuariosBiblioteca it : reg) {
				dao.createOrUpdate(it);
			}
		} finally {
			if ( dao != null ) dao.close();
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
	}
	
}
