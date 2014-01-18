package br.ufrn.integracao.dto.siafi;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("docContabilizacao")
public class DocContabilizacao {
	
	@XStreamAlias("anoDocCont")
	private int anoDocCont;//Obrigat�rio
	
	@XStreamAlias("codTipoDocCont")
	private String codTipoDocCont;//Obrigat�rio
	
	@XStreamAlias("numDocCont")
	private String numDocCont;//Obrigat�rio
	
	@XStreamAlias("codUgEmit")
	private int codUgEmit;//Obrigat�rio

	public int getAnoDocCont() {
		return anoDocCont;
	}

	public void setAnoDocCont(int anoDocCont) {
		this.anoDocCont = anoDocCont;
	}

	public String getCodTipoDocCont() {
		return codTipoDocCont;
	}

	public void setCodTipoDocCont(String codTipoDocCont) {
		this.codTipoDocCont = codTipoDocCont;
	}

	public String getNumDocCont() {
		return numDocCont;
	}

	public void setNumDocCont(String numDocCont) {
		this.numDocCont = numDocCont;
	}

	public int getCodUgEmit() {
		return codUgEmit;
	}

	public void setCodUgEmit(int codUgEmit) {
		this.codUgEmit = codUgEmit;
	}

}
