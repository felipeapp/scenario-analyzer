/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/05/2009
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArtigoDePeriodico;

/**
 *
 *    Passa os dados para o processador que cataloga um artigo
 *
 * @author jadson
 * @since 05/05/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoCatalogaAtualizaArtigoPeriodico extends AbstractMovimentoAdapter{

	/** O artigo que vai ser criado ou atualizado.*/
	private ArtigoDePeriodico artigo;

	/**
	 *  Como é lógica de negócio é simples, para não criar 2 processadores, um só vai catalogar ou    
	 * atualizar os artigos. Essa variável indica qual operação está sendo feita.                 
	 */
	private boolean atualizando; 
	
	/**
	 * Construtor para criação
	 * 
	 * @param artigo
	 * @param atualizando se está criando um novo ou apenas atualizando.
	 */
	public MovimentoCatalogaAtualizaArtigoPeriodico(ArtigoDePeriodico artigo, boolean atualizando) {
		this.artigo = artigo;
		this.atualizando = atualizando;
	}
	

	public ArtigoDePeriodico getArtigo() {
		return artigo;
	}

	public boolean isAtualizando() {
		return atualizando;
	}
	
}
