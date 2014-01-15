/*
 * MovimentoEstornaEmprestimo.java
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

import br.ufrn.arq.dominio.MovimentoCadastro;

/**
 *
 * Cont�m os dados que v�o ser passados para o processador que vai estornar o empr�stimo.
 *
 * @author jadson
 * @since 17/10/2008
 * @version 1.0 cria��o da classe
 *
 */
public class MovimentoEstornaEmprestimo extends MovimentoCadastro{

	private static final long serialVersionUID = 1L;
	
	/** Empr�stimo que vai ser estornado. */
	private Integer idEmprestimo;
	
	public MovimentoEstornaEmprestimo(Integer idEmprestimo){
		this.idEmprestimo = idEmprestimo;
	}

	public Integer getIdEmprestimo() {
		return idEmprestimo;
	}
}
