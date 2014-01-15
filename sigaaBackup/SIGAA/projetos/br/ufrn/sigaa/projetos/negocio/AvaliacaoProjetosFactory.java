/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 13/12/2010
 * 
 */
package br.ufrn.sigaa.projetos.negocio;

import br.ufrn.sigaa.pesquisa.negocio.AvaliacaoProjetoApoio;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Fábrica responsável por criar instâncias adequadas de
 * estratégias de avaliação de projetos.
 * 
 * @author ilueny santos
 *
 */
public class AvaliacaoProjetosFactory {
	
	protected static AvaliacaoProjetosFactory instance = new AvaliacaoProjetosFactory();
	
	private AvaliacaoProjetosFactory() {
	}
	
	public static AvaliacaoProjetosFactory getInstance() {
		return instance;
	}
	
	public EstrategiaAvaliacaoProjetos getEstrategia(Projeto projeto) {		
		if (projeto.isProjetoAssociado())
			return new AvaliacaoProjetoAssociado();
		else if (projeto.isApoioGrupoPesquisa() || projeto.isApoioNovosPesquisadores())
			return new AvaliacaoProjetoApoio();
		else {
			//Valor default
			return new AvaliacaoProjetoAssociado();
		}		
	}
}
