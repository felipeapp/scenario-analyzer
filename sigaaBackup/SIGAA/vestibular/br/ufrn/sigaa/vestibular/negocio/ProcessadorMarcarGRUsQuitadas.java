/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 30/04/2012
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
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;

public class ProcessadorMarcarGRUsQuitadas extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastro movimento = ((MovimentoCadastro) mov);
		@SuppressWarnings("unchecked")
		Collection<InscricaoVestibular> inscricoes = (Collection<InscricaoVestibular>) movimento.getColObjMovimentado();
		GenericDAO dao = getGenericDAO(movimento);
		try {
			for (InscricaoVestibular inscricao : inscricoes) {
				dao.updateField(InscricaoVestibular.class, inscricao.getId(), "gruQuitada", true);
			}
		} finally {
			dao.close();
		}
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
