package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("compensacao")
public class Compensacao {
	
	@XStreamAlias("numSeqItem")
	private long numSeqItem;
	
	@XStreamAlias("codSit")
	private String codSit;
	
	@XStreamAlias("indrRPagarLiq")
	private boolean indrRPagarLiq;
	
	@XStreamAlias("vlr")
	private BigDecimal vlr;
	
	@XStreamAlias("txtInscrA")
	private String txtInscrA;
	
	@XStreamAlias("numClassA")
	private int numClassA;
	
	@XStreamImplicit(itemFieldName="relDeducaoItem")
	private List<RelSemItem> itensRelDeducao;
	
	@XStreamImplicit(itemFieldName="relEncargoItem")
	private List<RelSemItem> itensRelEncargos;

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

	public List<RelSemItem> getItensRelDeducao() {
		return itensRelDeducao;
	}

	public void setItensRelDeducao(List<RelSemItem> itensRelDeducao) {
		this.itensRelDeducao = itensRelDeducao;
	}

	public List<RelSemItem> getItensRelEncargos() {
		return itensRelEncargos;
	}

	public void setItensRelEncargos(List<RelSemItem> itensRelEncargos) {
		this.itensRelEncargos = itensRelEncargos;
	}
}
