package br.ufrn.sigaa.ensino_rede.resources;

public class DadosCursoRedeDTO {
	private int id;
	private String programa;
	private String ies;
	private int idCampus;
	private String nomeCampus;
	private int idCurso;
	private String nomeCurso;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPrograma() {
		return programa;
	}
	public void setPrograma(String programa) {
		this.programa = programa;
	}
	public String getIes() {
		return ies;
	}
	public void setIes(String ies) {
		this.ies = ies;
	}
	public int getIdCampus() {
		return idCampus;
	}
	public void setIdCampus(int idCampus) {
		this.idCampus = idCampus;
	}
	public String getNomeCampus() {
		return nomeCampus;
	}
	public void setNomeCampus(String nomeCampus) {
		this.nomeCampus = nomeCampus;
	}
	public int getIdCurso() {
		return idCurso;
	}
	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
	}
	public String getNomeCurso() {
		return nomeCurso;
	}
	public void setNomeCurso(String nomeCurso) {
		this.nomeCurso = nomeCurso;
	}
}
