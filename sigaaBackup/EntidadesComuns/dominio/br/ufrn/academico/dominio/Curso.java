/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 16/09/2004
 *
 */
package br.ufrn.academico.dominio;

import br.ufrn.arq.dominio.GenericTipo;

/**
 * Curso da Instituição.
 *
 * @author Gleydson Lima
 */
public class Curso extends GenericTipo {
	
	/**
	 * Identifica se o curso encontra-se ou não ativo. Utilizado na exibição 
	 * dos cursos para recebimento de bolsas.
	 */
	private boolean ativo;
	
	/**
	 * Identifica se é um curso registrado no SIGAA
	 */
	private boolean sigaa;
	
	/**
	 * Utilizado para auxiliar a listagem de cursos no SIPAC.
	 */
	private Boolean exibir;
	
	public Curso() {
		super();
	}

	public String getNome() {
		return getDenominacao();
	}
	
	public void setNome(String nome) {
		setDenominacao(nome);
	}
	
	public Curso(int id, String denominacao) {
		super(id, denominacao);
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean getExibir() {
		return exibir;
	}

	public void setExibir(Boolean exibir) {
		this.exibir = exibir;
	}

	public boolean isSigaa() {
		return sigaa;
	}

	public void setSigaa(boolean sigaa) {
		this.sigaa = sigaa;
	}
}