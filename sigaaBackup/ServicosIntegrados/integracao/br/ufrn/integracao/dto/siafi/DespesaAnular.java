package br.ufrn.integracao.dto.siafi;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("despesaAnular")
public class DespesaAnular {

	@XStreamAlias("numSeqItem")
	private long numSeqItem;
	
	@XStreamAlias("codSit")
	private String codSit;
	
	@XStreamAlias("codUgEmpe")
	private int codUgEmpe;
	
	@XStreamAlias("txtInscrD")
	private String txtInscrD;
	
	@XStreamAlias("numClassD")
	private int numClassD;
	
	@XStreamAlias("txtInscrE")
	private String txtInscrE;
	
	@XStreamAlias("numClassE")
	private int numClassE;
	
	@XStreamImplicit(itemFieldName="despesaAnularItem")
	private List<DespesaAnularItem> itensDespesaAnular;

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

	public int getCodUgEmpe() {
		return codUgEmpe;
	}

	public void setCodUgEmpe(int codUgEmpe) {
		this.codUgEmpe = codUgEmpe;
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

	public String getTxtInscrE() {
		return txtInscrE;
	}

	public void setTxtInscrE(String txtInscrE) {
		this.txtInscrE = txtInscrE;
	}

	public int getNumClassE() {
		return numClassE;
	}

	public void setNumClassE(int numClassE) {
		this.numClassE = numClassE;
	}

	public List<DespesaAnularItem> getItensDespesaAnular() {
		return itensDespesaAnular;
	}

	public void setItensDespesaAnular(List<DespesaAnularItem> itensDespesaAnular) {
		this.itensDespesaAnular = itensDespesaAnular;
	}

}
