package br.ufrn.sigaa.ead.resources;

public class CoordenadorCursoEadDTO {
	private int id;
	private int idCurso;
	private int idCargoAcademico;
	private Long inicio;
	private Long fim;
	private PessoaEadDTO pessoa;
	private UsuarioEadDTO usuario;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdCurso() {
		return idCurso;
	}
	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
	}
	public int getIdCargoAcademico() {
		return idCargoAcademico;
	}
	public void setIdCargoAcademico(int idCargoAcademico) {
		this.idCargoAcademico = idCargoAcademico;
	}
	public Long getInicio() {
		return inicio;
	}
	public void setInicio(Long inicio) {
		this.inicio = inicio;
	}
	public Long getFim() {
		return fim;
	}
	public void setFim(Long fim) {
		this.fim = fim;
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
}
