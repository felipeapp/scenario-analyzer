package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;
import java.util.Date;

import br.ufrn.integracao.dto.siafi.converters.DateConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("predocDAR")
public class PredocDAR {
	
	@XStreamAlias("codRecurso")
	private String codRecurso;//Obrigatório
	
	@XStreamAlias("mesReferencia")
	private String mesReferencia;//Obrigatório
	
	@XStreamAlias("anoReferencia")
	private String anoReferencia;//Obrigatório
	
	@XStreamAlias("codUgTmdrServ")
	private int codUgTmdrServ;//Obrigatório
	
	@XStreamAlias("numNf")
	private int numNf;//Obrigatório
	
	@XStreamAlias("txtSerieNf")
	private String txtSerieNf;
	
	@XStreamAlias("numSubSerieNf")
	private int numSubSerieNf;
	
	@XStreamAlias("codMuniNf")
	private int codMuniNf;//Obrigatório
	
	//Datetime
	@XStreamAlias("dtEmisNf")
	@XStreamConverter(value=DateConverter.class)
	private Date dtEmisNf;//Obrigatório
	
	@XStreamAlias("vlrNf")
	private BigDecimal vlrNf;//Obrigatório
	
	@XStreamAlias("numAliqNf")
	private BigDecimal numAliqNf;//Obrigatório

	public String getCodRecurso() {
		return codRecurso;
	}

	public void setCodRecurso(String codRecurso) {
		this.codRecurso = codRecurso;
	}

	public String getMesReferencia() {
		return mesReferencia;
	}

	public void setMesReferencia(String mesReferencia) {
		this.mesReferencia = mesReferencia;
	}

	public String getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(String anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	public int getCodUgTmdrServ() {
		return codUgTmdrServ;
	}

	public void setCodUgTmdrServ(int codUgTmdrServ) {
		this.codUgTmdrServ = codUgTmdrServ;
	}

	public int getNumNf() {
		return numNf;
	}

	public void setNumNf(int numNf) {
		this.numNf = numNf;
	}

	public String getTxtSerieNf() {
		return txtSerieNf;
	}

	public void setTxtSerieNf(String txtSerieNf) {
		this.txtSerieNf = txtSerieNf;
	}

	public int getNumSubSerieNf() {
		return numSubSerieNf;
	}

	public void setNumSubSerieNf(int numSubSerieNf) {
		this.numSubSerieNf = numSubSerieNf;
	}

	public int getCodMuniNf() {
		return codMuniNf;
	}

	public void setCodMuniNf(int codMuniNf) {
		this.codMuniNf = codMuniNf;
	}

	public Date getDtEmisNf() {
		return dtEmisNf;
	}

	public void setDtEmisNf(Date dtEmisNf) {
		this.dtEmisNf = dtEmisNf;
	}

	public BigDecimal getVlrNf() {
		return vlrNf;
	}

	public void setVlrNf(BigDecimal vlrNf) {
		this.vlrNf = vlrNf;
	}

	public BigDecimal getNumAliqNf() {
		return numAliqNf;
	}

	public void setNumAliqNf(BigDecimal numAliqNf) {
		this.numAliqNf = numAliqNf;
	}


}
