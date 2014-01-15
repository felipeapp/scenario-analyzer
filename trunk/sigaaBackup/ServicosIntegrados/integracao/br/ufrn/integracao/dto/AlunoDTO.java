/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 11/04/2011
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Data Transfer Object para a troca de informações
 * de alunos entre sistemas.
 * 
 * @author David Pereira
 *
 */
public class AlunoDTO implements Serializable {

	private static final long serialVersionUID = -1L;
	
	private int id;
	
	private PessoaDto pessoa;
	
	private CursoDTO curso;
	
	private int idBolsa;
	
	private String matricula;
	
	private Boolean carente;
	
	private Date inicioBolsa;
	
	private Date fimBolsa;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PessoaDto getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaDto pessoa) {
		this.pessoa = pessoa;
	}

	public CursoDTO getCurso() {
		return curso;
	}

	public void setCurso(CursoDTO curso) {
		this.curso = curso;
	}

	public int getIdBolsa() {
		return idBolsa;
	}

	public void setIdBolsa(int idBolsa) {
		this.idBolsa = idBolsa;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	
	public Boolean getCarente() {
		return carente;
	}

	public void setCarente(Boolean carente) {
		this.carente = carente;
	}

	public Date getInicioBolsa() {
		return inicioBolsa;
	}

	public void setInicioBolsa(Date inicioBolsa) {
		this.inicioBolsa = inicioBolsa;
	}

	public Date getFimBolsa() {
		return fimBolsa;
	}

	public void setFimBolsa(Date fimBolsa) {
		this.fimBolsa = fimBolsa;
	}

	public String getNomeAluno(){
		if (pessoa != null) {
			return pessoa.getNome();
		}

		return "";
	}
	
	public String getDescricao() {
		return matricula + " - " + pessoa.getNome() + " - " + curso.getDenominacao();
	}

	/** Retorna Matricula do aluno concatenado com nome */
	public String getMatriculaNomeFormatado() {
		return "<div style='border-bottom: dashed 1px; padding: 2px'><div style='width:60px; float: left; text-align: right'>" + getMatricula() + "&nbsp;&nbsp;</div><div style='margin-left: 100px'>" + getNomeAluno() + "</div></div>";
	}
	
}
