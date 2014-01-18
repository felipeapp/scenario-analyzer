package br.ufrn.integracao.dto.siafi;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamImplicitCollection;

@XStreamAlias("ns2:CprDhConsultar")
public class CprDHCadastrar {
	
	//Cabeçalho	
	@XStreamAlias("codUgEmit")
	private int codUgEmit;//Obrigatório
	
	@XStreamAlias("anoDH")
	private int anoDH;//Obrigatório	

	@XStreamAlias("codTipoDH")
	private String codTipoDH;//Obrigatório	
	
	@XStreamAlias("numDH")
	private int numDH;
	
	@XStreamAlias("dadosBasicos")
	private DadosBasicos dadosBasicos;//Obrigatório
	
	@XStreamImplicit(itemFieldName="pco")
	private List<Pco> pcos;
	
	@XStreamImplicit(itemFieldName="pso")
	private List<Pso> psos;
	
	@XStreamImplicit(itemFieldName="credito")
	private List<Credito> creditos;
	
	@XStreamImplicit(itemFieldName="outrosLanc")
	private List<OutrosLanc> outrosLanc;
	
	@XStreamImplicit(itemFieldName="deducao")
	private List<Deducao> deducoes;
	
	@XStreamImplicit(itemFieldName="encargo")
	private List<Encargo> encargos;
	
	@XStreamImplicit(itemFieldName="varPatrimonial")
	private List<VarPatrimonial> varPatrimoniais;
	
	@XStreamImplicit(itemFieldName="despesaAnular")
	private List<DespesaAnular> despesasAnular;
	
	@XStreamImplicit(itemFieldName="compensacao")
	private List<Compensacao> compensacoes;

	@XStreamImplicit(itemFieldName="centroCusto")
	private List<CentroCusto> centrosCusto;
	
	@XStreamImplicit(itemFieldName="dadosPgto")
	private List<DadosPagto> dadosPagtos;
	
	@XStreamImplicit(itemFieldName="docContabilizacao")
	private List<DocContabilizacao> docsContabilizacao;
	

	public int getCodUgEmit() {
		return codUgEmit;
	}

	public void setCodUgEmit(int codUgEmit) {
		this.codUgEmit = codUgEmit;
	}

	public int getAnoDH() {
		return anoDH;
	}

	public void setAnoDH(int anoDH) {
		this.anoDH = anoDH;
	}

	public String getCodTipoDH() {
		return codTipoDH;
	}

	public void setCodTipoDH(String codTipoDH) {
		this.codTipoDH = codTipoDH;
	}

	public int getNumDH() {
		return numDH;
	}

	public void setNumDH(int numDH) {
		this.numDH = numDH;
	}

	public DadosBasicos getDadosBasicos() {
		return dadosBasicos;
	}

	public void setDadosBasicos(DadosBasicos dadosBasicos) {
		this.dadosBasicos = dadosBasicos;
	}

	public List<Pco> getPcos() {
		return pcos;
	}

	public void setPcos(List<Pco> pcos) {
		this.pcos = pcos;
	}

	public List<Pso> getPsos() {
		return psos;
	}

	public void setPsos(List<Pso> psos) {
		this.psos = psos;
	}

	public List<OutrosLanc> getOutrosLanc() {
		return outrosLanc;
	}

	public void setOutrosLanc(List<OutrosLanc> outrosLanc) {
		this.outrosLanc = outrosLanc;
	}

	public List<Deducao> getDeducoes() {
		return deducoes;
	}

	public void setDeducoes(List<Deducao> deducoes) {
		this.deducoes = deducoes;
	}

	public List<Encargo> getEncargos() {
		return encargos;
	}

	public void setEncargos(List<Encargo> encargos) {
		this.encargos = encargos;
	}

	public List<CentroCusto> getCentrosCusto() {
		return centrosCusto;
	}

	public void setCentrosCusto(List<CentroCusto> centrosCusto) {
		this.centrosCusto = centrosCusto;
	}

	public List<DadosPagto> getDadosPagtos() {
		return dadosPagtos;
	}

	public void setDadosPagtos(List<DadosPagto> dadosPagtos) {
		this.dadosPagtos = dadosPagtos;
	}

	public List<DocContabilizacao> getDocsContabilizacao() {
		return docsContabilizacao;
	}

	public void setDocsContabilizacao(List<DocContabilizacao> docsContabilizacao) {
		this.docsContabilizacao = docsContabilizacao;
	}

	public List<Credito> getCreditos() {
		return creditos;
	}

	public List<VarPatrimonial> getVarPatrimoniais() {
		return varPatrimoniais;
	}

	public void setVarPatrimoniais(List<VarPatrimonial> varPatrimoniais) {
		this.varPatrimoniais = varPatrimoniais;
	}

	public void setCreditos(List<Credito> creditos) {
		this.creditos = creditos;
	}

	public List<DespesaAnular> getDespesasAnular() {
		return despesasAnular;
	}

	public void setDespesasAnular(List<DespesaAnular> despesasAnular) {
		this.despesasAnular = despesasAnular;
	}

	public List<Compensacao> getCompensacoes() {
		return compensacoes;
	}

	public void setCompensacoes(List<Compensacao> compensacoes) {
		this.compensacoes = compensacoes;
	}

}
