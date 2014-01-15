/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/11/2008'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;

import br.ufrn.academico.dominio.ProgramaEducacaoTutorial;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.comum.dao.ProgramaEducacaoTutorialDAO;

/**
 * Processador responsável pelo cadastro de 
 * Programas de Educação Tutorial - PET
 * 
 * @author wendell
 *
 */
public class ProcessadorPet extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		ProgramaEducacaoTutorial pet = (ProgramaEducacaoTutorial) mov;
		
		if (pet.isAtivo()){
			validate(mov);
		}
		
		if (isEmpty(pet.getIdCurso())) {
			pet.setIdCurso(null);
		}
		if (isEmpty(pet.getIdAreaConhecimentoCnpq())) {
			pet.setIdAreaConhecimentoCnpq(null);
		}
		
		// Persistir o PET utilizando o DAO compartilhado
		ProgramaEducacaoTutorialDAO.getInstance().createOrUpdate(pet);
		return pet;
	}
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkValidation(((ProgramaEducacaoTutorial) mov).validate());
	}

}
