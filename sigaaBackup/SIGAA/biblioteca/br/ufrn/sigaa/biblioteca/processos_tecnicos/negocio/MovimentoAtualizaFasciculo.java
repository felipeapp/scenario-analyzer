/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 26/03/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 *
 *      Movimento que passa os dados para o processador que atualiza um fascículo.
 *
 * @author jadson
 * @since 25/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoAtualizaFasciculo extends AbstractMovimentoAdapter{

	/** Destina-se ao que será salvo no histórico, se não passar nada será salvo material.toString(); */
	private String descricaoOperacao;
	
	/** Verifica permissão de alteração */
	private boolean verificaPermissao = true; 
	
	/** Os fascículos que vão ser atualizados*/
	private List<Fasciculo> fasciculos;
	
	/**
	 * Indica se é permitida a alteração de fascículos baixados.
	 * Seta essa flag manualmente se você quiser alterar um fascículo baixado.
	 */
	private boolean permiteAtualizacaoDeBaixados = false;
	
	
	/**
	 * Indica se é permitida a alteração de fascículos emprestamos.
	 * Utilizado EXCLUSIVAMENTE no caso de uso de comunicação de material perdido para dar baixa em material emprestado e perdido pelo usuário.
	 */
	private boolean permiteAtualizacaoDeEmprestados = false;
	
	/**
	 *    Construtor para alterar um fascículo verificando a permissão e salvando a descrição passada no histórico.
	 * @param exemplar
	 */
	public MovimentoAtualizaFasciculo(Fasciculo fasciculo, String descricaoOperacao ){
		if(this.fasciculos == null)
			fasciculos = new ArrayList<Fasciculo>();
		fasciculos.add(fasciculo);
		
		this.descricaoOperacao = descricaoOperacao;
	}
	
	/**
	 *    Construtor para alterar um fascículo informando se vai verificar a permissão de alteração 
	 * e salvando no histórico do fascículo a descrição passada. Geralmente usado quando se deseja realizar
	 * uma alteração no fascículo sem ser na catalogação. Ex.: Bloquear um fascículo em circulação.
	 * 
	 * @param exemplar
	 */
	public MovimentoAtualizaFasciculo(Fasciculo fasciculo, String descricaoOperacao, boolean verificaPermissao){
		this(fasciculo, descricaoOperacao);
		this.verificaPermissao = verificaPermissao;
	}
	
	
	/**
	 *    Construtor para alterar vários fascículos de uma única vez.
	 * 
	 * @param exemplar
	 */
	public MovimentoAtualizaFasciculo(List<Fasciculo> fasciculos){
		this.fasciculos = fasciculos;
	}


	
	public List<Fasciculo> getFasciculos() {
		return fasciculos;
	}

	public String getDescricaoOperacao() {
		return descricaoOperacao;
	}


	public boolean isVerificaPermissao() {
		return verificaPermissao;
	}

	public boolean isPermiteAtualizacaoDeBaixados() {
		return permiteAtualizacaoDeBaixados;
	}

	public void setPermiteAtualizacaoDeBaixados(boolean permiteAtualizacaoDeBaixados) {
		this.permiteAtualizacaoDeBaixados = permiteAtualizacaoDeBaixados;
	}
	
	public boolean isPermiteAtualizacaoDeEmprestados() {
		return permiteAtualizacaoDeEmprestados;
	}

	public void setPermiteAtualizacaoDeEmprestados(boolean permiteAtualizacaoDeEmprestados) {
		this.permiteAtualizacaoDeEmprestados = permiteAtualizacaoDeEmprestados;
	}
	
	
	
}
