/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 21/07/2008
 *
 */	
package br.ufrn.sigaa.assistencia.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioAtleta;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioCreche;

/**
 * Processador para as operações de bolsa auxílio.
 * 
 * @author Gleydson
 *
 */
public class ProcessadorBolsaAuxilio extends ProcessadorCadastro {

	public Object execute(Movimento movimento) throws NegocioException,
			ArqException, RemoteException {
		
		MovimentoCadastro movCad = (MovimentoCadastro) movimento;
		
		BolsaAuxilio bolsa = (BolsaAuxilio) movCad.getObjMovimentado();
		
		validate(movCad);
		
		if ( movCad.getCodMovimento().equals(SigaaListaComando.CADASTRAR_BOLSA_AUXILIO)) {
			criar(movCad);
			criarAlterarAuxiliar(movCad);
			criarAlteracaoRenovacaoBolsa(movCad);
		} 
		else if ( movCad.getCodMovimento().equals(SigaaListaComando.ALTERAR_BOLSA_AUXILIO) ) {
			alterar(movCad);
			criarAlterarAuxiliar(movCad);
		}
		return bolsa;
	}

	private void criarAlteracaoRenovacaoBolsa(MovimentoCadastro movCad) throws DAOException {
		BolsaAuxilio bolsa = (BolsaAuxilio) movCad.getObjMovimentado();
		if ( bolsa.getBolsaAuxilioOriginal() != null ) {
			GenericDAO dao = getGenericDAO(movCad);
			try {
				dao.updateField(BolsaAuxilio.class, 
					bolsa.getBolsaAuxilioOriginal().getId(), "solicitadaRenovacao", Boolean.TRUE);
			} finally {
				dao.close();
			}
		}
	}

	/**
	 * Cria ou altera bolsa-auxílio auxiliar.
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	protected Object criarAlterarAuxiliar(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		if ( mov.getObjAuxiliar() != null ) {
			GenericDAO dao = getGenericDAO(mov);
			try {
				dao.createOrUpdate((PersistDB) setBolsaAuxilioAuxiliar(mov));
			} finally {
				dao.close();
			}
		}
		return mov.getObjAuxiliar();
	}

	/** 
	 * "Seta" bolsa-auxílio auxiliar.
	 * @param mov
	 * @return
	 */
	private Object setBolsaAuxilioAuxiliar(MovimentoCadastro mov) {
		
		if ( mov.getObjAuxiliar() instanceof BolsaAuxilioAtleta ) {
			BolsaAuxilioAtleta bolsaAtleta = (BolsaAuxilioAtleta) mov.getObjAuxiliar();
			BolsaAuxilio ba = mov.getObjMovimentado();
			bolsaAtleta.setBolsaAuxilio(ba);
			return bolsaAtleta;
		}
		if ( mov.getObjAuxiliar() instanceof BolsaAuxilioCreche ) {
			BolsaAuxilioCreche bolsaCreche = (BolsaAuxilioCreche) mov.getObjAuxiliar();
			BolsaAuxilio ba = mov.getObjMovimentado();
			bolsaCreche.setBolsaAuxilio(ba);
			return bolsaCreche;
		}
		
		return null;
	}
	
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}	
}
