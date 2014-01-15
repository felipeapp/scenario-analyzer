/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 13/09/2004
 */
package br.ufrn.comum.dominio;

import java.util.Collection;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.comum.dominio.Papel;

/**
 * O SIPAC é formado por um conjunto de subsistemas. Esta classe mapeia o
 * subsistema.
 *
 * @author Gleydson Lima
 *
 */
public class SubSistema implements PersistDB, Comparable<SubSistema> {

	private int id;
	
	/** Sistema ao qual pertence o subsistema */
	private Sistema sistema;

	/** Identifica se o subsistema está ativo ou não */
	private boolean ativo;

	/** Nome do subsistema */
	private String nome;
	
	/** Nome abreviado do subsistema */
	private String nomeReduzido;

	/** URL da página inicial do subsistema */
	private String link;

	/** Forward do struts para entrar no subsistema (se usar struts) */
	private String forward;

	/** Diretório base das jsps do subsistema */
	private String dirBase;

	private Collection<Papel> papeis;

	public String getForward() {
		return forward;
	}

	public void setForward(String forward) {
		this.forward = forward;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public SubSistema() {

	}

	public SubSistema(int id) {
		this.id = id;
	}

	public SubSistema(int id, String nome, String link, String forward) {
		this.id = id;
		this.nome = nome;
		this.link = link;
		this.forward = forward;
	}

	public SubSistema(int id, String nome, String link, String forward, String dirBase) {
		this.id = id;
		this.nome = nome;
		this.link = link;
		this.forward = forward;
		this.dirBase = dirBase;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int compareTo(SubSistema o) {
		if (nome != null && o != null && o.nome != null)
			return nome.compareTo(o.nome);
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubSistema other = (SubSistema) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String getDirBase() {
		if ( dirBase == null ) {
			return "";
		}
		return dirBase;
	}

	public void setDirBase(String dirBase) {
		this.dirBase = dirBase;
	}

	public Sistema getSistema() {
		return sistema;
	}

	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}

	public Collection<Papel> getPapeis() {
		return papeis;
	}

	public void setPapeis(Collection<Papel> papeis) {
		this.papeis = papeis;
	}
	
	public String getNomeReduzido() {
		return nomeReduzido;
	}
	
	public void setNomeReduzido(String nomeReduzido) {
		this.nomeReduzido = nomeReduzido;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
}
