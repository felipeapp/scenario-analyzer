/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '13/04/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.relatorios;

import br.ufrn.sigaa.dominio.Unidade;

/**
 * Classe que representa uma linha do relat�rio sint�tico de cursos de especializa��o por centro.
 * @author Leonardo
 *
 */
public class LinhaRelatorioSinteticoCursosCentro {

	
	private Unidade unidade;
	
	private long numeroCursos;
	
	public LinhaRelatorioSinteticoCursosCentro(){
		
	}

	public long getNumeroCursos() {
		return numeroCursos;
	}

	public void setNumeroCursos(long numeroCursos) {
		this.numeroCursos = numeroCursos;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}
	
	
}
