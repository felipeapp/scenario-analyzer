/*
 * MovimentoDevolveEmprestimos.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software � confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * N�o se deve utilizar este produto em desacordo com as normas
 * da referida institui��o.
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.util.Date;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.InterrupcaoBiblioteca;

/**
 * Movimento que guarda os dados passados ao processador que cria interrup��es para as bibliotecas. 
 *
 * @author Fred
 * @since 15/10/2008
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoCadastraInterrupcaoBiblioteca extends MovimentoCadastro {

	private InterrupcaoBiblioteca interrupcao; // a interrup��o que vai ser cadastrada
	
	// o per�odo das interrup��es //
	private Date dataInicio;
	private Date dataFim;
	
	
	/**
	 * Construtor padr�o
	 * 
	 * @param interrupcaoBiblioteca
	 * @param dataInicio
	 * @param dataFim
	 */
	public MovimentoCadastraInterrupcaoBiblioteca(InterrupcaoBiblioteca interrupcao, Date dataInicio, Date dataFim){
		this.interrupcao = interrupcao;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
	
	}
	
	public InterrupcaoBiblioteca getInterrupcao() {
		return interrupcao;
	}


	public Date getDataInicio() {
		return dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

}