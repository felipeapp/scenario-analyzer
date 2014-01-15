/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 19/07/2007
 * 
 */
package br.ufrn.sigaa.pesquisa.relatorios;

/**
 * Classe auxiliar para geração de relatórios de projetos por docente.
 * @author leonardo
 *
 */
public class LinhaRelatorioProjetosDocente {

	private String formacao;
	
	private int com29menos = 0;
	private int com30a70 = 0;
	private int com71mais = 0;
	
	public LinhaRelatorioProjetosDocente(){
		
	}

	public int getCom29menos() {
		return com29menos;
	}

	public void setCom29menos(int com29menos) {
		this.com29menos = com29menos;
	}

	public int getCom30a70() {
		return com30a70;
	}

	public void setCom30a70(int com30a70) {
		this.com30a70 = com30a70;
	}

	public int getCom71mais() {
		return com71mais;
	}

	public void setCom71mais(int com71mais) {
		this.com71mais = com71mais;
	}

	public String getFormacao() {
		return formacao;
	}

	public void setFormacao(String formacao) {
		this.formacao = formacao;
	}
	
}