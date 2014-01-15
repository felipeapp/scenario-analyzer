/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 12/11/2008
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.dao.vestibular.FiscalDao;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;
import br.ufrn.sigaa.vestibular.dominio.InscricaoFiscal;
import br.ufrn.sigaa.vestibular.dominio.ObjectSeletor;

/** Processador responsável pela seleção manual de fiscais.
 * @author Édipo Elder F. Melo
 *
 */
public class ProcessadorSelecaoManualFiscal extends ProcessadorCadastro {
	/** Processa a seleção manual dos fiscais.
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException,
			ArqException, RemoteException {
		FiscalDao dao = new FiscalDao();
		MovimentoSelecaoManualFiscal movimentoSelecaoManualFiscal = (MovimentoSelecaoManualFiscal) movimento;
		Collection<ObjectSeletor<InscricaoFiscal>> listaFiscal = movimentoSelecaoManualFiscal
				.getInscricoesFiscais();
		Collection<Fiscal> fiscaisTransferidos = new ArrayList<Fiscal>();
		for (ObjectSeletor<InscricaoFiscal> fiscalSeletor : listaFiscal) {
			if (fiscalSeletor.isSelecionado()) {
				Fiscal fiscal = new Fiscal(fiscalSeletor.getObjeto());
				fiscal.setReserva(true);
				fiscal.setPresenteAplicacao(true);
				fiscal.setPresenteReuniao(true);
				fiscal.addObservacao("Fiscal selecionado manualmente");
				dao.createNoFlush(fiscal);
				fiscaisTransferidos.add(fiscal);
			}
		}
		dao.close();
		return fiscaisTransferidos;
	}
}
