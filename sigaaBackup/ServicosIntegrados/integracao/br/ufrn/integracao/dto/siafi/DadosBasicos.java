package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.ufrn.integracao.dto.siafi.converters.DateConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("dadosBasicos")
public class DadosBasicos {
	
	@XStreamAlias("dtEmis")
	@XStreamConverter(value=DateConverter.class)
	private Date dtEmis;//Obrigatório
	
	@XStreamAlias("dtVenc")
	@XStreamConverter(value=DateConverter.class)
	private Date dtVenc;
	
	@XStreamAlias("codUgPgto")
	private int codUgPgto;//Obrigatório
	
	@XStreamAlias("vlr")
	private BigDecimal vlr;
	
	@XStreamAlias("txtObser")
	private String txtObser;
	
	@XStreamAlias("txtInfoAdic")
	private String txtInfoAdic;
	
	@XStreamAlias("vlrTaxaCambio")
	private BigDecimal vlrTaxaCambio;
	
	@XStreamAlias("txtProcesso")
	private String txtProcesso;
	
	@XStreamAlias("dtAteste")
	private String dtAteste;
	
	@XStreamAlias("codCredorDevedor")
	private String codCredorDevedor;
	
	@XStreamAlias("dtPgtoReceb")
	private String dtPgtoReceb;
	
	@XStreamImplicit(itemFieldName="docOrigem")
	private List<DocOrigem> docsOrigens;
	
	@XStreamImplicit(itemFieldName="docRelacionado")
	private List<DocRelacionado> docsRelacionados;
	
	@XStreamImplicit(itemFieldName="tramite")
	private List<Tramite> tramites;

	public Date getDtEmis() {
		return dtEmis;
	}

	public void setDtEmis(Date dtEmis) {
		this.dtEmis = dtEmis;
	}

	public Date getDtVenc() {
		return dtVenc;
	}

	public void setDtVenc(Date dtVenc) {
		this.dtVenc = dtVenc;
	}

	public int getCodUgPgto() {
		return codUgPgto;
	}

	public void setCodUgPgto(int codUgPgto) {
		this.codUgPgto = codUgPgto;
	}

	public BigDecimal getVlr() {
		return vlr;
	}

	public void setVlr(BigDecimal vlr) {
		this.vlr = vlr;
	}

	public String getTxtObser() {
		return txtObser;
	}

	public void setTxtObser(String txtObser) {
		this.txtObser = txtObser;
	}

	public String getTxtInfoAdic() {
		return txtInfoAdic;
	}

	public void setTxtInfoAdic(String txtInfoAdic) {
		this.txtInfoAdic = txtInfoAdic;
	}

	public BigDecimal getVlrTaxaCambio() {
		return vlrTaxaCambio;
	}

	public void setVlrTaxaCambio(BigDecimal vlrTaxaCambio) {
		this.vlrTaxaCambio = vlrTaxaCambio;
	}

	public String getTxtProcesso() {
		return txtProcesso;
	}

	public void setTxtProcesso(String txtProcesso) {
		this.txtProcesso = txtProcesso;
	}

	public String getDtAteste() {
		return dtAteste;
	}

	public void setDtAteste(String dtAteste) {
		this.dtAteste = dtAteste;
	}

	public String getCodCredorDevedor() {
		return codCredorDevedor;
	}

	public void setCodCredorDevedor(String codCredorDevedor) {
		this.codCredorDevedor = codCredorDevedor;
	}

	public String getDtPgtoReceb() {
		return dtPgtoReceb;
	}

	public void setDtPgtoReceb(String dtPgtoReceb) {
		this.dtPgtoReceb = dtPgtoReceb;
	}

	public List<DocOrigem> getDocsOrigens() {
		return docsOrigens;
	}

	public void setDocsOrigens(List<DocOrigem> docsOrigens) {
		this.docsOrigens = docsOrigens;
	}

	public List<DocRelacionado> getDocsRelacionados() {
		return docsRelacionados;
	}

	public void setDocsRelacionados(List<DocRelacionado> docsRelacionados) {
		this.docsRelacionados = docsRelacionados;
	}

	public List<Tramite> getTramites() {
		return tramites;
	}

	public void setTramites(List<Tramite> tramites) {
		this.tramites = tramites;
	}
	
	
}
