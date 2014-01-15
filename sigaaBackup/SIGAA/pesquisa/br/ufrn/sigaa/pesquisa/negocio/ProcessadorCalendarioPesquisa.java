/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/12/2007
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.pesquisa.dominio.CalendarioPesquisa;

/**
 * Executa atualização do calendário de pesquisa.
 * Motivo: tratamento do calendário vigente.
 * 
 * @author leonardo
 *
 */
public class ProcessadorCalendarioPesquisa extends ProcessadorCadastro {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastro cMov = (MovimentoCadastro) movimento;
		GenericDAO dao = getGenericDAO(movimento);

		CalendarioPesquisa cal = (CalendarioPesquisa) cMov.getObjMovimentado();
		if (cal.isVigente()) {
			// tratando vigência dos calendários
			CalendarioPesquisa atualVigente =  dao.findByExactField(CalendarioPesquisa.class, "vigente", Boolean.TRUE).iterator().next();
			if (atualVigente.getId() != cal.getId())
				dao.updateField(CalendarioPesquisa.class, atualVigente.getId(), "vigente", false);
			else
				dao.detach(atualVigente);
		}
		
		// update
		dao.update(cal);
		return cal;
	}
	
}
