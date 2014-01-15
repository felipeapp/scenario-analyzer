/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Edson Anibal de Macedo Reis Batista 09/02/2007 11:38:42
 * ambar@info.ufrn.br
 *
 */
package br.ufrn.sigaa.ava.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.EnqueteResposta;

/**
 * Processador para Computar os votos da enquete.
 * 
 * @author Edson Anibal De Macedo Reis Batista (ambar@info.ufrn.br)
 * 
 */
public class ProcessadorVotacaoEnquete extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		GenericDAO dao = getDAO(GenericSigaaDAO.class, mov);

		try {
			EnqueteResposta resposta = (EnqueteResposta) movimento
					.getObjMovimentado();
			dao.update(resposta);

		} catch (Exception e) {
		} finally {
			dao.close();
		}

		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
