/*
 * MovimentoAtribuiFasciculoAoArtigo.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArtigoDePeriodico;

/**
 *
 *      Classe responsável apenas pela passagem dos dados para o processador.
 *
 * @author jadson
 * @since 22/09/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoAtribuiFasciculoAoArtigo extends AbstractMovimentoAdapter{

	/** Objeto referente aos artigos de periódicos*/
	private ArtigoDePeriodico artigo;

	public MovimentoAtribuiFasciculoAoArtigo(ArtigoDePeriodico artigo) {
		this.artigo = artigo;
	}

	
	public ArtigoDePeriodico getArtigo() {
		return artigo;
	}

	public void setArtigo(ArtigoDePeriodico artigo) {
		this.artigo = artigo;
	}
	
}
