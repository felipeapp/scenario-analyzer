/*
 * MovimentoProrrogaPrazoEmprestimo.java
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

import java.util.Date;

import br.ufrn.arq.dominio.MovimentoCadastro;

/**
 *
 *    Classe responsável por repassar o período de prorrogação do empréstimo para o Processador.
 * @see ProcessadorProrrogaPrazoEmprestimo
 *
 * @author jadson
 * @since 23/10/2008
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoProrrogaPrazoEmprestimo extends MovimentoCadastro{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

	private Date  dataInicioPeriodo = new Date();
	
	private Date dataFinalPeriodo;
	
	

	public MovimentoProrrogaPrazoEmprestimo(Date dataInicioPeriodo, Date dataFinalPeriodo) {
		this.dataFinalPeriodo = dataFinalPeriodo;
		this.dataInicioPeriodo = dataInicioPeriodo;
	}

	public Date getDataInicioPeriodo() {
		return dataInicioPeriodo;
	}

	public Date getDataFinalPeriodo() {
		return dataFinalPeriodo;
	}
	
	
	
	
	
}
