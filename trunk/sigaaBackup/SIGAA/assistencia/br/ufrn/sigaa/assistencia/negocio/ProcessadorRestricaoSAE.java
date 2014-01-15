package br.ufrn.sigaa.assistencia.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.assistencia.dao.RestricaoSolicitacaoBolsaAuxilioDao;
import br.ufrn.sigaa.assistencia.dominio.RestricaoSolicitacaoBolsaAuxilio;

public class ProcessadorRestricaoSAE extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastro movCad = (MovimentoCadastro) mov;
		RestricaoSolicitacaoBolsaAuxilio restricao = movCad.getObjMovimentado();
		validate(mov);
		
		RestricaoSolicitacaoBolsaAuxilioDao dao = getDAO(RestricaoSolicitacaoBolsaAuxilioDao.class, movCad);
		try {

			Collection<RestricaoSolicitacaoBolsaAuxilio> restricaoBD = 
					dao.findAllRestricoes(restricao.getTipoBolsaAuxilio());
			
			if ( restricaoBD != null ) {
				for (RestricaoSolicitacaoBolsaAuxilio restri : restricaoBD ){
					if ( !restricao.getRestricao().contains(restri))  {
						restri.setAtivo(Boolean.FALSE);
						dao.update(restri);
					}
					
					dao.detach(restri);
				}
			}
			
			dao.getHibernateTemplate().saveOrUpdateAll(restricao.getRestricao());
			
		} finally {
			dao.close();
		}
		
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}

}
