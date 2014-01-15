/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>Passa os dados para o processador que vai remover a cole��o </p>
 *
 * @author jadson
 *
 */
public class MovimentoRemoveColecao extends AbstractMovimentoAdapter{

	/** A cole��o que vai ser removido (desativado) */
	private Colecao colecao; 
	
	/** A nova cole��o que os materias que possuem a cole��o antiga v�o sem migrados */
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
