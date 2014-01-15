package br.ufrn.sigaa.assistencia.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.assistencia.dao.FinalizarBolsaAuxilioDao;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioPeriodo;

public class ProcessadorFinalizacaoBolsista extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastro movCad = (MovimentoCadastro) mov;
		Collection<BolsaAuxilioPeriodo> bolsistas = (Collection<BolsaAuxilioPeriodo>) movCad.getObjAuxiliar();
		validate(mov);
		
		FinalizarBolsaAuxilioDao dao = getDAO(FinalizarBolsaAuxilioDao.class, movCad);
		try {
			Collection<BolsaAuxilio> bolsistaParaFinalizacao = new ArrayList<BolsaAuxilio>();
			for (BolsaAuxilioPeriodo bap : bolsistas) {
				if ( bap.getBolsaAuxilio().isSelecionado() ) {
					bolsistaParaFinalizacao.add(bap.getBolsaAuxilio());
				}
			}
			
			dao.finalizarBolsistas(bolsistaParaFinalizacao, movCad.getRegistroEntrada());
		} finally {
			dao.close();
		}
		
		return bolsistas;
	}
		
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}
	
}