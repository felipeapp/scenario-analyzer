package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ajuste")
public class Ajuste {
	
	@XStreamAlias("numSeqItem")
	private long numSeqItem;//Obrigatório
	
	@XStreamAlias("codSit")
	private String codSit;//Obrigatório
	
	@XStreamAlias("vlr")
	private BigDecimal vlr;//Obrigatório
	
	@XStreamAlias("indrTemContrato")
	private boolean indrTemContrato;
	
	@XStreamAlias("txtInscrA")
	private String txtInscrA;
	
	@XStreamAlias("numClassA")
	private int numClassA;
	
	@XStreamAlias("txtInscB")
	private String txtInscB;
	
	@XStreamAlias("numClassB")
	private int numClassB;

	@XStreamAlias("txtInscC")
	private String txtInscC;
	
	@XStreamAlias("numClassC")
	private int numClassC;
	
	@XStreamAlias("txtInscD")
	private String txtInscD;
	
	@XStreamAlias("numClassD")
	private int numClassD;

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

	public BigDecimal getVlr() {
		return vlr;
	}

	public void setVlr(BigDecimal vlr) {
		this.vlr = vlr;
	}

	public boolean isIndrTemContrato() {
		return indrTemContrato;
	}

	public void setIndrTemContrato(boolean indrTemContrato) {
		this.indrTemContrato = indrTemContrato;
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

	public String getTxtInscB() {
		return txtInscB;
	}

	public void setTxtInscB(String txtInscB) {
		this.txtInscB = txtInscB;
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

	public int getNumClassC() {
		return numClassC;
	}

	public void setNumClassC(int numClassC) {
		this.numClassC = numClassC;
	}

	public String getTxtInscD() {
		return txtInscD;
	}

	public void setTxtInscD(String txtInscD) {
		this.txtInscD = txtInscD;
	}

	public int getNumClassD() {
		return numClassD;
	}

	public void setNumClassD(int numClassD) {
		this.numClassD = numClassD;
	}

}
