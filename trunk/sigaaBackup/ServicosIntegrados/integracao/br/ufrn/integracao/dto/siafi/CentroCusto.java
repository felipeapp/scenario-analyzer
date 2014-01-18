package br.ufrn.integracao.dto.siafi;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("centroCusto")
public class CentroCusto {
	
	@XStreamAlias("numSeqItem")
	private long numSeqItem;//Obrigatório
	
	@XStreamAlias("codCentroCusto")
	private String codCentroCusto;//Obrigatório
	
	@XStreamAlias("mesReferencia")
	private int mesReferencia;//Obrigatório
	
	@XStreamAlias("anoReferencia")
	private int anoReferencia;//Obrigatório
	
	@XStreamAlias("codUgBenef")
	private int codUgBenef;//Obrigatório
	
	@XStreamImplicit(itemFieldName="relPcoItem")
	private List<RelComItemValor> relPcoItens;
	
	@XStreamImplicit(itemFieldName="relOutrosLanc")
	private List<RelSemItemValor> relOutrosLanc;

	@XStreamImplicit(itemFieldName="relPsoItem")
	private List<RelComItemValor> relPsoItens;
	
	@XStreamImplicit(itemFieldName="relVpItem")
	private List<RelComItemValor> relVpItens;
	
	@XStreamImplicit(itemFieldName="relEncargo")
	private List<RelSemItemValor> relEncargos;
	
	@XStreamImplicit(itemFieldName="relAcrescimoDeducao")
	private List<RelComItemValor> relAcrescimoDeducoes;
	
	@XStreamImplicit(itemFieldName="relAcrescimoEncargo")
	private List<RelComItemValor> relAcrescimoEncargos;
	
	@XStreamImplicit(itemFieldName="relAcrescimoDadosPag")
	private List<RelComItemValor> relAcrescimoDadosPags;
	
	@XStreamImplicit(itemFieldName="relDespesaAntecipada")
	private List<RelComItemValor> relDespesaAntecipada;
	
	@XStreamImplicit(itemFieldName="relDespesaAnular")
	private List<RelComItemValor> relDespesaAnular;

	public long getNumSeqItem() {
		return numSeqItem;
	}

	public void setNumSeqItem(long numSeqItem) {
		this.numSeqItem = numSeqItem;
	}

	public String getCodCentroCusto() {
		return codCentroCusto;
	}

	public void setCodCentroCusto(String codCentroCusto) {
		this.codCentroCusto = codCentroCusto;
	}

	public int getMesReferencia() {
		return mesReferencia;
	}

	public void setMesReferencia(int mesReferencia) {
		this.mesReferencia = mesReferencia;
	}

	public int getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(int anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	public int getCodUgBenef() {
		return codUgBenef;
	}

	public void setCodUgBenef(int codUgBenef) {
		this.codUgBenef = codUgBenef;
	}

	public List<RelSemItemValor> getRelOutrosLanc() {
		return relOutrosLanc;
	}

	public void setRelOutrosLanc(List<RelSemItemValor> relOutrosLanc) {
		this.relOutrosLanc = relOutrosLanc;
	}

	public List<RelComItemValor> getRelVpItens() {
		return relVpItens;
	}

	public void setRelVpItens(List<RelComItemValor> relVpItens) {
		this.relVpItens = relVpItens;
	}

	public List<RelComItemValor> getRelDespesaAntecipada() {
		return relDespesaAntecipada;
	}

	public void setRelDespesaAntecipada(List<RelComItemValor> relDespesaAntecipada) {
		this.relDespesaAntecipada = relDespesaAntecipada;
	}

	public List<RelComItemValor> getRelDespesaAnular() {
		return relDespesaAnular;
	}

	public void setRelDespesaAnular(List<RelComItemValor> relDespesaAnular) {
		this.relDespesaAnular = relDespesaAnular;
	}

	public List<RelComItemValor> getRelPcoItens() {
		return relPcoItens;
	}

	public void setRelPcoItens(List<RelComItemValor> relPcoItens) {
		this.relPcoItens = relPcoItens;
	}

	public List<RelComItemValor> getRelPsoItens() {
		return relPsoItens;
	}

	public void setRelPsoItens(List<RelComItemValor> relPsoItens) {
		this.relPsoItens = relPsoItens;
	}

	public List<RelSemItemValor> getRelEncargos() {
		return relEncargos;
	}

	public void setRelEncargos(List<RelSemItemValor> relEncargos) {
		this.relEncargos = relEncargos;
	}

	public List<RelComItemValor> getRelAcrescimoDeducoes() {
		return relAcrescimoDeducoes;
	}

	public void setRelAcrescimoDeducoes(List<RelComItemValor> relAcrescimoDeducoes) {
		this.relAcrescimoDeducoes = relAcrescimoDeducoes;
	}

	public List<RelComItemValor> getRelAcrescimoEncargos() {
		return relAcrescimoEncargos;
	}

	public void setRelAcrescimoEncargos(List<RelComItemValor> relAcrescimoEncargos) {
		this.relAcrescimoEncargos = relAcrescimoEncargos;
	}

	public List<RelComItemValor> getRelAcrescimoDadosPags() {
		return relAcrescimoDadosPags;
	}

	public void setRelAcrescimoDadosPags(List<RelComItemValor> relAcrescimoDadosPags) {
		this.relAcrescimoDadosPags = relAcrescimoDadosPags;
	}

}
