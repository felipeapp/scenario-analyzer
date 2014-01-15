/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Fábrica responsável por criar instâncias adequadas de
 * estratégias de distribuição de projetos.
 * 
 * @author Ilueny Santos
 * @author Leonardo Campos
 */
public class DistribuicaoProjetosFactory {
	
	/**
	 * Instância da fábrica de estratégias de distribuição.
	 */
	protected static DistribuicaoProjetosFactory instance = new DistribuicaoProjetosFactory();
	
	private DistribuicaoProjetosFactory() {
	}
	
	public static DistribuicaoProjetosFactory getInstance() {
		return instance;
	}
	
	/**
	 * Obtém uma estratégia a partir de uma distribuição.
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
		throw new NegocioException("Não há uma estratégia de distribuição cadastrada para essa distribuição. Contate o administrador do sistema.");
	}
}
