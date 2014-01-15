/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/11/2007
 */
package br.ufrn.comum.dominio.correios;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que contém as informações das diversas 
 * localidades (municípios, distritos, etc.) do país.
 * 
 * (Dados extraídos da base de dados dos Correios)
 * 
 * @author Ricardo Wendell
 * @author Gleydson Lima
 *
 */
@Entity
@Table(name = "localidade",schema = "correios")
public class Localidade implements PersistDB {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;

	private String uf;

	private String nome;
	
	@Column(name="nome_ascii")
	private String nomeAscii;

	private Long cep;

	public Long getCep() {
		return this.cep;
	}

	public void setCep(Long cep) {
		this.cep = cep;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUf() {
		return this.uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}
	
	
	
	public String getNomeAscii() {
		return nomeAscii;
	}

	public void setNomeAscii(String nomeAscii) {
		this.nomeAscii = nomeAscii;
	}

	public String getNomeUF(){
		
		return nome + " - " + getUf();
	}

}
