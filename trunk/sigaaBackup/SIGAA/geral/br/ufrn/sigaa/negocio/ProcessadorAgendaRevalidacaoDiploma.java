/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '09/01/2009'
 *
 */
package br.ufrn.sigaa.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.graduacao.dominio.AgendaRevalidacaoDiploma;

/**
 * Classe respons�vel pelo processamento do movimento da agenda de revalida��o de diplomas
 * @author M�rio Rizzi
 */
public class ProcessadorAgendaRevalidacaoDiploma extends ProcessadorCadastro {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

		validate(movimento);
		if(movimento.getCodMovimento() == SigaaListaComando.CADASTRA_AGENDA_REVALIDACAO_DIPLOMA)
			cadastrar(movimento);
		return movimento;
		
	}
	
	public void cadastrar(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
		MovimentoAgendaRevalidacaoDiploma mov = (MovimentoAgendaRevalidacaoDiploma) movimento;
		AgendaRevalidacaoDiploma agendaRevalidacaoDiploma = mov.getAgendaRevalidacaoDiploma();
		GenericDAO dao = getDAO(mov);
		try {
			dao.create(agendaRevalidacaoDiploma);
		}catch (Exception e) {
			e.printStackTrace();
			throw new NegocioException("Aconteceu algum erro inesperado. Por favor tente novamente!");
		}
		
	}
	
	@Override
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		
	}


}
