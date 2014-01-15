/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/05/2008
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Classe de domínio para auxiliar o processamento da seleção de fiscais,
 * informando quantos fiscais estão inscritos e quantos serão selecionados por
 * município
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public class QuantidadeFiscalPorMunicipio extends Municipio {
	/** Número de fiscais inscritos no município. */
	private int numInscritos = 0;
	
	/** Número de fiscais a serem selecionados. */
	private int numFiscais = 0;
	
	/** Percentual de reservas a serem selecionados. */
	private int percentualReserva = 0;

	/** Retorna o número de fiscais a serem selecionados.  
	 * @return
	 */
	public int getNumFiscais() {
		return numFiscais;
	}

	/** Retorna o número de fiscais inscritos no município. 
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

	/** Seta o número de fiscais a serem selecionados.
	 * @param numFiscais
	 */
	public void setNumFiscais(int numFiscais) {
		this.numFiscais = numFiscais;
	}

	/** Seta o número de fiscais inscritos no município.
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
	 * Retorna uma representação textual da quantidade de fiscais por município, no formato:
	 * nome do município, seguido de vírgula, seguido do número de inscritos
	 * seguido da palavra "inscritos", seguido de vírgula,
	 * seguido do número de fiscais a selecionar, seguido da palavra "a selecionar".
	 * 
	 */
	@Override
	public String toString() {
		return getNome() + ", " + getNumInscritos() + " inscritos, "
				+ getNumFiscais() + " a selecionar";
	}
}
