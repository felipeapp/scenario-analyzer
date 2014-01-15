/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 18/04/2011
 */
package br.ufrn.sigaa.relatoriosgestao.dominio;

/**
 * Classe respons�vel por armazenar as informa��es que ser�o exibidas no relat�rio de �ndices acad�micos.
 * 
 * @author arlindo
 *
 */
public class RelatorioIndicesTotais {
	
	/** Posi��o do array que a faixa se encontra */
	private int posicaoFaixa;
	
	/** Descri��o da faixa */
	private String faixa;
	
	/** Quantidade de discentes na faixa */
	private int valor;
	
	/** Percentual de discentes na faixa */
	private float percentual;

	public int getPosicaoFaixa() {
		return posicaoFaixa;
	}

	public void setPosicaoFaixa(int posicaoFaixa) {
		this.posicaoFaixa = posicaoFaixa;
	}

	public String getFaixa() {
		return faixa;
	}

	public void setFaixa(String faixa) {
		this.faixa = faixa;
	}

	public int getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}

	public float getPercentual() {
		return percentual;
	}

	public void setPercentual(float percentual) {
		this.percentual = percentual;
	}
}
