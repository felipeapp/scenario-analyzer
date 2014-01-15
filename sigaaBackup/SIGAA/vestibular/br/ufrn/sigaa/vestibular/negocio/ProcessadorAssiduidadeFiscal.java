/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/06/2008
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.dao.vestibular.FiscalDao;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;

/** Processa as assiduidades dos fiscais.
 * @author Édipo Elder F. Melo
 *
 */
public class ProcessadorAssiduidadeFiscal extends ProcessadorCadastro {

	/** Atualiza as freqüências dos fiscais.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException,
			ArqException, RemoteException {
		FiscalDao dao = new FiscalDao();
		try {
			List<Fiscal> listaFiscal = ((MovimentoPresencaReuniaoFiscal) movimento).getListaFiscal();
			for(Fiscal fiscal : listaFiscal){
				dao.updateNoFlush(fiscal);
			}
			return null;
		} finally {
			dao.close();
		}
	}
}
