package br.ufrn.integracao.dto.siafi;

import java.util.Date;
import java.util.List;

import br.ufrn.integracao.dto.siafi.converters.DateConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("varPatrimonial")
public class VarPatrimonial {
	
	@XStreamAlias("numSeqItem")
	private long numSeqItem;
	
	@XStreamAlias("codSit")
	private String codSit;
	
	@XStreamAlias("dtVenc")
	@XStreamConverter(value=DateConverter.class)
	private Date dtVenc;
	
	@XStreamAlias("indrTemContrato")
	private boolean indrTemContrato;
	
	@XStreamImplicit(itemFieldName="varPatrimonialItem")
	private List<VarPatrimonialItem> itensPatrimoniais;

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

	public boolean isIndrTemContrato() {
		return indrTemContrato;
	}

	public void setIndrTemContrato(boolean indrTemContrato) {
		this.indrTemContrato = indrTemContrato;
	}

	public List<VarPatrimonialItem> getItensPatrimoniais() {
		return itensPatrimoniais;
	}

	public void setItensPatrimoniais(List<VarPatrimonialItem> itensPatrimoniais) {
		this.itensPatrimoniais = itensPatrimoniais;
	}

}
