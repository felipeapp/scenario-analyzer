/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.form;

import org.apache.struts.upload.FormFile;

import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.pesquisa.dominio.CategoriaProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.projetos.dominio.Edital;

/**
 * Form para manipulações com Editais de Pesquisa
 * 
 * @author Leonardo Campos
 */
public class EditalPesquisaForm extends SigaaForm<EditalPesquisa> {
	
	/** Arquivo do edital */
	private FormFile arquivoEdital;
	
	/** String com as datas do período de submissão de projetos */
	private String inicioSubmissao;
	private String fimSubmissao;
	
	private String fppiMinimo;
	
	private TipoBolsaPesquisa tipoBolsa;
	private int quantidade;
	
	/** Utilizado para indicar se é o cadastro de um edital associado */
	private boolean editalAssociado;
	
	public EditalPesquisaForm() {
		this.obj = new EditalPesquisa();
		this.obj.setCota( new CotaBolsas() );
		this.tipoBolsa = new TipoBolsaPesquisa();
		this.obj.setCategoria( new CategoriaProjetoPesquisa() );
		this.obj.setTipo(Edital.PESQUISA);
	}
	
	/**
	 * @return the arquivoEdital
	 */
	public FormFile getArquivoEdital() {
		return arquivoEdital;
	}

	/**
	 * @return the fimSubmissao
	 */
	public String getFimSubmissao() {
		EditalPesquisa edital = obj;
		if(edital.getFimSubmissao() != null)
			fimSubmissao = Formatador.getInstance().formatarData( edital.getFimSubmissao() );
		return fimSubmissao;
	}

	/**
	 * @return the inicioSubmissao
	 */
	public String getInicioSubmissao() {
		EditalPesquisa edital = obj;
		if(edital.getInicioSubmissao() != null)
			inicioSubmissao = Formatador.getInstance().formatarData( edital.getInicioSubmissao() );
		return inicioSubmissao;
	}

	/**
	 * @param arquivoEdital the arquivoEdital to set
	 */
	public void setArquivoEdital(FormFile arquivoEdital) {
		this.arquivoEdital = arquivoEdital;
	}

	/**
	 * @param fimSubmissao the fimSubmissao to set
	 */
	public void setFimSubmissao(String fimSubmissao) {
		EditalPesquisa edital = obj;
		edital.setFimSubmissao(parseDate(fimSubmissao));
		this.fimSubmissao = fimSubmissao;
	}

	/**
	 * @param inicioSubmissao the inicioSubmissao to set
	 */
	public void setInicioSubmissao(String inicioSubmissao) {
		EditalPesquisa edital = obj;
		edital.setInicioSubmissao(parseDate(inicioSubmissao));
		this.inicioSubmissao = inicioSubmissao;
	}

	public TipoBolsaPesquisa getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(TipoBolsaPesquisa tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public String getFppiMinimo() {
		if(obj.getFppiMinimo() > 0)
			fppiMinimo = Formatador.getInstance().formatarMoeda(obj.getFppiMinimo());
		return fppiMinimo;
	}

	public void setFppiMinimo(String fppiMinimo) {
		obj.setFppiMinimo(parseValor(fppiMinimo));
		this.fppiMinimo = fppiMinimo;
	}

	public boolean isEditalAssociado() {
		return editalAssociado;
	}

	public void setEditalAssociado(boolean editalAssociado) {
		this.editalAssociado = editalAssociado;
	}
}
