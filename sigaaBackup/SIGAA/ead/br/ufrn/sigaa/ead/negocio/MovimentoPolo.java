/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/19 - 19:30:51
 */
package br.ufrn.sigaa.ead.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.Polo;

/**
 * Classe de Movimentação de Pólo
 * @author David Pereira
 *
 */
public class MovimentoPolo extends AbstractMovimentoAdapter {

	private List<Curso> cursos;
	
	private Polo polo;

	/**
	 * @return the cursos
	 */
	public List<Curso> getCursos() {
		return cursos;
	}

	/**
	 * @param cursos the cursos to set
	 */
	public void setCursos(List<Curso> cursos) {
		this.cursos = cursos;
	}

	/**
	 * @return the polo
	 */
	public Polo getPolo() {
		return polo;
	}

	/**
	 * @param polo the polo to set
	 */
	public void setPolo(Polo polo) {
		this.polo = polo;
	}
	
}
