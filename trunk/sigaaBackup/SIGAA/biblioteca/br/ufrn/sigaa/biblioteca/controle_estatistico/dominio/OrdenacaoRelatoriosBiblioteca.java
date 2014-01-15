/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 25/04/2013
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

/**
 * Representa os tipos de ordena��o que os relat�rios da biblioteca podem ter.
 *
 * @author jadson - jadson@info.ufrn.br
 * @vesion 1.0 - cria��o da classe.
 * @since 25/04/2013
 *
 */
public enum OrdenacaoRelatoriosBiblioteca {

	/** Ordena��o � opcional e n�o foi escolhida pelo usu�rio. */
	SEM_ORDENACAO(-1, "Nenhum", ""),
	
	/** Agrupamento por cole��o. */
	ORDENADO_POR_NOME(1, "Nome", "nome"),
	
	/** Agrupamento por situa��o de material. */
	ORDENADO_POR_PRAZO(2, "Prazo", "prazo");
	
	private OrdenacaoRelatoriosBiblioteca(int valor, String alias, String nomeCampo) {
		this.valor = valor;
		this.alias = alias;
		this.campoOrdenacao = nomeCampo;
	}
	
	/** O valor da ordena��o */
	private int valor;
	
	/** Alias padr�o para o campo. */
	private final String alias;
	
	/** Nome padr�o para o campo. */
	private final String campoOrdenacao;

	public int getValor() {
		return valor;
	}

	public String getAlias() {
		return alias;
	}

	public String getCampoOrdenacao() {
		return campoOrdenacao;
	}
	
	/**
	 * Retorna o agrupamento correspondente � constante.
	 * 
	 * @param constAgrup
	 * @return
	 */
	public static OrdenacaoRelatoriosBiblioteca getOrdenacao(int valor) {
		
		if(valor == OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_NOME.valor)
			return OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_NOME;
		if(valor == OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_PRAZO.valor)
			return OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_PRAZO;
		return OrdenacaoRelatoriosBiblioteca.SEM_ORDENACAO;
	}
	
}
