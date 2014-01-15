/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 29/07/2011
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Processador responsável por efetuar ajustes nas informações 
 * da capacidade de alunos e do local de uma turma.
 * 
 * @author Leonardo Campos
 *
 */
public class ProcessadorAjustarTurma extends AbstractProcessador {

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		GenericDAO dao = getGenericDAO(mov);
		
		try {
			MovimentoCadastro movimentoCadastro = (MovimentoCadastro) mov;
			
			Turma turma = movimentoCadastro.getObjMovimentado();
			Turma turmaBD = dao.findByPrimaryKey(turma.getId(), Turma.class, "capacidadeAluno", "local");
			
			movimentoCadastro.setObjAuxiliar(turmaBD);
			
			validate( movimentoCadastro );
			
			dao.updateField(Turma.class, turma.getId(), "capacidadeAluno", turma.getCapacidadeAluno());
			
			if(!turma.getLocal().equalsIgnoreCase(turmaBD.getLocal()))
				dao.updateField(Turma.class, turma.getId(), "local", turma.getLocal());
			
			return turma;
		} finally  {
			dao.close();
		}
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movimentoCadastro = (MovimentoCadastro) mov;
		Turma turma = movimentoCadastro.getObjMovimentado();
		Turma turmaBD = (Turma) movimentoCadastro.getObjAuxiliar();
		
		if(turma.getCapacidadeAluno() < turmaBD.getCapacidadeAluno())
			throw new NegocioException("Não é permitido diminuir a capacidade atual da turma através desta operação.");
	}

}
