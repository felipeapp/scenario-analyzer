package br.ufrn.integracao.dto.siafi;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("relSemItem")
public class RelSemItem {
	
	@XStreamAlias("numSeqItem")
	private long numSeqItem;

	public long getNumSeqItem() {
		return numSeqItem;
	}

	public void setNumSeqItem(long numSeqItem) {
		this.numSeqItem = numSeqItem;
	}

}
