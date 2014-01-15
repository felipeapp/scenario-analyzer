/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 07/01/2008 
 *
 */

package br.ufrn.sigaa.processamento.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;

/**
 * Classe para comparar uma matrícula com as outras da turma
 * e armazenar o resultado do seu processamento.
 * 
 * @author David Pereira
 *
 */
public class MatriculaEmProcessamento implements Comparable<MatriculaEmProcessamento> {

	public static final int VESTIBULAR = 7;
	public static final int NIVELADO = 6;
	public static final int FORMANDO = 5;
	public static final int RECUPERACAO = 4;
	public static final int ADIANTADO = 3;
	public static final int ELETIVO = 2;
	public static final int OUTROS = 1;

	public static final int FORMANDO_FERIAS = 6;
	public static final int RECUPERACAO_FERIAS = 5; 
	public static final int NIVELADO_FERIAS = 4;

	/** Matriz curricular do aluno cuja matrícula está sendo processada */
	private int idMatrizCurricular;
	
	/** Id da reserva para a matriz curricular na turma */
	private int idMatrizReserva;
	
	/**
	 *  7. Aluno recém ingresso através de vestibular (V)
	 *  6. Aluno nivelado (N)
	 *  5. Aluno formando (F)
	 *  4. Aluno em recuperação (R)
	 *  3. Aluno adiantado (A)
	 *  2. Aluno pagando componente eletivo (E)
	 *  1. Outros
	 */
	private int tipo;
	
	/** Id da matrícula que está sendo processada */
	private int idMatriculaComponente;

	/** Id do aluno que está associado à matrícula sendo processada */
	private int idDiscente;
	
	/** Ordem da matrícula na turma */
	private int ordem;
	
	/** Valor do Índice acadêmico utilizado para o desempate do aluno */
	private double indice;
	
	/** Situação da matrícula após o processamento */
	private SituacaoMatricula situacao;
	
	/** Matrícula do aluno */
	private long matricula;
	
	/** Nome do aluno */
	private String nome;
	
	/** Curso do aluno */
	private String curso;
	
	/** Indeferimento do aluno em turmas de bloco ou de co-requisitos */
	private List<String> indeferimentos;
	
	/** Motivo do indeferimento */
	private String motivo;

	public MatriculaEmProcessamento() { }
	
	public MatriculaEmProcessamento(int tipo, double indice) {
		this.tipo = tipo;
		this.indice = indice;
	}
	
	public MatriculaEmProcessamento(int tipo, double iea, int matriz) {
		this(tipo, iea);
		this.idMatrizCurricular = matriz;
		this.idMatrizReserva = matriz;
	}
	
	public int getIdMatrizCurricular() {
		return idMatrizCurricular;
	}

	public void setIdMatrizCurricular(int idMatrizCurricular) {
		this.idMatrizCurricular = idMatrizCurricular;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public int getIdMatriculaComponente() {
		return idMatriculaComponente;
	}

	public void setIdMatriculaComponente(int idMatriculaComponente) {
		this.idMatriculaComponente = idMatriculaComponente;
	}

	public int getIdDiscente() {
		return idDiscente;
	}

	public void setIdDiscente(int idDiscente) {
		this.idDiscente = idDiscente;
	}

	public double getIndice() {
		return indice;
	}

	public void setIndice(double indice) {
		this.indice = indice;
	}

	public SituacaoMatricula getSituacao() {
		return situacao;
	}

	public String getDescSituacao() {
		if (isEmpty(motivo)) {
			return situacao.getDescricao();
		} else {
			return motivo.toUpperCase();
		}
	}
	
	public void setSituacao(SituacaoMatricula situacao) {
		this.situacao = situacao;
	}

	/**
	 * Compara uma matrícula com as outras dentro de uma mesma reserva. Os critérios
	 * de ordenação estão definidos no Regulamento dos cursos de graduação e são os seguintes:
	 * 
	 *  1. Aluno recém ingresso através de vestibular (V)
	 *  2. Aluno nivelado (N)
	 *  3. Aluno formando (F)
	 *  4. Aluno em recuperação (R)
	 *  5. Aluno adiantado (A)
	 *  6. Aluno pagando componente eletivo (E)
	 */
	public int compareTo(MatriculaEmProcessamento o) {
		return new ProcessamentoGraduacaoComparator().compare(this, o);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idMatriculaComponente;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MatriculaEmProcessamento other = (MatriculaEmProcessamento) obj;
		if (idMatriculaComponente != other.idMatriculaComponente)
			return false;
		return true;
	}

	
	
	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Matricula { ");
		sb.append("situacao = " + situacao + ", ");
		sb.append("ordem = " + ordem + ", ");
		sb.append("tipo = " + tipo + ", ");
		sb.append("indice = " + indice + ", ");
		sb.append("idMatrizCurricular = " + idMatrizCurricular + ", ");
		sb.append("idSolicitacaoMatricula = " + idMatriculaComponente + ", ");
		sb.append("idDiscente = " + idDiscente + "}");
		return sb.toString();
	}

	public long getMatricula() {
		return matricula;
	}

	public void setMatricula(long matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return StringUtils.toAscii(nome);
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCurso() {
		return StringUtils.toAscii(curso);
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public int getIdMatrizReserva() {
		return idMatrizReserva;
	}

	public void setIdMatrizReserva(int idMatrizReserva) {
		this.idMatrizReserva = idMatrizReserva;
	}

	public List<String> getIndeferimentos() {
		return indeferimentos;
	}

	public void setIndeferimentos(List<String> indeferimentos) {
		this.indeferimentos = indeferimentos;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}	
	
	public String getMotivo() {
		return motivo;
	}
	
}
