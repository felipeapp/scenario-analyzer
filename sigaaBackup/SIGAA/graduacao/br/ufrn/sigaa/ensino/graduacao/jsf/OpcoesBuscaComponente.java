/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 12/07/2012
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import br.ufrn.sigaa.dominio.Unidade;

/**
 * Opções usadas na busca de componente
 * 
 * @author Henrique André
 *
 */
public class OpcoesBuscaComponente {
	private SeletorComponenteCurricular mBean;
	private String tituloOperacao;
	private Unidade unidade;
	private boolean formaColetiva;
	private int[] tiposValidos;
	private boolean selecionarSubUnidade;

	public SeletorComponenteCurricular getMBean() {
		return mBean;
	}

	public void setMBean(SeletorComponenteCurricular mBean) {
		this.mBean = mBean;
	}

	public String getTituloOperacao() {
		return tituloOperacao;
	}

	public void setTituloOperacao(String tituloOperacao) {
		this.tituloOperacao = tituloOperacao;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public boolean isFormaColetiva() {
		return formaColetiva;
	}

	public void setFormaColetiva(boolean formaColetiva) {
		this.formaColetiva = formaColetiva;
	}

	public int[] getTiposValidos() {
		return tiposValidos;
	}

	public void setTiposValidos(int[] tiposValidos) {
		this.tiposValidos = tiposValidos;
	}

	public boolean isSelecionarSubUnidade() {
		return selecionarSubUnidade;
	}

	public void setSelecionarSubUnidade(boolean selecionarSubUnidade) {
		this.selecionarSubUnidade = selecionarSubUnidade;
	}

}
