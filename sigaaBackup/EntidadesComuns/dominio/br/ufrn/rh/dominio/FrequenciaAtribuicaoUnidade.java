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
 * Frequ�ncia com que uma atribui��o da unidade � executada.
 *
 * @author Br�ulio
 */
@Entity
@Table( schema="comum", name="frequencia_atribuicao_unidade" )
public class FrequenciaAtribuicaoUnidade implements PersistDB {

	/** Id do registro. */
	@Id
	@Column(name = "id_frequencia_atribuicao_unidade")
	private int id;
	
	/** Descri��o da frequ�ncia. */
	@Column
	private String nome;
	
	/** Indica se o objeto � uma frequ�ncia v�lida. */
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
