/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/10/2009'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashSet;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.latosensu.dominio.TurmaEntradaLato;

/**
 * Processador responsável por implementar o cadastro de Turmas de Entrada Lato.
 * 
 * @author Igor
 *
 */
public class ProcessadorTurmaEntrada extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);

		if (SigaaListaComando.CADASTRAR_TURMA_ENTRADA_LATO.equals(mov.getCodMovimento())) {
			cadastrarTurmaEntradaLato((MovimentoCadastro) mov);		
		}
		
		return null;
	}

	/**
	 * Responsável por cadastrar uma Turma de Entrada Lato.
	 * 
	 * @param mov
	 * @throws DAOException
	 */
	private void cadastrarTurmaEntradaLato(MovimentoCadastro mov) throws DAOException {		
		GenericDAO dao = getGenericDAO(mov);
		TurmaEntradaLato turmaEntradaLato = mov.getObjMovimentado();
		
		//Evitar Erro de Lazy e adiciona uma turma de entrada na coleção de turmas do curso Lato
		//associado à turma de entrada.
		
		Collection<TurmaEntradaLato> turmasEntrada =  dao.findByExactField(TurmaEntradaLato.class, "cursoLato.id", turmaEntradaLato.getCursoLato().getId());
		turmaEntradaLato.getCursoLato().setTurmasEntrada(new HashSet<TurmaEntradaLato>());
		for(TurmaEntradaLato t : turmasEntrada ) {
			turmaEntradaLato.getCursoLato().getTurmasEntrada().add(t);
		}
		turmaEntradaLato.getCursoLato().getTurmasEntrada().add(turmaEntradaLato);
		
		
		if(turmaEntradaLato.getId()==0)
			dao.create(turmaEntradaLato);
		else{
			GenericDAO dao2 = getGenericDAO(mov);
			dao2.update(turmaEntradaLato);
			dao2.close();
		}
		dao.close();
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}