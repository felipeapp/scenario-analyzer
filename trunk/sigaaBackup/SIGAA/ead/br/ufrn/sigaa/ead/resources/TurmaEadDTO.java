package br.ufrn.sigaa.ead.resources;

public class TurmaEadDTO {
	private int id;
	private Integer idPolo;
	private String codigoTurma;
	private int ano;
	private int periodo;
	private Long dataInicio;
	private Long dataFim;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Integer getIdPolo() {
		return idPolo;
	}
	public void setIdPolo(Integer idPolo) {
		this.idPolo = idPolo;
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
}
