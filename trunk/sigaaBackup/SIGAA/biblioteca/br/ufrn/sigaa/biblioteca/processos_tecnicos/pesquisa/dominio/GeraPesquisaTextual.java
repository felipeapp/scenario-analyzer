/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 09/03/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio;


/**
 *
 * <p> Interface usada nas pesquisas no acervo do sistema de biblioteca respons�vel por gerar o sql do mecanismo de pesquisa textual 
 * de modo a poder aproveitar algum mecanismo de busca textual que o banco de dados possua.</p>
 *
 * <p> <i> <strong>Observa��o:</strong> Esta interface deve ser implementada para gerar o mecanismo de pesquisa textual espec�fico de cada banco</i> </p>
 * 
 * @author jadson
 *
 */
public interface GeraPesquisaTextual {

	/**
	 *  <p>M�todo que gera o mecaminismo de pesquisa utilizado nas pesquisas do acervo.</p>
	 *  <p>Caso n�o seja utilizado nenhum mecanismo de busca, pode-se implementar esse m�todo para retorna 
	 *  os mescaminis de pesquisa padr�o do SQL como o <code>"like '% %'"</code> ou <code>" = % %"</code>.</p>
	 * 
	 * @param nomeColuna o nome da coluna onde a busca ir� ser feita e para a qual o mecaminis de pesquisa textual deve ser gerado.
	 * @return 
	 *
	 */
	public String gerarMecanismoPesquisaTextual(String nomeColuna);
	

	
	/**
	 *  <p>M�todo que gera o mecaminismo de pesquisa utilizado nas pesquisas do acervo para campos inteiros 
	 *  para os quais a pesquisa textual n�o � suportada.</p>
	 * 
	 * @param nomeColuna o nome da coluna onde a busca ir� ser feita e para a qual o mecaminis de pesquisa textual deve ser gerado.
	 * @return 
	 *
	 */
	public String gerarMecanismoPesquisaInteiro(String nomeColuna);
	
	
	/**
	 *  <p>M�todo que gera o mecaminismo de pesquisa utilizado nas pesquisas do acervo para campos nos quais deve-ser 
	 *  buscar apenas no in�cio da palavra</p>
	 * 
	 * @param nomeColuna o nome da coluna onde a busca ir� ser feita e para a qual o mecaminis de pesquisa textual deve ser gerado.
	 * @return 
	 *
	 */
	public String gerarMecanismoPesquisaInicioPalavra(String nomeColuna);
	
	
	
	/**
	 *   Formata o texto de acordo com o mecamismo de pesquisa utilizado utilizando o operador l�gico
	 *   padr�o entre as palavras digitadas pelo usu�rio.
	 *
	 * @param palavrasPesquisa
	 * @return as palavras da pesquisa formatadas para serem usadas no mecanismo de busca apropriado
	 */
	public String formataTextoPesquisaTextual(String... palavrasPesquisa);
	


	/**
	 *   Formata o texto de acordo com o mecamismo de pesquisa utilizado utilizando o operador l�gico
	 *   padr�o entre as palavras digitadas pelo usu�rio para campos inteiros.
	 *
	 * @param palavrasPesquisa
	 * @return as palavras da pesquisa formatadas para serem usadas no mecanismo de busca apropriado
	 */
	public String formataTextoPesquisaInteiro(String palavraPesquisa);
	
	/**
	 * M�todo que gera o texto para pesquisas que precisam iniciar com o texto digitado pelo usu�rio
	 *  
	 * @param palavrasPesquisa
	 * @return
	 */
	public String  formataTextoPesquisaInicioPalavra(String palavraPesquisa);
	
	
	
	/**
	 *    <p>M�todo que deve implementar a convers�o entre o caracter coringa usado na apresenta��o pelo usu�rio
	 *    e o caracter coringa usado pela persist�ncia </p> 
	 *    
	 *    <p>No caso o SIGAA usa '?' como caracter coringa porque o sistema anterior, o Aleph, usava, mas na camada de persistencia
	 *    o hibernate que � o framework padr�o do sigga usa '%'. </p>
	 * 
	 */
	public String trocaCaracterCoringa(String valorCampo);
	
}
