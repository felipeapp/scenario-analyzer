/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 13/12/2010
 * 
 */
package br.ufrn.sigaa.projetos.negocio;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.pesquisa.negocio.DistribuicaoManualProjetoApoioNovosPesquisadores;
import br.ufrn.sigaa.projetos.dominio.DistribuicaoAvaliacao;

/**
 * F�brica respons�vel por criar inst�ncias adequadas de
 * estrat�gias de distribui��o de projetos.
 * 
 * @author Ilueny Santos
 * @author Leonardo Campos
 */
public class DistribuicaoProjetosFactory {
	
	/**
	 * Inst�ncia da f�brica de estrat�gias de distribui��o.
	 */
	protected static DistribuicaoProjetosFactory instance = new DistribuicaoProjetosFactory();
	
	private DistribuicaoProjetosFactory() {
	}
	
	public static DistribuicaoProjetosFactory getInstance() {
		return instance;
	}
	
	/**
	 * Obt�m uma estrat�gia a partir de uma distribui��o.
	 * @param distribuicao
	 * @return
	 * @throws NegocioException
	 */
	public EstrategiaDistribuicaoProjetos getEstrategia(DistribuicaoAvaliacao distribuicao) throws NegocioException {		
		if ( distribuicao.getModeloAvaliacao().isAssociado() || distribuicao.getModeloAvaliacao().getEdital().isAssociado()) {
			if(distribuicao.isAutomatica())
				return new DistribuicaoAutomaticaProjetoAssociado(distribuicao);
			else
				return new DistribuicaoManualProjetoAssociado(distribuicao);
		} else if (distribuicao.getModeloAvaliacao().getEdital().isPesquisa()) {
			return new DistribuicaoManualProjetoApoioNovosPesquisadores(distribuicao);
		}
		throw new NegocioException("N�o h� uma estrat�gia de distribui��o cadastrada para essa distribui��o. Contate o administrador do sistema.");
	}
}
