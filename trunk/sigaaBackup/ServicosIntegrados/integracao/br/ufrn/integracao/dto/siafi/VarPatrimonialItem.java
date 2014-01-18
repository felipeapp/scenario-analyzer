package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("varPatrimonialItem")
public class VarPatrimonialItem {
	
	@XStreamAlias("numSeqItem")
	private long numSeqItem;
	
	@XStreamAlias("indrRPagarLiq")
	private boolean indrRPagarLiq;
	
	@XStreamAlias("vlr")
	private BigDecimal vlr;
	
	@XStreamAlias("txtInscrA")
	private String txtInscrA;
	
	@XStreamAlias("numClassA")
	private int numClassA;
	
	@XStreamAlias("txtInscrB")
	private String txtInscrB;
	
	@XStreamAlias("numClassB")
	private int numClassB;
	
	@XStreamAlias("txtInscrC")
	private String txtInscrC;

	public long getNumSeqItem() {
		return numSeqItem;
	}

	public void setNumSeqItem(long numSeqItem) {
		this.numSeqItem = numSeqItem;
	}

	public boolean isIndrRPagarLiq() {
		return indrRPagarLiq;
	}

	public void setIndrRPagarLiq(boolean indrRPagarLiq) {
		this.indrRPagarLiq = indrRPagarLiq;
	}

	public BigDecimal getVlr() {
		return vlr;
	}

	public void setVlr(BigDecimal vlr) {
		this.vlr = vlr;
	}

	public String getTxtInscrA() {
		return txtInscrA;
	}

	public void setTxtInscrA(String txtInscrA) {
		this.txtInscrA = txtInscrA;
	}

	public int getNumClassA() {
		return numClassA;
	}

	public void setNumClassA(int numClassA) {
		this.numClassA = numClassA;
	}

	public String getTxtInscrB() {
		return txtInscrB;
	}

	public void setTxtInscrB(String txtInscrB) {
		this.txtInscrB = txtInscrB;
	}

	public int getNumClassB() {
		return numClassB;
	}

	public void setNumClassB(int numClassB) {
		this.numClassB = numClassB;
	}

	public String getTxtInscrC() {
		return txtInscrC;
	}

	public void setTxtInscrC(String txtInscrC) {
		this.txtInscrC = txtInscrC;
	}	

}
