/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 19/09/2007
 *
 */	
package br.ufrn.sigaa.assistencia.negocio;

import java.rmi.RemoteException;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.sae.BolsaAuxilioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;
import br.ufrn.sigaa.assistencia.dominio.CalendarioBolsaAuxilio;

/**
 * Processador para cadastro/alteração/remoção de ConfiguracaoPeriodoResultado
 * 
 * @author agostinho campos
 *
 */
public class ProcessadorCadastroPeriodoResultado extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoPeriodoResultados pMov = (MovimentoPeriodoResultados) mov; 
		
		try {
			if (SigaaListaComando.CADASTRAR_PERIODO_RESULTADOS .equals(mov.getCodMovimento()))
				cadastrar(pMov);
			if (SigaaListaComando.REMOVER_PERIODO_RESULTADOS.equals(mov.getCodMovimento()))
				remover(pMov);
			if (SigaaListaComando.CADASTRAR_ANO_PERIODO_REFERENCIA.equals(mov.getCodMovimento()))
			    cadastrarAnoPeriodoReferencia(pMov);
		} catch(ConstraintViolationException e) {
			throw new NegocioException("Não foi possível remover.");
		}
		
		return null;
	}
	
	/**
	 * Cadastra o Ano/Período de referência do SAE
	 *  
	 * @param mov
	 * @throws DAOException
	 */
	private void cadastrarAnoPeriodoReferencia(MovimentoPeriodoResultados mov) throws DAOException {
	    BolsaAuxilioDao dao = getDAO(BolsaAuxilioDao.class, mov);
	    AnoPeriodoReferenciaSAE confPeriodoResultados = mov.getAnoPeriodoReferenciaSAE();
	    dao.createOrUpdate(confPeriodoResultados);
    }

	/**
	 * Cadastra o Período de Resultados do SAE
	 * 
	 * @param mov
	 * @throws DAOException
	 */
    private void cadastrar(MovimentoPeriodoResultados mov) throws DAOException {
		
		BolsaAuxilioDao dao = getDAO(BolsaAuxilioDao.class, mov);
		List<CalendarioBolsaAuxilio> confPeriodoResultados = mov.getConfPeriodoResultadosList();
		
		for (CalendarioBolsaAuxilio it : confPeriodoResultados) {
				dao.createOrUpdate(it);
		}
	}
	
    /**
     * Remove o Período de Resultados do SAE
     * @param mov
     * @throws DAOException
     */
	private void remover(MovimentoPeriodoResultados mov) throws DAOException {
		BolsaAuxilioDao dao = getDAO(BolsaAuxilioDao.class, mov);
		List<CalendarioBolsaAuxilio> confPeriodoResultados = mov.getConfPeriodoResultadosList();
		
		for (CalendarioBolsaAuxilio it : confPeriodoResultados) {
				dao.remove(it);
		}
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
