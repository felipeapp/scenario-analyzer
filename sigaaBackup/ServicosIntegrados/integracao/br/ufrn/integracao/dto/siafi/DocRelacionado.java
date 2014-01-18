package br.ufrn.integracao.dto.siafi;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("docRelacionado")
public class DocRelacionado {
	
	@XStreamAlias("codUgEmit")
	private int codUgEmit;//Obrigat�rio
	
	@XStreamAlias("numDocRelacionado")
	private String numDocRelacionado;//Obrigat�rio

	public int getCodUgEmit() {
		return codUgEmit;
	}

	public void setCodUgEmit(int codUgEmit) {
		this.codUgEmit = codUgEmit;
	}

	public String getNumDocRelacionado() {
		return numDocRelacionado;
	}

	public void setNumDocRelacionado(String numDocRelacionado) {
		this.numDocRelacionado = numDocRelacionado;
	}

}
