/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/29 - 15:08:32
 */
package br.ufrn.sigaa.cv.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ava.dao.EnqueteDao;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroAva;
import br.ufrn.sigaa.cv.dominio.EnqueteComunidade;

/**
 * Processador para remover uma enquete e suas respostas
 * 
 * @author David Pereira
 *
 */
public class ProcessadorRemoverEnqueteComunidade extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastroAva cMov = (MovimentoCadastroAva) mov;
		EnqueteComunidade e = (EnqueteComunidade) cMov.getObjMovimentado();
		
		EnqueteDao dao = getDAO(EnqueteDao.class, mov);
		
		try {
			dao.removerEnqueteComVotosComunidadeVirtual(e.getId());
		} finally {
			dao.close();
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
