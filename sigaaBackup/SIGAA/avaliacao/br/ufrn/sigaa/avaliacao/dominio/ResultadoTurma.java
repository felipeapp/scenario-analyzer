/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Cria��o: 21/08/2008 
 */
package br.ufrn.sigaa.avaliacao.dominio;

import java.math.BigDecimal;
import java.util.TreeSet;

import br.ufrn.sigaa.ensino.dominio.DocenteTurma;

/**
 * Classe para guardar os dados do resultado de uma turma na avalia��o institucional.
 * 
 * @author David Pereira
 *
 */
public class ResultadoTurma {

	/** ID do {@link DocenteTurma docente da turma} referente � este resultado. */
	private int idDocenteTurma;
	/** Nome do docente da turma referente � este resultado. . */
	private String docente;
	/** Nome do componente curricular referente � este resultado. */
	private String disciplina;
	/** C�digo do componente curricular referente � este resultado. */
	private String codigoDisciplina;
	/** C�digo da turma referente � este resultado. */
	private String turma;
	/** Hor�rio da turma referente � este resultado. */
	private String horario;
	/** Local das aulas da turma referente � este resultado. */
	private String local;
	/** Situa��o da matr�cula do discente no componente curricular referente � este resultado. */
	private String situacaoMatricula;
	/** M�dia final no componente curricular referente � este resultado. */
	private BigDecimal mediaFinal;
	/** ID da turma referente � este resultado. */
	private Integer idTurma;
	/** N�mero de faltas do discente nesta turma. */
	private Integer numeroFaltas;
	/** Cole��o de respostas dadas pelo discente referente � este resultado. */
	private TreeSet<ResultadoResposta> dados;
	/** Indica se o discente fez prova de recupera��o.*/
	private String recuperacao;

	/** Construtor padr�o. */
	public ResultadoTurma() {
	}
	
	/** Retorna o ID do  docente da turma referente � este resultado. 
	 * @return
	 */
	public int getIdDocenteTurma() {
		return idDocenteTurma;
	}

	/** Seta o ID do  docente da turma referente � este resultado.
	 * @param idDocenteTurma
	 */
	public void setIdDocenteTurma(int idDocenteTurma) {
		this.idDocenteTurma = idDocenteTurma;
	}

	/** Retorna a cole��o de respostas dadas pelo discente referente � este resultado. 
	 * @return
	 */
	public TreeSet<ResultadoResposta> getDados() {
		return dados;
	}

	/** Seta a cole��o de respostas dadas pelo discente referente � este resultado.
	 * @param dados
	 */
	public void setDados(TreeSet<ResultadoResposta> dados) {
		this.dados = dados;
	}

	/** Adiciona um resultado � cole��o de respostas dadas pelo discente referente � este resultado.
	 * @param resultado
	 */
	public void add(ResultadoResposta resultado) {
		if (dados == null)
			dados = new TreeSet<ResultadoResposta>();
		dados.add(resultado);
	}
	
	/** Retorna o nome do docente da turma referente � este resultado. . 
	 * @return
	 */
	public String getDocente() {
		return docente;
	}

	/** Seta o nome do docente da turma referente � este resultado. .
	 * @param docente
	 */
	public void setDocente(String docente) {
		this.docente = docente;
	}

	/** Retorna o nome do componente curricular referente � este resultado. 
	 * @return
	 */
	public String getDisciplina() {
		return disciplina;
	}

	/** Seta o nome do componente curricular referente � este resultado.
	 * @param disciplina
	 */
	public void setDisciplina(String disciplina) {
		this.disciplina = disciplina;
	}

	/** Retorna o c�digo do componente curricular referente � este resultado. 
	 * @return
	 */
	public String getCodigoDisciplina() {
		return codigoDisciplina;
	}

	/** Seta o c�digo do componente curricular referente � este resultado.
	 * @param codigoDisciplina
	 */
	public void setCodigoDisciplina(String codigoDisciplina) {
		this.codigoDisciplina = codigoDisciplina;
	}

	/** Retorna o c�digo da turma referente � este resultado. 
	 * @return
	 */
	public String getTurma() {
		return turma;
	}

	/** Seta o c�digo da turma referente � este resultado.
	 * @param turma
	 */
	public void setTurma(String turma) {
		this.turma = turma;
	}

	/** Retorna o hor�rio da turma referente � este resultado. 
	 * @return
	 */
	public String getHorario() {
		return horario;
	}

	/** Seta o hor�rio da turma referente � este resultado.
	 * @param horario
	 */
	public void setHorario(String horario) {
		this.horario = horario;
	}

	/** Retorna o local das aulas da turma referente � este resultado. 
	 * @return
	 */
	public String getLocal() {
		return local;
	}

	/** Seta o local das aulas da turma referente � este resultado.
	 * @param local
	 */
	public void setLocal(String local) {
		this.local = local;
	}

	/** Retorna a situa��o da matr�cula do discente no componente curricular referente � este resultado.
	 * @return
	 */
	public String getSituacaoMatricula() {
		return situacaoMatricula;
	}

	/** Seta a situa��o da matr�cula do discente no componente curricular referente � este resultado. 
	 * @param situacaoMatricula
	 */
	public void setSituacaoMatricula(String situacaoMatricula) {
		this.situacaoMatricula = situacaoMatricula;
	}

	/** Retorna a m�dia final no componente curricular referente � este resultado.
	 * @return
	 */
	public BigDecimal getMediaFinal() {
		return mediaFinal;
	}

	/** Seta a m�dia final no componente curricular referente � este resultado. 
	 * @param mediaFinal
	 */
	public void setMediaFinal(BigDecimal mediaFinal) {
		this.mediaFinal = mediaFinal;
	}

	/** Retorna o ID da turma referente � este resultado.
	 * @return
	 */
	public Integer getIdTurma() {
		return idTurma;
	}

	/** Seta o ID da turma referente � este resultado. 
	 * @param idTurma
	 */
	public void setIdTurma(Integer idTurma) {
		this.idTurma = idTurma;
	}

	/** Retorna o n�mero de faltas do discente nesta turma.  
	 * @return
	 */
	public Integer getNumeroFaltas() {
		return numeroFaltas;
	}

	/** SEta o n�mero de faltas do discente nesta turma.
	 * @param numeroFaltas
	 */
	public void setNumeroFaltas(Integer numeroFaltas) {
		this.numeroFaltas = numeroFaltas;
	}

	/** Retorna o c�digo hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idDocenteTurma;
		return result;
	}

	/** Indica se este objeto � igual ao passado por par�metro, comparando o ID do docente da turma.
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
		ResultadoTurma other = (ResultadoTurma) obj;
		if (idDocenteTurma != other.idDocenteTurma)
			return false;
		return true;
	}

	/** Indica se o discente fez prova de recupera��o.
	 * @return
	 */
	public String getRecuperacao() {
		return recuperacao;
	}

	/** Seta se o discente fez prova de recupera��o.
	 * @param recuperacao
	 */
	public void setRecuperacao(String recuperacao) {
		this.recuperacao = recuperacao;
	}
	
	
}
