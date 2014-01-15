/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 13/12/2010
 * 
 */
package br.ufrn.sigaa.projetos.negocio;

import br.ufrn.sigaa.pesquisa.negocio.AvaliacaoProjetoApoio;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * F�brica respons�vel por criar inst�ncias adequadas de
 * estrat�gias de avalia��o de projetos.
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
