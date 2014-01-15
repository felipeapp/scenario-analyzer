/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 11/08/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.medio.dominio.CurriculoMedio;

/**
 * Processador responsável pelo processos referentes ao currículo de ensino médio.
 * 
 * @author Rafael Gomes
 *
 */
public class ProcessadorCurriculoMedio extends ProcessadorCadastro{

	@Override
	public Object execute(Movimento movimento) throws NegocioException,	ArqException, RemoteException {
		validate(movimento);
		MovimentoCadastro mc = (MovimentoCadastro) movimento;
		
		/* Cadastrar / Alterar / Remover TurmaSerieAno */
		if (mc.getCodMovimento().equals(SigaaListaComando.CADASTRAR_CURRICULO_MEDIO))
			criar(mc);
		else if (mc.getCodMovimento().equals(SigaaListaComando.ALTERAR_CURRICULO_MEDIO))
			alterar(mc);
		else if (mc.getCodMovimento().equals(SigaaListaComando.REMOVER_CURRICULO_MEDIO))
			remover(mc);
		else if (movimento.getCodMovimento().equals(SigaaListaComando.INATIVAR_ATIVAR_CURRICULO_MEDIO)) 
			return inativarOuAtivar(mc);
		
		return mc;
	}
	

	/**
	 * Torna um currículo inativo.
	 */
	private Object inativarOuAtivar(Movimento movimento) throws DAOException {
		CurriculoMedio c = (CurriculoMedio) ((MovimentoCadastro) movimento).getObjMovimentado();
		
		getGenericDAO(movimento).updateField(CurriculoMedio.class, c.getId(), "ativo", c.isAtivo());
		
		return c;
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		super.validate(mov);
	}

}
