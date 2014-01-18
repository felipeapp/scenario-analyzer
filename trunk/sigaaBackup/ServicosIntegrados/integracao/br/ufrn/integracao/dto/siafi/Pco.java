package br.ufrn.integracao.dto.siafi;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("pco")
public class Pco {
	
	@XStreamAlias("numSeqItem")
	private long numSeqItem;//Obrigatório
	
	@XStreamAlias("codSit")
	private String codSit;//Obrigatório
	
	@XStreamAlias("codUgEmpe")
	private int codUgEmpe;//Obrigatório
	
	@XStreamAlias("indrTemContrato")
	private boolean indrTemContrato;
	
	@XStreamAlias("txtInscrD")
	private String txtInscrD;
	
	@XStreamAlias("numClassD")
	private int numClassD;
	
	@XStreamAlias("numClassD")
	private String txtInscrE;
	
	@XStreamAlias("numClassE")
	private int numClasseE;
	
	@XStreamAlias("cronBaixaPatrimonial")
	private CronBaixaPatrimonial cronBaixaPatrimonial;
	
	@XStreamImplicit(itemFieldName="pcoItem")
	private List<PcoItem> pcoItens;

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

	public boolean isIndrTemContrato() {
		return indrTemContrato;
	}

	public void setIndrTemContrato(boolean indrTemContrato) {
		this.indrTemContrato = indrTemContrato;
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

	public int getNumClasseE() {
		return numClasseE;
	}

	public void setNumClasseE(int numClasseE) {
		this.numClasseE = numClasseE;
	}

	public List<PcoItem> getPcoItens() {
		return pcoItens;
	}

	public void setPcoItens(List<PcoItem> pcoItens) {
		this.pcoItens = pcoItens;
	}

	public CronBaixaPatrimonial getCronBaixaPatrimonial() {
		return cronBaixaPatrimonial;
	}

	public void setCronBaixaPatrimonial(CronBaixaPatrimonial cronBaixaPatrimonial) {
		this.cronBaixaPatrimonial = cronBaixaPatrimonial;
	}

}
