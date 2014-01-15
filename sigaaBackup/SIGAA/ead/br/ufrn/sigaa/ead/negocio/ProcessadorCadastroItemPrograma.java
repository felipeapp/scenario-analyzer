/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/19 - 19:28:02
 */
package br.ufrn.sigaa.ead.negocio;

import java.rmi.RemoteException;

import org.hibernate.exception.ConstraintViolationException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ead.dominio.ItemPrograma;

/**
 * Processador para cadastro de Pólos e suas associações
 * com cursos.
 * 
 * @author David Pereira
 *
 */
public class ProcessadorCadastroItemPrograma extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoItemPrograma pMov = (MovimentoItemPrograma) mov; 
		
		try {

			cadastrar(pMov);
			
		} catch(ConstraintViolationException e) {
			throw new NegocioException("Não foi possível remover este pólo pois ele está sendo usado pelo sistema.");
		}
		
		return null;
	}
	
	private void cadastrar(MovimentoItemPrograma mov) {
		
		ItemPrograma itemPrograma = mov.getObjMovimentado();
		
		try {
			
			GenericDAO dao = getGenericDAO(mov);
			dao.create(itemPrograma);
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}