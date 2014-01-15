package br.ufrn.sigaa.ead.resources;

public class ServidorEadDTO {
	private int id;
	private int idFormacao;
	private int siape;
	private String lotacao;
	private int regimeTrabalho;
	private boolean dedicacaoExclusiva;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdFormacao() {
		return idFormacao;
	}
	public void setIdFormacao(int idFormacao) {
		this.idFormacao = idFormacao;
	}
	public int getSiape() {
		return siape;
	}
	public void setSiape(int siape) {
		this.siape = siape;
	}
	public String getLotacao() {
		return lotacao;
	}
	public void setLotacao(String lotacao) {
		this.lotacao = lotacao;
	}
	public int getRegimeTrabalho() {
		return regimeTrabalho;
	}
	public void setRegimeTrabalho(int regimeTrabalho) {
		this.regimeTrabalho = regimeTrabalho;
	}
	public boolean isDedicacaoExclusiva() {
		return dedicacaoExclusiva;
	}
	public void setDedicacaoExclusiva(boolean dedicacaoExclusiva) {
		this.dedicacaoExclusiva = dedicacaoExclusiva;
	}
}
