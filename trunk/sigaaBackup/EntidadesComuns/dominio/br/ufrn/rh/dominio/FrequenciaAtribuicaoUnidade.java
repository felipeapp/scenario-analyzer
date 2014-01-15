/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Frequência com que uma atribuição da unidade é executada.
 *
 * @author Bráulio
 */
@Entity
@Table( schema="comum", name="frequencia_atribuicao_unidade" )
public class FrequenciaAtribuicaoUnidade implements PersistDB {

	/** Id do registro. */
	@Id
	@Column(name = "id_frequencia_atribuicao_unidade")
	private int id;
	
	/** Descrição da frequência. */
	@Column
	private String nome;
	
	/** Indica se o objeto é uma frequência válida. */
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
