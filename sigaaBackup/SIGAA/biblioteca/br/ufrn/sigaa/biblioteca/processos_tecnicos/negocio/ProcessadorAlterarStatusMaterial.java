/*
 * ProcessadorAlterarStatusMaterial.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
* <p>Processador que cont�m as regras de neg�cio para alterar um <code>Status do Material</code> do Sistema.</p>
*
*  <p> <strong> Oberva��o:</strong> <i>  Igual ao que ocorre no caso da remo��o, quando um status de material � alterado para n�o premitir empr�stimos
*  , as pol�ticas de empr�stimos que estavam ativas para ele precisam ser desativadas tamb�m. </i></p>
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
			
			// DESATIVA AS POL�TICAS PARA O STATUS QUE N�O VAI MAIS PERMITIR EMPR�STIMOS //
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
		// Sem valida��o, eles ocorrem no MBean nesse caso
	}

}
