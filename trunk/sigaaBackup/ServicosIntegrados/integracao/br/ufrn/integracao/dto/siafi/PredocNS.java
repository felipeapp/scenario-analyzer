package br.ufrn.integracao.dto.siafi;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("predocNS")
public class PredocNS {
	
	@XStreamAlias("codCredorDevedor")
	private String codCredorDevedor;//Obrigatório
	
	@XStreamAlias("numDomiBancPago")
	private String numDomiBancPago;
	
	@XStreamAlias("numDomiBancFavo")
	private String numDomiBancFavo;

	public String getCodCredorDevedor() {
		return codCredorDevedor;
	}

	public void setCodCredorDevedor(String codCredorDevedor) {
		this.codCredorDevedor = codCredorDevedor;
	}

	public String getNumDomiBancPago() {
		return numDomiBancPago;
	}

	public void setNumDomiBancPago(String numDomiBancPago) {
		this.numDomiBancPago = numDomiBancPago;
	}

	public String getNumDomiBancFavo() {
		return numDomiBancFavo;
	}

	public void setNumDomiBancFavo(String numDomiBancFavo) {
		this.numDomiBancFavo = numDomiBancFavo;
	}

}
