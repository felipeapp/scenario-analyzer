/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/04/2009
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.ensino.tecnico.dao.ProcessoSeletivoTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.ProcessoSeletivoTecnico;

/** Processador responsável pelo cadastro de processos seletivos vestibulares.
 * @author Édipo Elder F. Melo
 *
 */
public class ProcessadorProcessoSeletivoTecnico extends AbstractProcessador {

	/** Cadastra/altera o processo seletivo
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		MovimentoProcessoSeletivoTecnico movPSV = (MovimentoProcessoSeletivoTecnico) mov;
		ProcessoSeletivoTecnico psv = movPSV.getProcessoSeletivo();

		ProcessoSeletivoTecnicoDao psvDao = getDAO(ProcessoSeletivoTecnicoDao.class, mov);
		try {
			psvDao.createOrUpdate(psv);
		} finally {
			psvDao.close();
		}
		return psv;
	}

	/** Checa os papéis: VESTIBULAR.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO DEFINIR PAPEIS
		//checkRole(SigaaPapeis.VESTIBULAR, mov);
		MovimentoProcessoSeletivoTecnico movPSV = (MovimentoProcessoSeletivoTecnico) mov;
		ListaMensagens lista = movPSV.getProcessoSeletivo().validate();
		if (lista != null && lista.isErrorPresent())
			throw new NegocioException(lista);
	}

}
