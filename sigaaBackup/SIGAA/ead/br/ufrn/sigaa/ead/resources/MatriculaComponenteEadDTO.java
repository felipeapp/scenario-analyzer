package br.ufrn.sigaa.ead.resources;

import java.util.Collection;

public class MatriculaComponenteEadDTO {
	private int id;
	private int idDiscente;
	private int idTurma;
	private int idSituacaoMatricula;
	private Float mediaFinal;
	private Float recuperacao;
	private Long dataCadastro;
	private Long matricula;
	private String codigoComponente;
	private int idMunicipio;
	private String nomeMunicipio;
	private String codigoPolo;
	private String cpfAluno;
	private Collection<String> cpfsDocentes;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdDiscente() {
		return idDiscente;
	}
	public void setIdDiscente(int idDiscente) {
		this.idDiscente = idDiscente;
	}
	public int getIdTurma() {
		return idTurma;
	}
	public void setIdTurma(int idTurma) {
		this.idTurma = idTurma;
	}
	public int getIdSituacaoMatricula() {
		return idSituacaoMatricula;
	}
	public void setIdSituacaoMatricula(int idSituacaoMatricula) {
		this.idSituacaoMatricula = idSituacaoMatricula;
	}
	public Float getMediaFinal() {
		return mediaFinal;
	}
	public void setMediaFinal(Float mediaFinal) {
		this.mediaFinal = mediaFinal;
	}
	public Float getRecuperacao() {
		return recuperacao;
	}
	public void setRecuperacao(Float recuperacao) {
		this.recuperacao = recuperacao;
	}
	public Long getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Long dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	public Long getMatricula() {
		return matricula;
	}
	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}
	public String getCodigoComponente() {
		return codigoComponente;
	}
	public void setCodigoComponente(String codigoComponente) {
		this.codigoComponente = codigoComponente;
	}
	public int getIdMunicipio() {
		return idMunicipio;
	}
	public void setIdMunicipio(int idMunicipio) {
		this.idMunicipio = idMunicipio;
	}
	public String getNomeMunicipio() {
		return nomeMunicipio;
	}
	public void setNomeMunicipio(String nomeMunicipio) {
		this.nomeMunicipio = nomeMunicipio;
	}
	public String getCodigoPolo() {
		return codigoPolo;
	}
	public void setCodigoPolo(String codigoPolo) {
		this.codigoPolo = codigoPolo;
	}
	public String getCpfAluno() {
		return cpfAluno;
	}
	public void setCpfAluno(String cpfAluno) {
		this.cpfAluno = cpfAluno;
	}
	public Collection<String> getCpfsDocentes() {
		return cpfsDocentes;
	}
	public void setCpfsDocentes(Collection<String> cpfsDocentes) {
		this.cpfsDocentes = cpfsDocentes;
	}
}
