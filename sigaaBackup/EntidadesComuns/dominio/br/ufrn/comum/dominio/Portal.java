package br.ufrn.comum.dominio;

import br.ufrn.arq.util.UFRNUtils;

/**
 * Representa um Portal de Noticia
 * 
 * @author Henrique André
 *
 */
public class Portal {
	/**
	 * Nome do campo na tabela de noticias
	 */
	private String nome;
	
	/**
	 * Descricao do portal
	 */
	private String descricao;
	
	/**
	 * Valor calculado automaticamente
	 * Usando quando precisa passar o nome da tabela via url
	 */
	private String md5;

	public Portal(String nome, String descricao) {
		this.nome = nome;
		this.descricao = descricao;
	}

	public Portal() {
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getMd5() {
		md5 = UFRNUtils.generateKey(nome);
		return md5;
	}

}
