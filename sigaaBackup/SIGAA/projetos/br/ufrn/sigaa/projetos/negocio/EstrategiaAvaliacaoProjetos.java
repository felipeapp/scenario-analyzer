/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 18/11/2010
 * 
 */
package br.ufrn.sigaa.projetos.negocio;

import java.util.List;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Interface utilizada na implementa��o das diversas
 * estrat�gias de avalia��o de projetos. Devem existir
 * diversas implementa��es dessa interface para avalia��o 
 * de projetos de ensino, pesquisa e extens�o. 
 *  
 * @author ilueny santos
 *
 */
public interface EstrategiaAvaliacaoProjetos {
	
	/**
	 * Realiza a classifica��o dos projetos informados de acordo com
	 * os crit�rios de classifica��o estabelecidos para cada tipo de 
	 * projeto.
	 * 
	 */
	public void classificar(List<Projeto> projetos) throws NegocioException;
	
	public void avaliar(Avaliacao avaliacao) throws NegocioException;

}
