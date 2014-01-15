/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Livraria
 * Data de Criação: 06/07/2009
 */
package br.ufrn.sigaa.mobile.jsf;

import br.ufrn.sigaa.dominio.Usuario;

/**
 * Informações de acesso para usuários mobile.
 * 
 * @author David Pereira
 *
 */
public class DadosAcessoMobile {

	private Usuario usuario;
	
	private boolean aluno;
	
	private boolean professor;
	
	private boolean servidor;

	public DadosAcessoMobile(Usuario usuario) {
		this.usuario = usuario;
		this.aluno = usuario.getVinculoAtivo().isVinculoDiscente();
		this.professor = usuario.getVinculoAtivo().isVinculoServidor();
		this.servidor = usuario.getVinculoAtivo().isVinculoServidor();
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public boolean isAluno() {
		return aluno;
	}

	public void setAluno(boolean aluno) {
		this.aluno = aluno;
	}

	public boolean isProfessor() {
		return professor;
	}

	public void setProfessor(boolean professor) {
		this.professor = professor;
	}

	public boolean isServidor() {
		return servidor;
	}

	public void setServidor(boolean servidor) {
		this.servidor = servidor;
	}

}
