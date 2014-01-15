package br.ufrn.academico.dominio;

import java.util.Hashtable;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.comum.dominio.PessoaGeral;

/**
 *
 * Entidade que representa um discente da UFRN
 *
 * @author Gleydson Lima
 *
 */

public class Aluno implements PersistDB {
	
	/**
	 * Níveis possíveis para um aluno
	 */
	/**
	 * Aluno de graduação.
	 */
	public static final String NIVEL_GRADUACAO 		= "G";
	/**
	 * Aluno de mestrado.
	 */
	public static final String NIVEL_MESTRADO 		= "E";
	/**
	 * Aluno de doutorado.
	 */
	public static final String NIVEL_DOUTORADO 		= "D";
	/**
	 * Aluno de curso técnico.
	 */
	public static final String NIVEL_TECNICO 		= "T";
	/**
	 * Aluno de Lato Sensu.
	 */
	public static final String NIVEL_LATO_SENSU 	= "L";
	/**
	 * Aluno de Stricto Sensu.
	 */
	public static final String NIVEL_STRICTO_SENSU  = "S";
	
	
	public static final Integer[] SITUACOES_ALUNOS_ATIVOS_SIPAC = new Integer[]{StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO};
	
	/** Identificador */
	private int id;

	/** Matrícula do Aluno **/
	private long matricula;

	/** Ano e semestre inicial do aluno no curso */
	private Integer anoSemestreInicial;

	/** Nome do aluno **/
	private String nome;

	/** Status dele. A - Ativo, C - Concluído, N - Cancelado, T - Trancado */
	@Deprecated //(O que vale é idStatus)
	private String status;

	/** Identificador referente ao status do aluno no SIGAA */
	private Integer idStatus;
	
	/** G - Graduação, P - Pós-Graduação (Inclui mestrado, doutorado, especialização, etc), O - Outras instituições */
	private String tipo;
	
	/** Nível que vem do SIGAA: G - Graduação, E - Mestrado, D - Doutorado, T - Técnico */
	private String nivel;

	/** O aluno tem uma pessoa se tiver um CPF */
	private PessoaGeral pessoa;

	/** Referência ao curso ao qual o aluno pertence **/
	private Curso curso;
	
	/** Dado não persistido */
	private String senha;
	
	/** Identificador referente ao discente no SIGAA */
	private Integer idDiscente;
	
	/** Tipo de aluno - (1) Regular e (2) Especial */
	private Integer tipoAluno;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getMatricula() {
		return matricula;
	}

	public void setMatricula(long matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}	

	public String getNomeMatricula() {
		return nome + " (" + matricula + ")";
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public PessoaGeral getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaGeral pessoa) {
		this.pessoa = pessoa;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Deprecated
	public String getStatus() {
		return status;
	}

	@Deprecated
	public void setStatus(String status) {
		this.status = status;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getIdDiscente() {
		return idDiscente;
	}

	public void setIdDiscente(Integer idDiscente) {
		this.idDiscente = idDiscente;
	}

	public Integer getTipoAluno() {
		return tipoAluno;
	}

	public void setTipoAluno(Integer tipoAluno) {
		this.tipoAluno = tipoAluno;
	}

	/**
	 * Retorna representação XML do Aluno. Usado em consulta AJAX.
	 *
	 * @see br.ufrn.sipac.bolsas.struts.AjaxAlunoAction
	 * @return representação XML do Aluno.
	 */
	public final String toXML() {
		StringBuilder s = new StringBuilder(600);

		s.append("<ALUNO>\n");
		s.append("<ID>" + id + "</ID>\n");
		s.append("<NOME>" + nome + "</NOME>\n");
		s.append("<MATRICULA>" + matricula + "</MATRICULA>\n");
		s.append("<ID_CURSO>" + (curso != null? curso.getId(): -1) + "</ID_CURSO>\n");
		s.append((pessoa != null ? pessoa.toXML() : new PessoaGeral().toXML())
				+ "\n");
		s.append("</ALUNO>");

		return s.toString();
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public String getDescricaoTipo(String tipo) {
		return tabelaTipo.get(tipo);
	}

	/**
	 * Tabela de valores (P: Pós-Graduação, G: Graduação, O: Outros)
	 */
	private static Hashtable<String, String> tabelaTipo = new Hashtable<String, String>();

	// Inicializa tabela de valores
	static {
		tabelaTipo.put("P", "Pós-Graduação");
		tabelaTipo.put("G", "Graduação");
		tabelaTipo.put("O", "Outros");
	}

	public String getDescricaoStatus(String status) {
		return tabelaStatus.get(status);
	}

	/**
	 * Tabela com status do aluno.
	 * C: Concluído, N: Cancelado, A: Ativo, T: Trancado.
	 */
	private static Hashtable<String, String> tabelaStatus = new Hashtable<String, String>();

	// Inicializa tabela de valores
	static {
		tabelaStatus.put("C", "Concluído");
		tabelaStatus.put("N", "Cancelado");
		tabelaStatus.put("A", "Ativo");
		tabelaStatus.put("T", "Trancado");
	}

	public Integer getAnoSemestreInicial() {
		return anoSemestreInicial;
	}

	public void setAnoSemestreInicial(Integer anoSemestreInicial) {
		this.anoSemestreInicial = anoSemestreInicial;
	}

	public Integer getIdStatus() {
		return idStatus;
	}

	public void setIdStatus(Integer idStatus) {
		this.idStatus = idStatus;
	}

	public String getDescricaoStatus(){
		String resultado = "";
		if(status != null){
			if(status.equals("C"))
				resultado = "CONCLUÍDO";
			else if(status.equals("N"))
				resultado = "CANCELADO";
			else if(status.equals("A"))
				resultado = "ATIVO";
			else if(status.equals("T"))
				resultado = "TRANCADO";
		}
		return resultado;
	}
	
	/**
	 * Descrição dos status do aluno
	 * @return
	 */
	public String getDescricaoIdStatus() {
		if(idStatus == null)
			return "";
		if(idStatus == StatusDiscente.ATIVO)
			return "ATIVO";
		else if(idStatus == StatusDiscente.CADASTRADO)
			return "CADASTRADO";
		else if(idStatus == StatusDiscente.FORMANDO)
			return "FORMANDO";
		else if(idStatus == StatusDiscente.GRADUANDO)
			return "GRADUANDO";
		else if(idStatus == StatusDiscente.CONCLUIDO)
			return "CONCLUÍDO";
		else if(idStatus == StatusDiscente.CANCELADO)
			return "CANCELADO";
		else if(idStatus == StatusDiscente.JUBILADO)
			return "JUBILADO";
		else if(idStatus == StatusDiscente.EXCLUIDO)
			return "EXCLUIDO";
		else 
			return "";
	}
	
	public boolean isAtivo(){
		return (idStatus != null && (idStatus == StatusDiscente.ATIVO || idStatus == StatusDiscente.FORMANDO || idStatus == StatusDiscente.GRADUANDO));
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}	
	
	public String getTipoStr(){
		return tabelaTipo.get(tipo);
	}
}