package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("itemRecolhimento")
public class ItemRecolhimento {
	
	@XStreamAlias("numSeqItem")
	private long numSeqItem;//Obrigatório
	
	@XStreamAlias("codRecolhedor")
	private String codRecolhedor;//Obrigatório
	
	@XStreamAlias("vlr")
	private BigDecimal vlr;//Obrigatório
	
	@XStreamAlias("vlrBaseCalculo")
	private BigDecimal vlrBaseCalculo;
	
	@XStreamAlias("vlrMulta")
	private BigDecimal vlrMulta;
	
	@XStreamAlias("vlrJuros")
	private BigDecimal vlrJuros;
	
	@XStreamAlias("vlrOutrasEnt")
	private BigDecimal vlrOutrasEnt;
	
	@XStreamAlias("vlrAtmMultaJuros")
	private BigDecimal vlrAtmMultaJuros;

	public long getNumSeqItem() {
		return numSeqItem;
	}

	public void setNumSeqItem(long numSeqItem) {
		this.numSeqItem = numSeqItem;
	}

	public String getCodRecolhedor() {
		return codRecolhedor;
	}

	public void setCodRecolhedor(String codRecolhedor) {
		this.codRecolhedor = codRecolhedor;
	}

	public BigDecimal getVlr() {
		return vlr;
	}

	public void setVlr(BigDecimal vlr) {
		this.vlr = vlr;
	}

	public BigDecimal getVlrBaseCalculo() {
		return vlrBaseCalculo;
	}

	public void setVlrBaseCalculo(BigDecimal vlrBaseCalculo) {
		this.vlrBaseCalculo = vlrBaseCalculo;
	}

	public BigDecimal getVlrMulta() {
		return vlrMulta;
	}

	public void setVlrMulta(BigDecimal vlrMulta) {
		this.vlrMulta = vlrMulta;
	}

	public BigDecimal getVlrJuros() {
		return vlrJuros;
	}

	public void setVlrJuros(BigDecimal vlrJuros) {
		this.vlrJuros = vlrJuros;
	}

	public BigDecimal getVlrOutrasEnt() {
		return vlrOutrasEnt;
	}

	public void setVlrOutrasEnt(BigDecimal vlrOutrasEnt) {
		this.vlrOutrasEnt = vlrOutrasEnt;
	}

	public BigDecimal getVlrAtmMultaJuros() {
		return vlrAtmMultaJuros;
	}

	public void setVlrAtmMultaJuros(BigDecimal vlrAtmMultaJuros) {
		this.vlrAtmMultaJuros = vlrAtmMultaJuros;
	}

}
