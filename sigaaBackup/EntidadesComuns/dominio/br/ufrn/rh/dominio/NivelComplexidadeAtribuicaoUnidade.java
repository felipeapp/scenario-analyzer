/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 01/07/2011
 */
package br.ufrn.rh.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Indica qual � o n�vel de complexidade de uma atribui��o de uma unidade.
 *
 * @author Br�ulio
 */
@Entity
@Table( schema="comum", name="nivel_complexidade_atribuicao_unidade" )
public class NivelComplexidadeAtribuicaoUnidade implements PersistDB {

	/** Id do registro. */
	@Id
	@Column(name = "id_nivel_complexidade_atribuicao_unidade")
	private int id;
	
	/** Descri��o do n�vel de complexidade. */
	@Column
	private String nome;
	
	/** Indica se o objeto � um n�vel de complexidade v�lido. */
	@Column
	private boolean ativo;
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId( int id ) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome( String nome ) {
		this.nome = nome;
	}

	public boolean isAtivo() {
		return ativo;
	}
	
	public void setAtivo( boolean ativo ) {
		this.ativo = ativo;
	}

}
