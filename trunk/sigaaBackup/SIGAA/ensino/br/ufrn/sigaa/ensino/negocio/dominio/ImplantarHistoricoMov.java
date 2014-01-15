/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
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
 * Movimento da operação de implantar histórico 
 * @author Victor Hugo
 *
 */
public class ImplantarHistoricoMov extends AbstractMovimentoAdapter {

	/**
	 * Discente da implantação que está sendo realizada
	 */
	private Discente discente;
	
	/**
	 * Lista de matrículas para implantar (create)
	 */
	private List<MatriculaComponente> matriculas;
	
	/**
	 * Lista de matrículas que ja haviam sido implantadas e foram alteradas pelo usuário
	 */
	private List<MatriculaComponente> matriculasParaRemover;
	
	/**
	 * Lista de matrículas implantadas para remover do aluno 
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
