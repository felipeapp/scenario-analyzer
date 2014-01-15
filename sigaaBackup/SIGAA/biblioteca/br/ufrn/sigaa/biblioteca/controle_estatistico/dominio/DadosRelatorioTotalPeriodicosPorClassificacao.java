/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 05/01/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;


/**
 *
 * <p> Classe auxiliar para agrupar os dados para o relat�rio  </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public class DadosRelatorioTotalPeriodicosPorClassificacao implements Comparable<DadosRelatorioTotalPeriodicosPorClassificacao>{

	/** A classifica��o */
	private String classificacao;
	
	/** A quantidade de T�tulos */
	private int qtdTitulosCorrenteNacionais;
	/** A quantidade de T�tulos */
	private int qtdTitulosCorrenteInternacionais;
	/** A quantidade de T�tulos */
	private int qtdTitulosNaoCorrenteNacionais;
	/** A quantidade de T�tulos */
	private int qtdTitulosNaoCorrenteInternacionais;
	
	/** A quantidade de Fasc�culos */
	private int qtdFasciculosCorrenteNacionais;
	
	/** A quantidade de Fasc�culos */
	private int qtdFasciculosCorrenteInternacionais;
	
	/** A quantidade de Fasc�culos */
	private int qtdFasciculosNaoCorrenteNacionais;
	
	/** A quantidade de Fasc�culos */
	private int qtdFasciculosNaoCorrenteInternacionais;

	/** Contrutor para a identificar o objeto */
	public DadosRelatorioTotalPeriodicosPorClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}

	/** Adiciona a quantidade de T�tulo passados ao agrupamento */
	public void adicionarQtdTitulosCorrenteNacionais(int qtd){
		qtdTitulosCorrenteNacionais+=qtd;
	}
	
	/** Adiciona a quantidade de T�tulo passados ao agrupamento */
	public void adicionarQtdTitulosCorrenteInternacionais(int qtd){
		qtdTitulosCorrenteInternacionais+=qtd;
	}
	
	/** Adiciona a quantidade de T�tulo passados ao agrupamento */
	public void adicionarQtdTitulosNaoCorrenteNacionais(int qtd){
		qtdTitulosNaoCorrenteNacionais+=qtd;
	}
	
	/** Adiciona a quantidade de T�tulo passados ao agrupamento */
	public void adicionarQtdTitulosNaoCorrenteInternacionais(int qtd){
		qtdTitulosNaoCorrenteInternacionais+=qtd;
	}
	
	// fasc�culos
	
	/** Adiciona a quantidade de T�tulo passados ao agrupamento */
	public void adicionarQtdFasciculosCorrenteNacionais(int qtd){
		qtdFasciculosCorrenteNacionais+=qtd;
	}
	
	/** Adiciona a quantidade de T�tulo passados ao agrupamento */
	public void adicionarQtdFasciculosCorrenteInternacionais(int qtd){
		qtdFasciculosCorrenteInternacionais+=qtd;
	}
	
	/** Adiciona a quantidade de T�tulo passados ao agrupamento */
	public void adicionarQtdFasciculosNaoCorrenteNacionais(int qtd){
		qtdFasciculosNaoCorrenteNacionais+=qtd;
	}
	
	/** Adiciona a quantidade de T�tulo passados ao agrupamento */
	public void adicionarQtdFasciculosNaoCorrenteInternacionais(int qtd){
		qtdFasciculosNaoCorrenteInternacionais+=qtd;
	}
	
	
	public String getClassificacao() {return classificacao;}

	public void setClassificacao(String classificacao) {this.classificacao = classificacao;}

	public int getQtdTitulosCorrenteNacionais() {return qtdTitulosCorrenteNacionais;}

	public int getQtdTitulosCorrenteInternacionais() {return qtdTitulosCorrenteInternacionais;}

	public int getQtdTitulosNaoCorrenteNacionais() {return qtdTitulosNaoCorrenteNacionais;}

	public int getQtdTitulosNaoCorrenteInternacionais() {return qtdTitulosNaoCorrenteInternacionais;}

	public int getQtdFasciculosCorrenteNacionais() {return qtdFasciculosCorrenteNacionais;}

	public int getQtdFasciculosCorrenteInternacionais() {return qtdFasciculosCorrenteInternacionais;}

	public int getQtdFasciculosNaoCorrenteNacionais() {return qtdFasciculosNaoCorrenteNacionais;}

	public int getQtdFasciculosNaoCorrenteInternacionais() {return qtdFasciculosNaoCorrenteInternacionais;}

	/**Retorna o total da classifica��o*/
	public int getQtdTitulosPorClassificacao() {
			return qtdTitulosCorrenteNacionais
			+qtdTitulosCorrenteInternacionais
			+qtdTitulosNaoCorrenteNacionais
			+qtdTitulosNaoCorrenteInternacionais;
	}
	
	/**Retorna o total da classifica��o*/
	public int getQtdFasciculosPorClassificacao() {
		return qtdFasciculosCorrenteNacionais
		+qtdFasciculosCorrenteInternacionais
		+qtdFasciculosNaoCorrenteNacionais
		+qtdFasciculosNaoCorrenteInternacionais;
	}
	
	/**
	 * Compara os dados pela classifica��o, utilizado na ordena��o.<br/>
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DadosRelatorioTotalPeriodicosPorClassificacao o) {
		return this.classificacao.compareTo(o.classificacao);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result+ ((classificacao == null) ? 0 : classificacao.hashCode());
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
		DadosRelatorioTotalPeriodicosPorClassificacao other = (DadosRelatorioTotalPeriodicosPorClassificacao) obj;
		if (classificacao == null) {
			if (other.classificacao != null)
				return false;
		} else if (!classificacao.equals(other.classificacao))
			return false;
		return true;
	}

	
	
}
