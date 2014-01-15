package br.ufrn.sigaa.ead.resources;

import java.util.ArrayList;
import java.util.List;

public class ComponenteCurricularEadDTO {
	
	private int id;
	private String codigo;
	private String nome;
	private String ementa;
	private int chAula;
	private int chLaboratorio;
	private int chEstagio;
	private int idUnidadeResponsavel;
	private int idTipoComponenteCurricular;
	private List <TurmaEadDTO> turmas = new ArrayList <TurmaEadDTO> ();
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getEmenta() {
		return ementa;
	}
	public void setEmenta(String ementa) {
		this.ementa = ementa;
	}
	public int getChAula() {
		return chAula;
	}
	public void setChAula(int chAula) {
		this.chAula = chAula;
	}
	public int getChLaboratorio() {
		return chLaboratorio;
	}
	public void setChLaboratorio(int chLaboratorio) {
		this.chLaboratorio = chLaboratorio;
	}
	public int getChEstagio() {
		return chEstagio;
	}
	public void setChEstagio(int chEstagio) {
		this.chEstagio = chEstagio;
	}
	public int getIdUnidadeResponsavel() {
		return idUnidadeResponsavel;
	}
	public void setIdUnidadeResponsavel(int idUnidadeResponsavel) {
		this.idUnidadeResponsavel = idUnidadeResponsavel;
	}
	public int getIdTipoComponenteCurricular() {
		return idTipoComponenteCurricular;
	}
	public void setIdTipoComponenteCurricular(int idTipoComponenteCurricular) {
		this.idTipoComponenteCurricular = idTipoComponenteCurricular;
	}
	public List<TurmaEadDTO> getTurmas() {
		return turmas;
	}
	public void setTurmas(List<TurmaEadDTO> turmas) {
		this.turmas = turmas;
	}
	
}
