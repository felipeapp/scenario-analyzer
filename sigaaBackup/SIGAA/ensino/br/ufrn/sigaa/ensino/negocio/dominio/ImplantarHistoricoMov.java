/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Jun 27, 2007
 *
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Movimento da opera��o de implantar hist�rico 
 * @author Victor Hugo
 *
 */
public class ImplantarHistoricoMov extends AbstractMovimentoAdapter {

	/**
	 * Discente da implanta��o que est� sendo realizada
	 */
	private Discente discente;
	
	/**
	 * Lista de matr�culas para implantar (create)
	 */
	private List<MatriculaComponente> matriculas;
	
	/**
	 * Lista de matr�culas que ja haviam sido implantadas e foram alteradas pelo usu�rio
	 */
	private List<MatriculaComponente> matriculasParaRemover;
	
	/**
	 * Lista de matr�culas implantadas para remover do aluno 
	 */
	private List<MatriculaComponente> matriculasParaAlterar;

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public List<MatriculaComponente> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(List<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

	public List<MatriculaComponente> getMatriculasParaRemover() {
		return matriculasParaRemover;
	}

	public void setMatriculasParaRemover(
			List<MatriculaComponente> matriculasParaRemover) {
		this.matriculasParaRemover = matriculasParaRemover;
	}

	public List<MatriculaComponente> getMatriculasParaAlterar() {
		return matriculasParaAlterar;
	}

	public void setMatriculasParaAlterar( List<MatriculaComponente> matriculasParaAlterar) {
		this.matriculasParaAlterar = matriculasParaAlterar;
	}

	
	
}
