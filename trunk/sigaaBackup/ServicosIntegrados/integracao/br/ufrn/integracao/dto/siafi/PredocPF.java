package br.ufrn.integracao.dto.siafi;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("predocPF")
public class PredocPF {
	
	@XStreamAlias("codUGFavorecida")
	private int codUGFavorecida;//Obrigatório
	
	@XStreamAlias("vinculacaoPgto")
	private int vinculacaoPgto;
	
	@XStreamAlias("txtInscrA")
	private String txtInscrA;
	
	@XStreamAlias("txtClassA")
	private int txtClassA;

	@XStreamAlias("txtInscrB")
	private String txtInscrB;
	
	@XStreamAlias("txtClassB")
	private int txtClassB;
	
	@XStreamAlias("txtInscrC")
	private String txtInscrC;
	
	@XStreamAlias("txtClassC")
	private int txtClassC;
	
	@XStreamAlias("txtInscrD")
	private String txtInscrD;
	
	@XStreamAlias("txtClassD")
	private int txtClassD;

	public int getCodUGFavorecida() {
		return codUGFavorecida;
	}

	public void setCodUGFavorecida(int codUGFavorecida) {
		this.codUGFavorecida = codUGFavorecida;
	}

	public int getVinculacaoPgto() {
		return vinculacaoPgto;
	}

	public void setVinculacaoPgto(int vinculacaoPgto) {
		this.vinculacaoPgto = vinculacaoPgto;
	}

	public String getTxtInscrA() {
		return txtInscrA;
	}

	public void setTxtInscrA(String txtInscrA) {
		this.txtInscrA = txtInscrA;
	}

	public int getTxtClassA() {
		return txtClassA;
	}

	public void setTxtClassA(int txtClassA) {
		this.txtClassA = txtClassA;
	}

	public String getTxtInscrB() {
		return txtInscrB;
	}

	public void setTxtInscrB(String txtInscrB) {
		this.txtInscrB = txtInscrB;
	}

	public int getTxtClassB() {
		return txtClassB;
	}

	public void setTxtClassB(int txtClassB) {
		this.txtClassB = txtClassB;
	}

	public String getTxtInscrC() {
		return txtInscrC;
	}

	public void setTxtInscrC(String txtInscrC) {
		this.txtInscrC = txtInscrC;
	}

	public int getTxtClassC() {
		return txtClassC;
	}

	public void setTxtClassC(int txtClassC) {
		this.txtClassC = txtClassC;
	}

	public String getTxtInscrD() {
		return txtInscrD;
	}

	public void setTxtInscrD(String txtInscrD) {
		this.txtInscrD = txtInscrD;
	}

	public int getTxtClassD() {
		return txtClassD;
	}

	public void setTxtClassD(int txtClassD) {
		this.txtClassD = txtClassD;
	}
	

}
