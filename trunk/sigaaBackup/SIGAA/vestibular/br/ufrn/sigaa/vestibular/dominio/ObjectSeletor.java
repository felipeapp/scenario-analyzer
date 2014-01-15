/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/04/2009
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

/**
 * Classe para Auxiliar nas opera��es que envolvem uma lista de objetos a
 * selecionar.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
public class ObjectSeletor<T> {

	/** Indica se objeto est� selecionado para opera��o. */
	private boolean selecionado = false;

	/** Objeto a operar. */
	private T objeto;
	
	public ObjectSeletor() {
	}
	
	public ObjectSeletor(T objeto) {
		super();
		this.objeto = objeto;
	}

	/**
	 * Retorna a inscri��o de fiscal a operar.
	 * 
	 * @return
	 */
	public T getObjeto() {
		return objeto;
	}

	/**
	 * Indica se a inscri��o de fiscal est� selecionada.
	 * 
	 * @return
	 */
	public boolean isSelecionado() {
		return selecionado;
	}

	/**
	 * Seta o objeto a operar.
	 * 
	 * @param inscricaoFiscal
	 */
	public void setObjeto(T objeto) {
		this.objeto = objeto;
	}

	/**
	 * Seta se o objeto est� selecionada.
	 * 
	 * @param selecionado
	 */
	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	/**
	 * Retorna uma representa��o textual deste objeto no formato: inscri��o do
	 * fiscal, seguido de v�rgula, seguido da indica��o de sele��o.
	 */
	@Override
	public String toString() {
		return objeto.toString() + ", "
				+ (selecionado ? "SELECIONADO" : "N�O SELECIONADO");
	}

}
