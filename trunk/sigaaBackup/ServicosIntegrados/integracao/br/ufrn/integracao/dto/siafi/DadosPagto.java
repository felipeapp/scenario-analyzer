package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("dadosPgto")
public class DadosPagto {
	
	@XStreamAlias("codCredorDevedor")
	private String codCredorDevedor;
	
	@XStreamAlias("vlr")
	private BigDecimal vlr;
	
	@XStreamAlias("itemRecolhimento")
	private ItemRecolhimento itemRecolhimento;
	
	@XStreamAlias("predoc")
	private PreDoc predoc;
	
	@XStreamImplicit(itemFieldName="acrescimo")
	private List<Acrescimo> acrescimos;

	public String getCodCredorDevedor() {
		return codCredorDevedor;
	}

	public void setCodCredorDevedor(String codCredorDevedor) {
		this.codCredorDevedor = codCredorDevedor;
	}

	public BigDecimal getVlr() {
		return vlr;
	}

	public void setVlr(BigDecimal vlr) {
		this.vlr = vlr;
	}

	public ItemRecolhimento getItemRecolhimento() {
		return itemRecolhimento;
	}

	public void setItemRecolhimento(ItemRecolhimento itemRecolhimento) {
		this.itemRecolhimento = itemRecolhimento;
	}

	public PreDoc getPredoc() {
		return predoc;
	}

	public void setPredoc(PreDoc predoc) {
		this.predoc = predoc;
	}

	public List<Acrescimo> getAcrescimos() {
		return acrescimos;
	}

	public void setAcrescimos(List<Acrescimo> acrescimos) {
		this.acrescimos = acrescimos;
	}

}
