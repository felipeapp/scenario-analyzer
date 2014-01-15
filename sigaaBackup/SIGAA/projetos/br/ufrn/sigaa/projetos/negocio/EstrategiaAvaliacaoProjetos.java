/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Interface utilizada na implementação das diversas
 * estratégias de avaliação de projetos. Devem existir
 * diversas implementações dessa interface para avaliação 
 * de projetos de ensino, pesquisa e extensão. 
 *  
 * @author ilueny santos
 *
 */
public interface EstrategiaAvaliacaoProjetos {
	
	/**
	 * Realiza a classificação dos projetos informados de acordo com
	 * os critérios de classificação estabelecidos para cada tipo de 
	 * projeto.
	 * 
	 */
	public void classificar(List<Projeto> projetos) throws NegocioException;
	
	public void avaliar(Avaliacao avaliacao) throws NegocioException;

}
