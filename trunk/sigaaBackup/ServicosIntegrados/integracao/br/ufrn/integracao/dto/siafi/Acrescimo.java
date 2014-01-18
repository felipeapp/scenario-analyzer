package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("acrescimo")
public class Acrescimo {
	
	@XStreamAlias("tpAcrescimo")
	private String tpAcrescimo;//Obrigatório
	
	@XStreamAlias("vlr")
	private BigDecimal vlr;//Obrigatório
	
	@XStreamAlias("numEmpe")
	private String numEmpe;//Obrigatório
	
	@XStreamAlias("codSubItemEmpe")
	private int codSubItemEmpe;//Obrigatório
	
	@XStreamAlias("indrRPagarLiq")
	private boolean indrRPagarLiq;
	
	@XStreamAlias("txtInscrA")
	private String txtInscrA;
	
	@XStreamAlias("txtClassA")
	private int txtClassA;
	
	@XStreamAlias("txtInscrB")
	private String txtInscrB;

	public String getTpAcrescimo() {
		return tpAcrescimo;
	}

	public void setTpAcrescimo(String tpAcrescimo) {
		this.tpAcrescimo = tpAcrescimo;
	}

	public BigDecimal getVlr() {
		return vlr;
	}

	public void setVlr(BigDecimal vlr) {
		this.vlr = vlr;
	}

	public String getNumEmpe() {
		return numEmpe;
	}

	public void setNumEmpe(String numEmpe) {
		this.numEmpe = numEmpe;
	}

	public int getCodSubItemEmpe() {
		return codSubItemEmpe;
	}

	public void setCodSubItemEmpe(int codSubItemEmpe) {
		this.codSubItemEmpe = codSubItemEmpe;
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

	public boolean isIndrRPagarLiq() {
		return indrRPagarLiq;
	}

	public void setIndrRPagarLiq(boolean indrRPagarLiq) {
		this.indrRPagarLiq = indrRPagarLiq;
	}

}
