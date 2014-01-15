/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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

	/** Os exemplares que ser�o alterados por esse movimento. */
	private List<Exemplar> exemplares;
	
	/** Para salvar no hist�rico. Se n�o passar a descri��o da opera��o vai salvar material.toString(); */
	private String descricaoOperacao; 
	
	/** Atributo para verificar se a permiss�o na altera��o, chamado nas mudan�as no exemplar nos casos de uso da cataloga��o */
	private boolean verificaPermissao = true; 
	
	/**
	 * Indica se � permitida a altera��o de exemplares baixados.
	 * Seta essa flag manualmente se voc� quiser alterar um exemplar baixado.
	 */
	private boolean permiteAtualizacaoDeBaixados = false;
	
	/**
	 * Indica se � permitida a altera��o de exemplares emprestamos.
	 * Utilizado EXCLUSIVAMENTE no caso de uso de comunica��o de material perdido para dar baixa em material emprestado e perdido pelo usu�rio.
	 */
	private boolean permiteAtualizacaoDeEmprestados = false;
	
	
	/**
	 * Construtor do movimento para alterar um exemplar, verificando a permiss�o e salvando a
	 * descri��o passada no hist�rico.
	 */
	public MovimentoAtualizaExemplar(Exemplar exemplar, String descricaoOperacao ){
		
		if(this.exemplares == null)
			exemplares = new ArrayList<Exemplar>();
		exemplares.add(exemplar);
		
		this.descricaoOperacao = descricaoOperacao;
	}
	
	/**
	 *    Construtor do movimento para alterar um exemplar, informando se vai verificar a permiss�o de
	 * altera��o e salvando no hist�rico exemplar a descri��o passada. Geralmente usado quando se
	 * deseja realizar uma altera��o no exemplar sem ser na cataloga��o.
	 * Ex.: Bloquear um exemplar em circula��o.
	 */
	public MovimentoAtualizaExemplar(Exemplar exemplar, String descricaoOperacao,  boolean verificaPermissao ){
		this(exemplar, descricaoOperacao);
		this.verificaPermissao = verificaPermissao;
	}

	
	/**
	 *     Construtor para alterar v�rios exemplares de uma �nica vez
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
