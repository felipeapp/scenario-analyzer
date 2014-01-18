package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("despesaAnularItem")
public class DespesaAnularItem {

	@XStreamAlias("numSeqItem")
	private long numSeqItem;
	
	@XStreamAlias("numEmpe")
	private String numEmpe;
	
	@XStreamAlias("indrRPagarLiq")
	private boolean indrRPagarLiq;
	
	@XStreamAlias("vlr")
	private BigDecimal vlr;
	
	@XStreamAlias("txtInscrA")
	private int txtInscrA;
	
	@XStreamAlias("txtInscrB")
	private String txtInscrB;
	
	@XStreamAlias("numClassB")
	private int numClassB;
	
	@XStreamAlias("txtInscrC")
	private String txtInscrC;
	
	@XStreamAlias("numClassC")
	private int numClassC;
	
	@XStreamImplicit(itemFieldName="relEncargo")
	private List<RelSemItem> relEncargos;
	
	@XStreamImplicit(itemFieldName="relPcoItem")
	private List<PcoItem> relPcoItens;
	
	@XStreamAlias("codSubItemEmpe")
	private int codSubItemEmpe;

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

	public int getTxtInscrA() {
		return txtInscrA;
	}

	public void setTxtInscrA(int txtInscrA) {
		this.txtInscrA = txtInscrA;
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

	public int getNumClassC() {
		return numClassC;
	}

	public void setNumClassC(int numClassC) {
		this.numClassC = numClassC;
	}

	public List<RelSemItem> getRelEncargos() {
		return relEncargos;
	}

	public void setRelEncargos(List<RelSemItem> relEncargos) {
		this.relEncargos = relEncargos;
	}

	public int getCodSubItemEmpe() {
		return codSubItemEmpe;
	}

	public void setCodSubItemEmpe(int codSubItemEmpe) {
		this.codSubItemEmpe = codSubItemEmpe;
	}

	public List<PcoItem> getRelPcoItens() {
		return relPcoItens;
	}

	public void setRelPcoItens(List<PcoItem> relPcoItens) {
		this.relPcoItens = relPcoItens;
	}
	
}
