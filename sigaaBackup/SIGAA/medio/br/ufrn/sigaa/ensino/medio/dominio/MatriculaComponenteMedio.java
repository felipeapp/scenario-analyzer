/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 25/07/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dominio;

import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;

/**
 * Classe que representa as matr�culas em componentes do discente de ensino m�dio, 
 * possuindo os dados referente a sua matr�cula na s�rie. (N�o persistida)  
 * 
 * @author Rafael Gomes
 *
 */
public class MatriculaComponenteMedio {

	/** Objeto utilizado para armazenar a matricula do discente na s�rie. */
	MatriculaDiscenteSerie matriculaDiscenteSerie =  new MatriculaDiscenteSerie();
	/** Objeto utilizando para armazenar a matricula do discente na disciplina. */
	MatriculaComponente matriculaComponente = new MatriculaComponente();
	
	
	
	/**
	 * Construtor
	 */
	public MatriculaComponenteMedio(
			MatriculaDiscenteSerie matriculaDiscenteSerie,
			MatriculaComponente matriculaComponente) {
		this.matriculaDiscenteSerie = matriculaDiscenteSerie;
		this.matriculaComponente = matriculaComponente;
	}

	public MatriculaDiscenteSerie getMatriculaDiscenteSerie() {
		return matriculaDiscenteSerie;
	}
	public void setMatriculaDiscenteSerie(
			MatriculaDiscenteSerie matriculaDiscenteSerie) {
		this.matriculaDiscenteSerie = matriculaDiscenteSerie;
	}
	public MatriculaComponente getMatriculaComponente() {
		return matriculaComponente;
	}
	public void setMatriculaComponente(MatriculaComponente matriculaComponente) {
		this.matriculaComponente = matriculaComponente;
	}

}