/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 18/01/2010
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.vestibular.dominio.IsentoTaxaVestibular;

/**
 * Processador responsávelo pelo cadastro de CPFs de candidatos
 * isentos/parcialmente isentos da taxa de inscrição do Vestibular.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class ProcessadorCadastroIsento extends AbstractProcessador {

	/** Executa o cadastro dos CPFs.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		GenericDAO dao = getGenericDAO(mov);
		@SuppressWarnings("unchecked")
		Collection<IsentoTaxaVestibular> isentos = (Collection<IsentoTaxaVestibular>) movimento.getColObjMovimentado();
		for (IsentoTaxaVestibular isento : isentos) {
			dao.createNoFlush(isento);
		}
		dao.close();
		return null;
	}

	/** Valida os dados.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		@SuppressWarnings("unchecked")
		Collection<IsentoTaxaVestibular> isentos = (Collection<IsentoTaxaVestibular>) movimento.getColObjMovimentado();
		for (IsentoTaxaVestibular isento : isentos) {
			ListaMensagens lista = isento.validate();
			if (lista != null && lista.isErrorPresent())
				throw new NegocioException(lista);
		}
	}

}
