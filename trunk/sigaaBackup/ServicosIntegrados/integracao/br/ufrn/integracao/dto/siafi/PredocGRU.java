package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("predocGRU")
public class PredocGRU {
	
	@XStreamAlias("codRecurso")
	private String codRecurso;//Obrigatório
	
	@XStreamAlias("numCodBarras")
	private String numCodBarras;
	
	@XStreamAlias("codUgFavorecida")
	private int codUgFavorecida;//Obrigatório
	
	@XStreamAlias("codRecolhedor")
	private String codRecolhedor;
	
	@XStreamAlias("numReferencia")
	private Long numReferencia;
	
	@XStreamAlias("mesCompet")
	private int mesCompet;
	
	@XStreamAlias("anoCompet")
	private int anoCompet;
	
	@XStreamAlias("txtProcesso")
	private String txtProcesso;
	
	@XStreamAlias("txtDocumento")
	private BigDecimal txtDocumento;
	
	@XStreamAlias("txtDesconto")
	private BigDecimal txtDesconto;
	
	@XStreamAlias("txtOutrDeduc")
	private BigDecimal txtOutrDeduc;
	
	@XStreamAlias("codRecolhimento")
	private BigDecimal codRecolhimento;
	
	@XStreamAlias("vlrDocumento")
	private BigDecimal vlrDocumento;
	
	@XStreamAlias("vlrDesconto")
	private BigDecimal vlrDesconto;
	
	@XStreamAlias("vlrOutrDeduc")
	private BigDecimal vlrOutrDeduc;

	public String getCodRecurso() {
		return codRecurso;
	}

	public void setCodRecurso(String codRecurso) {
		this.codRecurso = codRecurso;
	}

	public String getNumCodBarras() {
		return numCodBarras;
	}

	public void setNumCodBarras(String numCodBarras) {
		this.numCodBarras = numCodBarras;
	}

	public int getCodUgFavorecida() {
		return codUgFavorecida;
	}

	public void setCodUgFavorecida(int codUgFavorecida) {
		this.codUgFavorecida = codUgFavorecida;
	}

	public String getCodRecolhedor() {
		return codRecolhedor;
	}

	public void setCodRecolhedor(String codRecolhedor) {
		this.codRecolhedor = codRecolhedor;
	}

	public Long getNumReferencia() {
		return numReferencia;
	}

	public void setNumReferencia(Long numReferencia) {
		this.numReferencia = numReferencia;
	}

	public int getMesCompet() {
		return mesCompet;
	}

	public void setMesCompet(int mesCompet) {
		this.mesCompet = mesCompet;
	}

	public int getAnoCompet() {
		return anoCompet;
	}

	public void setAnoCompet(int anoCompet) {
		this.anoCompet = anoCompet;
	}

	public String getTxtProcesso() {
		return txtProcesso;
	}

	public void setTxtProcesso(String txtProcesso) {
		this.txtProcesso = txtProcesso;
	}

	public BigDecimal getTxtDocumento() {
		return txtDocumento;
	}

	public void setTxtDocumento(BigDecimal txtDocumento) {
		this.txtDocumento = txtDocumento;
	}

	public BigDecimal getTxtDesconto() {
		return txtDesconto;
	}

	public void setTxtDesconto(BigDecimal txtDesconto) {
		this.txtDesconto = txtDesconto;
	}

	public BigDecimal getTxtOutrDeduc() {
		return txtOutrDeduc;
	}

	public void setTxtOutrDeduc(BigDecimal txtOutrDeduc) {
		this.txtOutrDeduc = txtOutrDeduc;
	}

	public BigDecimal getCodRecolhimento() {
		return codRecolhimento;
	}

	public void setCodRecolhimento(BigDecimal codRecolhimento) {
		this.codRecolhimento = codRecolhimento;
	}

	public BigDecimal getVlrDocumento() {
		return vlrDocumento;
	}

	public void setVlrDocumento(BigDecimal vlrDocumento) {
		this.vlrDocumento = vlrDocumento;
	}

	public BigDecimal getVlrDesconto() {
		return vlrDesconto;
	}

	public void setVlrDesconto(BigDecimal vlrDesconto) {
		this.vlrDesconto = vlrDesconto;
	}

	public BigDecimal getVlrOutrDeduc() {
		return vlrOutrDeduc;
	}

	public void setVlrOutrDeduc(BigDecimal vlrOutrDeduc) {
		this.vlrOutrDeduc = vlrOutrDeduc;
	}
	

}
