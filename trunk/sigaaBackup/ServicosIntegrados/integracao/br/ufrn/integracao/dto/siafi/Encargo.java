package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.ufrn.integracao.dto.siafi.converters.DateConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;


@XStreamAlias("encargo")
public class Encargo {
	
	@XStreamAlias("numSeqItem")
	private long numSeqItem;//Obrigatório
	
	@XStreamAlias("codSit")
	private String codSit;//Obrigatório
	
	@XStreamAlias("codUgPgto")
	private int codUgPgto;//Obrigatório
	
	@XStreamAlias("vlr")
	private BigDecimal vlr;//Obrigatório
	
	@XStreamAlias("codUgEmpe")
	private int codUgEmpe;//Obrigatório
	
	@XStreamAlias("numEmpe")
	private String numEmpe;//Obrigatório
	
	@XStreamAlias("indrRPagarLiq")
	private boolean indrRPagarLiq;
	
	//Datetime
	@XStreamAlias("dtVenc")
	@XStreamConverter(value=DateConverter.class)
	private Date dtVenc;
	
	//Datetime
	@XStreamAlias("dtPgtoReceb")
	@XStreamConverter(value=DateConverter.class)
	private Date dtPgtoReceb;
	
	@XStreamAlias("codSubItemEmpe")
	private int codSubItemEmpe;
	
	@XStreamAlias("txtInscrA")
	private String txtInscrA;
	
	@XStreamAlias("txtClassA")
	private int txtClassA;
	
	@XStreamAlias("txtInscrB")
	private String txtInscrB;
	
	@XStreamAlias("txtClassB")
	private int txtClassB;
	
	@XStreamAlias("txtInscrC")
	private String txtInscrC;
	
	@XStreamAlias("txtClassC")
	private int txtClassC;
	
	@XStreamImplicit(itemFieldName="itemRecolhimento")
	private List<ItemRecolhimento> itemRecolhimentos;
	
	@XStreamAlias("predoc")
	private PreDoc predoc;
	
	@XStreamImplicit(itemFieldName="acrescimo")
	private List<Acrescimo> acrescimos;
	
	@XStreamImplicit(itemFieldName="relComItem")
	private List<RelComItem> relVpItens;

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

	public int getCodUgPgto() {
		return codUgPgto;
	}

	public void setCodUgPgto(int codUgPgto) {
		this.codUgPgto = codUgPgto;
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

	public String getTxtInscrA() {
		return txtInscrA;
	}

	public void setTxtInscrA(String txtInscrA) {
		this.txtInscrA = txtInscrA;
	}

	public int getTxtClassA() {
		return txtClassA;
	}

	public void setTxtClassA(int txtClassA) {
		this.txtClassA = txtClassA;
	}

	public String getTxtInscrB() {
		return txtInscrB;
	}

	public void setTxtInscrB(String txtInscrB) {
		this.txtInscrB = txtInscrB;
	}

	public int getTxtClassB() {
		return txtClassB;
	}

	public void setTxtClassB(int txtClassB) {
		this.txtClassB = txtClassB;
	}

	public String getTxtInscrC() {
		return txtInscrC;
	}

	public void setTxtInscrC(String txtInscrC) {
		this.txtInscrC = txtInscrC;
	}

	public int getTxtClassC() {
		return txtClassC;
	}

	public void setTxtClassC(int txtClassC) {
		this.txtClassC = txtClassC;
	}

	public PreDoc getPredoc() {
		return predoc;
	}

	public void setPredoc(PreDoc predoc) {
		this.predoc = predoc;
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

	public boolean isIndrRPagarLiq() {
		return indrRPagarLiq;
	}

	public void setIndrRPagarLiq(boolean indrRPagarLiq) {
		this.indrRPagarLiq = indrRPagarLiq;
	}

	public List<RelComItem> getRelVpItens() {
		return relVpItens;
	}

	public void setRelVpItens(List<RelComItem> relVpItens) {
		this.relVpItens = relVpItens;
	}
	
	

}
