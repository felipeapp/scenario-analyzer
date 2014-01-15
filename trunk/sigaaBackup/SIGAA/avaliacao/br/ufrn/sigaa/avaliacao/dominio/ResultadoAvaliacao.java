/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Cria��o: 21/08/2008 
 */
package br.ufrn.sigaa.avaliacao.dominio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.sigaa.pessoa.dominio.Discente;


/**
 * Classe para guardar os dados do resultado de uma avalia��o institucional.
 * 
 * @author David Pereira
 *
 */
public class ResultadoAvaliacao {

	/** ID da {@link AvaliacaoInstitucional Avalia��o Institucional} correspondente � este resultado.*/
	private int idAvaliacao;
	
	/** Nome do departamento correspondente � este resultado.*/
	private String departamento;
	
	/** Nome do docente correspondente � este resultado.*/
	private String nomeProfessor;
	
	/** Categoria do docente correspondente � este resultado.*/
	private String categoria;
	
	/** Centro correspondente � este resultado.*/
	private String centro;
	
	/** Curso correspondente � este resultado.*/
	private String curso;
	
	/** Lista de turmas correspondente � este resultado.*/
	private List<ResultadoTurma> turmas;
	
	/** N�mero de discentes que realizaram trancamentos correspondente � este resultado.*/
	private int numTrancamentos;
	
	/** Lista de motivos de trancamento dos discentes correspondente � este resultado.*/
	private List<String> motivosTrancamento;

	/** Discente correspondente � este resultado.*/
	private Discente discente;
	
	/** Coment�rios correspondente � este resultado.*/
	private String comentarios;
	
	/** Construtor padr�o. */
	public ResultadoAvaliacao() {
	}
	
	/** Retorna o ID da Avalia��o Institucional correspondente � este resultado.
	 * @return
	 */
	public int getIdAvaliacao() {
		return idAvaliacao;
	}

	/** Seta o ID da Avalia��o Institucional correspondente � este resultado.
	 * @param idAvaliacao
	 */
	public void setIdAvaliacao(int idAvaliacao) {
		this.idAvaliacao = idAvaliacao;
	}

	/** Retorna o centro correspondente � este resultado.
	 * @return
	 */
	public String getCentro() {
		return centro;
	}

	/** Seta o centro correspondente � este resultado.
	 * @param centro
	 */
	public void setCentro(String centro) {
		this.centro = centro;
	}

	/** Retorna o curso correspondente � este resultado.
	 * @return
	 */
	public String getCurso() {
		return curso;
	}

	/** Seta o curso correspondente � este resultado.
	 * @param curso
	 */
	public void setCurso(String curso) {
		this.curso = curso;
	}

	/** Retorna a lista de turmas correspondente � este resultado.
	 * @return
	 */
	public List<ResultadoTurma> getTurmas() {
		return turmas;
	}

	/** Seta a lista de turmas correspondente � este resultado.
	 * @param turmas
	 */
	public void setTurmas(List<ResultadoTurma> turmas) {
		this.turmas = turmas;
	}

	/** Adiciona uma turma � lista de turmas correspondente � este resultado.
	 * @param t
	 */
	public void addTurma(ResultadoTurma t) {
		if (turmas == null)
			turmas = new ArrayList<ResultadoTurma>();
		turmas.add(t);
	}

	/** Retorna o c�digo hash deste resultado. 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idAvaliacao;
		return result;
	}

	/** Compara se este resultado � igual ao passado por par�metro, comparando os IDs das avalia��es institucionais.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultadoAvaliacao other = (ResultadoAvaliacao) obj;
		if (idAvaliacao != other.idAvaliacao)
			return false;
		return true;
	}

	/** Retorna o n�mero de discentes que realizaram trancamentos correspondente � este resultado.
	 * @return
	 */
	public int getNumTrancamentos() {
		return numTrancamentos;
	}

	/** Seta o n�mero de discentes que realizaram trancamentos correspondente � este resultado.
	 * @param numTrancamentos
	 */
	public void setNumTrancamentos(int numTrancamentos) {
		this.numTrancamentos = numTrancamentos;
	}

	/** Retorna a lista de motivos de trancamento dos discentes correspondente � este resultado.
	 * @return
	 */
	public List<String> getMotivosTrancamento() {
		return motivosTrancamento;
	}

	/** Seta a lista de motivos de trancamento dos discentes correspondente � este resultado.
	 * @param motivosTrancamento
	 */
	public void setMotivosTrancamento(List<String> motivosTrancamento) {
		this.motivosTrancamento = motivosTrancamento;
	}

	/** Retorna o discente correspondente � este resultado.
	 * @return
	 */
	public Discente getDiscente() {
		return discente;
	}
	
	/** Seta o discente correspondente � este resultado.
	 * @param discente
	 */
	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	/** Adiciona um motivo � lista de motivos de trancamento dos discentes correspondente � este resultado.
	 * @param motivo
	 */
	public void addMotivoTrancamento(String motivo) {
		if (motivosTrancamento == null)
			motivosTrancamento = new ArrayList<String>();
		motivosTrancamento.add(motivo);
	}

	/** Retorna o nome do departamento correspondente � este resultado.
	 * @return
	 */
	public String getDepartamento() {
		return departamento;
	}

	/** Seta o nome do departamento correspondente � este resultado.
	 * @param departamento
	 */
	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	/** Retorna o nome do docente correspondente � este resultado.
	 * @return
	 */
	public String getNomeProfessor() {
		return nomeProfessor;
	}

	/** Seta o nome do docente correspondente � este resultado.
	 * @param nomeProfessor
	 */
	public void setNomeProfessor(String nomeProfessor) {
		this.nomeProfessor = nomeProfessor;
	}

	/** Retorna a categoria do docente correspondente � este resultado.
	 * @return
	 */
	public String getCategoria() {
		return categoria;
	}

	/** Seta a categoria do docente correspondente � este resultado.
	 * @param categoria
	 */
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	/** Retorna os coment�rios correspondente � este resultado.
	 * @return
	 */
	public String getComentarios() {
		return comentarios;
	}

	/** Seta os coment�rios correspondente � este resultado.
	 * @param comentarios
	 */
	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}
	
	/**
	 * Retorna uma descri��o textual deste resultado no formato: ID da avalia��o
	 * institucional, seguido de v�rgula, seguido do nome do docente, seguido de
	 * v�rgula, seguido do nome do discente.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return idAvaliacao + 
			", " + 
			(nomeProfessor == null || nomeProfessor.isEmpty() ? "sem docente especificado" : nomeProfessor) + 
			", " + 
			(discente != null ? discente.getNome() : " sem discente especificado");
	}
	
}
