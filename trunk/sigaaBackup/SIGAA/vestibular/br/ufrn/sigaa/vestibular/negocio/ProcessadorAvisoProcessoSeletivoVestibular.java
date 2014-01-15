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
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.vestibular.AvisoProcessoSeletivoVestibularDao;
import br.ufrn.sigaa.vestibular.dominio.AvisoProcessoSeletivoVestibular;

/** Processador responsável pelo cadastro de avisos/notícias de Processos Seletivos Vestibulares.
 * @author Édipo Elder F. Melo
 *
 */
public class ProcessadorAvisoProcessoSeletivoVestibular extends
		ProcessadorCadastro {
	/** Cadastra/altera um aviso
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		MovimentoAvisoProcessoSeletivoVestibular movAviso = (MovimentoAvisoProcessoSeletivoVestibular) mov;
		AvisoProcessoSeletivoVestibular aviso = movAviso.getAviso();

		AvisoProcessoSeletivoVestibularDao dao = getDAO(AvisoProcessoSeletivoVestibularDao.class, mov);
		try {
			// Persistir arquivo com o Arquivo
			if (movAviso.getArquivo() != null) {

				if (aviso.getIdArquivo() != null) {
					EnvioArquivoHelper.removeArquivo(aviso.getIdArquivo());
				}
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, movAviso.getArquivo()
						.getBytes(), movAviso.getArquivo().getContentType(),
						movAviso.getArquivo().getName());
				aviso.setIdArquivo(idArquivo);
			}
		} catch (IOException e) {
			throw new ArqException(e);
		}
		dao.createOrUpdate(aviso);
		return aviso;
	}

	/** Checa os papéis: VESTIBULAR.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(SigaaPapeis.VESTIBULAR, mov);
	}

}
