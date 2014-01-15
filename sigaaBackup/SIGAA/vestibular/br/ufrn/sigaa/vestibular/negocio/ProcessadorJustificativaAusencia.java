/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/08/2009
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
import br.ufrn.sigaa.arq.dao.vestibular.FiscalDao;
import br.ufrn.sigaa.vestibular.dominio.JustificativaAusencia;

/**
 * Processador responsável pela persistência da JustificativaAusencia e arquivos
 * anexos.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class ProcessadorJustificativaAusencia extends AbstractProcessador {

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		MovimentoJustificativaFiscal movJustificativa = (MovimentoJustificativaFiscal) mov;
		JustificativaAusencia justificativa = movJustificativa.getJustificativaAusencia();
		FiscalDao dao = getDAO(FiscalDao.class, mov);
		try {
			if (movJustificativa.getArquivo() != null) {
				// apaga o arquivo anterior
				if (justificativa.getIdArquivo() != null) {
					EnvioArquivoHelper.removeArquivo(justificativa.getIdArquivo());
				}
				// Persistir arquivo com a comprovação da justificativa
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo,
						movJustificativa.getArquivo().getBytes(),
						movJustificativa.getArquivo().getContentType(),
						movJustificativa.getArquivo().getName());
				justificativa.setIdArquivo( idArquivo );
			}
		} catch (IOException e) {
			throw new ArqException(e);
		}
		justificativa.setRegistroEntrada(movJustificativa.getUsuarioLogado().getRegistroEntrada());
		dao.createOrUpdate(justificativa);
		return justificativa;
	}

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoJustificativaFiscal movJustificativa = (MovimentoJustificativaFiscal) mov;
		JustificativaAusencia justificativa = movJustificativa.getJustificativaAusencia();
		if (!justificativa.validate().isEmpty()){
			throw new NegocioException();
		}
	}

}
