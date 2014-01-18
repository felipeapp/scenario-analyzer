package br.ufrn.integracao.dto.siafi;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("relComItem")
public class RelComItem {
	
	@XStreamAlias("numSeqPai")
	private long numSeqPai;//Obrigat�rio
	
	@XStreamAlias("numSeqItem")
	private long numSeqItem;//Obrigat�rio
	

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

}