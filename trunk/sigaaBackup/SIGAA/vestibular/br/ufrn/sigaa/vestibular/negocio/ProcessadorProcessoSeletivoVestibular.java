/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/04/2009
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import java.io.IOException;
import java.rmi.RemoteException;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.vestibular.ProcessoSeletivoVestibularDao;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/** Processador responsável pelo cadastro de processos seletivos vestibulares.
 * @author Édipo Elder F. Melo
 *
 */
public class ProcessadorProcessoSeletivoVestibular extends AbstractProcessador {

	/** Cadastra/altera o processo seletivo
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		MovimentoProcessoSeletivoVestibular movPSV = (MovimentoProcessoSeletivoVestibular) mov;
		ProcessoSeletivoVestibular psv = movPSV.getProcessoSeletivo();

		ProcessoSeletivoVestibularDao psvDao = getDAO(ProcessoSeletivoVestibularDao.class, mov);
		try {
			// Persistir arquivo com o edital
			if (movPSV.getEdital() != null ) {

				if (psv.getIdEdital() != null) {
					EnvioArquivoHelper.removeArquivo(psv.getIdEdital());
				}
				int idEdital = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idEdital,
						movPSV.getEdital().getBytes(),
						movPSV.getEdital().getContentType(),
						movPSV.getEdital().getName());
				psv.setIdEdital( idEdital );
			}

			//	Persistir arquivo com o manual do candidato
			if (movPSV.getManualCandidato() != null ) {

				if (psv.getIdManualCandidato() != null) {
					EnvioArquivoHelper.removeArquivo(psv.getIdManualCandidato());
				}
				int idManual = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idManual,
						movPSV.getManualCandidato().getBytes(),
						movPSV.getManualCandidato().getContentType(),
						movPSV.getManualCandidato().getName());
				psv.setIdManualCandidato( idManual );
			}
			psvDao.createOrUpdate(psv);
		} catch (IOException e) {
			throw new ArqException(e);
		} finally {
			psvDao.close();
		}
		return psv;
	}

	/** Checa os papéis: VESTIBULAR.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(SigaaPapeis.VESTIBULAR, mov);
		MovimentoProcessoSeletivoVestibular movPSV = (MovimentoProcessoSeletivoVestibular) mov;
		ListaMensagens lista = movPSV.getProcessoSeletivo().validate();
		if (lista != null && lista.isErrorPresent())
			throw new NegocioException(lista);
	}

}
