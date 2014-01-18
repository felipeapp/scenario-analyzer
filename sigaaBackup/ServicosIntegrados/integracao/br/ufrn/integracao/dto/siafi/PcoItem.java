package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("pcoItem")
public class PcoItem {
	
	@XStreamAlias("numSeqItem")
	private long numSeqItem;//Obrigatório
	
	@XStreamAlias("numEmpe")
	private String numEmpe;//Obrigatório
	
	@XStreamAlias("codSubItemEmpe")
	private int codSubItemEmpe;//Obrigatório
	
	@XStreamAlias("vlr")
	private BigDecimal vlr;//Obrigatório
	
	@XStreamAlias("txtInscrA")
	private String txtInscrA;
	
	@XStreamAlias("numClassA")
	private String numClassA;
	
	@XStreamAlias("txtInscB")
	private String txtInscB;
	
	@XStreamAlias("numClassB")
	private int numClassB;
	
	@XStreamAlias("txtInscC")
	private String txtInscC;
	
	@XStreamAlias("numClassC")
	private int numClassC;
	
	@XStreamAlias("indrRPagarLiq")
	private boolean indrRPagarLiq;
	
	@XStreamAlias("numSeqPai")
	private long numSeqPai;//Obrigatório

	public long getNumSeqItem() {
		return numSeqItem;
	}

	public void setNumSeqItem(long numSeqItem) {
		this.numSeqItem = numSeqItem;
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

	public String getNumClassA() {
		return numClassA;
	}

	public void setNumClassA(String numClassA) {
		this.numClassA = numClassA;
	}

	public String getTxtInscB() {
		return txtInscB;
	}

	public void setTxtInscB(String txtInscB) {
		this.txtInscB = txtInscB;
	}

	public int getNumClassC() {
		return numClassC;
	}

	public void setNumClassC(int numClassC) {
		this.numClassC = numClassC;
	}

	public int getNumClassB() {
		return numClassB;
	}

	public void setNumClassB(int numClassB) {
		this.numClassB = numClassB;
	}

	public String getTxtInscC() {
		return txtInscC;
	}

	public void setTxtInscC(String txtInscC) {
		this.txtInscC = txtInscC;
	}

	public boolean isIndrRPagarLiq() {
		return indrRPagarLiq;
	}

	public void setIndrRPagarLiq(boolean indrRPagarLiq) {
		this.indrRPagarLiq = indrRPagarLiq;
	}

	public long getNumSeqPai() {
		return numSeqPai;
	}

	public void setNumSeqPai(long numSeqPai) {
		this.numSeqPai = numSeqPai;
	}

}
