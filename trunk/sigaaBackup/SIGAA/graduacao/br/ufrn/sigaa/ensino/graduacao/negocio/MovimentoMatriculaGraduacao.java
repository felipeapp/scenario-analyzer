/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
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
 * Movimento para realizar matr�culas ou pr�-matr�culas de discentes de
 * gradua��o
 *
 * @author ricardo
 *
 */
public class MovimentoMatriculaGraduacao extends MovimentoAcademicoAdapter {

	/** Discente para o qual a matr�cula est� sendo realizada */
	private DiscenteAdapter discente;

	/** Cole��o de turmas nas quais o discente tentar� se matricular */
	private Collection<Turma> turmas;

	/** Restri��es a serem verificadas antes de efetuar a matr�cula */
	private RestricoesMatricula restricoes;

	/** Situa��o que a matr�cula deve ficar ap�s o processamento do movimento */
	private SituacaoMatricula situacao;

	/** Indica se a matr�cula � de um aluno do Ensino a Dist�ncia */
	private boolean matriculaEAD;
	
	/** Indica se a matr�cula � de um aluno de algum conv�nio */
	private boolean matriculaConvenio;
	
	/** Indica se a matr�cula � em turmas de f�rias */
	private boolean matriculaFerias;
	
	/** Indica se o coordenador do programa esta matriculando um discente em turmas de outros programas */
	private boolean matriculandoTurmasOutrosProgramas;
	
	/** Indica se o coordenador do programa esta efetuando matricula de um discente em turmas e indica
	 * tamb�m a possibilidade de duplicidade de matr�cula */
	private boolean permiteDuplicidadeCasoConteudoVariavel = false;
	
	/** Lote de matr�culas de discentes para processamento. Utilizado somente na opera��o de matr�cula em lote */
	private LoteMatriculasDiscente loteMatriculasDiscente;
	
	/** Indica que esta matr�cula � de discente ingressante. */
	private boolean matriculaIngressante = false;
	
	/**
	 * Caso seja falso, durante a matr�cula n�o ir� atualiza os tipos de integraliza��es das matr�culas e o status do discente
	 */
	private boolean atualizarStatusDiscenteETiposIntegralizacao = true;

	/** Representa a matricula do componente criada durante a o processamento de matr�cula. */
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
