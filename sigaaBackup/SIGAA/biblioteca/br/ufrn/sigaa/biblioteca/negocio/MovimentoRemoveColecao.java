/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 19/05/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;

/**
 *
 * <p>Passa os dados para o processador que vai remover a coleção </p>
 *
 * @author jadson
 *
 */
public class MovimentoRemoveColecao extends AbstractMovimentoAdapter{

	/** A coleção que vai ser removido (desativado) */
	private Colecao colecao; 
	
	/** A nova coleção que os materias que possuem a coleção antiga vão sem migrados */
	private Colecao novaColecao;

	
	
	public MovimentoRemoveColecao(Colecao colecao, Colecao novaColecao) {
		super();
		this.colecao = colecao;
		this.novaColecao = novaColecao;
	}

	public Colecao getColecao() {
		return colecao;
	}

	public Colecao getNovaColecao() {
		return novaColecao;
	} 
	
	
	
}
