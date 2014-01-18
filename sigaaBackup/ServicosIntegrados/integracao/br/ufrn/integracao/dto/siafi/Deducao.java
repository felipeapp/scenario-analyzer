package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.ufrn.integracao.dto.siafi.converters.DateConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("deducao")
public class Deducao {
	
	@XStreamAlias("numSeqItem")
	private long numSeqItem;//Obrigatório
	
	@XStreamAlias("codSit")
	private String codSit;//Obrigatório
	
	@XStreamAlias("codUgPgto")
	private int codUgPagto;//Obrigatório
	
	@XStreamAlias("vlr")
	private BigDecimal vlr;//Obrigatório
	
	//Datatime
	@XStreamAlias("dtVenc")
	@XStreamConverter(value=DateConverter.class)
	private Date dtVenc;
	
	@XStreamAlias("dtPgtoReceb")
	@XStreamConverter(value=DateConverter.class)
	private Date dtPgtoReceb;	
	
	@XStreamAlias("indrRPagarLiq")
	private boolean indrRPagarLiq;
	
	@XStreamAlias("codUgEmpe")
	private int codUgEmpe;
	
	@XStreamAlias("numEmpe")
	private String numEmpe;
	
	@XStreamAlias("codSubItemEmpe")
	private int codSubItemEmpe;
	
	@XStreamAlias("txtInscrA")
	private String txtInscA;	
	
	@XStreamAlias("txtClassA")
	private int txtClassA;
	
	@XStreamAlias("txtInscrB")
	private String txtInscB;
	
	@XStreamAlias("txtClassB")
	private int txtClassB;	
	
	@XStreamAlias("txtInscrC")
	private String txtInscC;	
	
	@XStreamAlias("txtClassC")
	private int txtClassC;	
	
	@XStreamAlias("txtInscrD")
	private String txtInscD;	
	
	@XStreamAlias("txtClassD")
	private int txtClassD;
	
	@XStreamAlias("predoc")
	private PreDoc preDoc;
	
	@XStreamImplicit(itemFieldName="itemRecolhimento")
	private List<ItemRecolhimento> itemRecolhimentos;	
	
	@XStreamImplicit(itemFieldName="acrescimo")
	private List<Acrescimo> acrescimos;
	
	@XStreamImplicit(itemFieldName="relPcoItem")
	private List<RelComItem> relPcoItens;
	
	@XStreamImplicit(itemFieldName="relPsoItem")
	private List<RelComItem> relPsoItens;
	
	@XStreamImplicit(itemFieldName="relCredito")
	private List<RelSemItem> relCreditos;
	
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

	public Date getDtVenc() {
		return dtVenc;
	}

	public void setDtVenc(Date dtVenc) {
		this.dtVenc = dtVenc;
	}

	public Date getDtPgtoReceb() {
		return dtPgtoReceb;
	}

	public void setDtPgtoReceb(Date dtPgtoReceb) {
		this.dtPgtoReceb = dtPgtoReceb;
	}

	public int getCodUgPagto() {
		return codUgPagto;
	}

	public void setCodUgPagto(int codUgPagto) {
		this.codUgPagto = codUgPagto;
	}

	public BigDecimal getVlr() {
		return vlr;
	}

	public void setVlr(BigDecimal vlr) {
		this.vlr = vlr;
	}

	public int getCodUgEmpe() {
		return codUgEmpe;
	}

	public void setCodUgEmpe(int codUgEmpe) {
		this.codUgEmpe = codUgEmpe;
	}

	public String getNumEmpe() {
		return numEmpe;
	}

	public void setNumEmpe(String numEmpe) {
		this.numEmpe = numEmpe;
	}

	public int getCodSubItemEmpe() {
		return codSubItemEmpe;
	}

	public void setCodSubItemEmpe(int codSubItemEmpe) {
		this.codSubItemEmpe = codSubItemEmpe;
	}

	public String getTxtInscA() {
		return txtInscA;
	}

	public void setTxtInscA(String txtInscA) {
		this.txtInscA = txtInscA;
	}

	public int getTxtClassA() {
		return txtClassA;
	}

	public void setTxtClassA(int txtClassA) {
		this.txtClassA = txtClassA;
	}

	public String getTxtInscB() {
		return txtInscB;
	}

	public void setTxtInscB(String txtInscB) {
		this.txtInscB = txtInscB;
	}

	public int getTxtClassB() {
		return txtClassB;
	}

	public void setTxtClassB(int txtClassB) {
		this.txtClassB = txtClassB;
	}

	public String getTxtInscC() {
		return txtInscC;
	}

	public void setTxtInscC(String txtInscC) {
		this.txtInscC = txtInscC;
	}

	public int getTxtClassC() {
		return txtClassC;
	}

	public void setTxtClassC(int txtClassC) {
		this.txtClassC = txtClassC;
	}

	public PreDoc getPreDoc() {
		return preDoc;
	}

	public void setPreDoc(PreDoc preDoc) {
		this.preDoc = preDoc;
	}
	
	public List<ItemRecolhimento> getItemRecolhimentos() {
		return itemRecolhimentos;
	}

	public void setItemRecolhimentos(List<ItemRecolhimento> itemRecolhimentos) {
		this.itemRecolhimentos = itemRecolhimentos;
	}

	public List<Acrescimo> getAcrescimos() {
		return acrescimos;
	}

	public void setAcrescimos(List<Acrescimo> acrescimos) {
		this.acrescimos = acrescimos;
	}

	public List<RelComItem> getRelPcoItens() {
		return relPcoItens;
	}

	public void setRelPcoItens(List<RelComItem> relPcoItens) {
		this.relPcoItens = relPcoItens;
	}

	public List<RelComItem> getRelPsoItens() {
		return relPsoItens;
	}

	public void setRelPsoItens(List<RelComItem> relPsoItens) {
		this.relPsoItens = relPsoItens;
	}

	public String getTxtInscD() {
		return txtInscD;
	}

	public void setTxtInscD(String txtInscD) {
		this.txtInscD = txtInscD;
	}

	public int getTxtClassD() {
		return txtClassD;
	}

	public void setTxtClassD(int txtClassD) {
		this.txtClassD = txtClassD;
	}

	public boolean isIndrRPagarLiq() {
		return indrRPagarLiq;
	}

	public void setIndrRPagarLiq(boolean indrRPagarLiq) {
		this.indrRPagarLiq = indrRPagarLiq;
	}

	public List<RelSemItem> getRelCreditos() {
		return relCreditos;
	}

	public void setRelCreditos(List<RelSemItem> relCreditos) {
		this.relCreditos = relCreditos;
	}

	

}
