package br.ufrn.integracao.dto.siafi;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("cronBaixaPatrimonial")
public class CronBaixaPatrimonial {
	
	@XStreamAlias("parcela")
	private Parcela parcela;

	public Parcela getParcela() {
		return parcela;
	}

	public void setParcela(Parcela parcela) {
		this.parcela = parcela;
	}

}