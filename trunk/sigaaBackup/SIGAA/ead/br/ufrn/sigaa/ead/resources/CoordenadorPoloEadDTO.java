package br.ufrn.sigaa.ead.resources;

public class CoordenadorPoloEadDTO {
	private int id;
	private int idPolo;
	private long inicio;
	private Long fim;
	private PessoaEadDTO pessoa;
	private UsuarioEadDTO usuario;
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
	public long getInicio() {
		return inicio;
	}
	public void setInicio(long inicio) {
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
