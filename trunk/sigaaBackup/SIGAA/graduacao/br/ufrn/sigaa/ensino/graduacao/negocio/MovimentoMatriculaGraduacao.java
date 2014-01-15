/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.Collection;

import br.ufrn.sigaa.arq.dominio.MovimentoAcademicoAdapter;
import br.ufrn.sigaa.ead.dominio.LoteMatriculasDiscente;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.RestricoesMatricula;

/**
 * Movimento para realizar matrículas ou pré-matrículas de discentes de
 * graduação
 *
 * @author ricardo
 *
 */
public class MovimentoMatriculaGraduacao extends MovimentoAcademicoAdapter {

	/** Discente para o qual a matrícula está sendo realizada */
	private DiscenteAdapter discente;

	/** Coleção de turmas nas quais o discente tentará se matricular */
	private Collection<Turma> turmas;

	/** Restrições a serem verificadas antes de efetuar a matrícula */
	private RestricoesMatricula restricoes;

	/** Situação que a matrícula deve ficar após o processamento do movimento */
	private SituacaoMatricula situacao;

	/** Indica se a matrícula é de um aluno do Ensino a Distância */
	private boolean matriculaEAD;
	
	/** Indica se a matrícula é de um aluno de algum convênio */
	private boolean matriculaConvenio;
	
	/** Indica se a matrícula é em turmas de férias */
	private boolean matriculaFerias;
	
	/** Indica se o coordenador do programa esta matriculando um discente em turmas de outros programas */
	private boolean matriculandoTurmasOutrosProgramas;
	
	/** Indica se o coordenador do programa esta efetuando matricula de um discente em turmas e indica
	 * também a possibilidade de duplicidade de matrícula */
	private boolean permiteDuplicidadeCasoConteudoVariavel = false;
	
	/** Lote de matrículas de discentes para processamento. Utilizado somente na operação de matrícula em lote */
	private LoteMatriculasDiscente loteMatriculasDiscente;
	
	/** Indica que esta matrícula é de discente ingressante. */
	private boolean matriculaIngressante = false;
	
	/**
	 * Caso seja falso, durante a matrícula não irá atualiza os tipos de integralizações das matrículas e o status do discente
	 */
	private boolean atualizarStatusDiscenteETiposIntegralizacao = true;

	/** Representa a matricula do componente criada durante a o processamento de matrícula. */
	private MatriculaComponente matriculaGerada;
	
	public boolean isMatriculaEAD() {
		return matriculaEAD;
	}

	public void setMatriculaEAD(boolean matriculaEAD) {
		this.matriculaEAD = matriculaEAD;
	}

	public MovimentoMatriculaGraduacao() {

	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public Collection<Turma> getTurmas() {
		return turmas;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	public RestricoesMatricula getRestricoes() {
		return restricoes;
	}

	public void setRestricoes(RestricoesMatricula restricoes) {
		this.restricoes = restricoes;
	}

	public SituacaoMatricula getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoMatricula situacao) {
		this.situacao = situacao;
	}

	public boolean isMatriculaConvenio() {
		return matriculaConvenio;
	}

	public void setMatriculaConvenio(boolean matriculaConvenio) {
		this.matriculaConvenio = matriculaConvenio;
	}

	public LoteMatriculasDiscente getLoteMatriculasDiscente() {
		return loteMatriculasDiscente;
	}

	public void setLoteMatriculasDiscente(
			LoteMatriculasDiscente loteMatriculasDiscente) {
		this.loteMatriculasDiscente = loteMatriculasDiscente;
	}

	public boolean isAtualizarStatusDiscenteETiposIntegralizacao() {
		return atualizarStatusDiscenteETiposIntegralizacao;
	}

	public void setAtualizarStatusDiscenteETiposIntegralizacao(
			boolean atualizarStatusDiscenteETiposIntegralizacao) {
		this.atualizarStatusDiscenteETiposIntegralizacao = atualizarStatusDiscenteETiposIntegralizacao;
	}

	public boolean isMatriculaFerias() {
		return matriculaFerias;
	}

	public void setMatriculaFerias(boolean matriculaFerias) {
		this.matriculaFerias = matriculaFerias;
	}

	public boolean isMatriculandoTurmasOutrosProgramas() {
		return matriculandoTurmasOutrosProgramas;
	}

	public void setMatriculandoTurmasOutrosProgramas(
			boolean matriculandoTurmasOutrosProgramas) {
		this.matriculandoTurmasOutrosProgramas = matriculandoTurmasOutrosProgramas;
	}

	public boolean isPermiteDuplicidadeCasoConteudoVariavel() {
		return permiteDuplicidadeCasoConteudoVariavel;
	}

	public void setPermiteDuplicidadeCasoConteudoVariavel(
			boolean permiteDuplicidadeCasoConteudoVariavel) {
		this.permiteDuplicidadeCasoConteudoVariavel = permiteDuplicidadeCasoConteudoVariavel;
	}

	public MatriculaComponente getMatriculaGerada() {
		return matriculaGerada;
	}

	public void setMatriculaGerada(MatriculaComponente matriculaGerada) {
		this.matriculaGerada = matriculaGerada;
	}

	public boolean isMatriculaIngressante() {
		return matriculaIngressante;
	}

	public void setMatriculaIngressante(boolean matriculaIngressante) {
		this.matriculaIngressante = matriculaIngressante;
	}
	
	

}
