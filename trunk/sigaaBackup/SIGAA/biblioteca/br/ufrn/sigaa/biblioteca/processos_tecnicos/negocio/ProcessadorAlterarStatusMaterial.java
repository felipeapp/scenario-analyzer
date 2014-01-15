/*
 * ProcessadorAlterarStatusMaterial.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 17/04/2012
 * Autor: jadson
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.biblioteca.PoliticaEmprestimoDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;


/**
*
* <p>Processador que contém as regras de negócio para alterar um <code>Status do Material</code> do Sistema.</p>
*
*  <p> <strong> Obervação:</strong> <i>  Igual ao que ocorre no caso da remoção, quando um status de material é alterado para não premitir empréstimos
*  , as políticas de empréstimos que estavam ativas para ele precisam ser desativadas também. </i></p>
*
* @author jadson
* @see ProcessadorRemoveStatusTipoMaterialETipoEmprestimo
*/
public class ProcessadorAlterarStatusMaterial extends AbstractProcessador{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		
		PoliticaEmprestimoDao dao = null;
		
		validate(movimento);
		
		try{
			
			dao = getDAO(PoliticaEmprestimoDao.class, mov);
			
			
			StatusMaterialInformacional status = movimento.getObjMovimentado();
			dao.update(status);
			
			// DESATIVA AS POLÍTICAS PARA O STATUS QUE NÃO VAI MAIS PERMITIR EMPRÉSTIMOS //
			if(! status.isPermiteEmprestimo()) {
				
				List<PoliticaEmprestimo> politicasAtivas = dao.findPoliticasEmpretimoAtivasByStatusMaterial(status.getId());
				
				for (PoliticaEmprestimo politicaEmprestimo : politicasAtivas) {
					politicaEmprestimo.setAtivo(false);
					dao.updateNoFlush(politicaEmprestimo);
				}
			}
				
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
		
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// Sem validação, eles ocorrem no MBean nesse caso
	}

}
