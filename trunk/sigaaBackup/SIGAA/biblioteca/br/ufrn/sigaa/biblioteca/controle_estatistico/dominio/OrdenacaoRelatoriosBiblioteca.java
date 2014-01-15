/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 25/04/2013
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

/**
 * Representa os tipos de ordenação que os relatórios da biblioteca podem ter.
 *
 * @author jadson - jadson@info.ufrn.br
 * @vesion 1.0 - criação da classe.
 * @since 25/04/2013
 *
 */
public enum OrdenacaoRelatoriosBiblioteca {

	/** Ordenação é opcional e não foi escolhida pelo usuário. */
	SEM_ORDENACAO(-1, "Nenhum", ""),
	
	/** Agrupamento por coleção. */
	ORDENADO_POR_NOME(1, "Nome", "nome"),
	
	/** Agrupamento por situação de material. */
	ORDENADO_POR_PRAZO(2, "Prazo", "prazo");
	
	private OrdenacaoRelatoriosBiblioteca(int valor, String alias, String nomeCampo) {
		this.valor = valor;
		this.alias = alias;
		this.campoOrdenacao = nomeCampo;
	}
	
	/** O valor da ordenação */
	private int valor;
	
	/** Alias padrão para o campo. */
	private final String alias;
	
	/** Nome padrão para o campo. */
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
	 * Retorna o agrupamento correspondente à constante.
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
