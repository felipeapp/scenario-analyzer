/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

@Entity
@Table(name = "dados_aluno" , schema = "graduacao")
public class DadosAluno implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_dados_aluno")
	private int id;
	
	/**
	 * Telefone para contato com aluno
	 */
	@Column(name = "telefone")
	private String telefone;

	/**
	 * Email para contato com aluno
	 */
	@Column(name = "email")
	private String email;

	/**
	 * Link para o currículo lattes do aluno
	 */
	@Column(name = "link_lattes")
	private String linkLattes;

	/**
	 * Resumo das qualificações do aluno
	 */
	@Column(name = "qualificacoes")
	private String qualificacoes;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLinkLattes() {
		return linkLattes;
	}

	public void setLinkLattes(String linkLattes) {
		this.linkLattes = linkLattes;
	}

	public String getQualificacoes() {
		return qualificacoes;
	}

	public void setQualificacoes(String qualificacoes) {
		this.qualificacoes = qualificacoes;
	}
	
	
}
