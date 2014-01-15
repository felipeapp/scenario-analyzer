package br.ufrn.sigaa.ead.resources;

import java.util.ArrayList;
import java.util.List;

public class DiscenteEadDTO {
	
	private int id;
	private int periodoIngresso;
	private int anoIngresso;
	private Long matricula;
	private int idCurso;
	private int idStatus;
	private Integer idPolo;
	private Integer idTutor;
	
	private PessoaEadDTO pessoa;
	private UsuarioEadDTO usuario;
	
	private List <DiscenteTurmaEadDTO> discenteTurmas = new ArrayList <DiscenteTurmaEadDTO> ();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPeriodoIngresso() {
		return periodoIngresso;
	}
	public void setPeriodoIngresso(int periodoIngresso) {
		this.periodoIngresso = periodoIngresso;
	}
	public int getAnoIngresso() {
		return anoIngresso;
	}
	public void setAnoIngresso(int anoIngresso) {
		this.anoIngresso = anoIngresso;
	}
	public Long getMatricula() {
		return matricula;
	}
	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}
	public int getIdCurso() {
		return idCurso;
	}
	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
	}
	public Integer getIdTutor() {
		return idTutor;
	}
	public void setIdTutor(Integer idTutor) {
		this.idTutor = idTutor;
	}
	public int getIdStatus() {
		return idStatus;
	}
	public void setIdStatus(int idStatus) {
		this.idStatus = idStatus;
	}
	public Integer getIdPolo() {
		return idPolo;
	}
	public void setIdPolo(Integer idPolo) {
		this.idPolo = idPolo;
	}
	public PessoaEadDTO getPessoa() {
		return pessoa;
	}
	public void setPessoa(PessoaEadDTO pessoa) {
		this.pessoa = pessoa;
	}
	public UsuarioEadDTO getUsuario() {
		return usuario;
	}
	public void setUsuario(UsuarioEadDTO usuario) {
		this.usuario = usuario;
	}
	public List<DiscenteTurmaEadDTO> getDiscenteTurmas() {
		return discenteTurmas;
	}
	public void setDiscenteTurmas(List<DiscenteTurmaEadDTO> discenteTurmas) {
		this.discenteTurmas = discenteTurmas;
	}
}