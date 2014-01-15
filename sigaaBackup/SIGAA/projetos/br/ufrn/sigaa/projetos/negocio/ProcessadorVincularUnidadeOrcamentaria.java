/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/08/2009
 *
 */
package br.ufrn.sigaa.projetos.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Processador utilizado na vinculação de uma Unidade Orçamentária
 * a um projeto.
 * 
 * @author Ilueny Santos
 *
 */
public class ProcessadorVincularUnidadeOrcamentaria extends AbstractProcessador {


    public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
    	validate(mov);		
    	GenericDAO dao = getGenericDAO(mov);
    	MovimentoCadastro movC = (MovimentoCadastro) mov;
    	Projeto projeto = movC.getObjMovimentado();
    	Integer idUnidadeOrcamentaria = new Integer(projeto.getUnidadeOrcamentaria().getId()); 
    	try {
    		validate(movC);
    		dao.updateField(Projeto.class, projeto.getId(), "unidadeOrcamentaria.id", idUnidadeOrcamentaria);
    	} finally {
    		dao.close();
    	}
    	return mov;
    }

    public void validate(Movimento mov) throws NegocioException, ArqException {
    	checkRole(new int[]{SigaaPapeis.MEMBRO_COMITE_INTEGRADO}, mov);
    	
    	MovimentoCadastro movC = (MovimentoCadastro) mov;
    	Projeto projeto = movC.getObjMovimentado();
    	ListaMensagens lista = new ListaMensagens();
    	ProjetoBaseValidator.validaVincularProjetoUnidadeOrcamentaria(projeto, lista);
    	checkValidation(lista);
    }

}
