/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 21/08/2008 
 */
package br.ufrn.sigaa.avaliacao.dominio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.sigaa.pessoa.dominio.Discente;


/**
 * Classe para guardar os dados do resultado de uma avaliação institucional.
 * 
 * @author David Pereira
 *
 */
public class ResultadoAvaliacao {

	/** ID da {@link AvaliacaoInstitucional Avaliação Institucional} correspondente à este resultado.*/
	private int idAvaliacao;
	
	/** Nome do departamento correspondente à este resultado.*/
	private String departamento;
	
	/** Nome do docente correspondente à este resultado.*/
	private String nomeProfessor;
	
	/** Categoria do docente correspondente à este resultado.*/
	private String categoria;
	
	/** Centro correspondente à este resultado.*/
	private String centro;
	
	/** Curso correspondente à este resultado.*/
	private String curso;
	
	/** Lista de turmas correspondente à este resultado.*/
	private List<ResultadoTurma> turmas;
	
	/** Número de discentes que realizaram trancamentos correspondente à este resultado.*/
	private int numTrancamentos;
	
	/** Lista de motivos de trancamento dos discentes correspondente à este resultado.*/
	private List<String> motivosTrancamento;

	/** Discente correspondente à este resultado.*/
	private Discente discente;
	
	/** Comentários correspondente à este resultado.*/
	private String comentarios;
	
	/** Construtor padrão. */
	public ResultadoAvaliacao() {
	}
	
	/** Retorna o ID da Avaliação Institucional correspondente à este resultado.
	 * @return
	 */
	public int getIdAvaliacao() {
		return idAvaliacao;
	}

	/** Seta o ID da Avaliação Institucional correspondente à este resultado.
	 * @param idAvaliacao
	 */
	public void setIdAvaliacao(int idAvaliacao) {
		this.idAvaliacao = idAvaliacao;
	}

	/** Retorna o centro correspondente à este resultado.
	 * @return
	 */
	public String getCentro() {
		return centro;
	}

	/** Seta o centro correspondente à este resultado.
	 * @param centro
	 */
	public void setCentro(String centro) {
		this.centro = centro;
	}

	/** Retorna o curso correspondente à este resultado.
	 * @return
	 */
	public String getCurso() {
		return curso;
	}

	/** Seta o curso correspondente à este resultado.
	 * @param curso
	 */
	public void setCurso(String curso) {
		this.curso = curso;
	}

	/** Retorna a lista de turmas correspondente à este resultado.
	 * @return
	 */
	public List<ResultadoTurma> getTurmas() {
		return turmas;
	}

	/** Seta a lista de turmas correspondente à este resultado.
	 * @param turmas
	 */
	public void setTurmas(List<ResultadoTurma> turmas) {
		this.turmas = turmas;
	}

	/** Adiciona uma turma à lista de turmas correspondente à este resultado.
	 * @param t
	 */
	public void addTurma(ResultadoTurma t) {
		if (turmas == null)
			turmas = new ArrayList<ResultadoTurma>();
		turmas.add(t);
	}

	/** Retorna o código hash deste resultado. 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idAvaliacao;
		return result;
	}

	/** Compara se este resultado é igual ao passado por parâmetro, comparando os IDs das avaliações institucionais.
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

	/** Retorna o número de discentes que realizaram trancamentos correspondente à este resultado.
	 * @return
	 */
	public int getNumTrancamentos() {
		return numTrancamentos;
	}

	/** Seta o número de discentes que realizaram trancamentos correspondente à este resultado.
	 * @param numTrancamentos
	 */
	public void setNumTrancamentos(int numTrancamentos) {
		this.numTrancamentos = numTrancamentos;
	}

	/** Retorna a lista de motivos de trancamento dos discentes correspondente à este resultado.
	 * @return
	 */
	public List<String> getMotivosTrancamento() {
		return motivosTrancamento;
	}

	/** Seta a lista de motivos de trancamento dos discentes correspondente à este resultado.
	 * @param motivosTrancamento
	 */
	public void setMotivosTrancamento(List<String> motivosTrancamento) {
		this.motivosTrancamento = motivosTrancamento;
	}

	/** Retorna o discente correspondente à este resultado.
	 * @return
	 */
	public Discente getDiscente() {
		return discente;
	}
	
	/** Seta o discente correspondente à este resultado.
	 * @param discente
	 */
	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	/** Adiciona um motivo à lista de motivos de trancamento dos discentes correspondente à este resultado.
	 * @param motivo
	 */
	public void addMotivoTrancamento(String motivo) {
		if (motivosTrancamento == null)
			motivosTrancamento = new ArrayList<String>();
		motivosTrancamento.add(motivo);
	}

	/** Retorna o nome do departamento correspondente à este resultado.
	 * @return
	 */
	public String getDepartamento() {
		return departamento;
	}

	/** Seta o nome do departamento correspondente à este resultado.
	 * @param departamento
	 */
	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	/** Retorna o nome do docente correspondente à este resultado.
	 * @return
	 */
	public String getNomeProfessor() {
		return nomeProfessor;
	}

	/** Seta o nome do docente correspondente à este resultado.
	 * @param nomeProfessor
	 */
	public void setNomeProfessor(String nomeProfessor) {
		this.nomeProfessor = nomeProfessor;
	}

	/** Retorna a categoria do docente correspondente à este resultado.
	 * @return
	 */
	public String getCategoria() {
		return categoria;
	}

	/** Seta a categoria do docente correspondente à este resultado.
	 * @param categoria
	 */
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	/** Retorna os comentários correspondente à este resultado.
	 * @return
	 */
	public String getComentarios() {
		return comentarios;
	}

	/** Seta os comentários correspondente à este resultado.
	 * @param comentarios
	 */
	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}
	
	/**
	 * Retorna uma descrição textual deste resultado no formato: ID da avaliação
	 * institucional, seguido de vírgula, seguido do nome do docente, seguido de
	 * vírgula, seguido do nome do discente.
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
