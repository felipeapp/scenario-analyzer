/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 20/11/2008
 *
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

/**
 * Classe auxiliar que guarda os dados das consultas para exibir os dados no relatórios do CDU.
 * @author Fred_Castro
 *
 */
public class RelatorioCDUTotal {
	/** Nome da classe */
	private String classe;
	/** Descrição do título */
	private String titulos;
	/** Descrição dos Itens */
	private String itens;
	
	public RelatorioCDUTotal(){}
	
	public RelatorioCDUTotal(String classe, String titulos, String itens) {
		this.classe = classe;
		this.titulos = titulos;
		this.itens = itens;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getTitulos() {
		return titulos;
	}

	public void setTitulos(String titulos) {
		this.titulos = titulos;
	}

	public String getItens() {
		return itens;
	}

	public void setItens(String itens) {
		this.itens = itens;
	}
}
