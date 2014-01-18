package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("relComItemValor")
public class RelComItemValor {
	
	@XStreamAlias("numSeqPai")
	private long numSeqPai;//Obrigat�rio
	
	@XStreamAlias("numSeqItem")
	private long numSeqItem;//Obrigat�rio
	
	@XStreamAlias("vlr")
	private BigDecimal vlr;

	public long getNumSeqPai() {
		return numSeqPai;
	}

	public void setNumSeqPai(long numSeqPai) {
		this.numSeqPai = numSeqPai;
	}

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

}
