/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/09/2009
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pesquisa.dominio.InstitutoCienciaTecnologia;
import br.ufrn.sigaa.pesquisa.dominio.MembroInstitutoCienciaTecnologia;

/**
 * Processador respons�vel pelo cadastro dos Institutos de Ci�ncia e Tecnologia 
 * 
 * @author Jean Guerethes
 */
public class ProcessadorInstitutoCienciaTecnologia extends AbstractProcessador {

	/**
	 * Respons�vel pela execu��o do processador Instituto Ci�ncia e Tecnologia
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		MovimentoCadastro movCad = (MovimentoCadastro) mov;
		InstitutoCienciaTecnologia instituto = movCad.getObjMovimentado();
		Set<MembroInstitutoCienciaTecnologia> lista = instituto.getEquipesInstitutoCienciaTecnologia();
		
		if(mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_INSTITUTO_CIENCIA_TECNOLOGIA) ){
			alterar(mov, movCad, instituto, lista);
		} else if(mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_INSTITUTO_CIENCIA_TECNOLOGIA) ){
			cadastrar(mov, movCad, instituto, lista);
		}
		
		return null;
	}

	/**
	 * Respons�vel pelo processamento de altera��o de instituto ci�ncia e tecnologia
	 * 
	 * @param mov
	 * @param movCad
	 * @param instituto
	 * @param lista
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private void alterar(Movimento mov, MovimentoCadastro movCad,InstitutoCienciaTecnologia instituto,Set<MembroInstitutoCienciaTecnologia> lista) throws DAOException {
		GenericDAO dao = getGenericDAO(mov);
		List<MembroInstitutoCienciaTecnologia> membros = (List<MembroInstitutoCienciaTecnologia>) movCad.getColObjMovimentado();
		if(!movCad.getColObjMovimentado().isEmpty()){
			for (MembroInstitutoCienciaTecnologia membroInstitutoCienciaTecnologia : membros) {
				//if(movCad.getColObjMovimentado().contains(membroInstitutoCienciaTecnologia))
					dao.remove(membroInstitutoCienciaTecnologia);
			}
			instituto.setEquipesInstitutoCienciaTecnologia(lista);
		}
		else{
			dao.update(instituto);
			for (MembroInstitutoCienciaTecnologia membroInstitutoCienciaTecnologia : lista) {
				dao.createOrUpdate(membroInstitutoCienciaTecnologia);
			}
		}
			
		dao.close();
	}
	
	/**
	 * Respons�vel pelo cadastro de um Instituto de Ci�ncia e Tecnologia
	 * 
	 * @param mov
	 * @param movCad
	 * @param instituto
	 * @param lista
	 * @throws DAOException
	 */
	private void cadastrar(Movimento mov, MovimentoCadastro movCad,InstitutoCienciaTecnologia instituto,Set<MembroInstitutoCienciaTecnologia> lista) throws DAOException {
		GenericDAO dao = getGenericDAO(mov);
		dao.createOrUpdate(instituto);
		if(movCad.getColObjMovimentado().isEmpty()){
			for (MembroInstitutoCienciaTecnologia membroInstitutoCienciaTecnologia : lista) {
				dao.createOrUpdate(membroInstitutoCienciaTecnologia);
			}
		}
		dao.close();
	}

	/**
	 * Respons�vel pela valida��o do processador Instituto Ci�ncia Tecnologia  
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movCad = (MovimentoCadastro) mov;
		InstitutoCienciaTecnologia instituto = movCad.getObjMovimentado();
		Set<MembroInstitutoCienciaTecnologia> lista = instituto.getEquipesInstitutoCienciaTecnologia();
		for (MembroInstitutoCienciaTecnologia membroInstitutoCienciaTecnologia : lista) {
			if(membroInstitutoCienciaTecnologia.getPessoa() == null)
				throw new NegocioException("Falha ao adicionar membro a equipe.");
		}
	}

}
