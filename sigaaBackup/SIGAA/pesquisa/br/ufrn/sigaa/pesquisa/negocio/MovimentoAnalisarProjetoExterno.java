/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 11/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

/**
 * Entidade Responsável pelo movimento de análise do projeto Externo
 * 
 * @author Victor Hugo
 */
public class MovimentoAnalisarProjetoExterno extends AbstractMovimentoAdapter{

	ProjetoPesquisa projeto;
	boolean aprovado;
	
	/**
	 * @return Returns the aprovado.
	 */
	public boolean isAprovado() {
		return aprovado;
	}
	/**
	 * @param aprovado The aprovado to set.
	 */
	public void setAprovado(boolean aprovado) {
		this.aprovado = aprovado;
	}
	/**
	 * @return Returns the projeto.
	 */
	public ProjetoPesquisa getProjeto() {
		return projeto;
	}
	/**
	 * @param projeto The projeto to set.
	 */
	public void setProjeto(ProjetoPesquisa projeto) {
		this.projeto = projeto;
	}
	
	
	
}
