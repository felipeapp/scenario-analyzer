/**
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * 28/11/2007
 */
package br.ufrn.sigaa.ensino.tecnico.relatorios;

/**
 * Classe auxiliar utilizada para gerar o relatório de aprovados e reprovados
 * por disciplina no ensino técnico.
 * 
 * @author leonardo
 *
 */
public class LinhaRelatorioAprovadosReprovados {

	private int id;
	private String codigo;
	private String nome;
	private int matriculados = 0;
	private int aprovados = 0;
	private int reprovados = 0;
	private int trancados = 0;
	private int turmas = 0;
	private int turmaAtual = 0;
	
	public LinhaRelatorioAprovadosReprovados(){
		
	}

	public int getAprovados() {
		return aprovados;
	}

	public void setAprovados(int aprovados) {
		this.aprovados = aprovados;
	}

	public int getReprovados() {
		return reprovados;
	}

	public void setReprovados(int reprovados) {
		this.reprovados = reprovados;
	}

	public int getTrancados() {
		return trancados;
	}

	public void setTrancados(int trancados) {
		this.trancados = trancados;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMatriculados() {
		return matriculados;
	}

	public void setMatriculados(int matriculados) {
		this.matriculados = matriculados;
	}

	public int getTurmas() {
		return turmas;
	}

	public void setTurmas(int turmas) {
		this.turmas = turmas;
	}

	public int getTurmaAtual() {
		return turmaAtual;
	}

	public void setTurmaAtual(int turmaAtual) {
		this.turmaAtual = turmaAtual;
	}
	
	
}
