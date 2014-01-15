/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
	 *  Como � l�gica de neg�cio � simples, para n�o criar 2 processadores, um s� vai catalogar ou    
	 * atualizar os artigos. Essa vari�vel indica qual opera��o est� sendo feita.                 
	 */
	private boolean atualizando; 
	
	/**
	 * Construtor para cria��o
	 * 
	 * @param artigo
	 * @param atualizando se est� criando um novo ou apenas atualizando.
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
