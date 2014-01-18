package br.ufrn.integracao.dto.siafi;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("docRelacionado")
public class DocRelacionado {
	
	@XStreamAlias("codUgEmit")
	private int codUgEmit;//Obrigatório
	
	@XStreamAlias("numDocRelacionado")
	private String numDocRelacionado;//Obrigatório

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
