package br.ufrn.sigaa.ensino_rede.resources;

import java.util.Collection;

public class MatriculaComponenteRedeDTO {
	private int id;
	private String nomeDiscente;
	private String cpfDiscente;
	private String emailDiscente;
	private String siglaIes;
	private String campusIes;
	private String nomeCurso;
	private int idComponente;
	private String codigoComponente;
	private String nomeComponente;
	private String codigoTurma;
	private String situacaoMatricula;
	private int ano;
	private int periodo;
	private Long dataInicio;
	private Long dataFim;
	private Collection<String> nomesDocentes;
	private Collection<String> cpfsDocentes;
	private Collection<String> emailsDocentes;
	
	public String getNomeDiscente() {
		return nomeDiscente;
	}
	public void setNomeDiscente(String nomeDiscente) {
		this.nomeDiscente = nomeDiscente;
	}
	public String getCpfDiscente() {
		return cpfDiscente;
	}
	public void setCpfDiscente(String cpfDiscente) {
		this.cpfDiscente = cpfDiscente;
	}
	public String getCodigoComponente() {
		return codigoComponente;
	}
	public void setCodigoComponente(String codigoComponente) {
		this.codigoComponente = codigoComponente;
	}
	public String getCodigoTurma() {
		return codigoTurma;
	}
	public void setCodigoTurma(String codigoTurma) {
		this.codigoTurma = codigoTurma;
	}
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
	public Long getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Long dataInicio) {
		this.dataInicio = dataInicio;
	}
	public Long getDataFim() {
		return dataFim;
	}
	public void setDataFim(Long dataFim) {
		this.dataFim = dataFim;
	}
	public Collection<String> getNomesDocentes() {
		return nomesDocentes;
	}
	public void setNomesDocentes(Collection<String> nomesDocentes) {
		this.nomesDocentes = nomesDocentes;
	}
	public Collection<String> getCpfsDocentes() {
		return cpfsDocentes;
	}
	public void setCpfsDocentes(Collection<String> cpfsDocentes) {
		this.cpfsDocentes = cpfsDocentes;
	}
	public String getSiglaIes() {
		return siglaIes;
	}
	public void setSiglaIes(String siglaIes) {
		this.siglaIes = siglaIes;
	}
	public String getCampusIes() {
		return campusIes;
	}
	public void setCampusIes(String campusIes) {
		this.campusIes = campusIes;
	}
	public String getNomeCurso() {
		return nomeCurso;
	}
	public void setNomeCurso(String nomeCurso) {
		this.nomeCurso = nomeCurso;
	}
	public String getSituacaoMatricula() {
		return situacaoMatricula;
	}
	public void setSituacaoMatricula(String situacaoMatricula) {
		this.situacaoMatricula = situacaoMatricula;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEmailDiscente() {
		return emailDiscente;
	}
	public void setEmailDiscente(String emailDiscente) {
		this.emailDiscente = emailDiscente;
	}
	public String getNomeComponente() {
		return nomeComponente;
	}
	public void setNomeComponente(String nomeComponente) {
		this.nomeComponente = nomeComponente;
	}
	public Collection<String> getEmailsDocentes() {
		return emailsDocentes;
	}
	public void setEmailsDocentes(Collection<String> emailsDocentes) {
		this.emailsDocentes = emailsDocentes;
	}
	public int getIdComponente() {
		return idComponente;
	}
	public void setIdComponente(int idComponente) {
		this.idComponente = idComponente;
	}
}
