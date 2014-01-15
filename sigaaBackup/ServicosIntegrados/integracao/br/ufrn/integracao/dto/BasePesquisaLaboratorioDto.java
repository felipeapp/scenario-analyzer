/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 04/06/2010
 *
 */	

package br.ufrn.integracao.dto;

import java.io.Serializable;

import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * 	Cria objeto sem comportamento para ser transferido entre as camadas remotas.
 *  Utilizado para encapsular as informações de Grupos de Pesquisa (SIGAA) para ser associadao a laboratórios (SIPAC) no módulo de meio-ambiente.
 *  <br/>
 * 	@author Mychell Teixeira
*/

public class BasePesquisaLaboratorioDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Identificador */
	private int id;
	
	/** Código do grupo de pesquisa */
	private String codigo;

	/** Nome do grupo de pequisa */
	private String nome;

	/** Página do grupo de pesquisa */
	private String homePage;

	/** Email do grupo de pesquisa */
	private String email;

	/** Nome da área de conhecimento ligado a base de pesquisa */
	private String nomeAreaConhecimento;

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getHomePage() {
		return homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNomeAreaConhecimento() {
		return nomeAreaConhecimento;
	}

	public void setNomeAreaConhecimento(String nomeAreaConhecimento) {
		this.nomeAreaConhecimento = nomeAreaConhecimento;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "nome", "codigo");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getCodigo(), nome);
	}

	public int compareTo(Object obj) {
		BasePesquisaLaboratorioDto basePesquisa = (BasePesquisaLaboratorioDto) obj;
		int ultimaComparacao = nome.compareTo(basePesquisa.nome);
		return (ultimaComparacao != 0 ? ultimaComparacao : nome
				.compareTo(basePesquisa.nome));
	}
	
}
