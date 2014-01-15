/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/11/2008
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.dao.vestibular.FiscalDao;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;
import br.ufrn.sigaa.vestibular.dominio.ObjectSeletor;

/** Processa a aloca��o de fiscais � locais de aplica��o de prova.
 * @author �dipo Elder F. Melo
 *
 */
public class ProcessadorAlocacaoFiscalLocalProva extends ProcessadorCadastro {

	/** Executa a aloca��o de fiscais ao local de aplica��o de prova.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException,
			ArqException, RemoteException {
		FiscalDao dao = new FiscalDao();
		MovimentoTransferenciaFiscal movimentoTransferenciaFiscal = (MovimentoTransferenciaFiscal) movimento;
		List<ObjectSeletor<Fiscal>> listaFiscal = movimentoTransferenciaFiscal
				.getListaFiscalTransferencia();
		List<Fiscal> fiscaisTransferidos = new ArrayList<Fiscal>();
		for (ObjectSeletor<Fiscal> fiscalTransferir : listaFiscal) {
			if (fiscalTransferir.isSelecionado()) {
				Fiscal fiscal = fiscalTransferir.getObjeto();
				fiscal.setLocalAplicacaoProva(movimentoTransferenciaFiscal
						.getLocalAplicacaoDestino());
				dao.updateNoFlush(fiscal);
				fiscaisTransferidos.add(fiscal);
			}
		}
		dao.close();
		return fiscaisTransferidos;
	}
}
