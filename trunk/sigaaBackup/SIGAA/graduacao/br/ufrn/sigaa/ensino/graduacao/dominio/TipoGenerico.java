/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/19 - 14:54:47
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

/**
 * @author David Pereira
 *
 */
public class TipoGenerico {
	
	private String value;

	public TipoGenerico(String value) {
		this.value = value;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
}
