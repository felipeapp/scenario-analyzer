/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/12/2010
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroGrupoPesquisa;

/**
 * Processador responsável por cadastrar Grupos de Pesquisa.
 * 
 * @author Thalisson Muriel
 *
 */
public class ProcessadorGrupoPesquisa extends AbstractProcessador {


	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		checkRole(SigaaPapeis.GESTOR_PESQUISA, mov);

		GenericDAO dao = getGenericDAO(mov);
		
		MovimentoCadastro movCad = (MovimentoCadastro)mov;
		GrupoPesquisa grupoPesquisa = movCad.getObjMovimentado();
		ArrayList<MembroGrupoPesquisa> membrosGrupoPesquisa = (ArrayList<MembroGrupoPesquisa>) movCad.getColObjMovimentado();
		
		validate(mov);
		
		try {
			for(MembroGrupoPesquisa m : membrosGrupoPesquisa)
				if(!grupoPesquisa.getEquipesGrupoPesquisa().contains(m))
					grupoPesquisa.getEquipesGrupoPesquisa().add(m);
				else {
					grupoPesquisa.getEquipesGrupoPesquisa().remove(m);
					grupoPesquisa.getEquipesGrupoPesquisa().add(m);
				}
			
			for(MembroGrupoPesquisa m : grupoPesquisa.getEquipesGrupoPesquisa()){
				if(!m.isAtivo())
					dao.updateField(MembroGrupoPesquisa.class, m.getId(), "ativo", false);
			}
			dao.createOrUpdate(grupoPesquisa);
		}
		finally {
			dao.close();
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens lista = new ListaMensagens();
		MovimentoCadastro movCad = (MovimentoCadastro)mov;
		GrupoPesquisa grupo = movCad.getObjMovimentado();
		
		ValidatorUtil.validateRequired(grupo.getCodigo(), "Código", lista);
		ValidatorUtil.validateRequired(grupo.getNome(), "Nome", lista);
		ValidatorUtil.validateRequired(grupo.getCoordenador(), "Coordenador", lista);
		ValidatorUtil.validateRequiredId(grupo.getStatus(), "Status", lista);
		ValidatorUtil.validateRequired(grupo.getAreaConhecimentoCnpq(), "Área de Conhecimento", lista);
		
		checkValidation(lista);	
	}

}
