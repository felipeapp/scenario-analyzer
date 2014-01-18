package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("outrosLanc")
public class OutrosLanc {

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
	
	@XStreamAlias("txtInscrB")
	private String txtInscrB;
	
	@XStreamAlias("numClassB")
	private int numClassB;
	
	@XStreamAlias("txtInscrC")
	private String txtInscrC;
	
	@XStreamAlias("numClassC")
	private int numClassC;
	
	@XStreamAlias("txtInscrD")
	private String txtInscrD;
	
	@XStreamAlias("numClassD")
	private int numClassD;
	
	@XStreamAlias("tpNormalEstorno")
	private String tpNormalEstorno;
	
	@XStreamAlias("indrRPagarLiq")
	private boolean indrRPagarLiq;

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

	public int getNumClassC() {
		return numClassC;
	}

	public void setNumClassC(int numClassC) {
		this.numClassC = numClassC;
	}

	public String getTxtInscrD() {
		return txtInscrD;
	}

	public void setTxtInscrD(String txtInscrD) {
		this.txtInscrD = txtInscrD;
	}

	public int getNumClassD() {
		return numClassD;
	}

	public void setNumClassD(int numClassD) {
		this.numClassD = numClassD;
	}

	public String getTpNormalEstorno() {
		return tpNormalEstorno;
	}

	public void setTpNormalEstorno(String tpNormalEstorno) {
		this.tpNormalEstorno = tpNormalEstorno;
	}

	public boolean isIndrTemContrato() {
		return indrTemContrato;
	}

	public void setIndrTemContrato(boolean indrTemContrato) {
		this.indrTemContrato = indrTemContrato;
	}

	public BigDecimal getVlr() {
		return vlr;
	}

	public void setVlr(BigDecimal vlr) {
		this.vlr = vlr;
	}

	public boolean isIndrRPagarLiq() {
		return indrRPagarLiq;
	}

	public void setIndrRPagarLiq(boolean indrRPagarLiq) {
		this.indrRPagarLiq = indrRPagarLiq;
	}
	
	
	
}
