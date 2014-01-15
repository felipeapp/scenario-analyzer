/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/04/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

public class MovimentoProjetoPesquisa extends AbstractMovimentoAdapter {

	private ProjetoPesquisa projeto;

	// Informações do arquivo com o edital
	private byte[] dadosArquivo;
	private String contentType;
	private String nomeArquivo;
	public String getContentType() {
		return contentType;
	}
	public byte[] getDadosArquivo() {
		return dadosArquivo;
	}
	public String getNomeArquivo() {
		return nomeArquivo;
	}
	public ProjetoPesquisa getProjeto() {
		return projeto;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public void setDadosArquivo(byte[] dadosArquivo) {
		this.dadosArquivo = dadosArquivo;
	}
	public void setNomeArquivo(String nome) {
		this.nomeArquivo = nome;
	}
	public void setProjeto(ProjetoPesquisa projeto) {
		this.projeto = projeto;
	}

}
