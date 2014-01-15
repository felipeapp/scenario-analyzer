/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/24
 */
package br.ufrn.sigaa.ead.jsf;

import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.Polo;

/**
 * Cadastrar usuário para um Tutor Orientador 
 * 
 * @author David Pereira
 *
 */
public class CadastrarUsuarioTutorMBean {

	private Curso curso;
	
	private Polo polo;
	
	public CadastrarUsuarioTutorMBean() {
		curso = new Curso();
		polo = new Polo();
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}
	
}
