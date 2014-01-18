package br.ufrn.integracao.dto.siafi;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("predocGPS")
public class PredocGPS {
	
	@XStreamAlias("codRecurso")
	private String codRecurso;//Obrigat�rio
	
	@XStreamAlias("txtProcesso")
	private String txtProcesso;
	
	@XStreamAlias("mesCompet")
	private String mesCompet;//Obrigat�rio
	
	@XStreamAlias("anoCompet")
	private String anoCompet;//Obrigat�rio
	
	@XStreamAlias("indrAdiant13")
	private boolean indrAdiant13;//Obrigat�rio

	public String getCodRecurso() {
		return codRecurso;
	}

	public void setCodRecurso(String codRecurso) {
		this.codRecurso = codRecurso;
	}

	public String getTxtProcesso() {
		return txtProcesso;
	}

	public void setTxtProcesso(String txtProcesso) {
		this.txtProcesso = txtProcesso;
	}

	public String getMesCompet() {
		return mesCompet;
	}

	public void setMesCompet(String mesCompet) {
		this.mesCompet = mesCompet;
	}

	public String getAnoCompet() {
		return anoCompet;
	}

	public void setAnoCompet(String anoCompet) {
		this.anoCompet = anoCompet;
	}

	public boolean isIndrAdiant13() {
		return indrAdiant13;
	}

	public void setIndrAdiant13(boolean indrAdiant13) {
		this.indrAdiant13 = indrAdiant13;
	}

}
