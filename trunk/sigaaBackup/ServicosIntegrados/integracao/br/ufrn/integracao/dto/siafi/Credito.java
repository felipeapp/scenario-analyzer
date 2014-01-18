package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("credito")
public class Credito {
	
	@XStreamAlias("numSeqItem")
	private long numSeqItem;
	
	@XStreamAlias("codSit")
	private String codSit;
	
	@XStreamAlias("indrRPagarLiq")
	private boolean indrRPagarLiq;
	
	@XStreamAlias("vlr")
	private BigDecimal vlr;
	
	@XStreamAlias("codFontRecur")
	private int codFontRecur;
	
	@XStreamAlias("codCtgoGasto")
	private String codCtgoGasto;
	
	@XStreamAlias("txtInscrA")
	private int txtInscrA;
	
	@XStreamAlias("txtClassA")
	private int txtClassA;
	
	@XStreamAlias("txtInscrB")
	private String txtInscrB;
	
	@XStreamAlias("txtClassB")
	private int txtClassB;
	
	@XStreamAlias("txtInscrC")
	private String txtInscrC;

	public long getNumSeqItem() {
		return numSeqItem;
	}

	public void setNumSeqItem(long numSeqItem) {
		this.numSeqItem = numSeqItem;
	}

	public String getCodSit() {
		return codSit;
	}

	public void setCodSit(String codSit) {
		this.codSit = codSit;
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

	public int getCodFontRecur() {
		return codFontRecur;
	}

	public void setCodFontRecur(int codFontRecur) {
		this.codFontRecur = codFontRecur;
	}

	public String getCodCtgoGasto() {
		return codCtgoGasto;
	}

	public void setCodCtgoGasto(String codCtgoGasto) {
		this.codCtgoGasto = codCtgoGasto;
	}

	public int getTxtInscrA() {
		return txtInscrA;
	}

	public void setTxtInscrA(int txtInscrA) {
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
	
}