package br.ufrn.integracao.dto.siafi;

import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("tramite")
public class Tramite {
	
	@XStreamAlias("txtLocal")
	private String txtLocal;//Obrigatório
	
	@XStreamAlias("dtEntrada")
	private Date dtEntrada;//Obrigatório
	
	@XStreamAlias("dtSaida")
	private Date dtSaida;

	public String getTxtLocal() {
		return txtLocal;
	}

	public void setTxtLocal(String txtLocal) {
		this.txtLocal = txtLocal;
	}

	public Date getDtEntrada() {
		return dtEntrada;
	}

	public void setDtEntrada(Date dtEntrada) {
		this.dtEntrada = dtEntrada;
	}

	public Date getDtSaida() {
		return dtSaida;
	}

	public void setDtSaida(Date dtSaida) {
		this.dtSaida = dtSaida;
	}

}
