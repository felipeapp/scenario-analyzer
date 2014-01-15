/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '11/09/2009'
 *
 */
package br.ufrn.sigaa.diploma.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.diploma.dao.RegistroDiplomaDao;

/**
 * Processador respons�vel por retornar o pr�ximo n�mero de registro, anotando-o
 * como utilizado.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
public class ProcessadorRequisicaoNumeroRegistro extends AbstractProcessador {

	/** 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		Collection<Integer> lista = new ArrayList<Integer>();
		validate(mov);
		RegistroDiplomaDao dao = null;
		try {
			dao = getDAO(RegistroDiplomaDao.class, mov);
			int quantidade = ((MovimentoCadastro)mov).getAcao();
			for (int i = 0; i < quantidade; i++) { 
				lista.add(dao.requisitaNumeroRegistro(true, ((br.ufrn.sigaa.dominio.Usuario)mov.getUsuarioLogado()).getNivelEnsino()));
			}
			return lista;
		} finally {
			if (dao != null) dao.close();
		}
	}

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		if (((MovimentoCadastro)mov).getAcao() < 0)
			throw new NegocioException("Informe um valor maior que zero.");
	}

}
