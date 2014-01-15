/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/04/2009
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

/**
 * Classe para Auxiliar nas operações que envolvem uma lista de objetos a
 * selecionar.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class ObjectSeletor<T> {

	/** Indica se objeto está selecionado para operação. */
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
	 * Retorna a inscrição de fiscal a operar.
	 * 
	 * @return
	 */
	public T getObjeto() {
		return objeto;
	}

	/**
	 * Indica se a inscrição de fiscal está selecionada.
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
	 * Seta se o objeto está selecionada.
	 * 
	 * @param selecionado
	 */
	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	/**
	 * Retorna uma representação textual deste objeto no formato: inscrição do
	 * fiscal, seguido de vírgula, seguido da indicação de seleção.
	 */
	@Override
	public String toString() {
		return objeto.toString() + ", "
				+ (selecionado ? "SELECIONADO" : "NÃO SELECIONADO");
	}

}
