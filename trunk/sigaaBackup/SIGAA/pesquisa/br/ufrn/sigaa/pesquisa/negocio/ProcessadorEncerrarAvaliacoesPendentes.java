/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/06/2008
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
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoProjeto;

/**
 * Processador responsável pelo cadastro dos encerramentos das avaliações 
 * pendentes.
 * 
 * @author Leonardo
 */
public class ProcessadorEncerrarAvaliacoesPendentes extends AbstractProcessador {

	/**
	 * Responsável pela execução do processamento dos encerramentos das avaliações pendentes
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		GenericDAO dao = getDAO(mov);
		MovimentoEncerrarAvaliacoesPendentes movEncerrar = (MovimentoEncerrarAvaliacoesPendentes) mov;
		for(AvaliacaoProjeto av: movEncerrar.getAvaliacoesPendentes()){
			dao.updateField(AvaliacaoProjeto.class, av.getId(), "situacao", AvaliacaoProjeto.NAO_REALIZADA);
		}
		
		return movEncerrar.getAvaliacoesPendentes();
	}

	/**
	 * Responsável pela validação do processador Encerrar Avaliações Pendentes 
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, mov);
		MovimentoEncerrarAvaliacoesPendentes movEncerrar = (MovimentoEncerrarAvaliacoesPendentes) mov;
		for(AvaliacaoProjeto av: movEncerrar.getAvaliacoesPendentes()){
			if(av.getSituacao() != AvaliacaoProjeto.AGUARDANDO_AVALIACAO)
				throw new NegocioException("Todas as avaliações devem estar pendentes (aguardando avaliação) para serem encerradas.");
		}
	}

}
