/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 05/01/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

/**
 *
 * <p> Classe auxiliar para agrupar os dados para o relatório  </p>
 * 
 * @author jadson
 *
 */
public class DadosRelatorioTotalPeriodicosPorAreaCNPq implements Comparable<DadosRelatorioTotalPeriodicosPorAreaCNPq>{

	/** A área */
	private String areaCNPQ;
	
	/** A quantidade de Títulos */
	private int qtdTitulosCorrenteNacionais;
	/** A quantidade de Títulos */
	private int qtdTitulosCorrenteInternacionais;
	/** A quantidade de Títulos */
	private int qtdTitulosNaoCorrenteNacionais;
	/** A quantidade de Títulos */
	private int qtdTitulosNaoCorrenteInternacionais;
	
	/** A quantidade de Fascículos */
	private int qtdFasciculosCorrenteNacionais;
	
	/** A quantidade de Fascículos */
	private int qtdFasciculosCorrenteInternacionais;
	
	/** A quantidade de Fascículos */
	private int qtdFasciculosNaoCorrenteNacionais;
	
	/** A quantidade de Fascículos */
	private int qtdFasciculosNaoCorrenteInternacionais;

	/** Contrutor para a identificar o objeto */
	public DadosRelatorioTotalPeriodicosPorAreaCNPq(String areaCNPQ) {
		this.areaCNPQ = areaCNPQ;
	}

	/** Adiciona a quantidade de Título passados ao agrupamento */
	public void adicionarQtdTitulosCorrenteNacionais(int qtd){
		qtdTitulosCorrenteNacionais+=qtd;
	}
	
	/** Adiciona a quantidade de Título passados ao agrupamento */
	public void adicionarQtdTitulosCorrenteInternacionais(int qtd){
		qtdTitulosCorrenteInternacionais+=qtd;
	}
	
	/** Adiciona a quantidade de Título passados ao agrupamento */
	public void adicionarQtdTitulosNaoCorrenteNacionais(int qtd){
		qtdTitulosNaoCorrenteNacionais+=qtd;
	}
	
	/** Adiciona a quantidade de Título passados ao agrupamento */
	public void adicionarQtdTitulosNaoCorrenteInternacionais(int qtd){
		qtdTitulosNaoCorrenteInternacionais+=qtd;
	}
	
	// fascículos
	
	/** Adiciona a quantidade de Título passados ao agrupamento */
	public void adicionarQtdFasciculosCorrenteNacionais(int qtd){
		qtdFasciculosCorrenteNacionais+=qtd;
	}
	
	/** Adiciona a quantidade de Título passados ao agrupamento */
	public void adicionarQtdFasciculosCorrenteInternacionais(int qtd){
		qtdFasciculosCorrenteInternacionais+=qtd;
	}
	
	/** Adiciona a quantidade de Título passados ao agrupamento */
	public void adicionarQtdFasciculosNaoCorrenteNacionais(int qtd){
		qtdFasciculosNaoCorrenteNacionais+=qtd;
	}
	
	/** Adiciona a quantidade de Título passados ao agrupamento */
	public void adicionarQtdFasciculosNaoCorrenteInternacionais(int qtd){
		qtdFasciculosNaoCorrenteInternacionais+=qtd;
	}
	
	
	public String getAreaCNPQ() {return areaCNPQ;}

	public void setAreaCNPQ(String areaCNPQ) {this.areaCNPQ = areaCNPQ;}

	public int getQtdTitulosCorrenteNacionais() {return qtdTitulosCorrenteNacionais;}

	public int getQtdTitulosCorrenteInternacionais() {return qtdTitulosCorrenteInternacionais;}

	public int getQtdTitulosNaoCorrenteNacionais() {return qtdTitulosNaoCorrenteNacionais;}

	public int getQtdTitulosNaoCorrenteInternacionais() {return qtdTitulosNaoCorrenteInternacionais;}

	public int getQtdFasciculosCorrenteNacionais() {return qtdFasciculosCorrenteNacionais;}

	public int getQtdFasciculosCorrenteInternacionais() {return qtdFasciculosCorrenteInternacionais;}

	public int getQtdFasciculosNaoCorrenteNacionais() {return qtdFasciculosNaoCorrenteNacionais;}

	public int getQtdFasciculosNaoCorrenteInternacionais() {return qtdFasciculosNaoCorrenteInternacionais;}

	/**Retorna o total da classificação*/
	public int getQtdTitulosPorClassificacao() {
			return qtdTitulosCorrenteNacionais
			+qtdTitulosCorrenteInternacionais
			+qtdTitulosNaoCorrenteNacionais
			+qtdTitulosNaoCorrenteInternacionais;
	}
	
	/**Retorna o total da classificação*/
	public int getQtdFasciculosPorClassificacao() {
		return qtdFasciculosCorrenteNacionais
		+qtdFasciculosCorrenteInternacionais
		+qtdFasciculosNaoCorrenteNacionais
		+qtdFasciculosNaoCorrenteInternacionais;
	}
	
	/**
	 * Compara os dados pela classificação, utilizado na ordenação.<br/>
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DadosRelatorioTotalPeriodicosPorAreaCNPq o) {
		return this.areaCNPQ.compareTo(o.areaCNPQ);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result+ ((areaCNPQ == null) ? 0 : areaCNPQ.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DadosRelatorioTotalPeriodicosPorAreaCNPq other = (DadosRelatorioTotalPeriodicosPorAreaCNPq) obj;
		if (areaCNPQ == null) {
			if (other.areaCNPQ != null)
				return false;
		} else if (!areaCNPQ.equals(other.areaCNPQ))
			return false;
		return true;
	}

	
}
