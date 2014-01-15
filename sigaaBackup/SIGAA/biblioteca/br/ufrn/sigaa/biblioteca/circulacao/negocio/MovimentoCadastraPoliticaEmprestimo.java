/*
 * MovimentoCadastraPoliticaEmprestimo.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;

/**
 *
 *    Passa os dados para o processador que atualiza a política de empréstimo.
 *
 * @author jadson
 * @since 03/06/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoCadastraPoliticaEmprestimo extends AbstractMovimentoAdapter{

	
	private List<PoliticaEmprestimo> politicasEmprestimo;

	public MovimentoCadastraPoliticaEmprestimo(List<PoliticaEmprestimo> politicasEmprestimo) {
		this.politicasEmprestimo = politicasEmprestimo;
	}

	public List<PoliticaEmprestimo> getPoliticasEmprestimo() {
		return politicasEmprestimo;
	}
	
}
