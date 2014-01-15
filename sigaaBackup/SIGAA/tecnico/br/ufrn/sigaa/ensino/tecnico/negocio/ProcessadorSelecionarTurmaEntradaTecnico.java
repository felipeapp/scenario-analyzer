/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/12/2010
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import java.rmi.RemoteException;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Processador responsável por persistir as informações do cadastramento de discentes.
 * 
 * @author Leonardo Campos
 *
 */
public class ProcessadorSelecionarTurmaEntradaTecnico extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastro pMov = (MovimentoCadastro) mov;
		DiscenteTecnico d = pMov.getObjMovimentado();
		TurmaEntradaTecnico t = (TurmaEntradaTecnico) pMov.getObjAuxiliar();
		
		GenericDAO dao = null;
		
		try {
			if (d.getStatus() != StatusDiscente.CADASTRADO)
				throw new NegocioException ("Apenas discentes com status \"Cadastrado\" podem efetuar matrícula.");
			
			dao = getGenericDAO(pMov);
			
			dao.initialize(t);
			
			// Configura a turma de entrada
			dao.updateField(DiscenteTecnico.class, d.getId(), "turmaEntradaTecnico.id", t.getId());
			
			// Garante que a estrutura curricular do discente está correta
			if (d.getEstruturaCurricularTecnica() == null)
				dao.updateField(DiscenteTecnico.class, d.getId(), "estruturaCurricularTecnica.id", t.getEstruturaCurricularTecnica().getId());
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
