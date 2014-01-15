package br.ufrn.sigaa.ead.resources;

import java.util.ArrayList;
import java.util.List;

public class TutorEadDTO {
	
	private int id;
	private int idPolo;
	private Integer idVinculo;
	private int idCurso;
	
	private PessoaEadDTO pessoa;
	private UsuarioEadDTO usuario;
	private List <TutoriaAlunoEadDTO> tutorias = new ArrayList <TutoriaAlunoEadDTO> ();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdPolo() {
		return idPolo;
	}
	public void setIdPolo(int idPolo) {
		this.idPolo = idPolo;
	}
	public int getIdVinculo() {
		return idVinculo;
	}
	public void setIdVinculo(Integer idVinculo) {
		this.idVinculo = idVinculo;
	}
	public int getIdCurso() {
		return idCurso;
	}
	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
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
	public List<TutoriaAlunoEadDTO> getTutorias() {
		return tutorias;
	}
	public void setTutorias(List<TutoriaAlunoEadDTO> tutorias) {
		this.tutorias = tutorias;
	}
}
