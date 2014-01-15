/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 09/02/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jms.Session;

import org.hibernate.exception.ConstraintViolationException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.RegiaoMatricula;
import br.ufrn.sigaa.dominio.RegiaoMatriculaCampus;

/**
 * Processador para cadastro de Regi�o de Matr�cula e suas associa��es
 * com campus.
 * @author Rafael Gomes
 *
 */
public class ProcessadorRegiaoMatricula extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastro movimento = (MovimentoCadastro) mov; 
		
		try {
			if (SigaaListaComando.CADASTRAR_REGIAO_CAMPUS.equals(mov.getCodMovimento()))
				cadastrar(movimento);
			else if (SigaaListaComando.REMOVER_REGIAO_CAMPUS.equals(mov.getCodMovimento()))
				remover(movimento);
		} catch(ConstraintViolationException e) {
			throw new NegocioException("N�o foi poss�vel remover esta regi�o de matr�cula pois ele est� sendo usada pelo sistema.");
		}
		
		return null;
	}
	
	private void cadastrar(MovimentoCadastro mov) throws DAOException {
		GenericDAO dao = getGenericDAO(mov);
		RegiaoMatricula regiaoMatricula = mov.getObjMovimentado();
		@SuppressWarnings("unchecked")
		List<CampusIes> listCampusNovos = (List<CampusIes>) mov.getObjAuxiliar();
		try {
			if (regiaoMatricula.getId() == 0) {
				dao.create(regiaoMatricula);
				
				// Cadastrar associa��es com campus
				for (CampusIes campus : listCampusNovos) {
					RegiaoMatriculaCampus rmc = new RegiaoMatriculaCampus();
					rmc.setRegiaoMatricula(regiaoMatricula);
					rmc.setCampusIes(campus);
					dao.create(rmc);
				}		
				
			} else {
				dao.update(regiaoMatricula);
				
				// Atualizar associa��es com campus de ensino
				Collection<RegiaoMatriculaCampus> regioesCampusAtuais = regiaoMatricula.getRegioesMatriculaCampus();			
				List<RegiaoMatriculaCampus> campusRemover = new ArrayList<RegiaoMatriculaCampus>();
				List<CampusIes> campusAdicionar = new ArrayList<CampusIes>();
				List<CampusIes> campusAtuais = new ArrayList<CampusIes>();
				
				//adicionar em uma lista os campus a serem removido na atualiza��o de uma regi�o.
				for ( RegiaoMatriculaCampus rmc : regioesCampusAtuais ) {
					if (!listCampusNovos.contains(rmc.getCampusIes()))
						campusRemover.add(rmc);
					campusAtuais.add(rmc.getCampusIes());
				}
				//adicionar em uma lista os campus a serem adicionados na atualiza��o de uma regi�o.
				for (CampusIes campusNovo : listCampusNovos) {
					if (!campusAtuais.contains(campusNovo))
						campusAdicionar.add(campusNovo);
				}
				// Remove as associa��es de regi�o com campus removidas na opera��o.
				for (RegiaoMatriculaCampus rmRemove : campusRemover) {
					dao.remove(rmRemove);
				}
				// Adiciona as associa��es de regi�o com campus inseridas na opera��o.
				for (CampusIes campusAdd : campusAdicionar) {
					RegiaoMatriculaCampus rmc = new RegiaoMatriculaCampus();
					rmc.setRegiaoMatricula(regiaoMatricula);
					rmc.setCampusIes(campusAdd);
					dao.create(rmc);
				}
			}
		} finally {
			dao.close();
		}
	}
	
	private void remover(MovimentoCadastro mov) throws DAOException {
		GenericDAO dao = getGenericDAO(mov);
		RegiaoMatricula regiaoMatricula = mov.getObjMovimentado();
		
		try {
			// Remover associa��es com campus
			Collection<RegiaoMatriculaCampus> regioesCampus = dao.findByExactField(RegiaoMatriculaCampus.class, "regiaoMatricula.id", regiaoMatricula.getId());
			for (RegiaoMatriculaCampus rmc : regioesCampus) {
				dao.remove(rmc);
			}
			dao.clearSession();
			dao.remove(regiaoMatricula);
		} finally {
			dao.close();
		}
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}