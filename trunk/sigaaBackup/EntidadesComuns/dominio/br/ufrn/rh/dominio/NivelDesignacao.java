/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Criado em: 15/05/2008
 */
package br.ufrn.rh.dominio;

import br.ufrn.arq.dominio.PersistDB;

/**
 * @author Itamir Filho
 *
 */
public class NivelDesignacao implements PersistDB  {
	
	public static final String CD = "CD";
	public static final String FC = "FC";
	public static final String FG = "FG";
	
	private int id;
	
	private String sigla;
	
	private String descricao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
