/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '10/12/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.espacofisico;

/**
 * Objeto que encapsula os filtros que devem ser utilizados na consulta de busca por espaço físico
 * 
 * @author Henrique André
 *
 */
public class RestricoesBusca {

	private boolean buscaCodigo;

	private boolean buscaCapacidade;

	private boolean buscaDescricao;

	private boolean buscaArea;
	
	private boolean buscaTipoRecurso;

	private boolean buscaLocalizacao;
	
	private boolean buscaReservaPrioritaria;
	
	public boolean isBuscaCodigo() {
		return buscaCodigo;
	}

	public void setBuscaCodigo(boolean buscaCodigo) {
		this.buscaCodigo = buscaCodigo;
	}

	public boolean isBuscaCapacidade() {
		return buscaCapacidade;
	}

	public void setBuscaCapacidade(boolean buscaCapacidade) {
		this.buscaCapacidade = buscaCapacidade;
	}

	public boolean isBuscaLocalizacao() {
		return buscaLocalizacao;
	}

	public void setBuscaLocalizacao(boolean buscaLocalizacao) {
		this.buscaLocalizacao = buscaLocalizacao;
	}

	public boolean isBuscaDescricao() {
		return buscaDescricao;
	}

	public void setBuscaDescricao(boolean buscaDescricao) {
		this.buscaDescricao = buscaDescricao;
	}

	public boolean isBuscaArea() {
		return buscaArea;
	}

	public void setBuscaArea(boolean buscaArea) {
		this.buscaArea = buscaArea;
	}

	public boolean isBuscaTipoRecurso() {
		return buscaTipoRecurso;
	}

	public void setBuscaTipoRecurso(boolean buscaTipoRecurso) {
		this.buscaTipoRecurso = buscaTipoRecurso;
	}

	public boolean isBuscaReservaPrioritaria() {
		return buscaReservaPrioritaria;
	}

	public void setBuscaReservaPrioritaria(boolean buscaReservaPrioritaria) {
		this.buscaReservaPrioritaria = buscaReservaPrioritaria;
	}

	/**
	 * Se o usuário não selecionar nenhum filtro, retorna true.
	 * 
	 * @return
	 */
	public boolean isNaoSelecionou() {
		 return !(buscaCodigo || buscaCapacidade || buscaDescricao || buscaArea || buscaTipoRecurso || buscaLocalizacao || buscaReservaPrioritaria);
	}
}
