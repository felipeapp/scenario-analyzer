/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 02/03/2010
 * Autor:     David Pereira
 */
package br.ufrn.sigaa.ensino.negocio.calculos;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/**
 * Chain of Responsibility para realizar os cálculos
 * dos discentes de graduação.
 * 
 * @author David Pereira
 *
 */
public class CalculosDiscenteChainNode<D extends DiscenteAdapter> {

	private CalculosDiscenteChainNode<D> next;
	
	public void executar(D d, Movimento mov, boolean preProcessamento) throws ArqException, NegocioException {
		processar(d, mov, preProcessamento);
		if (next != null)
			next.executar(d, mov, preProcessamento);
	}
	
	public void processar(D d, Movimento mov, boolean preProcessamento) throws ArqException, NegocioException {
		
	}

	public CalculosDiscenteChainNode<D> setNext(CalculosDiscenteChainNode<D> next) {
		this.next = next;
		return next;
	}

	protected <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return getDAO(dao, null);
	}
	
	protected <T extends GenericDAO> T getDAO(Class<T> dao, Movimento mov) throws DAOException {
		return DAOFactory.getInstance().getDAOMov(dao, mov);
	}

}
