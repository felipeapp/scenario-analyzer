package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;
import java.util.Date;

import br.ufrn.integracao.dto.siafi.converters.DateConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("parcela")
public class Parcela {
	
	@XStreamAlias("numParcela")
	private long numParcela;
	
	@XStreamAlias("dtPrevista")
	@XStreamConverter(value=DateConverter.class)
	private Date dtPrevista;
	
	@XStreamAlias("vlr")
	private BigDecimal vlr;

	public long getNumParcela() {
		return numParcela;
	}

	public void setNumParcela(long numParcela) {
		this.numParcela = numParcela;
	}

	public Date getDtPrevista() {
		return dtPrevista;
	}

	public void setDtPrevista(Date dtPrevista) {
		this.dtPrevista = dtPrevista;
	}

	public BigDecimal getVlr() {
		return vlr;
	}

	public void setVlr(BigDecimal vlr) {
		this.vlr = vlr;
	}
	
}
