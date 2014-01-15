/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/05/2008
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Classe de dom�nio para auxiliar o processamento da sele��o de fiscais,
 * informando quantos fiscais est�o inscritos e quantos ser�o selecionados por
 * munic�pio
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
public class QuantidadeFiscalPorMunicipio extends Municipio {
	/** N�mero de fiscais inscritos no munic�pio. */
	private int numInscritos = 0;
	
	/** N�mero de fiscais a serem selecionados. */
	private int numFiscais = 0;
	
	/** Percentual de reservas a serem selecionados. */
	private int percentualReserva = 0;

	/** Retorna o n�mero de fiscais a serem selecionados.  
	 * @return
	 */
	public int getNumFiscais() {
		return numFiscais;
	}

	/** Retorna o n�mero de fiscais inscritos no munic�pio. 
	 * @return
	 */
	public int getNumInscritos() {
		return numInscritos;
	}

	/** Retorna o percentual de reservas a serem selecionados. 
	 * @return
	 */
	public int getPercentualReserva() {
		return percentualReserva;
	}

	/** Seta o n�mero de fiscais a serem selecionados.
	 * @param numFiscais
	 */
	public void setNumFiscais(int numFiscais) {
		this.numFiscais = numFiscais;
	}

	/** Seta o n�mero de fiscais inscritos no munic�pio.
	 * @param numInscritos
	 */
	public void setNumInscritos(int numInscritos) {
		this.numInscritos = numInscritos;
	}

	/** Seta o percentual de reservas a serem selecionados.
	 * @param percentualReserva
	 */
	public void setPercentualReserva(int percentualReserva) {
		this.percentualReserva = percentualReserva;
	}

	/**
	 * Retorna uma representa��o textual da quantidade de fiscais por munic�pio, no formato:
	 * nome do munic�pio, seguido de v�rgula, seguido do n�mero de inscritos
	 * seguido da palavra "inscritos", seguido de v�rgula,
	 * seguido do n�mero de fiscais a selecionar, seguido da palavra "a selecionar".
	 * 
	 */
	@Override
	public String toString() {
		return getNome() + ", " + getNumInscritos() + " inscritos, "
				+ getNumFiscais() + " a selecionar";
	}
}
