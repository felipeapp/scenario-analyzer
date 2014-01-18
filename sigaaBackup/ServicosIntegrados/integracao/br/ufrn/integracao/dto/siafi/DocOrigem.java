package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;
import java.util.Date;

import br.ufrn.integracao.dto.siafi.converters.DateConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("docOrigem")
public class DocOrigem {
	
	@XStreamAlias("codIdentEmit")
	private String codIdentEmit;//Obrigat�rio
	
	@XStreamAlias("dtEmis")
	@XStreamConverter(value=DateConverter.class)
	private Date dtEmis;//Obrigat�rio
	
	@XStreamAlias("numDocOrigem")
	private String numDocOrigem;//Obrigat�rio
	
	@XStreamAlias("vlr")
	private BigDecimal vlr;//Obrigat�rio

	public String getCodIdentEmit() {
		return codIdentEmit;
	}

	public void setCodIdentEmit(String codIdentEmit) {
		this.codIdentEmit = codIdentEmit;
	}

	public Date getDtEmis() {
		return dtEmis;
	}

	public void setDtEmis(Date dtEmis) {
		this.dtEmis = dtEmis;
	}

	public String getNumDocOrigem() {
		return numDocOrigem;
	}

	public void setNumDocOrigem(String numDocOrigem) {
		this.numDocOrigem = numDocOrigem;
	}

	public BigDecimal getVlr() {
		return vlr;
	}

	public void setVlr(BigDecimal vlr) {
		this.vlr = vlr;
	}

}
