/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 *      Movimento que passa os dados para o processador que atualiza um fasc�culo.
 *
 * @author jadson
 * @since 25/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoAtualizaFasciculo extends AbstractMovimentoAdapter{

	/** Destina-se ao que ser� salvo no hist�rico, se n�o passar nada ser� salvo material.toString(); */
	private String descricaoOperacao;
	
	/** Verifica permiss�o de altera��o */
	private boolean verificaPermissao = true; 
	
	/** Os fasc�culos que v�o ser atualizados*/
	private List<Fasciculo> fasciculos;
	
	/**
	 * Indica se � permitida a altera��o de fasc�culos baixados.
	 * Seta essa flag manualmente se voc� quiser alterar um fasc�culo baixado.
	 */
	private boolean permiteAtualizacaoDeBaixados = false;
	
	
	/**
	 * Indica se � permitida a altera��o de fasc�culos emprestamos.
	 * Utilizado EXCLUSIVAMENTE no caso de uso de comunica��o de material perdido para dar baixa em material emprestado e perdido pelo usu�rio.
	 */
	private boolean permiteAtualizacaoDeEmprestados = false;
	
	/**
	 *    Construtor para alterar um fasc�culo verificando a permiss�o e salvando a descri��o passada no hist�rico.
	 * @param exemplar
	 */
	public MovimentoAtualizaFasciculo(Fasciculo fasciculo, String descricaoOperacao ){
		if(this.fasciculos == null)
			fasciculos = new ArrayList<Fasciculo>();
		fasciculos.add(fasciculo);
		
		this.descricaoOperacao = descricaoOperacao;
	}
	
	/**
	 *    Construtor para alterar um fasc�culo informando se vai verificar a permiss�o de altera��o 
	 * e salvando no hist�rico do fasc�culo a descri��o passada. Geralmente usado quando se deseja realizar
	 * uma altera��o no fasc�culo sem ser na cataloga��o. Ex.: Bloquear um fasc�culo em circula��o.
	 * 
	 * @param exemplar
	 */
	public MovimentoAtualizaFasciculo(Fasciculo fasciculo, String descricaoOperacao, boolean verificaPermissao){
		this(fasciculo, descricaoOperacao);
		this.verificaPermissao = verificaPermissao;
	}
	
	
	/**
	 *    Construtor para alterar v�rios fasc�culos de uma �nica vez.
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
