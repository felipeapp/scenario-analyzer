package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("predocOB")
public class PredocOB {
	
	@XStreamAlias("codTipoOB")
	private String codTipoOB;//Obrigatório
	
	@XStreamAlias("codCredorDevedor")
	private String codCredorDevedor;
	
	@XStreamAlias("codNumLista")
	private String codNumLista;
	
	@XStreamAlias("txtCit")
	private String txtCit;
	
	@XStreamAlias("numDomiBancFavo")
	private String numDomiBancFavo;//Obrigatório
	
	@XStreamAlias("numDomiBancPgto")
	private String numDomiBancPagto;//Obrigatório
	
	@XStreamAlias("codRecoGru")
	private int codRecoGru;
	
	@XStreamAlias("codUgRaGru")
	private int codUGRaGru;
	
	@XStreamAlias("numRaGru")
	private String numRaGru;
	
	@XStreamAlias("codRecDarf")
	private int codRecDarf;
	
	@XStreamAlias("numRefDarf")
	private int numRefDarf;
	
	@XStreamAlias("codContRepas")
	private int codContRepas;
	
	@XStreamAlias("codEvntBacen")
	private String codEvntBacen;
	
	@XStreamAlias("codFinalidade")
	private int codFinalidade;
	
	@XStreamAlias("txtCtrlOriginal")
	private String txtCtrlOriginal;
	
	@XStreamAlias("vlrTavaCambio")
	private BigDecimal vlrTavaCambio;
	
	@XStreamAlias("txtProcesso")
	private String txtProcesso;
	
	@XStreamAlias("coddevolucaoSPB")
	private int coddevolucaoSPB;
	
	public String getCodTipoOB() {
		return codTipoOB;
	}

	public void setCodTipoOB(String codTipoOB) {
		this.codTipoOB = codTipoOB;
	}

	public String getCodCredorDevedor() {
		return codCredorDevedor;
	}

	public void setCodCredorDevedor(String codCredorDevedor) {
		this.codCredorDevedor = codCredorDevedor;
	}

	public String getCodNumLista() {
		return codNumLista;
	}

	public void setCodNumLista(String codNumLista) {
		this.codNumLista = codNumLista;
	}

	public String getTxtCit() {
		return txtCit;
	}

	public void setTxtCit(String txtCit) {
		this.txtCit = txtCit;
	}

	public String getNumDomiBancFavo() {
		return numDomiBancFavo;
	}

	public void setNumDomiBancFavo(String numDomiBancFavo) {
		this.numDomiBancFavo = numDomiBancFavo;
	}

	public String getNumDomiBancPagto() {
		return numDomiBancPagto;
	}

	public void setNumDomiBancPagto(String numDomiBancPagto) {
		this.numDomiBancPagto = numDomiBancPagto;
	}

	public int getCodRecoGru() {
		return codRecoGru;
	}

	public void setCodRecoGru(int codRecoGru) {
		this.codRecoGru = codRecoGru;
	}

	public int getCodUGRaGru() {
		return codUGRaGru;
	}

	public void setCodUGRaGru(int codUGRaGru) {
		this.codUGRaGru = codUGRaGru;
	}

	public String getNumRaGru() {
		return numRaGru;
	}

	public void setNumRaGru(String numRaGru) {
		this.numRaGru = numRaGru;
	}

	public int getCodRecDarf() {
		return codRecDarf;
	}

	public void setCodRecDarf(int codRecDarf) {
		this.codRecDarf = codRecDarf;
	}

	public int getNumRefDarf() {
		return numRefDarf;
	}

	public void setNumRefDarf(int numRefDarf) {
		this.numRefDarf = numRefDarf;
	}

	public int getCodContRepas() {
		return codContRepas;
	}

	public void setCodContRepas(int codContRepas) {
		this.codContRepas = codContRepas;
	}

	public String getCodEvntBacen() {
		return codEvntBacen;
	}

	public void setCodEvntBacen(String codEvntBacen) {
		this.codEvntBacen = codEvntBacen;
	}

	public int getCodFinalidade() {
		return codFinalidade;
	}

	public void setCodFinalidade(int codFinalidade) {
		this.codFinalidade = codFinalidade;
	}

	public String getTxtCtrlOriginal() {
		return txtCtrlOriginal;
	}

	public void setTxtCtrlOriginal(String txtCtrlOriginal) {
		this.txtCtrlOriginal = txtCtrlOriginal;
	}

	public BigDecimal getVlrTavaCambio() {
		return vlrTavaCambio;
	}

	public void setVlrTavaCambio(BigDecimal vlrTavaCambio) {
		this.vlrTavaCambio = vlrTavaCambio;
	}

	public String getTxtProcesso() {
		return txtProcesso;
	}

	public void setTxtProcesso(String txtProcesso) {
		this.txtProcesso = txtProcesso;
	}

	public int getCoddevolucaoSPB() {
		return coddevolucaoSPB;
	}

	public void setCoddevolucaoSPB(int coddevolucaoSPB) {
		this.coddevolucaoSPB = coddevolucaoSPB;
	}
	
	

}
