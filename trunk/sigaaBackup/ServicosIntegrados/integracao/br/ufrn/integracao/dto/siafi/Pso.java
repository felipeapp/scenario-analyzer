package br.ufrn.integracao.dto.siafi;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("pso")
public class Pso {

	@XStreamAlias("numSeqItem")
	private long numSeqItem;//Obrigatório
	
	@XStreamAlias("codSit")
	private String codSit;//Obrigatório
	
	@XStreamAlias("txtInscE")
	private String txtInscE;
	
	@XStreamAlias("txtClassE")
	private int txtClassE;
	
	@XStreamAlias("txtInscrF")
	private String txtInscrF;
	
	@XStreamAlias("txtClassF")
	private int txtClassF;
	
	@XStreamImplicit(itemFieldName="psoItem")
	private List<PsoItem> psoItens;

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

	public String getTxtInscE() {
		return txtInscE;
	}

	public void setTxtInscE(String txtInscE) {
		this.txtInscE = txtInscE;
	}

	public int getTxtClassE() {
		return txtClassE;
	}

	public void setTxtClassE(int txtClassE) {
		this.txtClassE = txtClassE;
	}

	public String getTxtInscrF() {
		return txtInscrF;
	}

	public void setTxtInscrF(String txtInscrF) {
		this.txtInscrF = txtInscrF;
	}

	public List<PsoItem> getPsoItens() {
		return psoItens;
	}

	public void setPsoItens(List<PsoItem> psoItens) {
		this.psoItens = psoItens;
	}

	public int getTxtClassF() {
		return txtClassF;
	}

	public void setTxtClassF(int txtClassF) {
		this.txtClassF = txtClassF;
	}

}
