/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 10/08/2005
 */
package br.ufrn.comum.dominio;

import br.ufrn.arq.dominio.GenericTipo;


/**
 * Representação de um estado do Brasil
 *
 * @author Gleydson Lima
 *
 */
public class Estado extends GenericTipo {

	public static int RN = 18;

	/** Nome completo do estado */
	private String descricao;

	public Estado(){}

	public Estado(String nome){
		super.setDenominacao(nome);
	}

	public Estado(int id) {
		super.setId(id);
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}