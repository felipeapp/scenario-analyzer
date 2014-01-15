/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * 01/06/2007
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;


/**
 * Classe auxiliar para gerar relat�rio de alunos com matr�cula pendente
 * @author leonardo
 *
 */
public class LinhaRelatorioAlunosPendenteMatricula {
	/**Nome do discente.*/
	private String nome;
	/**Email do discente.*/
	private String email;
	/**Matr�cula do discente.*/
	private long matricula;
	/**Total de discentes.*/
	private long total;
	/**Ano de ingresso.*/
	private int ano;
	/**Per�odo de ingresso.*/
	private int periodo;

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public LinhaRelatorioAlunosPendenteMatricula(){
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public long getMatricula() {
		return matricula;
	}

	public void setMatricula(long matricula) {
		this.matricula = matricula;
	}
	
	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
		
}
