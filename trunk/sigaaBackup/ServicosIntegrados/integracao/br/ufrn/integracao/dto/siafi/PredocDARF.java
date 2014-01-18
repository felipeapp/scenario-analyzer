package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;
import java.util.Date;

import br.ufrn.integracao.dto.siafi.converters.DateConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("predocDARF")
public class PredocDARF {
	
	@XStreamAlias("codRecurso")
	private String codRecurso;//Obrigatório
	
	@XStreamAlias("vlrPercentual")
	private BigDecimal vlrPercentual;
	
	//DateTime
	@XStreamAlias("dtPrdoApuracao")
	@XStreamConverter(value=DateConverter.class)
	private Date dtPrdoApuracao;//Obrigatório
	
	@XStreamAlias("numRef")
	private String numRef;//Obrigatório
	
	@XStreamAlias("vlrRctaBrutaAcum")
	private BigDecimal vlrRctaBrutaAcum;//Obrigatório
	
	@XStreamAlias("txtProcesso")
	private String txtProcesso;

	public String getCodRecurso() {
		return codRecurso;
	}

	public void setCodRecurso(String codRecurso) {
		this.codRecurso = codRecurso;
	}

	public BigDecimal getVlrPercentual() {
		return vlrPercentual;
	}

	public void setVlrPercentual(BigDecimal vlrPercentual) {
		this.vlrPercentual = vlrPercentual;
	}

	public String getNumRef() {
		return numRef;
	}

	public void setNumRef(String numRef) {
		this.numRef = numRef;
	}

	public String getTxtProcesso() {
		return txtProcesso;
	}

	public void setTxtProcesso(String txtProcesso) {
		this.txtProcesso = txtProcesso;
	}

	public Date getDtPrdoApuracao() {
		return dtPrdoApuracao;
	}

	public void setDtPrdoApuracao(Date dtPrdoApuracao) {
		this.dtPrdoApuracao = dtPrdoApuracao;
	}

	public BigDecimal getVlrRctaBrutaAcum() {
		return vlrRctaBrutaAcum;
	}

	public void setVlrRctaBrutaAcum(BigDecimal vlrRctaBrutaAcum) {
		this.vlrRctaBrutaAcum = vlrRctaBrutaAcum;
	}

}
