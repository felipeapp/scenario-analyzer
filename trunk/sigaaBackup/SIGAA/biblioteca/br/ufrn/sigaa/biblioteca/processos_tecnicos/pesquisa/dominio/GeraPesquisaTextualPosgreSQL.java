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

import static br.ufrn.arq.util.StringUtils.toAsciiAndUpperCase;
import br.ufrn.arq.util.StringUtils;

/**
 *
 * <p>  Gera o mecanismo de pesquisa textual do PostgreSQL </p>
 * <p> <i> ( Utilizando-se desse mecanismo, mais indices que devem ser criados para cada coluna da tabela cache_entidades_marc
 *  onde a busca textual � utilizada, impede-se que o PostgreSQL tenha que percorrer a tabela citada anteriormente seq�encialmente, 
 *  otimizando o tempo da pesquisa. </i> 
 *  </p>
 * 
 * @author jadson
 *
 */
public class GeraPesquisaTextualPosgreSQL implements GeraPesquisaTextual {

	/**
	 * Ver coment�rio na classe pai.
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual#gerarMecaminismoPesquisaTextual(java.lang.String)
	 */
	public String gerarMecanismoPesquisaTextual(String nomeColuna) {
		return " to_tsvector('portuguese', "+nomeColuna+") @@ to_tsquery( ? ) ";
	}
	
	
	/**
	 * Ver coment�rio na classe pai.
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual#gerarMecaminismoPesquisaInteiro(java.lang.String)
	 */
	public String gerarMecanismoPesquisaInteiro(String nomeColuna) {
		return nomeColuna+" = ? ";
	}
	
	
	/**
	 * Ver coment�rio na classe pai.
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual#gerarMecaminismoPesquisaInicioPalavra(java.lang.String)
	 */
	public String gerarMecanismoPesquisaInicioPalavra(String nomeColuna) {
		return nomeColuna+" like ? ";
	}
	

	
	/**
	 * Ver coment�rio na classe pai.
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual#formataTextoPesquisaTextual(java.lang.String[])
	 */
	
	public String  formataTextoPesquisaTextual(String... palavrasPesquisa) {
		
		StringBuilder textoBusca = new StringBuilder();
		
		textoBusca.append(" ' ");
		
		for (int ptr = 0; ptr < palavrasPesquisa.length; ptr++) {
			
			if(ptr == 0) // Primeira palavra
				textoBusca.append(" "+  toAsciiAndUpperCase(trocaCaracterCoringa(palavrasPesquisa[ptr]) )+" ");
			else
				textoBusca.append(" & "+" "+toAsciiAndUpperCase(trocaCaracterCoringa(palavrasPesquisa[ptr]) )+" ");
		}
		
		textoBusca.append(" ' ");
		
		return textoBusca.toString();
	}
	
	
	/**
	 * Ver coment�rio na classe pai.
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual#formataTextoPesquisaTextual(java.lang.String[])
	 */
	public String  formataTextoPesquisaInicioPalavra(String palavraPesquisa) {
		
		StringBuilder textoBusca = new StringBuilder();
		
		textoBusca.append( toAsciiAndUpperCase(" "+trocaCaracterCoringa(palavraPesquisa)) +"%");
		
		return textoBusca.toString();
	}
	
	
	/**
	 * Ver coment�rio na classe pai.
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual#formataTextoPesquisaInteiro(java.lang.String)
	 */
	
	public String formataTextoPesquisaInteiro(String palavraPesquisa) {
		StringBuilder textoBusca = new StringBuilder();
		
		
		textoBusca.append( palavraPesquisa );
		
		return textoBusca.toString();
	}
	
	
	/**
	 *    No Aleph o caractere coringa de busca � o '?', mas no hibernate � o '%'.
	 *    
	 *    Ent�o � preciso, percorrer o valor digitado pelo usu�rio e trocar todos o caracteres '?'
	 *  por '%'
	 * 
	 * 
	 */
	public String trocaCaracterCoringa(String valorCampo){

		if(StringUtils.notEmpty(valorCampo)){

			StringBuilder valor =  new StringBuilder(valorCampo);

			int index = valor.indexOf("?");

			while(index != -1){
				valor.replace(index, index+1, "|");
				index = valor.indexOf("?");
			}

			return valor.toString(); // com espa�os para procurar apenas em uma palavra.

		}else
			return valorCampo;
	}


	


	
	

}
