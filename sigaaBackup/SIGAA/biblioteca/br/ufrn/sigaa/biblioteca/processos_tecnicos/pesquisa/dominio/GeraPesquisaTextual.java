/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 09/03/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio;


/**
 *
 * <p> Interface usada nas pesquisas no acervo do sistema de biblioteca responsável por gerar o sql do mecanismo de pesquisa textual 
 * de modo a poder aproveitar algum mecanismo de busca textual que o banco de dados possua.</p>
 *
 * <p> <i> <strong>Observação:</strong> Esta interface deve ser implementada para gerar o mecanismo de pesquisa textual específico de cada banco</i> </p>
 * 
 * @author jadson
 *
 */
public interface GeraPesquisaTextual {

	/**
	 *  <p>Método que gera o mecaminismo de pesquisa utilizado nas pesquisas do acervo.</p>
	 *  <p>Caso não seja utilizado nenhum mecanismo de busca, pode-se implementar esse método para retorna 
	 *  os mescaminis de pesquisa padrão do SQL como o <code>"like '% %'"</code> ou <code>" = % %"</code>.</p>
	 * 
	 * @param nomeColuna o nome da coluna onde a busca irá ser feita e para a qual o mecaminis de pesquisa textual deve ser gerado.
	 * @return 
	 *
	 */
	public String gerarMecanismoPesquisaTextual(String nomeColuna);
	

	
	/**
	 *  <p>Método que gera o mecaminismo de pesquisa utilizado nas pesquisas do acervo para campos inteiros 
	 *  para os quais a pesquisa textual não é suportada.</p>
	 * 
	 * @param nomeColuna o nome da coluna onde a busca irá ser feita e para a qual o mecaminis de pesquisa textual deve ser gerado.
	 * @return 
	 *
	 */
	public String gerarMecanismoPesquisaInteiro(String nomeColuna);
	
	
	/**
	 *  <p>Método que gera o mecaminismo de pesquisa utilizado nas pesquisas do acervo para campos nos quais deve-ser 
	 *  buscar apenas no início da palavra</p>
	 * 
	 * @param nomeColuna o nome da coluna onde a busca irá ser feita e para a qual o mecaminis de pesquisa textual deve ser gerado.
	 * @return 
	 *
	 */
	public String gerarMecanismoPesquisaInicioPalavra(String nomeColuna);
	
	
	
	/**
	 *   Formata o texto de acordo com o mecamismo de pesquisa utilizado utilizando o operador lógico
	 *   padrão entre as palavras digitadas pelo usuário.
	 *
	 * @param palavrasPesquisa
	 * @return as palavras da pesquisa formatadas para serem usadas no mecanismo de busca apropriado
	 */
	public String formataTextoPesquisaTextual(String... palavrasPesquisa);
	


	/**
	 *   Formata o texto de acordo com o mecamismo de pesquisa utilizado utilizando o operador lógico
	 *   padrão entre as palavras digitadas pelo usuário para campos inteiros.
	 *
	 * @param palavrasPesquisa
	 * @return as palavras da pesquisa formatadas para serem usadas no mecanismo de busca apropriado
	 */
	public String formataTextoPesquisaInteiro(String palavraPesquisa);
	
	/**
	 * Método que gera o texto para pesquisas que precisam iniciar com o texto digitado pelo usuário
	 *  
	 * @param palavrasPesquisa
	 * @return
	 */
	public String  formataTextoPesquisaInicioPalavra(String palavraPesquisa);
	
	
	
	/**
	 *    <p>Método que deve implementar a conversão entre o caracter coringa usado na apresentação pelo usuário
	 *    e o caracter coringa usado pela persistência </p> 
	 *    
	 *    <p>No caso o SIGAA usa '?' como caracter coringa porque o sistema anterior, o Aleph, usava, mas na camada de persistencia
	 *    o hibernate que é o framework padrão do sigga usa '%'. </p>
	 * 
	 */
	public String trocaCaracterCoringa(String valorCampo);
	
}
