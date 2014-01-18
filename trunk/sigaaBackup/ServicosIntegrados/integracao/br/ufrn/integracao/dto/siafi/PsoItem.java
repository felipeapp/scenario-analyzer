package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("psoItem")
public class PsoItem {
	
	@XStreamAlias("numSeqItem")
	private long numSeqItem;//Obrigatório
	
	@XStreamAlias("indrRPagarLiq")
	private boolean indrRPagarLiq; //obrigatorio
	
	@XStreamAlias("vlr")
	private BigDecimal vlr;//Obrigatório
	
	@XStreamAlias("codFontRecur")
	private String codFontRecurso;//Obrigatório
	
	@XStreamAlias("codCtgoGasto")
	private String codCtgoGasto;//Obrigatório
	
	@XStreamAlias("txtInscrA")
	private String txtInscrA;
	
	@XStreamAlias("txtClassA")
	private int txtClassA;
	
	@XStreamAlias("txtInscrB")
	private String txtInscrB;
	
	@XStreamAlias("txtClassB")
	private int txtClassB;
	
	@XStreamAlias("txtInscC")
	private String txtInscC;
	
	@XStreamAlias("txtClassC")
	private int txtClassC;
	
	@XStreamAlias("txtInscrD")
	private String txtInscD;
	
	@XStreamAlias("txtClassD")
	private int txtClassD;

	public long getNumSeqItem() {
		return numSeqItem;
	}

	public void setNumSeqItem(long numSeqItem) {
		this.numSeqItem = numSeqItem;
	}

	public BigDecimal getVlr() {
		return vlr;
	}

	public void setVlr(BigDecimal vlr) {
		this.vlr = vlr;
	}

	public String getCodFontRecurso() {
		return codFontRecurso;
	}

	public void setCodFontRecurso(String codFontRecurso) {
		this.codFontRecurso = codFontRecurso;
	}

	public String getCodCtgoGasto() {
		return codCtgoGasto;
	}

	public void setCodCtgoGasto(String codCtgoGasto) {
		this.codCtgoGasto = codCtgoGasto;
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

	public String getTxtInscC() {
		return txtInscC;
	}

	public void setTxtInscC(String txtInscC) {
		this.txtInscC = txtInscC;
	}

	public int getTxtClassC() {
		return txtClassC;
	}

	public void setTxtClassC(int txtClassC) {
		this.txtClassC = txtClassC;
	}

	public String getTxtInscD() {
		return txtInscD;
	}

	public void setTxtInscD(String txtInscD) {
		this.txtInscD = txtInscD;
	}

	public int getTxtClassD() {
		return txtClassD;
	}

	public void setTxtClassD(int txtClassD) {
		this.txtClassD = txtClassD;
	}

	public boolean isIndrRPagarLiq() {
		return indrRPagarLiq;
	}

	public void setIndrRPagarLiq(boolean indrRPagarLiq) {
		this.indrRPagarLiq = indrRPagarLiq;
	}

}
