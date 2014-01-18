/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 20/12/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.graduacao.dominio.MudancaCurricular;
import br.ufrn.sigaa.pessoa.dominio.ObservacaoDiscente;

/**
 * Processador para efetuar a mudança coletiva de matrizes de alunos ativos.
 * Opcionalmente pode-se filtrar apenas os alunos de um determinado
 * curso, matriz, currículo, ano-período de ingresso.
 * 
 * @author Rafael Gomes
 *
 */
public class ProcessadorMudancaColetivaMatrizCurricular extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
	
		validate(mov);
		MovimentoMudancaCurricular movCadastro = (MovimentoMudancaCurricular) mov;
		Collection<MudancaCurricular> mudancasCurriculares = (Collection<MudancaCurricular>) movCadastro.getColObjMovimentado();
		ObservacaoDiscente obsDiscente = (ObservacaoDiscente) movCadastro.getObjMovimentado();
		
		if (mudancasCurriculares == null || mudancasCurriculares.isEmpty())
			throw new NegocioException("Não há alunos associados a mudança coletiva de matriz curricular.");

		for (MudancaCurricular mudanca : mudancasCurriculares) {
			MovimentoMudancaCurricular movMudanca = new MovimentoMudancaCurricular();
			movMudanca.setCodMovimento(SigaaListaComando.MUDANCA_CURRICULAR);
			movMudanca.setObjMovimentado(mudanca);
			if( obsDiscente != null )
				movMudanca.setObjAuxiliar(obsDiscente);
			movMudanca.setRequest(movCadastro.getRequest());
			movMudanca.setResponse(movCadastro.getResponse());
			movMudanca.setUsuarioLogado(movCadastro.getUsuarioLogado());
			ProcessadorMudancaCurricular processador = new ProcessadorMudancaCurricular();
			processador.execute(movMudanca);
		}
		
		return mov;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}