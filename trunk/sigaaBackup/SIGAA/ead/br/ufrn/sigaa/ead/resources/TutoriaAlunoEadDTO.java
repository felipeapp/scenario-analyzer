package br.ufrn.sigaa.ead.resources;


public class TutoriaAlunoEadDTO {
	
	private int id;
	private int idTutor;
	private int idDiscente;
	private long  inicio;
	private long fim;
	private boolean ativo;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdTutor() {
		return idTutor;
	}
	public void setIdTutor(int idTutor) {
		this.idTutor = idTutor;
	}
	public int getIdDiscente() {
		return idDiscente;
	}
	public void setIdDiscente(int idDiscente) {
		this.idDiscente = idDiscente;
	}
	public long getInicio() {
		return inicio;
	}
	public void setInicio(long inicio) {
		this.inicio = inicio;
	}
	public long getFim() {
		return fim;
	}
	public void setFim(long fim) {
		this.fim = fim;
	}
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
}
