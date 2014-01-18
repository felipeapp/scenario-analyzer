package br.ufrn.integracao.dto.siafi;

import java.math.BigDecimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("predocGFIP")
public class PredocGFIP {
	
	@XStreamAlias("codRecurso")
	private String codRecurso;//Obrigatório
	
	@XStreamAlias("numCodBarras")
	private String numCodBarras;
	
	@XStreamAlias("codAgencia")
	private int codAgencia;//Obrigatório
	
	@XStreamAlias("numIdentGfip")
	private int numIdentGfip;
	
	@XStreamAlias("numIdRecolhimento")
	private String numIdRecolhimento;//Obrigatório
	
	@XStreamAlias("codFpas")
	private int codFpas;//Obrigatório
	
	@XStreamAlias("codEntidades")
	private int codEntidades;//Obrigatório
	
	@XStreamAlias("indrSimples")
	private boolean indrSimples;//Obrigatório
	
	@XStreamAlias("numQtdTrabalhador")
	private int numQtdTrabalhador;//Obrigatório
	
	@XStreamAlias("vlrRmesFgts")
	private BigDecimal vlrRmesFgts;
	
	@XStreamAlias("vlrRmesCat")
	private BigDecimal vlrRmesCat;
	
	@XStreamAlias("vlrMensInss")
	private BigDecimal vlrMensInss;
	
	@XStreamAlias("vlr13SalrInss")
	private BigDecimal vlr13SalrInss;
	
	@XStreamAlias("vlrContSegDev")
	private BigDecimal vlrContSegDev;
	
	@XStreamAlias("vlrPrevSocial")
	private BigDecimal vlrPrevSocial;
	
	@XStreamAlias("vlrContSegDesc")
	private BigDecimal vlrContSegDesc;
	
	@XStreamAlias("vlrDepContSocial")
	private BigDecimal vlrDepContSocial;
	
	@XStreamAlias("vlrEncargos")
	private BigDecimal vlrEncargos;

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

	public int getCodAgencia() {
		return codAgencia;
	}

	public void setCodAgencia(int codAgencia) {
		this.codAgencia = codAgencia;
	}

	public int getNumIdentGfip() {
		return numIdentGfip;
	}

	public void setNumIdentGfip(int numIdentGfip) {
		this.numIdentGfip = numIdentGfip;
	}

	public String getNumIdRecolhimento() {
		return numIdRecolhimento;
	}

	public void setNumIdRecolhimento(String numIdRecolhimento) {
		this.numIdRecolhimento = numIdRecolhimento;
	}

	public int getCodFpas() {
		return codFpas;
	}

	public void setCodFpas(int codFpas) {
		this.codFpas = codFpas;
	}

	public int getCodEntidades() {
		return codEntidades;
	}

	public void setCodEntidades(int codEntidades) {
		this.codEntidades = codEntidades;
	}

	public boolean isIndrSimples() {
		return indrSimples;
	}

	public void setIndrSimples(boolean indrSimples) {
		this.indrSimples = indrSimples;
	}

	public int getNumQtdTrabalhador() {
		return numQtdTrabalhador;
	}

	public void setNumQtdTrabalhador(int numQtdTrabalhador) {
		this.numQtdTrabalhador = numQtdTrabalhador;
	}

	public BigDecimal getVlrRmesFgts() {
		return vlrRmesFgts;
	}

	public void setVlrRmesFgts(BigDecimal vlrRmesFgts) {
		this.vlrRmesFgts = vlrRmesFgts;
	}

	public BigDecimal getVlrRmesCat() {
		return vlrRmesCat;
	}

	public void setVlrRmesCat(BigDecimal vlrRmesCat) {
		this.vlrRmesCat = vlrRmesCat;
	}

	public BigDecimal getVlrMensInss() {
		return vlrMensInss;
	}

	public void setVlrMensInss(BigDecimal vlrMensInss) {
		this.vlrMensInss = vlrMensInss;
	}

	public BigDecimal getVlr13SalrInss() {
		return vlr13SalrInss;
	}

	public void setVlr13SalrInss(BigDecimal vlr13SalrInss) {
		this.vlr13SalrInss = vlr13SalrInss;
	}

	public BigDecimal getVlrContSegDev() {
		return vlrContSegDev;
	}

	public void setVlrContSegDev(BigDecimal vlrContSegDev) {
		this.vlrContSegDev = vlrContSegDev;
	}

	public BigDecimal getVlrPrevSocial() {
		return vlrPrevSocial;
	}

	public void setVlrPrevSocial(BigDecimal vlrPrevSocial) {
		this.vlrPrevSocial = vlrPrevSocial;
	}

	public BigDecimal getVlrContSegDesc() {
		return vlrContSegDesc;
	}

	public void setVlrContSegDesc(BigDecimal vlrContSegDesc) {
		this.vlrContSegDesc = vlrContSegDesc;
	}

	public BigDecimal getVlrDepContSocial() {
		return vlrDepContSocial;
	}

	public void setVlrDepContSocial(BigDecimal vlrDepContSocial) {
		this.vlrDepContSocial = vlrDepContSocial;
	}

	public BigDecimal getVlrEncargos() {
		return vlrEncargos;
	}

	public void setVlrEncargos(BigDecimal vlrEncargos) {
		this.vlrEncargos = vlrEncargos;
	}
	
}
