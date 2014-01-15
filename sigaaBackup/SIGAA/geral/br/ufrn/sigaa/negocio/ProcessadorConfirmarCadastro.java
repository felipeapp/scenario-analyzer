/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 29/11/2006
 *
 */
package br.ufrn.sigaa.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processador para realizar a confirmação do cadastro de usuários
 *
 * @author David Ricardo
 *
 */
public class ProcessadorConfirmarCadastro extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		GenericDAO dao = getDAO(mov);
		Usuario usr = (Usuario) cMov.getObjMovimentado();

		try {
			usr = dao.findByPrimaryKey(usr.getId(), Usuario.class);

			if (mov.getCodMovimento().equals(SigaaListaComando.CONFIRMAR_CADASTRO)) {
				usr.setAutorizado(new Boolean(true));
				dao.update(usr);
			} else {
				dao.remove(usr);
			}
		} finally {
			dao.close();
		}

		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
