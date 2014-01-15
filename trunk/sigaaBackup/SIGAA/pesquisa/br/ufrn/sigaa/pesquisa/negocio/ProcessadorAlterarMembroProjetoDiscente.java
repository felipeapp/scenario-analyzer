/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/03/2007
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pessoa.dominio.ContaBancaria;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador para alterar os dados bancários do discente membro de um projeto de pesquisa
 * e alterar as datas de indicação/substituição do discente como membro do projeto.
 * 
 * @author Leonardo
 *
 */
public class ProcessadorAlterarMembroProjetoDiscente extends
		AbstractProcessador {


	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		checkRole(SigaaPapeis.GESTOR_PESQUISA, mov);
		
		validate(mov);
		
		GenericDAO dao = getDAO(mov);
		
		MovimentoAlterarProjetoDiscente movAlterar = (MovimentoAlterarProjetoDiscente) mov;
		
		MembroProjetoDiscente membroDiscente = movAlterar.getMembroDiscente();
		MembroProjetoDiscente membroDiscenteBD = dao.findByPrimaryKey(membroDiscente.getId(), MembroProjetoDiscente.class);
		
		membroDiscenteBD.setDataInicio( membroDiscente.getDataInicio() );
		membroDiscenteBD.setDataFim( membroDiscente.getDataFim() );
		
		Pessoa pessoa = membroDiscente.getDiscente().getPessoa();
		Pessoa pessoaBD = dao.findByPrimaryKey(pessoa.getId(), Pessoa.class);
		
		// Se a pessoa não possui conta bancária, instância uma nova
		if(pessoaBD.getContaBancaria() == null)
			pessoaBD.setContaBancaria( new ContaBancaria() );
		
		pessoaBD.getContaBancaria().setBanco( pessoa.getContaBancaria().getBanco() );
		pessoaBD.getContaBancaria().setNumero( pessoa.getContaBancaria().getNumero() );
		pessoaBD.getContaBancaria().setAgencia( pessoa.getContaBancaria().getAgencia() );
		pessoaBD.getContaBancaria().setOperacao( pessoa.getContaBancaria().getOperacao() );
		
		
		try {
			if(pessoaBD.getContaBancaria().getId() > 0)
				dao.update(pessoaBD.getContaBancaria());
			else
				dao.create(pessoaBD.getContaBancaria());
			dao.update(membroDiscenteBD);
		} finally {
			dao.close();
		}
		
		return movAlterar;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
