/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Criado em: 01/10/2008
 */
package br.ufrn.comum.dominio;

/**
 * Classe auxiliar para realização da associação entre um processo e um interessado.
 * @author Itamir Filho
 */
public class ProcessoInteressado {

	private int id;
	
	private int idInteressado;
	
	private int idProcesso;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdInteressado() {
		return idInteressado;
	}

	public void setIdInteressado(int idInteressado) {
		this.idInteressado = idInteressado;
	}

	public int getIdProcesso() {
		return idProcesso;
	}

	public void setIdProcesso(int idProcesso) {
		this.idProcesso = idProcesso;
	}
}
