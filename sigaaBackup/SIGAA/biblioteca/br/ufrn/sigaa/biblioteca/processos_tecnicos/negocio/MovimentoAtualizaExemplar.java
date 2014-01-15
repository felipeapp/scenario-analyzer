/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/03/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;

/**
 *
 *    Movimento para atualizar um exemplar.
 *
 * @author jadson
 * @since 25/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoAtualizaExemplar  extends AbstractMovimentoAdapter{

	/** Os exemplares que serão alterados por esse movimento. */
	private List<Exemplar> exemplares;
	
	/** Para salvar no histórico. Se não passar a descrição da operação vai salvar material.toString(); */
	private String descricaoOperacao; 
	
	/** Atributo para verificar se a permissão na alteração, chamado nas mudanças no exemplar nos casos de uso da catalogação */
	private boolean verificaPermissao = true; 
	
	/**
	 * Indica se é permitida a alteração de exemplares baixados.
	 * Seta essa flag manualmente se você quiser alterar um exemplar baixado.
	 */
	private boolean permiteAtualizacaoDeBaixados = false;
	
	/**
	 * Indica se é permitida a alteração de exemplares emprestamos.
	 * Utilizado EXCLUSIVAMENTE no caso de uso de comunicação de material perdido para dar baixa em material emprestado e perdido pelo usuário.
	 */
	private boolean permiteAtualizacaoDeEmprestados = false;
	
	
	/**
	 * Construtor do movimento para alterar um exemplar, verificando a permissão e salvando a
	 * descrição passada no histórico.
	 */
	public MovimentoAtualizaExemplar(Exemplar exemplar, String descricaoOperacao ){
		
		if(this.exemplares == null)
			exemplares = new ArrayList<Exemplar>();
		exemplares.add(exemplar);
		
		this.descricaoOperacao = descricaoOperacao;
	}
	
	/**
	 *    Construtor do movimento para alterar um exemplar, informando se vai verificar a permissão de
	 * alteração e salvando no histórico exemplar a descrição passada. Geralmente usado quando se
	 * deseja realizar uma alteração no exemplar sem ser na catalogação.
	 * Ex.: Bloquear um exemplar em circulação.
	 */
	public MovimentoAtualizaExemplar(Exemplar exemplar, String descricaoOperacao,  boolean verificaPermissao ){
		this(exemplar, descricaoOperacao);
		this.verificaPermissao = verificaPermissao;
	}

	
	/**
	 *     Construtor para alterar vários exemplares de uma única vez
	 *     
	 * @param exemplares
	 */
	public MovimentoAtualizaExemplar(List<Exemplar> exemplares){
		this.exemplares = exemplares;
	}
	
	public List<Exemplar> getExemplares() {
		return exemplares;
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
