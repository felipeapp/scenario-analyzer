package br.ufrn.sigaa.ead.resources;

public class DiscenteTurmaEadDTO {
	private int id;
	private Integer mediaFinal;
	private Integer recuperacao;
	private Integer idTurma;
	private int idComponenteCurricular;
	private int idSituacaoMatricula;
	private String codigoDisciplina;
	private String codigoTurma;
	private int ano;
	private int periodo;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Integer getMediaFinal() {
		return mediaFinal;
	}
	public void setMediaFinal(Integer mediaFinal) {
		this.mediaFinal = mediaFinal;
	}
	public Integer getRecuperacao() {
		return recuperacao;
	}
	public void setRecuperacao(Integer recuperacao) {
		this.recuperacao = recuperacao;
	}
	public Integer getIdTurma() {
		return idTurma;
	}
	public void setIdTurma(Integer idTurma) {
		this.idTurma = idTurma;
	}
	public int getIdComponenteCurricular() {
		return idComponenteCurricular;
	}
	public void setIdComponenteCurricular(int idComponenteCurricular) {
		this.idComponenteCurricular = idComponenteCurricular;
	}
	public int getIdSituacaoMatricula() {
		return idSituacaoMatricula;
	}
	public void setIdSituacaoMatricula(int idSituacaoMatricula) {
		this.idSituacaoMatricula = idSituacaoMatricula;
	}
	public String getCodigoDisciplina() {
		return codigoDisciplina;
	}
	public void setCodigoDisciplina(String codigoDisciplina) {
		this.codigoDisciplina = codigoDisciplina;
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
	
	public boolean equals(Object o){
		if (o == null || !(o instanceof DiscenteTurmaEadDTO))
			return false;
		
		DiscenteTurmaEadDTO outro = (DiscenteTurmaEadDTO) o;
		if (outro.getId() == this.id)
			return true;
		
		return false;
	}
}
