/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/29 - 15:08:32
 */
package br.ufrn.sigaa.ava.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ava.dao.EnqueteDao;
import br.ufrn.sigaa.ava.dominio.Enquete;

/**
 * Processador para remover uma enquete e suas respostas
 * 
 * @author David Pereira
 *
 */
public class ProcessadorRemoverEnquete extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastroAva cMov = (MovimentoCadastroAva) mov;
		Enquete e = (Enquete) cMov.getObjMovimentado();
		EnqueteDao dao = getDAO(EnqueteDao.class, mov);
		try {
			dao.removerEnqueteComVotos(e.getId());
			MaterialTurmaHelper.reOrdenarMateriais(e.getAula());
		} finally {
			dao.close();
		}
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
